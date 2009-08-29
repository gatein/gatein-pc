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

import org.gatein.pc.portlet.container.object.PortletContainerObject;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.common.invocation.InvocationException;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletContainerObjectSupport extends ObjectSupport implements PortletContainerObject
{

   /** . */
   final Map<String, PortletFilter> filters = new HashMap<String, PortletFilter>();

   /** . */
   PortletApplication application;

   /** . */
   PortletContainerContext context;

   public PortletContainerObjectSupport(String id)
   {
      super(id);
   }

   public void setPortletApplication(PortletApplication application)
   {
      this.application = application;
   }

   public void setContext(PortletContainerContext context)
   {
      this.context = context;
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

   public PortletInfo getInfo()
   {
      throw new UnsupportedOperationException();
   }

   public PortletInvocationResponse dispatch(PortletInvocation invocation) throws PortletInvokerException, InvocationException
   {
      throw new UnsupportedOperationException();
   }

   public PortletApplication getPortletApplication()
   {
      throw new UnsupportedOperationException();
   }

   public PortletContainerContext getContext()
   {
      throw new UnsupportedOperationException();
   }
}
