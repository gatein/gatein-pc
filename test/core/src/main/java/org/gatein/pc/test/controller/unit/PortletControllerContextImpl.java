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

package org.gatein.pc.test.controller.unit;

import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.impl.AbstractControllerContext;
import org.gatein.pc.controller.impl.event.EventControllerContextImpl;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.controller.state.PageNavigationalState;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.portlet.impl.spi.AbstractServerContext;
import org.gatein.pc.test.unit.PortletTestServlet;
import org.gatein.common.io.Serialization;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletControllerContextImpl extends AbstractControllerContext
{

   /** . */
   private final PortletInvoker portletInvoker;
   
   /** . */
   private final EventControllerContext eventControllerContext;

   /** . */
   private final StateControllerContext stateControllerContext;

   /** . */
   private final Serialization<PageNavigationalState> serialization;

   public PortletControllerContextImpl(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext)
      throws IOException, ClassNotFoundException
   {
      super(req, resp);

      //
      this.portletInvoker = (PortletInvoker)servletContext.getAttribute("ConsumerPortletInvoker");
      this.eventControllerContext = new EventControllerContextImpl(portletInvoker);
      this.stateControllerContext = new StateControllerContextImpl();
      this.serialization = new PageNavigationalStateSerialization(stateControllerContext);
   }

   public PortletInvoker getPortletInvoker()
   {
      return portletInvoker;
   }

   protected Portlet getPortlet(String windowId) throws PortletInvokerException
   {
      return portletInvoker.getPortlet(PortletContext.createPortletContext(windowId));
   }

   public StateControllerContext getStateControllerContext()
   {
      return stateControllerContext;
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException
   {

      // Override ServerContext
      invocation.setServerContext(new AbstractServerContext(
         getClientRequest(), getClientResponse()
      ) {

         @Override
         public void dispatch(ServletContext target, HttpServletRequest request, HttpServletResponse response, Callable callable) throws Exception
         {
            RequestDispatcher dispatcher = target.getRequestDispatcher("/portlet");
            PortletTestServlet.callback.set(callable);
            try
            {
               dispatcher.include(getClientRequest(), getClientResponse());
            }
            finally
            {
               PortletTestServlet.callback.set(null);
            }
         }
      });

      //
      return portletInvoker.invoke(invocation);
   }

   public EventControllerContext getEventControllerContext()
   {
      return eventControllerContext;
   }

   public Collection<Portlet> getPortlets() throws PortletInvokerException
   {
      return portletInvoker.getPortlets();
   }

   public PortletInvocationContext createPortletInvocationContext(String windowId, PageNavigationalState pageNavigationalState)
   {
      return new ControllerPortletInvocationContext(serialization, req, resp, windowId, pageNavigationalState);
   }

   public Serialization<PageNavigationalState> getPageNavigationalStateSerialization()
   {
      return serialization;
   }
}
