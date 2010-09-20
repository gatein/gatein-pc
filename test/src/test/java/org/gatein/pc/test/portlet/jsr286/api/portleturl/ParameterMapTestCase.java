/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.pc.test.portlet.jsr286.api.portleturl;

import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.jboss.unit.api.Assert.assertEquals;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
@TestCase(Assertion.API286_BASE_URL_4)
public class ParameterMapTestCase
{
   public ParameterMapTestCase(PortletTestCase seq)
   {

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletURL url = response.createRenderURL();

            // Set parameter
            url.setParameter("foo", "bar");

            // Clear parameters
            Map<String, String[]> map = url.getParameterMap();

            // Check expected entry
            String[] bar1 = map.get("foo");
            assertEquals(1, bar1.length);
            assertEquals("bar", bar1[0]);

            // Check that the entry we had a copy of the value
            url.setParameter("foo", "juu");
            assertEquals("bar", bar1[0]);

            //
            map.clear();

            // Invoker with the no parameter URL
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            // It should be empty
            assertEquals(Collections.<Object, Object>emptyMap(), request.getParameterMap());

            // Done
            return new EndTestResponse();
         }
      });
   }
}
