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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.event.PortletWindowEvent;
import org.gatein.pc.controller.event.EventPhaseContext;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.info.PortletInfo;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Route events according to the portlets discovered on the page. For now it is pretty trivial.
 * We could leverage JSP tags to 'wire' portlets on the same page for instance.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PageEventControllerContext implements EventControllerContext
{

   /** . */
   private final PortalPrepareResponse prepareResponse;

   /** . */
   private final PagePortletControllerContext context;

   /** Used internally. */
   private final Map<PortletWindowEvent, EventRoute> routings;

   /** . */
   private final List<EventRoute> roots;

   public PageEventControllerContext(
      PagePortletControllerContext context,
      PortalPrepareResponse prepareResponse)
   {
      this.context = context;
      this.prepareResponse = prepareResponse;
      this.routings = new LinkedHashMap<PortletWindowEvent, EventRoute>();
      this.roots = new ArrayList<EventRoute>();
   }

   public List<EventRoute> getRoots()
   {
      return roots;
   }

   public void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent causeEvent)
   {
      EventRoute relatedRoute = routings.get(causeEvent);

      //
      for (String windowId : prepareResponse.getWindowIds())
      {
         try
         {
            Portlet portlet = this.context.getPortlet(windowId);

            //
            if (portlet != null)
            {
               PortletInfo portletInfo = portlet.getInfo();

               //
               if (portletInfo.getEventing().getConsumedEvents().containsKey(producedEvent.getName()))
               {
                  PortletWindowEvent destinationEvent = new PortletWindowEvent(producedEvent.getName(), producedEvent.getPayload(), windowId);

                  //
                  EventRoute eventRoute = new EventRoute(
                     relatedRoute,
                     producedEvent.getName(),
                     producedEvent.getPayload(),
                     producedEvent.getWindowId(),
                     destinationEvent.getWindowId());

                  //
                  if (relatedRoute != null)
                  {
                     relatedRoute.children.add(eventRoute);
                  }
                  else
                  {
                     roots.add(eventRoute);
                  }

                  //
                  routings.put(destinationEvent, eventRoute);

                  //
                  context.queueEvent(destinationEvent);
               }
            }
         }
         catch (PortletInvokerException e)
         {
            e.printStackTrace();
            context.interrupt();
         }
      }
   }

   public void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse)
   {
      EventRoute route = routings.get(consumedEvent);
      route.acknowledgement = new EventAcknowledgement.Consumed(consumerResponse);
   }

   public void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable)
   {
      EventRoute route = routings.get(failedEvent);
      route.acknowledgement = new EventAcknowledgement.Failed(throwable);
   }

   public void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause)
   {
      EventRoute route = routings.get(discardedEvent);
      route.acknowledgement = new EventAcknowledgement.Discarded(cause);
   }
}
