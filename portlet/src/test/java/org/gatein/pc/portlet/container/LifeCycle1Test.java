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

import junit.framework.TestCase;
import org.gatein.pc.portlet.impl.container.PortletApplicationLifeCycle;
import org.gatein.pc.portlet.impl.container.PortletFilterLifeCycle;
import org.gatein.pc.portlet.impl.container.PortletContainerLifeCycle;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class LifeCycle1Test extends TestCase
{

   ManagedObjectRegistryEventList events;

   PortletApplicationObjectSupport application;
   PortletApplicationLifeCycle applicationLC;

   PortletFilterObjectSupport filter;
   PortletContainerObjectSupport container;
   PortletFilterLifeCycle filterLC;
   PortletContainerLifeCycle containerLC;

   @Override
   protected void setUp() throws Exception
   {
      events = new ManagedObjectRegistryEventList();

      application = new PortletApplicationObjectSupport("application");
      applicationLC = new PortletApplicationLifeCycle(events, new PortletApplicationContextSupport(), application);

      filter = new PortletFilterObjectSupport("filter2");
      container = new PortletContainerObjectSupport("container2");
      filterLC = applicationLC.addPortletFilter(new PortletFilterContextSupport(), filter);
      containerLC = applicationLC.addPortletContainer(new PortletContainerContextSupport(), container);
      applicationLC.addDependency(filterLC, containerLC);

      applicationLC.create();
   }

   private void testState(
      int applicationStarted,
      int applicationStopped,
      LifeCycleStatus applicationStatus,
      int containerStarted,
      int containerStopped,
      LifeCycleStatus containerStatus,
      int filterStarted,
      int filterStopped,
      LifeCycleStatus filterStatus
   )
   {
      assertEquals(applicationStarted, application.getStarted());
      assertEquals(applicationStopped, application.getStopped());
      assertEquals(applicationStatus, applicationLC.getStatus());
      assertEquals(containerStarted, container.getStarted());
      assertEquals(containerStopped, container.getStopped());
      assertEquals(containerStatus, containerLC.getStatus());
      assertEquals(filterStarted, filter.getStarted());
      assertEquals(filterStopped, filter.getStopped());
      assertEquals(filterStatus, filterLC.getStatus());
   }

   public void testWiring()
   {
      events.assertAddedEvent(filterLC);
      events.assertAddedEvent(containerLC);
      events.assertEmpty();

      assertEquals(1, application.containers.size());
      assertSame(container, application.containers.get(container.getId()));
      assertEquals(1, application.filters.size());
      assertSame(filter, application.filters.get(filter.getId()));

      //
      assertSame(application, filter.application);

      //
      assertSame(application, container.application);
      assertEquals(1, container.filters.size());
      assertSame(filter, container.filters.get(filter.getId()));
   }

   public void testApplicationLifeCycle()
   {
      events.clear();

      //
      applicationLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.STARTED);
      events.assertStartedEvent(applicationLC);
      events.assertStartedEvent(filterLC);
      events.assertStartedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.STARTED);
      events.assertEmpty();

      //
      applicationLC.managedStop();

      //
      testState(1, 1, LifeCycleStatus.STOPPED, 1, 1, LifeCycleStatus.STOPPED, 1, 1, LifeCycleStatus.STOPPED);
      events.assertStoppedEvent(containerLC);
      events.assertStoppedEvent(filterLC);
      events.assertStoppedEvent(applicationLC);
      events.assertEmpty();
   }

   public void testFilterLifeCycle()
   {
      events.clear();

      //
      applicationLC.managedStart();

      //
      events.clear();

      // Filter stop triggers
      filterLC.managedStop();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 1, LifeCycleStatus.STOPPED, 1, 1, LifeCycleStatus.STOPPED);
      events.assertStoppedEvent(containerLC);
      events.assertStoppedEvent(filterLC);
      events.assertEmpty();

      // Container does not start if its filter is stopped
      containerLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 1, LifeCycleStatus.STOPPED, 1, 1, LifeCycleStatus.STOPPED);
      events.assertEmpty();

      // Filter start triggers container start
      filterLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 2, 1, LifeCycleStatus.STARTED, 2, 1, LifeCycleStatus.STARTED);
      events.assertStartedEvent(filterLC);
      events.assertStartedEvent(containerLC);
      events.assertEmpty();
   }

   public void testContainerLifeCycle()
   {
      events.clear();

      //
      applicationLC.managedStart();

      //
      events.clear();

      //
      containerLC.managedStop();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 1, LifeCycleStatus.STOPPED, 1, 0, LifeCycleStatus.STARTED);
      events.assertStoppedEvent(containerLC);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 2, 1, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.STARTED);
      events.assertStartedEvent(containerLC);
      events.assertEmpty();
   }

   public void testContainerFailsOnStart()
   {
      events.clear();

      //
      container.startCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      applicationLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 1, 0, LifeCycleStatus.FAILED, 1, 0, LifeCycleStatus.STARTED);
      events.assertStartedEvent(applicationLC);
      events.assertStartedEvent(filterLC);
      events.assertFailedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedStop();

      //
      testState(1, 1, LifeCycleStatus.STOPPED, 1, 0, LifeCycleStatus.FAILED, 1, 1, LifeCycleStatus.STOPPED);
      events.assertStoppedEvent(filterLC);
      events.assertStoppedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 2, 0, LifeCycleStatus.FAILED, 2, 1, LifeCycleStatus.STARTED);
      events.assertStartedEvent(applicationLC);
      events.assertStartedEvent(filterLC);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 3, 0, LifeCycleStatus.FAILED, 2, 1, LifeCycleStatus.STARTED);
      events.assertEmpty();

      //
      containerLC.managedStop();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 3, 0, LifeCycleStatus.FAILED, 2, 1, LifeCycleStatus.STARTED);
      events.assertEmpty();

      //
      filterLC.managedStop();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 3, 0, LifeCycleStatus.FAILED, 2, 2, LifeCycleStatus.STOPPED);
      events.assertStoppedEvent(filterLC);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 4, 0, LifeCycleStatus.FAILED, 3, 2, LifeCycleStatus.STARTED);
      events.assertStartedEvent(filterLC);
      events.assertEmpty();
   }

   public void testApplicationFailsOnStart()
   {
      events.clear();

      //
      application.startCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      applicationLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.FAILED, 0, 0, LifeCycleStatus.STOPPED, 0, 0, LifeCycleStatus.STOPPED);
      events.assertFailedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(2, 0, LifeCycleStatus.FAILED, 0, 0, LifeCycleStatus.STOPPED, 0, 0, LifeCycleStatus.STOPPED);
      events.assertEmpty();

      //
      applicationLC.managedStop();

      //
      testState(2, 0, LifeCycleStatus.FAILED, 0, 0, LifeCycleStatus.STOPPED, 0, 0, LifeCycleStatus.STOPPED);
      events.assertEmpty();
   }

   public void testFilterFailsOnStart()
   {
      events.clear();

      //
      filter.startCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      applicationLC.managedStart();

      //
      testState(1, 0, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 1, 0, LifeCycleStatus.FAILED);
      events.assertStartedEvent(applicationLC);
      events.assertFailedEvent(filterLC);
      events.assertEmpty();

      //
      applicationLC.managedStop();

      //
      testState(1, 1, LifeCycleStatus.STOPPED, 0, 0, LifeCycleStatus.STOPPED, 1, 0, LifeCycleStatus.FAILED);
      events.assertStoppedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 2, 0, LifeCycleStatus.FAILED);
      events.assertStartedEvent(applicationLC);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 3, 0, LifeCycleStatus.FAILED);
      events.assertEmpty();

      //
      filterLC.managedStop();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 3, 0, LifeCycleStatus.FAILED);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 4, 0, LifeCycleStatus.FAILED);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(2, 1, LifeCycleStatus.STARTED, 0, 0, LifeCycleStatus.STOPPED, 4, 0, LifeCycleStatus.FAILED);
      events.assertEmpty();
   }

   public void testContainerFailsOnStop()
   {
      container.stopCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      testApplicationLifeCycle();
   }

   public void testApplicationFailsOnStop()
   {
      application.stopCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      testApplicationLifeCycle();
   }

   public void testFilterFailsOnStop()
   {
      filter.stopCallback = ObjectSupport.FAILURE_CALLBACK;

      //
      testApplicationLifeCycle();
   }
}
