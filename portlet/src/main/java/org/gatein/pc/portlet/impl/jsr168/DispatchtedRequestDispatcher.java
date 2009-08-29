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
package org.gatein.pc.portlet.impl.jsr168;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponseWrapper;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class DispatchtedRequestDispatcher implements RequestDispatcher
{

   /** . */
   private final RequestDispatcher realDispatcher;

   /** . */
   private final String path;

   public DispatchtedRequestDispatcher(RequestDispatcher realDispatcher, String path)
   {
      this.realDispatcher = realDispatcher;
      this.path = path;
   }

   public void include(ServletRequest req, ServletResponse resp) throws ServletException, IOException
   {
      DispatchedHttpServletRequest dreq = unwrap(req);
      unwrap(resp);

      //
      dreq.pushDispatch(new Dispatch(DispatchType.INCLUDE, path));
      try
      {
         realDispatcher.include(req, resp);
      }
      finally
      {
         dreq.popDispatch();
      }
   }

   public void forward(ServletRequest req, ServletResponse resp) throws ServletException, IOException
   {
      DispatchedHttpServletRequest dreq = unwrap(req);
      unwrap(resp);

      //
      dreq.pushDispatch(new Dispatch(DispatchType.FORWARD, path));
      try
      {
         realDispatcher.include(req, resp);
      }
      finally
      {
         dreq.popDispatch();
      }
   }

   private static DispatchedHttpServletRequest unwrap(ServletRequest wrapped)
   {
      while (true)
      {
         if (wrapped instanceof DispatchedHttpServletRequest)
         {
            return (DispatchedHttpServletRequest)wrapped;
         }
         else if (wrapped instanceof ServletRequestWrapper)
         {
            ServletRequestWrapper wrapper = (ServletRequestWrapper)wrapped;
            wrapped = wrapper.getRequest();
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
   }

   private static DispatchedHttpServletResponse unwrap(ServletResponse wrapped)
   {
      while (true)
      {
         if (wrapped instanceof DispatchedHttpServletResponse)
         {
            return (DispatchedHttpServletResponse)wrapped;
         }
         else if (wrapped instanceof ServletResponseWrapper)
         {
            ServletResponseWrapper wrapper = (ServletResponseWrapper)wrapped;
            wrapped = wrapper.getResponse();
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
   }
}
