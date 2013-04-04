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
public class LifeCycle1TestCase extends TestCase
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
      Throwable applicationFailure,
      int containerStarted,
      int containerStopped,
      LifeCycleStatus containerStatus,
      Throwable containerFailure,
      int filterStarted,
      int filterStopped,
      LifeCycleStatus filterStatus,
      Throwable filterFailure
      )
   {
      assertEquals(applicationStarted, application.getStarted());
      assertEquals(applicationStopped, application.getStopped());
      assertEquals(applicationStatus, applicationLC.getStatus());
      assertSame(applicationFailure, applicationLC.getFailure());
      assertEquals(containerStarted, container.getStarted());
      assertEquals(containerStopped, container.getStopped());
      assertEquals(containerStatus, containerLC.getStatus());
      assertSame(containerFailure, containerLC.getFailure());
      assertEquals(filterStarted, filter.getStarted());
      assertEquals(filterStopped, filter.getStopped());
      assertEquals(filterStatus, filterLC.getStatus());
      assertSame(filterFailure, filterLC.getFailure());
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
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(applicationLC);
      events.assertStartedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertStartedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertStartedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.STARTED, null);
      events.assertEmpty();

      //
      applicationLC.managedDestroy();

      //
      testState(
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null);
      events.assertStoppedEvent(containerLC);
      events.assertDestroyedEvent(containerLC);
      events.assertStoppedEvent(filterLC);
      events.assertDestroyedEvent(filterLC);
      events.assertStoppedEvent(applicationLC);
      events.assertDestroyedEvent(applicationLC);
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
      filterLC.managedDestroy();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null);
      events.assertStoppedEvent(containerLC);
      events.assertDestroyedEvent(containerLC);
      events.assertStoppedEvent(filterLC);
      events.assertDestroyedEvent(filterLC);
      events.assertEmpty();

      // Container does not start if its filter is stopped
      containerLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null);
      events.assertEmpty();

      // Filter start triggers container start
      filterLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         2, 1, LifeCycleStatus.STARTED, null,
         2, 1, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(filterLC);
      events.assertStartedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
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
      containerLC.managedDestroy();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 0, LifeCycleStatus.STARTED, null);
      events.assertStoppedEvent(containerLC);
      events.assertDestroyedEvent(containerLC);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         2, 1, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(containerLC);
      events.assertStartedEvent(containerLC);
      events.assertEmpty();
   }

   public void testContainerFailsOnStart()
   {
      events.clear();

      //
      ObjectSupport.Failure failure = new ObjectSupport.Failure();
      container.startCallback = failure;

      //
      applicationLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         1, 0, LifeCycleStatus.CREATED, containerLC.getFailure(),
         1, 0, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(applicationLC);
      events.assertStartedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertStartedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertFailedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedDestroy();

      //
      testState(
         1, 1, LifeCycleStatus.INITIALIZED, null,
         1, 0, LifeCycleStatus.INITIALIZED, null,
         1, 1, LifeCycleStatus.INITIALIZED, null);
      events.assertDestroyedEvent(containerLC);
      events.assertStoppedEvent(filterLC);
      events.assertDestroyedEvent(filterLC);
      events.assertStoppedEvent(applicationLC);
      events.assertDestroyedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         2, 0, LifeCycleStatus.CREATED, containerLC.getFailure(),
         2, 1, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(applicationLC);
      events.assertStartedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertStartedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertFailedEvent(containerLC);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         3, 0, LifeCycleStatus.CREATED, failure.getFailure(),
         2, 1, LifeCycleStatus.STARTED, null);
      events.assertFailedEvent(containerLC);
      events.assertEmpty();

      //
      containerLC.managedDestroy();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         3, 0, LifeCycleStatus.INITIALIZED, null,
         2, 1, LifeCycleStatus.STARTED, null);
      events.assertDestroyedEvent(containerLC);
      events.assertEmpty();

      //
      filterLC.managedDestroy();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         3, 0, LifeCycleStatus.INITIALIZED, null,
         2, 2, LifeCycleStatus.INITIALIZED, null);
      events.assertStoppedEvent(filterLC);
      events.assertDestroyedEvent(filterLC);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         4, 0, LifeCycleStatus.CREATED, failure.getFailure(),
         3, 2, LifeCycleStatus.STARTED, null);
      events.assertCreatedEvent(filterLC);
      events.assertStartedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertFailedEvent(containerLC);
      events.assertEmpty();
   }

   public void testApplicationFailsOnStart()
   {
      events.clear();

      //
      ObjectSupport.Failure failure = new ObjectSupport.Failure();
      application.startCallback = failure;

      //
      applicationLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.CREATED, applicationLC.getFailure(),
         0, 0, LifeCycleStatus.CREATED, null,
         0, 0, LifeCycleStatus.CREATED, null);
      events.assertCreatedEvent(applicationLC);
      events.assertFailedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(
         2, 0, LifeCycleStatus.CREATED, failure.getFailure(),
         0, 0, LifeCycleStatus.CREATED, null,
         0, 0, LifeCycleStatus.CREATED, null);
      events.assertFailedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedDestroy();

      //
      testState(
         2, 0, LifeCycleStatus.INITIALIZED, null,
         0, 0, LifeCycleStatus.INITIALIZED, null,
         0, 0, LifeCycleStatus.INITIALIZED, null);
      events.assertDestroyedEvent(containerLC);
      events.assertDestroyedEvent(filterLC);
      events.assertDestroyedEvent(applicationLC);
      events.assertEmpty();
   }

   public void testFilterFailsOnStart()
   {
      events.clear();

      //
      ObjectSupport.Failure failure = new ObjectSupport.Failure();
      filter.startCallback = failure;

      //
      applicationLC.managedStart();

      //
      testState(
         1, 0, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.CREATED, null,
         1, 0, LifeCycleStatus.CREATED, filterLC.getFailure());
      events.assertCreatedEvent(applicationLC);
      events.assertStartedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertFailedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertEmpty();

      //
      applicationLC.managedDestroy();

      //
      testState(
         1, 1, LifeCycleStatus.INITIALIZED, null,
         0, 0, LifeCycleStatus.INITIALIZED, null,
         1, 0, LifeCycleStatus.INITIALIZED, null);
      events.assertDestroyedEvent(containerLC);
      events.assertDestroyedEvent(filterLC);
      events.assertStoppedEvent(applicationLC);
      events.assertDestroyedEvent(applicationLC);
      events.assertEmpty();

      //
      applicationLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.CREATED, null,
         2, 0, LifeCycleStatus.CREATED, failure.getFailure());
      events.assertCreatedEvent(applicationLC);
      events.assertStartedEvent(applicationLC);
      events.assertCreatedEvent(filterLC);
      events.assertFailedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.CREATED, null,
         3, 0, LifeCycleStatus.CREATED, failure.getFailure());
      events.assertFailedEvent(filterLC);
      events.assertEmpty();

      //
      filterLC.managedDestroy();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.INITIALIZED, null,
         3, 0, LifeCycleStatus.INITIALIZED, null);
      events.assertDestroyedEvent(containerLC);
      events.assertDestroyedEvent(filterLC);
      events.assertEmpty();

      //
      filterLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.CREATED, null,
         4, 0, LifeCycleStatus.CREATED, failure.getFailure());
      events.assertCreatedEvent(filterLC);
      events.assertFailedEvent(filterLC);
      events.assertCreatedEvent(containerLC);
      events.assertEmpty();

      //
      containerLC.managedStart();

      //
      testState(
         2, 1, LifeCycleStatus.STARTED, null,
         0, 0, LifeCycleStatus.CREATED, null,
         4, 0, LifeCycleStatus.CREATED, failure.getFailure());
      events.assertEmpty();
   }

   public void testContainerFailsOnStop()
   {
      container.stopCallback = new ObjectSupport.Failure();

      //
      testApplicationLifeCycle();
   }

   public void testApplicationFailsOnStop()
   {
      application.stopCallback = new ObjectSupport.Failure();

      //
      testApplicationLifeCycle();
   }

   public void testFilterFailsOnStop()
   {
      filter.stopCallback = new ObjectSupport.Failure();

      //
      testApplicationLifeCycle();
   }
}
