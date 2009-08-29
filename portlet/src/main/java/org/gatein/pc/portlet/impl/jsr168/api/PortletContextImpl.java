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

import org.gatein.pc.portlet.impl.info.ContainerPortletApplicationInfo;
import org.gatein.pc.api.info.RuntimeOptionInfo;
import org.gatein.pc.api.spi.PortalContext;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6713 $
 */
public class PortletContextImpl implements PortletContext
{

   /** . */
   private ServletContext servletContext;

   /** . */
   private ContainerPortletApplicationInfo info;

   private static final String VERSION = PortalContext.VERSION.getName().replace(" ", "") + "/"
      + PortalContext.VERSION.getMajor() + "." + PortalContext.VERSION.getMinor() + "."
      + PortalContext.VERSION.getQualifier();

   public PortletContextImpl(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   public String getServerInfo()
   {
      return VERSION;
   }

   public PortletRequestDispatcher getRequestDispatcher(String path)
   {
      if (path == null || !path.startsWith("/"))
      {
         return null;
      }
      RequestDispatcher rd = servletContext.getRequestDispatcher(path);
      if (rd != null)
      {
         return new PortletRequestDispatcherImpl(rd, path);
      }
      else
      {
         return null;
      }
   }

   public PortletRequestDispatcher getNamedDispatcher(String name)
   {
      if (name == null)
      {
         return null;
      }
      RequestDispatcher rd = servletContext.getNamedDispatcher(name);
      if (rd != null)
      {
         return new PortletRequestDispatcherImpl(rd);
      }
      else
      {
         return null;
      }
   }

   public InputStream getResourceAsStream(String s)
   {
      return servletContext.getResourceAsStream(s);
   }

   public int getMajorVersion()
   {
      return 2;
   }

   public int getMinorVersion()
   {
      return 0;
   }

   public String getMimeType(String s)
   {
      return servletContext.getMimeType(s);
   }

   public String getRealPath(String s)
   {
      return servletContext.getRealPath(s);
   }

   public Set<String> getResourcePaths(String s)
   {
      return (Set<String>)servletContext.getResourcePaths(s);
   }

   public URL getResource(String s) throws MalformedURLException
   {
      if (s == null || !s.startsWith("/"))
      {
         throw new MalformedURLException("invalid resource");
      }
      return servletContext.getResource(s);
   }

   public Object getAttribute(String s)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("attribute name must not be null");
      }
      return servletContext.getAttribute(s);
   }

   public Enumeration<String> getAttributeNames()
   {
      return (Enumeration<String>)servletContext.getAttributeNames();
   }

   public String getInitParameter(String s)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("init parameter name must not be null");
      }
      return servletContext.getInitParameter(s);
   }

   public Enumeration<String> getInitParameterNames()
   {
      return (Enumeration<String>)servletContext.getInitParameterNames();
   }

   public void log(String s)
   {
      servletContext.log(s);
   }

   public void log(String s, Throwable throwable)
   {
      servletContext.log(s, throwable);
   }

   public void removeAttribute(String s)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("attribute name must not be null");
      }
      servletContext.removeAttribute(s);
   }

   public void setAttribute(String s, Object o)
   {
      servletContext.setAttribute(s, o);
   }

   public String getPortletContextName()
   {
      return servletContext.getServletContextName();
   }

   public Enumeration<String> getContainerRuntimeOptions()
   {
      return Collections.enumeration(RuntimeOptionInfo.SUPPORTED_OPTIONS);
   }
}
