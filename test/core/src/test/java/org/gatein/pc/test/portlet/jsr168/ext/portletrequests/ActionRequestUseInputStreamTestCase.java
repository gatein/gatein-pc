/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.test.portlet.jsr168.ext.portletrequests;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.common.io.IOTools;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokePostResponse;
import org.jboss.unit.remote.http.HttpRequest;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.fail;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test that a POST request having a content type not set to x-www-form-urlencoded will make the body content
 * available as a reader.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_PORTLET_REQUESTS_2
   })
public class ActionRequestUseInputStreamTestCase
{

   private final byte[] byteContent = {65, 66, 67, 68, 69, 70};

   public ActionRequestUseInputStreamTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {

         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL url = response.createActionURL();
            url.setParameter("g_foo1", "g_bar1_1");
            url.setParameter("g_foo2", new String[]{"g_bar2_1", "g_bar2_2"});
            InvokePostResponse iur = new InvokePostResponse(url.toString());
            HttpRequest.Raw body = new HttpRequest.Raw();
            body.setBytes(byteContent);
            iur.setBody(body);
            iur.setContentType(InvokePostResponse.MULTIPART_FORM_DATA);
            return iur;
         }
      });

      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {

         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws IOException
         {

            assertEquals(new String[]{"g_bar1_1"}, request.getParameterValues("g_foo1"));
            assertEquals(new String[]{"g_bar2_1", "g_bar2_2"}, request.getParameterValues("g_foo2"));
            assertEquals(InvokePostResponse.MULTIPART_FORM_DATA, request.getContentType());
            assertEquals(byteContent.length, request.getContentLength());
            InputStream in = null;
            try
            {
               in = request.getPortletInputStream();
               ByteArrayOutputStream out = new ByteArrayOutputStream();
               IOTools.copy(in, out);
               out.close();
               byte[] bytes = out.toByteArray();
               assertEquals(-1, in.read());
               assertEquals(byteContent, bytes);
            }
            catch (IllegalStateException e)
            {
               fail("Was not expecting an ISE");
            }
            catch (IOException e)
            {
               fail("Was not expecting an IOException");
            }
            finally
            {
               IOTools.safeClose(in);
            }
            try
            {
               request.getReader();
               fail("The reader should not be available");
            }
            catch (IllegalStateException expected)
            {
            }
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
