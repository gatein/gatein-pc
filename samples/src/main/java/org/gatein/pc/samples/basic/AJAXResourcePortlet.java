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
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSecurityException;
import javax.portlet.PortletURL;
import javax.portlet.PortletMode;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class AJAXResourcePortlet extends GenericPortlet
{
   public final static int TITANIUM = 0;
   public final static int PLATINUM = 1;
   public final static int GOLD     = 2;
   public final static int SILVER   = 3;
   public final static int TIN      = 4;

   public void processAction(ActionRequest req, ActionResponse resp) throws PortletException, PortletSecurityException, IOException
   {

      String repeatText = req.getParameter("repeat");
      // set zip as render parameter
      if (repeatText != null){
      resp.setRenderParameter("repeat", repeatText);
      }
      // request view
      resp.setPortletMode(PortletMode.VIEW);

   }
   public void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      ResourceURL resourceURL = resp.createResourceURL();
      PortletURL actionURL = resp.createActionURL();
       //
//      Element elt = resp.createElement("script");
//      elt.setAttribute("type", "text/javascript");
//      elt.setAttribute("src", resourceURL.toString());
//      elt.appendChild(elt.getOwnerDocument().createTextNode(""));
//      resp.addProperty("script", elt);

      //

      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();
      writer.print("" +
         "<script type=\"text/javascript\">" +
         "" +
        // "function init(){\n" +
        //"     Event.observe('repeat', 'keyup', repeat(), false);\n" +
        // "}" +
         "" +
            "function browse(id) {" +
            "var url = id;\n" +
            "var pars = 'foo=bar';\n" +
            "var target = 'output-div';\n" +
            "var myAjax = new Ajax.Updater(target, url, {method: 'GET', parameters: pars});" +
         "}" +

         "</script>");

            writer.print("" +
               "<div class='full-width' style='padding:5px'>" +
               "<h4>Partial Refresh Repeater Demo</h4>");
            writer.print("" +
            " <div class='half-width float-left'>" +
               "  <form method='post' id=\"testrepeatform\" name=\"testrepeatform\" action=\"" + actionURL + "\" onsubmit=\"new Ajax.Updater('repeat-div', '" + resourceURL + "', {asynchronous:true, parameters:Form.serialize(this)}); return false;\">\n" +
            "      <font class='portlet-font'>Repeat Demo:</font><br/>\n" +
            "      <input class='portlet-form-input-field' type='text' value='' size='12' name=\"repeat\" id=\"repeat\" onkeyup=\"this.form.submit2.click();new Effect.Highlight(document.getElementById('repeat-div'));\"/>\n" +
           "      <input class='portlet-form-input-field hidden' type='submit' name='submit2' value='submit' style=\"display:hidden;\">\n" +
            "   </form>\n" +
               "</div>" +
            "");
            writer.print("<div id=\"repeat-container\"><div id=\"repeat-div\" class='half-width float-left' style='height:50px'></div></div>");
//         "<input type=\"text\" id=\"hidden-input\" value=\"" + resourceURL.toString() + "\" />" +
//         "<a href='javascript:" + resp.getNamespace() + "_handle()'>Click me to trigger script</a>" +
         writer.print("<br class='clear'/><br class='clear'/><hr/>" +
            "<h4>Partial Refresh Product Catalog</h4>" +
            "<div class='full-width'>" +
                        "");
         writer.print("<div class='float-left third-width'>");
         resourceURL.setParameter("prodId","1");
         writer.print("<a href=\"javascript: browse(\'" + resourceURL + "\');\">Product 1</a><br/>");

         resourceURL.setParameter("prodId","2");
         writer.print("<a href=\"javascript: browse(\'" + resourceURL + "\');\">Product 2</a><br/>");

         resourceURL.setParameter("prodId","3");
         writer.print("<a href=\"javascript: browse(\'" + resourceURL + "\');\">Product 3</a><br/>");

         writer.print("<br class='clear'/></div>");
         writer.print("<div class='float-left two-third-width'>" +
            "<h4 class='zero'>Product Details</h4>" +
            "<div id=\"output-div\"></div>" +
            "");
         writer.print("<br class='clear'/></div>");
         writer.print("<br class='clear'/></div>");
         writer.print("<br class='clear'/></div>");



        // writer.print("</div>");
   }

   public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException
   {
      String repeatText = req.getParameter("repeat");
      String prodId = req.getParameter("prodId");

      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();
      if (repeatText != null){
         writer.print("<div id=\"repeat-text\">"+ req.getPrivateParameterMap().get("repeat")[0] +"</div>");
      }
      if (prodId != null){
         if (prodId.equals("1")){
            writer.print("<div id=\"product-text\">Product ID: "+ prodId +"" +
            "<br/>" +
            " B BY BURTON ALPHA<br/>" +
            "Sale Price: $314.96 " +
            "</div>");
         }
         if (prodId.equals("2")){
            writer.print("<div id=\"product-text\">Product ID: "+ prodId +"" +
            "<br/>" +
               "FORUM DESTROYER LTD<br/>" +
               "$319.99 " +
            "</div>");
         }
         if (prodId.equals("3")){
            writer.print("<div id=\"product-text\">Product ID: "+ prodId +"" +
            "<br/>" +
            "\n" +
               "SANTA CRUZ ALLSTAR<br/>" +
               "$256.00 " +
            "</div>");
         }

      }




      writer.close();
   }
}
