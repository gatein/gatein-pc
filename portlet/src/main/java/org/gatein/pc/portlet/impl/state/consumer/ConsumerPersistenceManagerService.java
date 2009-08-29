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
package org.gatein.pc.portlet.impl.state.consumer;

import org.gatein.pc.portlet.state.consumer.ConsumerStateContext;
import org.gatein.pc.api.PortletStateType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ConsumerPersistenceManagerService extends AbstractConsumerPersistenceManagerService<byte[]>
{

   /** . */
   private Map<String, ConsumerStateContext<byte[]>> store = new HashMap<String, ConsumerStateContext<byte[]>>();

   public PortletStateType<byte[]> getStateType() {
      return PortletStateType.OPAQUE;
   }

   protected ConsumerStateContext<byte[]> get(String stateId)
   {
      return store.get(stateId);
   }

   protected void put(String stateId, ConsumerStateContext<byte[]> state)
   {
      store.put(stateId, state);
   }

   protected void remove(String stateId)
   {
      store.remove(stateId);
   }

   protected int size()
   {
      return store.size();
   }
}
