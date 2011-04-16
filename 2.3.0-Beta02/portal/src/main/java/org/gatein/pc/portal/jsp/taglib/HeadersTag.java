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
package org.gatein.pc.portal.jsp.taglib;

import org.gatein.pc.portal.jsp.PortalRenderResponse;
import org.gatein.pc.portal.jsp.WindowResult;
import org.gatein.pc.portal.jsp.PortalPrepareResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.xml.XMLTools;
import org.w3c.dom.Element;

import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class HeadersTag extends PortalSimpleTagSupport
{

   public HeadersTag()
   {
      super(false);
   }

   public void doTag(PortalPrepareResponse prepareResponse) throws JspException, IOException
   {
   }

   public void doTag(PortalRenderResponse renderResponse) throws JspException, IOException
   {
      String maxmizedWindowId = renderResponse.getMaximizedWindowId();

      //
      for (String windowId : renderResponse.getWindowIds())
      {
         if (maxmizedWindowId == null || maxmizedWindowId.equals(windowId))
         {
            WindowResult result = renderResponse.getWindowResult(windowId);

            //
            if (result != null)
            {
               PortletInvocationResponse response = result.getResponse();

               //
               if (response instanceof ContentResponse)
               {
                  ContentResponse fragmentResponse = (ContentResponse)response;

                  //
                  ResponseProperties properties = fragmentResponse.getProperties();

                  //
                  if (properties != null)
                  {
                     MultiValuedPropertyMap<Element> markupHeaders = properties.getMarkupHeaders();
                     for (String key : markupHeaders.keySet())
                     {
                        List<Element> headElement = markupHeaders.getValues(key);
                        for (Element headerValue : headElement)
                        {
                           try
                           {
                              String serializedElement = XMLTools.toString(headerValue);
                              getJspContext().getOut().print(serializedElement);
                           }
                           catch (Exception e)
                           {
                              e.printStackTrace();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
