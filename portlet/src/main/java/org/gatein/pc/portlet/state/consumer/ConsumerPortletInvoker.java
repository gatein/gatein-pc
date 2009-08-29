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

import org.gatein.common.NotYetImplemented;
import org.gatein.pc.api.InvalidPortletIdException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.StateEvent;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.InstanceContext;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.portlet.state.InvalidStateIdException;
import org.gatein.pc.portlet.state.NoSuchStateException;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.DestroyCloneFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6765 $
 */
public class ConsumerPortletInvoker extends PortletInvokerInterceptor
{

   /** . */
   private static final String CLONE_ID_PREFIX = "@";

   /** . */
   private ConsumerPersistenceManager persistenceManager;

   public PortletContext unwrapCCP(String wrappedCCP) throws InvalidPortletIdException
   {
      if (wrappedCCP == null)
      {
         throw new IllegalArgumentException();
      }
      if (!wrappedCCP.startsWith(CLONE_ID_PREFIX))
      {
         throw new InvalidPortletIdException(wrappedCCP);
      }
      return PortletContext.createPortletContext(wrappedCCP.substring(CLONE_ID_PREFIX.length()));
   }

   public PortletContext unwrapPOP(String wrappedPOPId) throws InvalidPortletIdException
   {
      if (wrappedPOPId == null)
      {
         throw new IllegalArgumentException();
      }
      return PortletContext.createPortletContext(wrappedPOPId);
   }

   public String wrapCCP(PortletContext ccpCtx) throws InvalidPortletIdException
   {
      if (ccpCtx == null)
      {
         throw new IllegalArgumentException();
      }
      return CLONE_ID_PREFIX + ccpCtx.getId();
   }

   public String wrapPOP(PortletContext popCtx) throws InvalidPortletIdException
   {
      if (popCtx == null)
      {
         throw new IllegalArgumentException();
      }
      if (popCtx.getId().startsWith(CLONE_ID_PREFIX))
      {
         throw new IllegalArgumentException("Must not start with " + CLONE_ID_PREFIX);
      }
      return popCtx.getId();
   }

   public void setPersistenceManager(ConsumerPersistenceManager persistenceManager)
   {
      this.persistenceManager = persistenceManager;
   }


   public Set<Portlet> getPortlets() throws PortletInvokerException
   {
      // We don't need proxies here because we return the list of offered portlets
      return super.getPortlets();
   }

   public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
   {
      return getConsumerContext(portletContext).getPortlet();
   }

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      PortletContext portletContext = invocation.getTarget();
      if (portletContext == null)
      {
         throw new IllegalArgumentException();
      }

      //
      ConsumerContext consumerContext = getConsumerContext(portletContext);

      //
      InstanceContext cictx = invocation.getInstanceContext();
      StatefulInstanceContextImpl pictx = new StatefulInstanceContextImpl(cictx);

