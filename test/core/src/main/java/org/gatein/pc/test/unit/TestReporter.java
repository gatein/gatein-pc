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

package org.gatein.pc.test.unit;

import org.gatein.pc.test.unit.annotations.TestCase;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
class TestReporter 
{

   private static final String jsr168File = "./target/apt-jsr168-testcases.csv";

   private static final String jsr286File = "./target/apt-jsr286-testcases.csv";

   private static final String api286File = "./target/apt-api286-testcases.csv";

   private static final String extFile = "./target/apt-ext-testcases.csv";

   private static final String jsr168AssertionPre = "JSR168_";

   private static final String jsr286AssertionPre = "JSR286_";

   private static final String api286AssertionPre = "API286_";

   private static final String extAssertionPre = "EXT_";

   private final Map<String, Set<String>> jsr168Assertions = new HashMap<String, Set<String>>();

   private final Map<String, Set<String>> jsr286Assertions = new HashMap<String, Set<String>>();

   private final Map<String, Set<String>> api286Assertions = new HashMap<String, Set<String>>();

   private final Map<String, Set<String>> extAssertions = new HashMap<String, Set<String>>();

   void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
   {
      if (roundEnv.processingOver())
      {
         System.out.println("#END");
         try
         {
            //Add 168 assertions test cases to related 286
            mergeAssertions();
            if (!jsr168Assertions.keySet().isEmpty())
            {
               printReport(jsr168Assertions, jsr168File, "JSR168_");
            }
            if (!jsr286Assertions.keySet().isEmpty())
            {
               printReport(jsr286Assertions, jsr286File, "JSR286_");
            }
            if (!api286Assertions.keySet().isEmpty())
            {
               printReport(api286Assertions, api286File, api286AssertionPre);
            }
            if (!extAssertions.keySet().isEmpty())
            {
               printReport(extAssertions, extFile, extAssertionPre);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      else
      {
         for (Element element : roundEnv.getElementsAnnotatedWith(TestCase.class))
         {
            TestCase tc = element.getAnnotation(TestCase.class);
            for (Assertion assertion : tc.value())
            {
               String tck = assertion.toString();
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
               addAssertion(am, tck, element.toString());
               System.out.println("#TCK: " + tck );
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

   private void mergeAssertions()
   {
      Map<String, Set<String>> jsr168a = jsr168Assertions;
      Map<String, Set<String>> jsr286a = jsr286Assertions;
      Set<String> names286 = jsr286a.keySet();
      for (String name286 : names286)
      {
         Assertion assertion286 = getAssertion(name286);
         Assertion related168 = assertion286.getAssertion();
         if(related168 != null)
         {
            jsr286a.get(name286).addAll(jsr168a.get(related168.name()));
         }
      }
   }

   private void printReport(Map<String, Set<String>> map, String fileName, String assertionPre) throws Exception
   {

      // Simple stats

      int total = 0;
      int tested = 0;
      int not_tested = 0;
      int inactive = 0;

      File f = new File(fileName);

      Writer writer = new FileWriter(f, false);

      StringBuilder output = new StringBuilder();

      // Line 1
      // Columns

      output
         .append("ASSERTION:,")
         .append("REF:,")
         .append("STATUS:,")
         .append("TEST_CASES:,")
         .append("DESCRIPTION:,");

      output.append("\n");

      List<Assertion> apiAssertions = getAssertions(assertionPre);

      for (Assertion assertion : apiAssertions)
      {
         total++;

         String assertionName = assertion.name();

         Set<String> tcs = map.get(assertionName);
         String[] testCases = null;

         if (tcs != null)
         {
            testCases = tcs.toArray(new String[tcs.size()]);
         }

         String description = assertion.getDescription().replaceAll(",", " ").trim();

         // Line 2

         // ASSERTION:

         output.append(assertionName).append(",");

         // REF:

         if (assertion.getRef() != null)
         {
            output.append(assertion.getRef().toString()).append(",");
         }
         else
         {
            output.append(",");
         }

         // STATUS:

         if (assertion.getStatus() != null)
         {
            Assertion.Status status = assertion.getStatus();
            output.append(status).append(",");

            if (status instanceof Assertion.Inactive || status instanceof Assertion.Duplicate)
            {
               inactive++;
            }
         }
         else
         {
            output.append(",");
         }

         // TEST_CASES:

         if (testCases != null && testCases.length > 0)
         {
            tested++;

            output.append(shortenClassname(testCases[0])).append(",");

         }
         else
         {
            not_tested++;
            output.append("NOT_TESTED,");
         }

         // DESCRIPTION:

         output.append(description).append('\n');


         // Rest of testcases

         if (testCases != null && testCases.length > 1)
         {
            for (int i = 1; i < testCases.length; i++)
            {
               String testCase = testCases[i];
               output.append(",,,").append(shortenClassname(testCase)).append("\n");
            }
         }

         output.append("\n");
      }

      output.append("\n\n");

      writer.write("STATISTICS:,***\n");
      writer.write("TOTAL:," + total + "\n");
      writer.write("TESTED:," + tested + "\n");
      writer.write("NOT TESTED:," + not_tested + "\n");

      // (POSTPONED; TODO; DISABLED; UNTESTABLE or DUPLICATE)
      writer.write("INACTIVE:," + inactive + "\n");
      float ratio = (float)tested/(float)(total-inactive);

      // ( TESTED/(TOTAL-INACTIVE) )
      writer.write("TESTED RATIO:," + ratio + "\n");

      writer.write("\n\n\n\n");
      writer.write(output.toString());
      writer.write("\n");

      writer.close();
   }



   private List<Assertion> getAssertions(String assertionPre)
   {

      List<Assertion> result = new LinkedList<Assertion>();

      Assertion[] assertions = Assertion.class.getEnumConstants();

      for (Assertion assertion : assertions)
      {
         if (assertion.name().startsWith(assertionPre))
         {
            result.add(assertion);
         }
      }

      Collections.sort(result);

      return result;

   }


   private Assertion getAssertion(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name cannot be null");
      }

      Assertion[] assertions = Assertion.class.getEnumConstants();

      for (Assertion assertion : assertions)
      {
         if (assertion.name().equals(name))
         {
            return assertion;
         }
      }


      return null;
   }

   private String shortenClassname(String className)
   {
      if (className == null)
      {
         throw new IllegalArgumentException("null argument");
      }

      String[] parts = className.split("\\.");

      StringBuffer packed = new StringBuffer();

      for (int i = 0; i < parts.length; i++)
      {
         String part = parts[i];

         if (i != parts.length - 1)
         {
            packed.append(part.substring(0,1)).append(".");
         }
         else
         {
            packed.append(part);
         }
      }


      return packed.toString();
   }
}
