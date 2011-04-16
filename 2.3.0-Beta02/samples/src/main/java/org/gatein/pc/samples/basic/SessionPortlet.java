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
import javax.portlet.PortletURL;
import javax.portlet.PortletSession;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class SessionPortlet extends GenericPortlet
{
   
   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      String name = req.getParameter("name");
      String value = req.getParameter("value");
      boolean portletScope = req.getParameter("portlet") != null;
      if (name != null && value != null)
      {
         req.getPortletSession().setAttribute(name, value, portletScope ? PortletSession.PORTLET_SCOPE : PortletSession.APPLICATION_SCOPE);
      }
      if (req.getParameter("invalidate") != null)
      {
         req.getPortletSession().invalidate();
      }
   }

   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletSession session = req.getPortletSession(false);
      PortletURL purl = resp.createActionURL();

      //
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //
      if (session == null)
      {
         writer.println("No session");
      }
      else
      {
         writer.println("Session id = " + session.getId());
         writer.println("<br/>");

         //
         print(session, PortletSession.PORTLET_SCOPE, writer);
         writer.println("<br/>");

         //
         print(session, PortletSession.APPLICATION_SCOPE, writer);
         writer.println("<br/>");

      }

      //
      writer.println("<form action=\"" + purl.toString() + "\" method=\"post\">");
      writer.println("<input type=\"text\" name=\"name\"/>");
      writer.println("<input type=\"text\" name=\"value\"/>");
      writer.println("<input type=\"submit\" name=\"portlet\" value=\"Add to portlet scope\"/>");
      writer.println("<input type=\"submit\" name=\"application\" value=\"Add to application scope\"/>");
      writer.println("</form><br/>");

      //
      purl.setParameter("invalidate", "true");
      writer.println("<a href=\"" + purl.toString() + "\">invalidate</a><br/>");
   }

   private void print(PortletSession session, int scope, PrintWriter writer)
   {
      String scopeName = PortletSession.PORTLET_SCOPE == scope ? "portlet" : "application";
      writer.println("<p style='border-bottom: 2px dashed #999;'>Session attributes for " + scopeName + " scope:</p>");
      Enumeration e = session.getAttributeNames(scope);
      if (e.hasMoreElements())
      {
         writer.println("<table style='border: 1px solid #333;'>");
         writer.println("<tr class='portlet-table-header'><th>name</th><th>value</th>");
         for (; e.hasMoreElements();)
         {
            String name = (String)e.nextElement();
            Object value = session.getAttribute(name, scope);
            writer.println("<tr><td>" + name + "</td><td>" + value + "</td>");
         }
         writer.println("</table>");
      }
      else
      {
         writer.println("<p>No attributes in " + scopeName + " scope!</p>");
      }
   }
}
