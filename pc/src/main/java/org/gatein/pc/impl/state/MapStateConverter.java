/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.impl.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.state.SimplePropertyMap;
import org.gatein.pc.state.StateConversionException;
import org.gatein.pc.state.StateConverter;
import org.gatein.pc.state.producer.PortletState;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 * @version $Revision$
 */
public class MapStateConverter implements StateConverter
{
   
   public <S extends Serializable> S marshall(PortletStateType<S> stateType, PortletState state)
         throws StateConversionException, IllegalArgumentException
   {
      if (stateType.getJavaType().equals(HashMap.class))
      {
         Object map = marshall(state);
         return (S)map;
      }
      else
      {
         throw new UnsupportedOperationException();
      }
   }
   
   public HashMap marshall(PortletState state)
   {
      if (state == null)
      {
         throw new IllegalArgumentException("No null state");
      }
            
      HashMap map = new HashMap();
      Iterator<String> iKeys = state.getProperties().keySet().iterator();
      while (iKeys.hasNext())
      {
         String key = iKeys.next();
         List<String> propList = state.getProperties().getProperty(key);
         
         map.put(key, propList.toArray());
      }
      
      map.put("portletID", state.getPortletId());
      
      return map;
   }

   public <S extends Serializable> PortletState unmarshall(PortletStateType<S> stateType, S marshalledState)
         throws StateConversionException, IllegalArgumentException
   {
      if (stateType.getJavaType().equals(HashMap.class))
      {
         HashMap map = (HashMap)marshalledState;
         return unmarshall(map);
      }
      else
      {
         throw new UnsupportedOperationException();
      }
   }
   
   public PortletState unmarshall(Map marshalledState)
   {
      if (marshalledState == null)
      {
         throw new IllegalArgumentException("No null map");
      }
      
      PropertyMap properties = new SimplePropertyMap(marshalledState.size());
      
      
      Iterator<String> iKeys = marshalledState.keySet().iterator();
      while (iKeys.hasNext())
      {
         String key = iKeys.next();
         if (key != "portletID")
         {
            Object mapValue = marshalledState.get(key);

            if (mapValue instanceof Object[])
            {
               Object[] values = (Object[])mapValue;

               List valueList = new ArrayList<String>();
               for (Object value: values)
               {
                  if (value instanceof String)
                     valueList.add((String)value);
               }

               properties.put(key, valueList);
            }
         }
      }
      
      String portletID = (String) marshalledState.get("portletID");
      
      return new PortletState(portletID, properties);
   }

}

