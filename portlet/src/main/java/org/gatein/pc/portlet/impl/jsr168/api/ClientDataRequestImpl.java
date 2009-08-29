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

import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.spi.RequestContext;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;

import javax.portlet.ClientDataRequest;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class ClientDataRequestImpl extends PortletRequestImpl implements ClientDataRequest 
{

   /** . */
   protected final RequestContext requestContext;

   public ClientDataRequestImpl(PortletContainerImpl container, ActionInvocation invocation)
   {
      super(container, invocation);

      //
      this.requestContext = invocation.getRequestContext();
   }

   public ClientDataRequestImpl(PortletContainerImpl container, ResourceInvocation invocation)
   {
      super(container, invocation);

      //
      this.requestContext = invocation.getRequestContext();
   }

   public InputStream getPortletInputStream() throws IOException
   {
      if ("application/x-www-form-urlencoded".equals(requestContext.getContentType()))
      {
         throw new IllegalStateException();
      }
      return requestContext.getInputStream();
   }

   public BufferedReader getReader() throws UnsupportedEncodingException, IOException
   {
      if ("application/x-www-form-urlencoded".equals(requestContext.getContentType()))
      {
         throw new IllegalStateException();
      }
      return requestContext.getReader();
   }

   public String getCharacterEncoding()
   {
      return requestContext.getCharacterEncoding();
   }

   public String getContentType()
   {
      return requestContext.getContentType();
   }

   public int getContentLength()
   {
      return requestContext.getContentLength();
   }

   public void setCharacterEncoding(String s) throws UnsupportedEncodingException
   {
      // This method is frankly stupid
      throw new IllegalStateException("called after the body has been read");
      // req.setCharacterEncoding(s);
   }

   public String getMethod()
   {
      return clientContext.getMethod();
   }
}
