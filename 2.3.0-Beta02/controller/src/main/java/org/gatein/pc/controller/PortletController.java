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

import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.controller.request.PortletRequest;
import org.gatein.pc.controller.request.PortletResourceRequest;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.api.Mode;

import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.List;

/**
 * <p>The portlet controller which handles the page state management and the interactions between the action phase and the
 * event phase. It really only does that and not more.</p>
 *
 * <p>The event distribution is based on a fifo policy.</p>
 *
 *
 * <p/>
 * 1/ introduce EventRequest so the portal can send events directly to a portlet
 * <p/>
 * 2/ make the event controller return a decision for a given event. Today it just returns a list of events to process.
 * An event can be handled in various manners by the controller: - produce new events (what exists today) - make it a
 * portal event which delivers the event to the portal
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletController
{

   /** . */
   private boolean distributeNonConsumableEvents;

   /** . */
   private boolean distributeNonProduceableEvents;

   /** . */
   private int producedEventThreshold;

   /** . */
   private int consumedEventThreshold;

   public PortletController()
   {
      distributeNonConsumableEvents = true;
      distributeNonProduceableEvents = true;
      producedEventThreshold = 32;
      consumedEventThreshold = 64;
   }

   /**
    * This option configures the behavior of the controller when an event returned by the
    * event controller is not declared by the target receiving portlet. If the value is true, then the event
    * will be fired whatsoever otherwise it will be discarded.
    *
    * @return true if the controller distribute a non declared event
    */
   public boolean getDistributeNonConsumableEvents()
   {
      return distributeNonConsumableEvents;
   }

   public void setDistributeNonConsumableEvents(boolean distributeNonConsumableEvents)
   {
      this.distributeNonConsumableEvents = distributeNonConsumableEvents;
   }

   /**
    * This option configures the behavior of the controller when a portlet produces an event it does
    * not declare. If the value is true then the event will be managed by the event controller for
    * further redistribution, otherwise it will be discarded.
    *
    * @return true if the controller distribute a non declared event
    */
   public boolean getDistributeNonProduceableEvents()
   {
      return distributeNonProduceableEvents;
   }

   public void setDistributeNonProduceableEvents(boolean distributeNonProduceableEvents)
   {
      this.distributeNonProduceableEvents = distributeNonProduceableEvents;
   }

   /**
    * The option configures the maximum number of events that can be produced during one interaction.
    * A negative value means that there is no limit to the number of events that can be produced
    * during one interaction.
    *
    * @return the maximum number of produced events
    */
   public int getProducedEventThreshold()
   {
      return producedEventThreshold;
   }

   public void setProducedEventThreshold(int producedEventThreshold)
   {
      this.producedEventThreshold = producedEventThreshold;
   }

   /**
    * The option configures the maximum number of events that can be consumed during one interaction.
    * A negative value means that there is no limit to the number of events that can be consumed
    * during one interaction.
    *
    * @return the maximum number of consumed events
    */
   public int getConsumedEventThreshold()
   {
      return consumedEventThreshold;
   }

   public void setConsumedEventThreshold(int consumedEventThreshold)
   {
      this.consumedEventThreshold = consumedEventThreshold;
   }

   public ControllerResponse process(PortletControllerContext controllerContext, ControllerRequest controllerRequest) throws PortletInvokerException
   {
      if (controllerContext == null)
      {
         throw new IllegalArgumentException("Null context");
      }
      if (controllerRequest == null)
      {
         throw new IllegalArgumentException("Null request");
      }

      //
      RequestHandler handler;
      if (controllerRequest instanceof PortletRequest)
      {
         handler = new PortletRequestHandler(this);
      }
      else if (controllerRequest instanceof PortletResourceRequest)
      {
         handler = new PortletResourceRequestHandler(this);
      }
      else
      {
         throw new IllegalArgumentException("Unknown request type: " + controllerRequest.getClass().getName());
      }

      //
      return handler.handle(controllerContext, controllerRequest);
   }

   public PortletInvocationResponse render(
      PortletControllerContext controllerContext,
      List<Cookie> cookies,
      PortletPageNavigationalState pageNavigationalState,
      String windowId) throws PortletInvokerException
   {
      PortletWindowNavigationalState windowNS = null;
      if (pageNavigationalState != null)
      {
         windowNS = pageNavigationalState.getPortletWindowNavigationalState(windowId);
      }

      //
      Map<String, String[]> publicNS = null;
      if (pageNavigationalState != null)
      {
         publicNS = pageNavigationalState.getPortletPublicNavigationalState(windowId);
      }

      //
      org.gatein.pc.api.Mode mode = Mode.VIEW;
      org.gatein.pc.api.WindowState windowState = org.gatein.pc.api.WindowState.NORMAL;
      StateString portletNS = null;

      //
      if (windowNS != null)
      {
         if (windowNS.getMode() != null)
         {
            mode = windowNS.getMode();
         }
         if (windowNS.getWindowState() != null)
         {
            windowState = windowNS.getWindowState();
         }
         if (windowNS.getPortletNavigationalState() != null)
         {
            portletNS = windowNS.getPortletNavigationalState();
         }
      }

      //
      PortletInvocationContext renderContext = controllerContext.createPortletInvocationContext(windowId, pageNavigationalState);

      //
      RenderInvocation render = new RenderInvocation(renderContext);

      //
      render.setMode(mode);
      render.setWindowState(windowState);
      render.setNavigationalState(portletNS);
      render.setPublicNavigationalState(publicNS);

      //
      return controllerContext.invoke(cookies, render);
   }
}
