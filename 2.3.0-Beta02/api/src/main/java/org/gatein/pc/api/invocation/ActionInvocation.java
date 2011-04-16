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
package org.gatein.pc.api.invocation;

import org.gatein.pc.api.spi.RequestContext;
import org.gatein.pc.api.spi.PortletInvocationContext;
import org.gatein.pc.api.StateString;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class ActionInvocation extends PortletInvocation
{

   /** The request context. */
   protected RequestContext requestContext;

   /** . */
   protected StateString interactionState;

   /** . */
   protected Map<String, String[]> form;

   public ActionInvocation(PortletInvocationContext ctx) throws IllegalArgumentException
   {
      super(ctx);
   }

   public StateString getInteractionState()
   {
      return interactionState;
   }

   public void setInteractionState(StateString interactionState)
   {
      this.interactionState = interactionState;
   }

   public Map<String, String[]> getForm()
   {
      return form;
   }

   public void setForm(Map<String, String[]> form)
   {
      this.form = form;
   }

   public RequestContext getRequestContext()
   {
      return requestContext;
   }

   public void setRequestContext(RequestContext requestContext)
   {
      this.requestContext = requestContext;
   }
}
