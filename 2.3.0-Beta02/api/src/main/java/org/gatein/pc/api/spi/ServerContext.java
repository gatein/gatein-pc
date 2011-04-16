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

import org.gatein.wci.RequestDispatchCallback;
import org.gatein.wci.ServletContainer;

import javax.servlet.ServletContext;

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
    * @param servletContainer the servlet container spi
    * @param targetServletContext the target servlet context
    * @param callback the call back to be done after dispatch
    * @param handback the hand back object to provide after dispatch to the call back
    * @return the call back returned object
    * @throws Exception any exception
    */
   Object dispatch(ServletContainer servletContainer, ServletContext targetServletContext, RequestDispatchCallback callback, Object handback) throws Exception;

}
