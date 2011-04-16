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
package org.gatein.pc.samples.basic;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;
import javax.portlet.Event;
import javax.portlet.StateAwareResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class RandomEventPortlet extends GenericPortlet
{

   /** . */
   private static final QName[] QNAMES = {
      new QName("urn:jboss:portal:samples:basic", "Event1"),
      new QName("urn:jboss:portal:samples:basic", "Event2"),
      new QName("urn:jboss:portal:samples:basic", "Event3")
   };

   /** . */
   private static final String[] PARAM_NAMES = {
      "foo", "bar", "juu", "daa", "zee"
   };

   /** . */
   private static final String[] PARAM_VALUES = {
      "red", "green", "blue", "yellow", "white", "black"
   };

   /** . */
   private static final Random random = new Random();

   private static QName pickQName()
   {
      return pickValue(QNAMES);
   }

   private static <T> T pickValue(T[] values)
   {
      synchronized (random)
      {
         return values[random.nextInt(values.length)];
      }
   }

   private static boolean shouldFail()
   {
      synchronized (random)
      {
         return random.nextInt(3) == 0;
      }
   }

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      int repeat = Integer.parseInt(req.getParameter("repeat"));

      //
      publishState(resp, repeat);
   }

   public void processEvent(EventRequest req, EventResponse resp) throws PortletException, IOException
   {
      Event event = req.getEvent();

      //
      int repeat = ((Integer)event.getValue());
      String name = event.getName();
      System.out.println("Portlet " + getPortletConfig().getPortletName() + " received the event (" + name + "," + repeat + ")");

      //
      if (shouldFail())
      {
         System.out.println("Portlet " + getPortletConfig().getPortletName() + " decided to fail");

         //
         throw new PortletException("Don't be scarred, this is expected to happen");
      }

      //
      publishState(resp, repeat - 1);
   }

   private void publishState(StateAwareResponse resp, int repeat)
   {
      if (repeat > 0)
      {
         QName name = pickQName();
         resp.setEvent(name, repeat);

         //
         resp.setRenderParameter(pickValue(PARAM_NAMES), pickValue(PARAM_VALUES));

         //
         System.out.println("Portlet " + getPortletConfig().getPortletName() + " generated the event (" + name + "," + repeat + ")");
      }
   }

   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletURL actionURL = resp.createActionURL();

      //
      PrintWriter writer = resp.getWriter();

      //
      writer.println("<p>The random event portlet generates and consumes randomly generated events during the event " +
         "phase of a portlet. It is used to show a complex eventing scenario and how the event debugger portlet " +
         "can be useful to understand how the events were distributed during the event phase.</p>");

      //
      writer.println("<p><form action=\"" + actionURL + "\" method=\"post\">");
      writer.println("Number maximum of event phases: <input type=\"text\" name=\"repeat\" value=\"3\"/>");
      writer.println("<input type=\"submit\"/>");
      writer.println("</form></p>");
   }
}
