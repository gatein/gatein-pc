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
package org.gatein.pc.portlet;

import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletStateType;

import java.util.Set;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base class for  {@link org.gatein.pc.api.PortletInvoker} interface interceptors. The subclasses
 * extends it and override the intercepted methods. The next interceptor in the chain is wired in the field
 * {@link #next} of the interceptor. When the interceptor wants to give control to the next interceptor, it must
 * invoke the same method on the super class. If no next interceptor is configured the invocation of the parent
 * method will throw an {@link IllegalStateException}. 
 *
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletInvokerInterceptor implements PortletInvoker
{

   /** . */
   private final AtomicReference<PortletInvoker> next = new AtomicReference<PortletInvoker>();

   public PortletInvokerInterceptor()
   {
   }

   public PortletInvokerInterceptor(PortletInvoker next)
   {
      this.next.set(next);
   }

   public PortletInvoker getNext()
   {
      return next.get();
   }

   public void setNext(PortletInvoker next)
   {
      this.next.set(next);
   }

   public Set<Portlet> getPortlets() throws PortletInvokerException
   {
      return safeGetNext().getPortlets();
   }

   public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      return safeGetNext().getPortlet(portletContext);
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      return safeGetNext().invoke(invocation);
   }

   public PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      return safeGetNext().createClone(stateType, portletContext);
   }

   public List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      return safeGetNext().destroyClones(portletContexts);
   }

   public PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      return safeGetNext().getProperties(portletContext, keys);
   }

   public PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      return safeGetNext().getProperties(portletContext);
   }

   public PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      return safeGetNext().setProperties(portletContext, changes);
   }

   /**
    * Attempt to get the next invoker, the method never returns a null value and rather throws an {@link IllegalStateException}
    * if the next invoker cannot be obtained.
    *
    * @return the non null next invoker
    */
   private PortletInvoker safeGetNext()
   {
      PortletInvoker next = this.next.get();

      //
      if (next == null)
      {
         throw new IllegalStateException("No next invoker");
      }

      //
      return next;
   }
}
