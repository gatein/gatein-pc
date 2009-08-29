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

import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.impl.jsr168.PortletParameterMap;
import org.gatein.pc.api.ActionURL;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.RenderURL;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.portlet.PortletURLGenerationListener;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6697 $
 */
public class PortletURLImpl extends BaseURLImpl implements PortletURL
{

   public static PortletURLImpl createActionURL(PortletInvocation invocation, PortletRequestImpl preq)
   {
      InternalActionURL url = new InternalActionURL(invocation.getNavigationalState());
      return new PortletURLImpl(invocation, preq, url, true);
   }

   public static PortletURLImpl createRenderURL(PortletInvocation invocation, PortletRequestImpl preq)
   {
      InternalRenderURL url = new InternalRenderURL(new PortletParameterMap(preq.navigationInfo));
      return new PortletURLImpl(invocation, preq, url, true);
   }

   /** . */
   private final InternalPortletURL url;

   private PortletURLImpl(
      PortletInvocation invocation,
      PortletRequestImpl preq,
      InternalPortletURL url,
      boolean filterable)
   {
      super(invocation, preq, filterable);

      //
      this.url = url;
   }

   public PortletURLImpl(PortletURLImpl original)
   {
      super(original);

      //
      this.url = original.url instanceof InternalRenderURL ? new InternalRenderURL((InternalRenderURL)original.url) : new InternalActionURL((InternalActionURL)original.url);
   }

   public void setWindowState(WindowState windowState) throws WindowStateException
   {
      if (!preq.isWindowStateAllowed(windowState))
      {
         throw new WindowStateException("Not supported", windowState);
      }
      url.windowState = windowState;
   }

   public void setPortletMode(PortletMode portletMode) throws PortletModeException
   {
      if (portletMode == null)
      {
         // The spec does not say the behaviour with null argument
         // we suppose it clear the current mode
         url.portletMode = null;
      }
      else
      {
         // Check possible
         if (!preq.isPortletModeAllowed(portletMode))
         {
            throw new PortletModeException("Not supported", portletMode);
         }

         // Set mode
         url.portletMode = portletMode;
      }
   }

   public PortletMode getPortletMode()
   {
      return url.portletMode;
   }

   public WindowState getWindowState()
   {
      return url.windowState;
   }

   public void removePublicRenderParameter(String name)
   {
      url.removePublicRenderParameter(name);
   }

   protected InternalContainerURL getContainerURL()
   {
      return url;
   }

   protected BaseURLImpl createClone()
   {
      return new PortletURLImpl(this);
   }

   protected void filter(PortletURLGenerationListener listener)
   {
      if (url instanceof InternalActionURL)
      {
         listener.filterActionURL(this);
      }
      else
      {
         listener.filterRenderURL(this);
      }
   }

   private static abstract class InternalPortletURL extends InternalContainerURL implements org.gatein.pc.api.PortletURL
   {

      /** . */
      protected WindowState windowState;

      /** . */
      protected PortletMode portletMode;

      protected InternalPortletURL()
      {
         this.windowState = null;
         this.portletMode = null;
      }

      protected InternalPortletURL(InternalPortletURL original)
      {
         this.windowState = original.windowState;
         this.portletMode = original.portletMode;
      }

      public org.gatein.pc.api.Mode getMode()
      {
         if (portletMode != null)
         {
            return org.gatein.pc.api.Mode.create(portletMode.toString());
         }
         return null;
      }

      public org.gatein.pc.api.WindowState getWindowState()
      {
         if (windowState != null)
         {
            return org.gatein.pc.api.WindowState.create(windowState.toString());
         }
         return null;
      }

      protected abstract void removePublicRenderParameter(String name);
   }

   private static class InternalActionURL extends InternalPortletURL implements ActionURL
   {

      /** . */
      private final ParametersStateString interactionState;

      /** . */
      private final StateString navigationalState;

      private InternalActionURL(StateString navigationalState)
      {
         this.interactionState = ParametersStateString.create();
         this.navigationalState = navigationalState;
      }

      private InternalActionURL(InternalActionURL original)
      {
         super(original);

         //
         this.interactionState = ParametersStateString.create(ParameterMap.clone(original.interactionState.getParameters()));
         this.navigationalState = original.navigationalState;
      }

      public StateString getInteractionState()
      {
         return interactionState;
      }

      protected void setParameter(String name, String value)
      {
         interactionState.setValue(name, value);
      }

      protected void setParameter(String name, String[] values)
      {
         interactionState.setValues(name, values);
      }

      protected void setParameters(Map<String, String[]> parameters)
      {
         interactionState.replace(parameters);
      }

      protected void removePublicRenderParameter(String name)
      {
      }

      protected Map<String, String[]> getParameters()
      {
         return ParameterMap.clone(interactionState.getParameters());
      }

      public StateString getNavigationalState()
      {
         return navigationalState;
      }
   }

   private static class InternalRenderURL extends InternalPortletURL implements RenderURL
   {

      private final PortletParameterMap parameters;

      private InternalRenderURL(PortletParameterMap parameters)
      {
         this.parameters = parameters;
      }

      private InternalRenderURL(InternalRenderURL original)
      {
         super(original);

         //
         this.parameters = new PortletParameterMap(original.parameters);
      }

      public StateString getNavigationalState()
      {
         return ParametersStateString.create(parameters.getPrivateMapSnapshot());
      }

      public Map<String, String[]> getPublicNavigationalStateChanges()
      {
         return parameters.getPublicMapSnapshot();
      }

      protected void setParameter(String name, String value)
      {
         parameters.setParameterValue(name, value);
      }

      protected void setParameter(String name, String[] values)
      {
         parameters.setParameterValues(name, values);
      }

      protected void setParameters(Map<String, String[]> parameterMap)
      {
         parameters.setMap(parameterMap);
      }

      protected void removePublicRenderParameter(String name)
      {
         parameters.removePublicParameterValue(name);
      }

      protected Map<String, String[]> getParameters()
      {
         return ParameterMap.clone(parameters.getMap());
      }
   }
}
