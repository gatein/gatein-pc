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
package org.gatein.pc.portal.jsp;

import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.controller.PortletController;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.Portlet;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.xml.namespace.QName;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortalRenderResponse extends PortalResponse
{

   /** . */
   private PortletPageNavigationalState pageNavigationalState;

   /** . */
   private PagePortletControllerContext portletControllerContext;

   /** . */
   private Map<String, WindowResult> windowResults;

   public PortalRenderResponse(
      HttpServletRequest request,
      HttpServletResponse response,
      PagePortletControllerContext portletControllerContext,
      PortletPageNavigationalState pageNavigationalState,
      PortalPrepareResponse prepareResponse) throws PortletInvokerException
   {
      super(request, response);

      //
      Set<QName> pageParameterNames = prepareResponse.getPageParameterNames();
      if (pageParameterNames.size() > 0)
      {
         if (pageNavigationalState == null)
         {
            pageNavigationalState = portletControllerContext.getStateControllerContext().createPortletPageNavigationalState(true);
         }
         for (QName parameterName : pageParameterNames)
         {
            PageParameterDef parameterDef = prepareResponse.getPageParameterDef(parameterName);
            boolean update = parameterDef.isFrozen() ? true : pageNavigationalState.getPublicNavigationalState(parameterName) == null;
            if (update)
            {
               pageNavigationalState.setPublicNavigationalState(parameterName, new String[]{parameterDef.getValue()});
            }
         }
      }

      //
      this.portletControllerContext = portletControllerContext;
      this.windowResults = new HashMap<String, WindowResult>();
      this.pageNavigationalState = pageNavigationalState;

      //
      render(prepareResponse);
   }

   /**
    * The page state for the rendering.
    *
    * @return the page state
    */
   public PortletPageNavigationalState getPageNavigationalState()
   {
      return pageNavigationalState;
   }

   public Set<String> getWindowIds()
   {
      return windowResults.keySet();
   }

   public WindowResult getWindowResult(String windowId)
   {
      return windowResults.get(windowId);
   }

   public PagePortletControllerContext getPortletControllerContext()
   {
      return portletControllerContext;
   }

   private PortletInvocationResponse render(List<Cookie> cookies, String windowId) throws PortletInvokerException
   {
      return new PortletController().render(portletControllerContext, cookies, pageNavigationalState, windowId);
   }

   private void render(PortalPrepareResponse prepareResponse)
   {
      for (String windowId : prepareResponse.getWindowIds())
      {
         WindowDef windowDef = prepareResponse.getWindowDef(windowId);

         //
         try
         {
            Portlet portlet = portletControllerContext.getPortlet(windowId);

            //
            if (portlet != null)
            {
               PortletInvocationResponse portletResponse = render(null, windowId);

               //
               WindowResult result = new WindowResult(windowDef, portletResponse);

               //
               windowResults.put(windowId, result);
            }
            else
            {
               // log
            }
         }
         catch (PortletInvokerException e)
         {
            e.printStackTrace();
         }
      }
   }

   public boolean isMaximizedWindow(String windowId)
   {
      if (windowId == null)
      {
         throw new IllegalArgumentException();
      }
      return windowId.equals(getMaximizedWindowId());
   }

   public String getMaximizedWindowId()
   {
      if (pageNavigationalState != null)
      {
         for (String windowId : pageNavigationalState.getPortletWindowIds())
         {
            PortletWindowNavigationalState windowNS = pageNavigationalState.getPortletWindowNavigationalState(windowId);
            if (org.gatein.pc.api.WindowState.MAXIMIZED.equals(windowNS.getWindowState()))
            {
               return windowId;
            }
         }
      }

      //
      return null;
   }
}
