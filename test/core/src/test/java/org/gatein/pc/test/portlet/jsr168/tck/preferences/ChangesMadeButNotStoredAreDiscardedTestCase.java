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
package org.gatein.pc.test.portlet.jsr168.tck.preferences;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ReadOnlyException;
import javax.portlet.PortletPreferences;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_96})
public class ChangesMadeButNotStoredAreDiscardedTestCase
{
   public ChangesMadeButNotStoredAreDiscardedTestCase(PortletTestCase seq)
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
            // Get prefs
            PortletPreferences prefs = request.getPreferences();

            // Check the initial value are good
            assertEquals("static_single_pref_value", prefs.getValue("static_single_pref", "other"));
            assertEquals(new String[]{"static_multi_pref_value_1", "static_multi_pref_value_2"}, prefs.getValues("static_multi_pref", new String[]{"other"}));
            assertEquals("other", prefs.getValue("dynamic_single_pref", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic_multi_pref", new String[]{"other"}));

            // Set values
            prefs.setValue("static_single_pref", "new_static_single_pref_value");
            prefs.setValues("static_multi_pref", new String[]{"new_static_multi_pref_value_1", "new_static_multi_pref_value_2"});
            prefs.setValue("dynamic_single_pref", "new_dynamic_single_pref_value");
            prefs.setValues("dynamic_multi_pref", new String[]{"new_dynamic_multi_pref_value_1", "new_dynamic_multi_pref_value_2"});

            // Check wit new values
            assertEquals("new_static_single_pref_value", prefs.getValue("static_single_pref", "other"));
            assertEquals(new String[]{"new_static_multi_pref_value_1", "new_static_multi_pref_value_2"}, prefs.getValues("static_multi_pref", new String[]{"other"}));
            assertEquals("new_dynamic_single_pref_value", prefs.getValue("dynamic_single_pref", "other"));
            assertEquals(new String[]{"new_dynamic_multi_pref_value_1", "new_dynamic_multi_pref_value_2"}, prefs.getValues("dynamic_multi_pref", new String[]{"other"}));
         }
      });

      seq.bindAction(1, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      seq.bindAction(2, UTP3.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            // Get prefs
            PortletPreferences prefs = request.getPreferences();

            // Check we have the original values back
            assertEquals("static_single_pref_value", prefs.getValue("static_single_pref", "other"));
            assertEquals(new String[]{"static_multi_pref_value_1", "static_multi_pref_value_2"}, prefs.getValues("static_multi_pref", new String[]{"other"}));
            assertEquals("other", prefs.getValue("dynamic_single_pref", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic_multi_pref", new String[]{"other"}));
         }
      });

      seq.bindAction(2, UTP3.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
