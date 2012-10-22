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

package org.gatein.pc.embed.renderformparameter;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import java.io.IOException;
import java.io.PrintWriter;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class RenderFormParameterPortlet extends GenericPortlet
{

   /** . */
   static String[] foo;

   /** . */
   static PortletMode mode;

   /** . */
   static WindowState windowState;

   @Override
   protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      foo = request.getParameterValues("foo");
      mode = request.getPortletMode();
      windowState = request.getWindowState();
      PortletURL url = response.createRenderURL();
      url.setPortletMode(PortletMode.EDIT);
      url.setWindowState(WindowState.MAXIMIZED);
      response.setContentType("text/html");
      PrintWriter writer = response.getWriter();
      writer.print(
         "<form action='" + url + "' method='post'>" +
         "<input type='text' name='foo' value='foo_value'/>" +
         "<input type='submit' id='submit'/>" +
         "</form>");
      writer.close();
   }
}
