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

import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.ActionURL;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.RenderURL;
import org.gatein.pc.api.ResourceURL;
import org.gatein.pc.api.PortletURL;
import org.gatein.pc.api.cache.CacheLevel;
import org.gatein.common.io.IOTools;
import org.gatein.common.io.SerializationFilter;
import org.gatein.common.io.Serialization;
import org.gatein.common.util.Base64;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ControllerRequestParameterMapFactory
{

   /** . */
   private final Serialization<PortletPageNavigationalState> serialization;

   public ControllerRequestParameterMapFactory(Serialization<PortletPageNavigationalState> serialization)
   {
      this.serialization = serialization;
   }

   public Map<String, String> encode(PortletPageNavigationalState pageNS, String windowId, ContainerURL containerURL)
   {
      Map<String, String> parameters = new HashMap<String, String>();

      //
      parameters.put(ControllerRequestParameterNames.WINDOW_ID, windowId);

      //
      String type;
      if (containerURL instanceof ActionURL)
      {
         type = ControllerRequestParameterNames.ACTION_PHASE;
      }
      else if (containerURL instanceof RenderURL)
      {
         type = ControllerRequestParameterNames.RENDER_PHASE;
      }
      else if (containerURL instanceof ResourceURL)
      {
         type = ControllerRequestParameterNames.RESOURCE_PHASE;
      }
      else
      {
         throw new Error();
      }
      parameters.put(ControllerRequestParameterNames.LIFECYCLE_PHASE, type);


      //
      String pageNavigationalState = null;
      if (pageNS != null)
      {
         byte[] bytes = IOTools.serialize(serialization, SerializationFilter.COMPRESSOR, pageNS);
         pageNavigationalState = Base64.encodeBytes(bytes, true);
      }

      //
      if (containerURL instanceof PortletURL)
      {
         PortletURL portletURL = (PortletURL)containerURL;

         if (portletURL.getMode() != null)
         {
            parameters.put(ControllerRequestParameterNames.MODE, portletURL.getMode().toString());
         }

         //
         if (portletURL.getWindowState() != null)
         {
            parameters.put(ControllerRequestParameterNames.WINDOW_STATE, portletURL.getWindowState().toString());
         }

         //
         if (pageNavigationalState != null)
         {
            parameters.put(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE, pageNavigationalState);
         }

         //
         if (containerURL instanceof ActionURL)
         {
            ActionURL actionURL = (ActionURL)containerURL;

            //
            if (actionURL.getNavigationalState() != null)
            {
               parameters.put(ControllerRequestParameterNames.NAVIGATIONAL_STATE, actionURL.getNavigationalState().getStringValue());
            }

            //
            StateString interactionState = actionURL.getInteractionState();
            parameters.put(ControllerRequestParameterNames.INTERACTION_STATE, interactionState.getStringValue());
         }
         else
         {
            RenderURL renderURL = (RenderURL)containerURL;

            //
            Map<String, String[]> changes = renderURL.getPublicNavigationalStateChanges();
            byte[] bytes = IOTools.serialize(Serialization.PARAMETER_MAP, SerializationFilter.COMPRESSOR, changes);
            String ns = Base64.encodeBytes(bytes, true);
            parameters.put(ControllerRequestParameterNames.PUBLIC_NAVIGATIONAL_STATE_CHANGES, ns);

            //
            StateString navigationalState = renderURL.getNavigationalState();
            if (navigationalState != null)
            {
               parameters.put(ControllerRequestParameterNames.NAVIGATIONAL_STATE, navigationalState.getStringValue());
            }
         }
      }
      else
      {
         ResourceURL resourceURL = (ResourceURL)containerURL;

         //
         StateString resourceState = resourceURL.getResourceState();
         parameters.put(ControllerRequestParameterNames.RESOURCE_STATE, resourceState.getStringValue());

         //
         String resourceId = resourceURL.getResourceId();
         if (resourceId != null)
         {
            parameters.put(ControllerRequestParameterNames.RESOURCE_ID, resourceId);
         }

         //
         CacheLevel cacheability = resourceURL.getCacheability();
         parameters.put(ControllerRequestParameterNames.RESOURCE_CACHEABILITY, cacheability.name());

         //
         if (cacheability != CacheLevel.FULL)
         {
            if (resourceURL.getNavigationalState() != null)
            {
               parameters.put(ControllerRequestParameterNames.NAVIGATIONAL_STATE, resourceURL.getNavigationalState().getStringValue());
            }

            //
            if (resourceURL.getMode() != null)
            {
               parameters.put(ControllerRequestParameterNames.MODE, resourceURL.getMode().toString());
            }

            //
            if (resourceURL.getWindowState() != null)
            {
               parameters.put(ControllerRequestParameterNames.WINDOW_STATE, resourceURL.getWindowState().toString());
            }

            if (cacheability == CacheLevel.PAGE && pageNavigationalState != null)
            {
               parameters.put(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE, pageNavigationalState);
            }
         }
      }

      //
      return parameters;
   }
}
