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
package org.gatein.pc.test.portlet;

import junit.framework.TestCase;
import org.gatein.pc.portlet.impl.jsr168.ContentBuffer;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContentBufferTestCase extends TestCase
{

   public void testResetChars()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      buffer.reset();
      assertEquals("", buffer.getChars());
   }

   public void testResetBytes() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      buffer.reset();
      assertEquals(0, buffer.getBytes().length);
   }

   public void testResetAfterCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      buffer.commit();
      try
      {
         buffer.reset();
         fail();
      }
      catch (IllegalStateException ignore)
      {
      }
   }

   public void testCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      assertFalse(buffer.isCommited());
      buffer.commit();
      assertTrue(buffer.isCommited());
   }

   public void testWriteCharsAfterCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      buffer.commit();
      PrintWriter writer = buffer.getWriter();
      assertNotNull(writer);
      writer.print("foo");
      writer.close();
      assertEquals("foo", buffer.getChars());
   }

   public void testWriteCharsAndCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      assertNotNull(writer);
      writer.print("foo");
      buffer.commit();
      writer.print("bar");
      writer.close();
      assertEquals("foobar", buffer.getChars());
   }

   public void testWriteBytesAfterCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      buffer.commit();
      OutputStream out = buffer.getOutputStream();
      assertNotNull(out);
      out.write("foo".getBytes("UTF8"));
      out.close();
      assertTrue(Arrays.equals("foo".getBytes("UTF8"), buffer.getBytes()));
   }

   public void testWriteBytesAndCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      buffer.commit();
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      assertNotNull(out);
      out.write("bar".getBytes("UTF8"));
      out.close();
      assertTrue(Arrays.equals("foobar".getBytes("UTF8"), buffer.getBytes()));
   }

   public void testFlushWriterDoesCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      writer.flush();
      assertTrue(buffer.isCommited());
   }

   public void testCloseWriterDoesCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      writer.close();
      assertTrue(buffer.isCommited());
   }

   public void testFlushStreamDoesCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      out.flush();
      assertTrue(buffer.isCommited());
   }

   public void testClosestreamDoesCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      out.close();
      assertTrue(buffer.isCommited());
   }
}
