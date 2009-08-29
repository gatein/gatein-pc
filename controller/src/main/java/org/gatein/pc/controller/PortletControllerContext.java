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
package org.gatein.pc.controller;

import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.controller.event.EventControllerContext;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.PortletInvocationContext;

import javax.servlet.http.Cookie;
import java.util.List;

/**
 * The context provided to call the portlet controller.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public interface PortletControllerContext
{

   /**
    * Returns the portet info for a specified window.
    *
    * @param windowId the window id
    * @return the portlet info or null if none can be found
    */
   PortletInfo getPortletInfo(String windowId);

   /**
    * Create a portlet invocation context for the specified window id.
    *
    * @param windowId the window id
    * @param pageNavigationalState
    * @return
    */
   PortletInvocationContext createPortletInvocationContext(String windowId, PortletPageNavigationalState pageNavigationalState);

   PortletInvocationResponse invoke(ActionInvocation actionInvocation) throws PortletInvokerException;

   PortletInvocationResponse invoke(List<Cookie> requestCookies, EventInvocation eventInvocation) throws PortletInvokerException;

   PortletInvocationResponse invoke(List<Cookie> requestCookies, RenderInvocation renderInvocation) throws PortletInvokerException;

   PortletInvocationResponse invoke(ResourceInvocation resourceInvocation) throws PortletInvokerException;

   EventControllerContext getEventControllerContext();

   StateControllerContext getStateControllerContext();

}
