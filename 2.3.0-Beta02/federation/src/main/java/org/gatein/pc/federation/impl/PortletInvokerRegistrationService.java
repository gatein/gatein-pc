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
package org.gatein.pc.federation.impl;

import org.gatein.pc.federation.FederatingPortletInvoker;
import org.gatein.pc.api.PortletInvoker;

/**
 * Register any portlet invoker into a federating portlet invoker.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 * @since 2.4
 */
public class PortletInvokerRegistrationService
{

   /** The registration id. */
   private String id;

   /** The portlet invoker to register. */
   private PortletInvoker portletInvoker;

   /** The federating portlet invoker. */
   private FederatingPortletInvoker federatingPortletInvoker;

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public PortletInvoker getPortletInvoker()
   {
      return portletInvoker;
   }

   public void setPortletInvoker(PortletInvoker portletInvoker)
   {
      this.portletInvoker = portletInvoker;
   }

   public FederatingPortletInvoker getFederatingPortletInvoker()
   {
      return federatingPortletInvoker;
   }

   public void setFederatingPortletInvoker(FederatingPortletInvoker federatingPortletInvoker)
   {
      this.federatingPortletInvoker = federatingPortletInvoker;
   }

   public void start() throws Exception
   {
      federatingPortletInvoker.registerInvoker(id, portletInvoker);
   }

   public void stop() throws Exception
   {
      federatingPortletInvoker.unregisterInvoker(id);
   }
}
