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

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.info.WindowStateInfo;

import java.util.Locale;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6818 $
 * @since 2.4
 */
class ContainerWindowStateInfo implements WindowStateInfo
{

   /** . */
   private static final LocalizedString DEFAULT_DESCRIPTION = new LocalizedString("Default window state description.", Locale.ENGLISH);

   /** . */
   private final org.gatein.pc.api.WindowState windowState;

   /** . */
   private final LocalizedString description;

   public ContainerWindowStateInfo(org.gatein.pc.api.WindowState windowState, LocalizedString description)
   {
      if (windowState == null)
      {
         throw new IllegalArgumentException("Specified window state cannot be null!");
      }
      if (description == null)
      {
         throw new IllegalArgumentException("Specified description cannot be null!");
      }

      //
      this.windowState = windowState;
      this.description = description;
   }

   public ContainerWindowStateInfo(org.gatein.pc.api.WindowState windowState)
   {
      this(windowState, DEFAULT_DESCRIPTION);
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

      ContainerWindowStateInfo that = (ContainerWindowStateInfo)o;

      return windowState.equals(that.windowState);

   }

   public int hashCode()
   {
      return windowState.hashCode();
   }

   public LocalizedString getDescription()
   {
      return description;
   }

   public org.gatein.pc.api.WindowState getWindowState()
   {
      return windowState;
   }

   public String getWindowStateName()
   {
      return windowState.toString();
   }
}