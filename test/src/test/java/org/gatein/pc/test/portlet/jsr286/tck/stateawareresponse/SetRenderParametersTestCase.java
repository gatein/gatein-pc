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
package org.gatein.pc.test.portlet.jsr286.tck.stateawareresponse;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.RenderRequest;
import javax.portlet.Portlet;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_103})
public class SetRenderParametersTestCase
{
   public SetRenderParametersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setRenderParameter("foo", "fooaction1");
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertRenderParameter(request, "foo", "fooaction1");
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      //
      seq.bindAction(2, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setRenderParameter("foo", "fooaction2");
            response.setEvent("Foo", null);
         }
      });
      seq.bindAction(2, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if ("Foo".equals(request.getEvent().getName()))
            {
               assertRenderParameter(request, "foo", "fooaction2");
               response.setRenderParameter("foo", "fooevent1");
            }
            else
            {
               fail();
            }
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertRenderParameter(request, "foo", "fooevent1");
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      //
      seq.bindAction(3, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setRenderParameter("foo", "fooaction3");
            response.setEvent("Foo", null);
         }
      });
      seq.bindAction(3, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            String eventName = request.getEvent().getName();
            if ("Foo".equals(eventName))
            {
               assertRenderParameter(request, "foo", "fooaction3");
               response.setRenderParameter("foo", "fooevent2");
               response.setEvent("Bar", null);
            }
            else if ("Bar".equals(eventName))
            {
               assertRenderParameter(request, "foo", "fooevent2");
               response.setRenderParameter("foo", "fooevent3");
            }
            else
            {
               fail();
            }
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertRenderParameter(request, "foo", "fooevent3");
            return new EndTestResponse();
         }
      });
   }

   private void assertRenderParameter(PortletRequest request, String name, String value)
   {
      assertEquals(1, request.getParameterMap().size());
      assertTrue(request.getParameterMap().containsKey(name));
      assertEquals(new String[]{value}, request.getParameterMap().get(name));
      assertEquals(value, request.getParameter(name));
      assertEquals(new String[]{value}, request.getParameterValues(name));
   }

}