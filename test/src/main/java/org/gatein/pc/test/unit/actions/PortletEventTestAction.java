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
package org.gatein.pc.test.unit.actions;

import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.TestAction;
import org.gatein.common.NotYetImplemented;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.Failure;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 */
public abstract class PortletEventTestAction extends TestAction
{

   public final DriverResponse execute(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
   {
      try
      {
         runWithRuntimeException(portlet, request, response, context);

         //
         return null;
      }
      catch (AssertionError t)
      {
         getLogger().error("The test case failed", t);

         //
         return new FailureResponse(Failure.createFailure(t));
      }
   }

   protected void runWithRuntimeException(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
   {
      try
      {
         run(portlet, request, response, context);
      }
      catch (Exception e)
      {
         if (e instanceof PortletException)
         {
            throw (PortletException)e;
         }
         if (e instanceof IOException)
         {
            throw (IOException)e;
         }
         throw new AssertionError(e);
      }
   }

   protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
   {
      throw new NotYetImplemented();
   }
}