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
package org.gatein.pc.test.portlet.jsr286.tck.portletfilter;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

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
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase(Assertion.JSR286_296)
public class PropagateDoFilterArgsTestCase
{
   public PropagateDoFilterArgsTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
         {
            return new InvokeGetResponse(resp.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP3.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest req, ActionResponse resp, PortletTestContext context) throws PortletException, IOException
         {
            assertSame(PropagateDoFilterArgsFilter1.publishedActionRequest, PropagateDoFilterArgsFilter2.consumedActionRequest);
            assertSame(PropagateDoFilterArgsFilter1.publishedActionResponse, PropagateDoFilterArgsFilter2.consumedActionResponse);
            assertSame(PropagateDoFilterArgsFilter2.publishedActionRequest, req);
            assertSame(PropagateDoFilterArgsFilter2.publishedActionResponse, resp);

            //
            resp.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP3.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest req, EventResponse resp, PortletTestContext context) throws PortletException, IOException
         {
            assertSame(PropagateDoFilterArgsFilter1.publishedEventRequest, PropagateDoFilterArgsFilter2.consumedEventRequest);
            assertSame(PropagateDoFilterArgsFilter1.publishedEventResponse, PropagateDoFilterArgsFilter2.consumedEventResponse);
            assertSame(PropagateDoFilterArgsFilter2.publishedEventRequest, req);
            assertSame(PropagateDoFilterArgsFilter2.publishedEventResponse, resp);
         }
      });
      seq.bindAction(1, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
         {
            assertSame(PropagateDoFilterArgsFilter1.publishedRenderRequest, PropagateDoFilterArgsFilter2.consumedRenderRequest);
            assertSame(PropagateDoFilterArgsFilter1.publishedRenderResponse, PropagateDoFilterArgsFilter2.consumedRenderResponse);
            assertSame(PropagateDoFilterArgsFilter2.publishedRenderRequest, req);
            assertSame(PropagateDoFilterArgsFilter2.publishedRenderResponse, resp);

            //
            return new InvokeGetResponse(resp.createResourceURL().toString());
         }
      });
      seq.bindAction(2, UTP3.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest req, ResourceResponse resp, PortletTestContext context) throws PortletException, IOException
         {
            assertSame(PropagateDoFilterArgsFilter1.publishedResourceRequest, PropagateDoFilterArgsFilter2.consumedResourceRequest);
            assertSame(PropagateDoFilterArgsFilter1.publishedResourceResponse, PropagateDoFilterArgsFilter2.consumedResourceResponse);
            assertSame(PropagateDoFilterArgsFilter2.publishedResourceRequest, req);
            assertSame(PropagateDoFilterArgsFilter2.publishedResourceResponse, resp);

            //
            return new EndTestResponse();
         }
      });
   }
}
