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

package org.gatein.pc.portlet.impl.info;

import org.gatein.pc.api.Mode;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.info.ModeInfo;

import java.util.Locale;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6818 $
 * @since 2.4
 */
class ContainerModeInfo implements ModeInfo
{

   /** . */
   private static final LocalizedString DEFAULT_DESCRIPTION = new LocalizedString("Default Portlet mode description.", Locale.ENGLISH);

   /** . */
   private final org.gatein.pc.api.Mode mode;

   /** . */
   private final LocalizedString description;

   public ContainerModeInfo(org.gatein.pc.api.Mode mode, LocalizedString description)
   {
      if (mode == null)
      {
         throw new IllegalArgumentException("Specified mode cannot be null!");
      }
      if (description == null)
      {
         throw new IllegalArgumentException("Specified description cannot be null!");
      }

      //
      this.mode = mode;
      this.description = description;
   }

   public ContainerModeInfo(Mode mode)
   {
      this(mode, DEFAULT_DESCRIPTION);
   }

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

      ContainerModeInfo that = (ContainerModeInfo)o;

      return mode.equals(that.mode);
   }

   public int hashCode()
   {
      return mode.hashCode();
   }

   public LocalizedString getDescription()
   {
      return DEFAULT_DESCRIPTION; // fix-me
   }

   public org.gatein.pc.api.Mode getMode()
   {
      return mode;
   }

   public String getModeName()
   {
      return mode.toString();
   }
}