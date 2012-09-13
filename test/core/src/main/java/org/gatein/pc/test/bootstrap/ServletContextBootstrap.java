/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.gatein.pc.test.bootstrap;

import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.portlet.aspects.CCPPInterceptor;
import org.gatein.pc.portlet.aspects.ConsumerCacheInterceptor;
import org.gatein.pc.portlet.aspects.ContextDispatcherInterceptor;
import org.gatein.pc.portlet.aspects.EventPayloadInterceptor;
import org.gatein.pc.portlet.aspects.PortletCustomizationInterceptor;
import org.gatein.pc.portlet.aspects.ProducerCacheInterceptor;
import org.gatein.pc.portlet.aspects.RequestAttributeConversationInterceptor;
import org.gatein.pc.portlet.aspects.SecureTransportInterceptor;
import org.gatein.pc.portlet.aspects.ValveInterceptor;
import org.gatein.pc.portlet.container.ContainerPortletDispatcher;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.impl.state.StateConverterV0;
import org.gatein.pc.portlet.impl.state.StateManagementPolicyService;
import org.gatein.pc.portlet.impl.state.producer.PortletStatePersistenceManagerService;
import org.gatein.pc.portlet.state.producer.ProducerPortletInvoker;
import org.gatein.pc.test.TestPortletApplicationDeployer;
import org.gatein.wci.ServletContainer;
import org.gatein.wci.ServletContainerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ServletContextBootstrap implements ServletContextListener
{

   /** . */
   private TestPortletApplicationDeployer portletApplicationDeployer;

   /** . */
   private PortletInvokerInterceptor consumerPortletInvoker;

   public void contextInitialized(final ServletContextEvent sce)
   {
      final ServletContext ctx = sce.getServletContext();

      //
      try
      {
         boostrap();

         //
         ctx.setAttribute("ConsumerPortletInvoker", consumerPortletInvoker);
      }
      catch (Exception e)
      {
         System.err.println("Could not boostrap test server");
         e.printStackTrace(System.err);
         throw new RuntimeException(e);
      }
   }

   public void boostrap() throws Exception
   {

      // **************
      // * WIRE PHASE *
      // **************

      // The producer persistence manager
      PortletStatePersistenceManagerService producerPersistenceManager = new PortletStatePersistenceManagerService();

      // The producer state management policy
      StateManagementPolicyService producerStateManagementPolicy = new StateManagementPolicyService();
      producerStateManagementPolicy.setPersistLocally(true);

      // The producer state converter
      StateConverterV0 producerStateConverter = new StateConverterV0();

      // The servlet container factory
      ServletContainerFactory servletContainerFactory = ServletContainerFactory.instance;

      // Container stack
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
      contextDispatcherInterceptor.setServletContainerFactory(servletContainerFactory);
      contextDispatcherInterceptor.setNext(producerCacheInterceptor);
      SecureTransportInterceptor secureTransportInterceptor = new SecureTransportInterceptor();
      secureTransportInterceptor.setNext(contextDispatcherInterceptor);
      ValveInterceptor valveInterceptor = new ValveInterceptor();
      valveInterceptor.setNext(secureTransportInterceptor);

      // The portlet container invoker
      ContainerPortletInvoker containerPortletInvoker = new ContainerPortletInvoker();
      containerPortletInvoker.setNext(valveInterceptor);

      // The producer portlet invoker
      ProducerPortletInvoker producerPortletInvoker = new ProducerPortletInvoker();
      producerPortletInvoker.setNext(containerPortletInvoker);
      producerPortletInvoker.setPersistenceManager(producerPersistenceManager);
      producerPortletInvoker.setStateManagementPolicy(producerStateManagementPolicy);
      producerPortletInvoker.setStateConverter(producerStateConverter);

      // The consumer portlet invoker
      PortletCustomizationInterceptor portletCustomizationInterceptor = new PortletCustomizationInterceptor();
      portletCustomizationInterceptor.setNext(producerPortletInvoker);
      ConsumerCacheInterceptor consumerCacheInterceptor = new ConsumerCacheInterceptor();
      consumerCacheInterceptor.setNext(portletCustomizationInterceptor);
      PortletInvokerInterceptor consumerPortletInvoker = new PortletInvokerInterceptor();
      consumerPortletInvoker.setNext(consumerCacheInterceptor);

      // The servlet container obtained from the servlet container factory
      ServletContainer servletContainer = servletContainerFactory.getServletContainer();

      //
      TestPortletApplicationDeployer portletApplicationDeployer = new TestPortletApplicationDeployer();
      portletApplicationDeployer.setServletContainerFactory(servletContainerFactory);
      portletApplicationDeployer.setContainerPortletInvoker(containerPortletInvoker);

      // Instantiated
      valveInterceptor.setPortletApplicationRegistry(portletApplicationDeployer);

      //
      this.consumerPortletInvoker = consumerPortletInvoker;

      // ***************
      // * START PHASE *
      // ***************

      //
      portletApplicationDeployer.start();
      this.portletApplicationDeployer = portletApplicationDeployer;
   }

   public void contextDestroyed(ServletContextEvent sce)
   {
      sce.getServletContext().setAttribute("ConsumerPortletInvoker", null);

      //
      if (portletApplicationDeployer != null)
      {
         portletApplicationDeployer.stop();
      }
   }
}
