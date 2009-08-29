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
package org.gatein.pc.api;

import org.gatein.common.util.ParameterValidation;

/**
 * An event that signals state modifications
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 7267 $
 * @since 2.6
 */
public class StateEvent
{

   /** . */
   private final PortletContext context;

   /** . */
   private final Type type;

   public StateEvent(PortletContext context, Type type)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(context, "Portlet context");
      ParameterValidation.throwIllegalArgExceptionIfNull(type, "StateEvent type");

      //
      this.context = context;
      this.type = type;
   }

   public PortletContext getPortletContext()
   {
      return context;
   }

   public Type getType()
   {
      return type;
   }

   public enum Type
   {

      PORTLET_CLONED_EVENT("Portlet Cloned Event"), PORTLET_MODIFIED_EVENT("Portlet Modified Event");

      /** . */
      private final String description;

      private Type(String description)
      {
         this.description = description;
      }

      public String toString()
      {
         return description;
      }
   }
}
