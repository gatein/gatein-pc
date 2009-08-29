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

import org.gatein.pc.controller.impl.PortletURLRenderer;
import org.gatein.pc.controller.impl.URLParameterConstants;
import org.gatein.pc.controller.impl.ControllerRequestFactory;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.portal.Constants;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.invocation.response.UnavailableResponse;
import org.gatein.pc.controller.PortletController;
import org.gatein.pc.controller.request.PortletActionRequest;
import org.gatein.pc.controller.impl.ControllerRequestParameterNames;
import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.response.PageUpdateResponse;
import org.gatein.pc.controller.response.ResourceResponse;
import org.gatein.pc.controller.response.PortletResponse;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletPageNavigationalStateSerialization;
import org.gatein.common.io.IOTools;
import org.gatein.common.io.SerializationFilter;
import org.gatein.common.mc.bootstrap.WebBootstrap;
import org.gatein.common.util.Base64;
import org.gatein.wci.util.RequestDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ControllerFilter implements Filter
{

   /** . */
   private FilterConfig config;

   /** . */
   private boolean redirectAfterAction = false;

   private ServletContext getServletContext()
   {
      return config.getServletContext();
   }

   public void init(FilterConfig config) throws ServletException
   {
      this.config = config;
   }

   public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
   {
      try
      {
         doFilter((HttpServletRequest)req, (HttpServletResponse)resp, chain);
      }
      catch (PortletInvokerException e)
      {
         throw new ServletException(e);
      }
   }

   public void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException, PortletInvokerException
   {
      PortletInvoker invoker = (PortletInvoker)getServletContext().getAttribute(WebBootstrap.BEAN_PREFIX + "ConsumerPortletInvoker");

      //
      PortalPrepareResponse prepareResponse = new PortalPrepareResponse(req, resp);

      // Discover existing portlets on the page (introspection phase)
      chain.doFilter(req, prepareResponse);

      // Now we can create a populated context
      PagePortletControllerContext context = new PagePortletControllerContext(req, resp, invoker, getServletContext(), prepareResponse);

      // The type of invocation
      String type = req.getParameter(URLParameterConstants.TYPE);

      // Process only portlet type
      // The request decoded if not null
      PortletPageNavigationalState pageNavigationalState = null;
      if (URLParameterConstants.PORTLET_TYPE.equals(type))
      {
         ControllerRequestFactory factory = new ControllerRequestFactory(context.getPageNavigationalStateSerialization());

         RequestDecoder decoder = new RequestDecoder(req);

         ControllerRequest request = factory.decode(decoder.getQueryParameters(), decoder.getBody());

         ControllerResponse controllerResponse;
         try
         {
            controllerResponse = new PortletController().process(context, request);
         }
         catch (PortletInvokerException e)
         {
            throw new ServletException(e);
         }

         //
         if (controllerResponse instanceof PageUpdateResponse)
         {
            PageUpdateResponse pageUpdate = (PageUpdateResponse)controllerResponse;

            //
            pageNavigationalState = pageUpdate.getPageNavigationalState();

            //
            req.setAttribute("bilto", context);

            // We perform a send redirect on actions
            if (request instanceof PortletActionRequest && redirectAfterAction)
            {
               PortletURLRenderer renderer = new PortletURLRenderer(
                  pageUpdate.getPageNavigationalState(),
                  context.getClientRequest(),
                  context.getClientResponse(),
                  context.getPageNavigationalStateSerialization());

               //
               String url = renderer.renderURL(new URLFormat(null, null, true, null));
               resp.sendRedirect(url);
               return;
            }
         }
         else if (controllerResponse instanceof ResourceResponse)
         {
            ResourceResponse resourceResponse = (ResourceResponse)controllerResponse;
            PortletInvocationResponse pir = resourceResponse.getResponse();

            //
            if (pir instanceof ContentResponse)
            {
               ContentResponse contentResponse = (ContentResponse)pir;

               //
               if (contentResponse.getType() == ContentResponse.TYPE_EMPTY)
               {
                  resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
               }
               else
               {
                  String contentType = contentResponse.getContentType();
                  if (contentType != null)
                  {
                     resp.setContentType(contentType);
                  }

                  //
                  if (contentResponse.getType() == ContentResponse.TYPE_BYTES)
                  {
                     ServletOutputStream out = null;
                     try
                     {
                        out = resp.getOutputStream();
                        out.write(contentResponse.getBytes());
                     }
                     finally
                     {
                        IOTools.safeClose(out);
                     }
                  }
                  else
                  {
                     Writer writer = null;
                     try
                     {
                        writer = resp.getWriter();
                        writer.write(contentResponse.getChars());
                     }
                     finally
                     {
                        writer.close();
                     }
                  }
               }
            }
            else
            {
               // todo
            }

            //
            return;
         }
         else if (controllerResponse instanceof PortletResponse)
         {
            PortletResponse portletResponse = (PortletResponse)controllerResponse;
            PortletInvocationResponse pir = portletResponse.getResponse();

            //
            if (pir instanceof ErrorResponse)
            {
               ErrorResponse errorResponse = (ErrorResponse)pir;

               //
               if (errorResponse.getCause() != null)
               {
                  throw new ServletException(Constants.PORTLET_ERROR, errorResponse.getCause());
               }
               else
               {
                  throw new ServletException(Constants.PORTLET_ERROR);
               }
            }
            else if (pir instanceof UnavailableResponse)
            {
               throw new ServletException(Constants.UNAVAILABLE);
            }
            else
            {
               // todo
            }
         }
         else
         {
            // todo
         }
      }
      else
      {
         PortletPageNavigationalStateSerialization serialization = new PortletPageNavigationalStateSerialization(context.getStateControllerContext());
         // The nav state provided with the request
         // Unmarshall portal navigational state if it is provided
         pageNavigationalState = null;
         String blah = req.getParameter(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE);
         if (blah != null)
         {
            byte[] bytes = Base64.decode(blah, true);
            pageNavigationalState = IOTools.unserialize(serialization, SerializationFilter.COMPRESSOR, bytes);
         }
      }

      //
      PortalRenderResponse renderResponse = new PortalRenderResponse(req, resp, context, pageNavigationalState, prepareResponse);

      //
      chain.doFilter(req, renderResponse);
   }

   public void destroy()
   {
      this.config = null;
   }
}
