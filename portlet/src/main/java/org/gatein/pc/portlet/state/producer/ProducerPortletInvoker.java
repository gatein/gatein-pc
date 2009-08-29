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

import org.gatein.common.invocation.InvocationException;
import org.gatein.pc.api.InvalidPortletIdException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StateEvent;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.PreferenceInfo;
import org.gatein.pc.api.info.PreferencesInfo;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.InstanceContext;
import org.gatein.pc.portlet.state.AbstractPropertyContext;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.StateConversionException;
import org.gatein.pc.portlet.state.StateConverter;
import org.gatein.pc.portlet.state.StateManagementPolicy;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6765 $
 */
public class ProducerPortletInvoker extends PortletInvokerInterceptor
{

   /** . */
   private static final String PRODUCER_CLONE_ID_PREFIX = "_";

   /** . */
   private static final String CONSUMER_CLONE_ID = "_dumbvalue";

   /** . */
   private PortletStatePersistenceManager persistenceManager;

   /** . */
   private StateManagementPolicy stateManagementPolicy;

   /** . */
   private StateConverter stateConverter;

   /** . */
   private Logger log = Logger.getLogger(ProducerPortletInvoker.class);

   public PortletStatePersistenceManager getPersistenceManager()
   {
      return persistenceManager;
   }

   public void setPersistenceManager(PortletStatePersistenceManager ppm)
   {
      this.persistenceManager = ppm;
   }

   public StateManagementPolicy getStateManagementPolicy()
   {
      return stateManagementPolicy;
   }

   public void setStateManagementPolicy(StateManagementPolicy stateManagementPolicy)
   {
      this.stateManagementPolicy = stateManagementPolicy;
   }

   public StateConverter getStateConverter()
   {
      return stateConverter;
   }

   public void setStateConverter(StateConverter stateConverter)
   {
      this.stateConverter = stateConverter;
   }

