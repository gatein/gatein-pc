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

   /** The separator used in the id to route to the correct invoker. */
   public static final String INVOKER_SEPARATOR = ".";

   public static final String PRODUCER_CLONE_ID_PREFIX = "_";
   public static final int PRODUCER_CLONE_PREFIX_LENGTH = PRODUCER_CLONE_ID_PREFIX.length();

   public static final String CONSUMER_CLONE_ID_PREFIX = "@";
   public static final int CONSUMER_CLONE_PREFIX_LENGTH = CONSUMER_CLONE_ID_PREFIX.length();

   public static final String CONSUMER_CLONE_DUMMY_STATE_ID = "dumbvalue";
   public static final String CONSUMER_CLONE_ID = PRODUCER_CLONE_ID_PREFIX + CONSUMER_CLONE_DUMMY_STATE_ID;

   public final static PortletContext LOCAL_CONSUMER_CLONE = PortletContext.createPortletContext(PortletInvoker.LOCAL_PORTLET_INVOKER_ID + INVOKER_SEPARATOR + CONSUMER_CLONE_ID);
   public static final String INVALID_PORTLET_CONTEXT = "Invalid portlet context: ";

   private final Components components;

   protected PortletContext(String id) throws IllegalArgumentException
   {
      this(id, true);
   }

   protected PortletContext(String id, boolean interpret)
   {
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(id, "portlet id", "PortletContext");

      Components components;

      // components
      if (interpret)
      {
         ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(id, "portlet id", "PortletContext");

         components = interpretIntoComponents(id);
      }
      else
      {
         components = new UninterpretedPortletContextComponents(id);
      }

      this.components = components;
   }

   /**
    * <p>Interprets the specified identifier and splits it into Components that can then be used to create a
    * PortletContext.</p>
    * <p/>
    * <ul>Currently supported formats:
    * <li>portletContext := invokerId + {@link #INVOKER_SEPARATOR} + {@link #PREFIX} + applicationName + {@link
    * #SEPARATOR} + portletName</li>
    * <li>portletContext := invokerId + {@link #INVOKER_SEPARATOR} + arbitraryPortletName</li>
    * <li>portletContext := {@link #PRODUCER_CLONE_ID_PREFIX} + portletName</li>
    * <li>portletContext := {@link #CONSUMER_CLONE_ID_PREFIX} + portletName</li>
    * <li>invokerId := space* + (digit | letter)+ + (digit | letter | space)*</li>
    * <li>applicationName := space* + (digit | letter)+ + (digit | letter | space)*</li>
    * <li>portletName := space* + (digit | letter)+ + (digit | letter | space)*</li>
    * <li>arbitraryPortletName := ({@link #PREFIX} | {@link #PRODUCER_CLONE_ID_PREFIX} | {@link
    * #CONSUMER_CLONE_ID_PREFIX})? + portletName</li> </ul>
    *
    * @param id the portlet identifier to interpret into components
    * @return a Components object representing the interpreted components of the specified portlet identifier
    * @throws IllegalArgumentException if the specified identifier cannot be properly interpreted into components
    */
   private Components interpretIntoComponents(String id)
   {
      String trimmedId = id.trim();
      try
      {
         if (trimmedId.startsWith(PREFIX))
         {
            // check the case: /application.portlet
            int separator = trimmedId.indexOf(SEPARATOR); // find first separator, other separator are considered part of the portlet name
            String portletName = trimmedId.substring(separator + 1).trim();
            String appName = trimmedId.substring(1, separator).trim();
            if (separator != -1 && appName.length() > 0 && portletName.length() > 0)
            {
               return new InterpretedPortletContextComponents(null, appName, portletName, null);
            }
            else
            {
               throw new IllegalArgumentException("Application name and portlet names were both empty.");
            }
         }
         else
         {
            if (!(trimmedId.startsWith(PRODUCER_CLONE_ID_PREFIX) || trimmedId.startsWith(CONSUMER_CLONE_ID_PREFIX)))
            {
               int invoker = trimmedId.indexOf(INVOKER_SEPARATOR);
               int prefix = trimmedId.indexOf(PREFIX);

               boolean isCompoundAppPortlet = false;
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
                           return new InterpretedPortletContextComponents(invokerId, applicationName, portletName, null);
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
                              return new InterpretedPortletContextComponents(invokerId, null, portletNameOrStateId, isProducerClone);
                           }
                        }
                        else
                        {
                           return new InterpretedPortletContextComponents(invokerId, null, portletNameOrStateId, null);
                        }
                     }
                  }
               }

               // we're in the case where we have an arbitrary string
               throw new IllegalArgumentException("'" + trimmedId + "' doesn't fit any known PortletContext formats");
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
                     return new InterpretedPortletContextComponents(null, null, trimmedId, isProducerClone);
                  }
               }

               // we're in the case where we have an arbitrary string
               throw new IllegalArgumentException("'" + trimmedId + "' doesn't fit any known PortletContext formats");
            }
         }
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException(INVALID_PORTLET_CONTEXT + id, e);
      }
   }

   protected PortletContext(Components components)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(components, "portlet context components");
      this.components = components;
   }

   public static PortletContext dereference(PortletContext compoundPortletContext)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(compoundPortletContext, "PortletContext to dereference");

      Components components = compoundPortletContext.getInternalComponents();
      if (!components.isInterpreted())
      {
         try
         {
            // try interpretation to see if we can extract an invoker id from the components
            components = compoundPortletContext.interpret();
         }
         catch (IllegalArgumentException e)
         {
            // we couldn't interpret the given portlet context so return it as is
            return compoundPortletContext;
         }
      }

      return createCopyWithNewComponents(compoundPortletContext, components.createCopyWithoutInvoker());
   }

   protected Components interpret()
   {
      return interpretIntoComponents(components.getId());
   }

   public static PortletContext reference(String invokerId, PortletContext portletContext)
   {
      ParameterValidation.throwIllegalArgExceptionIfNull(portletContext, "PortletContext to reference");
      ParameterValidation.throwIllegalArgExceptionIfNullOrEmpty(invokerId, "Invoker id to reference with", null);

      final Components newComponents = portletContext.getInternalComponents().createCopyWithInvoker(invokerId);

      return createCopyWithNewComponents(portletContext, newComponents);
   }

   private static PortletContext createCopyWithNewComponents(PortletContext portletContextToCopy, Components newComponents)
   {
      if (portletContextToCopy instanceof StatefulPortletContext)
      {
         StatefulPortletContext<?> compoundStatefulPortletContext = (StatefulPortletContext<?>)portletContextToCopy;
         return StatefulPortletContext.create(newComponents, compoundStatefulPortletContext);
      }
      else
      {
         return new PortletContext(newComponents);
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
         return getId().equals(that.getId());
      }
      return false;
   }

   public int hashCode()
   {
      return getId().hashCode();
   }

   public String getId()
   {
      return components.getId();
   }

   public String toString()
   {
      return "PortletContext[" + getId() + "]";
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

   /**
    * Creates a PortletContext by interpreting the specified identifier and breaking it down to its components. When
    * possible, it's better to use {@link #createPortletContext(String, String)} to create the PortletContext from its
    * components instead of trying to build it manually. In case a PortletContext namespaced by a PortletInvoker id is
    * needed, please use {@link #reference(String, PortletContext)}.
    *
    * @param portletId the portlet identifier to interpret as a PortletContext
    * @return a newly created PortletContext representing the portlet associated with the specified identifier
    * @throws IllegalArgumentException if the specified identifier cannot be properly interpreted into components
    * @see {@link #interpretIntoComponents(String)}
    */
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

      return new PortletContext(new InterpretedPortletContextComponents(null, applicationName, portletName, null));
   }

   public PortletContextComponents getComponents()
   {
      return getInternalComponents();
   }

   private Components getInternalComponents()
   {
      return components;
   }

   public static interface PortletContextComponents
   {
      String getApplicationName();

      String getPortletName();

      String getInvokerName();

      boolean isCloned();

      boolean isProducerCloned();

      boolean isConsumerCloned();

      String getStateId();

      String getId();

      boolean isInterpreted();
   }

   protected static interface Components extends PortletContextComponents
   {
      Components createCopyWithoutInvoker();

      Components createCopyWithInvoker(String invoker);
   }

   private static class InterpretedPortletContextComponents implements Components
   {
      private final String applicationName;
      private final String portletName;
      private final String invokerName;
      private final Boolean producerCloned;
      private final String id;

      InterpretedPortletContextComponents(String invokerName, String applicationName, String portletNameOrStateId, Boolean producerCloned)
      {
         this.producerCloned = producerCloned;

         if (isCloned() && !ParameterValidation.isNullOrEmpty(applicationName))
         {
            throw new IllegalArgumentException("Cannot be a clone if applicationName is provided");
         }

         this.applicationName = applicationName;
         this.portletName = portletNameOrStateId;
         this.invokerName = invokerName;

         id = (invokerName == null ? "" : invokerName + INVOKER_SEPARATOR)
            + (applicationName == null ? "" : PREFIX + applicationName + SEPARATOR)
            + (portletName == null ? "" : (producerCloned != null ? (producerCloned ? PRODUCER_CLONE_ID_PREFIX : CONSUMER_CLONE_ID_PREFIX) : "") + portletName);
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
         return id;
      }

      public boolean isInterpreted()
      {
         return true;
      }

      public Components createCopyWithoutInvoker()
      {
         return new InterpretedPortletContextComponents(null, applicationName, portletName, producerCloned);
      }

      public Components createCopyWithInvoker(String invoker)
      {
         return new InterpretedPortletContextComponents(invoker, applicationName, portletName, producerCloned);
      }
   }

   private static class UninterpretedPortletContextComponents implements Components
   {
      private static final String ERROR = "This PortletContext was not intepreted, only the portlet name is available!";
      private final String portletName;

      private UninterpretedPortletContextComponents(String portletName)
      {
         this.portletName = portletName;
      }

      public String getApplicationName()
      {
         throw new IllegalStateException(ERROR);
      }

      public String getPortletName()
      {
         return portletName;
      }

      public String getInvokerName()
      {
         throw new IllegalStateException(ERROR);
      }

      public boolean isCloned()
      {
         throw new IllegalStateException(ERROR);
      }

      public boolean isProducerCloned()
      {
         throw new IllegalStateException(ERROR);
      }

      public boolean isConsumerCloned()
      {
         throw new IllegalStateException(ERROR);
      }

      public String getStateId()
      {
         throw new IllegalStateException(ERROR);
      }

      public String getId()
      {
         return portletName;
      }

      public boolean isInterpreted()
      {
         return false;
      }

      public Components createCopyWithoutInvoker()
      {
         throw new IllegalStateException(ERROR);
      }

      public Components createCopyWithInvoker(String invoker)
      {
         return new InterpretedPortletContextComponents(invoker, null, portletName, null);
      }
   }
}
