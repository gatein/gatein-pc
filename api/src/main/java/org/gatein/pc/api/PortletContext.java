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

   private static final String PREFIX = "/";
   private static final char SEPARATOR = '.';

   /* TODO: remove from ProducerPortletInvoker so that we can use these constants in GateIn
   public static final String CONSUMER_CLONE_ID = "_dumbvalue";
   public static final String PRODUCER_CLONE_ID_PREFIX = "_";
   public final static PortletContext CONSUMER_CLONE = PortletContext.createPortletContext(PortletInvoker.LOCAL_PORTLET_INVOKER_ID + SEPARATOR + CONSUMER_CLONE_ID);*/

   protected final String id;
   private final PortletContextComponents components;

   protected PortletContext(String id) throws IllegalArgumentException
   {
      this(id, true);
   }

   protected PortletContext(String id, boolean interpret)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(id, "portlet id", "PortletContext");

      PortletContextComponents components = null;
      boolean isSimpleAppPortlet = false;
      boolean isOpaquePortlet = false;
      boolean isCompoundAppPortlet = false;

      // components
      if (interpret)
      {
         ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(id, "portlet id", "PortletContext");

         String trimmedId = id.trim();

         try
         {
            if (trimmedId.startsWith(PREFIX))
            {
               // check the case: /application.portlet
               int separator = trimmedId.indexOf(SEPARATOR); // find first separator, other separator are considered part of the portlet name
               String portletName = trimmedId.substring(separator + 1).trim();
               String appName = trimmedId.substring(1, separator).trim();
               isSimpleAppPortlet = separator != -1 && appName.length() > 0 && portletName.length() > 0;
               if (isSimpleAppPortlet)
               {
                  components = new PortletContextComponents(null, appName, portletName);
               }
            }
            else
            {
               int invoker = trimmedId.indexOf(SEPARATOR);
               int prefix = trimmedId.indexOf(PREFIX);

               if (prefix != -1)
               {
                  // check if we have the case: invokerId./something
                  if (invoker > 0 && invoker < prefix)
                  {
                     String invokerId = trimmedId.substring(0, invoker).trim();

                     int separator = trimmedId.indexOf(SEPARATOR, prefix);
                     // check the case: invokerId./application.portlet
                     if (separator != -1)
                     {
                        String portletName = trimmedId.substring(separator + 1).trim();
                        trimmedId = trimmedId.substring(invoker + 1).trim();
                        String applicationName = trimmedId.substring(1, trimmedId.indexOf(SEPARATOR)).trim();
                        isCompoundAppPortlet = invokerId.length() > 0 && applicationName.length() > 0 && portletName.length() > 0;
                        if (isCompoundAppPortlet)
                        {
                           components = new PortletContextComponents(invokerId, applicationName, portletName);
                        }
                     }
                  }
               }

               // check if we have the case: invokerId.portletId
               if (!isCompoundAppPortlet && invoker > 0)
               {
                  String invokerId = trimmedId.substring(0, invoker).trim();
                  String portletName = trimmedId.substring(invoker + 1).trim();
                  isOpaquePortlet = invokerId.length() > 0 && portletName.length() > 0;
                  if (isOpaquePortlet)
                  {
                     components = new PortletContextComponents(invokerId, null, portletName);
                  }
               }
            }
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Couldn't interpret id '" + id + "'", e);
         }
      }

      if (interpret && !(isSimpleAppPortlet || isCompoundAppPortlet || isOpaquePortlet))
      {
         throw new IllegalArgumentException("Couldn't interpret id '" + id + "'");
      }

      this.components = components;
      this.id = components != null ? components.getId() : id;
   }

   protected PortletContext(PortletContextComponents components)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(components, "portlet context components");
      this.components = components;
      this.id = components.getId();
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
      return createPortletContext(id, state, true);
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
      return createPortletContext(portletId, null, true);
   }

   public static PortletContext createPortletContext(String portletId, byte[] state, boolean interpret)
   {
      if (state != null && state.length > 0)
      {
         return new StatefulPortletContext<byte[]>(portletId, PortletStateType.OPAQUE, state);
      }
      else
      {
         return new PortletContext(portletId, interpret);
      }
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
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(applicationName, "portlet application id", "PortletContext");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(portletName, "container id", "PortletContext");

      if (applicationName.startsWith(PREFIX))
      {
         applicationName = applicationName.substring(1);
      }

      return new PortletContext(new PortletContextComponents(null, applicationName, portletName));
   }

   public PortletContextComponents getComponents()
   {
      return components;
   }

   public static class PortletContextComponents
   {
      private final String applicationName;
      private final String portletName;
      private final String invokerName;

      public PortletContextComponents(String invokerName, String applicationName, String portletName)
      {
         this.applicationName = applicationName;
         this.portletName = portletName;
         this.invokerName = invokerName;
      }

      public String getApplicationName()
      {
         return applicationName;
      }

      public String getPortletName()
      {
         return portletName;
      }

      public String getInvokerName()
      {
         return invokerName;
      }

      public String getId()
      {
         return (invokerName == null ? "" : invokerName + SEPARATOR)
            + (applicationName == null ? "" : PREFIX + applicationName + SEPARATOR)
            + (portletName == null ? "" : portletName);
      }
   }
}
