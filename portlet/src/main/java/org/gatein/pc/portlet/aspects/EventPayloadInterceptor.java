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
package org.gatein.pc.portlet.aspects;

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletApplicationContext;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.EventingInfo;
import org.gatein.pc.api.info.EventInfo;
import org.gatein.pc.portlet.impl.info.ContainerTypeInfo;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.common.io.IOTools;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.io.IOException;
import java.util.Map;

/**
 * This interceptor is responsible for taking care of converting the optional event payload to the application
 * classloader.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventPayloadInterceptor extends PortletInvokerInterceptor
{

   /** . */
   private final static Logger log = Logger.getLogger(EventPayloadInterceptor.class);

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      if (invocation instanceof EventInvocation)
      {
         EventInvocation eventInvocation = (EventInvocation)invocation;

         //
         Serializable srcPayload = eventInvocation.getPayload();

         //
         Serializable dstPayload = null;
         if (srcPayload != null)
         {
            PortletContainer container = (PortletContainer)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);

            //
            PortletApplication application = container.getPortletApplication();
            PortletApplicationContext applicationContext = application.getContext();
            ClassLoader applicationClassLoader = applicationContext.getClassLoader();
            String srcPayloadClassName = srcPayload.getClass().getName();
            boolean trace = log.isTraceEnabled();
            QName eventName = eventInvocation.getName();
            String containerId = container.getId();
            String applicationId = application.getId();

            //
            PortletInfo info = container.getInfo();
            EventingInfo eventingInfo = info.getEventing();
            Map<QName, ? extends EventInfo> consumedEventInfos = eventingInfo.getConsumedEvents();
            EventInfo eventInfo = consumedEventInfos.get(eventName);

            //
            if (trace)
            {
               log.trace("Attempt to obtain for event " + eventName + " its payload class " + srcPayloadClassName + " in the application " + applicationId +
                  " for portlet " + container.getInfo());
            }

            //
            Class dstPayloadClass = null;
            if (eventInfo != null)
            {
               ContainerTypeInfo typeInfo = (ContainerTypeInfo)eventInfo.getType();

               //
               if (typeInfo != null)
               {
                  dstPayloadClass = typeInfo.getType();
                  if (trace)
                  {
                     log.trace("Obtained for event " + eventName + " its payload class " + dstPayloadClass.getName() + " declared by the portlet meta data "
                        + containerId);
                  }
               }
               else
               {
                  if (trace)
                  {
                     log.trace("No type declared for event " + eventName + " declared by the portlet meta data " + containerId);
                  }
               }
            }


            //
            if (dstPayloadClass == null)
            {
               if (trace)
               {
                  log.trace("No event meta data declared by portlet " + containerId + " for event " + eventName + " will attempty " +
                     " to load same class name from the application " + applicationId + " classloader");
               }

               // We try to load the same class from the applicaton class loader
               try
               {
                  dstPayloadClass = applicationClassLoader.loadClass(srcPayloadClassName);
                  if (trace)
                  {
                     log.trace("Obtained matching event class " + dstPayloadClass.getName() + " in application " + applicationId + " for event " + eventName);
                  }
               }
               catch (ClassNotFoundException e)
               {
                  return new ErrorResponse("The application " + applicationId + " does not have access to the event payload class"
                     + srcPayloadClassName, e);
               }
               catch (NoClassDefFoundError e)
               {
                  return new ErrorResponse("The application " + applicationId + " does not have access to the event payload class"
                     + srcPayloadClassName, e);
               }
            }

            // We need maybe to perform some serialization to the classloader
            if (dstPayloadClass != srcPayload.getClass())
            {
               if (trace)
               {
                  log.trace("Need to convert event payload from class " + srcPayloadClassName + " to " + dstPayloadClass.getName());
               }
               try
               {
                  dstPayload = IOTools.clone(srcPayload, applicationClassLoader);
               }
               catch (ClassNotFoundException e)
               {
                  return new ErrorResponse("Could not convert the event payload from class " + srcPayloadClassName + " to class " + dstPayloadClass.getName(), e);
               }
               catch (IOException e)
               {
                  // The cause is likely a non compatible changes in class version
                  return new ErrorResponse("Could not convert the event payload from class " + srcPayloadClassName + " to class " + dstPayloadClass.getName(), e);
               }
            }
            else
            {
               dstPayload = srcPayload;
            }
         }

         // Set payload
         eventInvocation.setPayload(dstPayload);

         //
         try
         {
            return super.invoke(invocation);
         }
         finally
         {
            eventInvocation.setPayload(srcPayload);
         }
      }
      else
      {
         return super.invoke(invocation);
      }
   }
}
