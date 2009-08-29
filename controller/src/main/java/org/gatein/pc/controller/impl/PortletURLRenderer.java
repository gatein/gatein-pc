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

package org.gatein.pc.controller.impl;

import org.gatein.common.io.IOTools;
import org.gatein.common.io.SerializationFilter;
import org.gatein.common.io.Serialization;
import org.gatein.common.text.CharBuffer;
import org.gatein.common.text.FastURLEncoder;
import org.gatein.common.util.Base64;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.impl.ControllerRequestParameterNames;
import org.gatein.pc.controller.impl.ControllerRequestParameterMapFactory;
import static org.gatein.pc.controller.impl.URLParameterConstants.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Collections;

/**
 * A class that is able to create portlet URL 
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletURLRenderer
{

   /** . */
   final Serialization<PortletPageNavigationalState> serialization;

   /** . */
   final HttpServletRequest clientReq;

   /** . */
   final HttpServletResponse clientResp;

   /** . */
   final PortletPageNavigationalState pageNS;

   public PortletURLRenderer(
      PortletPageNavigationalState pageNS,
      HttpServletRequest clientReq,
      HttpServletResponse clientResp,
      Serialization<PortletPageNavigationalState> serialization)
   {
      this.pageNS = pageNS;
      this.clientReq = clientReq;
      this.clientResp = clientResp;
      this.serialization = serialization;
   }

   private String renderURL(Map<String, String> parameters, URLFormat format)
   {
      CharBuffer buffer = new CharBuffer();
      buffer.append(clientReq.getScheme());
      buffer.append("://");
      buffer.append(clientReq.getServerName());
      buffer.append(':');
      buffer.append(Integer.toString(clientReq.getServerPort()));
      buffer.append(clientReq.getContextPath());
      buffer.append(clientReq.getServletPath());

      //
      if (clientReq.getPathInfo() != null)
      {
         buffer.append(clientReq.getPathInfo());
      }

      //
      String parameterSeparator = format.getWantEscapeXML() == Boolean.TRUE ? "&amp;" : "&";

      //
      boolean first = true;
      for (Map.Entry<String, String> entry : parameters.entrySet())
      {
         String name = entry.getKey();
         String value = entry.getValue();
         buffer.append(first ? "?" : parameterSeparator);
         buffer.append(name, FastURLEncoder.getUTF8Instance());
         buffer.append('=');
         buffer.append(value, FastURLEncoder.getUTF8Instance());
         first = false;
      }

      //
      String url = buffer.asString();
      return clientResp.encodeURL(url);
   }

   public String renderURL(URLFormat format)
   {
      String pageNavigationalState = null;
      if (pageNS != null)
      {
         byte[] bytes = IOTools.serialize(serialization, SerializationFilter.COMPRESSOR, pageNS);
         pageNavigationalState = Base64.encodeBytes(bytes, true);
      }

      //
      Map<String, String> parameters;
      if (pageNavigationalState != null)
      {
         parameters = Collections.singletonMap(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE, pageNavigationalState);
      }
      else
      {
         parameters = Collections.emptyMap();
      }

      //
      return renderURL(parameters, format);
   }

   public String renderURL(String windowId, ContainerURL containerURL, URLFormat format)
   {
      ControllerRequestParameterMapFactory factory = new ControllerRequestParameterMapFactory(serialization);

      //
      Map<String, String> parameters = factory.encode(pageNS, windowId, containerURL);

      //
      parameters.put(TYPE, PORTLET_TYPE);

      //
      return renderURL(parameters, format);
   }
}
