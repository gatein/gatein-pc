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
package org.gatein.pc.test.portlet.jsr286.ext.portletcontext;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
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
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
//@TestCase
public class ScopePropagationToEventPhaseTestCase
{
   public ScopePropagationToEventPhaseTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            request.setAttribute("action_attr1", "action_attr1_action_value");
            request.setAttribute("action_attr2", "action_attr2_action_value");
            request.setAttribute("action_attr3", "action_attr3_action_value");
            request.setAttribute("action_attr4", "action_attr4_action_value");
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (request.getEvent().getValue() == null)
            {
               assertEquals("action_attr1_action_value", request.getAttribute("action_attr1"));
               assertEquals("action_attr2_action_value", request.getAttribute("action_attr2"));
               assertEquals("action_attr3_action_value", request.getAttribute("action_attr3"));
               assertEquals("action_attr4_action_value", request.getAttribute("action_attr4"));

               //
               request.setAttribute("action_attr2", "action_attr2_event0_value");
               request.setAttribute("action_attr3", "action_attr3_event0_value");
               request.setAttribute("event_attr1", "event_attr1_event0_value");
               request.setAttribute("event_attr2", "event_attr2_event0_value");

               //
               response.setEvent("Event", "this string is a non null object");
            }
            else
            {
               assertEquals("action_attr1_action_value", request.getAttribute("action_attr1"));
               assertEquals("action_attr2_event0_value", request.getAttribute("action_attr2"));
               assertEquals("action_attr3_event0_value", request.getAttribute("action_attr3"));
               assertEquals("action_attr4_action_value", request.getAttribute("action_attr4"));
               assertEquals("event_attr1_event0_value", request.getAttribute("event_attr1"));
               assertEquals("event_attr2_event0_value", request.getAttribute("event_attr2"));

               //
               request.setAttribute("action_attr3", "action_attr3_event1_value");
               request.setAttribute("action_attr4", "action_attr3_event1_value");
               request.setAttribute("event_attr2", "event_attr2_event1_value");
               request.setAttribute("event_attr3", "event_attr3_event1_value");
            }
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals("action_attr1_action_value", request.getAttribute("action_attr1"));
            assertEquals("action_attr2_event0_value", request.getAttribute("action_attr2"));
            assertEquals("action_attr3_event1_value", request.getAttribute("action_attr3"));
            assertEquals("action_attr3_event1_value", request.getAttribute("action_attr4"));
            assertEquals("event_attr1_event0_value", request.getAttribute("event_attr1"));
            assertEquals("event_attr2_event1_value", request.getAttribute("event_attr2"));
            assertEquals("event_attr3_event1_value", request.getAttribute("event_attr3"));

            //
            return new EndTestResponse();
         }
      });
   }
}