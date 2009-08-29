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
package org.gatein.pc.portlet.impl.state;

import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.StateConversionException;
import org.gatein.pc.portlet.state.StateConverter;
import org.gatein.pc.portlet.state.producer.PortletState;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * <p>An implementation that relies on the <code>DataInputStream</code> and <code>DataOutputStream</code> to marshall
 * and unmarshall the producer state. The marshalled value starts with a magic value and a version id to ensure future
 * backward compatibility.</p>
 * <p/>
 * <p>It is an important matter because a migrated producer will probably have to take care of consumers that hold a
 * previous version of the producer state.</p>
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class StateConverterV0 implements StateConverter
{

   /** . */
   private static final int MAGIC_VALUE = 0xBE57A515;

   /** . */
   private static final byte VERSION_ID = 0;

  public <S extends Serializable> S marshall(PortletStateType<S> stateType, PortletState state) throws StateConversionException, IllegalArgumentException {
    if (stateType.getJavaType().equals(byte[].class))
    {
       Object bytes = marshall(state);
       return (S)bytes;
    }
    else
    {
       throw new UnsupportedOperationException();
    }
  }

  public byte[] marshall(PortletState state) throws StateConversionException
   {
      if (state == null)
      {
         throw new IllegalArgumentException("No null state");
      }
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(baos);

         //
         dos.writeInt(MAGIC_VALUE);
         dos.write(VERSION_ID);
         dos.writeUTF(state.getPortletId());
         PropertyMap map = state.getProperties();
         dos.writeInt(map.size());
         for (Map.Entry<String, List<String>> entry : map.entrySet())
         {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            String[] strings = value.toArray(new String[value.size()]);
            dos.writeUTF(key);
            dos.writeInt(strings.length);
            for (String string : strings)
            {
               if (string == null)
               {
                  dos.writeBoolean(true);
               }
               else
               {
                  dos.writeBoolean(false);
                  dos.writeUTF(string);
               }
            }
         }
         dos.close();
         return baos.toByteArray();
      }
      catch (IOException e)
      {
         throw new StateConversionException(e);
      }
   }

   public <S extends Serializable> PortletState unmarshall(PortletStateType<S> stateType, S marshalledState) throws StateConversionException, IllegalArgumentException
   {
      if (stateType.getJavaType().equals(byte[].class))
      {
         byte[] bytes = (byte[])marshalledState;
         return unmarshall(bytes);
      }
      else
      {
         throw new UnsupportedOperationException();
      }
   }

   public PortletState unmarshall(byte[] marshalledState) throws StateConversionException
   {
      if (marshalledState == null)
      {
         throw new IllegalArgumentException("No null bytes");
      }
      try
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(marshalledState);
         DataInputStream dis = new DataInputStream(bais);
         int magicValue = dis.readInt();
         if (magicValue != MAGIC_VALUE)
         {
            throw new StateConversionException("Bad magic value " + Integer.toHexString(magicValue));
         }
         byte versionId = dis.readByte();
         if (versionId > 0)
         {
            throw new StateConversionException("Bad version id " + versionId);
         }
         String portletId = dis.readUTF();
         int size = dis.readInt();
         PropertyMap properties = new SimplePropertyMap(size);
         while (size-- > 0)
         {
            String key = dis.readUTF();
            int length = dis.readInt();
            String[] strings = new String[length];
            for (int i = 0; i < strings.length; i++)
            {
               boolean isNull = dis.readBoolean();
               if (isNull == false)
               {
                  String string = dis.readUTF();
                  strings[i] = string;
               }
            }
            List<String> value = Arrays.asList(strings.clone());
            properties.setProperty(key, value);
         }
         return new PortletState(portletId, properties);
      }
      catch (IOException e)
      {
         throw new StateConversionException(e);
      }
   }
}
