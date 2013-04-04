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

import org.gatein.pc.portlet.container.object.PortletContainerObject;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.PortletContainerContext;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.api.info.PortletInfo;

import javax.portlet.Portlet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletContainerLifeCycle extends LifeCycle implements ManagedPortletContainer
{

   /** . */
   private PortletApplicationLifeCycle portletApplicationLifeCycle;

   /** . */
   final PortletContainerContext portletContainerContext;

   /** . */
   final PortletContainerObject portletContainer;

   PortletContainerLifeCycle(
      PortletApplicationLifeCycle portletApplicationLifeCycle,
      PortletContainerContext portletContainerContext,
      PortletContainerObject portletContainer)
   {
      this.portletApplicationLifeCycle = portletApplicationLifeCycle;
      this.portletContainerContext = portletContainerContext;
      this.portletContainer = portletContainer;
   }

   @Override
   protected void invokeCreate() throws Exception
   {
      if (portletApplicationLifeCycle.getStatus().getStage() < LifeCycleStatus.CREATED.getStage())
      {
         throw new DependencyNotResolvedException("The parent application is not created");
      }

      //
      for (PortletFilterLifeCycle portletFilterLifeCycle : portletApplicationLifeCycle.getDependencies(this))
      {
         if (portletFilterLifeCycle.getStatus().getStage() < LifeCycleStatus.CREATED.getStage())
         {
            throw new DependencyNotResolvedException("The filter " + portletFilterLifeCycle + " is not created");
         }
      }

      //
      portletContainer.create();
   }

   protected void invokeStart() throws Exception
   {
      if (portletApplicationLifeCycle.getStatus().getStage() < LifeCycleStatus.STARTED.getStage())
      {
         throw new DependencyNotResolvedException("The parent application is not started");
      }

      //
      for (PortletFilterLifeCycle portletFilterLifeCycle : portletApplicationLifeCycle.getDependencies(this))
      {
         if (portletFilterLifeCycle.getStatus().getStage() < LifeCycleStatus.STARTED.getStage())
         {
            throw new DependencyNotResolvedException("The filter " + portletFilterLifeCycle + " is not started");
         }
      }

      //
      portletContainer.start();
   }

   protected void invokeStop()
   {
      portletContainer.stop();
   }

   @Override
   public Portlet getPortletInstance()
   {
      return portletContainer.getPortletInstance();
   }

   @Override
   protected void invokeDestroy() throws Exception
   {
      portletContainer.destroy();
   }

   public String getId()
   {
      return portletContainer.getId();
   }

   public PortletInfo getInfo()
   {
      return portletContainer.getInfo();
   }

   public PortletApplicationLifeCycle getManagedPortletApplication()
   {
      return portletApplicationLifeCycle;
   }

   public String toString()
   {
      return getClass().getSimpleName() + "[" + portletContainer.getId() + "]";
   }

   public PortletContainer getPortletContainer()
   {
      return portletContainer;
   }

   protected ManagedObjectRegistryEventListener getListener()
   {
      return portletApplicationLifeCycle.getListener();
   }
}
