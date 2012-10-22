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

package org.gatein.pc.embed.xmlescaping;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.PrintWriter;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class XmlEscapingPortlet extends GenericPortlet
{

   /** . */
   static int count;

   @Override
   protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      PortletURL url = response.createActionURL();
      url.setParameter("foo", "foo_value");
      url.setParameter("bar", "bar_value");
      response.setContentType("text/html");
      PrintWriter writer = response.getWriter();
      writer.print("FOO");
      url.write(writer);
      writer.print("FOO");
      writer.print("FOO");
      url.write(writer, false);
      writer.print("FOO");
      writer.print("FOO");
      url.write(writer, true);
      writer.print("FOO");
      writer.close();
      count++;
   }
}
