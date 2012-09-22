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
package org.gatein.pc.controller.state;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines the page navigational state view that the controller needs to operate on. Its name begins with page
 * however it does not mandate that the represented context  to be a page. I.E it could represent a set
 * of physical pages or something else.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public final class PageNavigationalState
{

   /** . */
   protected final Map<String, WindowNavigationalState> windows;

   /** . */
   protected final Map<QName, String[]> page;

   /** . */
   private final boolean modifiable;

   public PageNavigationalState(boolean modifiable)
   {
      this.windows = new HashMap<String, WindowNavigationalState>();
      this.page = new HashMap<QName, String[]>();
      this.modifiable = modifiable;
   }

   public PageNavigationalState(PageNavigationalState original, boolean modifiable)
   {
      this.windows = new HashMap<String, WindowNavigationalState>(original.windows);
      this.page = new HashMap<QName, String[]>(original.page);
      this.modifiable = modifiable;
   }

   /**
    * Returns the window ids referenced.
    *
    * @return a set of window id
    */
   public Set<String> getWindowIds()
   {
      return windows.keySet();
   }

   /**
    * Returns the navigational state of a portlet window or null if it does not exist.
    *
    * @param windowId the portlet window id
    * @return the portlet window navigational state
    * @throws IllegalArgumentException if an argument is not valid
    */
   public WindowNavigationalState getWindowNavigationalState(String windowId)  throws IllegalArgumentException
   {
      return windows.get(windowId);
   }

   /**
    * Update the navigational state of a portlet window.
    *
    * @param windowId the portlet window id
    * @param windowState the portlet window state
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   public void setWindowNavigationalState(String windowId, WindowNavigationalState windowState) throws IllegalArgumentException, IllegalStateException
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      //
      windows.put(windowId, windowState);
   }


   /**
    * Returns the set of public names.
    *
    * @return the public names
    */
   public Set<QName> getPublicNames()
   {
      return page.keySet();
   }

   /**
    * Returns a public navigational state entry or null if it is not found.
    *
    * @param name the name
    * @return the entry value
    * @throws IllegalArgumentException if an argument is not valid
    */
   public String[] getPublicNavigationalState(QName name) throws IllegalArgumentException
   {
      String[] values = page.get(name);
      return values != null ? values.clone() : null;
   }

   /**
    * Sets a public navigational state entry.
    *
    * @param name the name
    * @param value the new value
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   public void setPublicNavigationalState(QName name, String[] value) throws IllegalArgumentException, IllegalStateException
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      // We clone the value in order to keep the state not mutated by a side effect
      page.put(name, value.clone());
   }

   /**
    * Removes a public navigational state entry.
    *
    * @param name the name
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   public void removePublicNavigationalState(QName name) throws IllegalArgumentException, IllegalStateException
   {
      if (!modifiable)
      {
         throw new IllegalStateException("The page navigational state is not modifiable");
      }

      //
      page.remove(name);
   }
}
