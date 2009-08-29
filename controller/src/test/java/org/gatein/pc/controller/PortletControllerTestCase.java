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

import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.controller.request.PortletRenderRequest;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.response.PageUpdateResponse;
import org.gatein.pc.controller.response.PortletResponse;
import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.controller.event.AbstractEventControllerContext;
import org.gatein.pc.controller.handlers.EventProducerActionHandler;
import org.gatein.pc.controller.handlers.EventProducerEventHandler;
import org.gatein.pc.controller.handlers.NoOpEventHandler;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.OpaqueStateString;
import org.gatein.pc.api.StateString;
import org.gatein.pc.portlet.support.PortletSupport;
import org.gatein.pc.portlet.support.PortletInvokerSupport;
import org.gatein.pc.portlet.support.info.EventInfoSupport;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.controller.PortletController;
import org.gatein.common.util.Tools;
import org.gatein.pc.api.WindowState;
import org.jboss.unit.api.pojo.annotations.Test;
import org.jboss.unit.api.pojo.annotations.Create;
import static org.jboss.unit.api.Assert.*;

import javax.xml.namespace.QName;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@Test
public class PortletControllerTestCase
{

   /** . */
   PortletController controller = new PortletController();

   /** . */
   PortletControllerContextSupport context = new PortletControllerContextSupport();

   /** . */
   WiringEventControllerContext eventControllerContext = new WiringEventControllerContext();

   /** . */
   PortletInvokerSupport invoker = context.getInvoker();

   @Create
   public void create()
   {
      context.setEventControllerContext(eventControllerContext);
   }

   @Test
   public void testPortletControllerRenderRequest() throws PortletInvokerException
   {
      invoker.addPortlet("foo");
      StateString portletNS = new OpaqueStateString("abc");
      PortletPageNavigationalState pageNS = context.getStateControllerContext().createPortletPageNavigationalState(true);
      PortletWindowNavigationalState windowNS = new PortletWindowNavigationalState(portletNS, org.gatein.pc.api.Mode.EDIT, WindowState.MAXIMIZED);
      PortletRenderRequest render = new PortletRenderRequest("foo", windowNS, new HashMap<String, String[]>(), pageNS);
      ControllerResponse response = controller.process(context, render);
      PageUpdateResponse pageUpdate = assertInstanceOf(response, PageUpdateResponse.class);
      PortletPageNavigationalState pageNS2 = assertNotNull(pageUpdate.getPageNavigationalState());
      assertEquals(Tools.toSet("foo"), pageNS2.getPortletWindowIds());
      PortletWindowNavigationalState windowNS2 = pageNS2.getPortletWindowNavigationalState("foo");
      assertNotNull(windowNS2);
      assertEquals(portletNS, windowNS2.getPortletNavigationalState());
      assertEquals(org.gatein.pc.api.Mode.EDIT, windowNS2.getMode());
      assertEquals(WindowState.MAXIMIZED, windowNS2.getWindowState());
   }

   @Test
   public void testAction() throws PortletInvokerException
   {
      testAction(false);
   }

   @Test
   public void testActionPublishesAnEvent() throws PortletInvokerException
   {
      testAction(true);
   }

