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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.reflect.UndeclaredThrowableException;
import javax.portlet.PortletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6697 $
 */
public abstract class PortletResponseImpl implements PortletResponse
{

   /** Keep a document builder in a thread local as it can create contention during its creation. */
   private static final ThreadLocal<DocumentBuilder> builder = new ThreadLocal<DocumentBuilder>()
   {
      protected DocumentBuilder initialValue()
      {
         try
         {
           return DocumentBuilderFactory.newInstance().newDocumentBuilder();
         }
         catch (ParserConfigurationException e)
         {
           throw new UndeclaredThrowableException(e);
         }
      }
   };

   /** . */
   protected final PortletInvocation invocation;

   /** . */
   protected final PortletRequestImpl preq;

   /** . */
   private Document doc;

   /** . */
   private ResponseProperties properties;

   /** . */
   private HttpServletResponseWrapper realResp;

   protected PortletResponseImpl(PortletInvocation invocation, PortletRequestImpl preq)
   {
      this.invocation = invocation;
      this.preq = preq;
      this.realResp = new HttpServletResponseWrapper(invocation.getDispatchedResponse());
   }

   public abstract PortletInvocationResponse getResponse();

   public String encodeURL(String url)
   {
      if (url == null)
      {
         throw new IllegalArgumentException("URL must not be null");
      }
      return invocation.getContext().encodeResourceURL(url);
   }

   public void addProperty(String key, String value) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Name cannot be null");
      }
      if (value == null)
      {
         throw new IllegalArgumentException("Value cannot be null");
      }

      //
      getProperties().getTransportHeaders().addValue(key, value);
   }

   public void setProperty(String key, String value) throws IllegalArgumentException
   {
      if (key == null)
      {
         throw new IllegalArgumentException("Name cannot be null");
      }
      if (value == null)
      {
         throw new IllegalArgumentException("Value cannot be null");
      }
      getProperties().getTransportHeaders().setValue(key, value);
   }

   public void addProperty(Cookie cookie)
   {
      getProperties().getCookies().add(cookie);
   }

   public void addProperty(String key, Element element)
   {
      getProperties().getMarkupHeaders().addValue(key, element);
   }

   public Element createElement(String tagName) throws DOMException
   {
      if (doc == null)
      {
         doc = builder.get().newDocument();
      }

      //
      return doc.createElement(tagName);
   }

   public String getNamespace()
   {
      return invocation.getWindowContext().getNamespace();
   }

   public final HttpServletResponseWrapper getRealResponse()
   {
      return realResp;
   }

   protected final ResponseProperties getProperties()
   {
      return getProperties(true);
   }

   protected final ResponseProperties getProperties(boolean create)
   {
      if (properties == null && create)
      {
         properties = new ResponseProperties();
      }
      return properties;
   }
}
