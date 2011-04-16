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

import org.gatein.pc.controller.request.PortletActionRequest;
import org.gatein.pc.controller.request.PortletRenderRequest;
import org.gatein.pc.controller.request.PortletResourceRequest;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.impl.state.StateControllerContextImpl;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.OpaqueStateString;
import org.gatein.common.util.ParameterMap;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Test;

import java.util.Map;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@Test
public class PortletControllerRequestConstructorTestCase
{

   private String windowId = "foo";
   private StateString interactionState = new OpaqueStateString("");
   private StateString resourceState = new OpaqueStateString("");
   private ParameterMap body = new ParameterMap();
   private PortletWindowNavigationalState windowNS = new PortletWindowNavigationalState();
   private PortletPageNavigationalState pageNS = new StateControllerContextImpl(new PortletControllerContextSupport()).createPortletPageNavigationalState(false);
   private Map<String, String[]> pageNSChanges = Collections.emptyMap();

   @Test
   public void testWithNonNullArgs()
   {
      new PortletActionRequest(windowId, interactionState, body, windowNS, pageNS);
      new PortletRenderRequest(windowId, windowNS, pageNSChanges, pageNS);
      new PortletResourceRequest(windowId, "foo", resourceState, body, new PortletResourceRequest.FullScope());
      new PortletResourceRequest(windowId, "foo", resourceState, body, new PortletResourceRequest.PortletScope(windowNS));
      new PortletResourceRequest(windowId, "foo", resourceState, body, new PortletResourceRequest.PageScope(windowNS, pageNS));
   }

   @Test
   public void testWithNullWindowNavigationalState()
   {
      new PortletActionRequest(windowId, interactionState, body, null, pageNS);
      new PortletRenderRequest(windowId, null, pageNSChanges, pageNS);
      new PortletResourceRequest(windowId, "foo", resourceState, body, new PortletResourceRequest.PageScope(null, pageNS));
   }

   @Test
   public void testNullPageNavigationalState()
   {
      new PortletActionRequest(windowId, interactionState, body, windowNS, null);
      new PortletRenderRequest(windowId, windowNS, pageNSChanges, null);
      new PortletResourceRequest(windowId, "foo", resourceState, body, new PortletResourceRequest.PageScope(windowNS, null));
   }

   @Test
   public void testWithNullBody()
   {
      new PortletActionRequest(windowId, interactionState, null, windowNS, pageNS);
      new PortletResourceRequest(windowId, "foo", resourceState, null, new PortletResourceRequest.FullScope());
      new PortletResourceRequest(windowId, "foo", resourceState, null, new PortletResourceRequest.PortletScope(windowNS));
      new PortletResourceRequest(windowId, "foo", resourceState, null, new PortletResourceRequest.PageScope(windowNS, pageNS));
   }

   @Test
   public void testWithNullResourceId()
   {
      new PortletResourceRequest(windowId, null, resourceState, body, new PortletResourceRequest.FullScope());
      new PortletResourceRequest(windowId, null, resourceState, body, new PortletResourceRequest.PortletScope(windowNS));
      new PortletResourceRequest(windowId, null, resourceState, body, new PortletResourceRequest.PageScope(windowNS, pageNS));
   }

   @Test
   public void testNullWindowId()
   {
      try
      {
         new PortletActionRequest(null, interactionState, body, windowNS, pageNS);
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletRenderRequest(null, windowNS, pageNSChanges, pageNS);
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletResourceRequest(null, "foo", resourceState, body, new PortletResourceRequest.FullScope());
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletResourceRequest(null, "foo", resourceState, body, new PortletResourceRequest.PortletScope(windowNS));
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletResourceRequest(null, "foo", resourceState, body, new PortletResourceRequest.PageScope(windowNS, pageNS));
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
   }

   @Test
   public void testNullInteractionState()
   {
      try
      {
         new PortletActionRequest(windowId, null, body, windowNS, pageNS);
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
   }

   @Test
   public void testNullResourceState()
   {
      try
      {
         new PortletResourceRequest(windowId, "foo", null, body, new PortletResourceRequest.FullScope());
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletResourceRequest(windowId, "foo", null, body, new PortletResourceRequest.PortletScope(windowNS));
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
      try
      {
         new PortletResourceRequest(windowId, "foo", null, body, new PortletResourceRequest.PageScope(windowNS, pageNS));
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
   }

   @Test
   public void testNullScope()
   {
      try
      {
         new PortletResourceRequest(windowId, "foo", resourceState, body, null);
         fail();
      }
      catch (IllegalArgumentException ignore)
      {
      }
   }
}