   private void testAction(final boolean publishEvent) throws PortletInvokerException
   {
      final QName srcName = new QName("juu", "foo");
      final QName dstName = new QName("juu", "bar");

      //
      PortletSupport fooPortlet = invoker.addPortlet("foo");
      fooPortlet.addHandler(new PortletSupport.ActionHandler()
      {
         protected PortletInvocationResponse invoke(ActionInvocation action) throws PortletInvokerException
         {
            UpdateNavigationalStateResponse updateNS = new UpdateNavigationalStateResponse();
            updateNS.setMode(org.gatein.pc.api.Mode.EDIT);
            updateNS.setWindowState(WindowState.MAXIMIZED);
            updateNS.setNavigationalState(new OpaqueStateString("abc"));

            //
            if (publishEvent)
            {
               updateNS.queueEvent(new UpdateNavigationalStateResponse.Event(srcName, null));
            }
            
            //
            return updateNS;
         }
      });

      //
      PortletSupport barPortlet = invoker.addPortlet("bar");
      barPortlet.addHandler(new PortletSupport.EventHandler()
      {
         protected PortletInvocationResponse invoke(EventInvocation action) throws PortletInvokerException
         {
            assertEquals(dstName, action.getName());
            assertEquals(null, action.getPayload());

            //
            UpdateNavigationalStateResponse updateNS = new UpdateNavigationalStateResponse();
            updateNS.setMode(org.gatein.pc.api.Mode.HELP);
            updateNS.setWindowState(WindowState.MINIMIZED);
            updateNS.setNavigationalState(new OpaqueStateString("def"));

            //
            return updateNS;
         }
      });

      //
      eventControllerContext.createWire(srcName, "foo", dstName, "bar");

      //
      ControllerRequest request = context.createActionRequest("foo");
      ControllerResponse response = controller.process(context, request);
      PageUpdateResponse pageUpdate = assertInstanceOf(response, PageUpdateResponse.class);
      PortletPageNavigationalState pageNS = assertNotNull(pageUpdate.getPageNavigationalState());

      //
      PortletWindowNavigationalState fooNS = assertNotNull(pageNS.getPortletWindowNavigationalState("foo"));
      assertEquals(WindowState.MAXIMIZED, fooNS.getWindowState());
      assertEquals(org.gatein.pc.api.Mode.EDIT, fooNS.getMode());
      assertEquals(new OpaqueStateString("abc"), fooNS.getPortletNavigationalState());

      //
      if (publishEvent)
      {
         assertEquals(Tools.toSet("foo", "bar"), pageNS.getPortletWindowIds());

         //
         PortletWindowNavigationalState barNS = assertNotNull(pageNS.getPortletWindowNavigationalState("bar"));
         assertEquals(WindowState.MINIMIZED, barNS.getWindowState());
         assertEquals(org.gatein.pc.api.Mode.HELP, barNS.getMode());
         assertEquals(new OpaqueStateString("def"), barNS.getPortletNavigationalState());
      }
      else
      {
         assertEquals(Tools.toSet("foo"), pageNS.getPortletWindowIds());
      }
   }

   @Test
   public void testActionThrowsPortletInvokerException() throws PortletInvokerException
   {
      final PortletInvokerException e = new PortletInvokerException();
      PortletSupport fooPortlet = invoker.addPortlet("foo");
      fooPortlet.addHandler(new PortletSupport.ActionHandler()
      {
         protected PortletInvocationResponse invoke(ActionInvocation action) throws PortletInvokerException
         {
            throw e;
         }
      });

      //
      ControllerRequest request = context.createActionRequest("foo");

      try
      {
         controller.process(context, request);
         fail();
      }
      catch (PortletInvokerException ex)
      {
         assertSame(e, ex);
      }
   }

