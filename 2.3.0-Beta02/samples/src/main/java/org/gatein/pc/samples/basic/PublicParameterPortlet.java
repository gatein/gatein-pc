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
package org.gatein.pc.samples.basic;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.PortletException;
import javax.portlet.ProcessAction;
import javax.portlet.RenderRequest;
import java.util.Enumeration;
import java.util.Map;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PublicParameterPortlet extends GenericPortlet
{

   @ProcessAction(name="update")
   public void update(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      Map<String, String[]> publicParameters = req.getPublicParameterMap();

      //
      for (Map.Entry<String, String[]> actionParameter : req.getPrivateParameterMap().entrySet())
      {
         String parameterName = actionParameter.getKey();
         String parameterValue = actionParameter.getValue()[0];
         String[] publicValues = publicParameters.get(parameterName);

         //
         if (parameterValue.length() > 0)
         {
            if (publicValues == null || !publicValues[0].equals(parameterValue))
            {
               resp.setRenderParameter(parameterName, parameterValue);
            }
         }
         else if (publicValues != null)
         {
            resp.removePublicRenderParameter(parameterName);
         }
      }
   }

   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletURL actionURL = resp.createActionURL();
      actionURL.setParameter(ActionRequest.ACTION_NAME, "update");

      //
      PrintWriter writer = resp.getWriter();
      writer.print("<form action=\"" + actionURL + "\" method=\"POST\"><table>");

      //
      for (Enumeration<String> e = getPortletConfig().getPublicRenderParameterNames();e.hasMoreElements();)
      {
         String parameterName = e.nextElement();

         //
         String parameterValue = req.getParameter(parameterName);

         //
         writer.print("<tr><td>" + parameterName + "</td><td>");

         if (parameterValue != null)
         {
            writer.print("<input type=\"text\" name=\"" + parameterName + "\" width=\"36\" value=\"" + parameterValue + "\"/>");

            //
            PortletURL renderURL = resp.createRenderURL();
            renderURL.removePublicRenderParameter(parameterName);
            writer.println("<a href=\"" + renderURL + "\">Remove</>");
         }
         else
         {
            writer.print("<input type=\"text\" name=\"" + parameterName + "\" width=\"36\" value=\"\"/>");
         }
         writer.println();

         writer.println("</td>");

      }

      writer.println("<input type=\"submit\" value=\"Update\"/>");

      writer.print("</table></form>");

      //
      writer.close();
   }
}
