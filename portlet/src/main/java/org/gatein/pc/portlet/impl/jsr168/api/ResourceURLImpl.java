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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.cache.CacheLevel;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;

import javax.portlet.ResourceURL;
import javax.portlet.PortletURLGenerationListener;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ResourceURLImpl extends BaseURLImpl implements ResourceURL
{

   /** . */
   private static final Map<String, CacheLevel> jsr168ToType = new HashMap<String, CacheLevel>();

   /** . */
   private static final Map<CacheLevel, String> typetoJSR168 = new HashMap<CacheLevel, String>();

   static
   {
      jsr168ToType.put(ResourceURL.FULL, CacheLevel.FULL);
      jsr168ToType.put(ResourceURL.PAGE, CacheLevel.PAGE);
      jsr168ToType.put(ResourceURL.PORTLET, CacheLevel.PORTLET);

      //
      typetoJSR168.put(CacheLevel.FULL, ResourceURL.FULL);
      typetoJSR168.put(CacheLevel.PAGE, ResourceURL.PAGE);
      typetoJSR168.put(CacheLevel.PORTLET, ResourceURL.PORTLET);
   }

   public static String toJSR168(CacheLevel cacheLevel)
   {
      return typetoJSR168.get(cacheLevel);
   }

   /** . */
   private final InternalResourceURL url;

   private ResourceURLImpl(
      PortletInvocation invocation,
      PortletRequestImpl preq,
      InternalResourceURL url,
      boolean filterable)
   {
      super(invocation, preq, filterable);

      //
      this.url = url;

      //
      if (invocation instanceof ResourceInvocation)
      {
         url.parentCacheLevel = ((ResourceInvocation)invocation).getCacheLevel();
      }
   }

   private ResourceURLImpl(ResourceURLImpl original)
   {
      super(original);

      //
      this.url = new InternalResourceURL(original.url);
   }

   public void setResourceID(String resourceID)
   {
      if (resourceID != null)
      {
         url.id = resourceID;
      }
   }

   public String getCacheability()
   {
      CacheLevel cacheLevel = url.getCacheability();

      //
      return typetoJSR168.get(cacheLevel);
   }

   public void setCacheability(String s)
   {
      CacheLevel cacheLevel = jsr168ToType.get(s);

      //
      if (cacheLevel != null)
      {
         if (url.parentCacheLevel == null)
         {
            url.cacheLevel = cacheLevel;
         }
         else
         {
            switch (url.parentCacheLevel)
            {
               case FULL:
                  if (cacheLevel != CacheLevel.FULL)
                  {
                     throw new IllegalStateException();
                  }
                  break;
               case PORTLET:
                  if (cacheLevel == CacheLevel.PAGE)
                  {
                     throw new IllegalStateException();
                  }
                  break;
            }
            url.cacheLevel = cacheLevel;
         }
      }
   }

   protected InternalContainerURL getContainerURL()
   {
      return url;
   }

   protected BaseURLImpl createClone()
   {
      return new ResourceURLImpl(this);
   }

   protected void filter(PortletURLGenerationListener listener)
   {
      listener.filterResourceURL(this);
   }

   public static ResourceURLImpl createResourceURL(PortletInvocation invocation, PortletRequestImpl preq)
   {
      return new ResourceURLImpl(
         invocation,
         preq,
         new InternalResourceURL(invocation.getNavigationalState(), invocation.getMode(),invocation.getWindowState()),
         true);
   }

   private static class InternalResourceURL extends InternalContainerURL implements org.gatein.pc.api.ResourceURL
   {

      /** . */
      private String id;

      /** The cacheability constraining the url cacheability. */
      private CacheLevel parentCacheLevel;

      /** . */
      private CacheLevel cacheLevel;

      /** . */
      private ParametersStateString parameters;

      /** . */
      private final StateString navigationalState;

      /** . */
      private final Mode mode;

      /** . */
      private final org.gatein.pc.api.WindowState windowState;

      private InternalResourceURL(StateString navigationalState, Mode mode, WindowState windowState)
      {
         this.parameters = ParametersStateString.create();
         this.navigationalState = navigationalState;
         this.mode = mode;
         this.windowState = windowState;
      }

      private InternalResourceURL(InternalResourceURL original)
      {
         this.id = original.id;
         this.parentCacheLevel = original.parentCacheLevel;
         this.cacheLevel = original.cacheLevel;
         this.parameters = ParametersStateString.create(ParameterMap.clone(original.parameters.getParameters()));
         this.navigationalState = original.navigationalState;
         this.mode = original.mode;
         this.windowState = original.windowState;
      }

      protected void setParameter(String name, String value)
      {
         parameters.setValue(name, value);
      }

      protected void setParameter(String name, String[] values)
      {
         parameters.setValues(name, values);
      }

      protected void setParameters(Map<String, String[]> parameterMap)
      {
         parameters.replace(parameterMap);
      }

      protected Map<String, String[]> getParameters()
      {
         return ParameterMap.clone(parameters.getParameters());
      }

      public StateString getResourceState()
      {
         return parameters;
      }

      public String getResourceId()
      {
         return id;
      }

      public CacheLevel getCacheability()
      {
         if (cacheLevel != null)
         {
            return cacheLevel;
         }
         else
         {
            if (parentCacheLevel != null)
            {
               return parentCacheLevel;
            }
            else
            {
               return CacheLevel.PAGE;
            }
         }
      }

      public StateString getNavigationalState()
      {
         return navigationalState;
      }

      public Mode getMode()
      {
         return mode;
      }

      public WindowState getWindowState()
      {
         return windowState;
      }
   }
}
