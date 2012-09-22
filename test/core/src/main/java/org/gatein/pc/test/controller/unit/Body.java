/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2012, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.test.controller.unit;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * The body of a request.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class Body
{

   /** . */
   private final String characterEncoding;

   private Body(String characterEncoding)
   {
      this.characterEncoding = characterEncoding;
   }

   public String getCharacterEncoding()
   {
      return characterEncoding;
   }

   public static class Form extends Body
   {

      /** . */
      private final Map<String, String[]> parameters;

      public Form(String characterEncoding, Map<String, String[]> parameters)
      {
         super(characterEncoding);

         //
         if (parameters == null)
         {
            throw new IllegalArgumentException();
         }

         //
         this.parameters = parameters;
      }

      public Map<String, String[]> getParameters()
      {
         return parameters;
      }
   }

   public static class Raw extends Body
   {

      /** . */
      private final HttpServletRequest request;

      /** . */
      private boolean consumed;

      public Raw(String characterEncoding, HttpServletRequest request)
      {
         super(characterEncoding);

         //
         this.request = request;
      }

      public InputStream getInputStream() throws IOException
      {
         if (consumed)
         {
            throw new IllegalStateException();
         }
         consumed = true;
         return request.getInputStream();
      }

      public BufferedReader getReader() throws IOException
      {
         if (consumed)
         {
            throw new IllegalStateException();
         }
         consumed = true;
         return request.getReader();
      }
   }

}
