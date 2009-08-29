/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletApplicationContext;
import org.gatein.pc.portlet.impl.jsr168.Dispatch;
import org.gatein.pc.portlet.impl.jsr168.DispatchType;
import org.gatein.pc.portlet.impl.jsr168.DispatchedHttpServletRequest;
import org.gatein.pc.portlet.impl.jsr168.DispatchedHttpServletResponse;

import javax.portlet.ActionRequest;
import javax.portlet.EventRequest;
import javax.portlet.MimeResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.filter.PortletRequestWrapper;
import javax.portlet.filter.PortletResponseWrapper;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6854 $
 */
public class PortletRequestDispatcherImpl
   implements PortletRequestDispatcher
{

   /** The servlet request dispatcher. */
   private final RequestDispatcher dispatcher;

   /** . */
   private final String path;

   PortletRequestDispatcherImpl(RequestDispatcher dispatcher)
   {
      this(dispatcher, null);
   }

   PortletRequestDispatcherImpl(RequestDispatcher dispatcher, String path)
   {
      this.dispatcher = dispatcher;
      this.path = path;
   }

   public void include(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      dispatch(DispatchType.INCLUDE, req, resp);
   }

   public void include(PortletRequest req, PortletResponse resp) throws PortletException, IOException
   {
      dispatch(DispatchType.INCLUDE, req, resp);
   }

   public void forward(PortletRequest req, PortletResponse resp) throws PortletException, IOException, IllegalStateException
   {
      dispatch(DispatchType.FORWARD, req, resp);
   }

   private void dispatch(
      DispatchType type,
      PortletRequest wreq,
      PortletResponse wresp) throws PortletException, IOException
   {
      PortletRequestImpl req = unwrap(wreq);
      PortletResponseImpl resp = unwrap(wresp);

      //
      Dispatch dispatch = new Dispatch(type, path);

      //
      try
      {
         PortletApplication application = req.container.getPortletApplication();
         PortletApplicationContext applicationContext = application.getContext();
         ServletContext servletContext = applicationContext.getServletContext();

         //
         HttpServletRequestWrapper realReq = req.getRealRequest();
         HttpServletResponseWrapper realResp = resp.getRealResponse();

         //
         DispatchedHttpServletRequest direq;
         DispatchedHttpServletResponse diresp;
         if (req instanceof ActionRequest)
         {
            direq = new DispatchedHttpServletRequest.Action(dispatch, (ActionRequest)req, realReq, servletContext);
            diresp = new DispatchedHttpServletResponse.StateAware(direq, (StateAwareResponseImpl)resp, realResp);
         }
         else if (req instanceof EventRequest)
         {
            direq = new DispatchedHttpServletRequest.Event(dispatch, (EventRequest)req, realReq, servletContext);
            diresp = new DispatchedHttpServletResponse.StateAware(direq, (StateAwareResponseImpl)resp, realResp);
         }
         else if (req instanceof RenderRequest)
         {
            direq = new DispatchedHttpServletRequest.Render(dispatch, (RenderRequest)req, realReq, servletContext);
            diresp = new DispatchedHttpServletResponse.Mime(direq, (MimeResponseImpl)resp, realResp);
         }
         else
         {
            direq = new DispatchedHttpServletRequest.Resource(dispatch, (ResourceRequest)req, realReq, servletContext);
            diresp = new DispatchedHttpServletResponse.Mime(direq, (MimeResponseImpl)resp, realResp);
         }

         //
         switch (type)
         {
            case INCLUDE:
               dispatcher.include(direq, diresp);
               break;
            case FORWARD:

               if (resp instanceof MimeResponse)
               {
                  if (((MimeResponse)resp).isCommitted())
                  {
                     throw new IllegalStateException();
                  }
                  ((MimeResponse)resp).resetBuffer();
               }

               dispatcher.include(direq, diresp);

               // For now here
               if (resp instanceof MimeResponse)
               {
                  ((MimeResponse)resp).flushBuffer();
               }

               break;
         }
      }
      catch (ServletException e)
      {
         // We must translate the servlet exception into a portlet exception for the calling portlet
         throw new PortletException(e);
      }
   }

   private PortletRequestImpl unwrap(PortletRequest wrapped)
   {
      while (true)
      {
         if (wrapped instanceof PortletRequestImpl)
         {
            return (PortletRequestImpl)wrapped;
         }
         else if (wrapped instanceof PortletRequestWrapper)
         {
            PortletRequestWrapper wrapper = (PortletRequestWrapper)wrapped;
            wrapped = wrapper.getRequest();
         }
         else
         {
            // do we need something similar to unwrap(PortletResponse) behavior?
            throw new IllegalArgumentException("Cannot unwrap request: " + wrapped.getClass().getName());
         }
      }
   }

   private PortletResponseImpl unwrap(PortletResponse wrapped)
   {
      while (true)
      {
         if (wrapped instanceof PortletResponseImpl)
         {
            return (PortletResponseImpl)wrapped;
         }
         else if (wrapped instanceof PortletResponseWrapper)
         {
            PortletResponseWrapper wrapper = (PortletResponseWrapper)wrapped;
            wrapped = wrapper.getResponse();
         }
         else
         {
            // attempt to invoke a potential getResponse method (workaround for JBPORTAL-2204)
            Class<? extends PortletResponse> wrappedClass = wrapped.getClass();
            try
            {
               Method getResponse = wrappedClass.getMethod("getResponse");
               wrapped = (PortletResponse)getResponse.invoke(wrapped);
            }
            catch (Exception e)
            {
               throw new IllegalArgumentException("Cannot unwrap response: " + wrappedClass.getName());
            }
         }
      }
   }
}
