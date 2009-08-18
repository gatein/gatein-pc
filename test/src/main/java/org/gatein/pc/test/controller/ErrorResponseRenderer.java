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

package org.gatein.pc.test.controller;

import org.apache.log4j.Logger;
import org.gatein.pc.api.invocation.response.ErrorResponse;

import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ErrorResponseRenderer implements Renderer
{

   /** . */
   private static final Logger log = Logger.getLogger(ErrorResponseRenderer.class);

   /** . */
   private ErrorResponse response;

   public ErrorResponseRenderer(ErrorResponse response)
   {
      this.response = response;
   }

   public void render(RendererContext context) throws IOException
   {
      if (response.getCause() != null)
      {
         log.error("Portlet action threw an error: " + response.getMessage(), response.getCause());
      }
      else
      {
         log.error("Portlet action threw an error: " + response.getMessage());
      }

      //
      context.getClientResponse().sendError(404, response.getMessage());
   }
}
