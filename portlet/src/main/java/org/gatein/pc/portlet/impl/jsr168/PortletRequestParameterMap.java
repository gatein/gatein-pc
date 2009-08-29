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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.info.NavigationInfo;

import java.util.Iterator;
import java.util.Map;

/**
 * The main responsibility of this class is to combine the different parameter sources (private navigational state,
 * public navigational state, interaction state, form) into the private map, public map and parameter map.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletRequestParameterMap
{

   /** . */
   protected static final ParameterMap.AccessMode COPY_MODE = ParameterMap.AccessMode.get(true, true);

   /**
    * Factory method that will chose the right builder according to the context argument type.
    */
   public static PortletRequestParameterMap create(NavigationInfo navigationInfo, PortletInvocation invocation)
   {
      if (invocation instanceof EventInvocation)
      {
         return create(navigationInfo, (EventInvocation)invocation);
      }
      else if (invocation instanceof ActionInvocation)
      {
         return create(navigationInfo, (ActionInvocation)invocation);
      }
      else if (invocation instanceof RenderInvocation)
      {
         return create(navigationInfo, (RenderInvocation)invocation);
      }
      else
      {
         return ResourceRequestParameterMap.create(navigationInfo, (ResourceInvocation)invocation);
      }
   }

   public static PortletRequestParameterMap create(NavigationInfo navigationInfo, EventInvocation invocation)
   {
      // Build public parameters
      ParameterMap publicParameters = safeBuildPublicParameters(navigationInfo, invocation.getPublicNavigationalState());

      // Get render parameters
      Map<String, String[]> privateParameters = safeBuildParameters(invocation.getNavigationalState());

      // Build combined map
      Map<String, String[]> parameters = safeCombine(privateParameters, publicParameters);

      //
      return new PortletRequestParameterMap(
         parameters,
         privateParameters,
         publicParameters);
   }

   public static PortletRequestParameterMap create(NavigationInfo navigationInfo, RenderInvocation context)
   {
      // Build public parameters
      ParameterMap publicParameters = safeBuildPublicParameters(navigationInfo, context.getPublicNavigationalState());

      // Get render parameters
      Map<String, String[]> privateParameters = safeBuildParameters(context.getNavigationalState());

      // Build combined map
      Map<String, String[]> parameters = safeCombine(privateParameters, publicParameters);

      //
      return new PortletRequestParameterMap(
         parameters,
         privateParameters,
         publicParameters);
   }

   /**
    * - interaction parameter : always
    * - form parameter : if it is a POST with the content type set to application/...
    * - public render parameter : optionally sent by the consumer

    * - getParameterMap() : the interaction parameter + form parameters if any + public render parameter
    * - getPrivateParameterMap() : the interaction parameter + form parameter
    * - getPublicParameterMap() : public render parameter
    */
   public static PortletRequestParameterMap create(NavigationInfo navigationInfo, ActionInvocation invocation)
   {
      // Build public parameters
      ParameterMap publicParameters = safeBuildPublicParameters(navigationInfo, invocation.getPublicNavigationalState());

      // The private parameters
      Map<String, String[]> privateParameters = safeBuildParameters(invocation.getInteractionState());

      // Combine form if we have one
      privateParameters = safeCombine(privateParameters, invocation.getForm());

      // Combine to get shared map
      Map<String, String[]> parameters = safeCombine(privateParameters, publicParameters);

      //
      return new PortletRequestParameterMap(
         parameters,
         privateParameters,
         publicParameters);
   }

   /** . */
   protected final ParameterMap parameters;

   /** . */
   protected final ParameterMap privateParameters;

   /** . */
   protected final ParameterMap publicParameters;

   protected PortletRequestParameterMap(
      Map<String, String[]> parameters,
      Map<String, String[]> privateParameters,
      Map<String, String[]> publicParameters)
   {
      if (privateParameters != null)
      {
         this.privateParameters = ParameterMap.wrap(privateParameters, COPY_MODE);
      }
      else
      {
         this.privateParameters = null;
      }

      //
      if (publicParameters != null)
      {
         this.publicParameters = ParameterMap.wrap(publicParameters, COPY_MODE);
      }
      else
      {
         this.publicParameters = null;
      }

      //
      if (parameters != null)
      {
         this.parameters = ParameterMap.wrap(parameters, COPY_MODE);
      }
      else
      {
         this.parameters = null;
      }
   }

   public ParameterMap getParameters()
   {
      return parameters;
   }

   public ParameterMap getPrivateParameters()
   {
      return privateParameters;
   }

   public ParameterMap getPublicParameters()
   {
      return publicParameters;
   }

   protected static ParameterMap safeBuildPublicParameters(NavigationInfo navigationInfo, Map<String, String[]> publicNavigationalParameters)
   {
      if (publicNavigationalParameters != null)
      {
         // Clone the public navigational state with the copy mode
         ParameterMap publicParameters = ParameterMap.clone(publicNavigationalParameters);

         // Remove any parameter that would have been sent by the producer by mistake
         for (Iterator<String> i = publicParameters.keySet().iterator(); i.hasNext();)
         {
            String name = i.next();
            if (navigationInfo.getPublicParameter(name) == null)
            {
               i.remove();
            }
         }

         //
         return publicParameters;
      }
      else
      {
         return null;
      }
   }

   protected static Map<String, String[]> safeBuildParameters(StateString stateString)
   {
      if (stateString != null)
      {
         return ParametersStateString.create(stateString).getParameters();
      }
      else
      {
         return null;
      }
   }

   protected static Map<String, String[]> safeCombine(Map<String, String[]> privateParameters, Map<String, String[]> publicParameters)
   {
      if (publicParameters != null)
      {
         if (privateParameters != null)
         {
            ParameterMap parameters = ParameterMap.clone(privateParameters);
            parameters.append(publicParameters);
            return parameters;
         }
         else
         {
            return publicParameters;
         }
      }
      else
      {
         if (privateParameters != null)
         {
            return privateParameters;
         }
         else
         {
            return null;
         }
      }
   }
}
