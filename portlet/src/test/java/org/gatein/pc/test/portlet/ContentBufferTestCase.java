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

import org.jboss.unit.api.pojo.annotations.Test;
import org.gatein.pc.portlet.impl.jsr168.ContentBuffer;
import static org.jboss.unit.api.Assert.*;
import static org.jboss.unit.api.Assert.assertEquals;

import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@Test
public class ContentBufferTestCase
{

   @Test
   public void testResetChars()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      buffer.reset();
      assertEquals("", buffer.getChars());
   }

   @Test
   public void testResetBytes() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      buffer.reset();
      assertEquals(new byte[0], buffer.getBytes());
   }

   @Test
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

   @Test
   public void testCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      assertFalse(buffer.isCommited());
      buffer.commit();
      assertTrue(buffer.isCommited());
   }

   @Test
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

   @Test
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

   @Test
   public void testWriteBytesAfterCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      buffer.commit();
      OutputStream out = buffer.getOutputStream();
      assertNotNull(out);
      out.write("foo".getBytes("UTF8"));
      out.close();
      assertEquals("foo".getBytes("UTF8"), buffer.getBytes());
   }

   @Test
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
      assertEquals("foobar".getBytes("UTF8"), buffer.getBytes());
   }

   @Test
   public void testFlushWriterDoesCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      writer.flush();
      assertTrue(buffer.isCommited());
   }

   @Test
   public void testCloseWriterDoesCommit()
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      PrintWriter writer = buffer.getWriter();
      writer.print("foo");
      writer.close();
      assertTrue(buffer.isCommited());
   }

   @Test
   public void testFlushStreamDoesCommit() throws IOException
   {
      ContentBuffer buffer = new ContentBuffer();
      buffer.setContentType("text/html");
      OutputStream out = buffer.getOutputStream();
      out.write("foo".getBytes("UTF8"));
      out.flush();
      assertTrue(buffer.isCommited());
   }

   @Test
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
