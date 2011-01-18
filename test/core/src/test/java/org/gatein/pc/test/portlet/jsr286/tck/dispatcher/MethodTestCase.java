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
package org.gatein.pc.test.portlet.jsr286.tck.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokePostResponse;
import org.jboss.unit.remote.http.HttpRequest;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_126})
public class MethodTestCase
{

   /** . */
   private String method;

   public MethodTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      //
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "GET");

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "GET");
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            check(portlet, request, response, "GET");

            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, service);
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "GET");

            //
            InvokePostResponse post = new InvokePostResponse(response.createActionURL().toString());
            post.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED);
            post.setBody(new HttpRequest.Form());
            return post;
         }
      });
      seq.bindAction(2, UTS1.SERVICE_JOIN_POINT, service);

      //
      seq.bindAction(3, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "POST");

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(3, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "POST");
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            check(portlet, request, response, "GET");

            //
            InvokePostResponse post = new InvokePostResponse(response.createResourceURL().toString());
            post.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED);
            post.setBody(new HttpRequest.Form());
            return post;
         }
      });
      seq.bindAction(3, UTS1.SERVICE_JOIN_POINT, service);
      seq.bindAction(4, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            check(portlet, request, response, "POST");

            //
            return new EndTestResponse();
         }
      });
      seq.bindAction(4, UTS1.SERVICE_JOIN_POINT, service);
   }

   private void check(
      Portlet portlet,
      PortletRequest request,
      PortletResponse response,
      String expectedMethod) throws PortletException, IOException
   {
      method = null;
      PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
      dispatcher.include(request, response);
      assertEquals(expectedMethod, method);

      //
      method = null;
      dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/universalServletA");
      dispatcher.include(request, response);
      assertEquals(expectedMethod, method);
   }

   private final ServletServiceTestAction service = new ServletServiceTestAction()
   {
      protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
      {
         method = request.getMethod();
         return null;
      }
   };
}