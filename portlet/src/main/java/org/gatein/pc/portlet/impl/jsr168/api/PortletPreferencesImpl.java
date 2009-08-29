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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.apache.log4j.Logger;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.api.info.PreferenceInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferencesInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferenceInfo;

import javax.portlet.PortletPreferences;
import javax.portlet.PreferencesValidator;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6757 $
 */
public class PortletPreferencesImpl implements PortletPreferences
{

   /** The logger. */
   protected static final Logger log = Logger.getLogger(PortletPreferencesImpl.class);

   /** Indicates the object is used during a action phase. */
   public static final int ACTION = 1;

   /** Indicates the object is used during a render phase. */
   public static final int RENDER = 0;

   /** . */
   protected final PropertyContext prefs;

   /** . */
   protected final PreferencesValidator validator;

   /** . */
   protected final ContainerPreferencesInfo containerPrefs;

   /** . */
   protected final int mode;

   /** Keep track of updates */
   protected final Map<String, PropertyChange> updates;

   public PortletPreferencesImpl(
      PropertyContext prefs,
      ContainerPreferencesInfo containerPrefs,
      PreferencesValidator validator,
      int mode)
   {
      this.prefs = prefs;
      this.validator = validator;
      this.containerPrefs = containerPrefs;
      this.mode = mode;
      this.updates = new HashMap<String, PropertyChange>();
   }

   public Map<String, String[]> getMap()
   {
      return new PreferencesMap();
   }

   public Enumeration<String> getNames()
   {
      // Clone the system names
      Set<String> names = new HashSet<String>(containerPrefs.getKeys());

      // Add the user
      names.addAll(prefs.getKeys());

      // Add the transient updates
      for (PropertyChange change : updates.values())
      {
         if (change.getType() == PropertyChange.PREF_UPDATE)
         {
            names.add(change.getKey());
         }
         else
         {
            names.remove(change.getKey());
         }
      }

      // Convert to enumeration
      return Collections.enumeration(names);
   }

   private List<String> getValue(String key)
   {
      List<String> value = null;
      PropertyChange change = updates.get(key);
      if (change != null)
      {
         if (change.getType() == PropertyChange.PREF_UPDATE)
         {
            value = change.getValue();
         }
         else
         {
            ContainerPreferenceInfo containerPref = containerPrefs.getContainerPreference(key);
            if (containerPref != null)
            {
               value = containerPref.getDefaultValue();
            }
         }
      }
      else
      {
         // Get user
         value = prefs.getValue(key);

         // If does not exist or read only use what the default one
         if (value == null || isDDReadOnly(key))
         {
            ContainerPreferenceInfo containerPref = containerPrefs.getContainerPreference(key);
            if (containerPref != null)
            {
               value = containerPref.getDefaultValue();
            }
         }
      }
      return value;
   }

   /**
    * Return true if the preferences is marked as read only in the portlet.xml deployment
    * descriptor.
    *
    * @param key the preference key
    * @return the read only value
    */
   private boolean isDDReadOnly(String key)
   {
      PreferenceInfo pref = containerPrefs.getPreference(key);
      if (pref != null)
      {
         return Boolean.TRUE.equals(pref.isReadOnly());
      }
      return false;
   }


   public String getValue(String key, String def) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      List<String> value = getValue(key);
      if (value == null || value.isEmpty())
      {
         return def;
      }
      else
      {
         return value.get(0);
      }
   }

   public String[] getValues(String key, String[] def) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      List<String> value = getValue(key);
      if (value == null || value.isEmpty())
      {
         return def;
      }
      else
      {
         return value.toArray(new String[value.size()]);
      }
   }

   public boolean isReadOnly(String key) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      if (mode == ACTION)
      {
         // The accurate value is to combine what the portlet developer and the consumer specifies
         return prefs.isReadOnly() || isDDReadOnly(key);
      }
      else
      {
         // During render we cannot be aware of the consumer
         // intent with respect to the access mode of the current state
         return isDDReadOnly(key);
      }
   }

   public void reset(String key) throws IllegalArgumentException, ReadOnlyException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      if (isReadOnly(key))
      {
         throw new ReadOnlyException("Key " + key + " cannot be written");
      }
      updates.put(key, PropertyChange.newReset(key));
   }

   public void setValue(String key, String value) throws IllegalArgumentException, ReadOnlyException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      if (isReadOnly(key))
      {
         throw new ReadOnlyException("Key " + key + " cannot be written");
      }
      updates.put(key, PropertyChange.newUpdate(key, value));
   }

   public void setValues(String key, String[] values) throws IllegalArgumentException, ReadOnlyException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("key must not be null");
      }
      if (isReadOnly(key))
      {
         throw new ReadOnlyException("Key " + key + " cannot be written");
      }
      if (values == null)
      {
         values = new String[1];
      }
      updates.put(key, PropertyChange.newUpdate(key, values));
   }

   public void store() throws IOException, ValidatorException
   {
      // Check we are in the right mode
      if (mode != ACTION)
      {
         throw new IllegalStateException("Store must be called within the scope of an action request");
      }

      // Copy the transient set to the persistent set if the consumer allows it
      if (prefs.isReadOnly())
      {
         throw new IOException("Should not happen");
      }

      // If the optional validator is present validate
      if (validator != null)
      {
         validator.validate(this);
      }

      //
      PropertyChange[] changes = updates.values().toArray(new PropertyChange[updates.size()]);
      prefs.update(changes);

      // Clear the updates
      updates.clear();
   }

   private class PreferencesMap extends HashMap<String, String[]>
   {

      /** The serialVersionUID */
      private static final long serialVersionUID = 6969583304804836926L;

      public PreferencesMap()
      {
         super(10);

         //
         for (String key : containerPrefs.getKeys())
         {
            List<String> defaultValue = containerPrefs.getContainerPreference(key).getDefaultValue();
            String[] value = defaultValue.toArray(new String[defaultValue.size()]);
            super.put(key, value);
         }

         //
         for (String key : prefs.getKeys())
         {
            List<String> persistentValue = prefs.getValue(key);
            String[] value = persistentValue.toArray(new String[persistentValue.size()]);
            super.put(key, value);
         }

         //
         for (PropertyChange change : updates.values())
         {
            String key = change.getKey();

            if (change.getType() == PropertyChange.PREF_RESET)
            {
               super.remove(key);
            }
            else
            {
               List<String> updatedValue = change.getValue();
               String[] value = updatedValue.toArray(new String[updatedValue.size()]);
               super.put(key, value);
            }
         }
      }

      public boolean containsValue(Object value)
      {
         if (value instanceof String[])
         {
            String[] strings = (String[])value;
            for (String[] other : super.values())
            {
               if (Arrays.equals(strings, other))
               {
                  return true;
               }
            }
         }
         return false;
      }

      public String[] get(Object key)
      {
         return super.get(key); // Should be cloned here to ensure immutability
      }

      public Collection<String[]> values()
      {
         return super.values(); // String[] should be cloned here to ensure immutability
      }

      public Set<Map.Entry<String, String[]>> entrySet()
      {
         return super.entrySet();
      }

      /**
       * Do not change state.
       *
       * @return null
       */
      public String[] put(String key, String[] value)
      {
         return null;
      }

      /**
       * Do not change state.
       *
       * @return null
       */
      public String[] remove(Object key)
      {
         return null;
      }

      /** Do not change state. */
      public void putAll(Map<? extends String, ? extends String[]> m)
      {
      }

      /** Do not change state. */
      public void clear()
      {
      }
   }
}
