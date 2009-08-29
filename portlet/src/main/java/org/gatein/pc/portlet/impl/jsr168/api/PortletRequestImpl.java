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
import org.gatein.common.util.Tools;
import org.gatein.common.util.ParameterMap;
import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.util.SimpleMultiValuedPropertyMap;
import org.gatein.common.util.ContentInfo;
import org.gatein.pc.portlet.impl.jsr168.PortletApplicationImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;
import org.gatein.pc.portlet.impl.jsr168.PortletUtils;
import org.gatein.pc.portlet.impl.jsr168.PortletRequestAttributes;
import org.gatein.pc.portlet.impl.jsr168.PortletRequestParameterMap;
import org.gatein.pc.portlet.impl.info.ContainerSecurityInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferencesInfo;
import org.gatein.pc.portlet.impl.info.ContainerNavigationInfo;
import org.gatein.pc.portlet.impl.info.ContainerPortletInfo;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.spi.ServerContext;
import org.gatein.pc.api.spi.SecurityContext;
import org.gatein.pc.api.spi.UserContext;
import org.gatein.pc.api.spi.WindowContext;
import org.gatein.pc.api.spi.ClientContext;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;
import org.gatein.pc.api.info.PortletManagedModeInfo;
import org.gatein.pc.api.Mode;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PreferencesValidator;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 * PortletRequest implemention. The parameter implementation is left to subclasses that can implement it differently.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7226 $
 */
public abstract class PortletRequestImpl implements PortletRequest
{

   /** . */
   protected static final Logger log = Logger.getLogger(PortletRequestImpl.class);

   /** . */
   protected PortletInvocation invocation;

   /** . */
   protected ClientContext clientContext;

   /** . */
   protected UserContext userContext;

   /** . */
   protected SecurityContext securityContext;

   /** . */
   protected ServerContext serverContext;

   /** . */
   protected WindowContext windowContext;

   /** . */
   protected PortletPreferences preferences;

   /** . */
   protected PortletContainerImpl container;

   /** . */
   private HttpServletRequestWrapper realReq;

   /** . */
   protected PortalContext portalContext;

   /** . */
   protected PortletSessionImpl psession;

   /** . */
   protected int sessionStatus;

   /** . */
   protected ContainerNavigationInfo navigationInfo;

   /** . */
   private MultiValuedPropertyMap<String> properties;

   /** . */
   protected final PortletRequestAttributes attributes;

   /** . */
   protected final PortletRequestParameterMap requestParameterMap;

   /** . */
   protected final Set<org.gatein.pc.api.Mode> supportedModes;

   /** . */
   protected final Set<org.gatein.pc.api.WindowState> supportedWindowStates;

   /** . */
   private final String contextPath;

   public PortletRequestImpl(PortletContainerImpl container, PortletInvocation invocation)
   {
      int mode = this instanceof RenderRequest ? PortletPreferencesImpl.RENDER : PortletPreferencesImpl.ACTION;
      PropertyContext prefs = (PropertyContext)invocation.getAttribute(PropertyContext.PREFERENCES_ATTRIBUTE);
      PreferencesValidator validator = container.getPreferencesValidator();
      ContainerPortletInfo info = container.getInfo();
      ContainerPreferencesInfo containerPrefs = info.getPreferences();
      ContainerNavigationInfo navigationInfo = info.getNavigation();
      UserContext userContext = invocation.getUserContext();
      HttpServletRequestWrapper realReq = new HttpServletRequestWrapper(invocation.getDispatchedRequest());

      //
      PortletRequestAttributes attributes = new PortletRequestAttributes(invocation.getSecurityContext(), container, userContext, realReq);
      if (invocation.getRequestAttributes() != null)
      {
         attributes.setAttributeMap(invocation.getRequestAttributes());
      }

      //
      this.contextPath = (String)invocation.getDispatchedRequest().getAttribute("javax.servlet.include.context_path");
      this.invocation = invocation;
      this.userContext = userContext;
      this.securityContext = invocation.getSecurityContext();
      this.serverContext = invocation.getServerContext();
      this.clientContext = invocation.getClientContext();
      this.windowContext = invocation.getWindowContext();
      this.container = container;
      this.realReq = realReq;
      this.portalContext = new PortalContextImpl(invocation.getPortalContext());
      this.attributes = attributes;
      this.preferences = new PortletPreferencesImpl(prefs, containerPrefs, validator, mode);
      this.navigationInfo = navigationInfo;
      this.requestParameterMap = PortletRequestParameterMap.create(navigationInfo, invocation);
      this.supportedModes = buildSupportedModes();
      this.supportedWindowStates = buildSupportedWindowState();
   }

