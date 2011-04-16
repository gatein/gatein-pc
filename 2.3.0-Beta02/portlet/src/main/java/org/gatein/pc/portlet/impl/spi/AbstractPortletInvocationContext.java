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
package org.gatein.pc.portlet.impl.spi;

import org.gatein.common.util.MarkupInfo;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.spi.PortletInvocationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * An abstract implementation that relies on the a request and response provided by the client (i.e the portal).
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5064 $
 */
public abstract class AbstractPortletInvocationContext implements PortletInvocationContext
{

   /** . */
   protected final MarkupInfo markupInfo;

   protected AbstractPortletInvocationContext(MarkupInfo markupInfo)
   {
      this.markupInfo = markupInfo;
   }

   /**
    * Return the client request.
    *
    * @return the client request
    * @throws IllegalStateException if the client response is not available
    */
   public abstract HttpServletRequest getClientRequest() throws IllegalStateException;

   /**
    * Return the client response.
    *
    * @return the client response
    * @throws IllegalStateException if the client response is not available
    */
   public abstract HttpServletResponse getClientResponse() throws IllegalStateException;

   /**
    * Validate the url and then delegate the encoding of the url to the client response.
    *
    * @return the encoded url
    */
   public String encodeResourceURL(String url) throws IllegalArgumentException
   {
      if (url == null)
      {
         throw new IllegalArgumentException("URL cannot be null");
      }
      if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/"))
      {
         return getClientResponse().encodeURL(url);
      }
      throw new IllegalArgumentException("Invalid URL " + url);
   }

   public MarkupInfo getMarkupInfo()
   {
      return markupInfo;
   }

   /**
    * Delegates the URL rendition to the method {@link #renderURL(org.gatein.pc.api.ContainerURL , org.gatein.pc.api.URLFormat)}
    * and then invoke the {@link java.io.Writer#write(String)} method with the returned result.
    *
    * @see org.gatein.pc.api.spi.PortletInvocationContext#renderURL(org.gatein.pc.api.ContainerURL ,
    *      org.gatein.pc.api.URLFormat)
    */
   public void renderURL(Writer writer, ContainerURL containerURL, URLFormat format) throws IOException
   {
      String renderedURL = renderURL(containerURL, format);

      //
      writer.write(renderedURL);
   }
}
