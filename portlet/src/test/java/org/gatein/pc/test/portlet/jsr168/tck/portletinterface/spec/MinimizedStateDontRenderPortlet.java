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
package org.gatein.pc.test.portlet.jsr168.tck.portletinterface.spec;

import org.gatein.pc.test.unit.base.AbstractTestGenericPortlet;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.annotations.TestActor;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.Failure;

import static org.jboss.unit.api.Assert.*;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import java.io.IOException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 * @portlet.specification assert="SPEC:16 - If the window state of the portlet (see PLT.9 Window States Chapter) is
 * MINIMIZED, the render method of the GenericPortlet does not invoke any of the portlet mode rendering methods"
 */
@TestActor(id=MinimizedStateDontRenderPortlet.NAME)
public class MinimizedStateDontRenderPortlet extends AbstractTestGenericPortlet
{

   /** . */
   public static final String NAME = "MinimizedStateDontRenderPortlet";

   /** . */
   private String rendered;

   protected void doView(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      // Shouldn't be here
      rendered = "doView";
   }

   protected void doEdit(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      // Shouldn't be here
      rendered = "doEdit";
   }

   protected void doHelp(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      // Shouldn't be here
      rendered = "doHelp";
   }

   protected void preRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      rendered = null;
   }

   protected DriverResponse postRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      switch(context.getRequestCount())
      {
         case 0:
         {
            // Invoking VIEW mode
            PortletURL url = resp.createRenderURL();
            url.setPortletMode(PortletMode.VIEW);
            url.setWindowState(WindowState.MINIMIZED);
            return new InvokeGetResponse(url.toString());
         }
         case 1:
         {
            assertNull(rendered);

            // Invoking EDIT mode
            PortletURL url = resp.createRenderURL();
            url.setPortletMode(PortletMode.EDIT);
            url.setWindowState(WindowState.MINIMIZED);
            return new InvokeGetResponse(url.toString());
         }
         case 2:
         {
            assertNull(rendered);

            // Invoking HELP mode
            PortletURL url = resp.createRenderURL();
            url.setPortletMode(PortletMode.HELP);
            url.setWindowState(WindowState.MINIMIZED);
            return new InvokeGetResponse(url.toString());
         }
         case 3:
         {
            assertNull(rendered);

            //
            return new EndTestResponse();
         }
         default:
            return new FailureResponse(Failure.createAssertionFailure(""));
      }
   }
}
