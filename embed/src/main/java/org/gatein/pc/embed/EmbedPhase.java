/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.pc.embed;

import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.util.SimpleMultiValuedPropertyMap;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.RenderURL;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.FragmentResponse;
import org.gatein.pc.api.invocation.response.HTTPRedirectionResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;
import org.gatein.pc.portlet.impl.spi.AbstractClientContext;
import org.gatein.pc.portlet.impl.spi.AbstractInstanceContext;
import org.gatein.pc.portlet.impl.spi.AbstractPortalContext;
import org.gatein.pc.portlet.impl.spi.AbstractRequestContext;
import org.gatein.pc.portlet.impl.spi.AbstractSecurityContext;
import org.gatein.pc.portlet.impl.spi.AbstractServerContext;
import org.gatein.pc.portlet.impl.spi.AbstractUserContext;
import org.gatein.pc.portlet.impl.spi.AbstractWindowContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.portlet.MimeResponse;
import javax.portlet.ResourceResponse;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
abstract class EmbedPhase
{

   /** . */
   protected final Page page;

   /** . */
   protected final PortletInvoker invoker;

   /** . */
   protected final HashMap<String, String[]> parameters;

   /** . */
   protected final HttpServletRequest req;

   /** . */
   protected final HttpServletResponse resp;

   protected EmbedPhase(Page page, PortletInvoker invoker, HashMap<String, String[]> parameters, HttpServletRequest req, HttpServletResponse resp)
   {
      this.page = page;
      this.invoker = invoker;
      this.parameters = parameters;
      this.req = req;
      this.resp = resp;
   }

   static class Render extends EmbedPhase
   {
      Render(Page page, PortletInvoker invoker, HashMap<String, String[]> parameters, HttpServletRequest req, HttpServletResponse resp)
      {
         super(page, invoker, parameters, req, resp);
      }

