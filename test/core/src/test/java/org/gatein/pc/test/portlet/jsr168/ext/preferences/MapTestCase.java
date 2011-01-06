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
package org.gatein.pc.test.portlet.jsr168.ext.preferences;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertTrue;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ReadOnlyException;
import javax.portlet.PortletPreferences;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_PREFERENCES_2
   })
public class MapTestCase
{
   public MapTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      seq.bindAction(1, UTP3.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws ReadOnlyException
         {
            // Get prefs map
            PortletPreferences prefs = request.getPreferences();
            Map map = prefs.getMap();

            //
            assertEquals(2, map.size());
            assertTrue(map.containsKey("single_pref"));
            assertTrue(map.containsValue(new String[]{"single_pref_value"}));
            assertEquals(new String[]{"single_pref_value"}, (Object[])map.get("single_pref"));
            assertTrue(map.containsKey("multi_pref"));
            assertTrue(map.containsValue(new String[]{"multi_pref_value_1", "multi_pref_value_2"}));
            assertEquals(new String[]{"multi_pref_value_1", "multi_pref_value_2"}, (Object[])map.get("multi_pref"));

            // Modify prefs
            prefs.setValue("single_pref", "new_single_pref_value");
            prefs.setValues("multi_pref", new String[]{"new_multi_pref_value_1", "new_multi_pref_value_2"});

            //
            map = prefs.getMap();
            assertEquals(2, map.size());
            assertTrue(map.containsKey("single_pref"));
            assertTrue(map.containsValue(new String[]{"new_single_pref_value"}));
            assertEquals(new String[]{"new_single_pref_value"}, (Object[])map.get("single_pref"));
            assertTrue(map.containsKey("multi_pref"));
            assertTrue(map.containsValue(new String[]{"new_multi_pref_value_1", "new_multi_pref_value_2"}));
            assertEquals(new String[]{"new_multi_pref_value_1", "new_multi_pref_value_2"}, (Object[])map.get("multi_pref"));

            // Modify prefs
            prefs.setValue("single_pref", null);
            prefs.setValues("multi_pref", null);

            //
            map = prefs.getMap();
            assertEquals(2, map.size());
            assertTrue(map.containsKey("single_pref"));
            assertTrue(map.containsValue(new String[]{null}));
            assertEquals(new String[]{null}, (Object[])map.get("single_pref"));
            assertTrue(map.containsKey("multi_pref"));
            assertEquals(new String[]{null}, (Object[])map.get("multi_pref"));
         }
      });

      seq.bindAction(1, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
