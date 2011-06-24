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
import org.gatein.pc.portlet.impl.jsr168.PortletParameterMap;
import org.gatein.pc.portlet.support.info.NavigationInfoSupport;
import org.gatein.pc.portlet.support.info.ParameterInfoSupport;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6639 $
 */
public class ParametersTestCase extends TestCase
{

   public ParametersTestCase()
   {
   }

   private NavigationInfoSupport navigation;
   private PortletParameterMap map;

   public void setUp()
   {
      navigation = new NavigationInfoSupport();
      map = new PortletParameterMap(navigation);
   }

   public void tearDown()
   {
      map = null;
      navigation = null;
   }

   public void testGetWithNullName()
   {
      try
      {
         map.getParameterValue(null);
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   public void testSet()
   {
      map.setParameterValue("a", "b");
      assertEquals(map.getParameterValue("a"), "b");
   }

   public void testSetWithNullName()
   {
      try
      {
         map.setParameterValue(null, "b");
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   public void testSetWithNullValue()
   {
      try
      {
         map.setParameterValue("a", null);
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

//   public void testRemoveWithNullName()
//   {
//      try
//      {
//         param.remove(null);
//         fail("Expected IllegalArgumentException");
//      }
//      catch (NullPointerException e)
//      {
//      }
//   }

//   public void testRemove()
//   {
//      param.setValue("a", "b");
//      param.remove("a");
//      assertEquals(param.getValue("a"), null);
//   }

   public void testSetValues()
   {
      map.setParameterValues("a", new String[]{"b", "c"});
      assertTrue(Arrays.equals(map.getParameterValues("a"), new String[]{
         "b", "c"}));
      assertEquals(map.getParameterValue("a"), "b");
   }

   public void testSetValuesWithNullName()
   {
      try
      {
         map.setParameterValues(null, new String[]{"a"});
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   public void testSetValuesWithNullValues()
   {
      try
      {
         map.setParameterValues("a", null);
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   public void testSetValuesWithZeroLengthValues()
   {
      try
      {
         map.setParameterValues("a", new String[0]);
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   public void testSetValuesWithOneNullValue()
   {
      try
      {
         map.setParameterValues("a", new String[]{"a", null});
         fail("Expected IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

//   @Test
//   public void testReplaceWithParameters()
//   {
//      ParameterMap other = new ParameterMap();
//      other.setValue("a", "b");
//      other.setValues("c", new String[]{"d", "e"});
//      param.replace(other);
//      assertEquals("b", param.getValue("a"));
//      assertTrue(Arrays.equals(param.getValues("c"), new String[]{"d", "e"}));
//   }

   public void testBlah()
   {
      navigation.addPublicParameter(new ParameterInfoSupport("foo", new QName("", "foo")));
      navigation.addPublicParameter(new ParameterInfoSupport("abc", new QName("", "abc")));

      map.setParameterValue("juu", "daa");
      map.setParameterValue("foo", "bar");
      map.removePublicParameterValue("abc");

      Map<String, String[]> privateMap = map.getPrivateMapSnapshot();
      assertEquals(1, privateMap.size());
      assertEquals(Arrays.asList("daa"), Arrays.asList(privateMap.get("juu")));
      assertEquals(null, privateMap.get("abc"));

      Map<String, String[]> publicMap = map.getPublicMapSnapshot();
      assertEquals(2, publicMap.size());
      assertEquals(Arrays.asList("bar"), Arrays.asList(publicMap.get("foo")));
      assertEquals(Collections.<String>emptyList(), Arrays.asList(publicMap.get("abc")));

      Map<String, String[]> combinedMap = map.getMap();
      assertEquals(2, combinedMap.size());
      assertEquals(Arrays.asList("bar"), Arrays.asList(combinedMap.get("foo")));
      assertEquals(Arrays.asList("daa"), Arrays.asList(combinedMap.get("juu")));
   }

//   public void testReplaceWithNullMap()
//   {
//      try
//      {
//         param.replace(null);
//         fail("Expected NullPointerException");
//      }
//      catch (NullPointerException e)
//      {
//      }
//   }

//   public void testReplaceWithInvalidMap()
//   {
//      Map[] maps = buildInvalidMaps();
//      Class[] exceptionClasses = buildExceptionClasses();
//      for (int i = 0; i < maps.length; i++)
//      {
//         try
//         {
//            Map map = maps[i];
//            param.replace(map);
//            fail("Expected IllegalArgumentException with map=" + map);
//         }
//         catch (Exception e)
//         {
//            assertTrue(exceptionClasses[i].isAssignableFrom(e.getClass()));
//         }
//      }
//   }

//   public void testReplace()
//   {
//      param.setValue("a", "b");
//      param.setValues("c", new String[]{"d", "e"});
//      param.setValue("f", "g");
//      Map map = new HashMap();
//      map.put("a", new String[]{"_b"});
//      map.put("c", new String[]{"_d", "_e"});
//      map.put("h", new String[]{"_i"});
//      param.replace(map);
//      assertEquals(3, param.size());
//      assertEquals(param.getValues("a"), new String[]{"_b"});
//      assertEquals(param.getValues("c"), new String[]{"_d", "_e"});
//      assertEquals(param.getValues("h"), new String[]{"_i"});
//   }

//   public void testAppendWithInvalidMap()
//   {
//      Map[] maps = buildInvalidMaps();
//      Class[] exceptionClasses = buildExceptionClasses();
//      for (int i = 0; i < maps.length; i++)
//      {
//         try
//         {
//            Map map = maps[i];
//            param.append(map);
//            fail("Expected IllegalArgumentException with map=" + map);
//         }
//         catch (Exception e)
//         {
//            if (!exceptionClasses[i].isAssignableFrom(e.getClass()))
//            {
//               fail("Exception class " + exceptionClasses[i].getName() + " (index=" + i + ") should be assignable from caught exception " + e.getClass());
//            }
//         }
//      }
//   }

//   public void testAppend()
//   {
//      param.setValue("a", "b");
//      param.setValues("c", new String[]{"d", "e"});
//      param.setValue("f", "g");
//      Map map = new HashMap();
//      map.put("a", new String[]{"_b"});
//      map.put("c", new String[]{"_d", "_e"});
//      map.put("h", new String[]{"_i"});
//      param.append(map);
//      assertEquals(4, param.size());
//      assertEquals(param.getValues("a"), new String[]{"b", "_b"});
//      assertEquals(param.getValues("c"), new String[]{"d", "e", "_d", "_e"});
//      assertEquals(param.getValues("f"), new String[]{"g"});
//      assertEquals(param.getValues("h"), new String[]{"_i"});
//   }

//   public void testClear()
//   {
//      param.setValue("a", "b");
//      param.clear();
//      assertNull(param.getValue("a"));
//   }

   public Class[] buildExceptionClasses()
   {
      return new Class[]
         {
            NullPointerException.class,
            IllegalArgumentException.class,
            IllegalArgumentException.class,
            ClassCastException.class
         };
   }

   public Map[] buildInvalidMaps()
   {
      Map map1 = new HashMap();
      map1.put("a", null);
      Map map2 = new HashMap();
      map2.put("a", new String[0]);
      Map map3 = new HashMap();
      map3.put("a", new String[]{null});
      Map map4 = new HashMap();
      map4.put("a", new Object());
      return new Map[]{map1, map2, map3, map4};
   }
}