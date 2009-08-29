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

import org.gatein.pc.api.spi.ClientContext;
import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.util.SimpleMultiValuedPropertyMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class AbstractClientContext implements ClientContext
{

   /** . */
   private static final List<Cookie> NO_COOKIES = Collections.emptyList();

   /** . */
   private final String method;

   /** . */
   private final MultiValuedPropertyMap<String> headers;

   /** . */
   private final List<Cookie> cookies;

   public AbstractClientContext(HttpServletRequest request)
   {
      this(request, NO_COOKIES);
   }

   @SuppressWarnings("unchecked")
   public AbstractClientContext(HttpServletRequest request, List<Cookie> additionalCookies)
   {
      MultiValuedPropertyMap<String> headers = new SimpleMultiValuedPropertyMap<String>();
      for (Enumeration<String> e = request.getHeaderNames();e.hasMoreElements();)
      {
         String headerName = e.nextElement();
         for (Enumeration<String> f = request.getHeaders(headerName);f.hasMoreElements();)
         {
            String headerValue = f.nextElement();
            headers.addValue(headerName, headerValue);
         }
      }

      //
      Cookie[] requestCookies = request.getCookies();

      //
      int length = (requestCookies == null ? 0 : requestCookies.length) + (additionalCookies == null ? 0 : additionalCookies.size());
      List<Cookie> cookies;
      if (length == 0)
      {
         cookies = Collections.emptyList();
      }
      else
      {
         cookies = new ArrayList<Cookie>(length);
         if (requestCookies != null)
         {
            for (Cookie cookie : requestCookies)
            {
               Cookie copy = (Cookie)cookie.clone();
               cookies.add(copy);
            }
         }
         if (additionalCookies != null)
         {
            for (Cookie cookie : additionalCookies)
            {
               Cookie copy = (Cookie)cookie.clone();
               cookies.add(copy);
            }
         }
      }

      //
      this.headers = headers;
      this.method = request.getMethod();
      this.cookies = cookies;
   }

   public String getMethod()
   {
      return method;
   }

   public MultiValuedPropertyMap<String> getProperties()
   {
      return headers;
   }

   public List<Cookie> getCookies()
   {
      return cookies;
   }
}
