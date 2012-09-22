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
package org.gatein.pc.controller;

import junit.framework.TestCase;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.controller.state.PageNavigationalState;
import org.gatein.pc.test.controller.unit.PageNavigationalStateSerialization;
import org.gatein.pc.controller.state.WindowNavigationalState;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.StateString;
import org.gatein.common.io.IOTools;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class StateControllerContextTestCase extends TestCase
{

   /** . */
   private StateControllerContextImpl cc = new StateControllerContextImpl();

   public void testMarshalling1()
   {
      PageNavigationalState pageNS = new PageNavigationalState(true);
      assertMarshallable(pageNS);
   }

   public void testMarshalling2()
   {
      ParametersStateString params = ParametersStateString.create();
      params.setValue("foo", "bar");
      test(null, null, null);
      test(null, Mode.VIEW, null);
      test(null, null, org.gatein.pc.api.WindowState.NORMAL);
      test(null, Mode.VIEW, org.gatein.pc.api.WindowState.NORMAL);
      test(null, Mode.create("foo"), null);
      test(null, null, WindowState.create("bar"));
      test(null, org.gatein.pc.api.Mode.create("foo"), WindowState.create("bar"));
      test(params, null, null);
      test(params, org.gatein.pc.api.Mode.VIEW, null);
      test(params, null, WindowState.NORMAL);
      test(params, org.gatein.pc.api.Mode.VIEW, WindowState.NORMAL);
      test(params, org.gatein.pc.api.Mode.create("foo"), null);
      test(params, null, WindowState.create("bar"));
      test(params, org.gatein.pc.api.Mode.create("foo"), WindowState.create("bar"));
   }

   private void test(StateString portletNavigationalState, org.gatein.pc.api.Mode mode, WindowState windowState)
   {
      PageNavigationalState pageNS = new PageNavigationalState(true);
      pageNS.setWindowNavigationalState("foo", new WindowNavigationalState(portletNavigationalState, mode, windowState));
      assertMarshallable(pageNS);
   }

   private void assertMarshallable(PageNavigationalState pageNS)
   {
      byte[] bytes = IOTools.serialize(new PageNavigationalStateSerialization(cc), /*SerializationFilter.COMPRESSOR, */pageNS);
      PageNavigationalState expectedPageNS = IOTools.unserialize(new PageNavigationalStateSerialization(cc), /*SerializationFilter.COMPRESSOR, */bytes);
      assertEquals(expectedPageNS.getWindowIds(), pageNS.getWindowIds());
      for (String windowId : expectedPageNS.getWindowIds())
      {
         WindowNavigationalState windowNS = expectedPageNS.getWindowNavigationalState(windowId);
         WindowNavigationalState expectedWindowNS = pageNS.getWindowNavigationalState(windowId);
         assertEquals(windowNS.getPortletNavigationalState(), expectedWindowNS.getPortletNavigationalState());
         assertEquals(windowNS.getMode(), expectedWindowNS.getMode());
         assertEquals(windowNS.getWindowState(), expectedWindowNS.getWindowState());
      }
   }
}
