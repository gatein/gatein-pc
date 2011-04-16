/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;

import java.util.Set;

/**
 * Combines the invocation of a portlet and the info on the portlet JSP tag. It does
 * not need to be exposed out of this package.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class WindowDef
{

   /** . */
   private final String portletName;

   /** . */
   private final String applicationName;

   /** . */
   private final String windowId;

   /** . */
   private final Mode initialMode;

   /** . */
   private final Set<Mode> supportedModes;

   /** . */
   private final Set<org.gatein.pc.api.WindowState> supportedWindowStates;

   public WindowDef(
      String portletName,
      String applicationName,
      String windowId,
      Mode initialMode,
      Set<Mode> supportedModes,
      Set<WindowState> supportedWindowStates)
   {
      this.portletName = portletName;
      this.applicationName = applicationName;
      this.windowId = windowId;
      this.initialMode = initialMode;
      this.supportedModes = supportedModes;
      this.supportedWindowStates = supportedWindowStates;
   }

   public String getPortletName()
   {
      return portletName;
   }

   public String getApplicationName()
   {
      return applicationName;
   }

   public String getWindowId()
   {
      return windowId;
   }

   public Mode getInitialMode()
   {
      return initialMode;
   }

   public Set<Mode> getSupportedModes()
   {
      return supportedModes;
   }

   public Set<WindowState> getSupportedWindowStates()
   {
      return supportedWindowStates;
   }
}