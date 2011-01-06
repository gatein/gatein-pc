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
package org.gatein.pc.test.portlet.jsr168.tck.portletmode.spec;

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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import java.io.IOException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 * @portlet.specification assert="SPEC:36 - ."
 */
@TestActor(id=RequestDispatchingDependingOnModePortlet.NAME)
public class RequestDispatchingDependingOnModePortlet extends AbstractTestGenericPortlet
{

   /** . */
   public static final String NAME = "RequestDispatchingDependingOnModePortlet";

   /** . */
   private String methodCall;

   /** . */
   private PortletTestContext context;

   protected void doView(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      if (context.getRequestCount() == 1)
      {
         methodCall = "doView";
      }
   }

   protected void doEdit(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      if (context.getRequestCount() == 2)
      {
         methodCall = "doEdit";
      }
   }

   protected void doHelp(final RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      if (context.getRequestCount() == 3)
      {
         methodCall = "doHelp";
      }
   }

   protected void preRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      this.context = context;
      this.methodCall = null;
   }

   protected DriverResponse postRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      try
      {
         switch(context.getRequestCount())
         {
            case 0:
            {
               // Invoking VIEW mode
               PortletURL url = resp.createRenderURL();
               url.setPortletMode(PortletMode.VIEW);
               return new InvokeGetResponse(url.toString());
            }
            case 1:
            {
               assertEquals("doView", methodCall);

               // Invoking EDIT mode
               PortletURL url = resp.createRenderURL();
               url.setPortletMode(PortletMode.EDIT);
               return new InvokeGetResponse(url.toString());
            }
            case 2:
            {
               assertEquals("doEdit", methodCall);

               // Invoking HELP mode
               PortletURL url = resp.createRenderURL();
               url.setPortletMode(PortletMode.HELP);
               return new InvokeGetResponse(url.toString());
            }
            case 3:
            {
               assertEquals("doHelp", methodCall);

               //
               return new EndTestResponse();
            }
            default:
               return new FailureResponse(Failure.createAssertionFailure(""));
         }
      }
      finally
      {
         this.context = null;
         this.methodCall = null;
      }
   }
}

