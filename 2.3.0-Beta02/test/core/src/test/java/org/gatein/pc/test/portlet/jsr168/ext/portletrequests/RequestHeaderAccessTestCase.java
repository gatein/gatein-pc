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
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertFalse;
import static org.jboss.unit.api.Assert.assertNull;
import static org.jboss.unit.api.Assert.assertTrue;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Set;
import java.util.Enumeration;

/**
 * Show that we can access request headers from the portlet request properties.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_PORTLET_REQUESTS_6
   })
public class RequestHeaderAccessTestCase
{
   public RequestHeaderAccessTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            // Test the value is not there yet
            Set propertyNames = Tools.toSet(request.getPropertyNames());
            assertFalse(propertyNames.contains("myheader"));
            assertNull(request.getProperty("myheader"));
            assertFalse(request.getProperties("myheader").hasMoreElements());

            // Invoke render with header
            InvokeGetResponse render = new InvokeGetResponse(response.createRenderURL().toString());
            render.addHeader("myheader").addElement("render-value");
            return render;
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            // Test the header is here
            Set propertyNames = Tools.toSet(request.getPropertyNames());
            assertTrue(propertyNames.contains("myheader"));
            assertEquals("render-value", request.getProperty("myheader"));
            Enumeration values = request.getProperties("myheader");
            assertTrue(values.hasMoreElements());
            assertEquals("render-value", values.nextElement());
            assertFalse(values.hasMoreElements());

            // Invoke action with header
            InvokeGetResponse action = new InvokeGetResponse(response.createActionURL().toString());
            action.addHeader("myheader").addElement("action-value");
            return action;
         }
      });

      seq.bindAction(2, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            // Test the header is here
            Set propertyNames = Tools.toSet(request.getPropertyNames());
            assertTrue(propertyNames.contains("myheader"));
            assertEquals("action-value", request.getProperty("myheader"));
            Enumeration values = request.getProperties("myheader");
            assertTrue(values.hasMoreElements());
            assertEquals("action-value", values.nextElement());
            assertFalse(values.hasMoreElements());
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
