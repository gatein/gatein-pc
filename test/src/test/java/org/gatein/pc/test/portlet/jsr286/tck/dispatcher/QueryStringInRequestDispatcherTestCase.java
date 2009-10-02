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
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.pc.test.portlet.framework.UTS2;
import org.gatein.pc.test.portlet.framework.UTS3;
import org.gatein.pc.test.portlet.framework.UTS4;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_123})
public class QueryStringInRequestDispatcherTestCase
{

   public QueryStringInRequestDispatcherTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL actionURL = response.createActionURL();
            actionURL.setParameter("key1", "k1actionvalue");
            actionURL.setParameter("key3", "k3actionvalue");
            return new InvokeGetResponse(actionURL.toString());
         }
      });

      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws IOException, PortletException
         {
            Map<String, String[]> actionParams = new HashMap<String, String[]>();
            actionParams.put("key1", new String[]{"k1actionvalue"});
            actionParams.put("key3", new String[]{"k3actionvalue"});
            assertParameterMap(actionParams, request);

            //
            String path = "/universalServletA?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher(path);
            dispatcher.include(request, response);

            //assert that params from query string doesn't last longer then in include call
            assertParameterMap(actionParams, request);

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            assertEquals(new String[]{"k1value1","k1actionvalue"}, request.getParameterValues("key1"));
            assertEquals(new String[]{"k2value1"}, request.getParameterValues("key2"));
            assertEquals(new String[]{"k3actionvalue"}, request.getParameterValues("key3"));
            return null;
         }
      });

      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> eventParams = new HashMap<String, String[]>();
            assertParameterMap(eventParams, request);

            //
            String path = "/universalServletB?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher(path);
            dispatcher.include(request, response);

            //assert that params from query string doesn't last longer then in include call
            assertParameterMap(eventParams, request);

            //set some render params to test them in dispatcher include (precedense)
            response.setRenderParameter("key1", "k1rendervalue");
            response.setRenderParameter("key3", "k3rendervalue");
         }
      });
      seq.bindAction(1, UTS2.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            assertEquals(new String[]{"k1value1"}, request.getParameterValues("key1"));
            assertEquals(new String[]{"k2value1"}, request.getParameterValues("key2"));
            assertEquals(null, request.getParameterValues("key3"));
            return null;
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            Map<String, String[]> renderParams = new HashMap<String, String[]>();
            renderParams.put("key1", new String[]{"k1rendervalue"});
            renderParams.put("key3", new String[]{"k3rendervalue"});
            assertParameterMap(renderParams, request);

            //
            String path = "/universalServletC?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher(path);
            dispatcher.include(request, response);

            //assert that params from query string doesn't last longer then in include call
            assertParameterMap(renderParams, request);

            //
            ResourceURL resourceURL = response.createResourceURL();
            resourceURL.setParameter("key1", "k1resourcevalue");
            resourceURL.setParameter("key3", "k3resourcevalue");
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(1, UTS3.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            assertEquals(new String[]{"k1value1","k1rendervalue"}, request.getParameterValues("key1"));
            assertEquals(new String[]{"k2value1"}, request.getParameterValues("key2"));
            assertEquals(new String[]{"k3rendervalue"}, request.getParameterValues("key3"));
            return null;
         }
      });
      
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> resourceParams = new HashMap<String, String[]>();
            resourceParams.put("key1", new String[]{"k1resourcevalue","k1rendervalue"});
            resourceParams.put("key3", new String[]{"k3resourcevalue","k3rendervalue"});
            assertParameterMap(resourceParams, request);

            //
            String path = "/universalServletD?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher(path);
            dispatcher.include(request, response);

            //assert that params from query string doesn't last longer then in include call
            assertParameterMap(resourceParams, request);

            //
            return new EndTestResponse();
         }
      });
      seq.bindAction(2, UTS4.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            assertEquals(new String[]{"k1value1","k1resourcevalue","k1rendervalue"}, request.getParameterValues("key1"));
            assertEquals(new String[]{"k2value1"}, request.getParameterValues("key2"));
            assertEquals(new String[]{"k3resourcevalue","k3rendervalue"}, request.getParameterValues("key3"));
            return null;
         }
      });
   }
}