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
package org.gatein.pc.portal;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ErrorHandlingFilter implements Filter
{

   /** . */
   private final Logger log = Logger.getLogger(ErrorHandlingFilter.class);

   public void init(FilterConfig config) throws ServletException
   {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
   }

   public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
   {

      try
      {
         chain.doFilter(request, response);
      }
      catch (ServletException e)
      {
         log.error("Error during portal processing: " + e.getMessage(), e.getCause());

         //
         if (Constants.PORTLET_ERROR.equals(e.getMessage()))
         {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
         else if (Constants.UNAVAILABLE.equals(e.getMessage()))
         {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
         }
         else if (Constants.INTERNAL_ERROR.equals(e.getMessage()))
         {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
         else if (Constants.NOT_FOUND.equals(e.getMessage()))
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
         }
         else
         {
            throw e;
         }
      }

   }

   public void destroy()
   {
   }
}
