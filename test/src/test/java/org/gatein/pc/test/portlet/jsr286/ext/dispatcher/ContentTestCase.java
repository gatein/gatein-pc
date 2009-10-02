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
package org.gatein.pc.test.portlet.jsr286.ext.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.EXT_DISPATCHER_2
   })
public class ContentTestCase
{
   public ContentTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.print("@ShouldNotBePresent@");

            //
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getRequestDispatcher("/ForwardedRequestContentServlet");
            assertNotNull(prd);
            prd.forward(request, response);

            //
            assertTrue(response.isCommitted());

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            byte[] bytes = context.getResponseBody();
            String s = new String(bytes);
            assertFalse("Page " + s + " should not contain the string @ShouldNotBePresent@", s.contains("@ShouldNotBePresent@"));
            assertTrue("Page " + s + " should contain the string @ForwardedRequestContent@", s.contains("@ForwardedRequestContent@"));

            //
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.print("@ShouldNotBePresent@");

            //
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getNamedDispatcher("ForwardedNamedContentServlet");
            assertNotNull(prd);
            prd.forward(request, response);

            //
            assertTrue(response.isCommitted());

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            byte[] bytes = context.getResponseBody();
            String s = new String(bytes);
            assertFalse("Page " + s + " should not contain the string @ShouldNotBePresent@", s.contains("@ShouldNotBePresent@"));
            assertTrue("Page " + s + " should contain the string @NamedRequestContent@", s.contains("@ForwardedNamedContent@"));

            //
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.print("@BeforeIncludeRequestContent@");

            //
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getRequestDispatcher("/IncludedRequestContentServlet");
            assertNotNull(prd);
            prd.include(request, response);

            //
            assertFalse(response.isCommitted());
            writer.print("@AfterIncludeRequestContent@");

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            byte[] bytes = context.getResponseBody();
            String s = new String(bytes);
            String t = "@BeforeIncludeRequestContent@@IncludedRequestContent@@AfterIncludeRequestContent@";
            assertTrue("Page " + s + " should contain the string " + t, s.contains(t));

            //
            response.setContentType("text/html");
            PrintWriter writer = response.getWriter();
            writer.print("@BeforeIncludeNamedContent@");

            //
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getNamedDispatcher("IncludedNamedContentServlet");
            assertNotNull(prd);
            prd.include(request, response);

            //
            assertFalse(response.isCommitted());
            writer.print("@AfterIncludeNamedContent@");

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(4, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            byte[] bytes = context.getResponseBody();
            String s = new String(bytes);
            String t = "@BeforeIncludeNamedContent@@IncludedNamedContent@@AfterIncludeNamedContent@";
            assertTrue("Page " + s + " should contain the string " + t, s.contains(t));

            //
            return new EndTestResponse();
         }
      });
   }
}
