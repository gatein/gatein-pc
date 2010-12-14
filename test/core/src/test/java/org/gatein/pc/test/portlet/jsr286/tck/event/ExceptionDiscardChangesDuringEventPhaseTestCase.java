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
package org.gatein.pc.test.portlet.jsr286.tck.event;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.portlet.framework.UTP9;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.RenderRequest;
import javax.portlet.Portlet;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_146})
public class ExceptionDiscardChangesDuringEventPhaseTestCase
{
   public ExceptionDiscardChangesDuringEventPhaseTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP9.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP9.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setEvent("Bar", null);
            response.setRenderParameter("foo", "actionbar");
         }
      });
      seq.bindAction(1, UTP9.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (request.getEvent().getName().equals("Bar"))
            {
               response.setRenderParameter("foo", "eventbar");
               response.setEvent("Bar", null);
               throw new PortletException();
            }
            else
            {
               fail("Should not be here");
            }
         }
      });
      seq.bindAction(1, UTP9.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals(1, request.getParameterMap().size());
            assertTrue(request.getParameterMap().containsKey("foo"));
            assertEquals(new String[]{"actionbar"}, request.getParameterMap().get("foo"));
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(2, UTP9.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setEvent("Bar", null);
            response.setRenderParameter("foo", "actionbar");
         }
      });
      seq.bindAction(2, UTP9.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void runWithRuntimeException(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (request.getEvent().getName().equals("Bar"))
            {
               response.setRenderParameter("foo", "eventbar");
               response.setEvent("Bar", null);
               throw new RuntimeException();
            }
            else
            {
               fail("Should not be here");
            }
         }
      });
      seq.bindAction(2, UTP9.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals(1, request.getParameterMap().size());
            assertTrue(request.getParameterMap().containsKey("foo"));
            assertEquals(new String[]{"actionbar"}, request.getParameterMap().get("foo"));
            return new EndTestResponse();
         }
      });
   }
}