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
package org.gatein.pc.portlet.impl.spi;

import org.gatein.pc.api.spi.ServerContext;
import org.gatein.wci.RequestDispatchCallback;
import org.gatein.wci.ServletContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6929 $
 */
public class AbstractServerContext implements ServerContext
{

   /** . */
   private HttpServletRequest clientRequest;

   /** . */
   private HttpServletResponse clientResponse;

   public AbstractServerContext(HttpServletRequest clientRequest, HttpServletResponse clientResponse)
   {
      this.clientRequest = clientRequest;
      this.clientResponse = clientResponse;
   }

   public String getScheme()
   {
      return clientRequest.getScheme();
   }

   public String getServerName()
   {
      return clientRequest.getServerName();
   }

   public int getServerPort()
   {
      return clientRequest.getServerPort();
   }

   public Object dispatch(ServletContainer servletContainer, ServletContext targetServletContext, RequestDispatchCallback callback, Object handback) throws Exception
   {
      return servletContainer.include(targetServletContext, clientRequest, clientResponse, callback, handback);
   }

   public HttpServletResponse getResponse()
   {
      return clientResponse;
   }


}
