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
package org.gatein.pc.controller.event;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An event associated with a portlet window.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletWindowEvent
{

   private static final AtomicLong generator = new AtomicLong();

   /** . */
   private final QName name;

   /** . */
   private final Serializable payload;

   /** . */
   private final String windowId;

   /** . */
   private final long serialNumber = generator.incrementAndGet();

   public PortletWindowEvent(QName name, Serializable payload, String windowId)
   {
      if (name == null)
      {
         throw new IllegalArgumentException();
      }
      if (windowId == null)
      {
         throw new IllegalArgumentException();
      }
      this.name = name;
      this.payload = payload;
      this.windowId = windowId;
   }

   public QName getName()
   {
      return name;
   }

   public Serializable getPayload()
   {
      return payload;
   }

   public String getWindowId()
   {
      return windowId;
   }

   public long getSerialNumber()
   {
      return serialNumber;
   }

   public String toString()
   {
      return "Event[name=" + name + ",windowId=" + windowId + ",payload=" + payload + ",serialNumber=" + serialNumber + "]";
   }
}
