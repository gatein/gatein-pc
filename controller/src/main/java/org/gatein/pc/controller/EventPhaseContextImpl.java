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

import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.apache.log4j.Logger;

import java.util.LinkedList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
class EventPhaseContextImpl implements EventPhaseContext
{

   /** . */
   static final int READ_MODE = 0;

   /** . */
   static final int READ_WRITE_MODE = 1;

   /** . */
   static final int INTERRUPTED_MODE = 2;

   /** . */
   private final Logger log;

   /** . */
   LinkedList<EventProduction> producedEvents;

   /** . */
   LinkedList<PortletWindowEvent> toConsumeEvents;

   /** . */
   int consumedEventSize;

   /** . */
   int producedEventSize;

   /** . */
   int mode;

   EventPhaseContextImpl(Logger log)
   {
      this.log = log;
      this.producedEvents = new LinkedList<EventProduction>();
      this.toConsumeEvents = new LinkedList<PortletWindowEvent>();
      this.consumedEventSize = 0;
      this.producedEventSize = 0;
      this.mode = READ_MODE;
   }

   public void queueEvent(PortletWindowEvent event)
   {
      if (mode == INTERRUPTED_MODE)
      {
         throw new IllegalStateException("The event phase cannot queue events because it is interruped");
      }
      if (mode == READ_MODE)
      {
         throw new IllegalStateException("The event phase cannot queue events");
      }
      if (event == null)
      {
         throw new IllegalArgumentException("No null event accepted");
      }

      //
      log.trace("Queued event " + event + " in the session");

      //
      this.toConsumeEvents.addLast(event);
   }

   public void interrupt()
   {
      if (mode == READ_MODE)
      {
         throw new IllegalStateException("The event phase is not interruptable");
      }

      //
      log.trace("Event delivery interruped");

      //
      this.mode = INTERRUPTED_MODE;
   }
}
