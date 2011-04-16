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
package org.gatein.pc.portlet.container;

import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEvent;
import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.container.managed.ManagedObjectAddedEvent;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedObjectLifeCycleEvent;

import java.util.LinkedList;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ManagedObjectRegistryEventList implements ManagedObjectRegistryEventListener
{

   /** . */
   final LinkedList<ManagedObjectRegistryEvent> list = new LinkedList<ManagedObjectRegistryEvent>();

   public void onEvent(ManagedObjectRegistryEvent event)
   {
      list.add(event);
   }

   public void assertAddedEvent(ManagedObject managedObject)
   {
      ManagedObjectAddedEvent event = nextEvent(ManagedObjectAddedEvent.class);
      assertSame(managedObject, event.getManagedObject());
   }

   public void assertStartedEvent(ManagedObject managedObject)
   {
      assertLifeCycleEvent(managedObject, LifeCycleStatus.STARTED);
   }

   public void assertStoppedEvent(ManagedObject managedObject)
   {
      assertLifeCycleEvent(managedObject, LifeCycleStatus.STOPPED);
   }

   public void assertFailedEvent(ManagedObject managedObject)
   {
      assertLifeCycleEvent(managedObject, LifeCycleStatus.FAILED);
   }

   public void assertLifeCycleEvent(ManagedObject managedObject, LifeCycleStatus status)
   {
      ManagedObjectLifeCycleEvent event = nextEvent(ManagedObjectLifeCycleEvent.class);
      assertSame(managedObject, event.getManagedObject());
      assertEquals(status, event.getStatus());
   }

   public void assertEmpty()
   {
      assertTrue(list.isEmpty());
   }

   public void clear()
   {
      list.clear();
   }

   private <T extends ManagedObjectRegistryEvent> T nextEvent(Class<T> type)
   {
      assertFalse(list.isEmpty());
      return assertInstanceOf(list.removeFirst(), type);
   }
}
