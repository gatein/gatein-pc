/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.gatein.pc.api;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public abstract class PortletStateType<S extends Serializable>
{

   public static final PortletStateType<byte[]> OPAQUE = new PortletStateType<byte[]>()
   {
      public Class<byte[]> getJavaType() {
        return byte[].class;
      }

     @Override
      public boolean equals(byte[] state1, byte[] state2) {
         return Arrays.equals(state1, state2);
      }

      @Override
      public int hashCode(byte[] state) {
         return (state != null ? state.hashCode() : 0);
      }

      @Override
      public String toString(byte[] state) {
         return "" + state.length;
      }
   };

   public abstract Class<S> getJavaType();

   public abstract boolean equals(S state1, S state2);
  
   public abstract int hashCode(S state);

   public abstract String toString(S state);

}