      @Override
      void invoke() throws IOException, ServletException
      {
         //
         StringBuilder body = new StringBuilder();
         body.append("<body>\n");
         body.append("<ul>\n");

         //
         StringBuilder head = new StringBuilder();
         head.append("<head>\n");

         //
         SimpleMultiValuedPropertyMap<String> httpHeaders = new SimpleMultiValuedPropertyMap<String>();
         ArrayList<Cookie> cookies = new ArrayList<Cookie>();

         //
         for (Window window : page.windows.values())
         {
            RenderInvocation render = new RenderInvocation(new EmbedInvocationContext(page, window, req, resp));

            //
            render.setTarget(window.portlet.getContext());
            render.setRequest(req);
            render.setResponse(resp);
            render.setUserContext(new AbstractUserContext(req));
            render.setWindowContext(new AbstractWindowContext("" + window.id));
            render.setServerContext(new AbstractServerContext(req, resp));
            render.setSecurityContext(new AbstractSecurityContext(req));
            render.setClientContext(new AbstractClientContext(req));
            render.setPortalContext(new AbstractPortalContext());
            render.setInstanceContext(new AbstractInstanceContext("" + window.id));
            render.setWindowState(window.state != null ? window.state : WindowState.NORMAL);
            render.setMode(window.mode != null ? window.mode : Mode.VIEW);
            render.setNavigationalState(window.parameters != null ? ParametersStateString.create(window.parameters) : null);
            render.setPublicNavigationalState(page.parameters);

            //
            PortletInvocationResponse response;
            try
            {
               response = invoker.invoke(render);
            }
            catch (PortletInvokerException e)
            {
               throw new ServletException(e);
            }

            //
            if (response instanceof ErrorResponse)
            {
               throw createException(window, (ErrorResponse)response);
            }
            else if (response instanceof FragmentResponse)
            {
               FragmentResponse fragment = (FragmentResponse)response;
               body.append("<li>");
               body.append(fragment.getContent(UTF_8));
               body.append("</li>\n");

               //
               ResponseProperties props = fragment.getProperties();
               if (props != null)
               {
                  if (props.getCookies() != null)
                  {
                     cookies.addAll(props.getCookies());
                  }

                  //
                  if (props.getTransportHeaders() != null)
                  {
                     httpHeaders.append(props.getTransportHeaders());
                  }

                  //
                  MultiValuedPropertyMap<Element> markupHeaders = props.getMarkupHeaders();
                  if (markupHeaders != null)
                  {
                     List<Element> elements = markupHeaders.getValues(MimeResponse.MARKUP_HEAD_ELEMENT);
                     if (elements != null)
                     {
                        for (Element element : elements)
                        {
                           String name = element.getTagName().toLowerCase();
                           if ("title".equals(name))
                           {
                              head.append("<title>");
                              appendText(element, head);
                              head.append("</title>");
                           }
                           else if ("link".equals(name))
                           {
                              head.append("<link");
                              appendAttribute(element, "charset", head);
                              appendAttribute(element, "href", head);
                              appendAttribute(element, "media", head);
                              appendAttribute(element, "rel", head);
                              appendAttribute(element, "type", head);
                              head.append("/>");
                           }
                           else if ("meta".equals(name))
                           {
                              head.append("<meta");
                              appendAttribute(element, "http-equiv", head);
                              appendAttribute(element, "name", head);
                              appendAttribute(element, "content", head);
                              head.append("/>");
                           }
                           else if ("script".equals(name))
                           {
                              head.append("<script");
                              appendAttribute(element, "type", head);
                              appendAttribute(element, "src", head);
                              head.append("></script>");
                           }
                           else if ("style".equals(name))
                           {
                              head.append("<style");
                              appendAttribute(element, "type", head);
                              appendAttribute(element, "media", head);
                              head.append(">");
                              appendText(element, head);
                              head.append("</style>");
                           }
                        }
                     }
                  }
               }
            }
         }

         //
         body.append("</ul>\n");
         body.append("</body>\n");

         //
         head.append("</head>");

         //
         resp.setStatus(200);
         resp.setContentType("text/html;charset=utf-8");

         //
         sendHttpHeaders(httpHeaders, resp);
         sendCookies(cookies, resp);

         //
         PrintWriter writer = resp.getWriter();
         writer.append("<!DOCTYPE html>\n");
         writer.append("<html>\n");
         writer.append(head);
         writer.append(body);
         writer.append("</html>\n");
         writer.close();
      }
   }

   static abstract class Interaction extends EmbedPhase
   {

      /** . */
      int count;

      protected Interaction(int count, Page page, PortletInvoker invoker, HashMap<String, String[]> parameters, HttpServletRequest req, HttpServletResponse resp)
      {
         super(page, invoker, parameters, req, resp);

         //
         this.count = count;
      }

      PortletInvocationResponse invoke(final EmbedInvocationContext context, PortletInvocation invocation) throws ServletException, IOException
      {
         if (count > EmbedServlet.MAX_EVENT_COUNT)
         {
            throw new ServletException("Too many events");
         }

         //
         PortletInvocationResponse response;
         try
         {
            response = invoker.invoke(invocation);
         }
         catch (PortletInvokerException e)
         {
            throw new ServletException("Unexpected exception", e);
         }

         // Increment count
         count++;

         //
         if (response instanceof ErrorResponse)
         {
            throw createException(context.target, (ErrorResponse)response);
         }
         else if (response instanceof UpdateNavigationalStateResponse)
         {
            final UpdateNavigationalStateResponse update = (UpdateNavigationalStateResponse)response;

            // Update window navigational state
            if (update.getNavigationalState() != null)
            {
               context.target.parameters = ((ParametersStateString)update.getNavigationalState()).getParameters();
            }
            if (update.getMode() != null)
            {
               context.target.mode = update.getMode();
            }
            if (update.getWindowState() != null)
            {
               context.target.state = update.getWindowState();
            }

            // Update public navigational state
            if (update.getPublicNavigationalStateUpdates() != null)
            {
               for (Map.Entry<String, String[]> change : update.getPublicNavigationalStateUpdates().entrySet())
               {
                  if (change.getValue() == null || change.getValue().length == 0)
                  {
                     page.parameters.remove(change.getKey());
                  }
                  else
                  {
                     page.parameters.put(change.getKey(), change.getValue());
                  }
               }
            }

            //
            for (UpdateNavigationalStateResponse.Event producedEvent : update.getEvents())
            {
               Collection<Window> consumers = page.getConsumers (producedEvent.getName());
               for (Window consumer : consumers)
               {
                  Event event = new Event(
                     consumer,
                     producedEvent.getName(),
                     producedEvent.getPayload(),
                     count,
                     page,
                     invoker,
                     null,
                     req,
                     resp);

                  //
                  event.invoke();

                  // Update our count
                  this.count = event.count;
               }
            }
         }

         //
         return response;
      }
   }

