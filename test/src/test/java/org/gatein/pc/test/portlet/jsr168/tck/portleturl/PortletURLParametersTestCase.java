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
package org.gatein.pc.test.portlet.jsr168.tck.portleturl;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertNull;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_27, Assertion.JSR168_28, Assertion.JSR168_29})
public class PortletURLParametersTestCase
{
   public PortletURLParametersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL url = response.createActionURL();

            //set some render parameters to test
            url.setParameter("key1", "some strange value to overwrite");
            url.setParameter("key2", "some strange value to overwrite 2");
            url.setParameter("key1", "k1value1");
            url.setParameter("key2", new String[]{"k2value1", "k2value2", "k2value3"});


            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            assertEquals("k1value1", request.getParameter("key1"));
            assertEquals(new String[]{"k2value1", "k2value2", "k2value3"}, request.getParameterValues("key2"));
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL url = response.createActionURL();

            //
            url.setParameter("key3", "some strange value to overwrite");
            url.setParameter("key4", "some strange value to overwrite 2");

            //
            Map map = new HashMap();
            map.put("key3", new String[]{"k3value1"});
            map.put("key4", new String[]{"k4value1", "k4value2", "k4value3"});
            url.setParameters(map);

            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(2, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            //what was in previous request
            assertNull(request.getParameter("key1"));
            assertNull(request.getParameter("key2"));

            //what is now
            assertEquals("k3value1", request.getParameter("key3"));
            assertEquals(new String[]{"k4value1", "k4value2", "k4value3"}, request.getParameterValues("key4"));
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL url = response.createRenderURL();

            Map map = new HashMap();
            url.setParameter("key5", "some strange value to overwrite");
            url.setParameter("key6", "some strange value to overwrite 2");
            map.put("key5", new String[]{"k5value1"});
            map.put("key6", new String[]{"k6value1", "k6value2", "k6value3"});
            url.setParameters(map);
            url.setParameter("key7", new String[]{"k7value1", "k7value2"});

            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //what was in previous request
            assertNull(request.getParameter("key1"));
            assertNull(request.getParameter("key2"));
            assertNull(request.getParameter("key3"));
            assertNull(request.getParameter("key4"));

            //what is now
            assertEquals("k5value1", request.getParameter("key5"));
            assertEquals(new String[]{"k6value1", "k6value2", "k6value3"}, request.getParameterValues("key6"));
            assertEquals(new String[]{"k7value1", "k7value2"}, request.getParameterValues("key7"));
            return new EndTestResponse();
         }
      });
   }
}
