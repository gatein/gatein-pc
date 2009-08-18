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
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;

import static org.jboss.unit.api.Assert.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class WiringEventControllerContext implements EventControllerContext
{

   /** . */
   private final Map<Coordinate, List<Coordinate>> wires = new HashMap<Coordinate, List<Coordinate>>();

   /** . */
   private final List<EventCallback> callbacks = new LinkedList<EventCallback>();

   public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent)
   {
      List<Coordinate> dsts = wires.get(new Coordinate(producedEvent.getName(), producedEvent.getWindowId()));

      //
      if (dsts != null)
      {
         for (Coordinate dst : dsts)
         {
            context.queueEvent(new PortletWindowEvent(dst.name, producedEvent.getPayload(), dst.windowId));
         }
      }
   }

   public void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse)
   {
      callbacks.add(new EventCallback(EventCallback.EVENT_CONSUMED, consumedEvent, consumerResponse));
   }

   public void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable)
   {
      callbacks.add(new EventCallback(EventCallback.EVENT_FAILED, failedEvent, throwable));
   }

   public void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause)
   {
      callbacks.add(new EventCallback(EventCallback.EVENT_DISCARDED, discardedEvent, cause));
   }

   public void createWire(QName srcName, String srcWindowId, QName dstName, String dstWindowId)
   {
      Coordinate src = new Coordinate(srcName, srcWindowId);

      //
      List<Coordinate> dsts = wires.get(src);

      //
      if (dsts == null)
      {
         dsts = new ArrayList<Coordinate>();
         wires.put(src, dsts);
      }

      //
      Coordinate dst = new Coordinate(dstName, dstWindowId);

      if (dsts.contains(dst))
      {
         throw new IllegalStateException("Such a wire already exists");
      }

      //
      dsts.add(dst);
   }

   public static class EventCallback
   {

      /** . */
      public static final int EVENT_CONSUMED = 0;

      /** . */
      public static final int EVENT_FAILED = 1;

      /** . */
      public static final int EVENT_DISCARDED = 2;

      /** . */
      private final int type;

      /** . */
      private final PortletWindowEvent event;

      /** . */
      private final Object data;

      private EventCallback(int type, PortletWindowEvent event, Object data)
      {
         this.type = type;
         this.event = event;
         this.data = data;
      }

      public int getType()
      {
         return type;
      }

      public PortletWindowEvent getEvent()
      {
         return event;
      }

      public PortletInvocationResponse assertConsumed()
      {
         assertEquals(EVENT_CONSUMED, type);
         return (PortletInvocationResponse)data;
      }

      public Throwable assertFailed()
      {
         assertEquals(EVENT_FAILED, type);
         return (Throwable)data;
      }

      public int assertDiscarded()
      {
         assertEquals(EVENT_DISCARDED, type);
         return (Integer)data;
      }
   }

   private static class Coordinate
   {

      /** . */
      final QName name;

      /** . */
      final String windowId;

      private Coordinate(QName name, String windowId)
      {
         if (name == null)
         {
            throw new IllegalArgumentException();
         }
         if (windowId == null)
         {
            throw new IllegalArgumentException();
         }

         //
         this.name = name;
         this.windowId = windowId;
      }

      public boolean equals(Object obj)
      {
         if (obj == this)
         {
            return true;
         }
         if (obj instanceof Coordinate)
         {
            Coordinate that = (Coordinate)obj;
            return name.equals(that.name) && windowId.equals(that.windowId);
         }
         return false;
      }

      public int hashCode()
      {
         return name.hashCode() + windowId.hashCode();
      }
   }
}
