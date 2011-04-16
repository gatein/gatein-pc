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
package org.gatein.pc.test.portlet.jsr168.ext.portletmode;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertFalse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.PortletMode;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_PORTLET_MODE_3,
   Assertion.EXT_PORTLET_MODE_4,
   Assertion.EXT_PORTLET_MODE_5,
   Assertion.EXT_PORTLET_MODE_6
   })
public class TestDuringRenderTestCase
{
   public TestDuringRenderTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletModeException
         {
            // Test null returns false
            assertFalse(request.isPortletModeAllowed(null));

            // Test that we can use set the portlet mode on render URL
            // before having set the content type on the response
            PortletURL url1 = response.createRenderURL();
            url1.setPortletMode(PortletMode.VIEW);

            // Test we can set null portlet mode
            url1.setPortletMode(null);

            // Test that we can use set the portlet mode on action URL
            // before having set the content type on the response
            PortletURL url2 = response.createActionURL();
            url2.setPortletMode(PortletMode.VIEW);

            // Test we can set null portlet mode
            url2.setPortletMode(null);
            return new EndTestResponse();
         }
      });
   }
}
