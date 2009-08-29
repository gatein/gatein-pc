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
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.portlet.impl.jsr168.PortletApplicationImpl;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.ContainerURL;

import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURLGenerationListener;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class BaseURLImpl implements BaseURL
{

   /** . */
   private static final URLFormat[][] FORMATS = {
      {
         new URLFormat(null, null, true, null),
         new URLFormat(false, null, true, null),
         new URLFormat(true, null, true, null)
      },
      {
         new URLFormat(null, null, true, true),
         new URLFormat(false, null, true, true),
         new URLFormat(true, null, true, true)
      }
   };

   /** . */
   private static final int SECURE_NOT_DETERMINED = 0;

   /** . */
   private static final int NOT_SECURE = 1;

   /** . */
   private static final int SECURE = 2;

   /** . */
   protected final PortletInvocation inv;

   /** . */
   protected final PortletRequestImpl preq;

   /** . */
   protected int secure;

   /** . */
   protected final boolean filterable;

   protected BaseURLImpl(
      PortletInvocation invocation,
      PortletRequestImpl preq,
      boolean filterable)
   {
      this.inv = invocation;
      this.preq = preq;
      this.filterable = filterable;
      this.secure= SECURE_NOT_DETERMINED;
   }

   protected BaseURLImpl(BaseURLImpl original)
   {
      this.inv = original.inv;
      this.preq = original.preq;
      this.filterable = false;
      this.secure= SECURE_NOT_DETERMINED;
   }

   /** @throws IllegalArgumentException if the name is null */
   public void setParameter(String name, String value)
   {
      InternalContainerURL url = getContainerURL();
      url.setParameter(name, value);
   }

   public void setParameter(String name, String[] values)
   {
      InternalContainerURL url = getContainerURL();
      url.setParameter(name, values);
   }

   public void setParameters(Map<String, String[]> parameters)
   {
      InternalContainerURL url = getContainerURL();
      url.setParameters(parameters);
   }

   public void setSecure(boolean secure) throws PortletSecurityException
   {
      this.secure = secure ? SECURE : NOT_SECURE;
   }

   public Map<String, String[]> getParameterMap()
   {
      InternalContainerURL url = getContainerURL();
      return url.getParameters();
   }

   public void addProperty(String s, String s1)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("property name cannot be null");
      }

      //TODO:

   }

   public void setProperty(String s, String s1)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("property name cannot be null");
      }

      //TODO:
   }

   private InternalContainerURL blah()
   {
      PortletURLGenerationListener listener = ((PortletApplicationImpl)preq.container.getPortletApplication()).getListener(PortletURLGenerationListener.class);

      //
      InternalContainerURL url;
      if (listener != null)
      {
         // Create a safe clone
         BaseURLImpl clone = createClone();
         clone.filter(listener);
         url = clone.getContainerURL();
      }
      else
      {
         url = getContainerURL();
      }

      //
      return url;
   }

   public String toString()
   {
      InternalContainerURL url = blah();

      //
      URLFormat format = FORMATS[0][secure];

      //
      PortletInvocationContext responseContext = inv.getContext();

      //
      return responseContext.renderURL(url, format);
   }

   public void write(Writer writer) throws IOException
   {
      write(writer, false);
   }

   public void write(Writer writer, boolean b) throws IOException
   {
      InternalContainerURL url = blah();

      //
      URLFormat format = FORMATS[b ? 1 : 0][secure];

      //
      PortletInvocationContext responseContext = inv.getContext();

      //
      responseContext.renderURL(writer, url, format);
   }


   protected abstract void filter(PortletURLGenerationListener listener);

   protected abstract BaseURLImpl createClone();

   protected abstract InternalContainerURL getContainerURL();

   protected static abstract class InternalContainerURL implements ContainerURL
   {

      protected abstract void setParameter(String name, String value);

      protected abstract void setParameter(String name, String[] values);

      protected abstract void setParameters(Map<String, String[]> parameters);

      protected abstract Map<String, String[]> getParameters();

   }

}
