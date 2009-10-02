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
package org.gatein.pc.test.portlet.jsr286.ext.portletresponses;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.EXT_PORTLET_RESPONSES_3
   })
public class WriteCookiesTestCase
{
   public WriteCookiesTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertNull(cookieMap.get("render_cookie"));

            //
            Cookie cookie = new Cookie("render_cookie", "render_cookie_value");
            response.addProperty(cookie);

            //
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertNull(cookieMap.get("action_cookie"));

            //
            Cookie cookie = new Cookie("action_cookie", "action_cookie_value");
            response.addProperty(cookie);

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));

            //
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(2, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(2, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertNull(cookieMap.get("event_cookie"));

            //
            Cookie cookie = new Cookie("event_cookie", "event_cookie_value");
            response.addProperty(cookie);
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));

            //
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(3, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(3, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));

            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(4, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));

            //
            Cookie cookie = new Cookie("resource_cookie", "resource_cookie_value");
            response.addProperty(cookie);

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(5, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String> cookieMap = createCookieMap(request);
            assertEquals("render_cookie_value", cookieMap.get("render_cookie"));
            assertEquals("action_cookie_value", cookieMap.get("action_cookie"));
            assertEquals("event_cookie_value", cookieMap.get("event_cookie"));
            assertEquals("resource_cookie_value", cookieMap.get("resource_cookie"));

            //
            return new EndTestResponse();
         }
      });
   }
}
