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
import org.gatein.pc.api.info.NavigationInfo;
import org.gatein.pc.api.invocation.ResourceInvocation;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ResourceRequestParameterMap extends PortletRequestParameterMap
{

   /** . */
   protected final ParameterMap privateRenderParameters;

   protected ResourceRequestParameterMap(
      Map<String, String[]> parameters,
      Map<String, String[]> privateParameters,
      Map<String, String[]> publicParameters,
      Map<String, String[]> privateRenderParameters)
   {
      super(parameters, privateParameters, publicParameters);

      //
      if (privateRenderParameters != null)
      {
         this.privateRenderParameters = ParameterMap.wrap(privateRenderParameters, COPY_MODE);
      }
      else
      {
         this.privateRenderParameters = null;
      }
   }

   public ParameterMap getPrivateRenderParameters()
   {
      return privateRenderParameters;
   }

   /**
    * - resource parameter : always
    * - form parameter : if it is a POST with the content type set to application/...
    * - render parameter : depends on the cacheability of the request
    * - public render parameter : optionally sent by the consumer

    * - getParameterMap() : the resource parameter + form parameters if any + render parameter + public render parameter
    * - getPrivateParameterMap() : the resource parameter + form parameter
    * - getPublicParameterMap() : public render parameter
    * - getPrivateRenderParameterMap() : render parameter
    */
   public static ResourceRequestParameterMap create(NavigationInfo navigationInfo, ResourceInvocation invocation)
   {
      // Build public parameters
      ParameterMap publicParameters = safeBuildPublicParameters(navigationInfo, invocation.getPublicNavigationalState());

      // Combine private render parameters if any
      Map<String, String[]> privateRenderParameters = safeBuildParameters(invocation.getNavigationalState());

      // The private parameters
      Map<String, String[]> privateParameters = safeBuildParameters(invocation.getResourceState());

      // Combine form if we have one
      privateParameters = safeCombine(privateParameters, invocation.getForm());

      // Combien with private render parameters
      privateParameters = safeCombine(privateParameters, privateRenderParameters);

      // Combine to get shared map
      Map<String, String[]> parameters = safeCombine(privateParameters, publicParameters);

      //
      return new ResourceRequestParameterMap(
         parameters,
         privateParameters,
         publicParameters,
         privateRenderParameters);
   }
}
