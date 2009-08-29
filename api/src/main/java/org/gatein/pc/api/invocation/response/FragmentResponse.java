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
package org.gatein.pc.api.invocation.response;

import org.gatein.pc.api.cache.CacheControl;
import org.gatein.pc.api.Mode;

import java.util.Set;
import java.util.Map;

/**
 * Data produced.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5602 $
 */
public class FragmentResponse extends ContentResponse
{

   /** The title if any. */
   private final String title;

   /** The next modes. */
   private final Set<Mode> nextModes;

   public FragmentResponse(
      ResponseProperties properties,
      Map<String, Object> attributes,
      String contentType,
      byte[] bytes,
      String chars,
      String title,
      CacheControl cacheControl,
      Set<Mode> nextModes)
   {
      super(properties, attributes, contentType, bytes, chars, cacheControl);

      //
      this.title = title;
      this.nextModes = nextModes;
   }

   /**
    * Return the fragment title.
    *
    * @return the title.
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Returns the next modes.
    *
    * @return the next modes
    */
   public Set<org.gatein.pc.api.Mode> getNextModes()
   {
      return nextModes;
   }
}
