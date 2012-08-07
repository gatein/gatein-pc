package org.gatein.pc.test.unit;

import org.gatein.pc.test.unit.annotations.Archive;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
@SupportedAnnotationTypes("org.gatein.pc.test.unit.annotations.TestCase")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TestGenerator extends AbstractProcessor
{

   /** . */
   private static final Pattern P = Pattern.compile("^.*\\.([^.]+)\\.([^.]+)\\.([^.]+)$");

   /** . */
   private final TestReporter reporter = new TestReporter();

   /** . */
   private final Properties all = new Properties();

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
   {
      Filer filer = processingEnv.getFiler();

      //
      if (roundEnv.processingOver())
      {
         // Testsuite
         try
         {
            FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "suite.properties");
            OutputStream out = file.openOutputStream();
            all.store(out, null);
            out.close();
         }
         catch (IOException e)
         {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not create test case for package " + e.getMessage());
         }
      }

      //
      reporter.process(annotations, roundEnv);

      //
      Map<PackageElement, List<TypeElement>> map = new HashMap<PackageElement, List<TypeElement>>();
      for (Element annotated : roundEnv.getElementsAnnotatedWith(TestCase.class))
      {
         TestCase tc = annotated.getAnnotation(TestCase.class);
         if (tc.enabled())
         {
            if (annotated instanceof TypeElement)
            {
               PackageElement pkg = processingEnv.getElementUtils().getPackageOf(annotated);
               List<TypeElement> types = map.get(pkg);
               if (types == null)
               {
                  map.put(pkg, types = new ArrayList<TypeElement>());
               }
               types.add((TypeElement)annotated);
            }
         }
      }

      // Generate tests
      for (Map.Entry<PackageElement, List<TypeElement>> entry : map.entrySet())
      {
         PackageElement pkg = entry.getKey();
         Matcher m = P.matcher(pkg.getQualifiedName());
         if (m.matches())
         {
            String version = m.group(1);
            String type = m.group(2);
            String suite = m.group(3);
            String testPkg = version + '.' + type + '.' + suite;

            //
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            try
            {
               Thread.currentThread().setContextClassLoader(WebArchive.class.getClassLoader());
               WebArchive archive = AbstractWarTestCase.createDeployment(version,  type, suite);
               FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, testPkg, "suite.war");
               OutputStream out = file.openOutputStream();
               archive.as(ZipExporter.class).exportTo(out);
               out.close();
            }
            catch (Exception e)
            {
               processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not create war file for package " + e.getMessage(), pkg);
            }
            finally
            {
               Thread.currentThread().setContextClassLoader(old);
            }

            // Suite properties
            try
            {
               FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, testPkg, "suite.properties");
               OutputStream out = file.openOutputStream();
               Properties props = new Properties();
               for (TypeElement annotated : entry.getValue())
               {
                  props.put(annotated.getQualifiedName().toString(), "testcase");
               }
               props.store(out, null);
               out.close();
            }
            catch (IOException e)
            {
               processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not create test case for package " + e.getMessage(), pkg);
            }

            // Ear test cases
            for (TypeElement annotated : entry.getValue())
            {
               String name = annotated.getSimpleName().toString();
               try
               {
                  String baseFQN = testPkg + "." + name;

                  //
                  all.put(baseFQN, "fqn");

                  //
                  JavaFileObject file = filer.createSourceFile(baseFQN + "EarTestCase");
                  PrintWriter writer = new PrintWriter(file.openWriter());

                  // Package
                  writer.append("package ").append(testPkg).println(";");

                  // Open class
                  writer.append('@').append(RunWith.class.getName()).append('(').append(Arquillian.class.getName()).println(".class)");
                  writer.append("public class ").append(name).println("EarTestCase extends org.gatein.pc.test.unit.AbstractEarTestCase {");

                  // List of deployments
                  List<String[]> deployments = new ArrayList<String[]>();

                  // Add implicit deployment
                  deployments.add(new String[]{version,type,suite});

                  // Additional deployment
                  TestCase annotation = annotated.getAnnotation(TestCase.class);
                  for (Archive deployment : annotation.deployments())
                  {
                     deployments.add(deployment.value());
                  }

                  //
                  writer.append('@').append(Deployment.class.getName()).append("(name = \"deployment").println("\", testable = false)");
                  writer.append("public static ").append(EnterpriseArchive.class.getName()).append(" createDeployment").println("() throws Exception {");
                  writer.append("return ").append(AbstractEarTestCase.class.getName()).append(".createDeployment(");
                  for (int i = 0;i < deployments.size();i++)
                  {
                     if (i > 0)
                     {
                        writer.append(',');
                     }
                     writer.append("new String[]{");
                     String[] deployment = deployments.get(i);
                     for (int j = 0;j < deployment.length;j++)
                     {
                        if (j > 0)
                        {
                           writer.append(',');
                        }
                        writer.append('"').append(deployment[j]).append('"');
                     }
                     writer.append("}");
                  }
                  writer.println(");");
                  writer.println("}");

                  // Constructor
                  writer.append("public ").append(name).println("EarTestCase() {");
                  writer.append("super(\"").append(version).append("\",\"").append(type).append("\",\"").append(suite).append("").append("\",\"").append(name).println("\");");
                  writer.println("}");

                  // URL
                  writer.append('@').append(ArquillianResource.class.getName()).append("(").append(PortalTestServlet.class.getName()).println(".class)");
                  writer.println("java.net.URL deploymentURL;");
                  writer.println("protected java.net.URL getBaseURL() {");
                  writer.println("return deploymentURL;");
                  writer.println("}");

                  // Close class
                  writer.println("}");

                  // Close writer
                  writer.close();
               }
               catch (IOException e)
               {
                  processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not create test case for package " + e.getMessage(), pkg);
               }
            }

            // War test cases
            for (TypeElement annotated : entry.getValue())
            {
               String name = annotated.getSimpleName().toString();
               try
               {
                  JavaFileObject file = filer.createSourceFile(testPkg + "." + name + "WarTestCase");
                  PrintWriter writer = new PrintWriter(file.openWriter());

                  // Package
                  writer.append("package ").append(testPkg).println(";");

                  // Open class
                  writer.append('@').append(RunWith.class.getName()).append('(').append(Arquillian.class.getName()).println(".class)");
                  writer.append("public class ").append(name).println("WarTestCase extends org.gatein.pc.test.unit.AbstractWarTestCase {");

                  // List of deployments
                  List<String[]> deployments = new ArrayList<String[]>();

                  // Add implicit deployment
                  deployments.add(new String[]{version,type,suite});

                  // Additional deployment
                  TestCase annotation = annotated.getAnnotation(TestCase.class);
                  for (Archive deployment : annotation.deployments())
                  {
                     deployments.add(deployment.value());
                  }

                  //
                  for (int i = 0;i < deployments.size();i++)
                  {
                     writer.append('@').append(Deployment.class.getName()).append("(name = \"deployment").append(Integer.toString(i)).println("\", testable = false, managed = false)");
                     writer.append("public static ").append(WebArchive.class.getName()).append(" createDeployment").append(Integer.toString(i)).println("() throws Exception {");
                     writer.append("return createDeployment(\"");
                     String[] deployment = deployments.get(i);
                     for (int j = 0;j < deployment.length;j++)
                     {
                        if (j > 0) {
                           writer.append("\",\"");
                        }
                        writer.append(deployment[j]);
                     }
                     writer.println("\");");
                     writer.println("}");
                  }

                  // Constructor
                  writer.append("public ").append(name).println("WarTestCase() {");
                  writer.append("super(\"").append(version).append("\",\"").append(type).append("\",\"").append(suite).append("").append("\",\"").append(name).append('"');
                  for (int i = 0;i < deployments.size();i++)
                  {
                     writer.append(",\"deployment").append(Integer.toString(i)).append("\"");
                  }
                  writer.println(");");
                  writer.println("}");

                  // Close class
                  writer.println("}");

                  // Close writer
                  writer.close();
               }
               catch (IOException e)
               {
                  processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not create test case for package " + e.getMessage(), pkg);
               }
            }
         }
      }

      //
      return false;
   }
}
