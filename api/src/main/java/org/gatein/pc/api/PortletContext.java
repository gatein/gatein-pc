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
         int separator = trimmedId.indexOf(SEPARATOR); // find first separator, other separator are considered part of the portlet name
         if (separator != -1)
         {
            portletName = trimmedId.substring(separator + 1).trim();
            applicationName = PREFIX + trimmedId.substring(1, separator).trim();
         }
         else
         {
            portletName = null;
            applicationName = null;
         }
      }
      else
      {
         // check if we have the case: invokerId./application.portlet
         int prefix = trimmedId.indexOf(PREFIX);
         int invoker = trimmedId.indexOf(SEPARATOR);

         // find first separator, check if it could be an invoker id
         if (invoker > 0 && invoker < prefix)
         {
            // check if we have a second separator, which would indicate a portlet context with invoker id
            int separator = trimmedId.indexOf(SEPARATOR, prefix);
            if (separator != -1)
            {
               String invokerId = trimmedId.substring(0, invoker).trim();
               portletName = trimmedId.substring(separator + 1).trim();
               trimmedId = trimmedId.substring(invoker + 1).trim();
               applicationName = PREFIX + trimmedId.substring(1, trimmedId.indexOf(SEPARATOR)).trim();
               this.id = invokerId + SEPARATOR + buildIdFrom(applicationName, portletName); // recreate id with invoker
               return;
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
      }

      if (portletName == null || applicationName == null)
      {
         this.id = trimmedId;
      }
      else
      {
         this.id = buildIdFrom(applicationName, portletName);
      }
   }

   private PortletContext(String applicationName, String portletName, boolean formatApplicationName)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(applicationName, "portlet application id", "PortletContext");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(portletName, "container id", "PortletContext");

      if (!applicationName.startsWith(PREFIX))
      {
         if (formatApplicationName)
         {
            applicationName = PREFIX + applicationName;
         }
         else
         {
            throw new IllegalArgumentException("Application name must start with '" + PREFIX + "'. Was: " + applicationName);
         }
      }

      this.applicationName = applicationName;
      this.portletName = portletName;
      this.id = buildIdFrom(applicationName, portletName);
   }

   private String buildIdFrom(final String applicationName, final String portletName)
   {
      return applicationName + SEPARATOR + portletName;
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
    * this method instead of {@link #createPortletContext(String, byte[])} for cases when a state is expected and the
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
      return createPortletContext(portletId, (byte[])null);
   }

   public String getApplicationName()
   {
      return applicationName;
   }

   public String getPortletName()
   {
      return portletName;
   }

   /**
    * Creates a new PortletContext referencing the specified portlet in the specified application (usually a web
    * application).
    *
    * @param applicationName the application name (usually a web application context path)
    * @param portletName     the portlet name
    * @return a newly created PortletContext referencing the specified portlet in the specified application.
    * @throws IllegalArgumentException if the specified arguments are null or empty and if the application name is not
    *                                  properly formatted.
    */
   public static PortletContext createPortletContext(String applicationName, String portletName)
   {
      return createPortletContext(applicationName, portletName, false);
   }

   /**
    * Creates a new PortletContext referencing the specified portlet in the specified application (usually a web
    * application).
    *
    * @param applicationName       the application name (usually a web application context path)
    * @param portletName           the portlet name
    * @param formatApplicationName <code>true</code> if the application name should be formatted before attempting to
    *                              create the PortletContext, <code>false</code> otherwise.
    * @return a newly created PortletContext referencing the specified portlet in the specified application.
    * @throws IllegalArgumentException if the specified arguments are null or empty and if the application name is not
    *                                  properly formatted.
    */
   public static PortletContext createPortletContext(String applicationName, String portletName, boolean formatApplicationName)
   {
      return new PortletContext(applicationName, portletName, formatApplicationName);
   }
}
