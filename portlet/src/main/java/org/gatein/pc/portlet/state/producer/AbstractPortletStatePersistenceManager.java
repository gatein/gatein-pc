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

package org.gatein.pc.portlet.state.producer;

import org.gatein.common.util.ParameterValidation;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.portlet.state.SimplePropertyMap;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public abstract class AbstractPortletStatePersistenceManager implements PortletStatePersistenceManager
{
   public PortletStateContext loadState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(stateId, "state id", null);

      PortletStateContext context = getStateContext(stateId);
      if (context == null)
      {
         throw new NoSuchStateException(stateId);
      }
      return context;
   }

   protected PortletState getState(String stateId) throws NoSuchStateException, InvalidStateIdException
   {
      PortletStateContext context = loadState(stateId);
      return context.getState();
   }

   public String createState(String portletId, PropertyMap propertyMap)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(portletId, "portlet id", null);
      ParameterValidation.throwIllegalArgExceptionIfNull(propertyMap, "property map");

      return createStateContext(portletId, propertyMap);
   }

   public String cloneState(String stateId, PropertyMap propertyMap) throws NoSuchStateException, InvalidStateIdException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(propertyMap, "property map");

      PortletState stateContext = getState(stateId);
      return createState(stateContext.getPortletId(), propertyMap);
   }

   public String cloneState(String stateId) throws IllegalArgumentException, NoSuchStateException, InvalidStateIdException
   {
      PortletState state = getState(stateId);
      return createState(state.getPortletId(), new SimplePropertyMap(state.getProperties()));
   }

   public void updateState(String stateId, PropertyMap propertyMap) throws NoSuchStateException, InvalidStateIdException
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(propertyMap, "property map");

      PortletStateContext ctx = loadState(stateId);
      PortletState state = ctx.getState();
      PropertyMap props = state.getProperties();
      props.clear();
      props.putAll(propertyMap);
      updateStateContext(ctx);
   }

   public void destroyState(String stateId) throws InvalidStateIdException, NoSuchStateException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(stateId, "state id", null);

      if (destroyStateContext(stateId) == null)
      {
         throw new NoSuchStateException(stateId);
      }
   }

   protected abstract PortletStateContext getStateContext(String stateId) throws InvalidStateIdException;

   protected abstract String createStateContext(String portletId, PropertyMap propertyMap);

   protected abstract PortletStateContext destroyStateContext(String stateId) throws InvalidStateIdException;

   protected abstract void updateStateContext(PortletStateContext stateContext);
}
