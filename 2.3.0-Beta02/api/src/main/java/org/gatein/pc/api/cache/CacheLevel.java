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
package org.gatein.pc.api.cache;

import org.gatein.common.util.ParameterValidation;

import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public final class CacheLevel implements Serializable
{
   public static final CacheLevel FULL = new CacheLevel("FULL");
   public static final CacheLevel PORTLET = new CacheLevel("PORTLET");
   public static final CacheLevel PAGE = new CacheLevel("PAGE");

   private static final long serialVersionUID = -7020875805659724988L;

   private final String name;

   private CacheLevel(String name)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(name, "CacheLevel name", null);
      this.name = name;
   }

   public final String name()
   {
      return name;
   }

   private Object readResolve()
   {
      CacheLevel standardCacheLevel = isStandardCacheLevel(name);
      if (standardCacheLevel != null)
      {
         return standardCacheLevel;
      }
      else
      {
         return this;
      }
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      CacheLevel that = (CacheLevel)o;

      return !(name == null ? that.name != null : !name.equals(that.name));
   }

   @Override
   public int hashCode()
   {
      return name != null ? name.hashCode() : 0;
   }

   public static CacheLevel create(String name)
   {
      CacheLevel standardCacheLevel = isStandardCacheLevel(name);
      if (standardCacheLevel != null)
      {
         return standardCacheLevel;
      }
      else
      {
         return new CacheLevel(name);
      }
   }

   private static CacheLevel isStandardCacheLevel(String name)
   {
      if (FULL.name.equals(name))
      {
         return FULL;
      }
      else if (PORTLET.name.equals(name))
      {
         return PORTLET;
      }
      else if (PAGE.name.equals(name))
      {
         return PAGE;
      }
      else
      {
         return null;
      }
   }
}
