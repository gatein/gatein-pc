/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.pc.samples.remotecontroller;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.WindowState;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9979 $
 */
public class RemoteControllerPortlet extends GenericPortlet
{
   private static final String ZIPCODE = "zipcode";

   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.setContentType("text/html");
      PrintWriter printWriter = renderResponse.getWriter();
      String namespace = "n_" + renderResponse.getNamespace();
      printWriter.print("<p><a href='#' onclick=\"" + namespace + "_remote=" + namespace + "_openRemote('");
      ResourceURL resource = renderResponse.createResourceURL();
      // set the cache level to PAGE since the resource creates URLs and the spec mandates PAGE cache level in that case
      resource.setCacheability(ResourceURL.PAGE);
      printWriter.print(resource);
      printWriter.print("')\">Open remote control!</a></p>");
   }

   @Override
   protected void doHeaders(RenderRequest renderRequest, RenderResponse renderResponse)
   {
      PrintWriter printWriter = null;
      try
      {
         printWriter = renderResponse.getWriter();
         String namespace = "n_" + renderResponse.getNamespace();
         String remoteWindowName = namespace + "_remote";
         printWriter.print("<script type='text/javascript'>var " + remoteWindowName + "; function " + namespace
            + "_openRemote(url){window.name='" + namespace + "_parent';window.open(url, '" + remoteWindowName
            + "', 'width=400,height=200,scrollable=yes')}" +
            "onload = function() {" +
            "if (typeof " + remoteWindowName + " != 'undefined') {" + remoteWindowName + ".location.reload(true);}" +
            "}</script>");
      }
      catch (IOException e)
      {
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
   }

   @Override
   public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException, IOException
   {
      String id = resourceRequest.getResourceID();
      if (id == null)
      {
         resourceResponse.setContentType("text/html");
         PrintWriter writer = resourceResponse.getWriter();
         String namespace = "n_" + resourceResponse.getNamespace();
         writer.print("<html><head><script type='text/javascript'>function openLinkInParent(url){window.open(url,'"
            + namespace + "_parent');}</script>\n<link rel=\"stylesheet\" href='" + resourceRequest.getContextPath() + "/css/master.css' type=\"text/css\"/></head><body>");
         PortletURL url = resourceResponse.createRenderURL();
         url.setWindowState(WindowState.MINIMIZED);
         writer.print("<div class=\"remote-container\"><ul><li><a href='#' onclick=\"" + createParentURL(url) + "\"><img\n" +
            "                        src=\"" + resourceRequest.getContextPath() + "/images/icon-minimize.gif\" alt=\"\"/> minimize parent portlet</a></li>");
         url.setWindowState(WindowState.MAXIMIZED);
         writer.print("<li><a href='#' onclick=\"" + createParentURL(url) + "\"><img\n" +
            "                        src=\"" + resourceRequest.getContextPath() + "/images/icon-maximize.gif\" alt=\"\"/> maximize parent portlet</a></li>");
         url.setWindowState(WindowState.NORMAL);
         writer.print("<li><a href='#' onclick=\"" + createParentURL(url) + "\"><img\n" +
            "                        src=\"" + resourceRequest.getContextPath() + "/images/icon-normal.gif\" alt=\"\"/> make parent portlet normal</a></li></ul>");
         writer.print("<br/><ul><li>");
         url = resourceResponse.createRenderURL();
         writer.print("Set value of <b>'zipcode'</b> public render parameter to:</li>");
         url.setParameter(ZIPCODE, "80201");
         writer.print("<li><a href='#' onclick=\"" + createParentURL(url) + "\">Denver, CO</a>");
         url.setParameter(ZIPCODE, "94102");
         writer.print("<li><a href='#' onclick=\"" + createParentURL(url) + "\">San Francisco, CA</a>");
         url.setParameter(ZIPCODE, "20001");
         writer.print("<li><a href='#' onclick=\"" + createParentURL(url) + "\">Washington, DC</a>");
         writer.print("</ul></div></body></html>");
      }
      else
      {
         throw new IllegalArgumentException("Don't know how to handle resource: " + id);
      }
   }

   private String createParentURL(PortletURL url)
   {
      return "openLinkInParent('" + url + "')";
   }
}
