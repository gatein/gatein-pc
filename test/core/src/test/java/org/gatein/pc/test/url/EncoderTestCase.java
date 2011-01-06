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
package org.gatein.pc.test.url;

import org.gatein.common.util.ParameterMap;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Create;
import org.jboss.unit.api.pojo.annotations.Test;
import org.gatein.pc.test.url.CodecBuilder;
import org.gatein.pc.test.url.ParameterEncoder;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class EncoderTestCase
{

   Tester tester;

   @Create
   public void setUp() throws Exception
   {
      tester = new Tester();
   }

   @Test
   public void testEncodeNoMeta()
   {
      ParameterMap result = tester.encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));

      //
      tester.actual("action", "foo");
      result = tester.encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0","foo"}, result.getValues("action"));

      // 
      tester.meta("action", "foo");
      result = tester.encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
   }

   @Test
   public void testEncodeOneMeta()
   {
      tester.meta("m1");

      //
      ParameterMap result = tester.encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));

      //
      tester.meta("m1", "foo");
      result = tester.encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));

      //
      tester.actual("m1", "foo");
      result = tester.encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));

      //
      tester.meta("m1", "bar").actual("m1", "foo");
      result = tester.encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"bar","foo"}, result.getValues("m1"));
   }

   @Test
   public void testEncodeTwoMeta()
   {
      tester.meta("m1");
      tester.meta("m2");

      // No actual parameters

      //
      ParameterMap result = tester.encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));

      //
      result = tester.meta("m1", "foo").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));

      //
      result = tester.meta("m2", "foo").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));

      //
      result = tester.meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));

      //
      result = tester.meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));

      //
      result = tester.meta("m3", "zuu").encode();
      assertEquals(1, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));

      //
      result = tester.meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));

      //
      result = tester.meta("m1", "foo").meta("m2", "bar").meta("m2", "bar").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));

      // {m1} actual parameter

      //
      result = tester.actual("m1", "a").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));

      //
      result = tester.actual("m1", "a").meta("m1", "foo").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));

      //
      result = tester.actual("m1", "a").meta("m2", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));

      //
      result = tester.actual("m1", "a").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));

      //
      result = tester.actual("m1", "a").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));

      // {m2} actual parameter

      //
      result = tester.actual("m2", "b").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m1", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m2", "foo").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));

      //
      result = tester.actual("m2", "b").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));

      // {m3} actual parameter

      //
      result = tester.actual("m3", "c").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m1", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m2", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m3", "zuu").encode();
      assertEquals(2, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m3", "c").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      // {m1,m2} actual parameter

      //
      result = tester.actual("m1", "a").actual("m2", "b").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m1", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m2", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));

      // {m1,m3} actual parameter

      //
      result = tester.actual("m1", "a").actual("m3", "c").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m1", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m2", "foo").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      // {m2,m3} actual parameter

      //
      result = tester.actual("m2", "b").actual("m3", "c").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m1", "foo").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m2", "foo").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(3, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      // {m1,m2,m3} actual parameter

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m1", "foo").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m2", "foo").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"0"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"10"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m2", "foo").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"1"}, result.getValues("action"));
      assertEquals(new String[]{"a"}, result.getValues("m1"));
      assertEquals(new String[]{"foo","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));

      //
      result = tester.actual("m1", "a").actual("m2", "b").actual("m3", "c").meta("m1", "foo").meta("m2", "bar").meta("m3", "zuu").encode();
      assertEquals(4, result.size());
      assertEquals(new String[]{"11"}, result.getValues("action"));
      assertEquals(new String[]{"foo","a"}, result.getValues("m1"));
      assertEquals(new String[]{"bar","b"}, result.getValues("m2"));
      assertEquals(new String[]{"c"}, result.getValues("m3"));
   }

   private static class Tester
   {

      /** . */
      private CodecBuilder builder = new CodecBuilder("action");

      /** . */
      private ParameterMap actualParameters = new ParameterMap();

      /** . */
      private ParameterMap metaParameters = new ParameterMap();

      public Tester meta(String name)
      {
         builder.addMetaParameter(name);
         return this;
      }

      public Tester meta(String name, String value)
      {
         metaParameters.setValue(name, value);
         return this;
      }

      public Tester actual(String name, String value)
      {
         actualParameters.setValue(name, value);
         return this;
      }

      public ParameterMap encode()
      {
         ParameterEncoder encoder = builder.createEncoder();

         // Encode
         encoder.encode(actualParameters, metaParameters);

         // Clear once it is used
         actualParameters.clear();
         metaParameters.clear();

         // Clone for safety
         return encoder.getParameters();
      }
   }

}
