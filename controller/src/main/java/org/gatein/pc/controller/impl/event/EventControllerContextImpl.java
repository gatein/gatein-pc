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
package org.gatein.pc.controller.impl.event;

import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.api.info.PortletInfo;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventControllerContextImpl implements EventControllerContext
{

   /** . */
   private PortletInvoker invoker;

   public EventControllerContextImpl(PortletInvoker invoker)
   {
      this.invoker = invoker;
   }

   public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent)
   {
      try
      {
         for (Portlet portlet : invoker.getPortlets())
         {
            PortletInfo portletInfo = portlet.getInfo();
            if (portletInfo.getEventing().getConsumedEvents().containsKey(producedEvent.getName()))
            {
               PortletWindowEvent distributedEvent = new PortletWindowEvent(producedEvent.getName(), producedEvent.getPayload(), portlet.getContext().getId());
               context.queueEvent(distributedEvent);
            }
         }
      }
      catch (PortletInvokerException e)
      {
         System.out.println("e = " + e);
         context.interrupt();
      }
   }

   public void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse)
   {
   }

   public void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable)
   {
   }

   public void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause)
   {
   }
}
