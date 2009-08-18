/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.gatein.pc.samples.basic;

import org.gatein.common.text.EntityEncoder;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.ActionResponse;
import javax.portlet.ActionRequest;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * This portlet is used to display a form that helps to test the charset handling by the portal and the client. When a
 * fault occurs it may be the client fault (for instance the char with unicode value 160 fail the test in firefox but
 * succeds in IE6).
 * 
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class CharsetPortlet extends GenericPortlet
{

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      String fromString = req.getParameter("from");
      String toString = req.getParameter("to");
      int from;
      int to;
      try
      {
         from = Integer.parseInt(fromString);
         to = Integer.parseInt(toString);
      }
      catch (NumberFormatException ignore)
      {
         from = 65;
         to = 256;
      }
      resp.setRenderParameter("from", "" + from);
      resp.setRenderParameter("to", "" + to);

      //
      String text = req.getParameter("text");
      if (text != null)
      {
         StringBuffer buffer = new StringBuffer();
         for (int i = from; i < to; i++)
         {
            char c = (char)i;
            if (useChar(c))
            {
               buffer.append(c);
            }
         }
         String expectedText = buffer.toString();
         boolean same = expectedText.equals(text);
         if (!same)
         {
            getPortletContext().log("The input does not match the expected string");
            getPortletContext().log("Expected string " + expectedText);
            getPortletContext().log("Received string " + text);
         }
         resp.setRenderParameter("same", "" + same);
      }
   }


   protected void doHelp(RenderRequest req, RenderResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      //
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //
      writer.print("This portlet shows different ways to properly display and submit unicode text");
   }

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      String sameString = req.getParameter("same");
      Boolean same = null;
      if ("true".equalsIgnoreCase(sameString))
      {
         same = Boolean.TRUE;
      }
      else if ("false".equalsIgnoreCase(sameString))
      {
         same = Boolean.FALSE;
      }

      //
      String fromString = req.getParameter("from");
      String toString = req.getParameter("to");
      int from = 65;
      int to = 256;
      try
      {
         from = Integer.parseInt(fromString);
         to = from + 16;
         to = Integer.parseInt(toString);
      }
      catch (NumberFormatException ignore)
      {
         //
      }

      //
      if (to <= from)
      {
         to = from + 16;
      }

      //
      StringBuffer escapedText = new StringBuffer();
      StringBuffer text = new StringBuffer();
      for (int i = from; i < to; i++)
      {
         char c = (char)i;
         if (useChar(c))
         {
            text.append(c);
            String s = EntityEncoder.FULL.lookup(c);
            if (s == null)
            {
               escapedText.append(c);
            }
            else
            {
               escapedText.append("&").append(s).append(";");
            }
         }
      }

      //
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //
      writer.println("<div style=\"border-top:solid 1px\">Testing range:</div>");
      writer.print(
         "<div>" +
            "<form action=\"" + resp.createActionURL() + "\" method=\"post\"\">" +
            "<input type=\"text\" name=\"from\" value=\"" + from + "\"/>" +
            "<input type=\"text\" name=\"to\" value=\"" + to + "\"/>" +
            "<input type=\"submit\" value=\"change\"/>" +
            "</form>" +
            "</div>");

      //
      writer.println("<div style=\"border-top:solid 1px\">Test processAction() with a textarea field:</div>");
      writer.println(
         "<div>" +
            "<form action=\"" + resp.createActionURL() + "\" accept-charset=\"" + resp.getCharacterEncoding() + "\" method=\"post\">" +
            "<input type=\"hidden\" name=\"from\" value=\"" + from + "\"/>" +
            "<input type=\"hidden\" name=\"to\" value=\"" + to + "\"/>" +
            "<textarea name=\"text\" cols=\"20\" rows=\"10\" wrap=\"virtual\">" + escapedText + "</textarea>" +
            "<input type=\"submit\" value=\"check\"/>" +
            "</form>" +
            "</div>");

      //
      writer.println("<div style=\"border-top:solid 1px\">Test processAction() with a text field:</div>");
      writer.println(
         "<div>" +
            "<form action=\"" + resp.createActionURL() + "\" accept-charset=\"" + resp.getCharacterEncoding() + "\" method=\"post\">" +
            "<input type=\"hidden\" name=\"from\" value=\"" + from + "\"/>" +
            "<input type=\"hidden\" name=\"to\" value=\"" + to + "\"/>" +
            "<input type=\"text\" name=\"text\" value=\"" + escapedText + "\"/>" +
            "<input type=\"submit\" value=\"check\"/>" +
            "</form>" +
            "</div>");

      //
      PortletURL url = resp.createActionURL();
      url.setParameter("text", text.toString());
      writer.println("<div style=\"border-top:solid 1px\">Test processAction() with a portlet parameter:</div>");
      writer.println(
         "<div>" +
            "<form action=\"" + url + "\" accept-charset=\"" + resp.getCharacterEncoding() + "\" method=\"post\">" +
            "<input type=\"hidden\" name=\"from\" value=\"" + from + "\"/>" +
            "<input type=\"hidden\" name=\"to\" value=\"" + to + "\"/>" +
            "<input type=\"submit\" value=\"check\"/>" +
            "</form>" +
            "</div>");

      //
      PortletURL url2 = resp.createActionURL();
      url2.setParameter("text", text.toString());
      url2.setParameter("from", "" + from);
      url2.setParameter("to", "" + to);
      writer.println("<div style=\"border-top:solid 1px\"><a href=\"" + url2 + "\">Test</a> processAction() with a portlet parameter:</div>");

      //
      if (same != null)
      {
         writer.print("<div style=\"color:red;margin-top:2em;margin-bottom:2em\">Test result: ");
         if (same == Boolean.TRUE)
         {
            writer.print("The input matched the expected result");
         }
         else if (same == Boolean.FALSE)
         {
            writer.print("The input did not matched the expected result");
         }
         writer.print("</div>");
      }

      //
      writer.close();
   }

   public static boolean useChar(char c)
   {
      switch (Character.getType(c))
      {
         case Character.LOWERCASE_LETTER:          // Ll
         case Character.UPPERCASE_LETTER:          // Lu
         case Character.TITLECASE_LETTER:          // Lt
         case Character.MODIFIER_LETTER:           // Lm
         case Character.OTHER_LETTER:              // Lo
         case Character.NON_SPACING_MARK:          // Mn
         case Character.ENCLOSING_MARK:            // Me
         case Character.COMBINING_SPACING_MARK:    // Mc
         case Character.DECIMAL_DIGIT_NUMBER:      // Nd
         case Character.LETTER_NUMBER:             // Nl
         case Character.OTHER_NUMBER:              // No
         case Character.SPACE_SEPARATOR:           // Zs
         case Character.LINE_SEPARATOR:            // Zl
         case Character.PARAGRAPH_SEPARATOR:       // Zp
         case Character.DASH_PUNCTUATION:          // Pd
         case Character.START_PUNCTUATION:         // Ps
         case Character.END_PUNCTUATION:           // Pe
         case Character.CONNECTOR_PUNCTUATION:     // Pc
         case Character.OTHER_PUNCTUATION:         // Po
         case Character.INITIAL_QUOTE_PUNCTUATION: // Pi
         case Character.FINAL_QUOTE_PUNCTUATION:   // Pf
         case Character.MATH_SYMBOL:               // Sm
         case Character.CURRENCY_SYMBOL:           // Sc
         case Character.MODIFIER_SYMBOL:           // Sk
         case Character.OTHER_SYMBOL:              // So
            return true;
         default:
         case Character.UNASSIGNED:                // Cn
         case Character.CONTROL:                   // Cc
         case Character.FORMAT:                    // Cf
         case Character.PRIVATE_USE:               // Co
         case Character.SURROGATE:                 // Cs
            return false;
      }
   }

}
