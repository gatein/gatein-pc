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
package org.gatein.pc.portlet.impl.container;

import org.gatein.pc.portlet.container.object.PortletFilterObject;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedPortletFilter;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.PortletFilterContext;
import org.gatein.pc.portlet.container.PortletFilter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletFilterLifeCycle extends LifeCycle implements ManagedPortletFilter
{

   /** . */
   private PortletApplicationLifeCycle portletApplicationLifeCycle;

   /** . */
   final PortletFilterContext portletFilterContext;

   /** . */
   final PortletFilterObject portletFilter;

   PortletFilterLifeCycle(
      PortletApplicationLifeCycle portletApplicationLifeCycle,
      PortletFilterContext portletFilterContext,
      PortletFilterObject portletFilter)
   {
      this.portletApplicationLifeCycle = portletApplicationLifeCycle;
      this.portletFilterContext = portletFilterContext;
      this.portletFilter = portletFilter;
   }

   protected void invokeStart() throws Exception
   {
      if (portletApplicationLifeCycle.getStatus() != LifeCycleStatus.STARTED)
      {
         throw new DependencyNotResolvedException("The parent application is not started");
      }

      //
      portletFilter.start();
   }

   protected void startDependents()
   {
      // Dependent containers may or not start (for instance if a container depends on several filters)
      for (PortletContainerLifeCycle portletContainerLifeCycle : portletApplicationLifeCycle.getDependencies(this))
      {
         portletContainerLifeCycle.managedStart();
      }
   }

   protected void stopDependents()
   {
      for (PortletContainerLifeCycle portletContainerLifeCycle : portletApplicationLifeCycle.getDependencies(this))
      {
         portletContainerLifeCycle.managedStop();
      }
   }

   protected void invokeStop()
   {
      portletFilter.stop();
   }

   public String getId()
   {
      return portletFilter.getId();
   }

   public PortletApplicationLifeCycle getManagedPortletApplication()
   {
      return portletApplicationLifeCycle;
   }

   public String toString()
   {
      return "PortletFilterLifeCycle[" + portletFilter.getId() + "]";
   }

   public PortletFilter getPortletFilter()
   {
      return portletFilter;
   }

   protected ManagedObjectRegistryEventListener getListener()
   {
      return portletApplicationLifeCycle.getListener();
   }
}
