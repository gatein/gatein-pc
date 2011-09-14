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

import org.gatein.pc.api.NoSuchPortletException;

import java.util.Collection;
import java.util.Collections;

/**
 * Encapsulates behavior to resolve FederatedPortletInvokers in the context of a FederatingPortletInvoker when the
 * default resolution mechanism fails to retrieve an associated FederatedPortletInvoker.
 *
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public interface PortletInvokerResolver
{
   /**
    * Default handling mechanism: if we haven't found an invoker for the portlet id in the first place, throw
    * NoSuchPortletException if we specified a portlet id or null if we didn't.
    */
   PortletInvokerResolver DEFAULT_RESOLVER = new PortletInvokerResolver()
   {
      public FederatedPortletInvoker resolvePortletInvokerFor(String invokerId, FederatingPortletInvoker callingInvoker, String compoundPortletId) throws NoSuchPortletException
      {
         if (compoundPortletId != null)
         {
            throw new NoSuchPortletException(compoundPortletId);
         }
         else
         {
            return null;
         }
      }

      public boolean knows(String invokerId)
      {
         return false;
      }

      public Collection<String> getKnownInvokerIds()
      {
         return Collections.emptyList();
      }
   };

   /**
    * Attempts to resolve a FederatedPortletInvoker with the specified identifier in the context of the specified
    * calling FederatingPortletInvoker, optionally trying to perform resolution to invoke an action on the specified
    * portlet identifier.
    *
    * @param invokerId         the identifier of the FederatedPortletInvoker to be retrieved. Should match the optional
    *                          compound portlet identifier if one is specified.
    * @param callingInvoker    the calling FederatingPortletInvoker which failed to resolve a FederatedPortletInvoker
    *                          for the specified invoker identifier
    * @param compoundPortletId an optional portlet identifier for which we are trying to resolve an invoker, if no such
    *                          portlet identifier is required for the resolution, this argument should be
    *                          <code>null</code> and implementations should be prepared for that case.
    * @return the resolved FederatedPortletInvoker or <code>null</code> if one couldn't be found
    * @throws NoSuchPortletException if a PortletInvoker couldn't be found and a compound portlet identifier was
    *                                specified
    */
   FederatedPortletInvoker resolvePortletInvokerFor(String invokerId, FederatingPortletInvoker callingInvoker, String compoundPortletId) throws NoSuchPortletException;

   boolean knows(String invokerId);

   Collection<String> getKnownInvokerIds();
}
