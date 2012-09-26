/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.portlet.impl.jsr168;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class CharBuffer extends Buffer
{

   /** The output as chars if any. */
   private ClosableWriter chars;

   /** The writer that will produce the chars output if any. */
   private PrintWriter writer;

   public CharBuffer()
   {
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
    * Returns the writer.
    *
    * @return the writer
    * @throws IllegalStateException if the output stream is already used or if no content type is defined
    */
   public PrintWriter getWriter() throws IllegalStateException
   {
      if (chars == null)
      {
         chars = new ClosableWriter(new StringWriter());
         writer = new PrintWriter(chars);
      }
      return writer;
   }

   @Override
   protected void doReset()
   {
      StringWriter sw = (StringWriter)chars.writer;
      sw.flush();
      sw.getBuffer().setLength(0);
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
         closed = true;
      }
   }
}
