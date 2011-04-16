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

package org.gatein.pc.controller.impl;

import org.gatein.common.util.MarkupInfo;
import org.gatein.common.io.Serialization;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.impl.PortletURLRenderer;
import org.gatein.pc.portlet.impl.spi.AbstractPortletInvocationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of the <code>PortletInvocationContext</code> interface that is related to a specific
 * window.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ControllerPortletInvocationContext extends AbstractPortletInvocationContext
{

   /** . */
   private final PortletURLRenderer urlRenderer;

   /** . */
   private final String windowId;

   /** . */
   private final HttpServletRequest clientRequest;

   /** . */
   private final HttpServletResponse clientResponse;

   public ControllerPortletInvocationContext(
      Serialization<PortletPageNavigationalState> serialization,
      HttpServletRequest clientRequest,
      HttpServletResponse clientResponse,
      String windowId,
      PortletPageNavigationalState pageNavigationalState,
      MarkupInfo markupInfo)
   {
      super(markupInfo);

      //
      this.clientRequest = clientRequest;
      this.clientResponse = clientResponse;
      this.windowId = windowId;
      this.urlRenderer = new PortletURLRenderer(pageNavigationalState, clientRequest, clientResponse, serialization);
   }

   public String getWindowId()
   {
      return windowId;
   }

   public HttpServletRequest getClientRequest() throws IllegalStateException
   {
      return clientRequest;
   }

   public HttpServletResponse getClientResponse() throws IllegalStateException
   {
      return clientResponse;
   }

   public String renderURL(ContainerURL containerURL, URLFormat format)
   {
      return urlRenderer.renderURL(windowId, containerURL, format);
   }
}
