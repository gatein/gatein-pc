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
package org.gatein.pc.test.tck;

import org.gatein.pc.controller.impl.AbstractPortletControllerContext;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.controller.impl.event.EventControllerContextImpl;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.wci.IllegalRequestException;
import org.gatein.common.io.Serialization;
import org.gatein.common.mc.bootstrap.WebBootstrap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class TCKPortletControllerContext extends AbstractPortletControllerContext
{

   /** . */
   private final PortletInvoker portletInvoker;

   /** . */
   private final TCKStateControllerContext stateControllerContext;

   /** . */
   private final EventControllerContext eventControllerContext;

   /** . */
   private final Serialization<PortletPageNavigationalState> serialization;

   public TCKPortletControllerContext(
      HttpServletRequest req,
      HttpServletResponse resp,
      ServletContext servletContext) throws IllegalRequestException, IOException
   {
      super(req, resp);

      //
      this.portletInvoker = (PortletInvoker)servletContext.getAttribute(WebBootstrap.BEAN_PREFIX + "ConsumerPortletInvoker");
      this.stateControllerContext = new TCKStateControllerContext(new StateControllerContextImpl(this));
      this.eventControllerContext = new EventControllerContextImpl(portletInvoker);
      this.serialization = new TCKPageNavigationalStateSerialization(stateControllerContext);
   }

   public PortletInvoker getPortletInvoker()
   {
      return portletInvoker;
   }

   public Set<Portlet> getPortlets() throws PortletInvokerException
   {
      return portletInvoker.getPortlets();
   }

   protected Portlet getPortlet(String windowId) throws PortletInvokerException
   {
      return portletInvoker.getPortlet(PortletContext.createPortletContext(windowId));
   }

   protected PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException
   {
      return portletInvoker.invoke(invocation);
   }

   protected Serialization<PortletPageNavigationalState> getPageNavigationalStateSerialization()
   {
      return serialization;
   }

   public EventControllerContext getEventControllerContext()
   {
      return eventControllerContext;
   }

   public StateControllerContext getStateControllerContext()
   {
      return stateControllerContext;
   }
}
