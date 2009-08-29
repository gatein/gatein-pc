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
package org.gatein.pc.api;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6901 $
 */
public final class WindowState implements Serializable
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -6305311518934458562L;

   /** . */
   public static final WindowState NORMAL = new WindowState("normal");

   /** . */
   public static final WindowState MINIMIZED = new WindowState("minimized");

   /** . */
   public static final WindowState MAXIMIZED = new WindowState("maximized");

   /** . */
   private String name;

   public WindowState(String name)
   {
      this(name, false);
   }

   /**
    * @param name
    * @param preserveCase
    * @since 2.4.2
    */
   private WindowState(String name, boolean preserveCase)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Window state name cannot be null");
      }

      this.name = (preserveCase ? name : name.toLowerCase(Locale.ENGLISH));
   }

   public boolean equals(Object o)
   {
      if (o == this)
      {
         return true;
      }
      if (o instanceof WindowState)
      {
         WindowState that = (WindowState)o;
         return name.equals(that.name);
      }
      return false;
   }

   public int hashCode()
   {
      return name.hashCode();
   }

   public String toString()
   {
      return name;
   }

   private Object readResolve()
   {
      if (NORMAL.name.equals(name))
      {
         return NORMAL;
      }
      else if (MAXIMIZED.name.equals(name))
      {
         return MAXIMIZED;
      }
      else if (MINIMIZED.name.equals(name))
      {
         return MINIMIZED;
      }
      else
      {
         return this;
      }
   }

   public static WindowState create(String name)
   {
      return create(name, false);
   }

   /**
    * @param name
    * @param preserveCase
    * @return
    * @since 2.4.2
    */
   public static WindowState create(String name, boolean preserveCase)
   {
      if (WindowState.NORMAL.name.equals(name))
      {
         return WindowState.NORMAL;
      }
      else if (WindowState.MINIMIZED.name.equals(name))
      {
         return WindowState.MINIMIZED;
      }
      else if (WindowState.MAXIMIZED.name.equals(name))
      {
         return WindowState.MAXIMIZED;
      }
      else
      {
         return new WindowState(name, preserveCase);
      }
   }
}