      try
      {
         invocation.setTarget(consumerContext.producerPortletContext);
         invocation.setInstanceContext(pictx);

         //
         PortletInvocationResponse response = super.invoke(invocation);

         //
         PortletContext clonedContext = pictx.clonedContext;
         if (clonedContext != null)
         {
            if (clonedContext instanceof StatefulPortletContext)
            {
               StatefulPortletContext statefulClonedContext = (StatefulPortletContext)clonedContext;
               Serializable state = statefulClonedContext.getState();
               PortletStateType stateType = statefulClonedContext.getType();

               // Save the clone state
               ConsumerState consumerState = new ConsumerState<Serializable>(clonedContext.getId(), stateType, state);
               String stateId = persistenceManager.createState(consumerState);
               String clonedId = CLONE_ID_PREFIX + stateId;
               StateEvent event = new StateEvent(PortletContext.createPortletContext(clonedId), StateEvent.Type.PORTLET_CLONED_EVENT);
               cictx.onStateEvent(event);
            }
            else
            {
               StateEvent event = new StateEvent(PortletContext.createPortletContext(clonedContext.getId()), StateEvent.Type.PORTLET_CLONED_EVENT);
               cictx.onStateEvent(event);
            }
         }
         else
         {
            PortletContext modifiedContext = pictx.modifiedContext;
            if (modifiedContext != null)
            {
               // update state if needed
               if (modifiedContext instanceof StatefulPortletContext)
               {
                  StatefulPortletContext statefulClonedContext = (StatefulPortletContext)modifiedContext;
                  Serializable state = statefulClonedContext.getState();
                  PortletStateType stateType = statefulClonedContext.getType();
                  try
                  {
                     ConsumerState consumerState = new ConsumerState<Serializable>(modifiedContext.getId(), stateType, state);
                     persistenceManager.updateState(consumerContext.stateId, consumerState);
                  }
                  catch (NoSuchStateException e)
                  {
                     e.printStackTrace();
                  }
                  catch (InvalidStateIdException e)
                  {
                     e.printStackTrace();
                  }
               }
            }
         }

         //
         return response;
      }
      finally
      {
         invocation.setTarget(portletContext);
         invocation.setInstanceContext(cictx);
      }
   }

   public PortletContext createClone(PortletStateType stateType, PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      ConsumerContext consumerContext = getConsumerContext(portletContext);

      //
      PortletContext clonedContext = super.createClone(stateType, consumerContext.producerPortletContext);

      if (clonedContext instanceof StatefulPortletContext)
      {
         StatefulPortletContext statefulClonedContext = (StatefulPortletContext)clonedContext;
         ConsumerState consumerState = new ConsumerState<Serializable>(clonedContext.getId(), statefulClonedContext.getType(), statefulClonedContext.getState());
         String id = persistenceManager.createState(consumerState);
         return PortletContext.createPortletContext(CLONE_ID_PREFIX + id);
      }
      else
      {
         return clonedContext;
      }
   }

   public List<DestroyCloneFailure> destroyClones(List<PortletContext> portletContexts) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      if (portletContexts == null)
      {
         throw new IllegalArgumentException();
      }
      portletContexts = new ArrayList<PortletContext>(portletContexts);
      for (int i = 0; i < portletContexts.size(); i++)
      {
         PortletContext portletContext = portletContexts.get(i);
         ConsumerContext consumerContext = getConsumerContext(portletContext);
         portletContexts.set(i, consumerContext.producerPortletContext);
         if (consumerContext.stateId != null)
         {
            try
            {
               persistenceManager.destroyState(consumerContext.stateId);
            }
            catch (NoSuchStateException e)
            {
               //
            }
            catch (InvalidStateIdException e)
            {
               //
            }
         }
      }

      //
      List<DestroyCloneFailure> failures = super.destroyClones(portletContexts);

      // Probably should wrap the portlet context here ????
