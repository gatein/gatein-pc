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
package org.gatein.pc.api.invocation.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7228 $
 */
public abstract class AbstractSessionAttributeResolver implements AttributeResolver
{

   /** . */
   protected final HttpServletRequest req;

   public AbstractSessionAttributeResolver(HttpServletRequest req)
   {
      if (req == null)
      {
         throw new IllegalArgumentException();
      }
      this.req = req;
   }


   public Set getKeys()
   {
      Map map = getMap(false);

      //
      if (map != null)
      {
         return map.keySet();
      }
      else
      {
         return Collections.EMPTY_SET;
      }
   }

   public Object getAttribute(Object attrKey) throws IllegalArgumentException
   {
      if (attrKey == null)
      {
         throw new IllegalArgumentException();
      }

      //
      Object value = null;
      Map map = getMap(false);
      if (map != null)
      {
         value = map.get(attrKey);
      }
      return value;
   }

   public void setAttribute(Object attrKey, Object attrValue) throws IllegalArgumentException
   {
      if (attrKey == null)
      {
         throw new IllegalArgumentException();
      }

      //
      Map map = getMap(false);
      if (map != null)
      {
         if (attrValue != null)
         {
            map.put(attrKey, attrValue);
         }
         else
         {
            map.remove(attrKey);
         }
      }
      else
      {
         if (attrValue != null)
         {
            map = getMap(true);
            map.put(attrKey, attrValue);
         }
      }
   }

   protected abstract String getMapKey();

   protected Map createMap(String mapKey)
   {
      return new HashMap();
   }

   private Map getMap(boolean create)
   {
      HttpSession session = req.getSession(create);
      if (session != null)
      {
         String mapKey = getMapKey();
         Map map = (Map)session.getAttribute(mapKey);
         if (map == null)
         {
            map = createMap(mapKey);
            session.setAttribute(mapKey, map);
         }
         return map;
      }
      else
      {
         return null;
      }
   }
}