   public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      return _getPortlet(portletContext);
   }

   private <S extends Serializable> Portlet _getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {

      if (portletContext == null)
      {
         throw new IllegalArgumentException("No null portlet id accepted");
      }

      //
      String portletId = portletContext.getId();

      //
      if (CONSUMER_CLONE_ID.equals(portletId))
      {
         if (portletContext instanceof StatefulPortletContext)
         {
            StatefulPortletContext<S> statefulPortletContext = (StatefulPortletContext)portletContext;
            S state = statefulPortletContext.getState();

            //
            try
            {
               PortletState portletState = stateConverter.unmarshall(statefulPortletContext.getType(), state);
               Portlet delegate = super.getPortlet(PortletContext.createPortletContext(portletState.getPortletId()));
               return new ProducerPortlet(portletContext, delegate);
            }
            catch (StateConversionException e)
            {
               throw new PortletInvokerException(e);
            }
         }
         else
         {
            throw new InvalidPortletIdException("", portletId);
         }
      }
      else if (portletId.startsWith(PRODUCER_CLONE_ID_PREFIX))
      {
         try
         {
            String stateId = portletId.substring(PRODUCER_CLONE_ID_PREFIX.length());
            PortletStateContext stateContext = persistenceManager.loadState(stateId);
            PortletState state = stateContext.getState();
            Portlet delegate = super.getPortlet(PortletContext.createPortletContext(state.getPortletId()));
            return new ProducerPortlet(portletContext, delegate);
         }
         catch (NoSuchStateException e)
         {
            throw new NoSuchPortletException(e, portletId);
         }
         catch (InvalidStateIdException e)
         {
            throw new InvalidPortletIdException(e, portletId);
         }
      }
      else
      {
         return super.getPortlet(portletContext);
      }
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws PortletInvokerException, InvocationException
   {
      // Get the context of the portlet that the client want to use
      final PortletContext portletContext = invocation.getTarget();
      if (portletContext == null)
      {
         throw new InvocationException("No portlet context provided");
      }

      // Get the access mode
      InstanceContext instanceCtx = invocation.getInstanceContext();
      AccessMode access = instanceCtx.getAccessMode();

      // Get a state contxt for the portlet context
      InternalContext context = getStateContext(portletContext);

      // If it is a producer offered portlet we consider read-write as read-only
      if (!context.isStateful() && access == AccessMode.READ_WRITE)
      {
         access = AccessMode.READ_ONLY;
      }

      // Get the portlet container and set it on invocation
      Portlet portlet = super.getPortlet(context.getPortletContext());
      if (portlet == null)
      {
         throw new NoSuchPortletException("Portlet " + context.getPortletContext() + " not found", context.getPortletId());
      }

      // Create prefs
      AbstractPropertyContext prefs = new AbstractPropertyContext(
         access,
         context.isStateful() ? ((StatefulContext)context).getProperties() : null,
         invocation instanceof RenderInvocation);

      //
      PortletInvocationResponse response;
      try
      {
         invocation.setTarget(context.getPortletContext());
         invocation.setAttribute(PropertyContext.PREFERENCES_ATTRIBUTE, prefs);

         // Invoke
         response = super.invoke(invocation);
      }
      finally
      {
         invocation.setTarget(portletContext);
         invocation.removeAttribute(PropertyContext.PREFERENCES_ATTRIBUTE);
      }

      //
      int status = prefs.getStatus();
            
      // Producer state management if the invocation was succesful
      if ((invocation instanceof ActionInvocation || invocation instanceof ResourceInvocation || invocation instanceof EventInvocation) && status == AbstractPropertyContext.UPDATE_SUCCESSFUL)
      {
         // Get the potentially updated prefs
         PropertyMap newPrefs = prefs.getPrefs();

         //
         PortletStateType stateType = instanceCtx.getStateType();
         boolean persistLocally;
         if (stateType == null)
         {
            persistLocally = true;
         }
         else
         {
            persistLocally = stateManagementPolicy.persistLocally();
         }

         //
         switch(access)
         {
            case CLONE_BEFORE_WRITE:
            {
               // Create the state
               if (context.isStateful())
               {
                  StatefulContext statefulContext = (StatefulContext)context;
                  if (persistLocally)
                  {
                     try
                     {
                        // The state id should be ok as it was used before to load the state
                        LocalContext localContext = (LocalContext)statefulContext;
                        String portletStateId = localContext.getStateId();
                        String cloneStateId = persistenceManager.cloneState(portletStateId, newPrefs);

                        // Return the clone context
                        String cloneId = PRODUCER_CLONE_ID_PREFIX + cloneStateId;
                        PortletContext clonedCtx = PortletContext.createPortletContext(cloneId);
                        StateEvent event = new StateEvent(clonedCtx, StateEvent.Type.PORTLET_CLONED_EVENT);
                        instanceCtx.onStateEvent(event);
                     }
                     catch (NoSuchStateException e)
                     {
                        throw new PortletInvokerException("Unexpected exception", e);
                     }
                     catch (InvalidStateIdException e)
                     {
                        throw new PortletInvokerException("Unexpected exception", e);
                     }
                  }
                  else
                  {
                     PortletContext clonedCtx = marshall(stateType, context.getPortletId(), newPrefs);
                     StateEvent event = new StateEvent(clonedCtx, StateEvent.Type.PORTLET_CLONED_EVENT);
                     instanceCtx.onStateEvent(event);
                  }
               }
               else
               {
                  // Add the missing mutable portlet state
                  getPropertiesFromMetaData(portlet.getContext(), newPrefs);

                  //
                  if (persistLocally)
                  {
                     // Create the new state
                     String cloneStateId = persistenceManager.createState(context.getPortletId(), newPrefs);

                     // Return the clone context
                     String cloneId = PRODUCER_CLONE_ID_PREFIX + cloneStateId;
                     PortletContext clonedCtx = PortletContext.createPortletContext(cloneId);
                     StateEvent event = new StateEvent(clonedCtx, StateEvent.Type.PORTLET_CLONED_EVENT);
                     instanceCtx.onStateEvent(event);
                  }
                  else
                  {
                     PortletContext clonedCtx = marshall(stateType, context.getPortletId(), newPrefs);
                     StateEvent event = new StateEvent(clonedCtx, StateEvent.Type.PORTLET_CLONED_EVENT);
                     instanceCtx.onStateEvent(event);
                  }
               }
               break;
            }
            case READ_WRITE:
            {
               StatefulContext statefulContext = (StatefulContext)context;
               if (statefulContext.isLocal())
               {
                  // Update the state
                  try
                  {
                     LocalContext localContext = (LocalContext)statefulContext;
                     String stateId = localContext.getStateId();
                     persistenceManager.updateState(stateId, newPrefs);
                  }
                  catch (NoSuchStateException e)
                  {
                     throw new PortletInvokerException("Unexpected exception", e);
                  }
                  catch (InvalidStateIdException e)
                  {
                     throw new PortletInvokerException("Unexpected exception", e);
                  }
               }
               else
               {
                  PortletContext modifiedCtx = marshall(stateType, context.getPortletId(), newPrefs);
                  StateEvent event = new StateEvent(modifiedCtx, StateEvent.Type.PORTLET_MODIFIED_EVENT);
                  instanceCtx.onStateEvent(event);
               }
               break;
            }
            case READ_ONLY:
            {
               throw new PortletStateChangeRequiredException("Modification was requested for portlet with id '"
                  + context.getPortletId() + "' but access mode was READ ONLY.");
            }
         }
      }

      //
      return response;
   }

   public PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException("No null portlet context accepted");
      }

      //
      String portletId = portletContext.getId();
      InternalContext context = getStateContext(portletContext);

      //
      boolean persistLocally;
      if (stateType == null)
      {
         persistLocally = true;
      }
      else
      {
         persistLocally = stateManagementPolicy.persistLocally();
      }

      //
      if (context.isStateful())
      {
         StatefulContext statefulContext = (StatefulContext)context;
         if (persistLocally)
         {
            try
            {
               String stateId = portletId.substring(PRODUCER_CLONE_ID_PREFIX.length());
               String cloneId = PRODUCER_CLONE_ID_PREFIX + persistenceManager.cloneState(stateId);
               return PortletContext.createPortletContext(cloneId);
            }
            catch (NoSuchStateException e)
            {
               throw new NoSuchPortletException(e, portletId);
            }
            catch (InvalidStateIdException e)
            {
               throw new InvalidPortletIdException(e, portletId);
            }
         }
         else
         {
            return marshall(stateType, statefulContext.getPortletId(), statefulContext.getProperties());
         }
      }
      else
      {
         PropertyMap newState = new SimplePropertyMap();
         getPropertiesFromMetaData(portletContext, newState);
         if (persistLocally)
         {
            String cloneId = persistenceManager.createState(portletId, newState);
            return PortletContext.createPortletContext(PRODUCER_CLONE_ID_PREFIX + cloneId);
         }
         else
         {
            return marshall(stateType, portletId, newState);
         }
      }
   }

   public List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContexts == null)
      {
         throw new IllegalArgumentException("No portlet contexts provided");
      }

      //
      List<DestroyCloneFailure> result = new ArrayList<DestroyCloneFailure>();
      for (PortletContext portletContext : portletContexts)
      {
         // We only take care of producer hosted state
         if (!(portletContext instanceof StatefulPortletContext))
         {
            String portletId = portletContext.getId();
            if (!portletId.startsWith(PRODUCER_CLONE_ID_PREFIX))
            {
               log.debug("Attempt to destroy a producer offered portlet " + portletId);
               DestroyCloneFailure failure = new DestroyCloneFailure(portletId, "Cannot destroy POP");
               result.add(failure);
            }
            else
            {
               try
               {
                  persistenceManager.destroyState(portletId.substring(PRODUCER_CLONE_ID_PREFIX.length()));
               }
               catch (NoSuchStateException e)
               {
                  log.debug("Attempt to destroy a non existing portlet " + portletId);
                  DestroyCloneFailure failure = new DestroyCloneFailure(portletId, "Not found");
                  result.add(failure);
               }
               catch (InvalidStateIdException e)
               {
                  log.debug("Attempt to destroy a non valid portlet " + portletId);
                  DestroyCloneFailure failure = new DestroyCloneFailure(portletId, "Invalid portlet id");
                  result.add(failure);
               }
            }
         }
      }
      return result;
   }

   public PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (keys == null)
      {
         throw new IllegalArgumentException("No keys provided");
      }
      PropertyMap props = getProperties(portletContext);
      props.keySet().retainAll(keys);
      return props;
   }

   public PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException("No null portlet id is accepted");
      }

      //
      String portletId = portletContext.getId();
      InternalContext context = getStateContext(portletContext);

      //
      if (context.isStateful())
      {
         StatefulContext statefulContext = (StatefulContext)context;

         // Get the content
         PropertyMap props = new SimplePropertyMap(statefulContext.getProperties());

         // Dereference the portlet
         PortletContext refPortletContext = context.getPortletContext();

         // Get the referenced portlet
         Portlet refPortlet = super.getPortlet(refPortletContext);

         // We need the referenced portlet
         if (refPortlet == null)
         {
            throw new PortletInvokerException("The portlet " + refPortletContext + " referenced by this clone " + portletId + " is not available");
         }

         //
         getPropertiesFromMetaData(refPortletContext, props);

         //
         return props;
      }
      else
      {
         // Get the container
         PropertyMap props = new SimplePropertyMap();
         getPropertiesFromMetaData(context.getPortletContext(), props);
         return props;
      }
   }

   public PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException("No null portlet id accepted");
      }
      if (changes == null)
      {
         throw new IllegalArgumentException("No null changes accepted");
      }
      for (PropertyChange change : changes)
      {
         if (change == null)
         {
            throw new IllegalArgumentException("No null change accepted");
         }
      }

      //
      String portletId = portletContext.getId();
      InternalContext context = getStateContext(portletContext);

      //
      if (!context.isStateful())
      {
         throw new InvalidPortletIdException("Cannot configure producer offered portlets", portletId);
      }
      StatefulContext statefulContext = (StatefulContext)context;

      // Get the container
      Portlet referencedPortlet = super.getPortlet(context.getPortletContext());

      // We need the referenced portlet
      if (referencedPortlet == null)
      {
         throw new PortletInvokerException("The portlet " + context.getPortletContext() + " referenced by this clone " + portletId + " is not available");
      }

      // Get the portlet info
      PortletInfo referencedPortletInfo = referencedPortlet.getInfo();

      //
      PreferencesInfo prefs = referencedPortletInfo.getPreferences();

      // Clone the current state
      PropertyMap properties = new SimplePropertyMap(statefulContext.getProperties());

      // Clone argument
      for (PropertyChange change : changes)
      {
         String key = change.getKey();
         int type = change.getType();

         // If the original value exist and is read only we perform a reset instead
         PreferenceInfo pref = prefs.getPreference(key);
         if (pref != null)
         {
            if (Boolean.TRUE.equals(pref.isReadOnly()))
            {
               type = PropertyChange.PREF_RESET;
            }
         }

         //
         if (type == PropertyChange.PREF_UPDATE)
         {
            properties.setProperty(key, change.getValue());
         }
         else
         {
            properties.remove(key);
         }
      }

      //
      if (statefulContext.isLocal())
      {
         LocalContext localContext = (LocalContext)statefulContext;
         String stateId = localContext.getStateId();
         try
         {
            persistenceManager.updateState(stateId, properties);
         }
         catch (NoSuchStateException e)
         {
            throw new NoSuchPortletException(e, portletId);
         }
         catch (InvalidStateIdException e)
         {
            throw new InvalidPortletIdException(e, portletId);
         }
         return PortletContext.createPortletContext(portletId);
      }
      else
      {
         RemoteContext remoteStatefulContext = (RemoteContext)statefulContext;
         return marshall(remoteStatefulContext.getStateType(), context.getPortletId(), properties);
      }
   }

   private <S extends Serializable> PortletContext marshall(PortletStateType<S> stateType, String portletId, PropertyMap props) throws PortletInvokerException
   {
      try
      {
         PortletState sstate = new PortletState(portletId, props);
         S marshalledState = stateConverter.marshall(stateType, sstate);
         return StatefulPortletContext.create(CONSUMER_CLONE_ID, stateType, marshalledState);
      }
      catch (StateConversionException e)
      {
         throw new PortletInvokerException(e);
      }
   }

   /**
    * Retrieve the properties from the portlet meta data.
    *
    * @param portletContext the portlet context
    * @param props the properties
    * @throws PortletInvokerException any portlet invoker exception
    */
   private void getPropertiesFromMetaData(PortletContext portletContext, PropertyMap props) throws PortletInvokerException
   {
      //
      Portlet portlet = super.getPortlet(portletContext);

      // The prefs info
      PreferencesInfo prefs = portlet.getInfo().getPreferences();

      // Collect missing or read only properties from the referenced portlet
      Set<String> keys = new HashSet<String>();
      for (String key : prefs.getKeys())
      {
         PreferenceInfo pref = prefs.getPreference(key);
         if (Boolean.TRUE.equals(pref.isReadOnly()) || !props.keySet().contains(pref.getKey()))
         {
            keys.add(key);
         }
      }

      // Get the missing or read only properties from the referenced portlet properties
      // and add them to the actual state
      PropertyMap refPreferencesInfo = super.getProperties(portletContext, keys);
      for (Map.Entry<String, List<String>> entry : refPreferencesInfo.entrySet())
      {
         String key = entry.getKey();
         List<String> value = entry.getValue();
         props.setProperty(key, new ArrayList<String>(value));
      }
   }

   /**
    * Return an internal portlet context from the specified portlet context.
    *
    * @param portletContext the portlet context
    * @return the state that the portlet context carries
    * @throws NoSuchPortletException    if the underlying state does not exist
    * @throws InvalidPortletIdException if the state id is not valid
    */
   private InternalContext getStateContext(final PortletContext portletContext) throws NoSuchPortletException, InvalidPortletIdException
   {
      if (!(portletContext instanceof StatefulPortletContext))
      {
         String portletId = portletContext.getId();
         if (portletContext.getId().startsWith(PRODUCER_CLONE_ID_PREFIX))
         {
            String stateId = portletId.substring(PRODUCER_CLONE_ID_PREFIX.length());
            try
            {
               PortletStateContext stateContext = persistenceManager.loadState(stateId);
               return new LocalContext(stateContext.getState().getPortletId(), stateContext.getState().getProperties(), stateContext.getId());
            }
            catch (NoSuchStateException e)
            {
               throw new NoSuchPortletException(e, portletId);
            }
            catch (InvalidStateIdException e)
            {
               throw new InvalidPortletIdException(e, portletId);
            }
         }
         else
         {
            return new StatelessContext(portletContext.getId());
         }
      }
      else
      {
         StatefulPortletContext statefulPortletContext = (StatefulPortletContext)portletContext;
         Serializable bytes = statefulPortletContext.getState();
         PortletStateType stateType = statefulPortletContext.getType();
         try
         {
            final PortletState state = stateConverter.unmarshall(stateType, bytes);
            return new RemoteContext(stateType, state.getPortletId(), state.getProperties());
         }
         catch (StateConversionException e)
         {
            throw new InvalidPortletIdException(e, portletContext.getId());
         }
      }
   }

   /**
    * An internal portlet context that describe how the portlet was obtained.
    */
   private abstract static class InternalContext
   {

      /** . */
      private final String portletId;

      /** . */
      private final PortletContext portletContext;

      /**
       * @return true if the context represent a cloned portlet
       */
      public abstract boolean isStateful();

      public InternalContext(String portletId)
      {
         if (portletId == null)
         {
            throw new IllegalArgumentException();
         }
         this.portletId = portletId;
         this.portletContext = PortletContext.createPortletContext(portletId);
      }

      /**
       * @return the portlet id in the context of the delegate
       */
      public String getPortletId()
      {
         return portletId;
      }

      public PortletContext getPortletContext()
      {
         return portletContext;
      }
   }

   /**
    * Describe delegate portlets.
    */
   private static class StatelessContext extends InternalContext
   {
      public StatelessContext(String portletId)
      {
         super(portletId);
      }

      public boolean isStateful()
      {
         return false;
      }
   }

   /**
    * A cloned portlet that points to a delegate portlet.
    */
   private abstract static class StatefulContext extends InternalContext
   {

      /** . */
      private final PropertyMap properties;

      protected StatefulContext(String portletId, PropertyMap properties)
      {
         super(portletId);

         //
         if (properties == null)
         {
            throw new IllegalArgumentException();
         }
         this.properties = properties;
      }

      /**
       * @return true if the portlet is local
       */
      public abstract boolean isLocal();

      public PropertyMap getProperties()
      {
         return properties;
      }

      public boolean isStateful()
      {
         return true;
      }
   }

   private static class LocalContext extends StatefulContext
   {

      /** . */
      private String stateId;

      public LocalContext(String portletId, PropertyMap state, String stateId)
      {
         super(portletId, state);

         //
         if (stateId == null)
         {
            throw new IllegalArgumentException();
         }
         this.stateId = stateId;
      }

      public String getStateId()
      {
         return stateId;
      }

      public boolean isLocal()
      {
         return true;
      }
   }

   private static class RemoteContext extends StatefulContext
   {

      /** . */
      private final PortletStateType stateType;

      public RemoteContext(PortletStateType stateType, String portletId, PropertyMap state)
      {
         super(portletId, state);

         //
         this.stateType = stateType;
      }

      public PortletStateType getStateType()
      {
        return stateType;
      }

     public boolean isLocal()
      {
         return false;
      }
   }
}
