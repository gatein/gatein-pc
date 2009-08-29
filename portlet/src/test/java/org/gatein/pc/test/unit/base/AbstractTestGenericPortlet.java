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
package org.gatein.pc.test.unit.base;

import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.PortletTestDriver;
import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.JoinPointType;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.Failure;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * Abstract test class for testing GenericPortlet implementation
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 */
public abstract class AbstractTestGenericPortlet extends GenericPortlet
{

   /** Joinpoint for render phase. */
   private final JoinPoint renderJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_RENDER);

   /** Joinpoint for action phase. */
   private final JoinPoint actionJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_ACTION);

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      PortletTestContext context = PortletTestDriver.getPortletTestContext();

      //
      String actorId = context.getActorId(JoinPointType.PORTLET_ACTION);

      //
      DriverResponse response = new FailureResponse(Failure.createErrorFailure(""));

      //
      if (actorId != null)
      {
         JoinPoint tmp = new JoinPoint(actorId, JoinPointType.PORTLET_ACTION);

         //
         if (tmp.equals(actionJoinPoint))
         {
            try
            {
               response = doProcessAction(req, resp, context);
            }
            catch (AssertionError e)
            {
               response = new FailureResponse(Failure.createFailure(e));
            }
         }
      }

      //
      context.updateResponse(response);
   }

   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletTestContext context = PortletTestDriver.getPortletTestContext();

      //
      String actorId = context.getActorId(JoinPointType.PORTLET_RENDER);

      //
      if (actorId != null)
      {
         JoinPoint tmp = new JoinPoint(actorId, JoinPointType.PORTLET_RENDER);

         //
         if (tmp.equals(renderJoinPoint))
         {
            DriverResponse response;
            try
            {
               preRender(req, resp, context);
               super.render(req, resp);
               response = postRender(req, resp, context);
            }
            catch (AssertionError e)
            {
               response = new FailureResponse(Failure.createFailure(e));
            }

            //
            if (response != null)
            {
               context.updateResponse(response);
            }
         }
      }
   }

   protected DriverResponse doProcessAction(ActionRequest req, ActionResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      throw new PortletException();
   }

   /**
    * Invoked by render() method if current test is running. After this method GenericPortlet.render() will be called.
    *
    * @param req
    * @param resp
    * @param context
    * @throws PortletException
    * @throws PortletSecurityException
    * @throws IOException
    */
   protected void preRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      throw new PortletException();
   }

   /**
    * Invoked by render() method after GenericPortlet.render() invocation.
    *
    * @param req
    * @param resp
    * @param context
    * @throws PortletException
    * @throws PortletSecurityException
    * @throws IOException
    */
   protected DriverResponse postRender(RenderRequest req, RenderResponse resp, PortletTestContext context) throws PortletException, IOException
   {
      return null;
   }
}
