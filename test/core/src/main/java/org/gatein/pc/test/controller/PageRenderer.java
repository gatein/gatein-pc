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

package org.gatein.pc.test.controller;

import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.xml.XMLTools;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9748 $
 */
public class PageRenderer extends AbstractMarkupRenderer
{

   protected FragmentRenderer fragmentRenderer = new SimpleFragmentRenderer();
   protected List<ContentResponse> fragments;
   protected List<ErrorResponse> errors;
   protected PortletPageNavigationalState pageNavigationalState;

   public PageRenderer(ResponseProperties properties, PortletPageNavigationalState pageNavigationalState)
   {
      super(properties);

      //
      this.pageNavigationalState = pageNavigationalState;
   }

   public void setFragmentRenderer(FragmentRenderer fragmentRenderer)
   {
      this.fragmentRenderer = fragmentRenderer;
   }

   protected void renderContent(HttpServletResponse resp) throws IOException
   {
      //
      resp.setContentType("text/html");
      PrintWriter writer = resp.getWriter();
      writer.print("<html>");

      // Render the head contributions
      writer.print("<head>");
      MultiValuedPropertyMap<Element> markupHeaders = properties.getMarkupHeaders();
      List<Element> headElement = markupHeaders.getValues("javax.portlet.markup.head.element");
      if (headElement != null)
      {
         for (Element headerValue : headElement)
         {
            try
            {
               String serializedElement = XMLTools.toString(headerValue);
               writer.print(serializedElement);
            }
            catch (Exception e)
            {
               // todo
               e.printStackTrace();
            }
         }
      }
      writer.print("</head>");


      writer.print("<body>");
      for (ContentResponse fragment : fragments)
      {
         writer.print(fragmentRenderer.renderPortlet(fragment));

      }

      for (ErrorResponse error : errors)
      {
         writer.print(fragmentRenderer.renderError(error));
      }

      writer.print("</body></html>");
   }

   protected void prepareRendering(RendererContext context)
   {

      // What we collect during the different renders
      // we don't reuse the render properties argument since we want to avoid that
      // a portlet rendition affects another rendition of a portlet on the same page
      ResponseProperties renderProperties = new ResponseProperties();

      //
      Collection<Portlet> portlets = context.getPortlets();

      int capacity = portlets.size();
      fragments = new ArrayList<ContentResponse>(capacity);
      errors = new ArrayList<ErrorResponse>(capacity);

      for (Portlet portlet : portlets)
      {
         try
         {
            PortletInvocationResponse response = context.render(properties.getCookies(), pageNavigationalState, portlet.getContext().getId());

            //
            if (response instanceof ContentResponse)
            {
               ContentResponse fragment = (ContentResponse)response;

               //
               fragments.add(fragment);

               //
               ResponseProperties fragmentProperties = fragment.getProperties();
               if (fragmentProperties != null)
               {
                  renderProperties.append(fragmentProperties);
               }
            }
            else if (response instanceof ErrorResponse)
            {
               ErrorResponse error = (ErrorResponse)response;
               errors.add(error);
            }
         }
         catch (PortletInvokerException e)
         {
            e.printStackTrace();
         }
      }

      // Now we combine the render properties with the page properties
      properties.append(renderProperties);
   }
}
