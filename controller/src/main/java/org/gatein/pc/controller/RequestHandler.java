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

package org.gatein.pc.controller;

import org.apache.log4j.Logger;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 10014 $
 */
abstract class RequestHandler<T extends ControllerRequest>
{
   /** . */
   protected static final Logger log = Logger.getLogger(RequestHandler.class);

   /** . */
   protected PortletController controller;

   /** . */
   protected final Class<T> t;

   protected RequestHandler(Class<T> t, PortletController controller)
   {
      this.t = t;
      this.controller = controller;
   }

   public ControllerResponse handle(PortletControllerContext controllerContext, ControllerRequest request) throws PortletInvokerException
   {
      if (!t.isInstance(request))
      {
         throw new IllegalArgumentException("Request " + request + " cannot be handled by this handler");
      }

      //
      T req = t.cast(request);
      PortletInvocationResponse response = invoke(controllerContext, req);

      return processResponse(controllerContext, req, response);
   }

   abstract ControllerResponse processResponse(PortletControllerContext controllerContext, T request, PortletInvocationResponse response) throws PortletInvokerException;

   abstract PortletInvocationResponse invoke(PortletControllerContext controllerContext, T controllerRequest) throws PortletInvokerException;
}
