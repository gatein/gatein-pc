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
package org.gatein.pc.bridge;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;

import org.gatein.pc.api.invocation.PortletInvocation;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6862 $
 */
public class BridgeResponse extends HttpServletResponseWrapper
{

   /** . */
   protected final PortletResponse presp;

   /** . */
   protected final PortletInvocation invocation;

   /** . */
   protected ServletOutputStream sos;

   public BridgeResponse(JBossServletContextProvider.BridgeInfo info)
   {
      super(info.getInvocation().getDispatchedResponse());
      invocation = info.getInvocation();
      HttpServletRequest hreq = invocation.getDispatchedRequest();
      this.presp = (PortletResponse)hreq.getAttribute("javax.portlet.response");
      this.sos = null;
   }

   public String encodeRedirectURL(String s)
   {
      return null;
   }

   public String encodeRedirectUrl(String s)
   {
      return null;
   }

   public String getCharacterEncoding()
   {
      return "UTF-8";
   }

   public void setBufferSize(int i)
   {
      if (presp instanceof RenderResponse)
      {
         ((RenderResponse)presp).setBufferSize(i);
      }
      else
      {
         throw new IllegalStateException("setBufferSize called on non render response");
      }
   }

   public void flushBuffer() throws IOException
   {
      if (presp instanceof RenderResponse)
      {
         ((RenderResponse)presp).flushBuffer();
      }
      else
      {
         throw new IllegalStateException("flushBuffer called on non render response");
      }
   }

   public void resetBuffer()
   {
      if (presp instanceof RenderResponse)
      {
         ((RenderResponse)presp).resetBuffer();
      }
      else
      {
         throw new IllegalStateException("resetBuffer called on non render response");
      }
   }

   public void reset()
   {
      if (presp instanceof RenderResponse)
      {
         ((RenderResponse)presp).reset();
      }
      else
      {
         throw new IllegalStateException("reset called on non render response");
      }
   }

   public int getBufferSize()
   {
      if (presp instanceof RenderResponse)
      {
         return ((RenderResponse)presp).getBufferSize();
      }
      throw new IllegalStateException("getBufferSize called on non render response");
   }

   public boolean isCommitted()
   {
      if (presp instanceof RenderResponse)
      {
         return ((RenderResponse)presp).isCommitted();
      }
      throw new IllegalStateException("isCommited called on non render response");
   }

   public ServletOutputStream getOutputStream() throws IOException
   {
      if (sos == null)
      {
         if (presp instanceof RenderResponse)
         {
            sos = new ServletOutputStream()
            {
               private OutputStream out = ((RenderResponse)presp).getPortletOutputStream();

               public void write(byte b[], int off, int len) throws IOException
               {
                  out.write(b, off, len);
               }

               public void write(byte b[]) throws IOException
               {
                  out.write(b);
               }

               public void write(int b) throws IOException
               {
                  out.write(b);
               }
            };
         }
         else
         {
            throw new IllegalStateException("getOutputStream called on non render response");
         }
      }
      return sos;
   }

   public PrintWriter getWriter() throws IOException
   {
      if (presp instanceof RenderResponse)
      {
         RenderResponse rresp = (RenderResponse)presp;
         if (rresp.getContentType() == null)
         {
            //
            rresp.setContentType("text/html");
         }
         return rresp.getWriter();
      }
      throw new IllegalStateException("getWriter called on non render response");
   }

   public String encodeURL(String s)
   {
      // URL may not start with / or http:// so we need to defer it to the real response
      // PortletResponse would throw an IllegalArgumentException
      return invocation.getDispatchedResponse().encodeURL(s);
   }

   public String encodeUrl(String s)
   {
      return invocation.getDispatchedResponse().encodeURL(s);
   }

   // Must perform no operations

   public void setContentType(String mimeType)
   {
      if (presp instanceof RenderResponse)
      {
         ((RenderResponse)presp).setContentType(mimeType);
      }
      else
      {
         throw new IllegalStateException("setContentType called on non render response");
      }
   }

   public void setContentLength(int i)
   {
   }

   public void setLocale(Locale locale)
   {
   }

   public void addCookie(Cookie cookie)
   {
   }

   public void sendError(int i) throws IOException
   {
   }

   public void sendError(int i, String s) throws IOException
   {
   }

   public void sendRedirect(String s) throws IOException
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

   public boolean containsHeader(String s)
   {
      return false;
   }

   public Locale getLocale()
   {
      return invocation.getUserContext().getLocale();
   }

   public String getContentType()
   {
      throw new UnsupportedOperationException();
   }

   public void setCharacterEncoding(String s)
   {
      throw new UnsupportedOperationException();
   }
}

