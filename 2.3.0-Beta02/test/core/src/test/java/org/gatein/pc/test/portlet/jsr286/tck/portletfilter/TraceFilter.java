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
package org.gatein.pc.test.portlet.jsr286.tck.portletfilter;

import org.gatein.pc.test.portlet.jsr286.common.AbstractPortletFilter;

import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class TraceFilter extends AbstractPortletFilter implements ActionFilter, EventFilter, RenderFilter, ResourceFilter
{

   /** . */
   public static final int ACTION = 0;

   /** . */
   public static final int EVENT = 1;

   /** . */
   public static final int RENDER = 2;

   /** . */
   public static final int RESOURCE = 3;

   /** . */
   private static final Map<String, Integer> traces = new HashMap<String, Integer>();

   public static boolean isActive(String name, int type)
   {
      return traces.containsKey(name) && traces.get(name) == type;
   }

   private static void push(String name, int type)
   {
      if (!traces.containsKey(name))
      {
         traces.put(name, type);
      }
   }

   private static void pop(String name, int type)
   {
      if (isActive(name, type))
      {
         traces.remove(name);
      }
   }

   public void doFilter(ActionRequest req, ActionResponse resp, FilterChain chain) throws IOException, PortletException
   {
      String name = getName();

      //
      push(name, ACTION);
      try
      {
         chain.doFilter(req, resp);
      }
      finally
      {
         pop(name, ACTION);
      }
   }

   public void doFilter(EventRequest req, EventResponse resp, FilterChain chain) throws IOException, PortletException
   {
      String name = getName();

      //
      push(name, EVENT);
      try
      {
         chain.doFilter(req, resp);
      }
      finally
      {
         pop(name, EVENT);
      }
   }

   public void doFilter(RenderRequest req, RenderResponse resp, FilterChain chain) throws IOException, PortletException
   {
      String name = getName();

      //
      push(name, RENDER);
      try
      {
         chain.doFilter(req, resp);
      }
      finally
      {
         pop(name, RENDER);
      }
   }

   public void doFilter(ResourceRequest req, ResourceResponse resp, FilterChain chain) throws IOException, PortletException
   {
      String name = getName();

      //
      push(name, RESOURCE);
      try
      {
         chain.doFilter(req, resp);
      }
      finally
      {
         pop(name, RESOURCE);
      }
   }
}
