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
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.ActionResponse;
import javax.portlet.ActionRequest;
import java.util.Enumeration;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PrivateParameterPortlet extends GenericPortlet
{

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      for (Enumeration e = req.getParameterNames(); e.hasMoreElements();)
      {
         String name = (String)e.nextElement();
         String value = req.getParameter(name);
         if (!"name".equals(name) && !"value".equals(name))
         {
            resp.setRenderParameter(name, value);
         }
      }
      String name = req.getParameter("name");
      String value = req.getParameter("value");
      if (name != null && value != null)
      {
         resp.setRenderParameter(name, value);
      }
   }

   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      String color = req.getParameter("color");
      if (color == null)
      {
         color = "white";
      }

      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //
      PortletURL renderURL = resp.createRenderURL();

      //
      renderURL.setParameter("color", "blue");
      String blueURL = renderURL.toString();

      //
      renderURL.setParameter("color", "red");
      String redURL = renderURL.toString();

      //
      renderURL.setParameter("color", "white");
      String whiteURL = renderURL.toString();

      //
      writer.print("<a href=\"" + blueURL + "\">blue</a> -");
      writer.print("<a href=\"" + redURL + "\">red</a> -");
      writer.print("<a href=\"" + whiteURL + "\">white</a><br/>");

      //
      writer.print("Parameters :<br/>");
      writer.print("<table bgcolor=\"" + color + "\">");
      writer.print("<tr><td>Name</td><td>value</td></tr>");
      for (Enumeration e = req.getParameterNames(); e.hasMoreElements();)
      {
         String name = (String)e.nextElement();
         String value = req.getParameter(name);
         writer.print("<tr><td>" + name + "</td><td>" + value + "</td></tr>");
      }
      writer.print("</table>");

      //
      PortletURL actionURL = resp.createActionURL();
      writer.print("<form action=\"" + actionURL.toString() + "\" method=\"post\">");
      writer.print("<input type=\"text\" name=\"name\"/>");
      writer.print("<input type=\"text\" name=\"value\"/>");
      writer.print("<input type=\"submit\" value=\"add\"/>");
      writer.print("</form>");

      //
      writer.print("<a href=\"" + req.getContextPath() + "/test.txt\">test</a>");

      //
      writer.close();
   }
}
