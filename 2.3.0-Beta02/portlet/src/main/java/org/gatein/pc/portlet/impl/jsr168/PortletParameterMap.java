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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.common.util.TypedMap;
import org.gatein.pc.api.info.NavigationInfo;
import org.gatein.pc.api.info.ParameterInfo;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletParameterMap
{

   /** . */
   private static final String[] EMPTY_STRINGS = new String[0];

   /** . */
   private NavigationInfo navigationInfo;

   /** . */
   private Map<String, PortletParameter> entries;

   /** . */
   private Set<String> publicEntryRemovals;

   /** . */
   private TypedMap<String, String[], String, PortletParameter> combinedMap;

   public PortletParameterMap(NavigationInfo navigationInfo)
   {
      this.navigationInfo = navigationInfo;
      this.entries = new HashMap<String, PortletParameter>();
      this.publicEntryRemovals = new HashSet<String>();
      this.combinedMap = new TypedMap<String, String[], String, PortletParameter>(entries, keyConverter, valueConverter);
   }

   public PortletParameterMap(PortletParameterMap original)
   {
      this.navigationInfo = original.navigationInfo;
      this.entries = new HashMap<String, PortletParameter>(original.entries);
      this.publicEntryRemovals = new HashSet<String>(original.publicEntryRemovals);
      this.combinedMap = new TypedMap<String, String[], String, PortletParameter>(entries, keyConverter, valueConverter);
   }

   public String getParameterValue(String name)
   {
      String[] values = getParameterValues(name);

      return (values != null && values.length > 0) ? values[0] : null;
   }

   public String[] getParameterValues(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }

      //
      PortletParameter entry = entries.get(name);

      //
      return entry != null ? entry.getValues() : null;
   }

   public void setParameterValue(String name, String value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (value == null)
      {
         throw new IllegalArgumentException("No null string array accepted");
      }

      //
      internalPut(name, new String[]{value}, false);
   }

   public void setParameterValues(String name, String[] value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (value == null)
      {
         throw new IllegalArgumentException("No null string array accepted");
      }
      if (value.length == 0)
      {
         throw new IllegalArgumentException("Render parameter value cannot be a zero length array");
      }
      for (int i = 0;i < value.length;i++)
      {
         if (value[i] == null)
         {
            throw new IllegalArgumentException("String of parameter value at index " + i + " must not be null");
         }
      }

      //
      internalPut(name, value, true);
   }

   private void internalPut(String name, String[] value, boolean cloneValue)
   {
      if (cloneValue)
      {
         value = value.clone();
      }

      // Look if we have an entry already
      PortletParameter entry = entries.get(name);

      //
      if (entry == null)
      {
         ParameterInfo parameterInfo = navigationInfo.getPublicParameter(name);

         //
         PortletParameter.Scope scope = parameterInfo == null ? PortletParameter.Scope.PRIVATE : PortletParameter.Scope.PUBLIC;

         //
         if (scope == PortletParameter.Scope.PUBLIC)
         {
            publicEntryRemovals.remove(name);
         }

         //
         entries.put(name, new PortletParameter(scope, value));
      }
      else
      {
         entries.put(name, new PortletParameter(entry.getScope(), value));
      }
   }

   public void removePublicParameterValue(String name)
   {
      ParameterInfo parameterInfo = navigationInfo.getPublicParameter(name);

      //
      if (parameterInfo != null)
      {
         publicEntryRemovals.add(name);
         entries.remove(name);
      }
   }

   public Map<String, String[]> getMap()
   {
      return combinedMap;
   }

   public void setMap(Map<String, String[]> map)
   {
      if (map == null)
      {
         throw new IllegalArgumentException("No null map accepted");
      }
      
      // Make the check first to ensure atomicity of the update
      for (Map.Entry<String, String[]> entry : map.entrySet())
      {
         // We need to check that for portlets not using generics
         if (!(entry.getKey() instanceof String))
         {
            throw new IllegalArgumentException();
         }
         // We need to check that for portlets not using generics
         if (!(entry.getValue() instanceof String[]))
         {
            throw new IllegalArgumentException();
         }
         String[] values = entry.getValue();
         for (String value : values)
         {
            if (value == null)
            {
               throw new IllegalArgumentException();
            }
         }
      }

      //
      entries.clear();
      for (Map.Entry<String, String[]> entry : map.entrySet())
      {
         internalPut(entry.getKey(), entry.getValue(), true);
      }
   }

   public Map<String, String[]> getPrivateMapSnapshot()
   {
      return getMapSnapshot(PortletParameter.Scope.PRIVATE);
   }

   public Map<String, String[]> getPublicMapSnapshot()
   {
      Map<String, String[]> snapshot = getMapSnapshot(PortletParameter.Scope.PUBLIC);
      for (String removal : publicEntryRemovals)
      {
         snapshot.put(removal, EMPTY_STRINGS);
      }
      return snapshot;
   }

   private Map<String, String[]> getMapSnapshot(PortletParameter.Scope scope)
   {
      Map<String, String[]> snapshot = new HashMap<String, String[]>();

      //
      for (Map.Entry<String, PortletParameter> entry : entries.entrySet())
      {
         PortletParameter parameter = entry.getValue();

         //
         if (parameter.getScope() == scope)
         {
             snapshot.put(entry.getKey(), parameter.getValues().clone());
         }
      }

      //
      return snapshot;
   }

   private static TypedMap.Converter<String, String> keyConverter = new TypedMap.Converter<String, String>()
   {
      protected String getInternal(String external) throws IllegalArgumentException, ClassCastException
      {
         return external;
      }

      protected String getExternal(String internal)
      {
         return internal;
      }

      protected boolean equals(String left, String right)
      {
         return left.equals(right);
      }
   };

   private static TypedMap.Converter<String[], PortletParameter> valueConverter = new TypedMap.Converter<String[], PortletParameter>()
   {
      protected PortletParameter getInternal(String[] external) throws IllegalArgumentException, ClassCastException
      {
         throw new UnsupportedOperationException("Cannot write");
      }

      protected String[] getExternal(PortletParameter internal)
      {
         // We clone the value as it may be accessed
         return internal.getValues().clone();
      }

      protected boolean equals(PortletParameter left, PortletParameter right)
      {
         throw new UnsupportedOperationException();
      }
   };
}
