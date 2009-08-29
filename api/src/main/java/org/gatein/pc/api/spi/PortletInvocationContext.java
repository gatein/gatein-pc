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
package org.gatein.pc.api.spi;

import java.io.IOException;
import java.io.Writer;

import org.gatein.common.util.MarkupInfo;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.URLFormat;

/**
 * Contract that defines what input/services the caller of a portlet container must provide.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5064 $
 */
public interface PortletInvocationContext
{

   /**
    * todo : move me to client content // see if it is still needed with CC/PP
    *
    * Return information about the underlying http wire this invocation is performed.
    *
    * @return the stream info
    */
   MarkupInfo getMarkupInfo();

   /**
    * <p>Encodes the specified URL by including the session ID in it, or, if encoding is not needed, returns the URL
    * unchanged. The implementation of this method includes the logic to determine whether the session ID needs to be
    * encoded in the URL. For example, if the browser supports cookies, or session tracking is turned off, URL encoding
    * is unnecessary.</p>
    * <p/>
    * <p>For robust session tracking, all URLs emitted by a servlet should be run through this method. Otherwise, URL
    * rewriting cannot be used with browsers which do not support cookies.</p>
    *
    * @param url the url to be encoded
    * @return the encoded URL if encoding is needed, the unchanged URL otherwise
    * @throws IllegalArgumentException if the url is not valid or null
    */
   String encodeResourceURL(String url) throws IllegalArgumentException;

   /**
    * Renders a container URL.
    *
    * @param containerURL the portlet url
    * @param format the url format
    * @return the rendered url
    */
   String renderURL(ContainerURL containerURL, URLFormat format);

   /**
    * Renders a container URL.
    *
    * @param writer the writer
    * @param containerURL the portlet url
    * @param format the url format
    * @throws IOException any IOException thrown by the writer
    */
   void renderURL(Writer writer, ContainerURL containerURL, URLFormat format) throws IOException;
}
