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
package org.gatein.pc.test.unit.protocol;

import org.gatein.pc.test.unit.protocol.request.Request;
import org.gatein.pc.test.unit.protocol.response.Response;

import java.util.HashMap;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ClientRequestContext
{

   /** The previous response if not null. */
   private ClientResponseContext responseContext;

   /** . */
   private final int requestCount;

   /** The command to invoke. */
   private Request command;

   /** . */
   private final HashMap<String, Serializable> payload;

   public ClientRequestContext(ClientResponseContext responseContext, Request command)
   {
      this.responseContext = responseContext;
      this.requestCount = responseContext.getCommandContext().requestCount + 1;
      this.command = command;
      this.payload = new HashMap<String, Serializable>(responseContext.getPayload());
   }

   public ClientRequestContext(Request command)
   {
      this.responseContext = null;
      this.requestCount = -1;
      this.command = command;
      this.payload = new HashMap<String, Serializable>();
   }

   public ClientRequestContext(ClientRequestContext commandContext, Request command)
   {
      HashMap<String, Serializable> payload = commandContext.responseContext != null ? new HashMap<String, Serializable>(commandContext.responseContext.getPayload()) : new HashMap<String, Serializable>();

      //
      this.responseContext = commandContext.responseContext;
      this.requestCount = commandContext.requestCount + 1;
      this.command = command;
      this.payload = payload;
   }

   public ClientRequestContext(ClientRequestContext commandContext)
   {
      HashMap<String, Serializable> payload = commandContext.responseContext != null ? new HashMap<String, Serializable>(commandContext.responseContext.getPayload()) : new HashMap<String, Serializable>();

      //
      this.responseContext = commandContext.responseContext;
      this.requestCount = commandContext.requestCount + 1;
      this.command = commandContext.command;
      this.payload = payload;
   }

   public HashMap<String, Serializable> getPayload()
   {
      return payload;
   }

   public int getRequestCount()
   {
      return requestCount;
   }

   public ClientResponseContext createResponseContext(Response response)
   {
      return new ClientResponseContext(this, response);
   }

   public ClientResponseContext getResponseContext()
   {
      return responseContext;
   }

   public Request getCommand()
   {
      return command;
   }
}
