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
package org.gatein.pc.test.portlet.jsr286.ext.portletfilter;

import org.gatein.pc.test.portlet.jsr286.common.AbstractRenderFilter;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.filter.FilterChain;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class FilterConfigFilter extends AbstractRenderFilter
{

   /** . */
   public static String name;

   /** . */
   public static Map<String, String> parameters;

   public void init(javax.portlet.filter.FilterConfig config) throws PortletException
   {
      super.init(config);

      //
      HashMap<String, String> tmp = new HashMap<String, String>();
      for (Enumeration e = config.getInitParameterNames();e.hasMoreElements();)
      {
         String name = (String)e.nextElement();
         String value = config.getInitParameter(name);
         tmp.put(name, value);
      }

      //
      name = config.getFilterName();
      parameters = tmp;
   }

   public void doFilter(RenderRequest req, RenderResponse resp, FilterChain chain) throws IOException, PortletException
   {
      chain.doFilter(req, resp);
   }
}
