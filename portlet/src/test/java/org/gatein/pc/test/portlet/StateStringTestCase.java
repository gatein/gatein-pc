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
package org.gatein.pc.test.portlet;

import org.gatein.common.util.MapBuilder;
import org.gatein.pc.api.OpaqueStateString;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.StateString;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.api.pojo.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@Test
public class StateStringTestCase
{

   @Test
   public void testMarshalling() throws IOException
   {
      check(new OpaqueStateString("blah"));
      check(ParametersStateString.create());
      check(MapBuilder.hashMap("foo", new String[]{"bar"}).get());
      check(MapBuilder.hashMap("foo", new String[]{"foo_1"}).put("bar", new String[]{"bar_1", "bar_2"}).get());
   }

   private void check(Map<String, String[]> parameters) throws IOException
   {
      check(ParametersStateString.create(parameters));
   }

   private void check(StateString parameters) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(baos);
      parameters.writeTo(out);
      out.close();
      byte[] bytes = baos.toByteArray();
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      DataInputStream in = new DataInputStream(bais);
      StateString copy = StateString.create(in);
      assertEquals(parameters, copy);
   }
}
