/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.test;

import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.api.pojo.annotations.Test;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class StringCodecTestCase
{

   private static final char EURO_CHAR = '\u20AC';

   @Test
   public void testA()
   {

      assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", StringCodec.encode("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
      assertEquals("abcdefghijklmnopqrstuvwxyz", StringCodec.encode("abcdefghijklmnopqrstuvwxyz"));
      assertEquals("0123456789", StringCodec.encode("0123456789"));
      assertEquals("_2F", StringCodec.encode("/"));
      assertEquals("_40", StringCodec.encode("@"));
      assertEquals("_E2_82_AC", StringCodec.encode(Character.toString(EURO_CHAR)));
      assertEquals("A_E2_82_ACB_40C", StringCodec.encode("A" + EURO_CHAR + "B@C"));

   }

   @Test
   public void testB()
   {
      assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", StringCodec.decode(StringCodec.encode("ABCDEFGHIJKLMNOPQRSTUVWXYZ")));
      assertEquals("abcdefghijklmnopqrstuvwxyz", StringCodec.decode(StringCodec.encode("abcdefghijklmnopqrstuvwxyz")));
      assertEquals("0123456789", StringCodec.decode(StringCodec.encode("0123456789")));
      assertEquals("/", StringCodec.decode(StringCodec.encode("/")));
      assertEquals("@", StringCodec.decode(StringCodec.encode("@")));
      assertEquals(Character.toString(EURO_CHAR), StringCodec.decode(StringCodec.encode(Character.toString(EURO_CHAR))));
      assertEquals("A" + EURO_CHAR + "B@C", StringCodec.decode(StringCodec.encode("A" + EURO_CHAR + "B@C")));
   }

}
