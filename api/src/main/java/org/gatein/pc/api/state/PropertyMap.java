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
package org.gatein.pc.api.state;

import java.util.Map;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6643 $
 */
public interface PropertyMap extends Map<String, List<String>>
{
   /**
    * Return the value for the given key or null if it does not exist.
    *
    * @param key the requested key
    * @return the requested value or null if it does not exist
    * @throws IllegalArgumentException if the key is null
    */
   List<String> getProperty(String key) throws IllegalArgumentException;

   /**
    * Update the value of the given key. If the value object is null it means that the entry must be removed.
    * Implementation can throw an unsupported operation exception when it is abnormal to perform an update.
    *
    * @param key   the key to update
    * @param value the new value
    * @throws UnsupportedOperationException if the operation is not supported
    * @throws IllegalArgumentException      if the key is null
    */
   void setProperty(String key, List<String> value) throws IllegalArgumentException, UnsupportedOperationException;
}
