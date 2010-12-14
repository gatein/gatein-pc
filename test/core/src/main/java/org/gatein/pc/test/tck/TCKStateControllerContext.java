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
package org.gatein.pc.test.tck;

import org.gatein.pc.controller.state.StateControllerContext;
import org.gatein.pc.controller.state.PortletPageNavigationalState;

import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class TCKStateControllerContext implements StateControllerContext
{

   final StateControllerContext defaultStateControllerContext;

   public TCKStateControllerContext(StateControllerContext defaultStateControllerContext)
   {
      this.defaultStateControllerContext = defaultStateControllerContext;
   }

   public PortletPageNavigationalState clonePortletPageNavigationalState(PortletPageNavigationalState portletPageNavigationalState, boolean modifiable)
   {
      TCKPortletPageNavigationalState tckPageNavigationalState = (TCKPortletPageNavigationalState)portletPageNavigationalState;

      //
      return new TCKPortletPageNavigationalState(
         defaultStateControllerContext.clonePortletPageNavigationalState(tckPageNavigationalState.defaultState, modifiable),
         new HashSet<String>(tckPageNavigationalState.involvedPortlets));
   }

   public PortletPageNavigationalState createPortletPageNavigationalState(boolean modifiable)
   {
      return new TCKPortletPageNavigationalState(defaultStateControllerContext.createPortletPageNavigationalState(modifiable), new HashSet<String>());
   }
}
