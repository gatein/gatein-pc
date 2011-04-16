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

package org.gatein.pc.test.portlet.navigation;

import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.StateString;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertNotNull;
import org.jboss.unit.api.pojo.annotations.Test;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 5976 $
 * @since 2.4 (Apr 30, 2006)
 */
@Test
public class StateStringTestCase
{
   public static final String NAME1 = "param1";
   public static final String VALUE1 = "value1";
   public static final String VALUE2 = "value2";

   @Test
   public void testNavigationalState() throws Exception
   {
      ParametersStateString ns = ParametersStateString.create();
      ns.setValue(NAME1, VALUE1);
      assertEquals(VALUE1, ns.getValue(NAME1));

      String opaqueValue = ns.getStringValue();
      System.out.println("opaqueValue = " + opaqueValue);
      assertNotNull(opaqueValue);

      ns = (ParametersStateString)StateString.create(opaqueValue);
      assertEquals(VALUE1, ns.getValue(NAME1));
   }
}
