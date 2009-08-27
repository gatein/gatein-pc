/*
 * JBoss, a division of Red Hat
 * Copyright 2009, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.test;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.gatein.pc.PortletInvokerInterceptor;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.aspects.portlet.CCPPInterceptor;
import org.gatein.pc.aspects.portlet.ConsumerCacheInterceptor;
import org.gatein.pc.aspects.portlet.ContextDispatcherInterceptor;
import org.gatein.pc.aspects.portlet.EventPayloadInterceptor;
import org.gatein.pc.aspects.portlet.PortletCustomizationInterceptor;
import org.gatein.pc.aspects.portlet.ProducerCacheInterceptor;
import org.gatein.pc.aspects.portlet.RequestAttributeConversationInterceptor;
import org.gatein.pc.aspects.portlet.SecureTransportInterceptor;
import org.gatein.pc.aspects.portlet.ValveInterceptor;
import org.gatein.pc.container.ContainerPortletDispatcher;
import org.gatein.pc.container.ContainerPortletInvoker;
import org.gatein.pc.impl.state.MapStateConverter;
import org.gatein.pc.impl.state.StateConverterV0;
import org.gatein.pc.impl.state.StateManagementPolicyService;
import org.gatein.pc.impl.state.producer.PortletStatePersistenceManagerService;
import org.gatein.pc.state.StateConverter;
import org.gatein.pc.state.producer.PortletStatePersistenceManager;
import org.gatein.pc.state.producer.ProducerPortletInvoker;
import org.jboss.portal.test.framework.impl.generic.server.GenericServiceExporter;
import org.gatein.wci.ServletContainer;
import org.jboss.unit.remote.driver.RemoteTestDriverServer;

import javax.servlet.http.HttpServlet;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class ExoKernelIntegration extends HttpServlet
{

   //protected PortletApplicationDeployer portletApplicationRegistry;
   protected TestPortletApplicationDeployer portletApplicationRegistry;
   protected GenericServiceExporter testDriverServerExporter;

   public void init()
   {
      
      RemoteTestDriverServer testDriverServer = new RemoteTestDriverServer();
      testDriverServerExporter = new GenericServiceExporter("socket://localhost:5400", testDriverServer, "org.jboss.unit.remote.driver.RemoteTestDriver");
      
      ExoContainer container = ExoContainerContext.getCurrentContainer();

      // The producer persistence manager
      PortletStatePersistenceManagerService producerPersistenceManager = new PortletStatePersistenceManagerService();
      // register the persistence manager so that it can be used by WSRP
      container.registerComponentInstance(PortletStatePersistenceManager.class, producerPersistenceManager);

      // The producer state management policy
      StateManagementPolicyService producerStateManagementPolicy = new StateManagementPolicyService();
      producerStateManagementPolicy.setPersistLocally(true);//false);

      // The producer state converter
      StateConverter producerStateConverter = new StateConverterV0();//MapStateConverter();

      // The portlet container invoker
      ContainerPortletInvoker containerPortletInvoker = new ContainerPortletInvoker();
      // continued below


      // The portlet application deployer
      portletApplicationRegistry = new TestPortletApplicationDeployer();//new PortletApplicationDeployer();
      portletApplicationRegistry.setContainerPortletInvoker(containerPortletInvoker);
      portletApplicationRegistry.setDriver(testDriverServer);

      //Container Stack
      ContainerPortletDispatcher portletContainerDispatcher = new ContainerPortletDispatcher();
      EventPayloadInterceptor eventPayloadInterceptor = new EventPayloadInterceptor();
      eventPayloadInterceptor.setNext(portletContainerDispatcher);
      RequestAttributeConversationInterceptor requestAttributeConversationInterceptor = new RequestAttributeConversationInterceptor();
      requestAttributeConversationInterceptor.setNext(eventPayloadInterceptor);
      CCPPInterceptor ccppInterceptor = new CCPPInterceptor();
      ccppInterceptor.setNext(requestAttributeConversationInterceptor);
      ProducerCacheInterceptor producerCacheInterceptor = new ProducerCacheInterceptor();
      producerCacheInterceptor.setNext(ccppInterceptor);
      ContextDispatcherInterceptor contextDispatcherInterceptor = new ContextDispatcherInterceptor();
      contextDispatcherInterceptor.setNext(producerCacheInterceptor);
      SecureTransportInterceptor secureTransportInterceptor = new SecureTransportInterceptor();
      secureTransportInterceptor.setNext(contextDispatcherInterceptor);
      ValveInterceptor valveInterceptor = new ValveInterceptor();
      valveInterceptor.setPortletApplicationRegistry(portletApplicationRegistry);
      valveInterceptor.setNext(secureTransportInterceptor);

      // inject ServletContainer in objects that need it
      ServletContainer servletContainer = (ServletContainer)container.getComponentInstance(ServletContainer.class);
      portletApplicationRegistry.setServletContainer(servletContainer);
      contextDispatcherInterceptor.setServletContainer(servletContainer);

      // The portlet container invoker continued
      containerPortletInvoker.setNext(valveInterceptor);

      // The producer portlet invoker
      ProducerPortletInvoker producerPortletInvoker = new ProducerPortletInvoker();
      producerPortletInvoker.setNext(containerPortletInvoker);
      producerPortletInvoker.setPersistenceManager(producerPersistenceManager);
      producerPortletInvoker.setStateManagementPolicy(producerStateManagementPolicy);
      producerPortletInvoker.setStateConverter(producerStateConverter);

      // register producer portlet invoker so that WSRP can use it
      container.registerComponentInstance(ProducerPortletInvoker.class, producerPortletInvoker);

      // The consumer portlet invoker
      PortletCustomizationInterceptor portletCustomizationInterceptor = new PortletCustomizationInterceptor();
      portletCustomizationInterceptor.setNext(producerPortletInvoker);
      ConsumerCacheInterceptor consumerCacheInterceptor = new ConsumerCacheInterceptor();
      consumerCacheInterceptor.setNext(portletCustomizationInterceptor);
      PortletInvokerInterceptor consumerPortletInvoker = new PortletInvokerInterceptor();
      consumerPortletInvoker.setNext(consumerCacheInterceptor);

      //container.registerComponentInstance(PortletInvoker.class, consumerPortletInvoker);

      // Federating portlet invoker
      //FederatingPortletInvoker federatingPortletInvoker = new FederatingPortletInvokerService();

      // register local portlet invoker with federating portlet invoker
      //federatingPortletInvoker.registerInvoker(LOCAL_PORTLET_INVOKER_ID, containerPortletInvoker);//containerPortletInvoker);

      /* register with container */
      container.registerComponentInstance(PortletInvoker.class, consumerPortletInvoker);
      
      portletApplicationRegistry.start();
      
      try
      {
      testDriverServerExporter.start();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }


   public void destroy()
   {
      if (portletApplicationRegistry != null)
      {
         portletApplicationRegistry.stop();
      }
      if (testDriverServerExporter != null)
      {
         testDriverServerExporter.stop();
      }
   }
}

