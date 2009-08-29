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

import org.gatein.pc.portlet.state.consumer.ConsumerPersistenceManager;
import org.gatein.pc.portlet.state.consumer.ConsumerStateContext;
import org.gatein.pc.portlet.state.consumer.ConsumerState;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.portlet.state.InvalidStateIdException;

import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractConsumerPersistenceManagerService<S extends Serializable> implements ConsumerPersistenceManager<S>
{

   /** . */
   private int counter = 0;

   protected abstract ConsumerStateContext<S> get(String stateId);
   protected abstract void put(String stateId, ConsumerStateContext<S> state);
   protected abstract void remove(String stateId);
   protected abstract int size();

   public synchronized ConsumerStateContext<S> loadState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      checkId(stateId);
      ConsumerStateContext<S> state = get(stateId);
      if (state == null)
      {
         throw new NoSuchStateException(stateId);
      }
      return state;
   }

   public synchronized String createState(ConsumerState<S> state) throws IllegalArgumentException
   {
      if (state == null)
      {
         throw new IllegalArgumentException();
      }
      ConsumerStateContext<S> ctx = new ConsumerStateContext<S>(Integer.toString(counter++), state.getPortletId(), state.getStateType(), state.getState());
      put(ctx.getId(), ctx);
      return ctx.getId();
   }

   public synchronized void updateState(String stateId, ConsumerState<S> state) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      checkId(stateId);
      if (state == null)
      {
         throw new IllegalArgumentException();
      }
      if (get(stateId) == null)
      {
         throw new NoSuchStateException(stateId);
      }

      //
      put(stateId, new ConsumerStateContext<S>(stateId, state.getPortletId(), state.getStateType(), state.getState()));
   }

   public synchronized void destroyState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      checkId(stateId);
      if (get(stateId) != null)
      {
         throw new NoSuchStateException(stateId);
      }

      //
      remove(stateId);
   }

   public synchronized int getSize()
   {
      return size();
   }

   private void checkId(String stateId) throws IllegalArgumentException, InvalidStateIdException
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
   }
}
