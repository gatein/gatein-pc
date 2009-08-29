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

import org.gatein.pc.api.StateEvent;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.AccessMode;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public interface InstanceContext
{
   /**
    * Return an id that can differenciate instances.
    *
    * @return the instance id
    */
   String getId();

   /**
    * Return the access mode to this portlet instance.
    *
    * @return the access mode
    */
   AccessMode getAccessMode();

   /**
    * A state event occured.
    *
    * @param event the event
    */
   void onStateEvent(StateEvent event);

   /**
    * Returns the state type managed by the consumer. If the consumer cannot manage
    * state by itself, then null must be returned.
    *
    * @return the consumer state type
    */
   PortletStateType<?> getStateType();
}
