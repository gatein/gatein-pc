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

import org.gatein.pc.api.cache.CacheLevel;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public interface ResourceURL extends ContainerURL
{

   /**
    * Returns the resource id
    *
    * @return the resource id
    */
   String getResourceId();

   /**
    * Returns the resource state.
    *
    * @return the resource state
    */
   StateString getResourceState();

   /**
    * Returns the resource cacheability.
    *
    * @return the resource cacheability
    */
   CacheLevel getCacheability();

   /**
    * Return the navigational state that may be null.
    *
    * @return the navigational state
    */
   StateString getNavigationalState();

   /**
    * Returns the mode that may be null.
    *
    * @return the mode
    */
   Mode getMode();

   /**
    * Returns the window state that may be null.
    *
    * @return the window state
    */
   WindowState getWindowState();
}
