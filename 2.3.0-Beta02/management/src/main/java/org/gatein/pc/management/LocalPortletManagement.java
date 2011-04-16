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
package org.gatein.pc.management;

import org.gatein.pc.api.Portlet;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class LocalPortletManagement implements LocalPortletManagementMBean
{
   private Portlet portlet;
   
   private PortletContainerManagementInterceptor interceptor;
   
   public LocalPortletManagement(Portlet portlet, PortletContainerManagementInterceptor interceptor)
   {
      this.portlet = portlet;
      this.interceptor = interceptor;
   }
   
   public String getId()
   {
      return portlet.getContext().getId();
   }

   public float getAverageRenderTime()
   {
      return interceptor.getPortletInfo(getId()).getAverageRenderTime();
   }

   public float getAverageActionTime()
   {
      return interceptor.getPortletInfo(getId()).getAverageActionTime();
   }

   public long getMaxRenderTime()
   {
      return interceptor.getPortletInfo(getId()).getMaxRenderTime();
   }

   public long getMaxActionTime()
   {
      return interceptor.getPortletInfo(getId()).getMaxActionTime();
   }

   public long getRenderRequestCount()
   {
      return interceptor.getPortletInfo(getId()).getRenderRequestCount();
   }

   public long getActionRequestCount()
   {
      return interceptor.getPortletInfo(getId()).getActionRequestCount();
   }
   
   public long getActionErrorCount()
   {
      return interceptor.getPortletInfo(getId()).getActionErrorCount();
   }

   public long getRenderErrorCount()
   {
      return interceptor.getPortletInfo(getId()).getRenderErrorCount();
   }
}
