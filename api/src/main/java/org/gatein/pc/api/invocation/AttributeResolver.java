/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.api.invocation;

import java.util.Set;

/**
 * An attribute resolver.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7228 $
 */
public interface AttributeResolver<K, V>
{
   /**
    * Returns the set of keys of the attributes bound in that resolver.
    *
    * @return a set of keys
    */
   Set<K> getKeys();

   /**
    * Return an attribute from this resolver.
    *
    * @param attrKey
    * @return the attribute value or null if it is not found
    * @throws IllegalArgumentException if the attribute key is not valid
    */
   V getAttribute(K attrKey) throws IllegalArgumentException;

   /**
    * Update an attribute value on this resolve. If the attribute value is null the resolver must treat the operation as
    * a removal of the attribute.
    *
    * @param attrKey
    * @param attrValue the attribute value
    * @throws IllegalArgumentException if the attribute key is not valid
    */
   void setAttribute(K attrKey, V attrValue) throws IllegalArgumentException;
}
