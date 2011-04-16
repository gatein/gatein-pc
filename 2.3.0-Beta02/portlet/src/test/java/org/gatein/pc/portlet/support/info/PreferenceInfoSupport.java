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
package org.gatein.pc.portlet.support.info;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.info.PreferenceInfo;

import java.util.Locale;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class PreferenceInfoSupport implements PreferenceInfo
{

   /** . */
   private final String key;

   /** . */
   private LocalizedString displayName;

   /** . */
   private LocalizedString description;

   /** . */
   private Boolean readOnly;

   /** . */
   private List<String> defaultValue;

   public PreferenceInfoSupport(String key)
   {
      this.key = key;
      this.displayName = new LocalizedString("Display name of " + key, Locale.ENGLISH);
      this.description = new LocalizedString("Description of " + key, Locale.ENGLISH);
      this.readOnly = null;
   }

   public PreferenceInfoSupport(String key, Boolean readOnly)
   {
      this.key = key;
      this.displayName = new LocalizedString("Display name of " + key, Locale.ENGLISH);
      this.description = new LocalizedString("Description of " + key, Locale.ENGLISH);
      this.readOnly = readOnly;
   }

   public String getKey()
   {
      return key;
   }

   public LocalizedString getDisplayName()
   {
      return displayName;
   }

   public LocalizedString getDescription()
   {
      return description;
   }

   public Boolean isReadOnly()
   {
      return readOnly;
   }

   public void setReadOnly(Boolean readOnly)
   {
      this.readOnly = readOnly;
   }

   public void setDisplayName(LocalizedString displayName)
   {
      this.displayName = displayName;
   }

   public void setDescription(LocalizedString description)
   {
      this.description = description;
   }

   public List<String> getDefaultValue()
   {
      return defaultValue;
   }

   public void setDefaultValue(List<String> defaultValue)
   {
      this.defaultValue = defaultValue;
   }
}
