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

import org.gatein.common.logging.Logger;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.event.WindowEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventPhaseContext implements Iterator<WindowEvent>
{

   /** . */
   private static final EventControllerContextSafeInvoker safeInvoker = new EventControllerContextSafeInvoker();

   /** . */
   private final Logger log;

   /** . */
   LinkedList<WindowEvent> toConsumeEvents;

   /** . */
   int consumedEventSize;

   /** . */
   private int producedEventSize;

   /** . */
   final PortletController controller;

   /** . */
   final ControllerContext context;

   EventPhaseContext(PortletController controller, ControllerContext context, Logger log)
   {
      this.controller = controller;
      this.context = context;
      this.log = log;
      this.toConsumeEvents = new LinkedList<WindowEvent>();
      this.consumedEventSize = 0;
      this.producedEventSize = 0;
   }

   boolean push(WindowEvent producedEvent)
   {
      return push(null, producedEvent);
   }

   boolean push(WindowEvent consumedEvent, WindowEvent producedEvent)
   {
      String producerId = producedEvent.getWindowId();
      PortletInfo producerPortletInfo = context.getPortletInfo(producerId);

      //
      if (producerPortletInfo == null)
      {
         log.trace("Cannot deliver event " + producedEvent +" because the producer does not have portlet info");
         safeInvoker.eventDiscarded(context.getEventControllerContext(), this, producedEvent, EventControllerContext.EVENT_PRODUCER_INFO_NOT_AVAILABLE);
         return true;
      }
      else
      {
         //
         if (!controller.getDistributeNonProduceableEvents())
         {
            if (!producerPortletInfo.getEventing().getProducedEvents().containsKey(producedEvent.getName()))
            {
               log.trace("Cannot deliver event " + producedEvent +" because the producer of the event does not produce the event name");
               safeInvoker.eventDiscarded(context.getEventControllerContext(), this, producedEvent, EventControllerContext.PORTLET_DOES_NOT_CONSUME_EVENT);
               return true;
            }
         }

         // Apply produced event quota if necessary
         int producedEventThreshold = controller.getProducedEventThreshold();
         if (producedEventThreshold >= 0)
         {
            if (producedEventSize + 1 > producedEventThreshold)
            {
               log.trace("Event distribution interrupted because the maximum number of produced event is reached");
               safeInvoker.eventDiscarded(context.getEventControllerContext(), this, producedEvent, EventControllerContext.PRODUCED_EVENT_FLOODED);
               return false;
            }
         }

         //
         Iterable<WindowEvent> toConsume = safeInvoker.eventProduced(context.getEventControllerContext(), this, consumedEvent, producedEvent);

         //
         if (toConsume == null)
         {
            return false;
         }
         else
         {
            producedEventSize++;
            for (WindowEvent event : toConsume)
            {
               toConsumeEvents.add(event);
            }
            return true;
         }
      }
   }

   /** . */
   private WindowEvent next = null;

   @Override
   public boolean hasNext()
   {
      while (next == null && toConsumeEvents.size() > 0)
      {
         WindowEvent toConsumeEvent = toConsumeEvents.removeFirst();
         String consumedId = toConsumeEvent.getWindowId();

         //
         PortletInfo consumerPortletInfo = context.getPortletInfo(consumedId);
         if (consumerPortletInfo == null)
         {
            log.trace("Cannot deliver event " + toConsumeEvent +" because the consumer of the event does not have a portlet info");
            safeInvoker.eventDiscarded(context.getEventControllerContext(), this, toConsumeEvent, EventControllerContext.EVENT_CONSUMER_INFO_NOT_AVAILABLE);
            continue;
         }

         //
         if (!controller.getDistributeNonConsumableEvents())
         {
            if (!consumerPortletInfo.getEventing().getConsumedEvents().containsKey(toConsumeEvent.getName()))
            {
               log.trace("Cannot deliver event " + toConsumeEvent +" because the consumer of the event does not accept the event name");
               safeInvoker.eventDiscarded(context.getEventControllerContext(), this, toConsumeEvent, EventControllerContext.PORTLET_DOES_NOT_CONSUME_EVENT);
               continue;
            }
         }

         //
         next = toConsumeEvent;
      }

      //
      return next != null;
   }

   @Override
   public WindowEvent next()
   {
      if (!hasNext())
      {
         throw new NoSuchElementException();
      }
      WindowEvent tmp = next;
      next = null;
      return tmp;
   }

   @Override
   public void remove()
   {
      throw new UnsupportedOperationException();
   }
}
