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

package org.gatein.pc.samples.shoppingcart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9912 $
 */
public class Catalog
{
   private static Map<String, CatalogItem> items;

   static
   {
      items = new HashMap<String, CatalogItem>(7);
      items.put("1", new CatalogItem("1", 800, "Bike"));
      items.put("2", new CatalogItem("2", 450, "Snowboard"));
      items.put("3", new CatalogItem("3", 225, "Tent"));
      items.put("4", new CatalogItem("4", 75, "Backpack"));
      items.put("5", new CatalogItem("5", 119, "Skateboard"));
      items.put("6", new CatalogItem("6", 333, "Surfboard"));
      items.put("7", new CatalogItem("7", 90, "Sneakers"));
   }

   private Catalog()
   {
   }

   public static CatalogItem get(String id)
   {
      return items.get(id);
   }

   public static Collection<CatalogItem> getAll()
   {
      return items.values();
   }
}
