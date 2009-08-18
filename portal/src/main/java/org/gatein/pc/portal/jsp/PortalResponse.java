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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.api.PortletInvokerException;

import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Expose stuff common to action and render response.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortalResponse extends HttpServletResponseWrapper
{

   /** . */
   private int count = 0;

   /** . */
   private final String pageId;

   public PortalResponse(HttpServletRequest request, HttpServletResponse response) throws PortletInvokerException
   {
      super(response);

      // We don't keep a ref to the request since this will be used mainly during in a dispatch to a JSP
      // and the info returned by a request after a dispatch are not the same than before (like the path info).
      String pageId = request.getRequestURI();

      //
      this.pageId = pageId;
   }

   /**
    * todo : add on JSP portlet tag a notion of 'id', because this id generation relies on the portlet
    * rendering order on the page which could not be the same between 2 requests.
    */
   public String nextId()
   {
      return pageId + "/" + count++;
   }
}
