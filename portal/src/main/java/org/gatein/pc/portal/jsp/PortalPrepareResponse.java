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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.api.PortletInvokerException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletOutputStream;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortalPrepareResponse extends PortalResponse
{

   /** . */
   private String contentType;

   /** . */
   private Locale locale;

   /** . */
   private String characterEncoding;

   /** . */
   private int bufferSize;

   /** . */
   private ServletOutputStream outputStream;

   /** . */
   private PrintWriter writer;

   /** . */
   private Map<String, WindowDef> windowDefs;

   /** . */
   private Map<QName, PageParameterDef> paramDefs;

   public PortalPrepareResponse(HttpServletRequest request, HttpServletResponse response) throws PortletInvokerException
   {
      super(request, response);

      //
      this.windowDefs = new HashMap<String, WindowDef>();
      this.paramDefs = new HashMap<QName, PageParameterDef>();
   }

   public Set<String> getWindowIds()
   {
      return windowDefs.keySet();
   }

   public WindowDef getWindowDef(String windowId)
   {
      return windowDefs.get(windowId);
   }

   public void addWindowDef(String windowId, WindowDef portlet)
   {
      windowDefs.put(windowId, portlet);
   }

   public Set<QName> getPageParameterNames()
   {
      return paramDefs.keySet();
   }

   public PageParameterDef getPageParameterDef(QName name)
   {
      return paramDefs.get(name);
   }

   public void setPageParameterDef(PageParameterDef parameterDef)
   {
      paramDefs.put(parameterDef.getName(), parameterDef);
   }

   public ServletOutputStream getOutputStream() throws IOException
   {
      if (writer != null)
      {
         throw new IllegalStateException();
      }
      if (outputStream == null)
      {
         outputStream = new ServletOutputStream()
         {
            public void write(int b) throws IOException
            {
            }
         };
      }
      return outputStream;
   }

   public PrintWriter getWriter() throws IOException
   {
      if (outputStream != null)
      {
         throw new IllegalStateException();
      }
      if (writer == null)
      {
         Writer tmp = new Writer()
         {
            public void write(char cbuf[], int off, int len) throws IOException
            {
            }
            public void flush() throws IOException
            {
            }
            public void close() throws IOException
            {
            }
         };
         writer = new PrintWriter(tmp);
      }
      return writer;
   }

   public void flushBuffer() throws IOException
   {
   }

   public boolean isCommitted()
   {
      return false;
   }

   public void reset()
   {
   }

   public void resetBuffer()
   {
   }

   public void sendError(int i, String s) throws IOException
   {
   }

   public void sendError(int i) throws IOException
   {
   }

   public void sendRedirect(String s) throws IOException
   {
   }

   public void addCookie(Cookie cookie)
   {
   }

   public void setDateHeader(String s, long l)
   {
   }

   public void addDateHeader(String s, long l)
   {
   }

   public void setHeader(String s, String s1)
   {
   }

   public void addHeader(String s, String s1)
   {
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public void setContentLength(int i)
   {
   }

   public void setCharacterEncoding(String characterEncoding)
   {
      this.characterEncoding = characterEncoding;
   }

   public void setIntHeader(String s, int i)
   {
   }

   public void addIntHeader(String s, int i)
   {
   }

   public void setStatus(int i)
   {
   }

   public void setStatus(int i, String s)
   {
   }

   public void setLocale(Locale locale)
   {
      this.locale = locale;
   }

   public String getContentType()
   {
      return contentType;
   }

   public Locale getLocale()
   {
      return locale;
   }

   public String getCharacterEncoding()
   {
      return characterEncoding;
   }

   public void setBufferSize(int bufferSize)
   {
      this.bufferSize = bufferSize;
   }

   public int getBufferSize()
   {
      return bufferSize;
   }
}