   static class Event extends Interaction
   {

      /** . */
      final Window target;

      /** . */
      final QName name;

      /** . */
      final Serializable payload;

      Event(
         Window target,
         QName name,
         Serializable payload,
         int count,
         Page page,
         PortletInvoker invoker,
         HashMap<String, String[]> parameters,
         HttpServletRequest req,
         HttpServletResponse resp)
      {
         super(count, page, invoker, parameters, req, resp);

         //
         this.target = target;
         this.name = name;
         this.payload = payload;
      }

      @Override
      void invoke() throws IOException, ServletException
      {
         EmbedInvocationContext context = new EmbedInvocationContext(page, target, req, resp);
         EventInvocation event = new EventInvocation(context);

         //
         event.setName(name);
         event.setPayload(payload);
         event.setTarget(target.portlet.getContext());
         event.setRequest(req);
         event.setResponse(resp);
         event.setUserContext(new AbstractUserContext(req));
         event.setWindowContext(new AbstractWindowContext("" + target.id));
         event.setServerContext(new AbstractServerContext(req, resp));
         event.setSecurityContext(new AbstractSecurityContext(req));
         event.setClientContext(new AbstractClientContext(req));
         event.setPortalContext(new AbstractPortalContext());
         event.setInstanceContext(new AbstractInstanceContext("" + target.id));
         event.setWindowState(target.state != null ? target.state : WindowState.NORMAL);
         event.setMode(target.mode != null ? target.mode : Mode.VIEW);
         event.setNavigationalState(target.parameters != null ? ParametersStateString.create(target.parameters) : null);
         event.setPublicNavigationalState(page.parameters);

         //
         invoke(context, event);
      }
   }

   static class Action extends Interaction
   {
      Action(Page page, PortletInvoker invoker, HashMap<String, String[]> parameters, HttpServletRequest req, HttpServletResponse resp)
      {
         super(0, page, invoker, parameters, req, resp);
      }

