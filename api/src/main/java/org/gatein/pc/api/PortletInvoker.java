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
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyMap;

import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public interface PortletInvoker
{
   /** The identifier assigned to the local PortletInvoker. */
   String LOCAL_PORTLET_INVOKER_ID = "local";

   /**
    * Return the set of portlet exposed. Usually, this means only non-customized portlets.
    *
    * @return the set of exposed portlets
    * @throws PortletInvokerException a portlet invoker exception
    */
   Set<Portlet> getPortlets() throws PortletInvokerException;

   /**
    * Get information about a specific portlet. Note that this PortletInvoker can know about more portlets than returned
    * by {@link #getPortlets()}. In particular, cloned portlets wouldn't necessarily be exposed to getPortlets and still
    * be known by this PortletInvoker i.e. there exists PortletContexts <code>pc</code> as follows: <p> <code> assert
    * getPortlet(pc) != null && !getPortlets().contains(portlet);<br/> </code> </p>
    *
    * @param portletContext the portlet context in the scope of this invoker
    * @return the <code>PortletInfo</code> for the specified portlet
    * @throws IllegalArgumentException if the portlet context is null
    * @throws PortletInvokerException  a portlet invoker exception
    */
   Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Determines whether the specified PortletContext is part of the set of exposed Portlets as returned by {@link
    * #getPortlets()}.
    *
    * @param portletContext the PortletContext which exposed status we want to determine
    * @return <code>true</code> if the Portlet associated with the specified PortletContext is exposed by this
    *         PortletInvoker, <code>false</code> otherwise
    * @throws IllegalArgumentException if the specified PortletContext is null
    * @throws PortletInvokerException  if some other error occurs
    */
   boolean isExposed(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Determines whether the Portlet associated with the specified PortletContext (if it exists) is known to this
    * PortletInvoker whether it is exposed or not. In particular, if this method returns <code>true</code> then {@link
    * #getPortlet(PortletContext)} will return a valid Portlet.
    *
    * @param portletContext the PortletContext to check
    * @return <code>true</code>
    * @throws IllegalArgumentException
    * @throws PortletInvokerException
    */
   boolean isKnown(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Invoke an operation on a specific portlet.
    *
    * @param invocation the portlet invocation
    * @return the invocation response
    * @throws IllegalArgumentException if the invocation is null
    * @throws PortletInvokerException  a portlet invoker exception
    */
   PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException;

   /**
    * Clone a portlet.
    *
    * @param stateType      the portlet state type desired
    * @param portletContext the portlet context to clone  @return the clone id
    * @return the cloned portlet context
    * @throws IllegalArgumentException      if the portletId is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException       a portlet invoker exception
    */
   PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Destroy a cloned portlet.
    *
    * @param portletContexts a list of portlet contexts to destroy
    * @return a list of {@link org.gatein.pc.api.state.DestroyCloneFailure}, one per clone that couldn't be destroyed
    * @throws IllegalArgumentException      if the portletContext is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException       a portlet invoker exception
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
    * @throws PortletInvokerException       a portlet invoker exception
    */
   PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Return all the properties of the specified portlet.
    *
    * @param portletContext the portlet context
    * @return the properties
    * @throws IllegalArgumentException      if the portletContext is null
    * @throws UnsupportedOperationException if the invoker does not support this operation
    * @throws PortletInvokerException       a portlet invoker exception
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
    * @throws PortletInvokerException       a portlet invoker exception
    */
   PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException;

   /**
    * Exports a portlet from the invoker which can be used to recreate this portlet during an import portlet operation
    * The returned portlet Id will be the portlet Id of the base portlet, not a cloned portlet Id If the portlet
    * contains state, it will be returned regardless if the portlet invoker is set to persist state locally.
    *
    * @param stateType              the portlet state type desired
    * @param originalPortletContext the context of the porlet to be exported
    * @return A new portlet context which can be used to import a portlet
    * @throws PortletInvokerException
    */
   PortletContext exportPortlet(PortletStateType stateType, PortletContext originalPortletContext) throws PortletInvokerException;

   /**
    * Imports a portlet into the invoker.
    *
    * @param stateType       the portlet state type desired
    * @param contextToImport the context to be imported
    * @return The portletcontext for the imported portlet
    * @throws PortletInvokerException
    */
   PortletContext importPortlet(PortletStateType stateType, PortletContext contextToImport) throws PortletInvokerException;
}
