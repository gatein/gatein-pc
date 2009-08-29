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
package org.gatein.pc.portlet.impl.jsr168.taglib;

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.aspects.ContextDispatcherInterceptor;

import javax.portlet.PortletConfig;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletPreferences;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Map;
import java.util.HashMap;

/**
 * Superclass of the portlet tags for the JSR 168 Portlet specification.
 * <p/>
 * Provides hooks into the portlet environment
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 */
public class PortletTag extends TagSupport
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 8522925340258546845L;

   protected HttpServletRequest getDispatchedRequest()
   {
      PortletInvocation invocation = getInvocation();
      return invocation.getDispatchedRequest();
   }

   protected PortletRequest getPortletRequest()
   {
      HttpServletRequest dreq = getDispatchedRequest();
      return (PortletRequest)dreq.getAttribute("javax.portlet.request");
   }

   protected PortletResponse getPortletResponse()
   {
      HttpServletRequest dreq = getDispatchedRequest();
      return (PortletResponse)dreq.getAttribute("javax.portlet.response");
   }

   protected PortletConfig getConfig()
   {
      HttpServletRequest dreq = getDispatchedRequest();
      return (PortletConfig)dreq.getAttribute("javax.portlet.config");
   }

   protected RenderRequest getRenderRequest()
   {
      if (getPortletRequest() instanceof RenderRequest)
      {
         return (RenderRequest)getPortletRequest();
      }
      else
      {
         return null;
      }
   }
   protected RenderResponse getRenderResponse()
   {
      if (getPortletResponse() instanceof RenderResponse)
      {
         return (RenderResponse)getPortletResponse();
      }
      else
      {
         return null;
      }
   }

   protected ActionRequest getActionRequest()
   {
      if (getPortletRequest() instanceof ActionRequest)
      {
         return (ActionRequest)getPortletRequest();
      }
      else
      {
         return null;
      }
   }
   protected ActionResponse getActionResponse()
   {
      if (getPortletResponse() instanceof ActionResponse)
      {
         return (ActionResponse)getPortletResponse();
      }
      else
      {
         return null;
      }
   }

   protected EventRequest getEventRequest()
   {
      if (getPortletRequest() instanceof EventRequest)
      {
         return (EventRequest)getPortletRequest();
      }
      else
      {
         return null;
      }
   }
   protected EventResponse getEventResponse()
   {
      if (getPortletResponse() instanceof EventResponse)
      {
         return (EventResponse)getPortletResponse();
      }
      else
      {
         return null;
      }
   }

   protected ResourceRequest getResourceRequest()
   {
      if (getPortletRequest() instanceof ResourceRequest)
      {
         return (ResourceRequest)getPortletRequest();
      }
      else
      {
         return null;
      }
   }
   protected ResourceResponse getResourceResponse()
   {
      if (getPortletResponse() instanceof ResourceResponse)
      {
         return (ResourceResponse)getPortletResponse();
      }
      else
      {
         return null;
      }
   }




   protected PortletSession getSession()
   {
      return getPortletRequest().getPortletSession(false);
   }

   protected Map<String, Object> getPortletSessionScope()
   {
      if (getSession() != null)
      {
         return getSession().getAttributeMap();
      }
      else
      {
         return new HashMap<String, Object>();
      }
   }

   protected PortletPreferences getPortletPreferences()
   {
      return getPortletRequest().getPreferences();
   }

   protected Map<String, String[]> getPortletPreferenceValues()
   {
      return getPortletPreferences().getMap();
   }

   /**
    * Set up the environment for accessing the portlat environment
    *
    * @return The invocation that called the superclasses of this tag
    */
   protected PortletInvocation getInvocation()
   {
      // Get the invocation
      ServletRequest req = pageContext.getRequest();
      return (PortletInvocation)req.getAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);
   }
}
