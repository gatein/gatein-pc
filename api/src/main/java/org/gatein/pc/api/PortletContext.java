/*
 * JBoss, a division of Red Hat
 * Copyright 2011, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

   public static final String PRODUCER_CLONE_ID_PREFIX = "_";
   public static final int PRODUCER_CLONE_PREFIX_LENGTH = PRODUCER_CLONE_ID_PREFIX.length();

   public static final String CONSUMER_CLONE_ID_PREFIX = "@";
   public static final int CONSUMER_CLONE_PREFIX_LENGTH = CONSUMER_CLONE_ID_PREFIX.length();

   public static final String CONSUMER_CLONE_DUMMY_STATE_ID = "dumbvalue";
   public static final String CONSUMER_CLONE_ID = PRODUCER_CLONE_ID_PREFIX + CONSUMER_CLONE_DUMMY_STATE_ID;

   public final static PortletContext LOCAL_CONSUMER_CLONE = PortletContext.createPortletContext(PortletInvoker.LOCAL_PORTLET_INVOKER_ID + SEPARATOR + CONSUMER_CLONE_ID);

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
      boolean isCloned = false;

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
                  components = new PortletContextComponents(null, appName, portletName, null);
               }
            }
            else
            {
               if (!(trimmedId.startsWith(PRODUCER_CLONE_ID_PREFIX) || trimmedId.startsWith(CONSUMER_CLONE_ID_PREFIX)))
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
                              components = new PortletContextComponents(invokerId, applicationName, portletName, null);
                           }
                        }
                     }
                  }

                  // check if we have the case: invokerId.something
                  if (!isCompoundAppPortlet && invoker > 0)
                  {
                     String invokerId = trimmedId.substring(0, invoker).trim();

                     if (invokerId.length() > 0)
                     {
                        String portletNameOrStateId = trimmedId.substring(invoker + 1).trim();

                        if (portletNameOrStateId.length() > 0)
                        {
                           boolean isProducerClone = portletNameOrStateId.startsWith(PRODUCER_CLONE_ID_PREFIX);
                           boolean isConsumerClone = portletNameOrStateId.startsWith(CONSUMER_CLONE_ID_PREFIX);
                           if (isProducerClone || isConsumerClone)
                           {
                              int prefixLength = isProducerClone ? PRODUCER_CLONE_PREFIX_LENGTH : CONSUMER_CLONE_PREFIX_LENGTH;
                              portletNameOrStateId = portletNameOrStateId.substring(prefixLength).trim();
                              if (portletNameOrStateId.length() > 0)
                              {
                                 isCloned = true;
                                 components = new PortletContextComponents(invokerId, null, portletNameOrStateId, isProducerClone);
                              }
                           }
                           else
                           {
                              isOpaquePortlet = true;
                              components = new PortletContextComponents(invokerId, null, portletNameOrStateId, null);
                           }
                        }
                     }
                  }
               }
               else
               {
                  boolean isProducerClone = trimmedId.startsWith(PRODUCER_CLONE_ID_PREFIX);
                  boolean isConsumerClone = trimmedId.startsWith(CONSUMER_CLONE_ID_PREFIX);
                  if (isProducerClone || isConsumerClone)
                  {
                     int prefixLength = isProducerClone ? PRODUCER_CLONE_PREFIX_LENGTH : CONSUMER_CLONE_PREFIX_LENGTH;
                     trimmedId = trimmedId.substring(prefixLength).trim();
                     if (trimmedId.length() > 0)
                     {
                        isCloned = true;
                        components = new PortletContextComponents(null, null, trimmedId, isProducerClone);
                     }
                  }
               }
            }
         }
         catch (Exception e)
         {
            throw new IllegalArgumentException("Couldn't interpret id '" + id + "'", e);
         }
      }

      if (interpret && !(isSimpleAppPortlet || isCompoundAppPortlet || isOpaquePortlet || isCloned))
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
      return createPortletContext(portletId, true);
   }

   public static PortletContext createPortletContext(String portletId, boolean interpret)
   {
      return createPortletContext(portletId, null, interpret);
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

      return new PortletContext(new PortletContextComponents(null, applicationName, portletName, null));
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
      private final Boolean producerCloned;

      public PortletContextComponents(String invokerName, String applicationName, String portletNameOrStateId, Boolean producerCloned)
      {
         this.producerCloned = producerCloned;

         if (isCloned() && !ParameterValidation.isNullOrEmpty(applicationName))
         {
            throw new IllegalArgumentException("Cannot be a clone if applicationName is provided");
         }

         this.applicationName = applicationName;
         this.portletName = portletNameOrStateId;
         this.invokerName = invokerName;
      }

      public String getApplicationName()
      {
         return applicationName;
      }

      public String getPortletName()
      {
         return isCloned() ? null : portletName;
      }

      public String getInvokerName()
      {
         return invokerName;
      }

      public boolean isCloned()
      {
         return producerCloned != null;
      }

      public boolean isProducerCloned()
      {
         return isCloned() && producerCloned;
      }

      public boolean isConsumerCloned()
      {
         return isCloned() && !producerCloned;
      }

      public String getStateId()
      {
         return isCloned() ? portletName : null;
      }

      public String getId()
      {
         return (invokerName == null ? "" : invokerName + SEPARATOR)
            + (applicationName == null ? "" : PREFIX + applicationName + SEPARATOR)
            + (portletName == null ? "" : (producerCloned != null ? (producerCloned ? PRODUCER_CLONE_ID_PREFIX : CONSUMER_CLONE_ID_PREFIX) : "") + portletName);
      }
   }
}
