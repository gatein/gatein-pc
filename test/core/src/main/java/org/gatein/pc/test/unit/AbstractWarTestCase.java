package org.gatein.pc.test.unit;

import org.gatein.pc.test.unit.protocol.Conversation;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public abstract class AbstractWarTestCase
{

   @ArquillianResource
   protected URL deploymentURL;

   @ArquillianResource
   Deployer deployer;

   @Deployment(name = "portal", testable = false, order = 1)
   public static WebArchive createDeployment() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class);
      war.addAsWebInfResource("portal-war/WEB-INF/web.xml");
      return war;
   }

   public static WebArchive createDeployment(String version, String type, String suite) throws Exception
   {

      WebArchive war = ShrinkWrap.create(WebArchive.class);

      //
      war.addPackages(true, "org/gatein/pc/test/portlet/" + version + "/" + type + "/common");
      war.addPackages(true, "org/gatein/pc/test/portlet/" + version + "/common");
      war.addPackages(true, "org/gatein/pc/test/portlet/common");
      war.addPackages(true, "org/gatein/pc/test/portlet/" + version + "/" + type + "/" + suite);
      war.addPackages(true, "org/gatein/pc/test/portlet/framework");

      //
      String properties = version + "/" + type + "/" + suite + "/suite.properties";
      if (Thread.currentThread().getContextClassLoader().getResource(properties) != null)
      {
         war.addAsWebInfResource(properties, "suite.properties");
      }

      // Copy TLDs
      war.addAsWebInfResource("META-INF/portlet.tld", "tld/portlet.tld");
      war.addAsWebInfResource("META-INF/portlet_2_0.tld", "tld/portlet_2_0.tld");

      //
      URL url = Thread.currentThread().getContextClassLoader().getResource(version + "/" + type + "/" + suite + "-war/");
      Assert.assertNotNull(url);
      addResources(url, war);

      //
      return war;
   }

   private static void addResources(URL root, WebArchive target) throws Exception
   {
      if (root.getProtocol().equals("file"))
      {
         addResources(new File(root.toURI()), target, new LinkedList<String>());
      }
      else if (root.getProtocol().equals("jar"))
      {
         String path = root.getFile();
         String prefix = path.substring(path.indexOf("!/") + 2);
         JarURLConnection conn = (JarURLConnection)root.openConnection();
         JarFile jarFile = conn.getJarFile();
         for (JarEntry entry : Collections.list(conn.getJarFile().entries()))
         {
            if (!entry.isDirectory() && entry.getName().startsWith(prefix))
            {
               String key = entry.getName().substring(prefix.length());
               target.addAsWebResource(new ByteArrayAsset(jarFile.getInputStream(entry)), key);
            }
         }
      }
   }

   private static void addResources(File root, WebArchive target, LinkedList<String> path) throws Exception
   {
      File[] list = root.listFiles();
      if (list != null)
      {
         for (File child : list)
         {
            if (child.isDirectory())
            {
               path.addLast(child.getName());
               addResources(child, target, path);
               path.removeLast();
            }
            else
            {
               StringBuilder sb = new StringBuilder();
               for (String dir : path)
               {
                  sb.append(dir).append('/');
               }
               sb.append(child.getName());
               target.addAsWebResource(child, sb.toString());
            }
         }
      }
   }

   /** . */
   private final String version;

   /** . */
   private final String type;

   /** . */
   private final String suite;

   /** . */
   private final String name;

   /** . */
   private final String[] deployments;

   protected AbstractWarTestCase(String version, String type, String suite, String name, String... deployments)
   {
      this.version = version;
      this.type = type;
      this.suite = suite;
      this.name = name;
      this.deployments = deployments;
   }

   @Test
   public final void test()
   {
      System.out.println("testing " + version + "/" + type + "/" + suite + "/" + name + " with url " + deploymentURL);

      //
      for (String deployment : deployments)
      {
         deployer.deploy(deployment);
      }

      //
      try
      {
         new Conversation(deploymentURL, name).performInteractions();
      }
      finally
      {
         for (String deployment : deployments)
         {
            deployer.undeploy(deployment);
         }
      }
   }
}
