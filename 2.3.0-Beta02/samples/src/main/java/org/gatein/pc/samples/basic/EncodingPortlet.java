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
import javax.portlet.RenderRequest;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EncodingPortlet extends GenericPortlet
{

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      resp.setRenderParameter("text", req.getParameter("text"));
      req.getPortletSession().setAttribute("text", req.getParameter("text"));

   }

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      String param = req.getParameter("text");
      String attr = (String)req.getPortletSession().getAttribute("text");

      //
      writer.print(
         "<div> Text to pass : " +
            "<form action=\"" + resp.createRenderURL() + "\" method=\"post\"\">" +
            "<input type=\"text\" name=\"text\" value=\"\"/>" +
            "<input type=\"submit\" value=\"Submit to render phase\"/>" +
            "</form>" +
            "<form action=\"" + resp.createActionURL() + "\" method=\"post\"\">" +
            "<input type=\"text\" name=\"text\" value=\"\"/>" +
            "<input type=\"submit\" value=\"Submit to action phase\"/>" +
            "</form>" +
            "</div>");

      //
      writer.println(
         "<div>" + "Text retrieved from request parameter: " +
            "</div>" +
            "<div>" +
            "<textarea name=\"text\" cols=\"20\" rows=\"4\" wrap=\"virtual\">");

      if (param != null)
      {
         writer.println(param);
      }

      writer.println(
         "</textarea>" +
            "</div>");

      writer.println(
         "<div>" + "Text retrieved from portlet session (value set during Action Phase): " +
            "</div>" +
            "<div>" +
            "<textarea name=\"text\" cols=\"20\" rows=\"4\" wrap=\"virtual\">");

      if (attr != null)
      {
         writer.println(attr);
      }

      writer.println(
         "</textarea>" +
            "</div>");

      //
      writer.close();
   }
}
