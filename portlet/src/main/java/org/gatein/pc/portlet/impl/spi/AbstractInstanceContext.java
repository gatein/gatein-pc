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
package org.gatein.pc.portlet.impl.spi;

import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.StateEvent;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.spi.InstanceContext;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class AbstractInstanceContext implements InstanceContext
{

   /** . */
   private final String id;

   /** . */
   private final AccessMode accessMode;

   /** . */
   private PortletContext clonedContext;

   /** . */
   private PortletContext modifiedContext;

   public AbstractInstanceContext(String id)
   {
      this(id, AccessMode.READ_ONLY);
   }

   public AbstractInstanceContext(String id, AccessMode accessMode)
   {
      if (id == null)
      {
         throw new IllegalArgumentException();
      }
      if (accessMode == null)
      {
         throw new IllegalArgumentException();
      }

      //
      this.id = id;
      this.accessMode = accessMode;
   }

   public String getId()
   {
      return id;
   }

   public AccessMode getAccessMode()
   {
      return accessMode;
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

   public PortletContext getClonedContext()
   {
      return clonedContext;
   }

   public PortletContext getModifiedContext()
   {
      return modifiedContext;
   }

   public PortletStateType<?> getStateType() {
      return null;
   }
}
