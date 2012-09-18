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

package org.gatein.pc.embed;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.gatein.pc.embed.Chunk;
import org.gatein.pc.embed.Query;
import org.gatein.pc.embed.Segment;
import org.junit.Test;

import javax.servlet.ServletException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class SegmentTestCase
{

   @Test
   public void testSegments()
   {
      assertSegments("", (String[])null);
      assertSegments("/", (String[])null);
      assertSegments("a", "a");
      assertSegments("/a", "a");
      assertSegments("foo", "foo");
      assertSegments("/foo", "foo");
      assertSegments("//foo", "foo");
      assertSegments("/foo/", "foo");
      assertSegments("/foo//", "foo");
      assertSegments("/foo/bar", "foo", "bar");
      assertSegments("/foo/bar/", "foo", "bar");
   }

   private void assertSegments(String path, String... expected)
   {
      Chunk head;
      try
      {
         head = Chunk.parse(path);
      }
      catch (ServletException e)
      {
         AssertionFailedError afe = new AssertionFailedError();
         afe.initCause(e);
         throw afe;
      }
      if (head instanceof Segment)
      {
         Assert.assertNotNull(expected);
         Segment first = (Segment)head;
         ArrayList<String> names = new ArrayList<String>();
         for (Segment segment : first)
         {
            names.add(segment.value);
         }
         Assert.assertEquals(first.size(), expected.length);
         Assert.assertEquals(Arrays.asList(expected), names);
      }
      else
      {
         Assert.assertNull(expected);
      }
   }

   @Test
   public void testEncoding()
   {
      Assert.assertEquals("/a/b", new Segment("a", new Segment("b")).toString());
      Assert.assertEquals("/%2F", new Segment("/").toString());
      Assert.assertEquals("/%3B", new Segment(";").toString());
      Assert.assertEquals("/%D0%A3", new Segment("\u0423").toString());
      Assert.assertEquals("/%E2%82%AC", new Segment("€").toString());
      Assert.assertEquals("/;%3B==", new Segment("", Collections.singletonMap(";", new String[]{"="})).toString());
      Assert.assertEquals("?/=/", new Query().put("/", "/").toString());
   }

   @Test
   public void testDecoding()
   {
      // Pchar
      Assert.assertEquals("a", Chunk.decode("a"));
      Assert.assertEquals("_", Chunk.decode("_"));
      Assert.assertEquals("/", Chunk.decode("/"));
      Assert.assertEquals("0", Chunk.decode("0"));

      // Decode
      Assert.assertEquals("/", Chunk.decode("%2F"));
      Assert.assertEquals("\u0423", Chunk.decode("%D0%A3"));
      Assert.assertEquals("€", Chunk.decode("%E2%82%AC"));

      // Best effort
      Assert.assertEquals("é", Chunk.decode("é"));

      // Tolerate malformed input
      Chunk.decode("%E2a");
      Chunk.decode("%z0");
      Chunk.decode("%0");
   }
}
