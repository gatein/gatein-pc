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
package org.gatein.pc.controller.request;

import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.controller.ControllerContext;
import org.gatein.pc.controller.state.PageNavigationalState;
import org.gatein.pc.controller.state.WindowNavigationalState;
import org.gatein.pc.api.cache.CacheLevel;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletResourceRequest extends ContainerRequest
{

   /** . */
   private final String resourceId;

   /** . */
   private final StateString resourceState;

   /** . */
   private final Map<String, String[]> bodyParameters;

   /** . */
   private final Scope scope;

   /**
    * Build a new portlet resource request.
    *
    * @param windowId the window id
    * @param resourceId the resource id
    * @param resourceState the resource state
    * @param bodyParameters the body parameters
    * @param scope the scope
    * @throws IllegalArgumentException if the windows id, the resource state or the scope is null
    */
   public PortletResourceRequest(
      String windowId,
      String resourceId,
      StateString resourceState,
      Map<String, String[]> bodyParameters,
      Scope scope) throws IllegalArgumentException
   {
      super(windowId);

      //
      if (resourceState == null)
      {
         throw new IllegalArgumentException("No null resource state provided");
      }
      if (scope == null)
      {
         throw new IllegalArgumentException("No null scope provided");
      }

      //
      this.resourceId = resourceId;
      this.resourceState = resourceState;
      this.bodyParameters = bodyParameters;
      this.scope = scope;
   }

   public Scope getScope()
   {
      return scope;
   }

   public CacheLevel getCacheability()
   {
      return scope.getCacheability();
   }

   public String getResourceId()
   {
      return resourceId;
   }

   public StateString getResourceState()
   {
      return resourceState;
   }

   public Map<String, String[]> getBodyParameters()
   {
      return bodyParameters;
   }

   public PageNavigationalState getPageNavigationalState()
   {
      if (scope instanceof PageScope)
      {
         return ((PageScope)scope).getPageNavigationalState();
      }
      else
      {
         return null;
      }
   }

   public abstract static class Scope
   {

      public abstract CacheLevel getCacheability();

   }

   public static class FullScope extends Scope
   {
      public CacheLevel getCacheability()
      {
         return CacheLevel.FULL;
      }
   }

   public static class PortletScope extends FullScope
   {

      /** . */
      private final WindowNavigationalState windowNavigationalState;

      public PortletScope(WindowNavigationalState windowNavigationalState)
      {
         this.windowNavigationalState = windowNavigationalState;
      }

      public WindowNavigationalState getWindowNavigationalState()
      {
         return windowNavigationalState;
      }

      public CacheLevel getCacheability()
      {
         return CacheLevel.PORTLET;
      }
   }

   public static class PageScope extends PortletScope
   {

      /** . */
      private final PageNavigationalState pageNavigationalState;

      public PageScope(WindowNavigationalState windowNavigationalState, PageNavigationalState pageNavigationalState)
      {
         super(windowNavigationalState);

         //
         this.pageNavigationalState = pageNavigationalState;
      }

      public PageNavigationalState getPageNavigationalState()
      {
         return pageNavigationalState;
      }

      public CacheLevel getCacheability()
      {
         return CacheLevel.PAGE;
      }
   }

   @Override
   public PortletInvocationResponse invoke(ControllerContext context) throws PortletInvokerException
   {
      Mode mode = null;
      org.gatein.pc.api.WindowState windowState = null;
      PageNavigationalState pageNavigationalState = null;
      Map<String, String[]> publicNS = null;
      StateString portletNS = null;
      CacheLevel cacheability;

      if (scope instanceof PortletResourceRequest.PortletScope)
      {
         PortletResourceRequest.PortletScope portletScope = (PortletResourceRequest.PortletScope)scope;
         WindowNavigationalState navigationalState = portletScope.getWindowNavigationalState();

         // 
         if (navigationalState != null)
         {
            mode = navigationalState.getMode();
            windowState = navigationalState.getWindowState();
            portletNS = navigationalState.getPortletNavigationalState();
         }

         //
         if (scope instanceof PortletResourceRequest.PageScope)
         {
            PortletResourceRequest.PageScope pageScope = (PortletResourceRequest.PageScope)scope;
            pageNavigationalState = pageScope.getPageNavigationalState();
            cacheability = CacheLevel.PAGE;

            //
            if (pageNavigationalState != null)
            {
               publicNS = context.getStateControllerContext().getPublicWindowNavigationalState(context, pageNavigationalState, windowId);
            }
         }
         else
         {
            cacheability = CacheLevel.PORTLET;
         }
      }
      else
      {
         cacheability = CacheLevel.FULL;
      }

      //
      if (mode == null)
      {
         mode = Mode.VIEW;
      }
      if (windowState == null)
      {
         windowState = WindowState.NORMAL;
      }

      //
      PortletInvocationContext portletInvocationContext = context.createPortletInvocationContext(windowId, pageNavigationalState);
      ResourceInvocation resourceInvocation = new ResourceInvocation(portletInvocationContext);

      //
      resourceInvocation.setResourceId(resourceId);
      resourceInvocation.setCacheLevel(cacheability);
      resourceInvocation.setMode(mode);
      resourceInvocation.setWindowState(windowState);
      resourceInvocation.setNavigationalState(portletNS);
      resourceInvocation.setPublicNavigationalState(publicNS);
      resourceInvocation.setResourceState(resourceState);
      resourceInvocation.setForm(bodyParameters != null ? ParameterMap.clone(bodyParameters) : null);

      //
      try
      {
         return context.invoke(windowId, resourceInvocation);
      }
      catch (PortletInvokerException e)
      {
         return null;
      }
   }
}