   @Test
   public void testProcessActionProducedEventIsDistributed() throws PortletInvokerException
   {
      QName srcName = new QName("juu", "foo");
      QName dstName = new QName("juu", "bar");
      PortletSupport fooPortlet = invoker.addPortlet("foo");
      PortletSupport barPortlet = invoker.addPortlet("bar");
      EventProducerActionHandler eventProducerHandler = new EventProducerActionHandler(srcName);
      NoOpEventHandler eventConsumer = new NoOpEventHandler();
      eventControllerContext.createWire(srcName, "foo", dstName, "bar");
      ControllerRequest request = context.createActionRequest("foo");

      //
      controller.setDistributeNonProduceableEvents(true);
      controller.setDistributeNonConsumableEvents(true);
      fooPortlet.addHandler(eventProducerHandler);
      barPortlet.addHandler(eventConsumer);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(1);
      barPortlet.assertInvocationCountIs(1);

      //
      controller.setDistributeNonProduceableEvents(true);
      controller.setDistributeNonConsumableEvents(false);
      fooPortlet.addHandler(eventProducerHandler);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(2);
      barPortlet.assertInvocationCountIs(1);

      //
      barPortlet.getInfo().getEventing().addConsumedEvent(new EventInfoSupport(dstName));

      //
      controller.setDistributeNonProduceableEvents(true);
      controller.setDistributeNonConsumableEvents(true);
      fooPortlet.addHandler(eventProducerHandler);
      barPortlet.addHandler(eventConsumer);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(3);
      barPortlet.assertInvocationCountIs(2);

      //
      controller.setDistributeNonProduceableEvents(true);
      controller.setDistributeNonConsumableEvents(false);
      fooPortlet.addHandler(eventProducerHandler);
      barPortlet.addHandler(eventConsumer);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(4);
      barPortlet.assertInvocationCountIs(3);

      //
      controller.setDistributeNonProduceableEvents(false);
      controller.setDistributeNonConsumableEvents(true);
      fooPortlet.addHandler(eventProducerHandler);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(5);
      barPortlet.assertInvocationCountIs(3);

      //
      controller.setDistributeNonProduceableEvents(false);
      controller.setDistributeNonConsumableEvents(false);
      fooPortlet.addHandler(eventProducerHandler);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(6);
      barPortlet.assertInvocationCountIs(3);

      //
      fooPortlet.getInfo().getEventing().addProducedEvent(new EventInfoSupport(srcName));

      //
      controller.setDistributeNonProduceableEvents(false);
      controller.setDistributeNonConsumableEvents(true);
      fooPortlet.addHandler(eventProducerHandler);
      barPortlet.addHandler(eventConsumer);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(7);
      barPortlet.assertInvocationCountIs(4);

      //
      controller.setDistributeNonProduceableEvents(false);
      controller.setDistributeNonConsumableEvents(false);
      fooPortlet.addHandler(eventProducerHandler);
      barPortlet.addHandler(eventConsumer);
      controller.process(context, request);
      fooPortlet.assertInvocationCountIs(8);
      barPortlet.assertInvocationCountIs(5);
   }

   @Test
   public void testEventFloodDetection() throws PortletInvokerException
   {
      QName srcName = new QName("juu", "foo");
      PortletSupport fooPortlet = invoker.addPortlet("foo");
      eventControllerContext.createWire(srcName, "foo", srcName, "foo");
      ControllerRequest request = context.createActionRequest("foo");

      //
      EventProducerActionHandler eventProducerActionHandler = new EventProducerActionHandler(srcName);
      EventProducerEventHandler eventProducerEventHandler = new EventProducerEventHandler(srcName);

      //
      controller.setConsumedEventThreshold(10);
      controller.setProducedEventThreshold(1);
      fooPortlet.addHandler(eventProducerActionHandler);
      fooPortlet.addHandler(eventProducerEventHandler);
      ControllerResponse response = controller.process(context, request);
      PageUpdateResponse updateResponse = assertInstanceOf(response, PageUpdateResponse.class);
      assertEquals(PortletResponse.PRODUCED_EVENT_FLOODED, updateResponse.getEventDistributionStatus());

      //
      controller.setConsumedEventThreshold(1);
      controller.setProducedEventThreshold(10);
      fooPortlet.addHandler(eventProducerActionHandler);
      fooPortlet.addHandler(eventProducerEventHandler);
      response = controller.process(context, request);
      updateResponse = assertInstanceOf(response, PageUpdateResponse.class);
      assertEquals(PortletResponse.CONSUMED_EVENT_FLOODED, updateResponse.getEventDistributionStatus());
   }

   @Test
   public void testEventFloodInterruption() throws PortletInvokerException
   {
      QName srcName = new QName("juu", "foo");
      PortletSupport fooPortlet = invoker.addPortlet("foo");
      eventControllerContext.createWire(srcName, "foo", srcName, "foo");
      ControllerRequest request = context.createActionRequest("foo");

      //
      EventProducerActionHandler eventProducerActionHandler = new EventProducerActionHandler(srcName);

      //
      controller.setConsumedEventThreshold(10);
      controller.setProducedEventThreshold(10);
      context.setEventControllerContext(new AbstractEventControllerContext()
      {
         public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent)
         {
            context.interrupt();
         }
      });
      fooPortlet.addHandler(eventProducerActionHandler);
      ControllerResponse response = controller.process(context, request);
      PageUpdateResponse updateResponse = assertInstanceOf(response, PageUpdateResponse.class);
      assertEquals(PortletResponse.INTERRUPTED, updateResponse.getEventDistributionStatus());
   }
}
