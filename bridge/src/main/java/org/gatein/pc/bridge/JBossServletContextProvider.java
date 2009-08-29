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
package org.gatein.pc.bridge;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.portals.bridges.common.ServletContextProvider;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.api.invocation.PortletInvocation;

/**
 * The JBoss implementation of <code>org.apache.portals.bridges.common.ServletContextProvider</code> use thread local
 * variables to keep the request associated with the current thread of execution.
 *
 * @author <a href="mailto:dr@vizuri.com">Swarn Dhaliwal</a>
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class JBossServletContextProvider implements ServletContextProvider
{

   /** . */
   private static final ThreadLocal local = new ThreadLocal();

   static void set(BridgeInfo info)
   {
      local.set(info);
   }

   public static BridgeInfo get()
   {
      return (BridgeInfo)local.get();
   }

   /** @throws IllegalStateException if no bridge info is found */
   public ServletContext getServletContext(GenericPortlet genericPortlet) throws IllegalStateException
   {
      BridgeInfo info = ((BridgeInfo)local.get());
      if (info == null)
      {
         throw new IllegalStateException("No bridge set");
      }
      return info.ctx;
   }

   /** @throws IllegalStateException if no bridge info is found */
   public HttpServletRequest getHttpServletRequest(GenericPortlet genericPortlet, PortletRequest portletRequest) throws IllegalStateException
   {
      BridgeInfo info = (BridgeInfo)JBossServletContextProvider.local.get();
      if (info == null)
      {
         throw new IllegalStateException("No bridge set");
      }
      if (info.breq == null)
      {
         init(info);
      }
      return info.breq;
   }

   /** @throws IllegalStateException if no bridge info is found */
   public HttpServletResponse getHttpServletResponse(GenericPortlet genericPortlet, PortletResponse portletResponse) throws IllegalStateException
   {
      BridgeInfo info = (BridgeInfo)JBossServletContextProvider.local.get();
      if (info == null)
      {
         throw new IllegalStateException("No bridge set");
      }
      if (info.breq == null)
      {
         init(info);
      }
      return info.bresp;
   }

   /** Lazy initialisation of the bridge info. */
   private void init(BridgeInfo bridgeInfo)
   {
      bridgeInfo.breq = bridgeInfo.getInvocation().getDispatchedRequest();
      bridgeInfo.bresp = new BridgeResponse(bridgeInfo);
   }

   public static class BridgeInfo
   {
      /** Servlet context of the dispatched application. */
      private final PortletInvocation invocation;

      /** Servlet context of the dispatched application. */
      private final ServletContext ctx;

      /** The bridge response. */
      private HttpServletRequest breq;

      /** The bridge response. */
      private HttpServletResponse bresp;

      public BridgeInfo(PortletInvocation invocation)
      {
         PortletContainer container = (PortletContainer)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);

         //
         this.invocation = invocation;
         this.ctx = container.getPortletApplication().getContext().getServletContext();
         this.breq = null;
         this.bresp = null;
      }

      public PortletInvocation getInvocation()
      {
         return invocation;
      }
   }
}
