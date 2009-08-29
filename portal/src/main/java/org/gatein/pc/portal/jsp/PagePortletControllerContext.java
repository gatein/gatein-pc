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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletPageNavigationalStateSerialization;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.controller.impl.AbstractPortletControllerContext;
import org.gatein.wci.IllegalRequestException;
import org.gatein.common.io.Serialization;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PagePortletControllerContext extends AbstractPortletControllerContext
{

   /** . */
   private final PageEventControllerContext eventControllerContext;

   /** . */
   private final StateControllerContext stateControllerContext;

   /** . */
   private final Serialization<PortletPageNavigationalState> serialization;

   /** . */
   private final PortalPrepareResponse prepareResponse;

   /** . */
   private final Map<Key, Portlet> portlets;

   /** . */
   private final PortletInvoker invoker;

   public PagePortletControllerContext(
      HttpServletRequest req,
      HttpServletResponse resp,
      PortletInvoker invoker,
      ServletContext servletContext,
      PortalPrepareResponse prepareResponse)
      throws IllegalRequestException, IOException, ServletException, PortletInvokerException
   {
      super(req, resp);

      //
      Map<Key, Portlet> portlets = new HashMap<Key, Portlet>();
      for (Portlet portlet : invoker.getPortlets())
      {
         PortletInfo portletInfo = portlet.getInfo();
         String portletName = portletInfo.getName();
         String applicationName = portletInfo.getApplicationName();
         Key key = new Key(applicationName, portletName);
         portlets.put(key, portlet);
      }

      //
      this.invoker = invoker;
      this.portlets = portlets;
      this.prepareResponse = prepareResponse;
      this.stateControllerContext = new StateControllerContextImpl(this);
      this.eventControllerContext = new PageEventControllerContext(this, prepareResponse);
      this.serialization = new PortletPageNavigationalStateSerialization(stateControllerContext);
   }

   public Portlet findPortlet(String applicationName, String portletName)
   {
      return portlets.get(new Key(applicationName, portletName));
   }

   protected Portlet getPortlet(String windowId) throws PortletInvokerException
   {
      WindowDef windowDef = prepareResponse.getWindowDef(windowId);

      //
      return findPortlet(windowDef.getApplicationName(), windowDef.getPortletName());
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException
   {
      return invoker.invoke(invocation);
   }

   public PageEventControllerContext getEventControllerContext()
   {
      return eventControllerContext;
   }

   public StateControllerContext getStateControllerContext()
   {
      return stateControllerContext;
   }

   public Serialization<PortletPageNavigationalState> getPageNavigationalStateSerialization()
   {
      return serialization;
   }

   private static class Key
   {

      /** . */
      private final String applicationName;

      /** . */
      private final String portletName;

      private Key(String applicationName, String portletName)
      {
         this.applicationName = applicationName;
         this.portletName = portletName;
      }

      public boolean equals(Object obj)
      {
         if (obj == this)
         {
            return true;
         }
         if (obj instanceof Key)
         {
            Key that = (Key)obj;
            return applicationName.equals(that.applicationName) && portletName.equals(that.portletName);
         }
         return false;
      }

      public int hashCode()
      {
         return applicationName.hashCode() + portletName.hashCode();
      }
   }
}
