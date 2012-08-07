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

import org.gatein.pc.test.unit.protocol.response.Response;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ClientResponseContext
{

   /** . */
   private final ClientRequestContext commandContext;

   /** . */
   private final Response response;

   /** . */
   private final Map<String, Serializable> payload;

   ClientResponseContext(ClientRequestContext commandContext, Response response)
   {
      this.commandContext = commandContext;
      this.response = response;
      this.payload = new HashMap<String, Serializable>();
   }

   public ClientRequestContext getCommandContext()
   {
      return commandContext;
   }

   public Map<String, Serializable> getPayload()
   {
      return payload;
   }

   public Object getPayload(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      return payload.get(name);
   }

   public void setPayload(String name, Object value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (value == null)
      {
         payload.remove(name);
      }
      else
      {
         payload.put(name, (Serializable)value);
      }
   }

   public void removePayload(String name)
   {
      setPayload(name, null);
   }

   public Response getResponse()
   {
      return response;
   }
}
