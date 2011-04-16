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
package org.gatein.pc.controller.impl.state;

import org.gatein.common.util.ParameterMap;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.api.info.ParameterInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.NavigationInfo;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletPageNavigationalStateImpl implements PortletPageNavigationalState, Serializable
{

   /** . */
   protected final StateControllerContextImpl context;

   /** . */
   protected final Map<String, PortletWindowNavigationalState> windows;

   /** . */
   protected final Map<QName, String[]> page;

   /** . */
   private final boolean modifiable;

   protected PortletPageNavigationalStateImpl(StateControllerContextImpl context, boolean modifiable)
   {
      this.context = context;
      this.windows = new HashMap<String, PortletWindowNavigationalState>();
      this.page = new HashMap<QName, String[]>();
      this.modifiable = modifiable;
   }

   public PortletPageNavigationalStateImpl(PortletPageNavigationalStateImpl original, boolean modifiable)
   {
      this.context = original.context;
      this.windows = new HashMap<String, PortletWindowNavigationalState>(original.windows);
      this.page = new HashMap<QName, String[]>(original.page);
      this.modifiable = modifiable;
   }

   public Set<String> getPortletWindowIds()
   {
      return windows.keySet();
   }

   public PortletWindowNavigationalState getPortletWindowNavigationalState(String portletWindowId)
   {
      return windows.get(portletWindowId);
   }

   public Map<String, String[]> getPortletPublicNavigationalState(String portletWindowId)
   {
      PortletInfo info = context.portletControllerContext.getPortletInfo(portletWindowId);

      //
      if (info != null)
      {
         ParameterMap publicNavigationalState = new ParameterMap();
         for (ParameterInfo parameterInfo : info.getNavigation().getPublicParameters())
         {
            String[] parameterValue = page.get(parameterInfo.getName());

            //
            if (parameterValue != null)
            {
               String parameterId = parameterInfo.getId();

               // We clone the value here so we keep the internal state not potentially changed
               publicNavigationalState.put(parameterId, parameterValue.clone());
            }
         }

         //
         return publicNavigationalState;
      }

      //
      return null;
   }

   public Set<QName> getPublicNames()
   {
      return page.keySet();
   }

   public String[] getPublicNavigationalState(QName name)
   {
      String[] values = page.get(name);
      return values != null ? values.clone() : null;
   }

   public void setPortletWindowNavigationalState(String portletWindowId, PortletWindowNavigationalState portletWindowState)
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      //
      windows.put(portletWindowId, portletWindowState);
   }

   public void setPortletPublicNavigationalState(String portletWindowId, Map<String, String[]> update)
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      //
      PortletInfo info = context.portletControllerContext.getPortletInfo(portletWindowId);

      //
      if (info != null)
      {
         NavigationInfo navigationInfo = info.getNavigation();
         for (Map.Entry<String, String[]> entry : update.entrySet())
         {
            String id = entry.getKey();

            //
            ParameterInfo parameterInfo = navigationInfo.getPublicParameter(id);

            //
            if (parameterInfo != null)
            {
               QName name = parameterInfo.getName();
               String[] value = entry.getValue();
               if (value.length > 0)
               {
                  setPublicNavigationalState(name, value);
               }
               else
               {
                  removePublicNavigationalState(name);
               }
            }
         }
      }
   }

   public void setPublicNavigationalState(QName name, String[] value)
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      // We clone the value in order to keep the state not mutated by a side effect
      page.put(name, value.clone());
   }

   public void removePublicNavigationalState(QName name)
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      //
      page.remove(name);
   }
}