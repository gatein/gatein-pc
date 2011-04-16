/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portlet.aspects;

import org.gatein.common.concurrent.Valve;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.PortletContainerContext;
import org.gatein.pc.portlet.container.managed.PortletApplicationRegistry;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UnavailableResponse;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;
import org.gatein.pc.api.PortletInvokerException;

/**
 * This aspect has two responsabilities :<br/> <ul> <li>continue the request only if the portlet container valve is
 * open. When the valve is closed, it will return an unavailable response to the caller. When the valve is open then the
 * current thread of execution enters the valve for the duration of the call.</li> <li> if the response from the next
 * aspect is an unavailable result, stop the container in order to destroy the portlet and implement this part of the
 * portlet specification.</li> </ul>
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class ValveInterceptor extends PortletInvokerInterceptor
{

   /** . */
   private PortletApplicationRegistry registry;

   public PortletApplicationRegistry getPortletApplicationRegistry()
   {
      return registry;
   }

   public void setPortletApplicationRegistry(PortletApplicationRegistry portletApplicationRegistry)
   {
      this.registry = portletApplicationRegistry;
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      PortletContainerImpl container = (PortletContainerImpl)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);

      //
      Valve valve = container.getValve();

      if (valve.beforeInvocation())
      {
         PortletInvocationResponse response;

         try
         {
            response = super.invoke(invocation);
         }
         finally
         {
            // Release the valve
            valve.afterInvocation();
         }

         // Stop the container if necessary
         if (response instanceof UnavailableResponse)
         {
            PortletContainerContext containerContext = container.getContext();

            // This call will wait until all the current threads have exited the component valve.
            // Perhaps this should be done asynchronously as it may lead to a long delay ?
            // It could be made parametrizable as a runtime option too, so the deployer can choose what mode is preferable
            containerContext.managedStop();
         }

         //
         return response;
      }
      else
      {
         return new UnavailableResponse();
      }
   }
}
