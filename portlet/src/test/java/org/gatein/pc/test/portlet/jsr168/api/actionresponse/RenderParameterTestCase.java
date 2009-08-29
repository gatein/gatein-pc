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
package org.gatein.pc.test.portlet.jsr168.api.actionresponse;

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
import static org.jboss.unit.api.Assert.fail;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.API286_STATE_AWARE_RESPONSE_7,
   Assertion.API286_STATE_AWARE_RESPONSE_8
   })
public class RenderParameterTestCase
{
   public RenderParameterTestCase(PortletTestCase seq)
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
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {

            String val = null;
            String[] vals = null;

            try
            {
               response.setRenderParameter("name", val);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //expected
            }

            try
            {
               response.setRenderParameter(null, "val");
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            try
            {
               response.setRenderParameter("name", vals);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            try
            {
               response.setRenderParameter(null, new String[]{"val1, val2"});
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            try
            {
               response.setRenderParameter(null, val);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            try
            {
               response.setRenderParameter(null, vals);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }


            try
            {
               response.setRenderParameters(null);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            Map map = new HashMap();

            map.put("name", "val");
            map.put(null, "val");

            try
            {
               response.setRenderParameters(map);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            map = new HashMap();
            map.put("name", "val");
            map.put("name2", new Object());

            try
            {
               response.setRenderParameters(map);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }

            map = new HashMap();
            map.put("name", new String[]{"val"});
            map.put("name2", null);

            try
            {
               response.setRenderParameters(map);
               fail();
            }
            catch (IllegalArgumentException e)
            {
               //
            }
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
