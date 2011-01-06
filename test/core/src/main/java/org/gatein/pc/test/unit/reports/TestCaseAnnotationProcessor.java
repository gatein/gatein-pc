/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.test.unit.reports;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class TestCaseAnnotationProcessor implements AnnotationProcessor
{

   private AnnotationProcessorEnvironment environment;

	private AnnotationTypeDeclaration testCaseDeclaration;



   public static String jsr168Pre = "org.jboss.portal.test.portlet.jsr168";

   public static String jsr286Pre = "org.jboss.portal.test.portlet.jsr286";

   public static String jsr168AssertionPre = "JSR168_";

   public static String jsr286AssertionPre = "JSR286_";

   public static String api286AssertionPre = "API286_";

   public static String extAssertionPre = "EXT_";

   public static Map<String, Set<String>> jsr168Assertions = new HashMap<String, Set<String>>();

   public static Map<String, Set<String>> jsr286Assertions = new HashMap<String, Set<String>>();

   public static Map<String, Set<String>> api286Assertions = new HashMap<String, Set<String>>();

   public static Map<String, Set<String>> extAssertions = new HashMap<String, Set<String>>();

   //private int counter;

   public TestCaseAnnotationProcessor(AnnotationProcessorEnvironment env) {

      environment = env;
		testCaseDeclaration = (AnnotationTypeDeclaration) environment
				.getTypeDeclaration("org.jboss.portal.unit.annotations.TestCase");
	}

   public void process()
   {
      Collection<Declaration> declarations = environment
				.getDeclarationsAnnotatedWith(testCaseDeclaration);
		for (Declaration declaration : declarations) {
			processAnnotations(declaration);
		}
   }

   private void processAnnotations(Declaration declaration) {
		// Get all of the annotation usage for this declaration.
		// the annotation mirror is a reflection of what is in the source.

         Collection<AnnotationMirror> annotations = declaration
            .getAnnotationMirrors();
         // iterate over the mirrors.
         for (AnnotationMirror mirror : annotations) {
            // if the mirror in this iteration is for our note declaration...

            System.out.println("#Annotation processing... " + mirror.getAnnotationType().getDeclaration().toString());

            if(mirror.getAnnotationType().getDeclaration().equals(
               testCaseDeclaration)) {


               // print out the goodies.
               Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror
                  .getElementValues();

               for (Map.Entry<AnnotationTypeElementDeclaration, AnnotationValue> entry : values
                  .entrySet()) {

                  Collection<AnnotationValue> annotationValues = (Collection<AnnotationValue>)entry.getValue().getValue();

                  for (AnnotationValue value : annotationValues)
                  {

                     String tck = value.toString();

                     String[] parts = tck.split("\\.");

                     tck = parts[parts.length - 1];

                     Map<String, Set<String>> am = null;

                     if (tck.startsWith(jsr168AssertionPre))
                     {
                        am = jsr168Assertions;
                     }
                     else if (tck.startsWith(jsr286AssertionPre))
                     {
                        am = jsr286Assertions;
                     }
                     else if (tck.startsWith(api286AssertionPre))
                     {
                        am = api286Assertions;
                     }
                     else if (tck.startsWith(extAssertionPre))
                     {
                        am = extAssertions;
                     }
                     else
                     {
                        continue;
                     }


                     addAssertion(am, tck, declaration.toString());

                     System.out.println("#TCK: " + tck );
                    
                  }
               }
            }
         }


   }

   private void addAssertion(Map<String, Set<String>> map, String assertion, String testCase)
   {
      if (map.get(assertion) != null)
      {
         Set<String> set = map.get(assertion);
         set.add(testCase);
      }
      else
      {
         Set<String> set = new HashSet<String>();
         set.add(testCase);
         map.put(assertion, set);
      }
   }
}
