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

import org.apache.log4j.Logger;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.common.net.media.MediaType;
import org.gatein.common.net.media.MediaTypeMapImpl;
import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;
import org.gatein.pc.api.info.WindowStateInfo;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This object holds the content type and mode capabilities.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6700 $
 */
public class ContainerCapabilitiesInfo implements CapabilitiesInfo
{
   private final static Logger log = Logger.getLogger(ContainerCapabilitiesInfo.class);

   /** . */
   private final MediaTypeMapImpl<ModeInfo> supportedModes;

   /** . */
   private final MediaTypeMapImpl<WindowStateInfo> supportedWindowStates;

   /** . */
   private final Set<Locale> supportedLocales;

   public ContainerCapabilitiesInfo()
   {
      MediaTypeMapImpl<ModeInfo> supportedModes = new MediaTypeMapImpl<ModeInfo>();
      supportedModes.put(new ContainerModeInfo(Mode.VIEW));

      //
      MediaTypeMapImpl<WindowStateInfo> supportedWindowStates = new MediaTypeMapImpl<WindowStateInfo>();
      supportedWindowStates.put(new ContainerWindowStateInfo(org.gatein.pc.api.WindowState.MAXIMIZED));
      supportedWindowStates.put(new ContainerWindowStateInfo(org.gatein.pc.api.WindowState.NORMAL));
      supportedWindowStates.put(new ContainerWindowStateInfo(org.gatein.pc.api.WindowState.MINIMIZED));

      //
      this.supportedLocales = new HashSet<Locale>();
      this.supportedModes = supportedModes;
      this.supportedWindowStates = supportedWindowStates;
   }

   public void addLocale(Locale locale)
   {
      supportedLocales.add(locale);
   }

   public void add(String contentType, Mode mode)
   {
      add(contentType, new ContainerModeInfo(mode));
   }

   public void add(String contentType, ContainerModeInfo mode)
   {
      try
      {
         supportedModes.put(contentType, mode);
      }
      catch (IllegalArgumentException e)
      {
         log.debug("'" + contentType + "' is not a valid MIME type: ignoring!", e);
      }
   }

   public void add(String contentType, org.gatein.pc.api.WindowState windowState)
   {
      add(contentType, new ContainerWindowStateInfo(windowState));
   }

   public void add(String contentType, ContainerWindowStateInfo windowState)
   {
      try
      {
         supportedWindowStates.put(contentType, windowState);
      }
      catch (IllegalArgumentException e)
      {
         log.debug("'" + contentType + "' is not a valid MIME type: ignoring!", e);
      }
   }

   // CapabilitiesInfo implementation **********************************************************************************

   public Set<ModeInfo> getModes(MediaType mimeType)
   {
      return supportedModes.resolve(mimeType);
   }

   public Set<WindowStateInfo> getWindowStates(MediaType mimeType)
   {
      return supportedWindowStates.resolve(mimeType);
   }

   public Set<Locale> getLocales(MediaType mimeType)
   {
      return supportedLocales;
   }

   public Set<MediaType> getMediaTypes()
   {
      return supportedModes.getMediaTypes();
   }

   public Set<ModeInfo> getAllModes()
   {
      return supportedModes.getValues();
   }

   public ModeInfo getMode(org.gatein.pc.api.Mode value)
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
      return supportedWindowStates.getValues();
   }

   public WindowStateInfo getWindowState(WindowState value)
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

   public Set<Locale> getAllLocales()
   {
      return supportedLocales;
   }
}