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
package org.gatein.pc.portlet.container;

import org.gatein.pc.portlet.container.object.PortletApplicationObject;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletApplicationObjectSupport extends ObjectSupport implements PortletApplicationObject
{

   /** . */
   final Map<String, PortletContainer> containers = new HashMap<String, PortletContainer>();

   /** . */
   final Map<String, PortletFilter> filters = new HashMap<String, PortletFilter>();

   /** . */
   PortletApplicationContext context;

   public PortletApplicationObjectSupport(String id)
   {
      super(id);
   }

   public void setContext(PortletApplicationContext context)
   {
      this.context = context;
   }

   public PortletApplicationContext getContext()
   {
      throw new UnsupportedOperationException();
   }

   public void addPortletContainer(PortletContainer container)
   {
      if (container == null)
      {
         throw new AssertionError();
      }
      if (containers.containsKey(container.getId()))
      {
         throw new AssertionError();
      }
      containers.put(container.getId(), container);
   }

   public void removePortletContainer(PortletContainer container)
   {
      if (container == null)
      {
         throw new AssertionError();
      }
      if (!containers.containsKey(container.getId()))
      {
         throw new AssertionError();
      }
      containers.remove(container.getId());
   }

   public void addPortletFilter(PortletFilter filter)
   {
      if (filter == null)
      {
         throw new AssertionError();
      }
      if (filters.containsKey(filter.getId()))
      {
         throw new AssertionError();
      }
      filters.put(filter.getId(), filter);
   }

   public void removePortletFilter(PortletFilter filter)
   {
      if (filter == null)
      {
         throw new AssertionError();
      }
      if (!filters.containsKey(filter.getId()))
      {
         throw new AssertionError();
      }
      filters.remove(filter.getId());
   }

   public Collection<? extends PortletContainer> getPortletContainers()
   {
      throw new UnsupportedOperationException();
   }

   public PortletContainer getPortletContainer(String containerId)
   {
      throw new UnsupportedOperationException();
   }
}
