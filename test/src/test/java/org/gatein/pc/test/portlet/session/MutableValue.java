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
package org.gatein.pc.test.portlet.session;

import java.io.Serializable;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5985 $
 */
public class MutableValue implements Serializable
{

   private String string;

   public MutableValue(String string)
   {
      this.string = string;
   }

   public String getString()
   {
      return string;
   }

   public void setString(String string)
   {
      this.string = string;
   }

   public boolean equals(Object obj)
   {
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof MutableValue)
      {
         MutableValue that = (MutableValue)obj;
         return string == null ? that.string == null : string.equals(that.string);
      }
      return false;
   }

   private void writeObject(java.io.ObjectOutputStream out) throws IOException
   {
      out.writeUTF(string);
      System.out.print("Serializing " + string);
   }

   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      string = in.readUTF();
      System.out.print("Unserializing " + string);
   }

   public String toString()
   {
      return "MutableValue[" + string + "]";
   }
}
