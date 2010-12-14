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
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP4;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceURL;
import javax.portlet.PortletURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * For serveResource requests the portlet must receive any resource parameters that were
 * explicitly set on the ResourceURL that triggered the request. If the cacheability level of
 * that resource URL (see PLT.13.7) was PORTLET or PAGE, the portlet must also receive the
 * render parameters present in the request in which the URL was created
 * 
 * If a resource parameter is set that has the same name as a render parameter, the render
 * parameter must be the last entry in the parameter value array.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.EXT_PORTLET_REQUESTS_12
   })
public class ResourceRequestParametersTestCase
{
   public ResourceRequestParametersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP4.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("foo", new String[]{"render_foo_value1","render_foo_value2"});
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(1, UTP4.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            ResourceURL resourceURL = response.createResourceURL();
            resourceURL.setParameter("foo", new String[]{"resource_foo_value1","resource_foo_value2"});
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(2, UTP4.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedPageParameters = new HashMap<String, String[]>();
            expectedPageParameters.put("foo", new String[]{"resource_foo_value1","resource_foo_value2","render_foo_value1","render_foo_value2"});
            assertParameterMap(expectedPageParameters, request);

            //
            ResourceURL resourceURL = response.createResourceURL();
            resourceURL.setCacheability(ResourceURL.PORTLET);
            resourceURL.setParameter("foo", new String[]{"resource_foo_value3","resource_foo_value4"});
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(3, UTP4.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedPageParameters = new HashMap<String, String[]>();
            expectedPageParameters.put("foo", new String[]{"resource_foo_value3","resource_foo_value4","render_foo_value1","render_foo_value2"});
            assertParameterMap(expectedPageParameters, request);

            //
            return new EndTestResponse();
         }
      });

      
   }
}
