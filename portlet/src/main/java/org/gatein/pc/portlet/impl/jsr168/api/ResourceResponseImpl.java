/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.api.cache.CacheControl;
import org.gatein.pc.api.cache.CacheLevel;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;

import javax.portlet.PortletURL;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ResourceResponseImpl extends MimeResponseImpl implements ResourceResponse
{

   /** . */
   private final CacheLevel cacheability;

   /** . */
   private String encoding;

   public ResourceResponseImpl(ResourceInvocation invocation, PortletRequestImpl preq)
   {
      super(invocation, preq);

      //
      CacheLevel cacheLevel = invocation.getCacheLevel();
      if (cacheLevel == null)
      {
         cacheLevel = CacheLevel.PAGE;
      }
      this.cacheability = cacheLevel;
      this.encoding = encoding;
   }

   public void setLocale(Locale locale)
   {
      // TODO: setLocale should also set the character encoding according to the mapping done in web.xml

      if (locale == null)
      {
         throw new IllegalArgumentException("Locale cannot be null");
      }
      if (locale.getCountry().length() == 0)
      {
         addProperty("Content-Language", locale.getLanguage());
      }
      else
      {
         addProperty("Content-Language", locale.getLanguage() + "-" + locale.getCountry());
      }
   }

   @Override
   public void setContentType(String contentType)
   {
      // todo : it should be possible to set the encoding via the content type
      // although it is not possible on the RenderResponse
      super.setContentType(contentType);
   }

   public void setCharacterEncoding(String charset)
   {
      this.encoding = charset;
   }

   @Override
   public String getCharacterEncoding()
   {
      return encoding;
   }

   public void setContentLength(int i)
   {
      addProperty("Content-Length", "" + i);
   }

   public PortletURL createActionURL()
   {
      checkCacheLevel();
      return super.createActionURL();
   }

   public PortletURL createRenderURL()
   {
      checkCacheLevel();
      return super.createRenderURL();
   }

   private void checkCacheLevel()
   {
      if (cacheability != CacheLevel.PAGE)
      {
         throw new IllegalStateException("A resource cannot create URLs if the cache level hasn't been set to "
            + ResourceURL.PAGE + " as mandated by JSR-286 PLT.13.7. Cache level was: " + cacheability);
      }
   }

   @Override
   protected ContentResponse createResponse(ResponseProperties props, Map<String, Object> attrs, String contentType, byte[] bytes, CacheControl cacheControl)
   {
      return new ContentResponse(props, attrs, contentType, encoding,  bytes, cacheControl);
   }

   @Override
   protected ContentResponse createResponse(ResponseProperties props, Map<String, Object> attrs, String contentType, String chars, CacheControl cacheControl)
   {
      return new ContentResponse(props, attrs, contentType, encoding, chars, cacheControl);
   }

   @Override
   protected ContentResponse createResponse(ResponseProperties props, Map<String, Object> attrs, String contentType, CacheControl cacheControl)
   {
      return new ContentResponse(props, attrs, contentType, cacheControl);
   }
}
