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
package org.gatein.pc.controller;

import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.apache.log4j.Logger;

/**
 * An help class to catch and log exceptions thrown by an event controller context.
 *
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
class EventControllerContextSafeInvoker
{

   /** . */
   private final Logger log = Logger.getLogger(EventControllerContextSafeInvoker.class);

   public boolean eventProduced(EventControllerContext controllerContext, EventPhaseContext phaseContext, PortletWindowEvent sourceEvent, PortletWindowEvent producedEvent)
   {
      try
      {
         controllerContext.eventProduced(phaseContext, producedEvent, sourceEvent);

         //
         return true;
      }
      catch (Exception e)
      {
         log.error("Cannot deliver produced event " + producedEvent + " because the event " +
            "controller context threw a runtime exception", e);

         //
         return false;
      }
   }

   public boolean eventConsumed(EventControllerContext controllerContext, EventPhaseContext phaseContext, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse)
   {
      try
      {
         controllerContext.eventConsumed(phaseContext, consumedEvent, consumerResponse);

         //
         return true;
      }
      catch (Exception e)
      {
         log.error("Event consumed callback threw an exception that is ignored by the controller", e);

         //
         return false;
      }
   }

   public boolean eventFailed(EventControllerContext controllerContext, EventPhaseContext phaseContext, PortletWindowEvent failedEvent, Throwable throwable)
   {
      try
      {
         controllerContext.eventFailed(phaseContext, failedEvent, throwable);

         //
         return true;
      }
      catch (Exception e1)
      {
         log.error("Event delivery failed callback threw an exception that is ignored by the controller", e1);

         //
         return false;
      }
   }

   public boolean eventDiscarded(EventControllerContext controllerContext, EventPhaseContext phaseContext, PortletWindowEvent discardedEvent, int cause)
   {
      try
      {
         controllerContext.eventDiscarded(phaseContext, discardedEvent, cause);

         //
         return true;
      }
      catch (Exception e1)
      {
         log.error("Event delivery failed callback threw an exception that is ignored by the controller", e1);

         //
         return false;
      }
   }
}
