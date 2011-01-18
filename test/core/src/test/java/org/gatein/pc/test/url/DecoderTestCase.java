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

import java.util.ArrayList;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Create;
import org.jboss.unit.api.pojo.annotations.Test;
import org.gatein.pc.test.url.CodecBuilder;
import org.gatein.pc.test.url.ParameterDecoder;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class DecoderTestCase
{

   Tester tester;

   @Create
   public void setUp() throws Exception
   {
      tester = new Tester();
   }

   @Test
   public void testNoMeta()
   {
      tester.assertActual(0);
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m1", "a");
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();
   }

   @Test
   public void testNoMetaCorruption()
   {
      tester.parameter("action", "a").assertFailed().checkAndReset();
      tester.parameter("action", "1").assertFailed().checkAndReset();
   }

   @Test
   public void testOneMeta()
   {
      tester.builder.addMetaParameter("m1");

      //
      tester.assertActual(0);
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m1", "a");
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", "foo");
      tester.assertActual(0);
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", new String[]{"foo","a"});
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("m2", "b");
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m1", "a").parameter("m2", "b");
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", "foo").parameter("m2", "b");
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", new String[]{"foo","a"}).parameter("m2", "b");
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();
   }

   @Test
   public void testOneMetaCorruption()
   {
      tester.builder.addMetaParameter("m1");

      //
      tester.parameter("action", "a").assertFailed().checkAndReset();
      tester.parameter("action", "1").assertFailed().checkAndReset();
      tester.parameter("action", "10").assertFailed().checkAndReset();

      //
      tester.parameter("action", "a").assertFailed().checkAndReset();
      tester.parameter("action", "10").parameter("m1", "foo").assertFailed().checkAndReset();
   }

   @Test
   public void testTwoMeta()
   {
      tester.builder.addMetaParameter("m1");
      tester.builder.addMetaParameter("m2");

      //
      tester.assertActual(0);
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m1", "a");
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m2", "b");
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      //
      tester.parameter("m1", "a").parameter("m2", "b");
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(0);
//      tester.check();
      tester.parameter("action", "0");
      tester.checkAndReset();

      // {m1}

      //
      tester.parameter("action", "10").parameter("m1", "foo");
      tester.assertActual(0);
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("action", "10").parameter("m1", new String[]{"foo","a"});
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("action", "10").parameter("m1", "foo").parameter("m2", "b");
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      //
      tester.parameter("action", "10").parameter("m1", new String[]{"foo","a"}).parameter("m2", "b");
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m1", new String[]{"foo"});
      tester.checkAndReset();

      // {m2}

      //
      tester.parameter("action", "1").parameter("m2", "bar");
      tester.assertActual(0);
      tester.assertMeta(1).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", "a").parameter("m2", "bar");
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(1).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m2", new String[]{"bar","b"});
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "1").parameter("m1", "a").parameter("m2", new String[]{"bar","b"});
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(1).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      // {m1,m2}

      //
      tester.parameter("action", "11").parameter("m1", "foo").parameter("m2", "bar");
      tester.assertActual(0);
      tester.assertMeta(2).assertMeta("m1", new String[]{"foo"}).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "11").parameter("m1", new String[]{"foo","a"}).parameter("m2", "bar");
      tester.assertActual(1).assertActual("m1", new String[]{"a"});
      tester.assertMeta(2).assertMeta("m1", new String[]{"foo"}).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "11").parameter("m1", "foo").parameter("m2", new String[]{"bar","b"});
      tester.assertActual(1).assertActual("m2", new String[]{"b"});
      tester.assertMeta(2).assertMeta("m1", new String[]{"foo"}).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();

      //
      tester.parameter("action", "11").parameter("m1", new String[]{"foo","a"}).parameter("m2", new String[]{"bar","b"});
      tester.assertActual(2).assertActual("m1", new String[]{"a"}).assertActual("m2", new String[]{"b"});
      tester.assertMeta(2).assertMeta("m1", new String[]{"foo"}).assertMeta("m2", new String[]{"bar"});
      tester.checkAndReset();
   }

   private static class Tester
   {

      private interface Assertion
      {
         void doAssert();
      }

      ArrayList assertions = new ArrayList();

      CodecBuilder builder = new CodecBuilder("action");
      ParameterMap parameters = new ParameterMap();
      ParameterMap actualParameters = new ParameterMap();
      ParameterMap metaParameters = new ParameterMap();
      boolean failed;

      public void check()
      {
         ParameterDecoder decoder = builder.createDecoder();

         //
         try
         {
            decoder.decode(parameters);

            //
            failed = false;
            actualParameters = decoder.getActualParameters();
            metaParameters = decoder.getMetaParameters();
         }
         catch (IllegalArgumentException e)
         {
            failed = true;
         }


         for (int i = 0; i < assertions.size(); i++)
         {
            Assertion assertion = (Assertion)assertions.get(i);
            assertion.doAssert();
         }
      }

      public void checkAndReset()
      {
         check();

         //
         parameters.clear();
         assertions.clear();
      }

      public Tester parameter(String name, String value)
      {
         if (value == null)
         {
            parameters.remove(name);
         }
         else
         {
            parameters.setValue(name, value);
         }
         return this;
      }
      public Tester parameter(String name, String[] values)
      {
         parameters.setValues(name, values);
         return this;
      }


      public Tester assertActual(final int size)
      {
         assertions.add(new Assertion()
         {
            public void doAssert()
            {
               assertFalse(failed);
               assertEquals(size, actualParameters.size());
            }
         });
         return this;
      }
      public Tester assertActual(final String name, final String[] values)
      {
         assertions.add(new Assertion()
         {
            public void doAssert()
            {
               assertFalse(failed);
               assertEquals(values, actualParameters.getValues(name));
            }
         });
         return this;
      }
      public Tester assertMeta(final int size)
      {
         assertions.add(new Assertion()
         {
            public void doAssert()
            {
               assertFalse(failed);
               assertEquals(size, metaParameters.size());
            }
         });
         return this;
      }
      public Tester assertMeta(final String name, final String[] values)
      {
         assertions.add(new Assertion()
         {
            public void doAssert()
            {
               assertFalse(failed);
               assertEquals(values, metaParameters.getValues(name));
            }
         });
         return this;
      }
      public Tester assertFailed()
      {
         assertions.add(new Assertion()
         {
            public void doAssert()
            {
               assertTrue(failed);
            }
         });
         return this;
      }
   }
}
