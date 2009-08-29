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

import org.gatein.common.util.Base64;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.OpaqueStateString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulate state as a string.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public abstract class StateString implements Serializable
{
   /** . */
   public static final String JBPNS_PREFIX = "JBPNS_";
   protected static final int EMPTY = 0;
   protected static final int SERIALIZED = 1;
   protected static final int OPAQUE = 2;
   /** . */
   private static final String EOF = "__EOF__";

   /**
    * Return the value of the navigational state of the portlet.
    *
    * @return the string value
    */
   public abstract String getStringValue();

   public abstract void writeTo(DataOutputStream out) throws IOException;

   /**
    * Factory method that will create the most appropriate form from the byte representation.
    *
    * @param in the inputstream to read from
    * @return a new state string
    * @throws java.io.IOException any IOException
    */
   public static StateString create(DataInputStream in) throws IOException
   {
      if (in == null)
      {
         throw new IllegalArgumentException();
      }

      //
      byte b = in.readByte();
      switch (b)
      {
         case EMPTY:
            return new ParametersStateString();
         case SERIALIZED:
            return new ParametersStateString(in);
         case OPAQUE:
            return new OpaqueStateString(in.readUTF());
      }

      //
      throw new IllegalArgumentException("Wrong format unrecognized header " + b);
   }

   /**
    * Factory method that will create the most appropriate form from the string representation.
    *
    * @param opaqueValue the opaque value
    * @return a new state string
    */
   public static StateString create(String opaqueValue)
   {
      if (opaqueValue == null)
      {
         throw new IllegalArgumentException();
      }
      if (opaqueValue.startsWith(JBPNS_PREFIX))
      {
         return new ParametersStateString(opaqueValue);
      }
      else
      {
         return new OpaqueStateString(opaqueValue);
      }
   }

   public static Map<String, String[]> decodeOpaqueValue(String opaqueValue)
   {
      if (!opaqueValue.startsWith(JBPNS_PREFIX))
      {
         throw new IllegalArgumentException("Bad format: [" + opaqueValue
            + "] was not encoded by JBoss Portal and thus cannot be decoded.");
      }

      //
      opaqueValue = opaqueValue.substring(JBPNS_PREFIX.length());
      if (opaqueValue.length() > 0)
      {
         try
         {
            byte[] bytes = Base64.decode(opaqueValue, true);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Map<String, String[]> params = new HashMap<String, String[]>();

            //
            String[] values;

            // read the first String which should be a param name
            String current = ois.readUTF();

            // keep reading until we haven't reached the EOF marker
            while (!EOF.equals(current))
            {
               // next is the size of the value array
               int length = ois.readInt();
               values = new String[length];

               // read as many Strings as are supposed to be in the array
               for (int i = 0; i < length; i++)
               {
                  values[i] = ois.readUTF();
               }

               // we're done for this param, add it to the param map
               params.put(current, values);

               // read the next string to loop
               current = ois.readUTF();
            }

            return params;
         }
         catch (Exception e)
         {
            throw new Error(e);
         }
      }
      else
      {
         return Collections.emptyMap();
      }
   }

   public static String encodeAsOpaqueValue(Map<String, String[]> parameters)
   {
      if (parameters != null && parameters.size() != 0)
      {
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            for (Map.Entry entry : parameters.entrySet())
            {
               String name = (String)entry.getKey();
               oos.writeUTF(name);
               String[] values = (String[])entry.getValue();
               int length = values.length;
               oos.writeInt(length);
               for (String value : values)
               {
                  oos.writeUTF(value);
               }
            }
            oos.writeUTF(EOF);
            oos.close();
            byte[] bytes = baos.toByteArray();
            return JBPNS_PREFIX + Base64.encodeBytes(bytes, true);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         return JBPNS_PREFIX;
      }
   }
}
