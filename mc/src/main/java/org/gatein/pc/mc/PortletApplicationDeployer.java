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
import org.gatein.common.io.IOTools;
import org.gatein.pc.api.PortletInvoker;
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
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.PORTLET_JSR_168_NS;
import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.PORTLET_JSR_286_NS;
import org.gatein.pc.mc.metadata.factory.PortletApplicationModelFactory;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication10MetaData;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication20MetaData;
import org.gatein.pc.mc.metadata.impl.ValueTrimmingFilter;
import org.gatein.wci.ServletContainer;
import org.gatein.wci.ServletContainerFactory;
import org.gatein.wci.WebApp;
import org.gatein.wci.WebAppEvent;
import org.gatein.wci.WebAppLifeCycleEvent;
import org.gatein.wci.WebAppListener;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SingletonSchemaResolverFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletApplicationDeployer implements WebAppListener, PortletApplicationRegistry
{

   static
   {
      try
      {
         SingletonSchemaResolverFactory factory = SingletonSchemaResolverFactory.getInstance();
         DefaultSchemaResolver resolver = (DefaultSchemaResolver)factory.getSchemaBindingResolver();

         /** SchemaResolver */
         resolver.addSchemaLocation(PORTLET_JSR_168_NS, "portlet-app_1_0.xsd");
         resolver.addSchemaLocation(PORTLET_JSR_286_NS, "portlet-app_2_0.xsd");
         resolver.addClassBinding(PORTLET_JSR_286_NS, AnnotationPortletApplication20MetaData.class);
         resolver.addClassBinding(PORTLET_JSR_168_NS, AnnotationPortletApplication10MetaData.class);
      }
      catch (Exception e)
      {
         throw new Error(e);
      }
   }

   /** . */
   private Logger log = Logger.getLogger(PortletApplicationDeployer.class);

   /** . */
   private PortletApplicationRegistry registry;

   /** . */
   private ServletContainer servletContainer;

   private ServletContainerFactory servletContainerFactory;

   /** . */
   private Map<String, PortletApplicationDeployment> deploymentMap = new HashMap<String, PortletApplicationDeployment>();

   /** . */
   private ClassLoader classLoader;

   /** . */
   private final Map<String, PortletApplicationLifeCycle> applications = new HashMap<String, PortletApplicationLifeCycle>();

   /** . */
   private ContainerPortletInvoker containerPortletInvoker;

   /** . */
   private ManagedObjectRegistryEventBroadcaster broadcaster;

   public PortletApplicationRegistry getRegistry()
   {
      return registry;
   }

   public void setRegistry(PortletApplicationRegistry registry)
   {
      this.registry = registry;
   }

   public ServletContainer getServletContainer()
   {
      if (servletContainer == null)
      {
         servletContainer = servletContainerFactory.getServletContainer();
      }
      return servletContainer;
   }

   public void setServletContainer(ServletContainer servletContainer)
   {
      throw new UnsupportedOperationException("Inject ServletContainerFactory instead!");
   }

   public ServletContainerFactory getServletContainerFactory()
   {
      return servletContainerFactory;
   }

   public void setServletContainerFactory(ServletContainerFactory servletContainerFactory)
   {
      this.servletContainerFactory = servletContainerFactory;
   }

   public PortletInvoker getContainerPortletInvoker()
   {
      return containerPortletInvoker;
   }

   public void setContainerPortletInvoker(PortletInvoker containerPortletInvoker)
   {
      if (containerPortletInvoker instanceof ContainerPortletInvoker)
      {
         this.containerPortletInvoker = (ContainerPortletInvoker)containerPortletInvoker;
      }
      else
      {
         if (containerPortletInvoker == null)
         {
            return; // this method is called with null on shutdown
         }
         throw new IllegalArgumentException("PortletApplicationDeployer can only accept ContainerPortletInvokers!");
      }
   }

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
               add(event.getWebApp());
               log.debug("Installed war file" + cp);
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

   protected void add(WebApp webApp)
   {
      //
      PortletApplication10MetaData metaData = buildPortletApplicationMetaData(webApp);
      if (metaData != null)
      {
         ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
         try
         {
            Thread.currentThread().setContextClassLoader(classLoader);
            PortletApplicationDeployment deployment = new PortletApplicationDeployment(broadcaster, webApp, metaData);
            deploymentMap.put(webApp.getContextPath(), deployment);
            deployment.install();

            //
            PortletApplicationLifeCycle portletApplicationLifeCycle = deployment.getPortletApplicationLifeCycle();
            applications.put(portletApplicationLifeCycle.getId(), portletApplicationLifeCycle);
         }
         finally
         {
            Thread.currentThread().setContextClassLoader(oldCL);
         }
      }
   }

   private void remove(WebApp webApp)
   {
      PortletApplicationDeployment deployment = deploymentMap.remove(webApp.getContextPath());
      if (deployment != null)
      {
         PortletApplicationLifeCycle portletApplicationLifeCycle = deployment.getPortletApplicationLifeCycle();
         applications.remove(portletApplicationLifeCycle.getId());

         //
         ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
         try
         {
            Thread.currentThread().setContextClassLoader(classLoader);
            deployment.uninstall();
         }
         finally
         {
            Thread.currentThread().setContextClassLoader(oldCL);
         }
      }
   }

   public void start()
   {
      broadcaster = new ManagedObjectRegistryEventBroadcaster();
      classLoader = Thread.currentThread().getContextClassLoader();

      //
      broadcaster.addListener(bridgeToInvoker);
      getServletContainer().addWebAppListener(this);
   }

   public void stop()
   {
      // This should generate remove web app event and in cascade clear the registry
      // as well as the portlet container invoker
      getServletContainer().removeWebAppListener(this);

      //
      classLoader = null;
      broadcaster = null;
   }

   private PortletApplication10MetaData buildPortletApplicationMetaData(WebApp webApp)
   {
      try
      {
         URL url = webApp.getServletContext().getResource("/WEB-INF/portlet.xml");
         if (url != null)
         {
            InputStream in = null;
            try
            {
               in = IOTools.safeBufferedWrapper(url.openStream());

               // Validate 
               Unmarshaller unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
               unmarshaller.setNamespaceAware(true);
               unmarshaller.setSchemaValidation(false);
               unmarshaller.setValidation(false);

               //
               PortletApplicationModelFactory factory = new PortletApplicationModelFactory();

               // Unmarshal
               PortletApplication10MetaData portletApplicationMD = (PortletApplication10MetaData)unmarshaller.unmarshal(in, new ValueTrimmingFilter(factory), null);

               //
               return portletApplicationMD;
            }
            finally
            {
               IOTools.safeClose(in);
            }
         }
      }
      catch (IOException e)
      {
         log.error("Cannot read portlet.xml", e);
      }
      catch (JBossXBException e)
      {
         log.error("Cannot parse portlet.xml", e);
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
      broadcaster.addListener(listener);
   }

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
}
