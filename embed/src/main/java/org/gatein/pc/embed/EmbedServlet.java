/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.embed;

import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.portlet.aspects.ConsumerCacheInterceptor;
import org.gatein.pc.portlet.aspects.PortletCustomizationInterceptor;
import org.gatein.pc.portlet.aspects.RequestAttributeConversationInterceptor;
import org.gatein.pc.portlet.aspects.SecureTransportInterceptor;
import org.gatein.pc.portlet.aspects.ValveInterceptor;
import org.gatein.pc.portlet.container.ContainerPortletDispatcher;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.impl.container.PortletApplicationLifeCycle;
import org.gatein.pc.portlet.impl.deployment.DeploymentException;
import org.gatein.pc.portlet.impl.deployment.PortletApplicationDeployer;
import org.gatein.pc.portlet.impl.deployment.PortletApplicationDeployment;
import org.gatein.pc.portlet.impl.state.StateConverterV0;
import org.gatein.pc.portlet.impl.state.StateManagementPolicyService;
import org.gatein.pc.portlet.impl.state.producer.PortletStatePersistenceManagerService;
import org.gatein.pc.portlet.state.producer.ProducerPortletInvoker;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class EmbedServlet extends HttpServlet
{

   /** No more than 100 events. */
   static final int MAX_EVENT_COUNT = 100;

   /** . */
   private PortletApplicationDeployer deployer;

   /** . */
   private PortletInvoker invoker;

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);

      //
      ContainerPortletInvoker containerPortletInvoker = new ContainerPortletInvoker();
      PortletApplicationDeployer deployer = new PortletApplicationDeployer(containerPortletInvoker);

      // Container stack
      PortletInvokerInterceptor consumerPortletInvoker = new PortletInvokerInterceptor();
      consumerPortletInvoker.
         append(new ConsumerCacheInterceptor()).
         append(new PortletCustomizationInterceptor()).
         append(new ProducerPortletInvoker(new PortletStatePersistenceManagerService(), new StateManagementPolicyService(true), new StateConverterV0())).
         append(containerPortletInvoker).
         append(new ValveInterceptor(deployer)).
         append(new SecureTransportInterceptor()).
         append(new RequestAttributeConversationInterceptor()).
         append(new ContainerPortletDispatcher());

      //
      this.deployer = deployer;
      this.invoker = consumerPortletInvoker;

      // Deploy manually
      PortletApplicationDeployment deployment;
      try
      {
         deployment = deployer.add(getServletContext());
      }
      catch (DeploymentException e)
      {
         throw new ServletException(e);
      }

      //
      if (deployment != null)
      {
         PortletApplicationLifeCycle lifeCycle = deployment.getPortletApplicationLifeCycle();
         checkLifeCycle(lifeCycle);
         for (ManagedObject dependency : lifeCycle.getDependencies())
         {
            checkLifeCycle(dependency);
         }
      }
   }

   private void checkLifeCycle(ManagedObject managed) throws ServletException
   {
      if (managed.getStatus() == LifeCycleStatus.FAILED)
      {
         throw new ServletException("Portlet application start failed", managed.getFailure());
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doGet(req, resp);
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      HashMap<String, String[]> parameters = new HashMap<String, String[]>(req.getParameterMap());

      // Parse page
      Page page = new Page(invoker, req.getRequestURI());

      // Validate page
      Window notFound = null;
      for (Window window : page.windows.values())
      {
         if (window.portlet == null)
         {
            notFound = window;
            break;
         }
      }

      //
      if (notFound != null)
      {
         resp.sendError(404, "Portlet " + notFound.name + " is not found");
      }
      else
      {
         // Build phase
         EmbedPhase phase;
         String[] phaseParam = parameters.remove("javax.portlet.phase");
         if (phaseParam != null)
         {
            if ("render".equalsIgnoreCase(phaseParam[0]))
            {
               phase = new EmbedPhase.Render(page, invoker, parameters, req, resp);
            }
            else if ("action".equalsIgnoreCase(phaseParam[0]))
            {
               phase = new EmbedPhase.Action(page, invoker, parameters, req, resp);
            }
            else if ("resource".equalsIgnoreCase(phaseParam[0]))
            {
               phase = new EmbedPhase.Resource(page, invoker, parameters, req, resp);
            }
            else
            {
               phase = null;
            }
         }
         else
         {
            phase = new EmbedPhase.Render(page, invoker, parameters, req, resp);
         }

         // Decode invocation
         if (phase == null)
         {
            throw new ServletException("Illegal phase value " + req.getParameter("javax.portlet.phase"));
         }

         //
         phase.invoke();

         //

         // Setup the queue with the initial phase
//         LinkedList<EmbedPhase> queue = new LinkedList<EmbedPhase>();
//         queue.addLast(phase);
//
//
//         //
//         while (queue.size() > 0)
//         {
//            EmbedPhase current = queue.removeFirst();
//            if (current instanceof EmbedPhase.Interaction)
//            {
//               EmbedPhase.Interaction interaction = (EmbedPhase.Interaction)current;
//               for (UpdateNavigationalStateResponse.Event producedEvent : interaction.producedEvents)
//               {
///*
//                  for (Window consumer : page.getConsumers(producedEvent.getName()))
//                  {
//                     EmbedPhase.Event eventPhase = new EmbedPhase.Event(page, invoker, parameters, req, resp);
//                     queue.add(eventPhase);
//                  }
//*/
//               }
//            }
//         }
      }
   }

   @Override
   public void destroy()
   {
      if (deployer != null)
      {
         deployer.remove(getServletContext());
      }
   }
}
