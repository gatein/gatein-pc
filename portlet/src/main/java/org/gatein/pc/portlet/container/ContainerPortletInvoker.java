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
package org.gatein.pc.portlet.container;

import org.gatein.common.invocation.InvocationException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.impl.info.ContainerPreferencesInfo;
import org.gatein.pc.portlet.impl.info.ContainerPortletInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferenceInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.api.state.DestroyCloneFailure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of portlet invoker that makes a call to a portlet container.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class ContainerPortletInvoker extends PortletInvokerInterceptor
{

   /** The key under which the portlet container is stored in the request scope of the invocation. */
   public static final String PORTLET_CONTAINER = "PORTLET_CONTAINER";

   /** . */
   private Map<String, Portlet> portlets = new HashMap<String, Portlet>();

   public void addPortletContainer(PortletContainer portletContainer)
   {
      Map<String, Portlet> portlets = new HashMap<String, Portlet>(this.portlets);
      PortletImpl portlet = new PortletImpl(portletContainer);
      portlets.put(portlet.getContext().getId(), portlet);

      //
      this.portlets = portlets;
   }

   public void removePortletContainer(PortletContainer portletContainer)
   {
      Map<String, Portlet> portlets = new HashMap<String, Portlet>(this.portlets);
      PortletImpl portlet = new PortletImpl(portletContainer);
      portlets.remove(portlet.getContext().getId());

      //
      this.portlets = portlets;
   }

   public Set<Portlet> getPortlets()
   {
      return new HashSet<Portlet>(portlets.values());
   }

   public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException("No null portlet id accepted");
      }
      String portletId = portletContext.getId();
      PortletImpl portlet = (PortletImpl)portlets.get(portletId);
      if (portlet == null)
      {
         throw new NoSuchPortletException(portletId);
      }
      return portlet;
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException, InvocationException
   {
      // Get portlet container
      PortletContext ctx = invocation.getTarget();
      PortletImpl portlet = (PortletImpl)getPortlet(ctx);
      PortletContainer container = portlet.container;

      //
      try
      {
         invocation.setAttribute(ContainerPortletInvoker.PORTLET_CONTAINER, container);

         //
         return super.invoke(invocation);
      }
      catch (Exception e)
      {
         if (e instanceof PortletInvokerException)
         {
            throw (PortletInvokerException)e;
         }
         else if (e instanceof RuntimeException)
         {
            throw (RuntimeException)e;
         }
         else
         {
            throw new PortletInvokerException(e);
         }
      }
      finally
      {
         invocation.removeAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
      }
   }

   public PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletImpl portlet = (PortletImpl)portlets.get(portletContext.getId());
      if (portlet == null)
      {
         throw new NoSuchPortletException(portletContext.getId());
      }
      ContainerPortletInfo info = (ContainerPortletInfo)portlet.getInfo();
      ContainerPreferencesInfo prefs = (ContainerPreferencesInfo)info.getPreferences();
      PropertyMap result = new SimplePropertyMap();
      for (String key : keys)
      {
         ContainerPreferenceInfo pref = prefs.getContainerPreference(key);
         if (pref != null)
         {
            result.put(key, pref.getDefaultValue());
         }
      }
      return result;
   }

   public PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletImpl portlet = (PortletImpl)portlets.get(portletContext.getId());
      if (portlet == null)
      {
         throw new NoSuchPortletException(portletContext.getId());
      }
      ContainerPortletInfo info = (ContainerPortletInfo)portlet.getInfo();
      ContainerPreferencesInfo prefs = (ContainerPreferencesInfo)info.getPreferences();
      PropertyMap result = new SimplePropertyMap();
      for (String key : prefs.getKeys())
      {
         ContainerPreferenceInfo pref = prefs.getContainerPreference(key);
         if (pref != null)
         {
            result.put(key, pref.getDefaultValue());
         }
      }
      return result;
   }

   public PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   public List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   public PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      throw new UnsupportedOperationException();
   }

   private static class PortletImpl implements Portlet
   {

      /** . */
      private final PortletContainer container;

      /** . */
      private final PortletContext context;

      public PortletImpl(PortletContainer container)
      {
         this.container = container;
         this.context = PortletContext.createPortletContext(container.getPortletApplication().getId() + "." + container.getId());
         //this.context = PortletContext.createPortletContext(container.getPortletApplication().getId().substring(1) + "/" + container.getId());
      }

      public PortletContext getContext()
      {
         return context;
      }

      public PortletInfo getInfo()
      {
         return container.getInfo();
      }

      public boolean isRemote()
      {
         return false;
      }

      public String toString()
      {
         return "Portlet[context=" + context + ",container=" + container + "]";
      }
   }
}