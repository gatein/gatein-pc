/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.embed.resource;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import java.io.IOException;
import java.io.PrintWriter;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ResourcePortlet extends GenericPortlet
{

   /** . */
   static int count = 0;

   /** . */
   static String foo;

   /** . */
   static String resourceId;

   /** . */
   static String bar;

   @Override
   public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      foo = request.getParameter("foo");
      bar = request.getParameter("bar");
      ResourceURL url = response.createResourceURL();
      url.setParameter("bar", "bar_value");
      url.setResourceID("id_value");
      response.setContentType("text/html");
      PrintWriter writer = response.getWriter();
      writer.print("<a href='" + url + "' id='url'>resource</a>");
      writer.close();
   }

   @Override
   public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
   {
      foo = request.getParameter("foo");
      bar = request.getParameter("bar");
      resourceId = request.getResourceID();
      count++;
      response.setContentType("text/plain");
      response.setCharacterEncoding("utf-8");
      PrintWriter writer = response.getWriter();
      writer.print("SERVE_RESOURCE");
   }
}
