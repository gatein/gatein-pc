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
package org.gatein.pc.test.portlet.jsr286.tck.portletrequests;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.gatein.pc.test.portlet.framework.UTP4;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.Portlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import java.io.IOException;

/**
 * lxxxii:
 *
 * The portlet container must only send those public render parameters to a portlet which the
 * portlet has defined support for using supported-public-render-parameter element in the portlet.
 *
 * lxxxiii:
 *
 * The portlet container must only share those render parameters of a
 * portlet which the portlet has declared as supported public render parameters using
 * supported-public-render-parameter element in the portlet.xml
 *
 * lxxxiv: 
 *
 * If the portlet was the target of a render URL and this render URL has set a specific public
 * render parameter the portlet must receive at least this render parameter
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.JSR168_82,
   Assertion.JSR168_83,
   Assertion.JSR168_84
   })
public class PortletDoesNotReceiveUnsupportedRenderParametersTestCase
{

   public PortletDoesNotReceiveUnsupportedRenderParametersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      // Test updates from an action
      seq.bindAction(1, UTP3.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            response.setRenderParameter("foo", new String[]{"foo_value1", "foo_value2"});
            response.setRenderParameter("bar", new String[]{"bar_value1", "bar_value2"});
         }
      });
      seq.bindAction(1, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals(new String[]{"foo_value1","foo_value2"}, request.getParameterValues("foo"));
            assertEquals(new String[]{"bar_value1","bar_value2"}, request.getParameterValues("bar"));
            return null;
         }
      });
      seq.bindAction(1, UTP4.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            String[] fooValues = request.getParameterValues("foo");
            assertEquals(new String[]{"foo_value1","foo_value2"}, fooValues);
            assertEquals(null, request.getParameterValues("bar"));
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      // Test updates from a render URL
      seq.bindAction(2, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL url = response.createRenderURL();
            url.setParameter("foo", new String[]{"foo_value3", "foo_value4"});
            url.setParameter("bar", new String[]{"bar_value3", "bar_value4"});
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(3, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals(new String[]{"foo_value3","foo_value4"}, request.getParameterValues("foo"));
            assertEquals(new String[]{"bar_value3","bar_value4"}, request.getParameterValues("bar"));
            return null;
         }
      });
      seq.bindAction(3, UTP4.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            String[] fooValues = request.getParameterValues("foo");
            assertEquals(new String[]{"foo_value3","foo_value4"}, fooValues);
            assertEquals(null, request.getParameterValues("bar"));
            //
//            PortletURL renderURL = response.createRenderURL();
//            renderURL.setParameter("bar", "");
            return new EndTestResponse();
         }
      });

      // Test updates from an action with the portlet having a private render parameter

   }
}