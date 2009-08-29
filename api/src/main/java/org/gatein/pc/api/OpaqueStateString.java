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

import org.gatein.pc.api.StateString;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An opaque implementation of the navigational state.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class OpaqueStateString extends StateString
{

   /** . */
   private String value;

   public OpaqueStateString(String value)
   {
      if (value == null)
      {
         throw new IllegalArgumentException("No null value accepted");
      }
      this.value = value;
   }

   public String getStringValue()
   {
      return value;
   }

   public void writeTo(DataOutputStream out) throws IOException
   {
      out.writeByte(StateString.OPAQUE);
      out.writeUTF(value);
   }

   public int hashCode()
   {
      return value.hashCode();
   }

   public boolean equals(Object o)
   {
      if (o == this)
      {
         return true;
      }
      if (o instanceof OpaqueStateString)
      {
         OpaqueStateString that = (OpaqueStateString)o;
         return value.equals(that.value);
      }
      return false;
   }
}
