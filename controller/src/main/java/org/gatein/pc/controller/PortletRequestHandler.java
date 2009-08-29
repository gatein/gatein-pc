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

import org.gatein.pc.api.WindowState;
import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.request.PortletActionRequest;
import org.gatein.pc.controller.request.PortletRenderRequest;
import org.gatein.pc.controller.request.PortletRequest;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.response.PageUpdateResponse;
import org.gatein.pc.controller.response.PortletResponse;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.api.invocation.ActionInvocation;
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
      PortletControllerContext context,
      PortletRequest portletRequest,
      PortletInvocationResponse response) throws PortletInvokerException
   {
      StateControllerContext stateContext = context.getStateControllerContext();

      // The page navigational state we will operate on during the request
      // Either we have nothing in the request so we create a new one
      // Or we have one but we copy it as we should not modify the input state provided
      PortletPageNavigationalState pageNavigationalState = portletRequest.getPageNavigationalState();
      if (pageNavigationalState == null)
      {
         pageNavigationalState = stateContext.createPortletPageNavigationalState(true);
      }
      else
      {
         pageNavigationalState = stateContext.clonePortletPageNavigationalState(pageNavigationalState, true);
      }

      //
      ResponseProperties requestProperties = new ResponseProperties();

      //
      if (response instanceof UpdateNavigationalStateResponse)
      {
         // Update portlet NS
         UpdateNavigationalStateResponse updateResponse = (UpdateNavigationalStateResponse)response;
         updateNavigationalState(context, portletRequest.getWindowId(), updateResponse, pageNavigationalState);

         //
         ResponseProperties update = updateResponse.getProperties();
         if (update != null)
         {
            requestProperties.append(update);
         }

         //
         EventControllerContext eventCC = context.getEventControllerContext();
         UpdateNavigationalStateResponse stateResponse = (UpdateNavigationalStateResponse)response;

         //
         EventPhaseContextImpl phaseContext = new EventPhaseContextImpl(log);

         // Feed session it with the events that may have been produced
         for (UpdateNavigationalStateResponse.Event portletEvent : stateResponse.getEvents())
         {
            PortletWindowEvent producedEvent = new PortletWindowEvent(portletEvent.getName(), portletEvent.getPayload(), portletRequest.getWindowId());
            phaseContext.producedEvents.add(new EventProduction(null, producedEvent));
         }

         //
         int eventDistributionStatus = PortletResponse.DISTRIBUTION_DONE;

         // Deliver events
         while (phaseContext.producedEvents.size() > 0)
         {
            EventProduction eventProduction = phaseContext.producedEvents.removeFirst();
            PortletWindowEvent producedEvent = eventProduction.getProducedEvent();

            //
            String producerId = producedEvent.getWindowId();
            PortletInfo producerPortletInfo = context.getPortletInfo(producerId);

            //
            if (producerPortletInfo == null)
            {
               log.trace("Cannot deliver event " + producedEvent +" because the producer does not have portlet info");
               safeInvoker.eventDiscarded(eventCC, phaseContext, producedEvent, EventControllerContext.EVENT_PRODUCER_INFO_NOT_AVAILABLE);
               continue;
            }

            //
            if (!controller.getDistributeNonProduceableEvents())
            {
               if (!producerPortletInfo.getEventing().getProducedEvents().containsKey(producedEvent.getName()))
               {
                  log.trace("Cannot deliver event " + producedEvent +" because the producer of the event does not produce the event name");
                  safeInvoker.eventDiscarded(eventCC, phaseContext, producedEvent, EventControllerContext.PORTLET_DOES_NOT_CONSUME_EVENT);
                  continue;
               }
            }

            // Apply produced event quota if necessary
            int producedEventThreshold = controller.getProducedEventThreshold();
            if (producedEventThreshold >= 0)
            {
               if (phaseContext.producedEventSize + 1 > producedEventThreshold)
               {
                  log.trace("Event distribution interrupted because the maximum number of produced event is reached");
                  eventDistributionStatus = PortletResponse.PRODUCED_EVENT_FLOODED;
                  safeInvoker.eventDiscarded(eventCC, phaseContext, producedEvent, EventControllerContext.PRODUCED_EVENT_FLOODED);
                  break;
               }
            }

            // Give control to the event context
            phaseContext.mode = EventPhaseContextImpl.READ_WRITE_MODE;
            if (!safeInvoker.eventProduced(eventCC, phaseContext, eventProduction.getConsumedEvent(), producedEvent))
            {
               continue;
            }

            // Perform flow control
            if (phaseContext.mode == EventPhaseContextImpl.INTERRUPTED_MODE)
            {
               log.trace("Event distribution interrupted by controller context");
               eventDistributionStatus = PortletResponse.INTERRUPTED;
               break;
            }

            //
            while (phaseContext.toConsumeEvents.size() > 0)
            {
               PortletWindowEvent toConsumeEvent = phaseContext.toConsumeEvents.removeFirst();
               String consumedId = toConsumeEvent.getWindowId();

               //
               PortletInfo consumerPortletInfo = context.getPortletInfo(consumedId);
               if (consumerPortletInfo == null)
               {
                  log.trace("Cannot deliver event " + producedEvent +" because the consumer of the event does not have a portlet info");
                  safeInvoker.eventDiscarded(eventCC, phaseContext, toConsumeEvent, EventControllerContext.EVENT_CONSUMER_INFO_NOT_AVAILABLE);
                  continue;
               }

               //
               if (!controller.getDistributeNonConsumableEvents())
               {
                  if (!consumerPortletInfo.getEventing().getConsumedEvents().containsKey(toConsumeEvent.getName()))
                  {
                     log.trace("Cannot deliver event " + producedEvent +" because the consumer of the event does not accept the event name");
                     safeInvoker.eventDiscarded(eventCC, phaseContext, toConsumeEvent, EventControllerContext.PORTLET_DOES_NOT_CONSUME_EVENT);
                     continue;
                  }
               }

               // Apply consumed event quota if necessary
               int consumedEventThreshold = controller.getConsumedEventThreshold();
               if (consumedEventThreshold >= 0)
               {
                  if (phaseContext.consumedEventSize + 1 > consumedEventThreshold)
                  {
                     log.trace("Event distribution interrupted because the maximum number of consumed event is reached");
                     safeInvoker.eventDiscarded(eventCC, phaseContext, toConsumeEvent, EventControllerContext.CONSUMED_EVENT_FLOODED);
                     eventDistributionStatus = PortletResponse.CONSUMED_EVENT_FLOODED;
                     break;
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
                     PortletWindowEvent toRouteEvent = new PortletWindowEvent(portletEvent.getName(), portletEvent.getPayload(), toConsumeEvent.getWindowId());
                     phaseContext.producedEvents.add(new EventProduction(toConsumeEvent, toRouteEvent));
                  }

                  //
                  ResponseProperties updateProperties = eventStateResponse.getProperties();
                  if (updateProperties != null)
                  {
                     requestProperties.append(updateProperties);
                  }
               }

               // And we make a callback
               phaseContext.mode = EventPhaseContextImpl.READ_MODE;
               safeInvoker.eventConsumed(eventCC, phaseContext, toConsumeEvent, eventResponse);
            }

            // We archive the consumed event in the history
            phaseContext.producedEventSize++;
         }

         //
         return new PageUpdateResponse(updateResponse, requestProperties, pageNavigationalState, eventDistributionStatus);
      }
      else
      {
         return new PortletResponse(response, PortletResponse.DISTRIBUTION_DONE);
      }
   }

   PortletInvocationResponse invoke(PortletControllerContext context, PortletRequest portletRequest) throws PortletInvokerException
   {
      if (portletRequest instanceof PortletRenderRequest)
      {
         PortletRenderRequest portletRenderRequest = (PortletRenderRequest)portletRequest;

         //
         UpdateNavigationalStateResponse updateNavigationalState = new UpdateNavigationalStateResponse();
         updateNavigationalState.setMode(portletRenderRequest.getWindowNavigationalState().getMode());
         updateNavigationalState.setWindowState(portletRenderRequest.getWindowNavigationalState().getWindowState());
         updateNavigationalState.setNavigationalState(portletRenderRequest.getWindowNavigationalState().getPortletNavigationalState());
         updateNavigationalState.setPublicNavigationalStateUpdates(portletRenderRequest.getPublicNavigationalStateChanges());

         //
         return updateNavigationalState;
      }
      else
      {
         PortletActionRequest portletActionRequest = (PortletActionRequest)portletRequest;

         //
         PortletPageNavigationalState pageNavigationalState = portletActionRequest.getPageNavigationalState();

         //
         org.gatein.pc.api.Mode mode = portletActionRequest.getWindowNavigationalState().getMode();
         if (mode == null)
         {
            mode = org.gatein.pc.api.Mode.VIEW;
         }

         //
         WindowState windowState = portletActionRequest.getWindowNavigationalState().getWindowState();
         if (windowState == null)
         {
            windowState = WindowState.NORMAL;
         }

         //
         Map<String, String[]> publicNS = null;
         if (pageNavigationalState != null)
         {
            publicNS = pageNavigationalState.getPortletPublicNavigationalState(portletRequest.getWindowId());
         }

         PortletInvocationContext portletInvocationContext = context.createPortletInvocationContext(portletRequest.getWindowId(), pageNavigationalState);
         ActionInvocation actionInvocation = new ActionInvocation(portletInvocationContext);

         //
         actionInvocation.setMode(mode);
         actionInvocation.setWindowState(windowState);
         actionInvocation.setNavigationalState(portletActionRequest.getWindowNavigationalState().getPortletNavigationalState());
         actionInvocation.setPublicNavigationalState(publicNS);
         actionInvocation.setInteractionState(portletActionRequest.getInteractionState());
         actionInvocation.setForm(portletActionRequest.getBodyParameters() != null ? ParameterMap.clone(portletActionRequest.getBodyParameters()) : null);

         //
         return context.invoke(actionInvocation);
      }
   }

   private PortletInvocationResponse deliverEvent(
      PortletControllerContext context, PortletWindowEvent event,
      PortletPageNavigationalState pageNavigationalState,
      List<Cookie> requestCookies) throws PortletInvokerException
   {
      PortletWindowNavigationalState windowNS = pageNavigationalState.getPortletWindowNavigationalState(event.getWindowId());

      //
      if (windowNS == null)
      {
         windowNS = new PortletWindowNavigationalState();
      }

      //
      Map<String, String[]> publicNS = pageNavigationalState.getPortletPublicNavigationalState(event.getWindowId());

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
      return context.invoke(requestCookies, eventInvocation);
   }

   private void updateNavigationalState(
      PortletControllerContext context,
      String windowId,
      UpdateNavigationalStateResponse update,
      PortletPageNavigationalState pageNavigationalState)
      throws PortletInvokerException
   {
      PortletWindowNavigationalState windowNS = pageNavigationalState.getPortletWindowNavigationalState(windowId);

      //
      if (windowNS == null)
      {
         windowNS = new PortletWindowNavigationalState();
      }

      //
      org.gatein.pc.api.Mode mode = windowNS.getMode();
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
      windowNS = new PortletWindowNavigationalState(portletNS, mode, windowState);
      pageNavigationalState.setPortletWindowNavigationalState(windowId, windowNS);

      // Now update shared state scoped at page
      Map<String, String[]> publicNS = update.getPublicNavigationalStateUpdates();
      if (publicNS != null)
      {
         pageNavigationalState.setPortletPublicNavigationalState(windowId, publicNS);
      }
   }
}
