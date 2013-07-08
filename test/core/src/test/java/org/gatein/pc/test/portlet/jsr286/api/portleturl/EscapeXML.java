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
package org.gatein.pc.test.portlet.jsr286.api.portleturl;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.web.UTP1;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import org.gatein.pc.test.unit.protocol.response.InvokeGetResponse;

import static org.gatein.pc.test.unit.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase(Assertion.API286_BASE_URL_5)
public class EscapeXML
{

   public EscapeXML(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setContentType("text/html");
            Writer writer = response.getWriter();

            //
            PortletURL url = response.createRenderURL();

            //
            url.setParameter("foo", "bar");

            // calling BaseURL.toString should result in a non XML-escaped String
            writer.write("@BEFORE_TOSTRING@");
            writer.write(url.toString());
            writer.write("@AFTER_TOSTRING@");

            // calling write should result in the URL being XML-escaped by default
            writer.write("@BEFORE_DEFAULT@");
            url.write(writer);
            writer.write("@AFTER_DEFAULT@");

            //
            writer.write("@BEFORE_ESCAPED@");
            url.write(writer, true);
            writer.write("@AFTER_ESCAPED@");

            //
            writer.write("@BEFORE_NON_ESCAPED@");
            url.write(writer, false);
            writer.write("@AFTER_NON_ESCAPED@");

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            byte[] content = context.getResponseBody();
            String text = new String(content, "UTF-8");

            String toString = getURL(text, "TOSTRING");
            checkNonEscaped(toString);

            String defaultWrite = getURL(text, "DEFAULT");
            checkEscaped(defaultWrite);

            //
            String escapedURL = getURL(text, "ESCAPED");
            checkEscaped(escapedURL);

            String nonEscapedURL = getURL(text, "NON_ESCAPED");
            checkNonEscaped(nonEscapedURL);

            //
            return new EndTestResponse();
         }
      });


   }

   private void checkNonEscaped(String nonEscapedURL)
   {
      for (int pos = nonEscapedURL.indexOf('&'); pos != -1; pos = nonEscapedURL.indexOf('&', pos + 1))
      {
         int end = Math.min(pos + "&amp;".length(), nonEscapedURL.length());
         String s = nonEscapedURL.substring(pos, end);
         String comparison = "&amp;".substring(0, end - pos);
         assertNotEquals(comparison, s);
      }
   }

   private void checkEscaped(String escapedURL)
   {
      for (int pos = escapedURL.indexOf('&'); pos != -1; pos = escapedURL.indexOf('&', pos + 1))
      {
         String s = escapedURL.substring(pos, pos + "&amp;".length());
         assertEquals("&amp;", s);
      }
   }

   private String getURL(String text, String type)
   {
      int from = text.indexOf("@BEFORE_" + type +  "@");
      assertNotEquals(-1, from);

      //
      int to = text.indexOf("@AFTER_" + type + "@");
      assertNotEquals(-1, to);

      //
      return text.substring(from + ("@BEFORE_" + type + "@").length(), to);
   }
}
