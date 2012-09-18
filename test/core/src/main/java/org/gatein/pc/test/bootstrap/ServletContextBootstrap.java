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

      final ContainerPortletInvoker containerPortletInvoker = new ContainerPortletInvoker();
      TestPortletApplicationDeployer portletApplicationDeployer = new TestPortletApplicationDeployer(containerPortletInvoker);

      //
      PortletInvokerInterceptor consumerPortletInvoker = new PortletInvokerInterceptor();
      consumerPortletInvoker.
         append(new ConsumerCacheInterceptor()).
         append(new PortletCustomizationInterceptor()).
         append(new ProducerPortletInvoker(new PortletStatePersistenceManagerService(), new StateManagementPolicyService(true), new StateConverterV0())).
         append(containerPortletInvoker).
         append(new ValveInterceptor(portletApplicationDeployer)).
         append(new SecureTransportInterceptor()).
         append(new ContextDispatcherInterceptor()).
         append(new ProducerCacheInterceptor()).
         append(new CCPPInterceptor()).
         append(new RequestAttributeConversationInterceptor()).
         append(new EventPayloadInterceptor()).
         append(new ContainerPortletDispatcher());

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
