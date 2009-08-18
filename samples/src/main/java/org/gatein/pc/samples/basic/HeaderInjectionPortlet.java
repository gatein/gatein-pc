/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
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

package org.gatein.pc.samples.basic;

import org.w3c.dom.Element;

import javax.portlet.GenericPortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import java.io.IOException;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class HeaderInjectionPortlet extends GenericPortlet
{

   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.getWriter().print("Look at browser title!");
   }

   @Override
   protected void doHeaders(RenderRequest renderRequest, RenderResponse renderResponse)
   {
      Element element = renderResponse.createElement("title");
      element.setTextContent("My new web browser title");
      renderResponse.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, element);
   }
}
