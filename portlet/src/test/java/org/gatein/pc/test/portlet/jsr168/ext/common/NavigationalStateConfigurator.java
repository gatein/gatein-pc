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
package org.gatein.pc.test.portlet.jsr168.ext.common;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionResponse;
import javax.portlet.PortletModeException;
import javax.portlet.PortletMode;
import javax.portlet.WindowStateException;
import javax.portlet.WindowState;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public interface NavigationalStateConfigurator
{

   PortletURL createPortletURL(RenderResponse resp) throws PortletException;

   void configureNavigationalState(ActionResponse resp) throws PortletException;

   public static final NavigationalStateConfigurator PORTLET_MODE_CONFIGURATOR = new NavigationalStateConfigurator()
   {
      public PortletURL createPortletURL(RenderResponse resp) throws PortletModeException
      {
         PortletURL url = resp.createRenderURL();
         url.setPortletMode(PortletMode.EDIT);
         return url;
      }
      public void configureNavigationalState(ActionResponse resp) throws PortletModeException
      {
         resp.setPortletMode(PortletMode.EDIT);
      }
   };

   public static final NavigationalStateConfigurator RENDER_PARAMETER_CONFIGURATOR = new NavigationalStateConfigurator()
   {
      public PortletURL createPortletURL(RenderResponse resp)
      {
         PortletURL url = resp.createRenderURL();
         url.setParameter("abc", "def");
         return url;
      }
      public void configureNavigationalState(ActionResponse resp)
      {
         resp.setRenderParameter("abc", "fed");
      }
   };

   public static final NavigationalStateConfigurator WINDOW_STATE_CONFIGURATOR = new NavigationalStateConfigurator()
   {
      public PortletURL createPortletURL(RenderResponse resp) throws WindowStateException
      {
         PortletURL url = resp.createRenderURL();
         url.setWindowState(WindowState.MAXIMIZED);
         return url;
      }
      public void configureNavigationalState(ActionResponse resp) throws WindowStateException
      {
         resp.setWindowState(WindowState.MAXIMIZED);
      }
   };
}