      @Override
      void invoke() throws IOException, ServletException
      {
         String id = parameters.remove("javax.portlet.id")[0];
         Window window = page.windows.get(id);
         final EmbedInvocationContext context = new EmbedInvocationContext(page, window, req, resp);
         ActionInvocation action = new ActionInvocation(context);

         //
         String[] modeParameter = parameters.remove("javax.portlet.portlet_mode");
         String[] windowStateParameter = parameters.remove("javax.portlet.window_state");
         Mode mode = modeParameter != null ? Mode.create(modeParameter[0]) : (window.mode != null ? window.mode : Mode.VIEW);
         WindowState windowState = windowStateParameter != null ? WindowState.create(windowStateParameter[0]) : (window.state != null ? window.state : WindowState.NORMAL);

         //
         action.setTarget(window.portlet.getContext());
         action.setRequest(req);
         action.setResponse(resp);
         action.setRequestContext(new AbstractRequestContext(req));
         action.setUserContext(new AbstractUserContext(req));
         action.setWindowContext(new AbstractWindowContext("" + window.id));
         action.setServerContext(new AbstractServerContext(req, resp));
         action.setSecurityContext(new AbstractSecurityContext(req));
         action.setClientContext(new AbstractClientContext(req));
         action.setPortalContext(new AbstractPortalContext());
         action.setInstanceContext(new AbstractInstanceContext("" + window.id));
         action.setWindowState(windowState);
         action.setMode(mode);
         action.setNavigationalState(window.parameters != null ? ParametersStateString.create(window.parameters) : null);
         action.setInteractionState(ParametersStateString.create(parameters));
         action.setPublicNavigationalState(page.parameters);

         //
         PortletInvocationResponse response = invoke(context, action);

         //
         if (response instanceof UpdateNavigationalStateResponse)
         {
            UpdateNavigationalStateResponse update = (UpdateNavigationalStateResponse)response;

            // Compute redirect URI
            RenderURL url = new RenderURL()
            {
               @Override
               public Map<String, String[]> getPublicNavigationalStateChanges()
               {
                  return Collections.emptyMap();
               }

               @Override
               public Mode getMode()
               {
                  return context.target.mode;
               }

               @Override
               public WindowState getWindowState()
               {
                  return context.target.state;
               }

               @Override
               public StateString getNavigationalState()
               {
                  return ParametersStateString.create(context.target.parameters);
               }

               @Override
               public Map<String, String> getProperties()
               {
                  return Collections.emptyMap();
               }
            };
            String uri = context.renderURL(url, URL_FORMAT);

            //
            ResponseProperties properties = update.getProperties();
            sendHttpHeaders(properties, resp);
            sendCookies(properties, resp);

            //
            resp.sendRedirect(uri);
         }
         else if (response instanceof HTTPRedirectionResponse)
         {
            HTTPRedirectionResponse redirect = (HTTPRedirectionResponse)response;
            resp.sendRedirect(redirect.getLocation());
         }
         else
         {
            throw new ServletException("Not yet implemented " + response.getClass().getSimpleName());
         }
      }
   }

   static class Resource extends EmbedPhase
   {
      Resource(Page page, PortletInvoker invoker, HashMap<String, String[]> parameters, HttpServletRequest req, HttpServletResponse resp)
      {
         super(page, invoker, parameters, req, resp);
      }

      @Override
      void invoke() throws IOException, ServletException
      {
         String id = parameters.remove("javax.portlet.id")[0];
         Window window = page.windows.get(id);
         EmbedInvocationContext context = new EmbedInvocationContext(page, window, req, resp);
         ResourceInvocation resource = new ResourceInvocation(context);
         String[] resourceId = parameters.remove("javax.portlet.resource");

         //
         resource.setTarget(window.portlet.getContext());
         resource.setRequest(req);
         resource.setResponse(resp);
         resource.setRequestContext(new AbstractRequestContext(req));
         resource.setUserContext(new AbstractUserContext(req));
         resource.setWindowContext(new AbstractWindowContext("" + window.id));
         resource.setServerContext(new AbstractServerContext(req, resp));
         resource.setSecurityContext(new AbstractSecurityContext(req));
         resource.setClientContext(new AbstractClientContext(req));
         resource.setPortalContext(new AbstractPortalContext());
         resource.setInstanceContext(new AbstractInstanceContext("" + window.id));
         resource.setWindowState(window.state != null ? window.state : WindowState.NORMAL);
         resource.setMode(window.mode != null ? window.mode : Mode.VIEW);
         resource.setNavigationalState(window.parameters != null ? ParametersStateString.create(window.parameters) : null);
         resource.setResourceState(ParametersStateString.create(parameters));
         resource.setPublicNavigationalState(page.parameters);
         resource.setResourceId(resourceId != null ? resourceId[0] : null);

         //
         PortletInvocationResponse response;
         try
         {
            response = invoker.invoke(resource);
         }
         catch (PortletInvokerException e)
         {
            throw new ServletException("Unexpected exception", e);
         }

         //
         if (response instanceof ErrorResponse)
         {
            throw createException(window, (ErrorResponse)response);
         }
         else if (response instanceof ContentResponse)
         {
            ContentResponse contentResponse = (ContentResponse)response;
            ResponseProperties properties = contentResponse.getProperties();

            // Determine status
            int status = 200;
            MultiValuedPropertyMap<String> headers = properties != null ? properties.getTransportHeaders() : null;
            if (headers != null)
            {
               String value = headers.getValue(ResourceResponse.HTTP_STATUS_CODE);
               if (value != null)
               {
                  try
                  {
                     status = Integer.parseInt(value);
                  }
                  catch (NumberFormatException e)
                  {
                     throw new ServletException("Bad " + ResourceResponse.HTTP_STATUS_CODE + "=" + value +
                        " resource value", e);
                  }
               }
            }
            resp.setStatus(status);

            // Send content type
            if (contentResponse.getContentType() != null)
            {
               resp.setContentType(contentResponse.getContentType());
            }

            // Set encoding
            String encoding = contentResponse.getEncoding();
            if (encoding != null)
            {
               resp.setCharacterEncoding(encoding);
            }

            //
            sendHttpHeaders(properties, resp);
            sendCookies(properties, resp);

            //
            if (contentResponse.getChars() != null)
            {
               PrintWriter writer = resp.getWriter();
               writer.write(contentResponse.getChars());
            }
            else if (contentResponse.getBytes() != null)
            {
               OutputStream out = resp.getOutputStream();
               out.write(contentResponse.getBytes());
            }
         }
         else
         {
            throw new ServletException("Not yet implemented " + response.getClass().getSimpleName());
         }
      }
   }

