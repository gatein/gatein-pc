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

import org.gatein.pc.api.WindowState;
import org.gatein.common.io.IOTools;
import org.gatein.common.io.Serialization;
import org.gatein.common.io.SerializationFilter;
import org.gatein.common.util.Base64;
import org.gatein.common.util.MapAdapters;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.cache.CacheLevel;
import org.gatein.pc.controller.request.ControllerRequest;
import org.gatein.pc.controller.request.PortletActionRequest;
import org.gatein.pc.controller.request.PortletRenderRequest;
import org.gatein.pc.controller.request.PortletResourceRequest;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.wci.Body;
import org.gatein.wci.WebRequest;

import java.util.Map;

/**
 * A factory that provides a way to create ControllerRequest. This factory is just a default implementation and is not
 * an authority which means that any client of the controller framework is free to determine how a controller request is
 * created.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ControllerRequestFactory
{

   /** . */
   private final Serialization<PortletPageNavigationalState> serialization;

   public ControllerRequestFactory(Serialization<PortletPageNavigationalState> serialization)
   {
      this.serialization = serialization;
   }

   /**
    * Create a controller request.
    *
    * @param req the web request
    * @return the decoded controller request
    */
   public ControllerRequest decode(WebRequest req)
   {
      return decode(req.getQueryParameterMap(), req.getBody());
   }

   /**
    * Create a controller request.
    *
    * @param queryParameters the query parameters
    * @param body            the body
    * @return the decoded controller request
    */
   public ControllerRequest decode(Map<String, String[]> queryParameters, Body body)
   {
      Map<String, String[]> bodyParameters = null;

      //
      if (body instanceof Body.Form)
      {
         bodyParameters = ((Body.Form)body).getParameters();
      }

      //
      return decode(queryParameters, bodyParameters);
   }

   /**
    * Create a controller request.
    *
    * @param queryParameterMap the query parameter map
    * @param formParameterMap  the optional form parameter map
    * @return the decoded controller request
    */
   public ControllerRequest decode(Map<String, String[]> queryParameterMap, Map<String, String[]> formParameterMap)
   {
      Map<String, String> queryParameters = MapAdapters.adapt(queryParameterMap);

      // The nav state provided with the request
      // Unmarshall portal navigational state if it is provided
      PortletPageNavigationalState pageNavigationalState = null;
      String context = queryParameters.get(ControllerRequestParameterNames.PAGE_NAVIGATIONAL_STATE);
      if (context != null)
      {
         byte[] bytes = Base64.decode(context, true);
         pageNavigationalState = IOTools.unserialize(serialization, SerializationFilter.COMPRESSOR, bytes);
      }

      // Get the window id
      String windowId = queryParameters.get(ControllerRequestParameterNames.WINDOW_ID);

      //
      org.gatein.pc.api.Mode mode = null;
      if (queryParameters.get(ControllerRequestParameterNames.MODE) != null)
      {
         mode = org.gatein.pc.api.Mode.create(queryParameters.get(ControllerRequestParameterNames.MODE));
      }

      //
      WindowState windowState = null;
      if (queryParameters.get(ControllerRequestParameterNames.WINDOW_STATE) != null)
      {
         windowState = WindowState.create(queryParameters.get(ControllerRequestParameterNames.WINDOW_STATE));
      }

      //
      String navigationalStateString = queryParameters.get(ControllerRequestParameterNames.NAVIGATIONAL_STATE);
      StateString navigationalState = null;
      if (navigationalStateString != null)
      {
         navigationalState = StateString.create(navigationalStateString);
      }

      //
      PortletWindowNavigationalState windowNavigationalState = new PortletWindowNavigationalState(navigationalState, mode, windowState);

      //
      String phase = queryParameters.get(ControllerRequestParameterNames.LIFECYCLE_PHASE);
      if (ControllerRequestParameterNames.RESOURCE_PHASE.equals(phase))
      {
         StateString resourceState = StateString.create(queryParameters.get(ControllerRequestParameterNames.RESOURCE_STATE));
         String resourceId = queryParameters.get(ControllerRequestParameterNames.RESOURCE_ID);

         //
         CacheLevel resourceCacheLevel = CacheLevel.valueOf(queryParameters.get(ControllerRequestParameterNames.RESOURCE_CACHEABILITY));

         //
         PortletResourceRequest.Scope scope;
         switch (resourceCacheLevel)
         {
            case FULL:
               scope = new PortletResourceRequest.FullScope();
               break;
            case PORTLET:
               scope = new PortletResourceRequest.PortletScope(windowNavigationalState);
               break;
            case PAGE:
               scope = new PortletResourceRequest.PageScope(windowNavigationalState, pageNavigationalState);
               break;
            default:
               throw new AssertionError();
         }

         //
         return new PortletResourceRequest(
            windowId,
            resourceId,
            resourceState,
            formParameterMap,
            scope);
      }
      else
      {
         if (ControllerRequestParameterNames.ACTION_PHASE.equals(phase))
         {
            StateString interactionState = StateString.create(queryParameters.get(ControllerRequestParameterNames.INTERACTION_STATE));

            //
            return new PortletActionRequest(
               windowId,
               interactionState,
               formParameterMap,
               windowNavigationalState,
               pageNavigationalState);
         }
         else
         {
            byte[] bytes = Base64.decode(queryParameters.get(ControllerRequestParameterNames.PUBLIC_NAVIGATIONAL_STATE_CHANGES), true);
            Map<String, String[]> publicNavigationalStateChanges = IOTools.unserialize(Serialization.PARAMETER_MAP, SerializationFilter.COMPRESSOR, bytes);

            //
            return new PortletRenderRequest(
               windowId,
               windowNavigationalState,
               publicNavigationalStateChanges,
               pageNavigationalState);
         }
      }
   }

}