//      for (Iterator i = failures.iterator(); i.hasNext();)
//      {
//         DestroyCloneFailure failure = (DestroyCloneFailure)i.next();
//         //
//      }

      //
      return failures;
   }

   public PropertyMap getProperties(PortletContext portletContext, Set<String> keys) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      ConsumerContext consumerContext = getConsumerContext(portletContext);
      return super.getProperties(consumerContext.producerPortletContext, keys);
   }

   public PropertyMap getProperties(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      ConsumerContext consumerContext = getConsumerContext(portletContext);
      return super.getProperties(consumerContext.producerPortletContext);
   }

   public PortletContext setProperties(PortletContext portletContext, PropertyChange[] changes) throws IllegalArgumentException, PortletInvokerException, UnsupportedOperationException
   {
      ConsumerContext consumerContext = getConsumerContext(portletContext);

      //
      PortletContext updatedPortletContext = super.setProperties(consumerContext.producerPortletContext, changes);

      if (updatedPortletContext instanceof StatefulPortletContext)
      {
         StatefulPortletContext statefulUpdatedPortletContext = (StatefulPortletContext)updatedPortletContext;
         Serializable state = statefulUpdatedPortletContext.getState();
         PortletStateType stateType = statefulUpdatedPortletContext.getType();

         //
         if (consumerContext.stateId == null)
         {
            throw new NotYetImplemented();
         }

         //
         ConsumerState consumerState = new ConsumerState<Serializable>(updatedPortletContext.getId(), stateType, state);
         try
         {
            persistenceManager.updateState(consumerContext.stateId, consumerState);
         }
         catch (NoSuchStateException e)
         {
            // What to do ?
         }
         catch (InvalidStateIdException e)
         {
            // What to do ?
         }
      }
      else
      {
         if (consumerContext.stateId != null)
         {
            throw new NotYetImplemented();
         }
      }

      // As the point is to make it constant we can simply return it
      return portletContext;
   }

   private class StatefulInstanceContextImpl implements InstanceContext
   {

      /** . */
      private InstanceContext cictx;

      /** . */
      private PortletContext clonedContext;

      /** . */
      private PortletContext modifiedContext;

      public StatefulInstanceContextImpl(InstanceContext cictx)
      {
         this.cictx = cictx;
      }

      public String getId()
      {
         return cictx.getId();
      }

      public AccessMode getAccessMode()
      {
         return cictx.getAccessMode();
      }

      public void onStateEvent(StateEvent event)
      {
         switch (event.getType())
         {
            case PORTLET_CLONED_EVENT:
               clonedContext = event.getPortletContext();
               break;
            case PORTLET_MODIFIED_EVENT:
               modifiedContext = event.getPortletContext();
               break;
         }
      }

      public PortletStateType<?> getStateType() {
         return persistenceManager.getStateType();
      }
   }

   private ConsumerContext getConsumerContext(PortletContext portletContext) throws IllegalArgumentException, InvalidPortletIdException
   {
      if (portletContext == null)
      {
         throw new IllegalArgumentException();
      }

      //
      String portletId = portletContext.getId();

      //
      if (portletId.startsWith(CLONE_ID_PREFIX))
      {
         String stateId = portletId.substring(CLONE_ID_PREFIX.length());
         try
         {
            ConsumerStateContext stateCtx = persistenceManager.loadState(stateId);
            StatefulPortletContext<Serializable> blah = StatefulPortletContext.create(
              stateCtx.getPortletId(),
              stateCtx.getStateType(),
              stateCtx.getState());
            return new ConsumerContext(portletContext, blah, stateId);
         }
         catch (NoSuchStateException e)
         {
            throw new NoSuchPortletException(portletId);
         }
         catch (InvalidStateIdException e)
         {
            throw new InvalidPortletIdException(portletId);
         }
      }
      else
      {
         return new ConsumerContext(portletContext, portletContext, null);
      }
   }

   /**
    * A context which defines how the consumer see the producer portlet.
    */
   private class ConsumerContext
   {

      /** . */
      private final PortletContext consumerPortletContext;

      /** The target portlet context. */
      private final PortletContext producerPortletContext;

      /** The id in the store. */
      private final String stateId;

      /** The lazy created portlet. */
      private Portlet portlet;

      public ConsumerContext(
         PortletContext consumerPortletContext,
         PortletContext producerPortletContext,
         String stateId)
      {
         this.consumerPortletContext = consumerPortletContext;
         this.producerPortletContext = producerPortletContext;
         this.stateId = stateId;
      }

      public Portlet getPortlet() throws PortletInvokerException
      {
         if (portlet == null)
         {
            Portlet producerPortlet = ConsumerPortletInvoker.super.getPortlet(producerPortletContext);

            //
            if (stateId == null)
            {
               portlet = producerPortlet;
            }
            else
            {
               portlet = new ConsumerPortlet(consumerPortletContext, producerPortlet);
            }
         }

         //
         return portlet;
      }
   }
}
