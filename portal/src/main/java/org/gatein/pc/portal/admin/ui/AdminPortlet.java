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
package org.gatein.pc.portal.admin.ui;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.container.managed.ManagedPortletApplication;
import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.portlet.container.managed.ManagedPortletFilter;
import org.gatein.pc.portlet.container.managed.PortletApplicationRegistry;
import org.gatein.pc.api.info.MetaInfo;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class AdminPortlet extends GenericPortlet
{

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {

      String lifeCycle = req.getParameter("lifecycle");

      //
      String applicationId = req.getParameter("application");

      //
      if (applicationId != null)
      {
         // Use an alias
         PortletApplicationRegistry registry = (PortletApplicationRegistry)getPortletContext().getAttribute("PortletApplicationDeployer");

         //
         ManagedPortletApplication application = registry.getManagedPortletApplication(applicationId);
         ManagedObject managedObject = application;

         //
         String containerId = req.getParameter("container");
         if (containerId != null)
         {
            managedObject = application.getManagedPortletContainer(containerId);
         }
         else
         {
            String filterId = req.getParameter("filter");
            if (filterId != null)
            {
               managedObject = application.getManagedPortletFilter(filterId);
            }
         }

         //
         final ManagedObject tmp = managedObject;
         if (tmp != null)
         {
            if ("start".equals(lifeCycle))
            {
               tmp.managedStart();
            }
            else if ("stop".equals(lifeCycle))
            {
               // Async stop otherwise it is not possible to stop the admin portlet (self deadlock)
               new Thread()
               {
                  public void run()
                  {
                     tmp.managedStop();
                  }
               }.start();
            }
         }
      }
   }

   protected void doView(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();

      //PortletRequestDispatcher rd = getPortletContext().getRequestDispatcher("admin/admin.jsp");
      //rd.include(request, response);

      // Use an alias
      PortletApplicationRegistry registry = (PortletApplicationRegistry)getPortletContext().getAttribute("PortletApplicationDeployer");

      //
      writer.print("" +
         "" +
         "" +
         "<div class=\"admin-container full-width\">\n" +
         "                  <div class=\"admin-header\">\n" +
         "                     <div class=\"expand\">\n" +
         "                        <img src=\"/simple-portal/images/admin/expand.gif\" alt=\"\"/><a href=\"javascript:expandAll();\">Expand All</a>\n" +
         "                     </div>\n" +
         "                     <div class=\"collapse\">\n" +
         "                        <img src=\"/simple-portal/images/admin/contract.gif\" alt=\"\"/><a href=\"javascript:collapseAll();\">Collapse All</a>\n" +
         "                     </div>\n" +
         "                  </div>\n");


      for (ManagedPortletApplication application : registry.getManagedPortletApplications())
      {
         PortletURL url = resp.createActionURL();
         url.setParameter("application", application.getId());

         //
         url.setParameter("lifecycle", "stop");
         String stopURL = url.toString();

         //
         url.setParameter("lifecycle", "start");
         String startURL = url.toString();

         String htmlId = application.getId().substring(1, application.getId().length());


         writer.print("" +

            "\n" +
            "                  <div class=\"par-container\">\n" +
            "                     <div class=\"par-header\">\n" +
            "                        <div class=\"par-header-left\">\n" +
            "                           <a id=\"contract-" + htmlId + "\" href=\"javascript:return false;\" onclick=\"collapse(this,'" + htmlId + "');\">\n" +
            "                           <img src=\"/simple-portal/images/admin/contract.gif\" alt=\"\"  />\n" +
            "                           <h3 class=\"par\">" + application.getId() + "</h3>\n" +
            "                           </a>\n" +
            "                           <a id=\"expand-" + htmlId + "\" href=\"#\" onclick=\"expand(this,'" + htmlId + "');return false;\">\n" +
            "                           <img src=\"/simple-portal/images/admin/expand.gif\" alt=\"\" />\n" +
            "                           <h3 class=\"par\">" + application.getId() + "</h3>\n" +
            "                           </a>\n" +
            "                        </div>\n" +
            "\n" +
            "                        <div class=\"par-header-right\">\n" +
            "                        <span class=\"par-status\">");

         if (application.getStatus().toString().equals("STARTED"))
         {
            writer.print("<img src=\"/simple-portal/images/admin/started-icon.gif\" alt=\"\"/>");
         }
         else
         {
            writer.print("<img src=\"/simple-portal/images/admin/stopped-icon.gif\" alt=\"\"/>");
         }
         writer.print("" +
            "" + application.getStatus() + "</span>\n" +
            "\n" +
            "                        <div class=\"par-control\">\n" +
            "                           <img src=\"/simple-portal/images/admin/start-stop-edge.gif\" alt=\"\"/>\n");
         if (application.getStatus().toString().equals("STARTED"))
         {
            writer.print("<a class=\"stop\" href=\"" + stopURL + "\">Stop</a></td>\n");
         }
         else
         {
            writer.print("<a class=\"start\" href=\"" + startURL + "\">Start</a></td>\n");
         }

         writer.print("" +
            "" +
            "" +
            "                        </div>\n" +
            "                        </div>\n" +
            "                       \n" +
            "                     </div></div>" +
            "" +
            "                    <div class=\"target\" id=\"" + htmlId + "-target\">\n" +
            "                        <table class=\"par-table full-width\" >\n" +
            "                           <tr class=\"par-subhead\">\n" +
            "                              <td class=\"par-header-left\">ID</td>\n" +
            "                              <td>Status</td>\n" +
            "                              <td>Description</td>\n" +
            "                              <td></td>\n" +
            "                           </tr>\n" +
            "\n");

         //
         for (ManagedPortletFilter filter : application.getManagedPortletFilters())
         {
            url.setParameter("filter", filter.getId());

            //
            url.setParameter("lifecycle", "stop");
            stopURL = url.toString();

            //
            url.setParameter("lifecycle", "start");
            startURL = url.toString();

            //
            //writer.print("<li>Filter " + filter.getId() + " " + filter.getStatus() + " <a href=\"" +
            // startURL + "\">Start</a> <a href=\"" + stopURL + "\">Stop</a></li>");

            writer.print("" +
               "" +
               "                          <tr class=\"par-filter-row\">\n" +
               "                              <td class=\"par-row-left\"><img class=\"icon\" src=\"/simple-portal/images/admin/filter-icon.gif\" alt=\"\"/>" + filter.getId() + "</td>\n" +
               "                              <td>" + filter.getStatus() + "</td>\n" +
               "                              <td></td>\n");
            if (filter.getStatus().toString().equals("STARTED"))
            {
               writer.print("                   <td class=\"par-row-right\">Start | <a class=\"stop\" href=\"" + stopURL + "\">Stop</a></td>\n");
            }
            else
            {
               writer.print("                   <td class=\"par-row-right\"><a class=\"start\" href=\"" + startURL + "\">Start</a> | Stop</td>\n");
            }


            writer.print("                 </tr>");


         }

         //
         for (ManagedPortletContainer container : application.getManagedPortletContainers())
         {
            url.setParameter("container", container.getId());

            //
            url.setParameter("lifecycle", "stop");
            stopURL = url.toString();

            //
            url.setParameter("lifecycle", "start");
            startURL = url.toString();


            LocalizedString description = null;
            String descString = "";
            try
            {
               MetaInfo metaInfo = container.getInfo().getMeta();
               description = metaInfo.getMetaValue("description");
            }
            catch (Exception e)
            {
               //e.printStackTrace();
            }
            if (description != null)
            {
               descString = description.getString(req.getLocale(), true);
            }

            writer.print("" +
               "" +
               "                          <tr class=\"par-container-row\">\n" +
               "                              <td class=\"par-row-left\"><img class=\"icon\" src=\"/simple-portal/images/admin/portlet-icon.gif\" alt=\"\"/>" + container.getId() + "</td>\n" +
               "                              <td>" + container.getStatus());
            if (container.getStatus().toString().equals("FAILED"))
            {
               writer.print("<img class=\"failed\" src=\"/simple-portal/images/admin/warning-icon.gif\" alt=\"\"/>");
            }


            writer.print("</td>\n" +
               "                              <td>" + descString + "</td>\n");
            if (container.getStatus().toString().equals("STARTED"))
            {
               writer.print("                   <td class=\"par-row-right\">Start | <a class=\"stop\" href=\"" + stopURL + "\">Stop</a></td>\n");
            }
            else
            {
               writer.print("                   <td class=\"par-row-right\"><a class=\"start\" href=\"" + startURL + "\">Start</a> | Stop</td>\n");
            }


            writer.print("                 </tr>");

         }
         writer.print("</table>" +
            "  </div>\n" +
            "");
      }

      writer.print("</div>" +
         "           </div>\n" +
         "         </div>\n" +
         "      </div>\n" +
         "\n" +
         "   </div>\n" +
         "   <br class=\"clear\"/>\n" +
         "</div>");

      //
      writer.close();

   }

}