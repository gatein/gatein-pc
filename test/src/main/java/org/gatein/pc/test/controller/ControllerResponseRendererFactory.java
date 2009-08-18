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

package org.gatein.pc.test.controller;

import org.gatein.pc.controller.response.ControllerResponse;
import org.gatein.pc.controller.response.PageUpdateResponse;
import org.gatein.pc.controller.response.PortletResponse;
import org.gatein.pc.controller.response.ResourceResponse;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.HTTPRedirectionResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.invocation.response.ContentResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ControllerResponseRendererFactory
{

   /** . */
   private boolean sendNoContentResponseOnEmptyResource;

   /** . */
   private boolean sendErrorOnProcessActionError;

   /** The page navigational state if there is one in the request. */
   private PortletPageNavigationalState requestPageNavigationalState;

   /** . */
   private StateControllerContext stateControllerContext;

   public ControllerResponseRendererFactory(
      boolean sendNoContentResponseOnEmptyResource,
      boolean sendErrorOnProcessActionError,
      StateControllerContext stateControllerContext,
      PortletPageNavigationalState requestPageNavigationalState)
   {
      this.sendNoContentResponseOnEmptyResource = sendNoContentResponseOnEmptyResource;
      this.sendErrorOnProcessActionError = sendErrorOnProcessActionError;
      this.stateControllerContext = stateControllerContext;
      this.requestPageNavigationalState = requestPageNavigationalState;
   }

   public Renderer getRenderer(ControllerResponse response)
   {
      if (response instanceof PageUpdateResponse)
      {
         PageUpdateResponse pageUpdate = (PageUpdateResponse)response;

         //
         return new PageRenderer(pageUpdate.getProperties(), pageUpdate.getPageNavigationalState());
      }
      else if (response instanceof PortletResponse)
      {
         return getRenderer(((PortletResponse)response).getResponse());
      }
      else if (response instanceof ResourceResponse)
      {
         ResourceResponse resourceResponse = (ResourceResponse)response;

         //
         if (resourceResponse.getResponse() instanceof ContentResponse)
         {
            return new ResourceRenderer((ContentResponse)resourceResponse.getResponse(), sendNoContentResponseOnEmptyResource);
         }
         else
         {
            return getRenderer(((PortletResponse)response).getResponse());
         }
      }

      //
      throw new IllegalArgumentException("Unknown response type: " + response);
   }

   private Renderer getRenderer(PortletInvocationResponse response)
   {
      if (response instanceof HTTPRedirectionResponse)
      {
         return new RedirectResponseRenderer((HTTPRedirectionResponse)response);
      }
      else if (response instanceof ErrorResponse)
      {
         if (sendErrorOnProcessActionError)
         {
            return new ErrorResponseRenderer((ErrorResponse)response);
         }
         else
         {
            return new PageRenderer(new ResponseProperties(), requestPageNavigationalState != null ? requestPageNavigationalState : stateControllerContext.createPortletPageNavigationalState(false));
         }
      }

      //
      throw new IllegalArgumentException("Unknown response type: " + response);
   }

}
