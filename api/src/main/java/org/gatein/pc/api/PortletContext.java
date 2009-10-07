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

import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6890 $
 * @since 2.6
 */
public class PortletContext implements Serializable
{

   /** . */
   protected final String id;
   private final String applicationName;
   private final String portletName;
   private static final String PREFIX = "/";
   private static final char SEPARATOR = '.';

   PortletContext(String id) throws IllegalArgumentException
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(id, "portlet id", "PortletContext");

      // components
      String trimmedId = id.trim();
      if (trimmedId.startsWith(PREFIX)) // only consider components if the id starts with '/'
      {
         String compound = trimmedId.substring(1); // exclude starting '/'

         int separator = compound.indexOf(SEPARATOR); // find first separator, other separator are considered part of the portlet name
         if (separator != -1)
         {
            portletName = compound.substring(separator + 1).trim();
            applicationName = compound.substring(0, separator).trim();
         }
         else
         {
            portletName = null;
            applicationName = null;
         }
      }
      else
      {
         portletName = null;
         applicationName = null;
      }

      if (portletName == null || applicationName == null)
      {
         this.id = trimmedId;
      }
      else
      {
         this.id = PREFIX + applicationName + SEPARATOR + portletName;
      }
   }


   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o instanceof PortletContext)
      {
         PortletContext that = (PortletContext)o;
         return id.equals(that.id);
      }
      return false;
   }

   public int hashCode()
   {
      return id.hashCode();
   }

   public String getId()
   {
      return id;
   }

   public String toString()
   {
      return "PortletContext[" + id + "]";
   }

   /**
    * Create a PortletContext based on id and optional state.
    *
    * @param id    the portlet id
    * @param state the optional state
    * @return a new PortletContext based on the provided information
    */
   @Deprecated()
   public static PortletContext createPortletContext(String id, byte[] state)
   {
      if (state != null && state.length > 0)
      {
         return new StatefulPortletContext<byte[]>(id, PortletStateType.OPAQUE, state);
      }
      else
      {
         return new PortletContext(id);
      }
   }

   /**
    * Create a PortletContext based on given id and required state: this method will fail if state is not valid. Use
    * this method instead of {@link #createPortletContext(String,byte[])} for cases when a state is expected and the
    * creation of the PortletContext should fail if no state was given.
    *
    * @param id    the portlet id
    * @param state the mandatory state
    * @return a new PortletContext
    */
   @Deprecated
   public static StatefulPortletContext<byte[]> createStatefulPortletContext(String id, byte[] state)
   {
      return new StatefulPortletContext<byte[]>(id, PortletStateType.OPAQUE, state);
   }

   public static PortletContext createPortletContext(String portletId)
   {
      return createPortletContext(portletId, null);
   }

   public String getApplicationName()
   {
      return applicationName;
   }

   public String getPortletName()
   {
      return portletName;
   }
}
