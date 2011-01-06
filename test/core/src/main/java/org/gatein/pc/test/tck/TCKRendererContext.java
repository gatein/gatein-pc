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

import org.gatein.pc.test.controller.AbstractRendererContext;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.NoSuchPortletException;

import java.util.Collection;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class TCKRendererContext extends AbstractRendererContext
{

   /** . */
   private final Collection<Portlet> involvedPortlets;

   public TCKRendererContext(
      TCKPortletControllerContext portletControllerContext,
      TCKPortletPageNavigationalState tckPageNavigationalState) throws PortletInvokerException
   {
      super(portletControllerContext);

      //
      Collection<Portlet> involvedPortlets = new ArrayList<Portlet>();

      // Page state could be null for some requests
      if (tckPageNavigationalState != null)
      {
         for (String involvedPortletId : tckPageNavigationalState.getInvolvedPortlets())
         {
            try
            {
               Portlet involvedPortlet = portletControllerContext.getPortlet(involvedPortletId);
               involvedPortlets.add(involvedPortlet);
            }
            catch (NoSuchPortletException e)
            {
               // It happen when a portlet becomes unavailable and
               // therefore is removed from the available portlet
               // in that case it should not prevent the other portlets to be
               // rendered
            }
         }
      }

      //
      this.involvedPortlets = involvedPortlets;
   }

   public Collection<Portlet> getPortlets()
   {
      return involvedPortlets;
   }
}
