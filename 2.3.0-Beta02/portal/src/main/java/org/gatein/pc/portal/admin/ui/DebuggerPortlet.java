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
package org.gatein.pc.portal.admin.ui;

import org.gatein.pc.portal.jsp.PageEventControllerContext;
import org.gatein.pc.portal.jsp.EventRoute;
import org.gatein.pc.portal.jsp.PagePortletControllerContext;
import org.gatein.pc.portal.jsp.EventAcknowledgement;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.ParametersStateString;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Map;
import java.util.Iterator;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class DebuggerPortlet extends GenericPortlet
{

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PagePortletControllerContext context = (PagePortletControllerContext)req.getAttribute("bilto");

      PageEventControllerContext eventCC = null;
      if (context != null)
      {
         eventCC = context.getEventControllerContext();
      }

      //
      PrintWriter writer = resp.getWriter();

      //
      if (eventCC != null)
      {
         writer.print("" +
            "<a href=\"javascript:showAllDetails()\" >Show All Details</a><br/>" +
            "<div class=\"debug-container\">");
         int id;
         for (EventRoute route : eventCC.getRoots())
         {
            id = rand(1,1000);
            writer.println("<div class=\"event\" id=\"event-" + id + "\">\n");
            printRoute(route, writer, context, id, "");
            writer.println("</div>\n");
         }
         writer.println("</div>\n");
      }
      else
      {
         writer.println("No event phase happened in the last request");
      }
   }

   private void printRoute(EventRoute route, PrintWriter writer,PagePortletControllerContext context, int id, String cid)
   {
      writer.print("<div onmouseover=\"showDetails(this,'details-" + id + cid + "')\" onmouseout=\"hideDetails(this,'details-" + id + cid + "')\" onclick=\"new Effect.Highlight('" + context.getPortletInfo(route.getSource()).getName() + "', {startcolor:'#990000', endcolor:'ffffdd',queue: {position:'start', scope: 'highlight', limit:2}});new Effect.Highlight('" + context.getPortletInfo(route.getDestination()).getName() + "', {startcolor:'#990000', endcolor:'ffffdd',queue: {position:'end', scope: 'highlight', limit:2}});\" >" +
         "<div class=\"event-header\">\n" +
            "         <h4>" + context.getPortletInfo(route.getSource()).getName() + "</h4>\n" +
            "         <div>Event: " + route.getName().getLocalPart() + "</div>\n" +
            "       </div>\n");

      writer.print("<div class=\"event-body\">" +
         "<p><label>Source: </label>" + context.getPortletInfo(route.getSource()).getName() + "<img src=\"/simple-portal/images/debug/arrow.gif\" alt=\"\"/>");
      writer.print("<label>Destination: </label>" + context.getPortletInfo(route.getDestination()).getName() + "</p>\n" +
         "  <div class=\"details\" id=\"details-" + id + cid + "\">\n" +
         "      <div class=\"details-frame\">\n" +
         "         " + formatAck(route.getAcknowledgement()) + "\n" +
         "         <div class=\"dotted-white\"></div>\n" +
         "         <label>Payload: " + route.getPayload() + "</label>\n" +
         "      </div>\n" +
         "   </div></div>" +
         "");
      writer.print("</div>");

      for (EventRoute child : route.getChildren())
      {
         id = rand(1,1000);
         writer.println("<div class=\"children\">");
         printRoute(child, writer, context, id, "a");
         writer.println("</div>");
      }

   }

   private String formatAck(EventAcknowledgement ack)
   {
      if (ack instanceof EventAcknowledgement.Consumed)
      {
         EventAcknowledgement.Consumed consumedAck = (EventAcknowledgement.Consumed)ack;
         PortletInvocationResponse response = consumedAck.getResponse();
         if (response instanceof UpdateNavigationalStateResponse)
         {
            UpdateNavigationalStateResponse updateResponse = (UpdateNavigationalStateResponse)response;

            //
            String[] strings = new String[4];

            //
            if (updateResponse.getMode() != null)
            {
               strings[0] = "mode=" + updateResponse.getMode();
            }

            //
            if (updateResponse.getWindowState() != null)
            {
               strings[1] = "windowstate=" + updateResponse.getWindowState();
            }

            // Should be ok, we are not consuming remote portlets
            ParametersStateString newNS = (ParametersStateString)updateResponse.getNavigationalState();
            if (newNS != null)
            {
               StringBuilder sb = new StringBuilder();
               sb.append("private=");
               formatMap(newNS.getParameters(), sb);
               strings[2] = sb.toString();
            }

            //
            Map<String, String[]> publicChanges = updateResponse.getPublicNavigationalStateUpdates();
            if (publicChanges != null)
            {
               StringBuilder sb = new StringBuilder();
               sb.append("public=");
               formatMap(publicChanges, sb);
               strings[3] = sb.toString();
            }

            //
            StringBuilder sb = new StringBuilder("[");
            formatList(strings, sb);
            sb.append("]");

            //
            return sb.toString();
         }
         else if (response instanceof ErrorResponse)
         {
            ErrorResponse errorResponse = (ErrorResponse)response;

            //
            return "Error:" + errorResponse.getMessage();
         }
         else
         {
            return "Todo format:" + response;
         }
      }
      else
      {
         return ack.toString();
      }
   }

   private void formatList(String[] strings, StringBuilder sb)
   {
      boolean done = false;
      for (String string : strings)
      {
         if (string != null)
         {
            sb.append(done ? "," : "").append(string);

            //
            done = true;
         }
      }
   }

   private void formatMap(Map<String, String[]> map, StringBuilder sb)
   {
      sb.append("{");
      for (Iterator<Map.Entry<String, String[]>> i = map.entrySet().iterator();i.hasNext();)
      {
         Map.Entry<String, String[]> entry = i.next();

         //
         sb.append(entry.getKey());

         //
         sb.append("=(");
         String[] value = entry.getValue();
         formatList(entry.getValue(), sb);
         sb.append(')');

         //
         if (i.hasNext())
         {
            sb.append(',');
         }
      }
      sb.append("}");
   }

   public static int rand(int lo, int hi)
     {
         Random rn2 = new Random();
             int n = hi - lo + 1;
             int i = rn2.nextInt() % n;
             if (i < 0)
                     i = -i;
             return lo + i;
     }

}
