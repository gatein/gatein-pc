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
package org.gatein.pc.portlet.support;

import org.gatein.pc.api.InvalidPortletIdException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.support.info.PortletInfoSupport;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.portlet.state.SimplePropertyMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6712 $
 */
public class PortletInvokerSupport implements PortletInvoker
{

   /** . */
   private Map<String, PortletSupport> portlets;

   public PortletInvokerSupport()
   {
      this.portlets = new HashMap<String, PortletSupport>();
   }

   public void setValid(String portletId, boolean valid)
   {
      getPortlet(portletId).valid = valid;
   }

   public PortletSupport addPortlet(String portletId)
   {
      return addPortlet(portletId, new PortletInfoSupport());
   }

   public PortletSupport addPortlet(String portletId, PortletInfoSupport info)
   {
      if (portletId == null)
      {
         throw new IllegalArgumentException();
      }
      if (info == null)
      {
         throw new IllegalArgumentException();
      }

      //
      PortletSupport portlet = new PortletSupport(portletId, info);

      //
      if (portlets.put(portletId, portlet) != null)
      {
         throw new IllegalStateException();
      }

      //
      return portlet;
   }

   public PortletInvokerSupport removePortlet(String portletId)
   {
      if (portlets.remove(portletId) == null)
      {
         throw new IllegalStateException();
      }
      return this;
   }

   /**
    * Returns a portlet or null if it is not found. The portlet is returned whether it is tagged as valid or not.
    * This method is not equivalent to the <code>getPortlet(PortletContext)</code> method which returns a portlet
    * only if that one is valid. It should be used for configuration purposes.
    *
    * @param portletId the portlet id
    * @return the portlet
    * @throws IllegalArgumentException if the portlet id is null
    */
   public PortletSupport getPortlet(String portletId) throws IllegalArgumentException
   {
      if (portletId == null)
      {
         throw new IllegalArgumentException();
      }

      //
      return portlets.get(portletId);
   }

   public Set<Portlet> getPortlets()
   {
      return new HashSet<Portlet>(portlets.values());
   }

   public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      return internalGetPortlet(portletContext);
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException
   {
      PortletContext portletContext = invocation.getTarget();
      PortletSupport portlet = internalGetPortlet(portletContext);
      return portlet.invoke(invocation);
   }

   private PortletSupport internalGetPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException();
      }

      //
      String portletId = portletContext.getId();

      //
      PortletSupport portlet = portlets.get(portletId);

      //
      if (portlet == null)
      {
         throw new NoSuchPortletException(portletId);
      }

      //
      if (!portlet.valid)
      {
         throw new InvalidPortletIdException(portletId);
      }

      //
      return portlet;
   }

   public PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletSupport internalPortlet = internalGetPortlet(portletContext);
      PropertyMap props = new SimplePropertyMap();
      for (String key : keys)
      {
         List<String> value = internalPortlet.state.get(key);
         if (value != null)
         {
            props.put(key, new ArrayList<String>(value));
         }
      }
      return props;
   }

   public PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletSupport internalPortlet = internalGetPortlet(portletContext);
      PropertyMap props = new SimplePropertyMap();
      for (String key: internalPortlet.state.keySet())
      {
         List<String> value = internalPortlet.state.get(key);
         if (value != null)
         {
            props.put(key, new ArrayList<String>(value));
         }
      }
      return props;
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
}
