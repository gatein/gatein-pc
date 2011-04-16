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
import org.gatein.pc.test.portlet.framework.UTP5;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.fail;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_93})
public class ReadOnlyPreferenceCannotBeModifiedTestCase
{
   public ReadOnlyPreferenceCannotBeModifiedTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP5.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      seq.bindAction(1, UTP5.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            // Get prefs
            PortletPreferences prefs = request.getPreferences();

            // Test the initial values are ok
            assertEquals("single_pref_value", prefs.getValue("single_pref", "other"));
            assertEquals(new String[]{"multi_pref_value_1", "multi_pref_value_2"}, prefs.getValues("multi_pref", new String[]{"other"}));

            // Try to modify
            try
            {
               prefs.setValue("single_pref", "");
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }
            try
            {
               prefs.setValues("single_pref", new String[]{""});
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }
            try
            {
               prefs.reset("single_pref");
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }
            try
            {
               prefs.setValue("multi_pref", "");
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }
            try
            {
               prefs.setValues("multi_pref", new String[]{""});
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }
            try
            {
               prefs.reset("multi_pref");
               fail();
            }
            catch (ReadOnlyException e)
            {
               // expected
            }

            // Test values have not changed
            assertEquals("single_pref_value", prefs.getValue("single_pref", "other"));
            assertEquals(new String[]{"multi_pref_value_1", "multi_pref_value_2"}, prefs.getValues("multi_pref", new String[]{"other"}));
         }
      });

      seq.bindAction(1, UTP5.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new EndTestResponse();
         }
      });
   }
}
