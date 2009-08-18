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

import org.gatein.pc.api.invocation.response.PortletInvocationResponse;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventAcknowledgement
{

   public static class Consumed extends EventAcknowledgement
   {

      /** . */
      private final PortletInvocationResponse response;

      public Consumed(PortletInvocationResponse response)
      {
         this.response = response;
      }

      public PortletInvocationResponse getResponse()
      {
         return response;
      }

      public String toString()
      {
         return "Consumed " + response.toString();
      }
   }

   public static class Failed extends EventAcknowledgement
   {

      /** . */
      private final Throwable throwable;

      public Failed(Throwable throwable)
      {
         this.throwable = throwable;
      }

      public Throwable getThrowable()
      {
         return throwable;
      }

      public String toString()
      {
         return "Failed";
      }
   }

   public static class Discarded extends EventAcknowledgement
   {

      /** . */
      private final int cause;

      public Discarded(int cause)
      {
         this.cause = cause;
      }

      public int getCause()
      {
         return cause;
      }

      public String toString()
      {
         return "Discarded";
      }
   }

}
