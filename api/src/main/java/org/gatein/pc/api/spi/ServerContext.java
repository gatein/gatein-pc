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
package org.gatein.pc.api.spi;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Defines the request context contract. It's usage is related to the Servlet Container operational
 * environment.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6929 $
 */
public interface ServerContext
{
   /**
    * Return the scheme value.
    *
    * @return the scheme
    */
   String getScheme();

   /**
    * Return the server name value.
    *
    * @return the server name
    */
   String getServerName();

   /**
    * Return the server port value.
    *
    * @return the server port
    */
   int getServerPort();

   /**
    * Delegate to the request context the dispatching to the target servlet context using the specified spi elements.
    *
    * @param target the target servlet context
    * @param callable the callable
    * @throws Exception any exception
    */
   void dispatch(ServletContext target, HttpServletRequest request, HttpServletResponse response, Callable callable) throws Exception;

   /**
    * The dispatch callable contract.
    */
   interface Callable
   {

      /**
       * The callback
       *
       * @param context the servlet context
       * @param request the servlet request
       * @param response the servle response
       * @throws ServletException any exception
       */
      void call(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

   }
}
