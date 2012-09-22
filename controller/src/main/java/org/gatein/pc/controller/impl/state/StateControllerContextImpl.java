/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.gatein.pc.controller.impl.state;

import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.info.NavigationInfo;
import org.gatein.pc.api.info.ParameterInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.controller.ControllerContext;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PageNavigationalState;

import javax.xml.namespace.QName;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class StateControllerContextImpl implements StateControllerContext
{

   public void updatePublicNavigationalState(
      ControllerContext controller,
      PageNavigationalState page,
      String portletWindowId,
      Map<String, String[]> update)
   {
      PortletInfo info = controller.getPortletInfo(portletWindowId);
      if (info != null)
      {
         NavigationInfo navigationInfo = info.getNavigation();
         for (Map.Entry<String, String[]> entry : update.entrySet())
         {
            String id = entry.getKey();
            ParameterInfo parameterInfo = navigationInfo.getPublicParameter(id);
            if (parameterInfo != null)
            {
               QName name = parameterInfo.getName();
               String[] value = entry.getValue();
               if (value.length > 0)
               {
                  page.setPublicNavigationalState(name, value);
               }
               else
               {
                  page.removePublicNavigationalState(name);
               }
            }
         }
      }
   }

   public Map<String, String[]> getPublicWindowNavigationalState(ControllerContext controller, PageNavigationalState page, String windowId)
   {
      PortletInfo info = controller.getPortletInfo(windowId);

      //
      if (info != null)
      {
         ParameterMap publicNavigationalState = new ParameterMap();
         for (ParameterInfo parameterInfo : info.getNavigation().getPublicParameters())
         {
            String[] parameterValue = page.getPublicNavigationalState(parameterInfo.getName());

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
}
