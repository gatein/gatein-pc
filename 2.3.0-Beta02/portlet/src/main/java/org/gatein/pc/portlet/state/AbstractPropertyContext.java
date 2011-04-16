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
package org.gatein.pc.portlet.state;

import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.AccessMode;

import java.util.Collections;
import java.util.Set;
import java.util.List;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6757 $
 */
public class AbstractPropertyContext implements PropertyContext
{

   /** No change have been done or attempted. */
   public static final int NO_CHANGE = 0;

   /** Attempts to update the state that failed has been performed. */
   public static final int UPDATE_FAILED = 1;

   /** The state has been succesfully updated. */
   public static final int UPDATE_SUCCESSFUL = 2;

   /** True if render phase. */
   private final boolean render;

   /** The user prefs. */
   private PropertyMap prefs;

   /** The access mode. */
   private final AccessMode access;

   /** State change status. */
   private int status;

   /**
    * Create a new object.
    *
    * @param prefs the user prefs
    * @throws IllegalArgumentException if the portletPrefs are null
    */
   public AbstractPropertyContext(
      AccessMode access,
      PropertyMap prefs,
      boolean render) throws IllegalArgumentException
   {
      if (access == null)
      {
         throw new IllegalArgumentException("No access mode provided");
      }
      this.access = access;
      this.prefs = prefs;
      this.status = NO_CHANGE;
      this.render = render;
   }

   public void update(PropertyChange[] changes) throws IllegalStateException
   {
      if (access == AccessMode.READ_ONLY)
      {
         status = UPDATE_FAILED;
         throw new IllegalStateException("Cannot update when read only");
      }
      if (changes.length > 0)
      {
         if (status == NO_CHANGE)
         {
            if (prefs == null)
            {
               prefs = new SimplePropertyMap();
            }
            else
            {
               prefs = new SimplePropertyMap(prefs);
            }
         }

         //
         for (PropertyChange change : changes)
         {
            prefs.setProperty(change.getKey(), change.getValue());
         }
         status = UPDATE_SUCCESSFUL;
      }
   }

   public Set<String> getKeys()
   {
      if (prefs == null)
      {
         return Collections.emptySet();
      }
      return prefs.keySet();
   }

   public List<String> getValue(String key) throws IllegalArgumentException
   {
      if (prefs == null)
      {
         return null;
      }
      return prefs.getProperty(key);
   }

   public boolean isReadOnly()
   {
      if (render)
      {
         throw new IllegalStateException("Not authorized to call this method during the render phase");
      }
      return access == AccessMode.READ_ONLY;
   }

   public PropertyMap getPrefs()
   {
      return prefs;
   }

   public int getStatus()
   {
      return status;
   }
}
