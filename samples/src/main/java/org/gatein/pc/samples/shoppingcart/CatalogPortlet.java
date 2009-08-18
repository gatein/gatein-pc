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

package org.gatein.pc.samples.shoppingcart;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9932 $
 */
public class CatalogPortlet extends GenericPortlet
{
   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.setContentType("text/html");
      Writer writer = renderResponse.getWriter();

      writer.append("<table width='95%'><thead><tr align='left'><th width='50%'>Description</th><th width='25%'>Price</th><th>Actions</th></tr></thead><tbody>");

      PortletURL addURL = renderResponse.createActionURL();
      addURL.setParameter("op", "add");

      Collection<CatalogItem> items = Catalog.getAll();
      for (CatalogItem item : items)
      {
         addURL.setParameter("id", item.getId());
         writer.append("<tr><td>").append(item.getDescription()).append("</td><td>$").append("" + item.getPrice())
            .append("</td><td><a href='").append(addURL.toString()).append("' onclick=\"new Effect.Highlight(document.getElementById('cart-table'))\">Add to cart</a></td></tr>");
      }

      writer.append("</tbody></table>");
   }

   @Override
   public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
   {
      String value = actionRequest.getParameter("id");
      actionResponse.setEvent(CartEvent.QNAME, new CartEvent(value));
   }
}
