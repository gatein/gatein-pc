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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.common.NotYetImplemented;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;

import javax.portlet.ActionResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7379 $
 */
public class ActionResponseImpl extends StateAwareResponseImpl implements ActionResponse
{

   /** . */
   protected PortletInvocationResponse response;

   public ActionResponseImpl(ActionInvocation invocation, PortletRequestImpl preq)
   {
      super(invocation, preq);

      //
      UpdateNavigationalStateResponse rr = new UpdateNavigationalStateResponse();
      rr.setNavigationalState(ParametersStateString.create());

      //
      this.response = rr;
   }

   public void sendRedirect(String s, String s1) throws IOException
   {
      throw new NotYetImplemented();
   }

   public void sendRedirect(String location) throws IOException
   {
      checkRedirect("sendRedirect cannot be called after " +
                  "setPortletMode/setWindowState/setRenderParameter/setRenderParameters " +
                  "has been called previously");

      //
      if (location == null)
      {
         // Do something more clever than simply returning
         return;
      }

      //
      if (location.startsWith("http://") || location.startsWith("https://") || location.startsWith("/"))
      {
         WantRedirect redirect = requireRedirect();

         //
         redirect.location = location;
      }
      else
      {
         throw new IllegalArgumentException("URL must be absolute. Was: " + location);
      }
   }
}
