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
package org.gatein.pc.portlet.impl.deployment;

import org.gatein.common.io.IOTools;
import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.container.managed.ManagedObjectEvent;
import org.gatein.pc.portlet.container.managed.ManagedObjectLifeCycleEvent;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEvent;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventBroadcaster;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.managed.ManagedPortletApplication;
import org.gatein.pc.portlet.container.managed.PortletApplicationRegistry;
import org.gatein.pc.portlet.impl.container.PortletApplicationLifeCycle;
import org.gatein.pc.portlet.impl.container.PortletContainerLifeCycle;
import org.gatein.pc.portlet.impl.deployment.staxnav.PortletApplicationMetaDataBuilder;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletApplicationDeployer implements PortletApplicationRegistry
{

   /** . */
   private PortletApplicationRegistry registry;

   /** . */
   protected final Logger log = LoggerFactory.getLogger(PortletApplicationDeployer.class);

   /** . */
   private Map<String, PortletApplicationDeployment> deploymentMap = new HashMap<String, PortletApplicationDeployment>();

   /** . */
   private final Map<String, PortletApplicationLifeCycle> applications = new HashMap<String, PortletApplicationLifeCycle>();

   /** . */
   private ContainerPortletInvoker containerPortletInvoker;

   /** . */
   private final ManagedObjectRegistryEventBroadcaster broadcaster;

   /** . */
   private boolean schemaValidated;

   /** Bridge managed object event to add/remove portlet container in portlet container invoker. */
   private final ManagedObjectRegistryEventListener bridgeToInvoker = new ManagedObjectRegistryEventListener()
   {
      public void onEvent(ManagedObjectRegistryEvent event)
      {
         if (event instanceof ManagedObjectEvent)
         {
            ManagedObjectEvent managedObjectEvent = (ManagedObjectEvent)event;
            ManagedObject managedObject = managedObjectEvent.getManagedObject();

            //
            if (managedObject instanceof PortletContainerLifeCycle)
            {
               PortletContainerLifeCycle portletContainerLifeCycle = (PortletContainerLifeCycle)managedObject;
               PortletContainer portletContainer = portletContainerLifeCycle.getPortletContainer();

               //
               if (managedObjectEvent instanceof ManagedObjectLifeCycleEvent)
               {
                  ManagedObjectLifeCycleEvent lifeCycleEvent = (ManagedObjectLifeCycleEvent)managedObjectEvent;

                  //
                  LifeCycleStatus status = lifeCycleEvent.getStatus();

                  //
                  if (status == LifeCycleStatus.STARTED)
                  {
                     containerPortletInvoker.addPortletContainer(portletContainer);
                  }
                  else
                  {
                     containerPortletInvoker.removePortletContainer(portletContainer);
                  }
               }
            }
         }
      }
   };

   public PortletApplicationDeployer(ContainerPortletInvoker containerPortletInvoker)
   {
      broadcaster = new ManagedObjectRegistryEventBroadcaster();
      broadcaster.addListener(bridgeToInvoker);

      //
      this.containerPortletInvoker = containerPortletInvoker;
   }

   public PortletApplicationDeployer()
   {
      this(null);
   }

   public boolean isSchemaValidated()
   {
      return schemaValidated;
   }

   public void setSchemaValidated(boolean schemaValidated)
   {
      this.schemaValidated = schemaValidated;
   }

   public ContainerPortletInvoker getContainerPortletInvoker()
   {
      return containerPortletInvoker;
   }

   public void setContainerPortletInvoker(ContainerPortletInvoker containerPortletInvoker)
   {
      this.containerPortletInvoker = containerPortletInvoker;
   }

/*
   public void onEvent(WebAppEvent event)
   {
      if (event instanceof WebAppLifeCycleEvent)
      {
         WebAppLifeCycleEvent lifeCycleEvent = (WebAppLifeCycleEvent)event;
         String cp = event.getWebApp().getContextPath();
         switch (lifeCycleEvent.getType())
         {
            case WebAppLifeCycleEvent.ADDED:
               log.debug("Going to install war file" + cp);
               try
               {
                  add(event.getWebApp());
                  log.debug("Installed war file" + cp);
               }
               catch (DeploymentException e)
               {
                  log.error("Could not deploy war file " + cp, e);
               }
               break;
            case WebAppLifeCycleEvent.REMOVED:
               try
               {
                  log.debug("Going to uninstall war file" + cp);
                  remove(event.getWebApp());
                  log.debug("Uninstalled war file" + cp);
               }
               catch (Throwable e)
               {
                  log.error("Uninstalled war file " + cp + " with an error", e);
               }
               break;
         }
      }
   }
*/

   public final PortletApplicationDeployment add(ServletContext webApp) throws DeploymentException
   {
      //
      PortletApplication10MetaData metaData = buildPortletApplicationMetaData(webApp);
      if (metaData != null)
      {
         PortletApplicationDeployment deployment = createPortletApplicationDeployment(webApp, metaData);
         deploymentMap.put(webApp.getContextPath(), deployment);
         deployment.install();

         //
         PortletApplicationLifeCycle portletApplicationLifeCycle = deployment.getPortletApplicationLifeCycle();
         applications.put(portletApplicationLifeCycle.getId(), portletApplicationLifeCycle);

         //
         return deployment;
      }
      else
      {
         return null;
      }
   }

   public final void remove(ServletContext webApp)
   {
      PortletApplicationDeployment deployment = deploymentMap.remove(webApp.getContextPath());
      if (deployment != null)
      {
         PortletApplicationLifeCycle portletApplicationLifeCycle = deployment.getPortletApplicationLifeCycle();
         applications.remove(portletApplicationLifeCycle.getId());
         deployment.uninstall();
      }
   }

   protected PortletApplicationDeployment createPortletApplicationDeployment(ServletContext webApp, PortletApplication10MetaData metaData) {
       return new PortletApplicationDeployment(broadcaster, webApp, metaData);
   }

   protected PortletApplication10MetaData buildPortletApplicationMetaData(ServletContext webApp) throws DeploymentException
   {
      URL url;
      try
      {
         url = webApp.getResource("/WEB-INF/portlet.xml");
      }
      catch (MalformedURLException e)
      {
         throw new DeploymentException("Could not read portlet.xml deployment descriptor", e);
      }
      if (url != null)
      {
         InputStream in = null;
         try
         {
            in = IOTools.safeBufferedWrapper(url.openStream());
            PortletApplicationMetaDataBuilder builder = new PortletApplicationMetaDataBuilder();
            builder.setSchemaValidation(schemaValidated);
            return builder.build(in);
         }
         catch (Exception e)
         {
            if (e instanceof DeploymentException)
            {
               throw new DeploymentException("Could not deploy " + url + ":" + e.getMessage(), e.getCause());
            }
            else
            {
               throw new DeploymentException("Unexpected exception with portlet.xml " + url, e);
            }
         }
         finally
         {
            IOTools.safeClose(in);
         }
      }
      return null;
   }

   public Collection<? extends ManagedPortletApplication> getManagedPortletApplications()
   {
      return applications.values();
   }

   public ManagedPortletApplication getManagedPortletApplication(String id)
   {
      return applications.get(id);
   }

   public void addListener(ManagedObjectRegistryEventListener listener)
   {
      broadcaster.addListener(listener);
   }

   public void removeListener(ManagedObjectRegistryEventListener listener)
   {
      broadcaster.removeListener(listener);
   }
}
