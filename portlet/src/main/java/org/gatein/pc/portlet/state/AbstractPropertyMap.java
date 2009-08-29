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
package org.gatein.pc.portlet.state;

import org.gatein.common.util.TypedMap;
import org.gatein.pc.api.state.PropertyMap;

import java.util.Map;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6643 $
 */
public class AbstractPropertyMap<IK, IV> extends TypedMap<String, List<String>, IK, IV> implements PropertyMap
{

   public AbstractPropertyMap(Map<IK, IV> map, Converter<String, IK> keyConverter, Converter<List<String>, IV> valueConverter)
   {
      super(map, keyConverter, valueConverter);
   }

   public List<String> getProperty(String key) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("No null key accepted");
      }
      return get(key);
   }

   public void setProperty(String key, List<String> value) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("No null key accepted");
      }
      if (value != null)
      {
         put(key, value);
      }
      else
      {
         remove(key);
      }
   }
}
