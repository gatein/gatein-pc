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
 * @version $Revision: 7867 $
 */
public final class Mode implements Serializable
{

   /** The serialVersionUID. */
   private static final long serialVersionUID = 6033765240710422050L;

   /** . */
   public static final Mode EDIT = new Mode("edit");

   /** . */
   public static final Mode HELP = new Mode("help");

   /** . */
   public static final Mode VIEW = new Mode("view");

   /** . */
   public static final Mode EDIT_DEFAULTS = new Mode("edit_defaults");

   /** . */
   public static final Mode ADMIN = new Mode("admin");

   /** . */
   private String name;

   public Mode(String name)
   {
      this(name, false);
   }

   /**
    * @param name
    * @param preserveCase
    * @since 2.4.2
    */
   private Mode(String name, boolean preserveCase)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Mode cannot be null");
      }
      this.name = (preserveCase ? name : name.toLowerCase(Locale.ENGLISH));
   }


   public boolean equals(Object o)
   {
      if (o == this)
      {
         return true;
      }
      if (o instanceof Mode)
      {
         Mode that = (Mode)o;
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
      if (VIEW.name.equals(name))
      {
         return VIEW;
      }
      else if (EDIT.name.equals(name))
      {
         return EDIT;
      }
      else if (HELP.name.equals(name))
      {
         return HELP;
      }
      else if (EDIT_DEFAULTS.name.equals(name))
      {
         return EDIT_DEFAULTS;
      }
      else
      {
         return this;
      }
   }

   public static Mode create(String name)
   {
      return create(name, false);
   }

   /**
    * @param name
    * @param preserveCase
    * @return
    * @since 2.4.2
    */
   public static Mode create(String name, boolean preserveCase)
   {
      if (Mode.VIEW.name.equals(name))
      {
         return Mode.VIEW;
      }
      else if (Mode.EDIT.name.equals(name))
      {
         return Mode.EDIT;
      }
      else if (Mode.HELP.name.equals(name))
      {
         return Mode.HELP;
      }
      else if (Mode.ADMIN.name.equals(name))
      {
         return Mode.ADMIN;
      }
      else if (Mode.EDIT_DEFAULTS.name.equals(name))
      {
         return Mode.EDIT_DEFAULTS;
      }
      else
      {
         return new Mode(name, preserveCase);
      }
   }
}