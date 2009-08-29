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

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9932 $
 */
public class CartPortlet extends GenericPortlet
{
   public static final String ITEMS = "cart_items";

   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.setContentType("text/html");
      Writer writer = renderResponse.getWriter();

      List<CatalogItem> items = getItems(renderRequest.getPortletSession());

      if (!items.isEmpty())
      {
         writer.append("<table width='95%' id=\"cart-table\"><thead><tr align='left'><th width='66%'>Description</th><th align='right'>Price</th></tr></thead><tbody>");
         int total = 0;
         for (CatalogItem item : items)
         {
            int price = item.getPrice();
            total += price;
            writer.append("<tr><td>").append(item.getDescription()).append("</td><td align='right'>$").append("" + price)
               .append("</td></tr>");
         }
         writer.append("<tr><td><b>Total:</b></td><td align='right' style='border-top: 2px solid #000;'>")
            .append("$" + total).append("</td></tr></tbody></table>");
      }
      else
      {
         writer.append("Cart is empty.");
      }
   }

   @Override
   public void processEvent(EventRequest eventRequest, EventResponse eventResponse) throws PortletException, IOException
   {
      List<CatalogItem> items = getItems(eventRequest.getPortletSession());

      Event event = eventRequest.getEvent();
      if (event.getName().equals("CartEvent"))
      {
         CartEvent cartEvent = (CartEvent)event.getValue();
         items.add(Catalog.get(cartEvent.getId()));
      }

      eventRequest.getPortletSession().setAttribute(ITEMS, items);
   }

   private List<CatalogItem> getItems(PortletSession session)
   {
      List<CatalogItem> items = (List<CatalogItem>)session.getAttribute(ITEMS);
      if (items == null)
      {
         items = new ArrayList<CatalogItem>(7);
      }
      return items;
   }


   
}
