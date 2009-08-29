/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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

package org.gatein.pc.mc.metadata.factory;

import org.apache.log4j.Logger;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.portlet.impl.metadata.adapter.LocalizedStringAdapter;
import org.gatein.pc.portlet.impl.metadata.common.LocalizedDescriptionMetaData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class LocalizedStringBuilder
{

   /** The object to localized meta data. */
   private Map<Object, LocalizedMetaDataContainer> map = new LinkedHashMap<Object, LocalizedMetaDataContainer>();

   /** The logger. */
   private static final Logger log = Logger.getLogger(LocalizedStringBuilder.class);

   public void put(Object object, LocalizedDescriptionMetaData description)
   {
      LocalizedMetaDataContainer container = map.get(object);
      if (container == null)
      {
         container = new LocalizedMetaDataContainer();
         map.put(object, container);
      }
      container.addLocalizedDescription(description);
   }

   public LocalizedString getLocalizedString(Object key)
   {
      try
      {
         return this.map.get(key) != null ? this.map.get(key).getLocalizedString() : null;
      }
      catch (Exception e)
      {
         log.error("could not generate localized string.", e);
         return null;
      }
   }

   private static class LocalizedMetaDataContainer
   {
      /** The list of localiezd meta data */
      private List<LocalizedDescriptionMetaData> list = new ArrayList<LocalizedDescriptionMetaData>();

      public void addLocalizedDescription(LocalizedDescriptionMetaData description)
      {
         this.list.add(description);
      }

      public LocalizedString getLocalizedString() throws Exception
      {
         LocalizedStringAdapter adapter = new LocalizedStringAdapter();
         return adapter.unmarshal(list);
      }
   }
}
