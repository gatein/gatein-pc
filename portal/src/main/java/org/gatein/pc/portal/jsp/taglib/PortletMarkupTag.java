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
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletMarkupTag extends PortalSimpleTagSupport
{
   public void doTag(PortalRenderResponse renderResponse) throws JspException, IOException
   {
      PortletTag portletTag = (PortletTag)SimpleTagSupport.findAncestorWithClass(this, PortletTag.class);

      PortletInvocationResponse portletResp = portletTag.result.getResponse();

      JspWriter out = getJspContext().getOut();

      if (portletResp != null)
      {
         if (portletResp instanceof ContentResponse)
         {
            ContentResponse fragment = (ContentResponse)portletResp;
            PortletWindowNavigationalState windowNS = null;
            if (renderResponse.getPageNavigationalState() != null)
            {
               windowNS = renderResponse.getPageNavigationalState().getPortletWindowNavigationalState(portletTag.result.getWindowDef().getWindowId());
            }
            if (windowNS == null || !windowNS.getWindowState().equals(org.gatein.pc.api.WindowState.MINIMIZED))
            {
               if (fragment.getType() != ContentResponse.TYPE_EMPTY)
               {
                  String frag;
                  if (fragment.getType() == ContentResponse.TYPE_BYTES)
                  {
                     frag = fragment.getBytes().toString();
                  }
                  else
                  {
                     frag = fragment.getChars();
                  }

                  out.write(frag);
               }
            }
         }
         else
         {
            out.write(portletResp.getClass().getSimpleName() + "[" + portletTag.result.getWindowDef().getPortletName() + "," + portletTag.result.getWindowDef().getApplicationName() + "]");
            out.flush();
         }
      }
      else
      {
         out.write("Empty[" + portletTag.result.getWindowDef().getPortletName() + "," + portletTag.result.getWindowDef().getApplicationName() + "]");
         out.flush();
      }
   }
}
