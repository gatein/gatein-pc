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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.apache.log4j.Logger;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.util.Tools;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.portlet.impl.info.ContainerEventInfo;
import org.gatein.pc.portlet.impl.info.ContainerPortletApplicationInfo;
import org.gatein.pc.portlet.impl.info.ContainerTypeInfo;
import org.gatein.pc.portlet.impl.jsr168.PortletApplicationImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletParameterMap;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.HTTPRedirectionResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class StateAwareResponseImpl extends PortletResponseImpl implements StateAwareResponse
{

   /** . */
   private static final Logger log = Logger.getLogger(ActionResponseImpl.class);

   /** . */
   protected WantUpdate wantUpdate;

   /** . */
   protected WantRedirect wantRedirect;

   /** . */
   private List<UpdateNavigationalStateResponse.Event> events;

   public StateAwareResponseImpl(PortletInvocation invocation, PortletRequestImpl preq)
   {
      super(invocation, preq);
   }

   public PortletInvocationResponse getResponse()
   {
      Decision decision = wantRedirect;

      //
      if (wantRedirect == null)
      {
         decision = wantUpdate;
      }

      //
      if (decision == null)
      {
         decision = new WantUpdate();
      }

      //
      return decision.getResponse();
   }

   protected WantUpdate requireUpdate(String errorMsg)
   {
      if (wantRedirect != null)
      {
         throw new IllegalStateException(errorMsg);
      }
      else if (wantUpdate == null)
      {
         wantUpdate = new WantUpdate();
      }
      return wantUpdate;
   }

   protected void checkRedirect(String errorMsg)
   {
      if (wantUpdate != null)
      {
         throw new IllegalStateException(errorMsg);
      }
   }

   protected WantRedirect requireRedirect()
   {
      if (wantUpdate != null)
      {
         throw new IllegalStateException();
      }
      else if (wantRedirect == null)
      {
         wantRedirect = new WantRedirect();
      }
      return wantRedirect;
   }

   protected WantUpdate wantUpdate()
   {
      if (wantUpdate == null)
      {
         wantUpdate = new WantUpdate();
      }
      return wantUpdate;
   }

   public void setWindowState(WindowState windowState) throws WindowStateException
   {
      WantUpdate update = requireUpdate("Window state cannot be set after redirect");

      //
      if (!preq.isWindowStateAllowed(windowState))
      {
         throw new WindowStateException("Not supported", windowState);
      }

      //
      update.windowState = org.gatein.pc.api.WindowState.create(windowState.toString());
   }

   public void setPortletMode(PortletMode portletMode) throws PortletModeException
   {
      WantUpdate update = requireUpdate("Portlet mode cannot be set after redirect");

      //
      if (portletMode == null)
      {
         // The spec does not define that case
         // we just issue a warn
         log.warn("Set null portlet mode");
      }
      else
      {
         if (!preq.isPortletModeAllowed(portletMode))
         {
            throw new PortletModeException("Not supported", portletMode);
         }

         //
         update.mode = org.gatein.pc.api.Mode.create(portletMode.toString());
      }
   }

   public void setRenderParameters(Map<String, String[]> map)
   {
      WantUpdate update = requireUpdate("setRenderParameters cannot be called after redirect");

      //
      update.navigationalState.setMap(map);
   }

   public void setRenderParameter(String name, String value)
   {
      WantUpdate update = requireUpdate("setRenderParameter cannot be called after redirect");

      //
      update.navigationalState.setParameterValue(name, value);
   }

   public void setRenderParameter(String name, String[] values)
   {
      WantUpdate update = requireUpdate("setRenderParameter cannot be called after redirect");

      //
      update.navigationalState.setParameterValues(name, values);
   }

   public Map<String, String[]> getRenderParameterMap()
   {
      WantUpdate update = wantUpdate();

      //
      return update.navigationalState.getMap();
   }

   public void removePublicRenderParameter(String name)
   {
      WantUpdate update = wantUpdate();

      //
      update.navigationalState.removePublicParameterValue(name);
   }

   private static final Set<? extends Class<? extends Serializable>> acceptedFinalClasses = Tools.toSet(
      Boolean.class, Integer.class, Byte.class, Long.class, Float.class, Double.class, String.class, URI.class, UUID.class
   );

   /**
    * Returns true if the class requires a check of its JAXB annotation.
    *
    * @param clazz the class to check
    * @return true if the class requires the JAXB annotation
    */
   private boolean requiresJAXBAnnotation(Class<? extends Serializable> clazz)
   {
      // Since they are final a equals should be enough
      if (acceptedFinalClasses.contains(clazz))
      {
         return false;
      }

      //
      if (clazz.getName().startsWith("java"))
      {
         if (BigInteger.class.isAssignableFrom(clazz))
         {
            return false;
         }
         else if (BigDecimal.class.isAssignableFrom(clazz))
         {
            return false;
         }
         else if (Calendar.class.isAssignableFrom(clazz))
         {
            return false;
         }
         else if (Date.class.isAssignableFrom(clazz))
         {
            return false;
         }
         else if (QName.class.isAssignableFrom(clazz))
         {
            return false;
         }
      }

      //
      return true;
   }

   public void setEvent(QName name, Serializable value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("The portlet must specify a name when producing an event");
      }

      //
      if (value != null)
      {
         Class<? extends Serializable> valueType = value.getClass();

         // Check jaxb annotation
         if (requiresJAXBAnnotation(valueType))
         {
            boolean b = valueType.isAnnotationPresent(XmlRootElement.class);
            if (!b)
            {
               throw new IllegalArgumentException("The provided event value type " + value.getClass().getName() +
                  " does not have a valid jaxb annotation");
            }
         }

         // Check type
         ContainerEventInfo eventInfo = preq.container.getInfo().getEventing().getProducedEvents().get(name);

         // If we have event info it means we need to check the type validity otherwise it is a non declared event
         // and it is up to the controller to manage the payload.
         if (eventInfo != null)
         {
            ContainerTypeInfo typeInfo = eventInfo.getType();
            if (typeInfo != null)
            {
               Class expectedType = typeInfo.getType();

               //
               if (!expectedType.isInstance(value))
               {
                  throw new IllegalArgumentException("The provided event value type " + valueType.getName() +
                     " does not match the declared event type " + expectedType.getName());
               }
            }
         }
      }

      //
      queueEvent(name, value);
   }

   public void setEvent(String localName, Serializable value)
   {
      if (localName == null)
      {
         throw new IllegalArgumentException("The portlet must specify a local name when producing an event");
      }

      //
      ContainerPortletApplicationInfo info = ((PortletApplicationImpl)preq.container.getPortletApplication()).getInfo();
      QName name = new QName(info.getDefaultNamespace(), localName);
      setEvent(name, value);
   }

   public PortletMode getPortletMode()
   {
      throw new NotYetImplemented();
   }

   public WindowState getWindowState()
   {
      throw new NotYetImplemented();
   }

   protected final void queueEvent(QName name, Serializable value)
   {
      if (events == null)
      {
         events = new LinkedList<UpdateNavigationalStateResponse.Event>();
      }
      events.add(new UpdateNavigationalStateResponse.Event(name, value));
   }

   private abstract class Decision
   {

      protected abstract PortletInvocationResponse getResponse();

   }

   protected class WantUpdate extends Decision
   {

      /** The navigational state returned. */
      protected PortletParameterMap navigationalState = new PortletParameterMap(preq.navigationInfo);

      /** The new window state requested. */
      protected org.gatein.pc.api.WindowState windowState = new org.gatein.pc.api.WindowState(preq.getWindowState().toString());

      /** The new mode requested. */
      protected org.gatein.pc.api.Mode mode = new org.gatein.pc.api.Mode(preq.getPortletMode().toString());

      protected PortletInvocationResponse getResponse()
      {
         UpdateNavigationalStateResponse response = new UpdateNavigationalStateResponse();

         //
         response.setProperties(getProperties(false));
         response.setAttributes(preq.attributes.getAttributeMap());

         //
         response.setMode(mode);
         response.setWindowState(windowState);
         response.setPublicNavigationalStateUpdates(navigationalState.getPublicMapSnapshot());
         response.setNavigationalState(ParametersStateString.create(navigationalState.getPrivateMapSnapshot()));

         //
         if (events != null)
         {
            for (UpdateNavigationalStateResponse.Event event : events)
            {
               response.queueEvent(event);
            }
         }

         //
         return response;
      }
   }

   protected class WantRedirect extends Decision
   {

      /** . */
      protected String location;

      protected PortletInvocationResponse getResponse()
      {
         return new HTTPRedirectionResponse(location);
      }
   }
}
