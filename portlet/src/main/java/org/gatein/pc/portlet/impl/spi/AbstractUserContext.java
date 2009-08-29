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

import org.gatein.common.util.Tools;
import org.gatein.common.invocation.resolver.PrincipalAttributeResolver;
import org.gatein.common.invocation.resolver.MapAttributeResolver;
import org.gatein.common.invocation.AttributeResolver;
import org.gatein.pc.api.spi.UserContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Collections;
import java.util.Locale;
import java.util.List;
import java.util.Enumeration;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class AbstractUserContext implements UserContext
{

   /** . */
   private static final Map<String, String> EMPTY_STRING_TO_STRING_MAP = Collections.emptyMap();

   /** . */
   private static final List<Locale> EMPTY_LOCALE_LIST = Collections.emptyList();

   /** . */
   private final String id;

   /** . */
   private final HttpServletRequest clientRequest;

   /** . */
   private final AttributeResolver attributeResolver;

   public AbstractUserContext(HttpServletRequest clientRequest) throws IllegalArgumentException
   {
      if (clientRequest == null)
      {
         throw new IllegalArgumentException("No client request provided");
      }
      this.id = clientRequest.getRemoteUser();
      this.clientRequest = clientRequest;
      this.attributeResolver = new PrincipalAttributeResolver(clientRequest);
   }

   public AbstractUserContext(String id) throws IllegalArgumentException
   {
      if (id == null)
      {
         throw new IllegalArgumentException("No user id provided");
      }
      this.id = id;
      this.clientRequest = null;
      this.attributeResolver = new MapAttributeResolver();
   }

   public AbstractUserContext()
   {
      this.id = null;
      this.clientRequest = null;
      this.attributeResolver = new MapAttributeResolver();
   }

   /**
    * Returns the user id or null if none was provided.
    */
   public String getId()
   {
      return id;
   }

   /**
    * Returns an immutable empty map.
    */
   public Map<String, String> getInformations()
   {
      return EMPTY_STRING_TO_STRING_MAP;
   }

   /**
    * Returns the client request locale or <code>Locale.ENGLISH</code> if no request was provided.
    */
   public Locale getLocale()
   {
      return clientRequest != null ? clientRequest.getLocale() : Locale.ENGLISH;
   }

   /**
    * Returns the client request locales or an empty list if no request was provided.
    */
   @SuppressWarnings("unchecked")
   public List<Locale> getLocales()
   {
      if (clientRequest == null)
      {
         return EMPTY_LOCALE_LIST;
      }
      else
      {
         return Tools.toList((Enumeration<Locale>)clientRequest.getLocales());
      }
   }

   public void setAttribute(String attrKey, Object attrValue)
   {
      attributeResolver.setAttribute(attrKey, attrValue);
   }

   public Object getAttribute(String attrKey)
   {
      return attributeResolver.getAttribute(attrKey);
   }
}
