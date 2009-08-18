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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class SessionCounterPortlet extends GenericPortlet
{
   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
   }

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //
      PortletSession session = req.getPortletSession();
      int count = 0;
      if (session.getAttribute("count") != null)
      {
         count = (Integer)session.getAttribute("count") + 1;
      }
      session.setAttribute("count", count);
      writer.write("<p>");
      writer.write("<div class=\"portlet-section-header\">Render call count</div>");
      writer.write("<div class=\"portlet-section-body\">");
      writer.write("<div>" + count + "</div>");
      writer.write("</div>");
      writer.write("</p>");

      writer.write("<div class=\"portlet-section-header\">Render call count</div>");
      writer.write("<div><a href=\"" + resp.createActionURL() + "\">action</a></div");
      writer.write("<div><a href=\"" + resp.createRenderURL() + "\">render</a></div");

      //
      writer.close();
   }
}
