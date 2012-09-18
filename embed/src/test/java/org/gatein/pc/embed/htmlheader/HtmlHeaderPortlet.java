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

package org.gatein.pc.embed.htmlheader;

import org.w3c.dom.Element;

import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class HtmlHeaderPortlet extends GenericPortlet
{

   @Override
   public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
   {
      Element title = response.createElement("title");
      title.setTextContent("_title_");

      //
      Element script = response.createElement("script");
      script.setAttribute("type", "_type_");
      script.setAttribute("src", "_src_");

      //
      Element link = response.createElement("link");
      link.setAttribute("charset", "_charset_");
      link.setAttribute("href", "_href_");
      link.setAttribute("media", "_media_");
      link.setAttribute("rel", "_rel_");
      link.setAttribute("type", "_type_");

      //
      Element meta = response.createElement("meta");
      meta.setAttribute("http-equiv", "_http-equiv_");
      meta.setAttribute("name", "_name_");
      meta.setAttribute("content", "_content_");

      //
      Element style = response.createElement("style");
      style.setAttribute("type", "_type_");
      style.setAttribute("media", "_media_");
      style.setTextContent("_style_");

      //
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, title);
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, script);
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, link);
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, meta);
      response.addProperty(MimeResponse.MARKUP_HEAD_ELEMENT, style);
   }
}
