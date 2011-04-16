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
import org.gatein.pc.api.info.MetaInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class MetaInfoSupport implements MetaInfo
{

   /** . */
   private Map values = new HashMap();

   public LocalizedString getMetaValue(String key)
   {
      if (key == null)
      {
         throw new IllegalArgumentException();
      }
      return (LocalizedString)values.get(key);
   }

   public void addValue(String key, Locale locale, String value)
   {
      if (key == null)
      {
         throw new IllegalArgumentException();
      }
      LocalizedString string = new LocalizedString(value, locale);
      if (values.put(key, string) != null)
      {
         throw new IllegalStateException("Already existing");
      }
   }

   public void setTitle(String name)
   {
      addValue(MetaInfo.TITLE, Locale.ENGLISH, name);
   }

   public void setShortTitle(String name)
   {
      addValue(MetaInfo.SHORT_TITLE, Locale.ENGLISH, name);
   }

   public void setKeywords(String name)
   {
      addValue(MetaInfo.KEYWORDS, Locale.ENGLISH, name);
   }

   public void setDisplayName(String name)
   {
      addValue(MetaInfo.DISPLAY_NAME, Locale.ENGLISH, name);
   }

   public void setDescription(String name)
   {
      addValue(MetaInfo.DESCRIPTION, Locale.ENGLISH, name);
   }

   public String getDisplayName()
   {
      LocalizedString string = getMetaValue(MetaInfo.DISPLAY_NAME);
      if (string == null)
      {
         throw new IllegalStateException();
      }
      return string.getString(Locale.ENGLISH, true);
   }
}
