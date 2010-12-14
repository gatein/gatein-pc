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
package org.gatein.pc.test.portlet.jsr286.ext.portletrequests;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test that removePublicRenderParameter method on PortletURL removes the render parameter
 * in the context of a render url and does nothing in the context of an action url.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.EXT_PORTLET_REQUESTS_11
   })
public class RemovePublicRenderParameterWithPortletURLTestCase
{

   /** . */
   private final Map<String, String[]> expectedPublicRenderParameterMap = Collections.singletonMap("foo", new String[]{"foo_value"});

   public RemovePublicRenderParameterWithPortletURLTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("foo", "foo_value");
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(1, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());

            //
            PortletURL renderURL = response.createRenderURL();
            renderURL.removePublicRenderParameter("foo");
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(2, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(new HashMap<String, String[]>(), request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());

            //
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("foo", "foo_value");
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(2, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(new HashMap<String, String[]>(), request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());
            return null;
         }
      });
      seq.bindAction(3, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());

            //
            PortletURL actionURL = response.createActionURL();
            actionURL.removePublicRenderParameter("foo");
            return new InvokeGetResponse(actionURL.toString());
         }
      });
      seq.bindAction(3, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());
            return null;
         }
      });
      seq.bindAction(4, UTP2.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());
         }
      });
      seq.bindAction(4, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());
            return new EndTestResponse();
         }
      });
      seq.bindAction(4, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicRenderParameterMap, request);
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateParameterMap());
            assertParameterMap(expectedPublicRenderParameterMap, request.getPublicParameterMap());
            return null;
         }
      });
   }
}
