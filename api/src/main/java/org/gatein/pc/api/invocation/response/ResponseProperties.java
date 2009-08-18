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
package org.gatein.pc.api.invocation.response;

import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.util.SimpleMultiValuedPropertyMap;
import org.w3c.dom.Element;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class ResponseProperties
{

   /** . */
   private MultiValuedPropertyMap<String> transportHeaders = new SimpleMultiValuedPropertyMap<String>();

   /** . */
   private MultiValuedPropertyMap<Element> markupHeaders = new SimpleMultiValuedPropertyMap<Element>();

   /** . */
   private List<Cookie> cookies = new LinkedList<Cookie>();

   public ResponseProperties()
   {
   }

   public MultiValuedPropertyMap<String> getTransportHeaders()
   {
      return transportHeaders;
   }

   public MultiValuedPropertyMap<Element> getMarkupHeaders()
   {
      return markupHeaders;
   }

   public List<Cookie> getCookies()
   {
      return cookies;
   }

   public void append(ResponseProperties appended)
   {
      if (appended == null)
      {
         throw new IllegalArgumentException();
      }

      //
      transportHeaders.append(appended.transportHeaders);
      markupHeaders.append(appended.markupHeaders);
      cookies.addAll(appended.cookies);
   }

   public void clear()
   {
      transportHeaders.clear();
      markupHeaders.clear();
      cookies.clear();
   }
}