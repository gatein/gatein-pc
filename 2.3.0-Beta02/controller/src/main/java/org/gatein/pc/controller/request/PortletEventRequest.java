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
package org.gatein.pc.controller.request;

import org.gatein.pc.controller.state.PortletWindowNavigationalState;
import org.gatein.pc.controller.state.PortletPageNavigationalState;

import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * An event request. The purpose of this type of request is to allow
 * the portal to fire events to a portlet.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletEventRequest extends PortletRequest
{

   /** . */
   private final QName name;

   /** . */
   private final Serializable payload;

   public PortletEventRequest(
      String windowId,
      PortletWindowNavigationalState windowNavigationalState,
      PortletPageNavigationalState pageNavigationalState,
      QName name,
      Serializable payload)
   {
      super(windowId, windowNavigationalState, pageNavigationalState);

      //
      this.name = name;
      this.payload = payload;
   }

   public QName getName()
   {
      return name;
   }

   public Serializable getPayload()
   {
      return payload;
   }
}
