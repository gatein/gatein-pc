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
package org.gatein.pc.controller.state;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.StateString;

import java.io.Serializable;

/**
 * The navigational state of a window that contains the portlet navigational state, the mode and window state.
 * This class is immutable.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletWindowNavigationalState implements Serializable
{

   /** . */
   private final StateString portletNavigationalState;

   /** . */
   private final org.gatein.pc.api.Mode mode;

   /** . */
   private final org.gatein.pc.api.WindowState windowState;

   public PortletWindowNavigationalState()
   {
      this.portletNavigationalState = null;
      this.mode = org.gatein.pc.api.Mode.VIEW;
      this.windowState = org.gatein.pc.api.WindowState.NORMAL;
   }

   public PortletWindowNavigationalState(StateString portletNavigationalState, org.gatein.pc.api.Mode mode, org.gatein.pc.api.WindowState windowState)
   {
      this.portletNavigationalState = portletNavigationalState;
      this.mode = mode;
      this.windowState = windowState;
   }

   public StateString getPortletNavigationalState()
   {
      return portletNavigationalState;
   }

   public Mode getMode()
   {
      return mode;
   }

   public org.gatein.pc.api.WindowState getWindowState()
   {
      return windowState;
   }
}
