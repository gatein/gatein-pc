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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.pc.portlet.impl.jsr168.api.MimeResponseImpl;
import org.gatein.pc.portlet.impl.jsr168.api.StateAwareResponseImpl;

import javax.portlet.PortletResponse;
import javax.portlet.MimeResponse;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Locale;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6639 $
 */
public abstract class DispatchedHttpServletResponse extends HttpServletResponseWrapper
{

   /** . */
   DispatchedHttpServletRequest req;

   /** . */
   private PortletResponse presp;

   public DispatchedHttpServletResponse(
      DispatchedHttpServletRequest req,
      PortletResponse presp,
      HttpServletResponse dresp)
   {
      super(dresp);

      //
      this.req = req;
      this.presp = presp;
   }

   // Must return null

   public final String encodeRedirectURL(String s)
   {
      return null;
   }

   public final String encodeRedirectUrl(String s)
   {
      return null;
   }

   // Must be equivalent to the methods of the PortletResponse

   public final String encodeURL(String s)
   {
      return presp.encodeURL(s);
   }

   public final String encodeUrl(String s)
   {
      return presp.encodeURL(s);
   }

   // Must perform no operations

   public final void setContentLength(int i)
   {
   }

   public final void setLocale(Locale locale)
   {
   }

   public final void addCookie(Cookie cookie)
   {
   }

   public final void sendError(int i) throws IOException
   {
   }

   public final void sendError(int i, String s) throws IOException
   {
   }

   public final void sendRedirect(String s) throws IOException
   {
   }

   public final void setDateHeader(String s, long l)
   {
   }

   public final void addDateHeader(String s, long l)
   {
   }

   public final void setHeader(String s, String s1)
   {
   }

   public final void addHeader(String s, String s1)
   {
   }

   public final void setIntHeader(String s, int i)
   {
   }

   public final void addIntHeader(String s, int i)
   {
   }

   public final void setStatus(int i)
   {
   }

   public final void setStatus(int i, String s)
   {
   }


   public final void setCharacterEncoding(String s)
   {
      //TODO: must perform nothing for include but in forward must delegate to portlet response

   }

   // Must return false

   public boolean containsHeader(String s)
   {
      return false;
   }

   // Defined by subclasses

   public abstract void setContentType(String contentType);

   public abstract String getCharacterEncoding();

   public abstract Locale getLocale();

   public abstract void flushBuffer() throws IOException;

   public abstract void resetBuffer();

   public abstract void reset();

   public abstract int getBufferSize();

   public abstract boolean isCommitted();

   public abstract ServletOutputStream getOutputStream() throws IOException;

   public abstract String getContentType();

   public abstract PrintWriter getWriter() throws IOException;

   public abstract void setBufferSize(int i);

   public static final class StateAware extends DispatchedHttpServletResponse
   {

      public StateAware(DispatchedHttpServletRequest req, StateAwareResponseImpl saresp, HttpServletResponse dresp)
      {
         super(req, saresp, dresp);
      }

      // Must return null

      public String getCharacterEncoding()
      {
         return null;
      }

      public String getContentType()
      {
         return null;
      }

      public Locale getLocale()
      {
         return null;
      }

      public void resetBuffer()
      {
      }

      public void reset()
      {
      }

      // Must return 0

      public int getBufferSize()
      {
         return 0;
      }

      // Ignore

      public ServletOutputStream getOutputStream() throws IOException
      {
         return new ServletOutputStream()
         {
            public void write(int b) throws IOException
            {
               // Ignore
            }
         };
      }

      public PrintWriter getWriter() throws IOException
      {
         return new PrintWriter(new Writer()
         {
            public void write(char cbuf[], int off, int len) throws IOException
            {
               // Ignore
            }

            public void flush() throws IOException
            {
               // Ignore
            }

            public void close() throws IOException
            {
               // Ignore
            }
         });
      }

      // Must No op

      public void setBufferSize(int i)
      {
      }

      public void flushBuffer() throws IOException
      {
      }

      public void setContentType(String contentType)
      {
      }

      // Must return true for include and false for forward

      public boolean isCommitted()
      {
         return req.dispatchType == DispatchType.INCLUDE; 
      }
   }

   public static final class Mime extends DispatchedHttpServletResponse
   {

      /** . */
      private final MimeResponse mresp;

      /** . */
      private ServletOutputStream sos;

      public Mime(DispatchedHttpServletRequest req, MimeResponseImpl mresp, HttpServletResponse dresp)
      {
         super(req, mresp, dresp);

         //
         this.mresp = mresp;
         this.sos = null;
      }

      // Must be equivalent

      public String getCharacterEncoding()
      {
         return mresp.getCharacterEncoding();
      }

      public void setBufferSize(int i)
      {
         mresp.setBufferSize(i);
      }

      public void flushBuffer() throws IOException
      {
         mresp.flushBuffer();
      }

      public void resetBuffer()
      {
         mresp.resetBuffer();
      }

      public void reset()
      {
         mresp.reset();
      }

      public int getBufferSize()
      {
         return mresp.getBufferSize();
      }

      public boolean isCommitted()
      {
         return mresp.isCommitted();
      }

      public ServletOutputStream getOutputStream() throws IOException
      {
         if (sos == null)
         {
            sos = new ServletOutputStream()
            {
               /** . */
               private final OutputStream out = mresp.getPortletOutputStream();

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
         return sos;
      }

      public PrintWriter getWriter() throws IOException
      {
         return mresp.getWriter();
      }

      public Locale getLocale()
      {
         return mresp.getLocale();
      }

      public String getContentType()
      {
         return mresp.getContentType();
      }

      public void setContentType(String contentType)
      {
         mresp.setContentType(contentType);
      }
   }
}

