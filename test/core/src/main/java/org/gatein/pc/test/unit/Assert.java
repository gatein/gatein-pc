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
package org.gatein.pc.test.unit;

import java.util.Arrays;

/**
 * A bunch of convenient utilities methods for asserting facts.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public final class Assert
{

   public static <T> T assertInstanceOf(Object o, Class<T> c)
   {
      if (o == null)
      {
         throw createAssertionError(null, "Cannot test null object");
      }
      else if (c == null)
      {
         throw createAssertionError(null, "Cannot test null class");
      }
      else if (c.isInstance(o))
      {
         return c.cast(o);
      }
      else
      {
         throw createAssertionError(null, "Object " + o + " with class " + o.getClass().getName() + " is not an instance of " + c.getName());
      }
   }

   public static <T> T assertNotNull(T actual)
   {
      return assertNotNull(null, actual);
   }

   public static <T> T assertNotNull(String msg, T actual)
   {
      if (actual == null)
      {
         if (msg != null)
         {
            msg = "Expected a non null object";
         }
         fail(msg);
      }
      return actual;
   }

   public static void assertNull(Object actual)
   {
      assertSame(null, actual);
   }

   public static void assertNull(String msg, Object actual)
   {
      assertSame(msg, null, actual);
   }

   public static void assertNotSame(String msg, Object expected, Object actual)
   {
      if (actual == null)
      {
         if (msg != null)
         {
            msg = "Expected a non null object";
         }
         if (expected == null)
         {
            fail(msg);
         }
      }
      else
      {
         if (msg == null)
         {
            if (expected != null)
            {
               msg = "Expected " + format(actual) + " object to have a different reference than " + format(expected);
            }
            else
            {
               msg = "Expected " + format(actual) + " object to be not null";
            }
         }
         if (actual == expected)
         {
            fail(msg);
         }
      }
   }

   public static void assertNotSame(Object expected, Object actual)
   {
      assertNotSame(null, expected, actual);
   }

   public static void assertSame(String msg, Object expected, Object actual)
   {
      if (actual == null)
      {
         if (msg == null)
         {
            msg = "Expected a null value";
         }
         if (expected != null)
         {
            fail(msg);
         }
      }
      else
      {
         if (msg == null)
         {
            if (expected != null)
            {
               msg = "Expected " + format(actual) + " valobjectue to have the same reference than " + format(expected);
            }
            else
            {
               msg = "Expected " + format(actual) + " object to be null";
            }
         }
         if (actual != expected)
         {
            fail(msg);
         }
      }
   }

   public static void assertSame(Object expected, Object actual)
   {
      assertSame(null, expected, actual);
   }

   public static void assertNotEquals(String msg, Object expected, Object actual)
   {
      if (actual == null)
      {
         if (msg != null)
         {
            msg = "Expected a non null object";
         }
         if (expected == null)
         {
            fail(msg);
         }
      }
      else
      {
         if (msg == null)
         {
            msg = "Expected " + format(actual) + " object to be not equals to " + format(expected);
         }
         if (actual.equals(expected))
         {
            fail(msg);
         }
      }
   }

   public static void assertNotEquals(Object expected, Object actual)
   {
      assertNotEquals(null, expected, actual);
   }

   public static void assertEquals(String msg, Object expected, Object actual)
   {
      if (actual == null)
      {
         if (msg == null)
         {
            msg = "Expected a null object";
         }
         if (expected != null)
         {
            fail(msg);
         }
      }
      else
      {
         if (msg == null)
         {
            msg = "Expected " + format(actual) + " object to be equals to " + format(expected);
         }
         if (!actual.equals(expected))
         {
            fail(msg);
         }
      }
   }

   public static void assertEquals(Object expected, Object actual)
   {
      assertEquals(null, expected, actual);
   }

   public static void assertTrue(boolean b)
   {
      assertTrue(null, b);
   }

   public static void assertTrue(String msg, boolean b)
   {
      assertEquals(msg, true, b);
   }

   public static void assertFalse(boolean b)
   {
      assertFalse(null, b);
   }

   public static void assertFalse(String msg, boolean b)
   {
      assertEquals(msg, false, b);
   }

   public static void fail(Throwable cause)
   {
      throw createAssertionError(cause, null);
   }

   public static void fail()
   {
      throw createAssertionError(null, null);
   }

   public static void fail(String msg)
   {
      throw createAssertionError(null, msg);
   }

   public static void fail(Throwable cause, String msg)
   {
      throw createAssertionError(cause, msg);
   }

   public static Error createFailure()
   {
      return createFailure((String)null);
   }

   public static Error createFailure(String msg)
   {
      return createAssertionError(null, msg);
   }

   public static Error createFailure(Throwable throwable)
   {
      return createAssertionError(throwable, null);
   }

   public static Error createAssertionError(Throwable cause, String msg)
   {
      if (msg == null)
      {
         msg = "Failure";
      }
      AssertionError assertionError = new AssertionError(msg);
      if (cause != null)
      {
         assertionError.initCause(cause);
      }
      return assertionError;
   }

   //

   /** @see #assertEquals(Object[],Object[]) */
   public static void assertEquals(Object[] expected, Object[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(Object[], Object[]). */
   public static void assertEquals(String message, Object[] expected, Object[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, actual));
      }
   }

   /** @see #assertEquals(char[],char[]) */
   public static void assertEquals(char[] expected, char[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(char[], char[]). */
   public static void assertEquals(String message, char[] expected, char[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(byte[],byte[]) */
   public static void assertEquals(byte[] expected, byte[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(char[], char[]). */
   public static void assertEquals(String message, byte[] expected, byte[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(int[],int[]) */
   public static void assertEquals(int[] expected, int[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(int[], int[]). */
   public static void assertEquals(String message, int[] expected, int[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(float[],float[]) */
   public static void assertEquals(float[] expected, float[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(float[], float[]). */
   public static void assertEquals(String message, float[] expected, float[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(short[],short[]) */
   public static void assertEquals(short[] expected, short[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(short[], short[]). */
   public static void assertEquals(String message, short[] expected, short[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(long[],long[]) */
   public static void assertEquals(long[] expected, long[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(long[], long[]). */
   public static void assertEquals(String message, long[] expected, long[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(double[],double[]) */
   public static void assertEquals(double[] expected, double[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(double[], double[]). */
   public static void assertEquals(String message, double[] expected, double[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   /** @see #assertEquals(boolean[],boolean[]) */
   public static void assertEquals(boolean[] expected, boolean[] actual)
   {
      assertEquals(null, expected, actual);
   }

   /** Test equality as defined by java.util.Array#equals(boolean[], boolean[]). */
   public static void assertEquals(String message, boolean[] expected, boolean[] actual)
   {
      if (!Arrays.equals(expected, actual))
      {
         fail(format(message, expected, expected));
      }
   }

   private static String format(String message, Object expected, Object actual)
   {
      String formatted = "";

      //
      if (message != null)
      {
         formatted = message + " ";
      }

      //
      return formatted + "expected:" + format(expected) + " but was:" + format(actual) + "";
   }

   private static String format(Object o)
   {
      if (o == null)
      {
         return "null";
      }
      else if (o instanceof Object[])
      {
         Object[] array = (Object[])o;
         StringBuffer buffer = new StringBuffer("<[");
         for (int i = 0; i < array.length; i++)
         {
            buffer.append(i == 0 ? "" : ",").append(String.valueOf(array[i]));
         }
         buffer.append("]>");
         return buffer.toString();
      }
      else
      {
         return " <" + o + ">";
      }
   }
}
