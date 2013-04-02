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
package org.gatein.pc.portlet.impl.container;

import org.gatein.common.logging.LoggerFactory;
import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedObjectFailedEvent;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.managed.ManagedObjectLifeCycleEvent;
import org.gatein.common.logging.Logger;

import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class LifeCycle implements ManagedObject
{

   /** . */
   private static final ThreadLocal<Set<Object>> faileds = new ThreadLocal<Set<Object>>();

   /** . */
   private Logger log = LoggerFactory.getLogger(LifeCycle.class);

   /** . */
   private LifeCycleStatus status = LifeCycleStatus.INITIALIZED;

   /** Cheap reentrancy detection. */
   private boolean active = false;

   /** . */
   private Throwable failure;

   public final LifeCycleStatus getStatus()
   {
      return status;
   }

   @Override
   public Throwable getFailure()
   {
      return failure;
   }

   @Override
   public final void managedStart() throws IllegalStateException
   {
      promote(LifeCycleStatus.STARTED);
   }

   @Override
   public final synchronized void promote(LifeCycleStatus to) throws IllegalStateException
   {
      if (active)
      {
         throw new IllegalStateException("Reentrancy detected");
      }

      // Avoid attempt to re promote if failed previously
      boolean clearFaileds = false;
      if (faileds.get() == null)
      {
         clearFaileds = true;
         faileds.set(new HashSet<Object>());
      }
      else if (faileds.get().contains(this))
      {
         return;
      }

      //
      active = true;
      failure = null;
      try
      {
         while (to.getStage() > status.getStage())
         {
            LifeCycleStatus current = status;
            promote();
            if (current == status)
            {
               break;
            }
         }

         //
         promoteDependents(to);
      }
      finally
      {
         if (clearFaileds)
         {
            faileds.set(null);
         }
         active = false;
      }
   }

   private void promote() throws IllegalStateException
   {
      LifeCycleStatus to = status.getPromotion();

      //
      if (to != null)
      {
         Throwable failure = null;
         try
         {
            LifeCycleStatus next = status;
            try
            {
               switch (to)
               {
                  case CREATED:
                     invokeCreate();
                     break;
                  case STARTED:
                     invokeStart();
                     break;
               }
               next = to;
            }
            catch (DependencyNotResolvedException ignore)
            {
               // We stay in current status
            }
            catch (Exception e)
            {
               log.error("Cannot promote object to " + to, e);
               failure = e;
            }
            catch (Error e)
            {
               log.error("Cannot promote object to " + to, e);
               failure = e;
            }
            finally
            {
               this.status = next;

               //
               if (failure != null)
               {
                  faileds.get().add(this);
               }
            }

            //
            if (failure == null)
            {
               if (next == to)
               {
                  getListener().onEvent(new ManagedObjectLifeCycleEvent(this, next));
               }
            }
            else
            {
               getListener().onEvent(new ManagedObjectFailedEvent(this, next));
            }
         }
         finally
         {
            this.failure = failure;
         }
      }
   }

   public final void managedDestroy()
   {
      demote(LifeCycleStatus.INITIALIZED);
   }

   public final synchronized void demote(LifeCycleStatus to) throws IllegalStateException
   {
      if (active)
      {
         throw new IllegalStateException("Reentrancy detected");
      }

      //
      active = true;
      failure = null;
      try
      {
         demoteDependents(to);

         //
         while (to.getStage() < status.getStage())
         {
            LifeCycleStatus current = status;
            demote();
            if (current == status)
            {
               break;
            }
         }
      }
      finally
      {
         active = false;
      }
   }

   private void demote() throws IllegalStateException
   {
      LifeCycleStatus to = status.getDemotion();

      //
      if (to != null)
      {
         try
         {
            switch (to)
            {
               case CREATED:
                  invokeStop();
                  break;
               case INITIALIZED:
                  invokeDestroy();
                  break;
            }
         }
         catch (Exception e)
         {
            log.error("Error during object demotion to " + to, e);
         }
         catch (Error e)
         {
            log.error("Error during object demotion to " + to, e);
         }
         finally
         {
            status = to;
         }

         //
         getListener().onEvent(new ManagedObjectLifeCycleEvent(this, to));
      }
   }

   protected void promoteDependents(LifeCycleStatus to)
   {
   }

   protected void demoteDependents(LifeCycleStatus to)
   {
   }

   protected abstract void invokeCreate() throws Exception;

   protected abstract void invokeStart() throws Exception;

   protected abstract void invokeStop();

   protected abstract void invokeDestroy() throws Exception;

   protected abstract ManagedObjectRegistryEventListener getListener();

}
