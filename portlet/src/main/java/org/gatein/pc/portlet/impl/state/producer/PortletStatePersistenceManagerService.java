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
package org.gatein.pc.portlet.impl.state.producer;

import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.producer.PortletStatePersistenceManager;
import org.gatein.pc.portlet.state.producer.PortletStateContext;
import org.gatein.pc.portlet.state.producer.PortletState;

import java.util.HashMap;
import java.util.Map;

/**
 * An in memory implementation of the producer state persistence manager.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7215 $
 */
public class PortletStatePersistenceManagerService implements PortletStatePersistenceManager
{

   /** . */
   private Map<String, PortletStateContext> store = new HashMap<String, PortletStateContext>();

   /** . */
   private int counter = 0;

   public synchronized PortletStateContext loadState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      if (stateId == null)
      {
         throw new IllegalArgumentException("No null state id accepted");
      }
      try
      {
         Integer.parseInt(stateId);
      }
      catch (NumberFormatException e)
      {
         throw new InvalidStateIdException(e, stateId);
      }
      PortletStateContext context = store.get(stateId);
      if (context == null)
      {
         throw new NoSuchStateException(stateId);
      }
      return context;
   }

   private synchronized PortletState getState(String stateId) throws NoSuchStateException, InvalidStateIdException
   {
      PortletStateContext context = loadState(stateId);
      return context.getState();
   }

   public synchronized String createState(String portletId, PropertyMap propertyMap)
   {
      if (portletId == null)
      {
         throw new IllegalArgumentException("No null portlet id accepted");
      }
      if (propertyMap == null)
      {
         throw new IllegalArgumentException("No null value map accepted");
      }
      String id = Integer.toString(counter++);
      PortletStateContext state = new PortletStateContextImpl(id, portletId, new SimplePropertyMap(propertyMap));
      store.put(id, state);
      return id;
   }

   public synchronized String cloneState(String stateId, PropertyMap propertyMap) throws NoSuchStateException, InvalidStateIdException
   {
      if (propertyMap == null)
      {
         throw new IllegalArgumentException();
      }
      PortletState stateContext = getState(stateId);
      return createState(stateContext.getPortletId(), propertyMap);
   }

   public String cloneState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      PortletState state = getState(stateId);
      return createState(state.getPortletId(), new SimplePropertyMap(state.getProperties()));
   }

   public synchronized void updateState(String stateId, PropertyMap propertyMap) throws NoSuchStateException, InvalidStateIdException
   {
      if (propertyMap == null)
      {
         throw new IllegalArgumentException("No null value map");
      }
      PortletState ctx = getState(stateId);
      ctx.getProperties().clear();
      ctx.getProperties().putAll(propertyMap);
   }

   public synchronized void destroyState(String stateId) throws InvalidStateIdException, NoSuchStateException
   {
      if (stateId == null)
      {
         throw new IllegalArgumentException();
      }
      try
      {
         Integer.parseInt(stateId);
      }
      catch (NumberFormatException e)
      {
         throw new InvalidStateIdException(e, stateId);
      }
      if (store.remove(stateId) == null)
      {
         throw new NoSuchStateException(stateId);
      }
   }

   public synchronized int getSize()
   {
      return store.size();
   }
}