   // PLT.11.1.1

   public String getParameter(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Name should not be null");
      }

      //
      ParameterMap parameters = requestParameterMap.getParameters();

      //
      return parameters != null ? parameters.getValue(name) : null;
   }

   public Enumeration<String> getParameterNames()
   {
      ParameterMap parameters = requestParameterMap.getParameters();

      //
      return parameters != null ? Collections.enumeration(parameters.keySet()) : (Enumeration<String>)Tools.EMPTY_ENUMERATION;
   }

   public String[] getParameterValues(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Name shoudl not be null");
      }

      //
      ParameterMap parameters = requestParameterMap.getParameters();

      //
      return parameters != null ? parameters.getValues(name) : null;
   }

   public Map<String, String[]> getParameterMap()
   {
      ParameterMap parameters = requestParameterMap.getParameters();

      //
      if (parameters != null)
      {
         return Collections.unmodifiableMap(parameters);
      }
      else
      {
         return Collections.emptyMap();
      }
   }

   public WindowState getWindowState()
   {
      String s = invocation.getWindowState().toString();
      return PortletUtils.decodeWindowState(s);
   }

   public PortletMode getPortletMode()
   {
      String s = invocation.getMode().toString();
      return PortletUtils.decodePortletMode(s);
   }

   // PLT.11.1.3

   public Object getAttribute(String name) throws IllegalArgumentException
   {
      return attributes.getAttribute(name);
   }

   public Enumeration<String> getAttributeNames()
   {
      return Tools.toEnumeration(attributes.getAttributeNames());
   }

   public void setAttribute(String name, Object value)
   {
      attributes.setAttribute(name, value);
   }

   public void removeAttribute(String name)
   {
      attributes.removeAttribute(name);
   }

   // PLT.11.1.4

   public String getProperty(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name must not be null");
      }

      //
      return getProperties().getValue(name);
   }

   public Enumeration<String> getProperties(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name must not be null");
      }

      //
      List<String> properties = getProperties().getValues(name);

      //
      if (properties == null)
      {
         return Tools.emptyEnumeration();
      }
      else
      {
         return Collections.enumeration(properties);
      }
   }

   public Enumeration<String> getPropertyNames()
   {
      Set<String> names = getProperties().keySet();

      //
      return Collections.enumeration(names);
   }

   // PLT.11.1.5

   public String getContextPath()
   {
      return contextPath;
   }

   // PLT.11.1.6

   public String getAuthType()
   {
      return securityContext.getAuthType();
   }

   public String getRemoteUser()
   {
      return securityContext.getRemoteUser();
   }

   public Principal getUserPrincipal()
   {
      return securityContext.getUserPrincipal();
   }

   public boolean isUserInRole(String roleName)
   {
      // Get the map role name to role link
      ContainerSecurityInfo securityRoleRefsMap = container.getInfo().getSecurity();

      // Process the role link
      String roleLink = securityRoleRefsMap.getRoleRef(roleName);

      //
      if (roleLink == null)
      {
         if (securityRoleRefsMap.containsRoleRef(roleName))
         {
            // The role name exist without a role link value
            return securityContext.isUserInRole(roleName);
         }
         else
         {
            // No role name is defined
            return false;
         }
      }
      else
      {
         // We have the role link value
         return securityContext.isUserInRole(roleLink);
      }
   }

   public boolean isSecure()
   {
      return securityContext.isSecure();
   }

   // PLT.11.1.7

   public String getResponseContentType()
   {
      return invocation.getContext().getMarkupInfo().getMediaType().getValue();
   }

   public Enumeration<String> getResponseContentTypes()
   {
      return Tools.toEnumeration(getResponseContentType());
   }

   // PLT.11.1.8

   public Locale getLocale()
   {
      return userContext.getLocale();
   }

   // PLT.11.1.9

   public boolean isPortletModeAllowed(PortletMode portletMode)
   {
      if (portletMode == null)
      {
         // The spec does not give way to handle that case properly
         log.warn("Try to test a null portlet mode");
         return false;
      }
      else
      {
         return supportedModes.contains(Mode.create(portletMode.toString()));
      }
   }

   // PLT.11.1.10

   public boolean isWindowStateAllowed(WindowState windowState)
   {
      return supportedWindowStates.contains(org.gatein.pc.api.WindowState.create(windowState.toString()));
   }

   //

   public PortletSession getPortletSession()
   {
      return getPortletSession(true);
   }

   public PortletSession getPortletSession(boolean create)
   {
      // Dereference an existing session if it is not valid
      if (psession != null && !psession.isValid())
      {
         psession = null;
      }

      // If we have a session here we are sure it is valid and ok to return it
      if (psession != null)
      {
         // So we do nothing
      }
      else if (create)
      {
         // For sure we need a session we will obtain a valid one
         HttpSession hsession = realReq.getSession();
         PortletApplicationImpl portletApp = (PortletApplicationImpl)container.getPortletApplication();
         psession = new PortletSessionImpl(hsession, windowContext.getId(), portletApp.getPortletContext());
      }
      else
      {
         // Here we can try an existing session but it may return null
         HttpSession hsession = realReq.getSession(false);

         //
         if (hsession != null)
         {
            PortletApplicationImpl portletApp = (PortletApplicationImpl)container.getPortletApplication();
            psession = new PortletSessionImpl(hsession, windowContext.getId(), portletApp.getPortletContext());
         }
      }

      //
      return psession;
   }

   public PortalContext getPortalContext()
   {
      return portalContext;
   }

   public String getRequestedSessionId()
   {
      return realReq.getRequestedSessionId();
   }

   public boolean isRequestedSessionIdValid()
   {
      return realReq.isRequestedSessionIdValid();
   }

   public Enumeration<Locale> getLocales()
   {
      return Collections.enumeration(userContext.getLocales());
   }

   public String getScheme()
   {
      return serverContext.getScheme();
   }

   public String getServerName()
   {
      return serverContext.getServerName();
   }

   public int getServerPort()
   {
      return serverContext.getServerPort();
   }

   public PortletPreferences getPreferences()
   {
      return preferences;
   }

   //

   public PortletRequestAttributes getAttributes()
   {
      return attributes;
   }

   public String getWindowID()
   {
      return windowContext.getId();
   }

   public Cookie[] getCookies()
   {
      List<Cookie> cookies = clientContext.getCookies();

      //
      if (cookies.isEmpty())
      {
         return null;
      }
      else
      {
         return cookies.toArray(new Cookie[cookies.size()]);
      }
   }

   public Map<String, String[]> getPrivateParameterMap()
   {
      ParameterMap parameters = requestParameterMap.getPrivateParameters();

      //
      if (parameters != null)
      {
         return Collections.unmodifiableMap(parameters);
      }
      else
      {
         return Collections.emptyMap();
      }
   }

   public Map<String, String[]> getPublicParameterMap()
   {
      ParameterMap parameters = requestParameterMap.getPublicParameters();

      //
      if (parameters != null)
      {
         return Collections.unmodifiableMap(parameters);
      }
      else
      {
         return Collections.emptyMap();
      }
   }

   public final HttpServletRequestWrapper getRealRequest()
   {
      return realReq;
   }

   private MultiValuedPropertyMap<String> getProperties()
   {
      if (properties == null)
      {
         SimpleMultiValuedPropertyMap<String> properties = new SimpleMultiValuedPropertyMap<String>();
         properties.append(clientContext.getProperties());
         initProperties(properties);

         //
         this.properties = properties;
      }

      //
      return properties;
   }

   protected void initProperties(MultiValuedPropertyMap<String> properties)
   {
   }

   /**
    * Build the initial set of supported modes.
    *
    * @return the set of portlet modes
    */
   private Set<org.gatein.pc.api.Mode> buildSupportedModes()
   {
      // Get content type
      ContentInfo si = invocation.getContext().getMarkupInfo();

      //
      org.gatein.pc.api.spi.PortalContext  portalContext =invocation.getPortalContext();

      // Get the modes for this content type
      CapabilitiesInfo capabilities = container.getInfo().getCapabilities();

      // Add all the modes
      Set<org.gatein.pc.api.Mode> modes = new HashSet<org.gatein.pc.api.Mode>();
      for (ModeInfo modeInfo : capabilities.getModes(si.getMediaType()))
      {
         org.gatein.pc.api.Mode mode = modeInfo.getMode();

         //
         if (modeInfo instanceof PortletManagedModeInfo || portalContext.getModes().contains(mode))
         {
            modes.add(mode);
         }
      }

      //
      return modes;
   }

   /**
    * Build the initial set of supported modes.
    *
    * @return the set of portlet modes
    */
   private Set<org.gatein.pc.api.WindowState> buildSupportedWindowState()
   {
      return new HashSet<org.gatein.pc.api.WindowState>(invocation.getPortalContext().getWindowStates());
   }
}
