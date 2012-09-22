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

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StateString;
import org.gatein.pc.controller.event.WindowEvent;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.request.PortletRequest;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.response.PageUpdateResponse;
import org.gatein.pc.controller.response.PortletResponse;
import org.gatein.pc.controller.state.PageNavigationalState;
import org.gatein.pc.controller.state.WindowNavigationalState;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 10580 $
 */
class PortletRequestHandler extends RequestHandler<PortletRequest>
{

   /** . */
   private static final EventControllerContextSafeInvoker safeInvoker = new EventControllerContextSafeInvoker();

   public PortletRequestHandler(PortletController controller)
   {
      super(PortletRequest.class, controller);
   }

   ControllerResponse processResponse(
      ControllerContext context,
      PortletRequest portletRequest,
      PortletInvocationResponse response) throws PortletInvokerException
   {
      // The page navigational state we will operate on during the request
      // Either we have nothing in the request so we create a new one
      // Or we have one but we copy it as we should not modify the input state provided
      PageNavigationalState pageNavigationalState = portletRequest.getPageNavigationalState();
      if (pageNavigationalState == null)
      {
         pageNavigationalState = new PageNavigationalState(true);
      }
      else
      {
         pageNavigationalState = new PageNavigationalState(pageNavigationalState, true);
      }

      //
      ResponseProperties requestProperties = new ResponseProperties();

      //
      if (response instanceof UpdateNavigationalStateResponse)
      {
         // Update portlet NS
         UpdateNavigationalStateResponse updateResponse = (UpdateNavigationalStateResponse)response;

         //
         updateNavigationalState(context, portletRequest.getWindowId(), updateResponse, pageNavigationalState);

         //
         ResponseProperties update = updateResponse.getProperties();
         if (update != null)
         {
            requestProperties.append(update);
         }

         //
         EventControllerContext eventCC = context.getEventControllerContext();

         //
         EventPhaseContext phaseContext = new EventPhaseContext(controller, context, log);

         // Feed session it with the events that may have been produced
         for (UpdateNavigationalStateResponse.Event portletEvent : updateResponse.getEvents())
         {
            if (!phaseContext.push(new WindowEvent(portletEvent.getName(), portletEvent.getPayload(), portletRequest.getWindowId())))
            {
               return new PageUpdateResponse(updateResponse, requestProperties, pageNavigationalState, PortletResponse.INTERRUPTED);
            }
         }

         // Deliver events
         while (phaseContext.hasNext())
         {
            WindowEvent toConsumeEvent = phaseContext.next();

            // Apply consumed event quota if necessary
            int consumedEventThreshold = controller.getConsumedEventThreshold();
            if (consumedEventThreshold >= 0)
            {
               if (phaseContext.consumedEventSize + 1 > consumedEventThreshold)
               {
                  log.trace("Event distribution interrupted because the maximum number of consumed event is reached");
                  safeInvoker.eventDiscarded(eventCC, phaseContext, toConsumeEvent, EventControllerContext.CONSUMED_EVENT_FLOODED);
                  return new PageUpdateResponse(updateResponse, requestProperties, pageNavigationalState, PortletResponse.INTERRUPTED);
               }
            }

            //
            PortletInvocationResponse eventResponse;
            try
            {
               eventResponse = deliverEvent(context, toConsumeEvent, pageNavigationalState, requestProperties.getCookies());
            }
            catch (Exception e)
            {
               log.trace("Event delivery of " + toConsumeEvent + " failed", e);
               safeInvoker.eventFailed(eventCC, phaseContext, toConsumeEvent, e);
               continue;
            }

            // Now it is consumed
            phaseContext.consumedEventSize++;

            // Update nav state if needed
            if (eventResponse instanceof UpdateNavigationalStateResponse)
            {
               UpdateNavigationalStateResponse eventStateResponse = (UpdateNavigationalStateResponse)eventResponse;

               // Update ns
               updateNavigationalState(context, toConsumeEvent.getWindowId(), eventStateResponse, pageNavigationalState);

               // Add events to source event queue
               for (UpdateNavigationalStateResponse.Event portletEvent : eventStateResponse.getEvents())
               {
                  WindowEvent toRouteEvent = new WindowEvent(portletEvent.getName(), portletEvent.getPayload(), toConsumeEvent.getWindowId());

                  //
                  if (!phaseContext.push(toConsumeEvent,  toRouteEvent))
                  {
                     return new PageUpdateResponse(updateResponse, requestProperties, pageNavigationalState, PortletResponse.INTERRUPTED);
                  }
               }

               //
               ResponseProperties updateProperties = eventStateResponse.getProperties();
               if (updateProperties != null)
               {
                  requestProperties.append(updateProperties);
               }
            }

            // And we make a callback
            safeInvoker.eventConsumed(eventCC, phaseContext, toConsumeEvent, eventResponse);
         }

         //
         return new PageUpdateResponse(updateResponse, requestProperties, pageNavigationalState, PortletResponse.DISTRIBUTION_DONE);
      }
      else
      {
         return new PortletResponse(response, PortletResponse.DISTRIBUTION_DONE);
      }
   }

   private PortletInvocationResponse deliverEvent(
      ControllerContext context,
      WindowEvent event,
      PageNavigationalState pageNavigationalState,
      List<Cookie> requestCookies) throws PortletInvokerException
   {
      WindowNavigationalState windowNS = pageNavigationalState.getWindowNavigationalState(event.getWindowId());

      //
      if (windowNS == null)
      {
         windowNS = new WindowNavigationalState();
      }

      //
      Map<String, String[]> publicNS = context.getStateControllerContext().getPublicWindowNavigationalState(context, pageNavigationalState, event.getWindowId());

      //
      PortletInvocationContext portletInvocationContext = context.createPortletInvocationContext(event.getWindowId(), pageNavigationalState);
      EventInvocation eventInvocation = new EventInvocation(portletInvocationContext);

      //
      eventInvocation.setMode(windowNS.getMode());
      eventInvocation.setWindowState(windowNS.getWindowState());
      eventInvocation.setNavigationalState(windowNS.getPortletNavigationalState());
      eventInvocation.setPublicNavigationalState(publicNS);
      eventInvocation.setName(event.getName());
      eventInvocation.setPayload(event.getPayload());

      //
      return context.invoke(event.getWindowId(), requestCookies, eventInvocation);
   }

   private void updateNavigationalState(
      ControllerContext context,
      String windowId,
      UpdateNavigationalStateResponse update,
      PageNavigationalState page)
      throws PortletInvokerException
   {
      WindowNavigationalState windowNS = page.getWindowNavigationalState(windowId);

      //
      if (windowNS == null)
      {
         windowNS = new WindowNavigationalState();
      }

      //
      Mode mode = windowNS.getMode();
      if (update.getMode() != null)
      {
         mode = update.getMode();
      }
      WindowState windowState = windowNS.getWindowState();
      if (update.getWindowState() != null)
      {
         windowState = update.getWindowState();
      }
      StateString portletNS = windowNS.getPortletNavigationalState();
      if (update.getNavigationalState() != null)
      {
         portletNS = update.getNavigationalState();
      }
      windowNS = new WindowNavigationalState(portletNS, mode, windowState);
      page.setWindowNavigationalState(windowId, windowNS);

      // Now update shared state scoped at page
      Map<String, String[]> publicNS = update.getPublicNavigationalStateUpdates();
      if (publicNS != null)
      {
         context.getStateControllerContext().updatePublicNavigationalState(
            context,
            page,
            windowId,
            publicNS);
      }
   }
}
