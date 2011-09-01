/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.federation;

import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;

import java.util.Collection;
import java.util.Set;

/**
 * A portlet invoker that federates other invokers.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5687 $
 * @since 2.4
 */
public interface FederatingPortletInvoker extends PortletInvoker
{

   /**
    * Registers an invoker.
    *
    * @param federatedId       the invoker id to register
    * @param registeredInvoker the invoker to register
    * @throws IllegalArgumentException if the invoker is null or already registered
    */
   FederatedPortletInvoker registerInvoker(String federatedId, PortletInvoker registeredInvoker) throws IllegalArgumentException;

   /**
    * Returns the registered FederatedPortletInvoker associated with the specified identifier, delegating to the {@link
    * NullInvokerHandler} specified using {@link #setNullInvokerHandler(NullInvokerHandler)} if it's not initially
    * resolved or returns <code>null</code> if a FederatedPortletInvoker is not found after going through the specified
    * NullInvokerHandler resolution mechanism.
    *
    * @param federatedId the id
    * @return the invoker
    * @throws IllegalArgumentException if the id is null
    */
   FederatedPortletInvoker getFederatedInvoker(String federatedId) throws IllegalArgumentException;

   /**
    * Returns the known portlet invoker identifiers, including resolvable ones (though they might not have been already
    * resolved).
    *
    * @return a collection of all the resolvable portlet invoker identifiers
    */
   Collection<String> getFederatedInvokerIds();

   /**
    * Returns only the portlet invoker identifiers of the currently resolved invokers, which might be different from
    * the
    * set potentially resolvable invokers as returned by {@link #getFederatedInvokerIds()}.
    *
    * @return a collection of the currently resolved portlet invoker identifiers
    */
   Collection<String> getResolvedInvokerIds();

   /**
    * Determines whether the FederatedPortletInvoker with the specified identifier has already been resolved.
    *
    * @param federatedId the identifier of the FederatedPortletInvoker to test for resolution status
    * @return <code>true</code> if the specified invoker is already resolved, <code>false</code> otherwise
    * @throws IllegalArgumentException if the specified invoker identifier is not among the known resolvable
    *                                  identifiers
    *                                  as returned by {@link #getFederatedInvokerIds()}
    */
   boolean isResolved(String federatedId) throws IllegalArgumentException;

   /**
    * Unregisters the invoker associated with the specified identifier.
    *
    * @param federatedId the identifier of the invoker to unregister
    * @throws IllegalArgumentException if the identifier is null or no invoker is registered with this identifier
    * @since 2.6
    */
   void unregisterInvoker(String federatedId);

   /**
    * Return only the portlets from local federated invokers.
    *
    * @return a Set containing only the portlets from local federated invokers.
    */
   Set<Portlet> getLocalPortlets() throws PortletInvokerException;

   /**
    * Return only the portlets from remote federated invokers.
    *
    * @return a Set containing only the portlets from remote federated invokers.
    */
   Set<Portlet> getRemotePortlets() throws PortletInvokerException;

   /**
    * Specifies which NullInvokerHandler to use to attempt retrieval of a federated invoker when default resolution
    * mechanism cannot find an associated invoker.
    *
    * @param nullHandler
    */
   void setNullInvokerHandler(NullInvokerHandler nullHandler);
}
