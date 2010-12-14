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

import org.jboss.unit.api.pojo.annotations.Test;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.controller.state.PortletPageNavigationalStateSerialization;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.PortletControllerContext;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.io.IOTools;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import static org.jboss.unit.api.Assert.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@Test
public class StateControllerContextTestCase
{

   /** . */
   private StateControllerContextImpl cc = new StateControllerContextImpl(dummyContext);

   @Test
   public void testMarshalling1()
   {
      PortletPageNavigationalState pageNS = cc.createPortletPageNavigationalState(true);
      assertMarshallable(pageNS);
   }

   @Test
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
      PortletPageNavigationalState pageNS = cc.createPortletPageNavigationalState(true);
      pageNS.setPortletWindowNavigationalState("foo", new PortletWindowNavigationalState(portletNavigationalState, mode, windowState));
      assertMarshallable(pageNS);
   }

   private void assertMarshallable(PortletPageNavigationalState pageNS)
   {
      byte[] bytes = IOTools.serialize(new PortletPageNavigationalStateSerialization(cc), /*SerializationFilter.COMPRESSOR, */pageNS);
      PortletPageNavigationalState expectedPageNS = IOTools.unserialize(new PortletPageNavigationalStateSerialization(cc), /*SerializationFilter.COMPRESSOR, */bytes);
      assertEquals(expectedPageNS.getPortletWindowIds(), pageNS.getPortletWindowIds());
      for (String windowId : expectedPageNS.getPortletWindowIds())
      {
         PortletWindowNavigationalState windowNS = expectedPageNS.getPortletWindowNavigationalState(windowId);
         PortletWindowNavigationalState expectedWindowNS = pageNS.getPortletWindowNavigationalState(windowId);
         assertEquals(windowNS.getPortletNavigationalState(), expectedWindowNS.getPortletNavigationalState());
         assertEquals(windowNS.getMode(), expectedWindowNS.getMode());
         assertEquals(windowNS.getWindowState(), expectedWindowNS.getWindowState());
      }
   }

   private static final PortletControllerContext dummyContext = new PortletControllerContext()
   {
      public HttpServletRequest getClientRequest()
      {
         throw new NotYetImplemented();
      }

      public HttpServletResponse getClientResponse()
      {
         throw new NotYetImplemented();
      }

      public PortletInfo getPortletInfo(String windowId) 
      {
         throw new NotYetImplemented();
      }

      public PortletInvocationContext createPortletInvocationContext(String windowId, PortletPageNavigationalState pageNavigationalState)
      {
         throw new NotYetImplemented();
      }

      public PortletInvocationResponse invoke(ActionInvocation actionInvocation) throws PortletInvokerException
      {
         throw new NotYetImplemented();
      }

      public PortletInvocationResponse invoke(List<Cookie> requestCookies, EventInvocation eventInvocation) throws PortletInvokerException
      {
         throw new NotYetImplemented();
      }

      public PortletInvocationResponse invoke(ResourceInvocation resourceInvocation) throws PortletInvokerException
      {
         throw new NotYetImplemented();
      }

      public PortletInvocationResponse invoke(List<Cookie> requestCookies, RenderInvocation renderInvocation) throws PortletInvokerException
      {
         throw new NotYetImplemented();
      }

      public EventControllerContext getEventControllerContext()
      {
         throw new NotYetImplemented();
      }

      public StateControllerContext getStateControllerContext()
      {
         throw new NotYetImplemented();
      }
   };

}
