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
package org.gatein.pc.test.portlet.jsr286.tck.portleturl;

import org.jboss.unit.api.Assert;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletURLSnapshot
{

   /** . */
   private static final int ACTION = 0;

   /** . */
   private static final int RENDER = 1;

   /** . */
   private static final int RESOURCE = 2;

   /** . */
   final String source;

   /** . */
   final int type;

   /** . */
   final PortletMode portletMode;

   /** . */
   final WindowState windowState;

   /** . */
   final Map<String, String[]> parameters;

   /** . */
   final String cacheability;

   public static PortletURLSnapshot createActionURL(String source, PortletURL actionURL)
   {
      return new PortletURLSnapshot(source, ACTION, actionURL);
   }

   public static PortletURLSnapshot createRenderURL(String source, PortletURL renderURL)
   {
      return new PortletURLSnapshot(source, RENDER, renderURL);
   }

   public static PortletURLSnapshot createResourceURL(String source, ResourceURL resourceURL)
   {
      return new PortletURLSnapshot(source, resourceURL);
   }

   public static PortletURLSnapshot createActionURL(String source, PortletMode portletMode, WindowState windowState, Map<String, String[]> parameters)
   {
      return new PortletURLSnapshot(source, ACTION, portletMode, windowState, parameters);
   }

   public static PortletURLSnapshot createRenderURL(String source, PortletMode portletMode, WindowState windowState, Map<String, String[]> parameters)
   {
      return new PortletURLSnapshot(source, RENDER, portletMode, windowState, parameters);
   }

   public static PortletURLSnapshot createResourceURL(String source, Map<String, String[]> parameters, String cacheability)
   {
      return new PortletURLSnapshot(source, parameters, cacheability);
   }

   private PortletURLSnapshot(String source, int type, PortletURL portletURL)
   {
      this.source = source;
      this.type = type;
      this.portletMode = portletURL.getPortletMode();
      this.windowState = portletURL.getWindowState();
      this.parameters = portletURL.getParameterMap();
      this.cacheability = null;
   }

   private PortletURLSnapshot(String source, ResourceURL resourceURL)
   {
      this.source = source;
      this.type = RESOURCE;
      this.portletMode = null;
      this.windowState = null;
      this.parameters = resourceURL.getParameterMap();
      this.cacheability = resourceURL.getCacheability();
   }

   private PortletURLSnapshot(String source, int type, PortletMode portletMode, WindowState windowState, Map<String, String[]> parameters)
   {
      this.source = source;
      this.type = type;
      this.portletMode = portletMode;
      this.windowState = windowState;
      this.parameters = parameters;
      this.cacheability = null;
   }

   private PortletURLSnapshot(String source, Map<String, String[]> parameters, String cacheability)
   {
      this.source = source;
      this.type = RESOURCE;
      this.portletMode = null;
      this.windowState = null;
      this.parameters = parameters;
      this.cacheability = cacheability;
   }

   public void assertEquals(PortletURLSnapshot actual)
   {
      Assert.assertNotNull(actual);
      Assert.assertEquals(source, actual.source);
      Assert.assertEquals(type, actual.type);
      Assert.assertEquals(parameters.keySet(), actual.parameters.keySet());
      for (Map.Entry<String, String[]> entry : parameters.entrySet())
      {
         Assert.assertEquals(entry.getValue(), actual.parameters.get(entry.getKey()));
      }
      if (type == RESOURCE)
      {
         Assert.assertEquals(cacheability, actual.cacheability);
      }
      else
      {
         Assert.assertEquals(portletMode, actual.portletMode);
         Assert.assertEquals(windowState, actual.windowState);
      }
   }
}
