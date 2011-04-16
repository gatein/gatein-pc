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

import org.gatein.pc.api.spi.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class AbstractRequestContext implements RequestContext
{

   /** . */
   private final HttpServletRequest clientRequest;

   public AbstractRequestContext(HttpServletRequest clientRequest)
   {
      if (clientRequest == null)
      {
         throw new IllegalArgumentException("No client request provided");
      }

      //
      this.clientRequest = clientRequest;
   }

   public String getCharacterEncoding()
   {
      return clientRequest.getCharacterEncoding();
   }

   public BufferedReader getReader() throws IOException
   {
      return clientRequest.getReader();
   }

   public InputStream getInputStream() throws IOException
   {
      return clientRequest.getInputStream();
   }

   public int getContentLength()
   {
      return clientRequest.getContentLength();
   }

   public String getContentType()
   {
      return clientRequest.getContentType();
   }
}
