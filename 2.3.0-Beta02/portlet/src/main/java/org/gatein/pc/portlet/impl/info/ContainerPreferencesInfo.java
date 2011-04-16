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
package org.gatein.pc.portlet.impl.info;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.info.PreferenceInfo;
import org.gatein.pc.api.info.PreferencesInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6818 $
 */
public class ContainerPreferencesInfo implements PreferencesInfo
{

   /** The cached container preference info. */
   private final Map<String, PreferenceInfo> content;

   /** . */
   private final Set<String> keys;

   /** . */
   private final String validatorClassName;

   public ContainerPreferencesInfo(String validatorClassName)
   {
      this.content = new HashMap<String, PreferenceInfo>();
      this.keys = Collections.unmodifiableSet(content.keySet());
      this.validatorClassName = validatorClassName;
   }

   public String getValidatorClassName()
   {
      return validatorClassName;
   }

   public ContainerPreferenceInfo getContainerPreference(String key)
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Preference key must not be null");
      }
      return (ContainerPreferenceInfo)content.get(key);
   }

   public void addContainerPreference(
      String name,
      List<String> value,
      boolean readOnly,
      LocalizedString displayName,
      LocalizedString description)
   {
      ContainerPreferenceInfo pref = new ContainerPreferenceInfo(name, displayName, description, readOnly, value);
      content.put(pref.getKey(), pref);
   }

   public Set<String> getKeys()
   {
      return keys;
   }

   public PreferenceInfo getPreference(String key)
   {
      return getContainerPreference(key);
   }
}