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

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Collection;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerPortletApplicationInfo
{

   /** . */
   private final String id;

   /** . */
   private final String defaultNamespace;

   /** . */
   private final List<ContainerListenerInfo> listeners;

   /** . */
   private final ContainerUserInfo user;

   /** . */
   private final Map<String, ContainerFilterInfo> filters;

   public ContainerPortletApplicationInfo(
      String id,
      String defaultNamespace,
      Collection<ContainerFilterInfo> filters,
      List<ContainerListenerInfo> listeners,
      ContainerUserInfo user)
   {
      LinkedHashMap<String, ContainerFilterInfo> tmp = new LinkedHashMap<String, ContainerFilterInfo>();
      for (ContainerFilterInfo filter : filters)
      {
         tmp.put(filter.getName(), filter);
      }

      //
      this.id = id;
      this.defaultNamespace = defaultNamespace;
      this.listeners = listeners;
      this.filters = Collections.unmodifiableMap(tmp);
      this.user = user;
   }

   public String getId()
   {
      return id;
   }

   public String getDefaultNamespace()
   {
      return defaultNamespace;
   }

   public List<ContainerListenerInfo> getListeners()
   {
      return listeners;
   }

   public Map<String, ContainerFilterInfo> getFilters()
   {
      return filters;
   }

   public ContainerUserInfo getUser()
   {
      return user;
   }
}
