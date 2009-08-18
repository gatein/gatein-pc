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
package org.gatein.pc.api.state;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5893 $
 */
public class DestroyCloneFailure
{

   /** The clone id. */
   private final String portletId;

   /** . */
   private final String message;

   public DestroyCloneFailure(String portletId, String message)
   {
      if (portletId == null)
      {
         throw new IllegalArgumentException("Must provide a portlet id");
      }
      this.portletId = portletId;
      this.message = message;
   }

   public DestroyCloneFailure(String portletId)
   {
      this(portletId, null);
   }

   public String getPortletId()
   {
      return portletId;
   }

   public String getMessage()
   {
      return message;
   }

   public String toString()
   {
      return "DestroyCloneFailure[" + portletId + "," + message + "]";
   }

   public int hashCode()
   {
      return portletId.hashCode();
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof DestroyCloneFailure)
      {
         DestroyCloneFailure that = (DestroyCloneFailure)obj;
         return portletId.equals(that.portletId);
      }
      return false;
   }
}
