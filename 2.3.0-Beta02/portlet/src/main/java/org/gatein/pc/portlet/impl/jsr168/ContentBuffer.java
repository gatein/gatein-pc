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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Data produced.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5602 $
 */
public class ContentBuffer
{

   /** The output as a bytes if any. */
   private ClosableOutputStream bytes;

   /** The output as chars if any. */
   private ClosableWriter chars;

   /** The writer that will produce the chars output if any. */
   private PrintWriter writer;

   /** The result content type if any. */
   private String contentType;

   /** . */
   private boolean commited;

   public ContentBuffer()
   {
      this.bytes = null;
      this.chars = null;
      this.writer = null;
      this.contentType = null;
      this.commited = false;
   }

   /**
    * Return the bytes of the content held by the fragment or null.
    *
    * @return the bytes
    */
   public byte[] getBytes()
   {
      if (bytes == null)
      {
         return null;
      }
      else
      {
         return ((ByteArrayOutputStream)bytes.out).toByteArray();
      }
   }

   /**
    * Return the chars of the content held by the fragment or null.
    *
    * @return the chars
    */
   public String getChars() 
   {
      if (chars == null)
      {
         return null;
      }
      else
      {
         return chars.writer.toString();
      }
   }

   /**
    * Return the content type of the generated fragment.
    *
    * @return the content type
    */
   public String getContentType()
   {
      return contentType;
   }

   /**
    * Set the fragment of the content type.
    *
    * @param contentType the content type
    */
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   /**
    * Returns the writer.
    *
    * @return the writer
    * @throws IllegalStateException if the output stream is already used or if no content type is defined
    */
   public PrintWriter getWriter() throws IllegalStateException
   {
      if (bytes != null)
      {
         throw new IllegalStateException("The window output stream is already used");
      }
      if (contentType == null)
      {
         throw new IllegalStateException("No content type defined");
      }
      if (chars == null)
      {
         chars = new ClosableWriter(new StringWriter());
         writer = new PrintWriter(chars);
      }
      return writer;
   }

   /**
    * @return the output stream
    * @throws IllegalStateException if the window writer is already used or if no content type is defined
    */
   public OutputStream getOutputStream() throws IllegalStateException
   {
      if (chars != null)
      {
         throw new IllegalStateException("The window writer is already used");
      }
      if (contentType == null)
      {
         throw new IllegalStateException("No content type defined");
      }
      if (bytes == null)
      {
         bytes = new ClosableOutputStream(new ByteArrayOutputStream());
      }
      return bytes;
   }

   public void reset()
   {
      if (commited)
      {
         throw new IllegalStateException("Cannot reset a commited stream");
      }
      if (bytes != null)
      {
         ((ByteArrayOutputStream)bytes.out).reset();
      }
      else if (chars != null)
      {
         StringWriter sw = (StringWriter)chars.writer;
         sw.flush();
         sw.getBuffer().setLength(0);
      }
   }

   /**
    * Simulate a response commit.
    *
    * @throws IllegalStateException if no content type is defined
    */
   public void commit() throws IllegalStateException
   {
      commited = true;
   }

   public boolean isCommited()
   {
      return commited;
   }

   private class ClosableOutputStream extends OutputStream
   {

      /** . */
      boolean closed = false;

      /** . */
      final OutputStream out;

      public ClosableOutputStream(OutputStream out)
      {
         this.out = out;
      }


      public void write(byte b[]) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed stream");
         }

         //
         out.write(b);
      }

      public void write(byte b[], int off, int len) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed stream");
         }

         //
         out.write(b, off, len);
      }

      public void write(int b) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed stream");
         }

         //
         out.write(b);
      }

      public void flush() throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot flush a closed stream");
         }

         //
         commited = true;
      }

      public void close() throws IOException
      {
         super.close();

         //
         commited = true;

         //
         closed = true;
      }
   }

   /**
    * Adds behavior to a writer that complies with JSR-168 notion of writer.
    */
   private class ClosableWriter extends Writer
   {

      /** . */
      boolean closed = false;

      /** . */
      final Writer writer;

      public ClosableWriter(Writer writer)
      {
         this.writer = writer;
      }

      public void write(int c) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed writer");
         }

         //
         writer.write(c);
      }


      public void write(char cbuf[], int off, int len) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed writer");
         }

         //
         writer.write(cbuf, off, len);
      }


      public void write(String str) throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot write to a closed writer");
         }

         //
         writer.write(str);
      }

      public void flush() throws IOException
      {
         if (closed)
         {
            throw new IOException("Cannot flush closed writer");
         }

         //
         commited = true;
      }

      public void close() throws IOException
      {
         writer.close();

         //
         commited = true;

         //
         closed = true;
      }
   }
}