/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.test.portlet.jsr168.api.portletsession;

import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.web.UTP1;

import javax.portlet.Portlet;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.util.Collections;
import java.util.Map;

import static org.gatein.pc.test.unit.Assert.assertEquals;
import static org.gatein.pc.test.unit.Assert.assertTrue;
import static org.gatein.pc.test.unit.Assert.fail;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.API286_PORTLET_SESSION_4})
public class AttributeMap
{
   public AttributeMap(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletSession session = request.getPortletSession();

            //
            Map<String, Object> portletScope = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
            Map<String, Object> applicationScope = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);

            //
            assertEquals(Collections.emptyMap(), portletScope);
            assertEquals(Collections.emptyMap(), applicationScope);

            //
            session.setAttribute("portlet_key", "portlet_value", PortletSession.PORTLET_SCOPE);
            session.setAttribute("application_key", "application_value", PortletSession.APPLICATION_SCOPE);

            //
            portletScope = session.getAttributeMap(PortletSession.PORTLET_SCOPE);
            applicationScope = session.getAttributeMap(PortletSession.APPLICATION_SCOPE);

            //
            assertEquals(1, portletScope.size());
            assertEquals("portlet_value", portletScope.get("portlet_key"));
            assertEquals(null, portletScope.get("application_key"));
            assertEquals(2, applicationScope.size());
            assertTrue(applicationScope.containsValue("portlet_value"));
            assertEquals("application_value", applicationScope.get("application_key"));

            // Check unmodifiable
            try
            {
               portletScope.remove("portlet_key");
               fail();
            }
            catch (Exception ignore)
            {
            }
            try
            {
               applicationScope.remove("application_key");
               fail();
            }
            catch (Exception ignore)
            {
            }

            //
            return new EndTestResponse();
         }
      });
   }
}
