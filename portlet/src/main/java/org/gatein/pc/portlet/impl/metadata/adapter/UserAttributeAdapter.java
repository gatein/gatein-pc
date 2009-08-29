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
package org.gatein.pc.portlet.impl.metadata.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */

public class UserAttributeAdapter
      extends XmlAdapter<List<UserAttributeMetaData>, Map<String, UserAttributeMetaData>>
{

   @Override
   public List<UserAttributeMetaData> marshal(Map<String, UserAttributeMetaData> arg0) throws Exception
   {
      throw new UnsupportedOperationException();
   }

   @Override
   public Map<String, UserAttributeMetaData> unmarshal(List<UserAttributeMetaData> list) throws Exception
   {
      HashMap<String, UserAttributeMetaData> m = new HashMap<String, UserAttributeMetaData>();
      for (UserAttributeMetaData a : list)
      {
         String userAttributeName = a.getName();
         if (!m.containsKey(userAttributeName))
         {
            m.put(a.getName(), a);
         }
         else
         {
            throw new IllegalArgumentException("Duplicate user-attribute: " + userAttributeName);
         }
      }
      return m;
   }

}
