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

import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class FilterChainImpl<T extends PortletFilter> implements FilterChain
{

   /** . */
   private List<T> filters;

   /** . */
   private Class<T> phase;

   /** . */
   private int index;

   public FilterChainImpl(List<T> filters, Class<T> phase)
   {
      this.filters = filters;
      this.phase = phase;
      this.index = 0;
   }

   public void doFilter(PortletRequest req, PortletResponse resp) throws IOException, PortletException
   {
      if (req instanceof ActionRequest && resp instanceof ActionResponse)
      {
         doFilter((ActionRequest)req, (ActionResponse)resp);
      }
      else if (req instanceof EventRequest && resp instanceof EventResponse)
      {
         doFilter((EventRequest)req, (EventResponse)resp);
      }
      else if (req instanceof RenderRequest && resp instanceof RenderResponse)
      {
         doFilter((RenderRequest)req, (RenderResponse)resp);
      }
      else if (req instanceof ResourceRequest && resp instanceof ResourceResponse)
      {
         doFilter((ResourceRequest)req, (ResourceResponse)resp);
      }
      else
      {
         throw new IllegalArgumentException();
      }
   }

   public void doFilter(ActionRequest req, ActionResponse resp) throws IOException, PortletException
   {
      if (!ActionFilter.class.isAssignableFrom(phase))
      {
         throw new IllegalStateException();
      }
      if (index >= filters.size())
      {
         throw new IllegalStateException();
      }

      //
      int currentIndex = index++;
      try
      {
         PortletFilter filter = filters.get(currentIndex);
         ActionFilter actionFilter = ActionFilter.class.cast(filter);
         actionFilter.doFilter(req, resp, this);
      }
      finally
      {
         index--;
      }
   }

   public void doFilter(EventRequest req, EventResponse resp) throws IOException, PortletException
   {
      if (!EventFilter.class.isAssignableFrom(phase))
      {
         throw new IllegalStateException();
      }
      if (index >= filters.size())
      {
         throw new IllegalStateException();
      }

      //
      int currentIndex = index++;
      try
      {
         PortletFilter filter = filters.get(currentIndex);
         EventFilter eventFilter = EventFilter.class.cast(filter);
         eventFilter.doFilter(req, resp, this);
      }
      finally
      {
         index--;
      }
   }

   public void doFilter(RenderRequest req, RenderResponse resp) throws IOException, PortletException
   {
      if (!RenderFilter.class.isAssignableFrom(phase))
      {
         throw new IllegalStateException();
      }
      if (index >= filters.size())
      {
         throw new IllegalStateException();
      }

      //
      int currentIndex = index++;
      try
      {
         PortletFilter filter = filters.get(currentIndex);
         RenderFilter renderFilter = RenderFilter.class.cast(filter);
         renderFilter.doFilter(req, resp, this);
      }
      finally
      {
         index--;
      }
   }

   public void doFilter(ResourceRequest req, ResourceResponse resp) throws IOException, PortletException
   {
      if (!ResourceFilter.class.isAssignableFrom(phase))
      {
         throw new IllegalStateException();
      }
      if (index >= filters.size())
      {
         throw new IllegalStateException();
      }

      //
      int currentIndex = index++;
      try
      {
         PortletFilter filter = filters.get(currentIndex);
         ResourceFilter resourceFilter = ResourceFilter.class.cast(filter);
         resourceFilter.doFilter(req, resp, this);
      }
      finally
      {
         index--;
      }
   }
}
