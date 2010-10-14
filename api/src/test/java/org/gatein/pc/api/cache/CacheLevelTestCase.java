/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.gatein.pc.api.cache;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class CacheLevelTestCase extends TestCase
{
   public void testCacheLevelFactory()
   {
      CacheLevel cacheLevel = CacheLevel.create("FULL");
      assertTrue(CacheLevel.FULL == cacheLevel);
      assertEquals(CacheLevel.FULL, cacheLevel);
      assertEquals(CacheLevel.FULL.name(), cacheLevel.name());

      cacheLevel = CacheLevel.create("PORTLET");
      assertTrue(CacheLevel.PORTLET == cacheLevel);
      assertEquals(CacheLevel.PORTLET, cacheLevel);
      assertEquals(CacheLevel.PORTLET.name(), cacheLevel.name());

      cacheLevel = CacheLevel.create("PAGE");
      assertTrue(CacheLevel.PAGE == cacheLevel);
      assertEquals(CacheLevel.PAGE, cacheLevel);
      assertEquals(CacheLevel.PAGE.name(), cacheLevel.name());

      try
      {
         CacheLevel.create(null);
         fail("Shouldn't allow creating a CacheLevel with null name!");
      }
      catch (Exception e)
      {
         // expected
      }

      cacheLevel = CacheLevel.create("foo");
      assertNotNull(cacheLevel);
      assertEquals("foo", cacheLevel.name());
      assertEquals(cacheLevel, CacheLevel.create("foo"));
   }

   public void testSerialization() throws IOException, ClassNotFoundException
   {
      Object read = serializeCacheLevel(CacheLevel.FULL);

      assertTrue(CacheLevel.FULL == read);

      CacheLevel foo = CacheLevel.create("foo");
      read = serializeCacheLevel(foo);

      assertEquals(foo, read);
   }

   private Object serializeCacheLevel(final CacheLevel cacheLevel)
      throws IOException, ClassNotFoundException
   {
      File tempFile = File.createTempFile("foo", "tmp");
      tempFile.deleteOnExit();

      FileOutputStream fos = new FileOutputStream(tempFile);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(cacheLevel);
      oos.close();

      FileInputStream fis = new FileInputStream(tempFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object read = ois.readObject();
      ois.close();
      return read;
   }
}
