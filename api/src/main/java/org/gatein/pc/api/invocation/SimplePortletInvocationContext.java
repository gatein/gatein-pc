/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.api.invocation;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gatein.common.util.MarkupInfo;
import org.gatein.pc.api.ActionURL;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.RenderURL;
import org.gatein.pc.api.ResourceURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.spi.PortletInvocationContext;

/**
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 * @version $Revision$
 */
public class SimplePortletInvocationContext implements PortletInvocationContext
{

   private MarkupInfo markupInfo;
   private String baseURL;
   private HttpServletResponse response;
   
   public SimplePortletInvocationContext(MarkupInfo markupInfo, String baseURL, HttpServletResponse response)
   {
      this.markupInfo = markupInfo;
      this.baseURL = baseURL;
      this.response = response;
   }
   
   public String encodeResourceURL(String url) throws IllegalArgumentException
   {
	  return response.encodeURL(url);
   }

   public MarkupInfo getMarkupInfo()
   {
      return this.markupInfo;
   }

   public String renderURL(ContainerURL containerURL, URLFormat format)
   {
      String url = baseURL;
      
      String type;
      if (containerURL instanceof RenderURL)
      {
         type = "render";
      }
      else if (containerURL instanceof ResourceURL)
      {
         type = "resource";
      }
      else if (containerURL instanceof ActionURL)
      {
         type = "action";
      }
      else
      {
         throw new Error("Unrecognized containerURL type");
      }
      
      url += "&portal:type=" + type;
      
      //TODO: fix this part
      url += "&portal:isSecure=" + "false";
      
      return url;
   }

   public void renderURL(Writer writer, ContainerURL containerURL, URLFormat format) throws IOException
   {
      String url = renderURL(containerURL, format);
      writer.write(url);
   }

}

