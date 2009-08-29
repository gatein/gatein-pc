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
package org.gatein.pc.controller.response;

import org.gatein.pc.api.invocation.response.PortletInvocationResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletResponse extends ControllerResponse
{

   /** The event distribution was properly done. */
   public static final int DISTRIBUTION_DONE = 0;

   /** The event distribution was interruped by the event controller context. */
   public static final int INTERRUPTED = 1;

   /** The event distribution did flood with produced events. */
   public static final int PRODUCED_EVENT_FLOODED = 2;

   /** The event distribution did flood with consumed events. */
   public static final int CONSUMED_EVENT_FLOODED = 3;

   /** . */
   private final PortletInvocationResponse response;

   /** . */
   private final int eventDistributionStatus;

   public PortletResponse(PortletInvocationResponse response, int eventDistributionStatus)
   {
      this.response = response;
      this.eventDistributionStatus = eventDistributionStatus;
   }

   public int getEventDistributionStatus()
   {
      return eventDistributionStatus;
   }

   public PortletInvocationResponse getResponse()
   {
      return response;
   }
}
