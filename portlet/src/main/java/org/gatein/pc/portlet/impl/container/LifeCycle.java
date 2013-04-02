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
   private LifeCycleStatus status = LifeCycleStatus.CREATED;

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

   public synchronized final void managedStart() throws IllegalStateException
   {
      if (active)
      {
         throw new IllegalStateException("Reentrancy detected");
      }

      // Update state
      active = true;
      failure = null;

      //
      boolean clearFaileds = false;
      Throwable failure = null;
      try
      {
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
         if (status != LifeCycleStatus.STARTED)
         {
            LifeCycleStatus status = LifeCycleStatus.CREATED;
            try
            {
               invokeStart();
               status = LifeCycleStatus.STARTED;
            }
            catch (DependencyNotResolvedException ignore)
            {
               status = LifeCycleStatus.CREATED;
            }
            catch (Exception e)
            {
               log.error("Cannot start object", e);
               failure = e;
            }
            catch (Error e)
            {
               log.error("Cannot start object", e);
               failure = e;
            }
            finally
            {
               this.status = status;

               //
               if (failure != null)
               {
                  faileds.get().add(this);
               }
            }

            //
            if (failure == null)
            {
               if (status == LifeCycleStatus.STARTED)
               {
                  getListener().onEvent(new ManagedObjectLifeCycleEvent(this, status));
               }
            }
            else
            {
               getListener().onEvent(new ManagedObjectFailedEvent(this, status));
            }

         }

         //
         if (status == LifeCycleStatus.STARTED)
         {
            startDependents();
         }
      }
      finally
      {
         if (clearFaileds)
         {
            faileds.set(null);
         }

         //
         this.active = false;
         this.failure = failure;
      }
   }

   public synchronized final void managedStop()
   {
      if (active)
      {
         throw new IllegalStateException("Reentrancy detected");
      }

      //
      active = true;
      failure = null;

      //
      try
      {
         stopDependents();

         //
         if (status == LifeCycleStatus.STARTED)
         {
            try
            {
               invokeStop();
            }
            catch (Exception e)
            {
               log.error("Error during object stop", e);
            }
            catch (Error e)
            {
               log.error("Error during object stop", e);
            }
            finally
            {
               status = LifeCycleStatus.CREATED;
            }

            //
            getListener().onEvent(new ManagedObjectLifeCycleEvent(this, LifeCycleStatus.CREATED));
         }
      }
      finally
      {
         active = false;
      }
   }

   protected void startDependents()
   {
   }

   protected void stopDependents()
   {
   }

   protected abstract void invokeStart() throws Exception;

   protected abstract void invokeStop();

   protected abstract ManagedObjectRegistryEventListener getListener();

}
