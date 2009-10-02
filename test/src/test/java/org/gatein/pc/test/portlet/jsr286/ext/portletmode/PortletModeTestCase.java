/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.test.portlet.jsr286.ext.portletmode;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.common.util.Tools;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import java.io.IOException;
import java.util.List;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.EXT_PORTLET_MODE_7})
public class PortletModeTestCase
{

   /** . */
   private static final PortletMode CUSTOM_PORTAL = new PortletMode("CUSTOM_PORTAL");

   /** . */
   private static final PortletMode CUSTOM_PORTLET = new PortletMode("CUSTOM_PORTLET");

   /** . */
   private static final PortletMode NON_EXISTING = new PortletMode("NON_EXISTING");

   /** . */
   private static final List<PortletMode> legalModes = Tools.toList(PortletMode.VIEW, PortletMode.HELP, CUSTOM_PORTLET);

   /** . */
   private static final List<PortletMode> illegalModes = Tools.toList(PortletMode.EDIT, CUSTOM_PORTAL, NON_EXISTING);

   public PortletModeTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(PortletMode.VIEW, request.getPortletMode());

            //
            testRequest(request, legalModes, illegalModes);

            //
            testURL(response.createActionURL(), legalModes, illegalModes);
            testURL(response.createRenderURL(), legalModes, illegalModes);

            //
            PortletURL url = response.createActionURL();
            url.setPortletMode(PortletMode.HELP);
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(PortletMode.HELP, request.getPortletMode());

            //
            testRequest(request, legalModes, illegalModes);

            //
            testResponse(response, legalModes, illegalModes);

            //
            response.setPortletMode(PortletMode.HELP);
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(PortletMode.HELP, request.getPortletMode());

            //
            testRequest(request, legalModes, illegalModes);

            //
            testURL(response.createActionURL(), legalModes, illegalModes);
            testURL(response.createRenderURL(), legalModes, illegalModes);

            //
            PortletURL url = response.createActionURL();
            url.setPortletMode(CUSTOM_PORTLET);
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(2, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(CUSTOM_PORTLET, request.getPortletMode());

            //
            testRequest(request, legalModes, illegalModes);

            //
            testResponse(response, legalModes, illegalModes);

            //
            response.setPortletMode(CUSTOM_PORTLET);
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(CUSTOM_PORTLET, request.getPortletMode());

            //
            testRequest(request, legalModes, illegalModes);

            //
            testURL(response.createActionURL(), legalModes, illegalModes);
            testURL(response.createRenderURL(), legalModes, illegalModes);

            //
            return new EndTestResponse();
         }
      });
   }

   private void testURL(
      PortletURL url,
      List<PortletMode> legalModes,
      List<PortletMode> illegalModes)
   {
      for (PortletMode mode : legalModes)
      {
         try
         {
            url.setPortletMode(mode);
         }
         catch (PortletModeException expected)
         {
            fail("Was not expecting a portlet mode exception");
         }
      }
      for (PortletMode mode : illegalModes)
      {
         try
         {
            url.setPortletMode(mode);
            fail("Was expecting a portlet mode exception");
         }
         catch (PortletModeException expected)
         {
         }
      }
   }

   private void testResponse(
      ActionResponse response,
      List<PortletMode> legalModes,
      List<PortletMode> illegalModes)
   {
      for (PortletMode mode : legalModes)
      {
         try
         {
            response.setPortletMode(mode);
         }
         catch (PortletModeException expected)
         {
            fail("Was not expecting a portlet mode exception");
         }
      }
      for (PortletMode mode : illegalModes)
      {
         try
         {
            response.setPortletMode(mode);
            fail("Was expecting a portlet mode exception");
         }
         catch (PortletModeException expected)
         {
         }
      }
   }

   private void testRequest(
      PortletRequest url,
      List<PortletMode> legalModes,
      List<PortletMode> illegalModes)
   {
      for (PortletMode mode : legalModes)
      {
         assertTrue(url.isPortletModeAllowed(mode));
      }
      for (PortletMode mode : illegalModes)
      {
         assertFalse(url.isPortletModeAllowed(mode));
      }
   }
}
