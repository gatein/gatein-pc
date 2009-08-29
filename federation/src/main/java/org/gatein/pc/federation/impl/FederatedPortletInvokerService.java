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
package org.gatein.pc.federation.impl;

import org.gatein.common.invocation.InvocationException;
import org.gatein.pc.federation.FederatedPortletInvoker;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.StateEvent;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.InstanceContext;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.state.PropertyChange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5943 $
 * @since 2.4
 */
public class FederatedPortletInvokerService implements FederatedPortletInvoker
{

   /** . */
   private String id;

   /** . */
   private PortletInvoker portletInvoker;

   /** . */
   private FederatingPortletInvoker federatingPortletInvoker;

   public FederatedPortletInvokerService(FederatingPortletInvoker federatingPortletInvoker, String id, PortletInvoker portletInvoker)
   {
      this.id = id;
      this.portletInvoker = portletInvoker;
      this.federatingPortletInvoker = federatingPortletInvoker;
   }

   public PortletInvoker getPortletInvoker()
   {
      return portletInvoker;
   }

   public String getId()
   {
      return id;
   }

   public FederatingPortletInvoker getFederatingPortletInvoker()
   {
      return federatingPortletInvoker;
   }

   public Set<Portlet> getPortlets() throws PortletInvokerException
   {
      Set<Portlet> federatedPortlets = new LinkedHashSet<Portlet>();
      for (Portlet portlet : portletInvoker.getPortlets())
      {
         Portlet federatedPortlet = new FederatedPortlet(this, reference(portlet.getContext()), portlet);
         federatedPortlets.add(federatedPortlet);
      }
      return federatedPortlets;
   }

   public Portlet getPortlet(PortletContext compoundPortletContext) throws IllegalArgumentException, PortletInvokerException
   {
      // Get portlet context
      PortletContext portletContext = dereference(compoundPortletContext);

      // Retrieve wrapped portlet
      Portlet portlet = portletInvoker.getPortlet(portletContext);

      // Return correct result
      return new FederatedPortlet(this, compoundPortletContext, portlet);
   }

   private class FederatedInstanceContext implements InstanceContext
   {

      /** . */
      private InstanceContext ctx;

      public FederatedInstanceContext(InstanceContext ctx)
      {
         this.ctx = ctx;
      }

      public String getId()
      {
         return ctx.getId();
      }

      public AccessMode getAccessMode()
      {
         return ctx.getAccessMode();
      }

      public void onStateEvent(StateEvent event)
      {
         ctx.onStateEvent(new StateEvent(reference(event.getPortletContext()), event.getType()));
      }

      public PortletStateType<?> getStateType() {
         return ctx.getStateType();
      }
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws InvocationException, PortletInvokerException
   {
      PortletContext compoundPortletContext = invocation.getTarget();
      PortletContext portletContext = dereference(compoundPortletContext);
      InstanceContext instanceContext = invocation.getInstanceContext();
      try
      {
         invocation.setTarget(portletContext);
         invocation.setInstanceContext(new FederatedInstanceContext(instanceContext));
         return portletInvoker.invoke(invocation);
      }
      finally
      {
         invocation.setTarget(compoundPortletContext);
         invocation.setInstanceContext(instanceContext);
      }
   }

   public PortletContext createClone(PortletStateType stateType, PortletContext compoundPortletContext) throws PortletInvokerException
   {
      PortletContext portletContext = dereference(compoundPortletContext);
      PortletContext cloneContext = portletInvoker.createClone(stateType, portletContext);
      return reference(cloneContext);
   }

   public List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContexts == null)
      {
         throw new IllegalArgumentException("Null portlet id list not accepted");
      }
      if (portletContexts.size() == 0)
      {
         return Collections.emptyList();
      }

      //
      List<PortletContext> dereferencedList = new ArrayList<PortletContext>(portletContexts);
      for (int i = 0; i < dereferencedList.size(); i++)
      {
         PortletContext compoundPortletContext = dereferencedList.get(i);
         PortletContext portletContext = dereference(compoundPortletContext);
         dereferencedList.set(i, portletContext);
      }

      //
      List<DestroyCloneFailure> failures = portletInvoker.destroyClones(dereferencedList);
      for (int i = 0; i < failures.size(); i++)
      {
         DestroyCloneFailure failure = failures.get(i);
         String cloneId = failure.getPortletId();
         failure = new DestroyCloneFailure(reference(cloneId));
         failures.set(i, failure);
      }

      //
      return failures;
   }

   public PropertyMap getProperties(PortletContext compoundPortletContext) throws PortletInvokerException
   {
      PortletContext portletId = dereference(compoundPortletContext);
      return portletInvoker.getProperties(portletId);
   }

   public PropertyMap getProperties(PortletContext compoundPortletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletContext portletId = dereference(compoundPortletContext);
      return portletInvoker.getProperties(portletId, keys);
   }

   public PortletContext setProperties(PortletContext compoundPortletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      PortletContext portletContext = dereference(compoundPortletContext);
      portletContext = portletInvoker.setProperties(portletContext, changes);
      return reference(portletContext);
   }


   private PortletContext dereference(PortletContext compoundPortletContext)
   {
      String portletId = compoundPortletContext.getId().substring(id.length() + 1);
      if (compoundPortletContext instanceof StatefulPortletContext)
      {
         StatefulPortletContext<?> compoundStatefulPortletContext = (StatefulPortletContext<?>)compoundPortletContext;
         return StatefulPortletContext.create(portletId, compoundStatefulPortletContext);
      }
      else
      {
         return PortletContext.createPortletContext(portletId);
      }
   }

   private PortletContext reference(PortletContext portletContext)
   {
      String compoundPortletId = reference(portletContext.getId());
      if (portletContext instanceof StatefulPortletContext)
      {
         StatefulPortletContext<?> statefulPortletContext = (StatefulPortletContext<?>)portletContext;
         return StatefulPortletContext.create(compoundPortletId, statefulPortletContext);
      }
      else
      {
         return PortletContext.createPortletContext(compoundPortletId);
      }
   }

   private String reference(String portletId)
   {
      return id + FederatingPortletInvokerService.SEPARATOR + portletId;
   }
}
                        