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

import org.gatein.pc.portlet.container.managed.ManagedObject;
import org.gatein.pc.portlet.container.managed.LifeCycleStatus;
import org.gatein.pc.portlet.container.managed.ManagedObjectRegistryEventListener;
import org.gatein.pc.portlet.container.managed.ManagedObjectLifeCycleEvent;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class LifeCycle implements ManagedObject
{

   /** . */
   private Logger log = Logger.getLogger(LifeCycle.class);

   /** . */
   private LifeCycleStatus status = LifeCycleStatus.STOPPED;

   /** Cheap reentrancy detection. */
   private boolean active = false;

   public final LifeCycleStatus getStatus()
   {
      return status;
   }

   private static final ThreadLocal<Set<Object>> faileds = new ThreadLocal<Set<Object>>();

   public synchronized final void managedStart() throws IllegalStateException
   {
      if (active)
      {
         throw new IllegalStateException("Reentrancy detected");
      }

      //
      active = true;

      //
      boolean clearFaileds = false;

      //
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
         LifeCycleStatus previousStatus = status;

         //
         if (status != LifeCycleStatus.STARTED)
         {
            LifeCycleStatus status = LifeCycleStatus.FAILED;
            try
            {
               invokeStart();
               status = LifeCycleStatus.STARTED;
            }
            catch (DependencyNotResolvedException ignore)
            {
               status = LifeCycleStatus.STOPPED;
            }
            catch (Exception e)
            {
               log.error("Cannot start object", e);
            }
            catch (Error e)
            {
               log.error("Cannot start object", e);
            }
            finally
            {
               this.status = status;

               //
               if (status == LifeCycleStatus.FAILED)
               {
                  faileds.get().add(this);
               }
            }
         }

         //
         if (status != previousStatus)
         {
            getListener().onEvent(new ManagedObjectLifeCycleEvent(this, status));
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
         active = false;
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
               status = LifeCycleStatus.STOPPED;
            }

            //
            getListener().onEvent(new ManagedObjectLifeCycleEvent(this, LifeCycleStatus.STOPPED));
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
