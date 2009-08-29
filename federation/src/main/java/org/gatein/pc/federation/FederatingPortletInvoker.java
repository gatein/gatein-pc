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
package org.gatein.pc.federation;

import org.gatein.pc.api.PortletInvoker;

import java.util.Collection;

/**
 * A portlet invoker that federates other invokers.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5687 $
 * @since 2.4
 */
public interface FederatingPortletInvoker extends PortletInvoker
{

   /** The identifier assigned to the local PortletInvoker. */
   String LOCAL_PORTLET_INVOKER_ID = "local";

   /**
    * Registers an invoker.
    *
    * @param federatedId the invoker id to register
    * @param registeredInvoker the invoker to register
    * @throws IllegalArgumentException if the invoker is null or already registered
    */
   FederatedPortletInvoker registerInvoker(String federatedId, PortletInvoker registeredInvoker) throws IllegalArgumentException;

   /**
    * Return a portlet invoker registered or null if not found
    *
    * @param federatedId the id
    * @return the invoker
    * @throws IllegalArgumentException if the id is null
    */
   FederatedPortletInvoker getFederatedInvoker(String federatedId) throws IllegalArgumentException;

   /**
    * Return the registered portlet invokers.
    *
    * @return a collection that contains the portlet invokers
    */
   Collection<FederatedPortletInvoker> getFederatedInvokers();

   /**
    * Unregisters the invoker associated with the specified identifier.
    *
    * @param federatedId the identifier of the invoker to unregister
    * @throws IllegalArgumentException if the identifier is null or no invoker is registered with this identifier
    * @since 2.6
    */
   void unregisterInvoker(String federatedId);
}
