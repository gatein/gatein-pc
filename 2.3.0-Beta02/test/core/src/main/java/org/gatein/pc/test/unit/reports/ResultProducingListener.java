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

import com.sun.mirror.apt.RoundCompleteEvent;
import com.sun.mirror.apt.RoundCompleteListener;
import org.gatein.pc.test.unit.Assertion;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class ResultProducingListener implements RoundCompleteListener
{
   public static String jsr168File = "./target/apt-jsr168-testcases.csv";

   public static String jsr286File = "./target/apt-jsr286-testcases.csv";

   public static String api286File = "./target/apt-api286-testcases.csv";

   public static String extFile = "./target/apt-ext-testcases.csv";

   public void roundComplete(RoundCompleteEvent event)
   {
      if (event.getRoundState().finalRound())
      {
         System.out.println("#END");


         try
         {
            //Add 168 assertions test cases to related 286
            mergeAssertions();


            if (!TestCaseAnnotationProcessor.jsr168Assertions.keySet().isEmpty())
            {
               printReport(TestCaseAnnotationProcessor.jsr168Assertions, jsr168File, "JSR168_");
            }

            if (!TestCaseAnnotationProcessor.jsr286Assertions.keySet().isEmpty())
            {
               printReport(TestCaseAnnotationProcessor.jsr286Assertions, jsr286File, "JSR286_");
            }

            if (!TestCaseAnnotationProcessor.api286Assertions.keySet().isEmpty())
            {
               printReport(TestCaseAnnotationProcessor.api286Assertions, api286File, TestCaseAnnotationProcessor.api286AssertionPre);
            }

            if (!TestCaseAnnotationProcessor.extAssertions.keySet().isEmpty())
            {
               printReport(TestCaseAnnotationProcessor.extAssertions, extFile, TestCaseAnnotationProcessor.extAssertionPre);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }


      }
   }


   private void mergeAssertions()
   {
      Map<String, Set<String>> jsr168a = TestCaseAnnotationProcessor.jsr168Assertions;
      Map<String, Set<String>> jsr286a = TestCaseAnnotationProcessor.jsr286Assertions;

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
