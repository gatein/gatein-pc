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

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class Segment extends Chunk implements Iterable<Segment>
{

   /** . */
   final String value;

   /** . */
   final Map<String, String[]> parameters;

   /** . */
   Chunk next;

   Segment(String value)
   {
      this(value, (Chunk)null);
   }

   Segment(String value, Chunk next)
   {
      this(value, null, next);
   }

   Segment(String value, Map<String, String[]> parameters)
   {
      this(value, parameters, null);
   }

   Segment(String value, Map<String, String[]> parameters, Chunk next)
   {
      this.value = value;
      this.parameters = parameters;
      this.next = next;
   }

   int size()
   {
      return next instanceof Segment ? ((Segment)next).size() + 1 : 1;
   }

   @Override
   public Iterator<Segment> iterator()
   {
      return new Iterator<Segment>()
      {

         private Segment next = Segment.this;

         @Override
         public boolean hasNext()
         {
            return next != null;
         }

         @Override
         public Segment next()
         {
            if (next == null)
            {
               throw new NoSuchElementException();
            }
            Segment current = next;
            next = next.next instanceof Segment ? (Segment)next.next : null;
            return current;
         }

         @Override
         public void remove()
         {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   protected void writeTo(StringBuilder sb)
   {
      sb.append('/');
      encode(sb, value, SEGMENT_VALUE);
      if (parameters != null)
      {
         for (Map.Entry<String, String[]> parameter : parameters.entrySet())
         {
            for (String value : parameter.getValue())
            {
               sb.append(';');
               encode(sb, parameter.getKey(), MATRIX_PARAM_NAME);
               sb.append('=');
               encode(sb, value, MATRIX_PARAM_VALUE);
            }
         }
      }
      if (next != null)
      {
         next.writeTo(sb);
      }
   }
}
