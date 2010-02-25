/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.portlet.impl.state.producer;

import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.producer.AbstractPortletStatePersistenceManager;
import org.gatein.pc.portlet.state.producer.PortletStateContext;

import java.util.HashMap;
import java.util.Map;

/**
 * An in memory implementation of the producer state persistence manager.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7215 $
 */
public class PortletStatePersistenceManagerService extends AbstractPortletStatePersistenceManager
{

   /** . */
   private Map<String, PortletStateContext> store = new HashMap<String, PortletStateContext>();

   /** . */
   private int counter = 0;

   @Override
   protected synchronized PortletStateContext getStateContext(String stateId) throws InvalidStateIdException
   {
      checkId(stateId);

      return store.get(stateId);
   }

   @Override
   protected synchronized String createStateContext(String portletId, PropertyMap propertyMap)
   {
      String id = Integer.toString(counter++);
      PortletStateContext state = new PortletStateContextImpl(id, portletId, new SimplePropertyMap(propertyMap));
      store.put(id, state);
      return id;
   }

   @Override
   protected synchronized PortletStateContext destroyStateContext(String stateId) throws InvalidStateIdException
   {
      checkId(stateId);

      return store.remove(stateId);
   }

   private void checkId(String stateId) throws InvalidStateIdException
   {
      try
      {
         Integer.parseInt(stateId);
      }
      catch (NumberFormatException e)
      {
         throw new InvalidStateIdException(e, stateId);
      }
   }

   @Override
   protected void updateStateContext(PortletStateContext stateContext)
   {
      // nothing to do here
   }

   public synchronized int getSize()
   {
      return store.size();
   }
}
