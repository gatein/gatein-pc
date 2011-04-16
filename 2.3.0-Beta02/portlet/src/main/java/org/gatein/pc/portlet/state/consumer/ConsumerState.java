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
package org.gatein.pc.portlet.state.consumer;

import org.gatein.pc.api.PortletStateType;

import java.util.Date;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5776 $
 */
public class ConsumerState<S extends Serializable>
{

   /** . */
   private final String portletId;

   /** . */
   private final PortletStateType<S> stateType;

   /** . */
   private final S state;

   /** . */
   private final Date terminationTime;

  public ConsumerState(String porteltId, PortletStateType<S> stateType, S state)
   {
      if (porteltId == null)
      {
         throw new IllegalArgumentException("No portlet id provided");
      }
      if (state == null)
      {
         throw new IllegalArgumentException("No bytes provided");
      }
      this.portletId = porteltId;
      this.stateType = stateType;
      this.state = state;
      this.terminationTime = null;
   }

   /**
    *
    */
   public String getPortletId()
   {
      return portletId;
   }

   public PortletStateType<S> getStateType()
   {
     return stateType;
   }

   /**
    *
    */
   public S getState()
   {
      return state;
   }

   /**
    * The scheduled termination time.
    *
    * @return the termination time
    */
   public Date getTerminationTime()
   {
      return terminationTime;
   }
}
