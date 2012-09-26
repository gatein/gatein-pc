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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ByteBuffer extends Buffer
{

   /** The output as a bytes if any. */
   private ClosableOutputStream bytes;

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
    * @return the output stream
    * @throws IllegalStateException if the window writer is already used or if no content type is defined
    */
   public OutputStream getOutputStream() throws IllegalStateException
   {
      if (bytes == null)
      {
         bytes = new ClosableOutputStream(new ByteArrayOutputStream());
      }
      return bytes;
   }

   @Override
   protected void doReset()
   {
      ((ByteArrayOutputStream)bytes.out).reset();
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
         closed = true;
      }
   }
}
