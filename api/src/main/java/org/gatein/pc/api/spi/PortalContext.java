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
package org.gatein.pc.api.spi;

import org.gatein.pc.api.WindowState;
import org.gatein.common.util.Version;

import java.util.Map;
import java.util.Set;

/**
 * Represent the context of the portal that performs the invocation.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6720 $
 */
public interface PortalContext
{
   public static final Version VERSION = new Version("JBoss Portlet Container", 2, 0, 8, new Version.Qualifier(Version.Qualifier.Prefix.SNAPSHOT), "Community");

   /**
    * Return info about the portal.
    *
    * @return portal infos
    */
   String getInfo();

   /**
    * Return the window states accepted by this portal context.
    *
    * @return the window states
    */
   Set<WindowState> getWindowStates();

   /**
    * Return the modes accepted by this portal context.
    *
    * @return the modes
    */
   Set<org.gatein.pc.api.Mode> getModes();

   /**
    * Return the set of properties of this portal context.
    *
    * @return the properties
    */
   Map<String, String> getProperties();
}
