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
package javax.portlet;

import java.io.IOException;

/**
 * The <code>PortletRequestDispatcher</code> interface defines an object that receives requests from the client and
 * sends them to the specified resources (such as a servlet, HTML file, or JSP file) on the server. The portlet
 * container creates the <code>PortletRequestDispatcher</code> object, which is used as a wrapper around a server
 * resource located at a particular path or given by a particular name.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PortletRequestDispatcher
{
   /**
    * Includes the content of a resource (servlet, JSP page, HTML file) in the response. In essence, this method enables
    * programmatic server-side includes.
    * <p/>
    * The included servlet cannot set or change the response status code or set headers; any attempt to make a change is
    * ignored.
    *
    * @param request  a {@link RenderRequest} object that contains the client request
    * @param response a {@link RenderResponse} object that contains the render response
    * @throws PortletException    if the included resource throws a ServletException, or other exceptions that are not
    *                             Runtime- or IOExceptions.
    * @throws java.io.IOException if the included resource throws this exception
    */
   void include(RenderRequest request, RenderResponse response) throws PortletException, IOException;
}
