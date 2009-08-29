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
package org.gatein.pc.test.portlet.jsr168.api.portletpreferences;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.API286_PORTLET_PREFERENCES_3,
   Assertion.API286_PORTLET_PREFERENCES_4
   })
public class ZeroLengthArrayValueTestCase
{
   public ZeroLengthArrayValueTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletPreferences prefs = request.getPreferences();

            // With the default value coming from the portlet.xml descriptor
            String value = prefs.getValue("empty", "other");
            assertEquals("other", value);
            assertEquals(new String[]{"other"}, prefs.getValues("empty", new String[]{"other"}));

            //
            PortletURL url = response.createActionURL();
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(1, UTP2.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws ReadOnlyException, IOException, ValidatorException
         {
            PortletPreferences prefs = request.getPreferences();

            // Check it does not exist yet
            assertEquals("other", prefs.getValue("dynamic", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic", new String[]{"other"}));

            // Set the value to the empty array and check we get the other value
            prefs.setValues("dynamic", new String[0]);
            assertEquals("other", prefs.getValue("dynamic", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic", new String[]{"other"}));

            // Commit change
            prefs.store();

            // Check we still have the other value
            assertEquals("other", prefs.getValue("dynamic", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic", new String[]{"other"}));
         }
      });
      seq.bindAction(1, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletPreferences prefs = request.getPreferences();

            // Check we still have the other value
            assertEquals("other", prefs.getValue("dynamic", "other"));
            assertEquals(new String[]{"other"}, prefs.getValues("dynamic", new String[]{"other"}));

            //
            return new EndTestResponse();
         }
      });
   }
}
