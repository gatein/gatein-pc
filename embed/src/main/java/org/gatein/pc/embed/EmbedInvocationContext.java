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

import org.gatein.pc.api.ActionURL;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.RenderURL;
import org.gatein.pc.api.ResourceURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.portlet.impl.spi.AbstractPortletInvocationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class EmbedInvocationContext extends AbstractPortletInvocationContext
{

   /** . */
   private final Page page;

   /** . */
   private final HttpServletRequest clientRequest;

   /** . */
   private final HttpServletResponse clientResponse;

   /** . */
   final Window target;

   /** . */
   final String baseURL;

   /** . */
   final String servletPath;

   EmbedInvocationContext(Page page, Window target, HttpServletRequest clientRequest, HttpServletResponse clientResponse)
   {
      StringBuilder baseURL = new StringBuilder();
      baseURL.append(clientRequest.getScheme()).append("://").append(clientRequest.getServerName());
      int port = clientRequest.getServerPort();
      if (port != 80)
      {
         baseURL.append(':').append(port);
      }
      baseURL.append(clientRequest.getContextPath());

      //
      this.page = page;
      this.clientRequest = clientRequest;
      this.clientResponse = clientResponse;
      this.baseURL = baseURL.toString();
      this.target = target;
      this.servletPath = clientRequest.getServletPath();
   }

   @Override
   public HttpServletRequest getClientRequest() throws IllegalStateException
   {
      return clientRequest;
   }

   @Override
   public HttpServletResponse getClientResponse() throws IllegalStateException
   {
      return clientResponse;
   }

   @Override
   public String renderURL(ContainerURL containerURL, URLFormat format)
   {
      LinkedHashMap<String, String[]> pageParameters = page.parameters != null ? new LinkedHashMap<String, String[]>(page.parameters) : new LinkedHashMap<String, String[]>();
      if (containerURL instanceof RenderURL)
      {
         Map<String, String[]> changes = ((RenderURL)containerURL).getPublicNavigationalStateChanges();
         if (changes != null)
         {
            for (Map.Entry<String, String[]> change : changes.entrySet())
            {
               if (change.getValue() == null || change.getValue().length == 0)
               {
                  pageParameters.remove(change.getKey());
               }
               else
               {
                  pageParameters.put(change.getKey(), change.getValue());
               }
            }
         }
      }

      //
      HashMap<Window, Map<String, String[]>> parameterMap = new HashMap<Window, Map<String, String[]>>();
      HashMap<Window, Mode> modeMap = new HashMap<Window, Mode>();
      HashMap<Window, WindowState> windowStateMap = new HashMap<Window, WindowState>();
      for (Window window : page.windows.values())
      {
         if (window.parameters != null)
         {
            parameterMap.put(window, window.parameters);
         }
         if (window.mode != null)
         {
            modeMap.put(window, window.mode);
         }
         if (window.state != null)
         {
            windowStateMap.put(window, window.state);
         }
      }

      // We override for render URL with the new state
      if (containerURL instanceof RenderURL)
      {
         if (containerURL.getNavigationalState() != null)
         {
            parameterMap.put(target, ((ParametersStateString)containerURL.getNavigationalState()).getParameters());
         }
         if (containerURL.getMode() != null)
         {
            modeMap.put(target, containerURL.getMode());
         }
         if (containerURL.getWindowState() != null)
         {
            windowStateMap.put(target, containerURL.getWindowState());
         }
      }

      //
      Segment head = new Segment(servletPath.substring(1), pageParameters);
      Segment tail = head;

      //
      for (Window window : page.windows.values())
      {
         Map<String, String[]> parameters = parameterMap.get(window);
         if (parameters != null)
         {
            parameters = new HashMap<String, String[]>(parameters);
         }
         Mode mode = modeMap.get(window);
         if (mode != null && !Mode.VIEW.equals(mode))
         {
            parameters.put("javax.portlet.portlet_mode", new String[]{mode.toString()});
         }
         WindowState windowState = windowStateMap.get(window);
         if (windowState != null && !WindowState.NORMAL.equals(windowState))
         {
            parameters.put("javax.portlet.window_state", new String[]{windowState.toString()});
         }
         tail.next = new Segment(window.name, parameters);
         tail = (Segment)tail.next;
      }

      //
      if (containerURL instanceof ActionURL)
      {
         ActionURL actionURL = (ActionURL)containerURL;
         Mode mode = containerURL.getMode();
         WindowState windowState = containerURL.getWindowState();
         Map<String, String[]> parameters = actionURL.getInteractionState() != null ? ((ParametersStateString)actionURL.getInteractionState()).getParameters() : new HashMap<String, String[]>();
         parameters.put("javax.portlet.phase", new String[]{"action"});
         parameters.put("javax.portlet.id", new String[]{target.id});
         if (mode != null && !Mode.VIEW.equals(mode))
         {
            parameters.put("javax.portlet.portlet_mode", new String[]{mode.toString()});
         }
         if (windowState != null && !WindowState.NORMAL.equals(windowState))
         {
            parameters.put("javax.portlet.window_state", new String[]{windowState.toString()});
         }
         tail.next = new Query(parameters);
      }
      else if (containerURL instanceof ResourceURL)
      {
         ResourceURL resourceURL = (ResourceURL)containerURL;
         Map<String, String[]> parameters = resourceURL.getResourceState() != null ? ((ParametersStateString)resourceURL.getResourceState()).getParameters() : new HashMap<String, String[]>();
         parameters.put("javax.portlet.phase", new String[]{"resource"});
         parameters.put("javax.portlet.id", new String[]{target.id});
         String resource = resourceURL.getResourceId();
         if (resource != null)
         {
            parameters.put("javax.portlet.resource", new String[]{resource});
         }
         tail.next = new Query(parameters);
      }

      //
      StringBuilder sb = new StringBuilder();
      sb.append(baseURL);
      head.writeTo(sb, Boolean.TRUE == format.getWantEscapeXML() ? "&amp;" : "&");
      return sb.toString();
   }
}
