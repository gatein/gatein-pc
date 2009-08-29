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
package org.gatein.pc.portlet.impl.spi;

import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.spi.PortalContext;
import org.gatein.common.util.Tools;

import java.util.Set;
import java.util.Map;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class AbstractPortalContext implements PortalContext
{

   /** . */
   private static final Map<String, String> EMPTY_STRING_TO_STRING_MAP = Collections.emptyMap();

   /** . */
   private static final Set<WindowState> ALL_WINDOW_STATES = Collections.unmodifiableSet(Tools.toSet(WindowState.MAXIMIZED, WindowState.MINIMIZED, WindowState.NORMAL));

   /** . */
   private static final Set<org.gatein.pc.api.Mode> ALL_MODES = Collections.unmodifiableSet(Tools.toSet(org.gatein.pc.api.Mode.EDIT, Mode.HELP, org.gatein.pc.api.Mode.VIEW));

   /** . */
   private final Set<WindowState> windowStates;

   /** . */
   private final Set<org.gatein.pc.api.Mode> modes;

   /** . */
   private final Map<String, String> props;

   public AbstractPortalContext(Set<WindowState> windowStates, Set<org.gatein.pc.api.Mode> modes, Map<String, String> props)
   {
      if (windowStates == null)
      {
         throw new IllegalArgumentException("No window states provided");
      }
      if (modes == null)
      {
         throw new IllegalArgumentException("No modes provided");
      }
      if (props == null)
      {
         throw new IllegalArgumentException("No properties provided");
      }
      this.windowStates = windowStates;
      this.modes = modes;
      this.props = props;
   }

   public AbstractPortalContext(Map<String, String> props)
   {
      this(ALL_WINDOW_STATES, ALL_MODES, props);
   }

   public AbstractPortalContext()
   {
      this(EMPTY_STRING_TO_STRING_MAP);
   }

   public String getInfo()
   {
      return "JBossPortal/1.0";
   }

   public Set<WindowState> getWindowStates()
   {
      return windowStates;
   }

   public Set<org.gatein.pc.api.Mode> getModes()
   {
      return modes;
   }

   public Map<String, String> getProperties()
   {
      return props;
   }
}
