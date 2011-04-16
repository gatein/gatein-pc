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

import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.federation.FederatedPortletInvoker;
import org.gatein.pc.api.info.PortletInfo;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class FederatedPortlet implements Portlet
{

   /** . */
   final PortletContext compoundContext;

   /** . */
   final Portlet portlet;

   /** . */
   final FederatedPortletInvoker invoker;

   public FederatedPortlet(FederatedPortletInvoker invoker, PortletContext compoundContext, Portlet portlet)
   {
      if (invoker == null)
      {
         throw new IllegalArgumentException("No null invoker accepted");
      }
      if (compoundContext == null)
      {
         throw new IllegalArgumentException("No null id accepted");
      }
      if (portlet == null)
      {
         throw new IllegalArgumentException("No null portlet accepted");
      }
      this.invoker = invoker;
      this.compoundContext = compoundContext;
      this.portlet = portlet;
   }

   public String getFederatedId()
   {
      return invoker.getId();
   }

   public PortletContext getContext()
   {
      return compoundContext;
   }

   public PortletInfo getInfo()
   {
      return portlet.getInfo();
   }

   public boolean isRemote()
   {
      return portlet.isRemote();
   }

   public String toString()
   {
      return "FederatedPortlet[context=" + compoundContext + ",portlet=" + portlet + "]";
   }
}
