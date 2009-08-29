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
package org.gatein.pc.mc;

import org.apache.log4j.Logger;
import org.gatein.pc.portlet.impl.info.ContainerInfoBuilder;
import org.gatein.pc.portlet.impl.info.ContainerInfoBuilderContext;
import org.gatein.pc.portlet.impl.info.ContainerPortletInfo;
import org.gatein.pc.portlet.impl.info.ContainerFilterInfo;
import org.gatein.pc.portlet.impl.jsr168.ContainerInfoBuilderContextImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletApplicationImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletFilterImpl;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.container.PortletApplicationLifeCycle;
import org.gatein.pc.portlet.impl.container.PortletFilterLifeCycle;
import org.gatein.pc.portlet.impl.container.PortletContainerLifeCycle;
import org.gatein.pc.portlet.container.object.PortletApplicationObject;
import org.gatein.pc.portlet.container.object.PortletContainerObject;
import org.gatein.pc.portlet.container.object.PortletFilterObject;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.wci.WebApp;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletApplicationDeployment
{

   /** . */
   private final Logger log;

   /** . */
   private final WebApp webApp;

   /** . */
   private final PortletApplication10MetaData metaData;

   /** . */
   private final ManagedObjectRegistryEventListener listener;

   /** . */
   private PortletApplicationLifeCycle portletApplicationLifeCycle;

   public PortletApplicationDeployment(
      ManagedObjectRegistryEventListener listener,
      WebApp webApp,
      PortletApplication10MetaData metaData)
   {
      this.listener = listener;
      this.webApp = webApp;
      this.metaData = metaData;
      this.log = Logger.getLogger(PortletApplicationDeployment.class + "." + webApp.getContextPath().replace('.', '_'));
   }

   public PortletApplicationLifeCycle getPortletApplicationLifeCycle()
   {
      return portletApplicationLifeCycle;
   }

   void install()
   {

      log.debug("Starting installation");

      //
      ContainerInfoBuilderContext builderContext = new ContainerInfoBuilderContextImpl(metaData, webApp);
      ContainerInfoBuilder builder = new ContainerInfoBuilder(webApp.getContextPath(), metaData, builderContext);
      builder.build();

      //
      PortletApplicationObject portletApplicationObject = new PortletApplicationImpl(builder.getApplication());
      PortletApplicationContextImpl portletApplicationContext = new PortletApplicationContextImpl(webApp);


      //
      portletApplicationLifeCycle = new PortletApplicationLifeCycle(
         listener,
         portletApplicationContext,
         portletApplicationObject);

      // Needed for the managed callbacks
      portletApplicationContext.managedPortletApplication = portletApplicationLifeCycle;

      //
      for (ContainerFilterInfo filterInfo : builder.getApplication().getFilters().values())
      {
         PortletFilterObject portletFilterObject = new PortletFilterImpl(filterInfo);
         PortletFilterContextImpl portletFilterContext = new PortletFilterContextImpl();

         //
         PortletFilterLifeCycle portletFilterLifeCycle = portletApplicationLifeCycle.addPortletFilter(portletFilterContext, portletFilterObject);

         // Needed for the managed callbacks
         portletFilterContext.managedPortletFilter = portletFilterLifeCycle;
      }

      //
      for (ContainerPortletInfo containerInfo : builder.getPortlets())
      {
         PortletContainerObject portletContainerObject = new PortletContainerImpl(containerInfo);
         PortletContainerContextImpl portletContainerContext = new PortletContainerContextImpl();
         
         //
         PortletContainerLifeCycle portletContainerLifeCycle = portletApplicationLifeCycle.addPortletContainer(portletContainerContext, portletContainerObject);

         // Needed for the managed callbacks
         portletContainerContext.managedPortletContainer = portletContainerLifeCycle;

         // Now create deps
         for (String filterRef : containerInfo.getFilterRefs())
         {
            PortletFilterLifeCycle portletFilterLifeCycle = portletApplicationLifeCycle.getManagedPortletFilter(filterRef);

            //
            if (portletFilterLifeCycle != null)
            {
               portletApplicationLifeCycle.addDependency(portletFilterLifeCycle, portletContainerLifeCycle);
            }
            else
            {
               // todo
            }
         }
      }

      //
      portletApplicationLifeCycle.create();

      //
      portletApplicationLifeCycle.managedStart();

      //

   }

   void uninstall()
   {
      log.debug("Uninstalling");

      //
      portletApplicationLifeCycle.managedStop();

      //
      log.debug("Uninstalled");
   }
}
