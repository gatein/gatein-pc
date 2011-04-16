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
package org.gatein.pc.portlet.state.consumer;

import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.api.PortletStateType;

import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5776 $
 */
public interface ConsumerPersistenceManager<S extends Serializable>
{

  /**
   * Returns the consumer state type.
   *
   * @return the consume state type
   */
   PortletStateType<S> getStateType();

   /**
    * Load the state.
    *
    * @param stateId the state id
    * @return the value map or null if it does not exist
    * @throws IllegalArgumentException if the state id is null
    * @throws NoSuchStateException     is the specified state does not exist
    * @throws InvalidStateIdException  if the state id is not valid
    */
   ConsumerStateContext<S> loadState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException;

   /**
    * Create the initial state.
    *
    * @param state the state
    * @return the id of the state created
    * @throws IllegalArgumentException if the portlet id is null
    */
   String createState(ConsumerState<S> state) throws IllegalArgumentException;

   /**
    * Update the state.
    *
    * @param stateId the state id
    * @param propertyMap the updated state
    * @throws IllegalArgumentException if the state id is null or the values are null
    * @throws NoSuchStateException     is the specified state does not exist
    * @throws InvalidStateIdException  if the state id is not valid
    */
   void updateState(String stateId, ConsumerState<S> propertyMap) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException;

   /**
    * Destroy the state.
    *
    * @param stateId the state id
    * @throws IllegalArgumentException if the state id is null
    * @throws NoSuchStateException     is the specified state does not exist
    * @throws InvalidStateIdException  if the state id is not valid
    */
   void destroyState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException;
}
