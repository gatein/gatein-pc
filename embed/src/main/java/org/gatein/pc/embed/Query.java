/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.embed;

import java.util.HashMap;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class Query extends Chunk
{

   /** . */
   final Map<String, String[]> parameters;

   Query(Map<String, String[]> parameters)
   {
      this.parameters = parameters;
   }

   Query()
   {
      this(new HashMap<String, String[]>());
   }

   Chunk put(String key, String value)
   {
      parameters.put(key, new String[]{value});
      return this;
   }

   @Override
   protected void writeTo(StringBuilder sb, String amp)
   {
      if (parameters.size() > 0)
      {
         String previous = "?";
         for (Map.Entry<String, String[]> parameter : parameters.entrySet())
         {
            String name = parameter.getKey();
            for (String value : parameter.getValue())
            {
               sb.append(previous);
               encode(sb, name, QUERY_PARAM);
               sb.append('=');
               encode(sb, value, QUERY_PARAM);
               previous = amp;
            }
         }
      }
   }
}
