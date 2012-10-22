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

import org.gatein.common.util.Tools;

import javax.servlet.ServletException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
abstract class Chunk
{

   /** . */
   private static final char[] ALPHABET = "0123456789ABCDEF".toCharArray();

   /** . */
   private static final Charset UTF_8 = Charset.forName("UTF-8");

   /**
    * pchar       = unreserved / pct-encoded / sub-delims / ":" / "@"
    * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
    * pct-encoded = "%" HEXDIG HEXDIG
    * sub-delims  = "!" / "$" / "&" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
    */
   protected static final BitSet PCHAR = new BitSet();

   static
   {
      // unreserved
      PCHAR.set('A', 'Z' + 1);
      PCHAR.set('a', 'z' + 1);
      PCHAR.set('0', '9' + 1);
      PCHAR.set('-');
      PCHAR.set('.');
      PCHAR.set('_');
      PCHAR.set('~');

      // sub-delims
      PCHAR.set('!');
      PCHAR.set('$');
      PCHAR.set('&');
      PCHAR.set('\'');
      PCHAR.set('(');
      PCHAR.set(')');
      PCHAR.set('*');
      PCHAR.set('+');
      PCHAR.set(',');
      PCHAR.set(';');
      PCHAR.set('=');

      //
      PCHAR.set(':');
      PCHAR.set('@');
   }

   /**
    * segment = *pchar
    */
   protected static final BitSet SEGMENT = new BitSet();

   /** . */
   protected static final BitSet SEGMENT_VALUE = new BitSet();

   /** . */
   protected static final BitSet MATRIX_PARAM_NAME = new BitSet();

   /** . */
   protected static final BitSet MATRIX_PARAM_VALUE = new BitSet();

   static
   {
      SEGMENT.or(PCHAR);
   }

   static
   {
      SEGMENT_VALUE.or(SEGMENT);
      SEGMENT_VALUE.clear(';');
   }

   static
   {
      MATRIX_PARAM_NAME.or(SEGMENT_VALUE);
      MATRIX_PARAM_NAME.clear('=');
   }

   static
   {
      MATRIX_PARAM_VALUE.or(SEGMENT_VALUE);
   }

   /**
    * query = pchar / "/" / "?"
    */
   protected static final BitSet QUERY_PARAM = new BitSet();

   static
   {
      QUERY_PARAM.or(PCHAR);
      QUERY_PARAM.set('/');
      QUERY_PARAM.set('?');
      QUERY_PARAM.clear('=');
      QUERY_PARAM.clear('&');
   }

   static Chunk parse(String path) throws ServletException
   {
      return parse(path.split("/"), 0, null);
   }

   static Chunk parse(String path, Map<String, String[]> query) throws ServletException
   {
      return parse(path.split("/"), 0, query);
   }

   private static Chunk parse(String[] path, int index, Map<String, String[]> query) throws ServletException
   {
      if (index < path.length)
      {
         String s = path[index];
         if (s.length() > 0)
         {
            // Find matrix parameters
            String[] vals = s.split(";");

            // Parameters
            LinkedHashMap<String, String[]> parameters;
            switch (vals.length)
            {
               case 0:
                  throw new ServletException("Illegal path " + s);
               case 1:
                  parameters = null;
                  break;
               default:
                  parameters = new LinkedHashMap<String, String[]>();
                  for (int i = 1;i < vals.length;i++)
                  {
                     String val = vals[i];
                     int pos = val.indexOf('=');
                     String name = decode(val.substring(0, pos), MATRIX_PARAM_NAME);
                     String value = decode(val.substring(pos + 1), MATRIX_PARAM_VALUE);
                     String[] values = parameters.get(name);
                     values = values != null ? Tools.appendTo(values, value) : new String[]{value};
                     parameters.put(name, values);
                  }
                  break;
            }

            //
            return new Segment(decode(vals[0], SEGMENT_VALUE), parameters, parse(path, index + 1, query));
         }
         else
         {
            return parse(path, index + 1, query);
         }
      }
      else
      {
         return new Query(query);
      }
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      writeTo(sb, "&");
      return sb.toString();
   }

   protected abstract void writeTo(StringBuilder sb, String amp);

   private static int hexa(char c)
   {
      if (c >= '0' && c <= '9')
      {
         return c - '0';
      }
      if (c >= 'A' && c <= 'Z')
      {
         return c - 'A';
      }
      if (c >= 'a' && c <= 'z')
      {
         return c - 'z';
      }
      return -1;
   }

   protected static String decode(String value)
   {
      return decode(value, PCHAR);
   }

   protected static String decode(String value, BitSet allowed)
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int i = 0;
      while (i < value.length())
      {
         char c = value.charAt(i++);
         if (c == '%')
         {
            if (i + 1 < value.length())
            {
               int i1 = Character.digit(value.charAt(i++), 16);
               int i2 = Character.digit(value.charAt(i++), 16);
               if (i1 == -1 || i2 == -1)
               {
                  continue;
               }
               byte b = (byte) ((i1 << 4) + i2);
               baos.write(b);
            }
            else
            {
               break;
            }
         }
         else if (allowed.get(c))
         {
            baos.write(c);
         }
         else
         {
            // Try best effort
            CharBuffer cb = CharBuffer.allocate(1);
            cb.put(c);
            cb.flip();
            ByteBuffer bytes = UTF_8.encode(cb);
            while (bytes.hasRemaining())
            {
               byte b = bytes.get();
               baos.write(b);
            }
         }
      }
      byte[] bytes = baos.toByteArray();
      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      CharBuffer chars = UTF_8.decode(buffer);
      return chars.toString();
   }

   protected static void encode(StringBuilder sb, String value, BitSet allowed)
   {
      for (int i = 0;i < value.length();i++)
      {
         char c = value.charAt(i);
         if (allowed.get(c))
         {
            sb.append(c);
         }
         else
         {
            CharBuffer cb = CharBuffer.allocate(1);
            cb.put(c);
            cb.flip();
            ByteBuffer bytes = UTF_8.encode(cb);
            while (bytes.hasRemaining())
            {
               byte b = bytes.get();
               sb.append('%');
               sb.append(ALPHABET[(b >> 4)  & 0xF]);
               sb.append(ALPHABET[b & 0xF]);
            }
         }
      }
   }
}
