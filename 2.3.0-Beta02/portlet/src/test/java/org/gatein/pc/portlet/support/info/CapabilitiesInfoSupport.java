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
package org.gatein.pc.portlet.support.info;

import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;
import org.gatein.pc.api.info.WindowStateInfo;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.common.net.media.MediaType;

import java.util.Set;
import java.util.HashSet;
import java.util.Locale;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class CapabilitiesInfoSupport implements CapabilitiesInfo
{

   /** . */
   private Set<MediaType> mediaTypes;

   /** . */
   private Set<ModeInfo> modes;

   /** . */
   private Set<WindowStateInfo> windowStates;

   /** . */
   private Set<Locale> locales;

   public CapabilitiesInfoSupport()
   {
      mediaTypes = new HashSet<MediaType>();
      mediaTypes.add(MediaType.TEXT_HTML);

      //
      this.modes = new HashSet<ModeInfo>();
      modes.add(new ModeInfoSupport(org.gatein.pc.api.Mode.VIEW));
      modes.add(new ModeInfoSupport(org.gatein.pc.api.Mode.EDIT));
      modes.add(new ModeInfoSupport(org.gatein.pc.api.Mode.HELP));

      //
      this.windowStates = new HashSet<WindowStateInfo>();
      windowStates.add(new WindowStateInfoSupport(org.gatein.pc.api.WindowState.NORMAL));
      windowStates.add(new WindowStateInfoSupport(WindowState.MAXIMIZED));
      windowStates.add(new WindowStateInfoSupport(org.gatein.pc.api.WindowState.MINIMIZED));

      //
      locales = new HashSet<Locale>();
      locales.add(Locale.ENGLISH);
   }

   public Set<MediaType> getMediaTypes()
   {
      return mediaTypes;
   }

   public Set<ModeInfo> getAllModes()
   {
      return modes;
   }

   public Set<ModeInfo> getModes(MediaType mimeType)
   {
      if (mediaTypes.contains(mimeType))
      {
         return modes;
      }
      return Collections.emptySet();
   }

   public ModeInfo getMode(Mode value)
   {
      for (ModeInfo mode : getAllModes())
      {
         if (mode.getMode().equals(value))
         {
            return mode;
         }
      }

      //
      return null;
   }

   public Set<WindowStateInfo> getAllWindowStates()
   {
      return windowStates;
   }

   public Set<WindowStateInfo> getWindowStates(MediaType mimeType)
   {
      if (mediaTypes.contains(mimeType))
      {
         return windowStates;
      }
      return Collections.emptySet();
   }

   public Set<Locale> getAllLocales()
   {
      return locales;
   }

   public WindowStateInfo getWindowState(org.gatein.pc.api.WindowState value)
   {
      for (WindowStateInfo windowState : getAllWindowStates())
      {
         if (windowState.getWindowState().equals(value))
         {
            return windowState;
         }
      }

      //
      return null;
   }

   public Set<Locale> getLocales(MediaType mimeType)
   {
      if (mediaTypes.contains(mimeType))
      {
         return locales;
      }
      return Collections.emptySet();
   }
}
