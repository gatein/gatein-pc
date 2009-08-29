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
package org.gatein.pc.test.portlet.state;

import org.gatein.pc.portlet.impl.state.StateConverterV0;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.StateConversionException;
import org.gatein.pc.portlet.state.StateConverter;
import org.gatein.pc.portlet.state.producer.PortletState;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Test;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@Test
public class StateConverterV0TestCase
{

   private final StateConverter converter = new StateConverterV0();

   @Test
   public void testIAE() throws StateConversionException
   {
      try
      {
         converter.marshall(PortletStateType.OPAQUE, null);
         fail("Was expecting an IAE");
      }
      catch (IllegalArgumentException expected)
      {
      }
      try
      {
         converter.unmarshall(PortletStateType.OPAQUE, null);
         fail("Was expecting an IAE");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   @Test
   public void testAlteredMagic() throws StateConversionException
   {
      byte[] bytes = converter.marshall(PortletStateType.OPAQUE, new PortletState("foo"));
      bytes[0] = (byte)0xCA;
      bytes[1] = (byte)0xFE;
      bytes[2] = (byte)0xBA;
      bytes[3] = (byte)0xBE;
      try
      {
         converter.unmarshall(PortletStateType.OPAQUE, bytes);
         fail("Was expecting a state conversion exception");
      }
      catch (StateConversionException expected)
      {
      }
   }

   @Test
   public void testBadVersionNumber() throws StateConversionException
   {
      byte[] bytes = converter.marshall(PortletStateType.OPAQUE,new PortletState("foo"));
      bytes[4] = (byte)0x01;
      try
      {
         converter.unmarshall(PortletStateType.OPAQUE, bytes);
         fail("Was expecting a state conversion exception");
      }
      catch (StateConversionException expected)
      {
      }
   }

   @Test
   public void testWorks() throws Exception
   {
      assertWorks(new PortletState("foo"));

      //
      PropertyMap props = new SimplePropertyMap();
      props.setProperty("ab", Arrays.asList("cd"));
      props.setProperty("ef", new ArrayList<String>());
      props.setProperty("gh", Arrays.asList(""));
      props.setProperty("ij", Arrays.asList("kl"));
      props.setProperty("mn", Arrays.asList("op", null));
      props.setProperty("qr", Arrays.asList(null, "st"));
      props.setProperty("uv", Arrays.asList("wx", null, "yz"));
      assertWorks(new PortletState("bar", props));
   }

   private void assertWorks(PortletState expectedState) throws Exception
   {
      byte[] bytes = converter.marshall(PortletStateType.OPAQUE, expectedState);
      assertNotNull(bytes);
      PortletState state = converter.unmarshall(PortletStateType.OPAQUE, bytes);
      assertNotNull(state);

      //
      assertEquals(expectedState.getPortletId(), state.getPortletId());
      ValueMapAssert.assertEquals(expectedState.getProperties(), state.getProperties());
   }
}
