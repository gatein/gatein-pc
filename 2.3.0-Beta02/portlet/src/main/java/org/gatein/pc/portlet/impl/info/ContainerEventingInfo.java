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
package org.gatein.pc.portlet.impl.info;

import org.gatein.pc.api.info.EventingInfo;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerEventingInfo implements EventingInfo
{

   /** . */
   private final Map<QName, ContainerEventInfo> producedEvents;

   /** . */
   private final Map<QName, ContainerEventInfo> consumedEvents;

   public ContainerEventingInfo()
   {
      producedEvents = new HashMap<QName, ContainerEventInfo>();
      consumedEvents = new HashMap<QName, ContainerEventInfo>();
   }

   public void addProducedEvent(ContainerEventInfo producedEvent)
   {
      producedEvents.put(producedEvent.getName(), producedEvent);
   }

   public void addConsumedEvent(ContainerEventInfo consumedEvent)
   {
      consumedEvents.put(consumedEvent.getName(), consumedEvent);
   }

   public Map<QName, ContainerEventInfo> getProducedEvents()
   {
      return Collections.unmodifiableMap(producedEvents);
   }

   public Map<QName, ContainerEventInfo> getConsumedEvents()
   {
      return Collections.unmodifiableMap(consumedEvents);
   }
}
