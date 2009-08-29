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
package org.gatein.pc.api;

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.Portlet;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public interface PortletInvoker
{
   /**
    * Return the set of portlet exposed.
    *
    * @return the set of exposed portlets
    * @throws PortletInvokerException a portlet invoker exception
    */
   Set<Portlet> getPortlets() throws PortletInvokerException;

   /**
    * Get information about a specific portlet.
    *
    * @param portletContext the portlet context in the scope of this invoker
    * @return the <code>PortletInfo</code> for the specified portlet
    * @throws IllegalArgumentException if the portlet context is null
    * @throws PortletInvokerException a portlet invoker exception
    */
   Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Invoke an operation on a specific portlet.
    *
    * @param invocation the portlet invocation
    * @return the invocation response
    * @throws IllegalArgumentException if the invocation is null
    * @throws PortletInvokerException a portlet invoker exception
    */
   PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Clone a portlet.
    *
    * @param stateType the portle state type desired
    * @param portletContext the portlet context to clone  @return the clone id
    * @return the cloned portlet context
    * @throws IllegalArgumentException      if the portletId is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException a portlet invoker exception
    */
   PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Destroy a cloned portlet.
    *
    * @param portletContexts a list of portlet contexts to destroy
    * @return a list of {@link org.gatein.pc.api.state.DestroyCloneFailure}, one per clone that couldn't be
    *         destroyed
    * @throws IllegalArgumentException      if the portletContext is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException a portlet invoker exception
    */
   List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Return a subset of the properties of the specified portlet.
    *
    * @param portletContext the portlet context
    * @param keys           the set of keys to retrieve
    * @return the properties
    * @throws IllegalArgumentException      if the portletContext or the keys arguments are null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException a portlet invoker exception
    */
   PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Return all the properties of the specified portlet.
    *
    * @param portletContext the portlet context
    * @return the properties
    * @throws IllegalArgumentException      if the portletContext is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException a portlet invoker exception
    */
   PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Set the properties on the specified portlet.
    *
    * @param portletContext the portlet context
    * @param changes        the changes
    * @return the new portlet context
    * @throws IllegalArgumentException      if the portletContext or the properties is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException a portlet invoker exception
    */
   PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;
}