   abstract void invoke() throws IOException, ServletException;

   /** . */
   private static final URLFormat URL_FORMAT = new URLFormat(null, null, null, false);

   /** . */
   private static final Charset UTF_8 = Charset.forName("UTF-8");

   private static ServletException createException(Window window, ErrorResponse response)
   {
      ErrorResponse error = (ErrorResponse)response;
      return new ServletException("Portlet " + window.name + " threw an error: " + error.getMessage(), error.getCause());
   }

   private static void sendCookies(ResponseProperties properties, HttpServletResponse resp)
   {
      if (properties != null)
      {
         sendCookies(properties.getCookies(), resp);
      }
   }

   private static void sendCookies(List<Cookie> cookies, HttpServletResponse resp)
   {
      if (cookies != null)
      {
         for (Cookie cookie : cookies)
         {
            resp.addCookie(cookie);
         }
      }
   }

   private static void sendHttpHeaders(ResponseProperties properties, HttpServletResponse resp)
   {
      if (properties != null)
      {
         sendHttpHeaders(properties.getTransportHeaders(), resp);
      }
   }

   private static void sendHttpHeaders(MultiValuedPropertyMap<String> httpHeaders, HttpServletResponse resp)
   {
      if (httpHeaders != null)
      {
         for (String headerName : httpHeaders.keySet())
         {
            if (!headerName.equals(ResourceResponse.HTTP_STATUS_CODE))
            {
               resp.setHeader(headerName, httpHeaders.getValue(headerName));
            }
         }
      }
   }

   private static void appendAttribute(Element element, String name, StringBuilder to)
   {
      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0;i < attributes.getLength();i++)
      {
         Attr attribute = (Attr)attributes.item(i);
         if (attribute.getName().toLowerCase().equals(name))
         {
            String value = attribute.getValue();
            if (value.length() > 0)
            {
               to.append(" ").append(name).append(" =\"").append(value).append("\"");
            }
            break;
         }
      }
   }

   private static void appendText(Node node, StringBuilder to)
   {
      switch (node.getNodeType())
      {
         case Node.CDATA_SECTION_NODE:
         case Node.TEXT_NODE:
            to.append(((Text)node).getData());
            break;
         case Node.ELEMENT_NODE:
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
               Node child = children.item(i);
               appendText(child, to);
            }
            break;
         default:
            break;
      }
   }
}
