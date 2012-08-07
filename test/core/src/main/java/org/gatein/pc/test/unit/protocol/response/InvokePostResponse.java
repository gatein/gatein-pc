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
package org.gatein.pc.test.unit.protocol.response;

import org.gatein.pc.test.unit.protocol.Body;

import java.net.URI;

/**
 * The portlet wants to invoke a post.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class InvokePostResponse extends InvokeMethodResponse
{

   /** . */
   public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

   /** . */
   public static final String MULTIPART_FORM_DATA = "multipart/form-data";

   /** The content type. */
   private String contentType;

   /** The post body. */
   private Body body;

   public InvokePostResponse(String uri)
   {
      super(uri);
   }

   public InvokePostResponse(URI uri)
   {
      super(uri);
   }

   public Body getBody()
   {
      return body;
   }

   public void setBody(Body body)
   {
      this.body = body;
   }

   public String getContentType()
   {
      return contentType;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public String toString()
   {
      return "InvokePost[uri=" + getURI() + "]";
   }
}
