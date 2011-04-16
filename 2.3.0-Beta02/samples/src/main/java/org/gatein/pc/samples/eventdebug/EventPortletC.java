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
package org.gatein.pc.samples.eventdebug;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Event;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @version $Revision: 630 $
 */
public class EventPortletC extends GenericPortlet
{

   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.setContentType("text/html");
      Writer writer = renderResponse.getWriter();
      PortletURL addURL = renderResponse.createActionURL();
      addURL.setParameter("id", "B");

      writer.append("<a href='").append(addURL.toString()).append("'\">Dispatch Event B</a></td></tr>");

   }

   @Override
   public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
   {
      String value = actionRequest.getParameter("id");
      if (value != null){
         actionResponse.setEvent(EventB.QNAME, new EventB(value));
      }

   }


   @Override
   public void processEvent(EventRequest eventRequest, EventResponse eventResponse) throws PortletException, IOException
   {
      Event event = eventRequest.getEvent();
      System.out.println("------------c----" + event.getName());
      if (event.getName().equals("EventA"))
      {
         EventA eventA = (EventA)event.getValue();
         System.out.println("-------------c--eventA-" + eventA);
         //eventA.getId();
      }
      if (event.getName().equals("EventB"))
      {
         EventB eventB = (EventB)event.getValue();
         System.out.println("-------------c--eventB-" + eventB);
         //eventB.getId();
      }
      if (event.getName().equals("EventC"))
      {
         EventC eventC = (EventC)event.getValue();
         //eventC.getId();
      }
   }
}
