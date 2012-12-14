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

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.WindowState;

import javax.servlet.ServletException;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
class Page
{

   /** . */
   final Map<String, String[]> parameters;

   /** . */
   final LinkedHashMap<String, Window> windows;

   Page(PortletInvoker invoker, String s) throws ServletException
   {
      LinkedHashMap<String, Window> windows = new LinkedHashMap<String, Window>();
      int count = 0;
      Map<String, String[]> parameters;

      //
      if (s == null ||  s.length() == 0 || (s.length() == 1 && s.charAt(0) == '/'))
      {
         parameters = new HashMap<String, String[]>();
      }
      else
      {
         Segment segments = (Segment)Chunk.parse(s);

         // Skip context path
         segments = (Segment)segments.next;

         // Servlet parameter
         parameters = segments.parameters != null ? segments.parameters : new HashMap<String, String[]>();

         //
         for (Segment segment : (Segment)segments.next)
         {
            Portlet found = null;
            if (invoker != null)
            {
               try
               {
                  for (Portlet portlet : invoker.getPortlets())
                  {
                     if (portlet.getInfo().getName().equals(segment.value))
                     {
                        found = portlet;
                        break;
                     }
                  }
               }
               catch (PortletInvokerException e)
               {
                  // ?
               }
            }

            //
            LinkedHashMap<String, String[]> windowParameters;
            Mode windowMode;
            WindowState windowState;
            if (segment.parameters != null)
            {
               windowParameters = new LinkedHashMap<String, String[]>(segment.parameters);
               String[] modeParameter = windowParameters.remove("javax.portlet.portlet_mode");
               String[] windowStateParameter = windowParameters.remove("javax.portlet.window_state");
               windowMode = modeParameter != null ? Mode.create(modeParameter[0]) : null;
               windowState = windowStateParameter != null ? WindowState.create(windowStateParameter[0]) : null;
            }
            else
            {
               windowParameters = null;
               windowMode = null;
               windowState = null;
            }

            //
            Window context = new Window("" + count++, found, segment.value, windowMode, windowState, windowParameters);

            //
            windows.put(context.id, context);
         }
      }

      //
      this.windows = windows;
      this.parameters = parameters;
   }

   /**
    * Returns the windows that can consume the wanted event name.
    *
    * @param wanted the wanted event name
    * @return the collection of windows that can consume the wanted event
    */
   Collection<Window> getConsumers(QName wanted)
   {
      List<Window> consumers = null;
      for (Window window : windows.values())
      {
         if (window.portlet != null)
         {
            for (QName name : window.portlet.getInfo().getEventing().getConsumedEvents().keySet())
            {
               if (wanted.equals(name))
               {
                  if (consumers == null)
                  {
                     consumers = new ArrayList<Window>();
                  }
                  consumers.add(window);
               }
            }
         }
      }
      return consumers == null ? Collections.<Window>emptyList() : consumers;
   }
}
