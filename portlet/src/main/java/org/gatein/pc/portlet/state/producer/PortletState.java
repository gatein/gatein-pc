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
package org.gatein.pc.portlet.state.producer;

import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.state.SimplePropertyMap;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5873 $
 */
@SuppressWarnings("serial")
public class PortletState implements Serializable
{

   /** . */
   private String portletId;

   /** . */
   private PropertyMap properties;

   /** . */
   private Date terminationTime;

   public PortletState(String portletId)
   {
      this(portletId, new SimplePropertyMap());
   }

   public PortletState(String portletId, PropertyMap properties)
   {
      if (portletId == null)
      {
         throw new IllegalArgumentException();
      }
      if (properties == null)
      {
         throw new IllegalArgumentException();
      }
      this.portletId = portletId;
      this.properties = properties;
      this.terminationTime = null;
   }

   public String getPortletId()
   {
      return portletId;
   }

   public PropertyMap getProperties()
   {
      return properties;
   }

   public Date getTerminationTime()
   {
      return terminationTime;
   }

   public void setTerminationTime(Date terminationTime)
   {
      this.terminationTime = terminationTime;
   }
}
