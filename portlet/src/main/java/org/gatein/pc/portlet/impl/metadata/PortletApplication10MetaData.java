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
package org.gatein.pc.portlet.impl.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gatein.pc.portlet.impl.metadata.adapter.CustomPortletModeAdapter;
import org.gatein.pc.portlet.impl.metadata.adapter.CustomWindowStateAdapter;
import org.gatein.pc.portlet.impl.metadata.adapter.PortletListAdapter;
import org.gatein.pc.portlet.impl.metadata.adapter.UserAttributeAdapter;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.metadata.security.SecurityConstraintMetaData;
import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;
import org.gatein.pc.portlet.impl.metadata.CustomWindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.CustomPortletModeMetaData;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */

@XmlRootElement(name = "portlet-app")
@XmlType(name = "portlet-appType")
public class PortletApplication10MetaData
{
   /** The portlet application id. */
   private String id;

   /** The portlet application version. */
   private String version;

   /** A bunch of portlets. */
   private Map<String, PortletMetaData> portlets;

   /** The user attributes. */
   private Map<String, UserAttributeMetaData> userAttributes;

   /** The custom portlet mode. */
   private Map<String, CustomPortletModeMetaData> customPortletModes;

   /** The custom window states. */
   private Map<String, CustomWindowStateMetaData> customWindowStates;

   /** The security constraints */
   private List<SecurityConstraintMetaData> securityConstraints;

   @XmlAttribute(name = "id")
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @XmlAttribute(name = "version")
   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   @XmlElement(name = "portlet")
   @XmlJavaTypeAdapter(PortletListAdapter.class)
   public Map<String, PortletMetaData> getPortlets()
   {
      return portlets;
   }

   public void setPortlets(Map<String, PortletMetaData> portlets)
   {
      // Adding reference to the portlet application metadata
      for( PortletMetaData p : portlets.values())
      {
         p.setPortletApplication(this);
      }
      this.portlets = portlets;
   }

   public Collection<PortletMetaData> getPortletCollection()
   {
      return portlets != null ? portlets.values() : null;
   }

   public PortletMetaData getPortlet(String portletName)
   {
      return portlets.get(portletName);
   }
   
   public void addPortlet(PortletMetaData portlet)
   {
      if(this.portlets == null)
      {
         this.portlets = new LinkedHashMap<String, PortletMetaData>();
      }
      // Adding reference to the portlet application metadata
      portlet.setPortletApplication(this);
      this.portlets.put(portlet.getPortletName(), portlet);
   }

   @XmlElement(name = "user-attribute")
   @XmlJavaTypeAdapter(UserAttributeAdapter.class)
   public Map<String, UserAttributeMetaData> getUserAttributes()
   {
      return userAttributes;
   }

   public void setUserAttributes(Map<String, UserAttributeMetaData> userAttributes)
   {
      this.userAttributes = userAttributes;
   }
   
   public void addUserAttribute(UserAttributeMetaData userAttribute)
   {
      if( this.userAttributes == null )
      {
         this.userAttributes = new HashMap<String, UserAttributeMetaData>();
      }
      this.userAttributes.put(userAttribute.getName(), userAttribute);
   }

   @XmlElement(name = "custom-portlet-mode")
   @XmlJavaTypeAdapter(CustomPortletModeAdapter.class)
   public Map<String, CustomPortletModeMetaData> getCustomPortletModes()
   {
      return customPortletModes;
   }

   public void setCustomPortletModes(Map<String, CustomPortletModeMetaData> customPortletMode)
   {
      this.customPortletModes = customPortletMode;
   }
   
   public void addCustomPortletMode(CustomPortletModeMetaData portletMode)
   {
      if ( this.customPortletModes == null)
      {
         this.customPortletModes = new HashMap<String, CustomPortletModeMetaData>();
      }
      this.customPortletModes.put(portletMode.getPortletMode(), portletMode);
   }

   @XmlElement(name = "custom-window-state")
   @XmlJavaTypeAdapter(CustomWindowStateAdapter.class)
   public Map<String, CustomWindowStateMetaData> getCustomWindowStates()
   {
      return customWindowStates;
   }

   public void setCustomWindowStates(Map<String, CustomWindowStateMetaData> customWindowState)
   {
      this.customWindowStates = customWindowState;
   }
   
   public void addCustomWindowState(CustomWindowStateMetaData windowState)
   {
      if( this.customWindowStates == null )
      {
         this.customWindowStates = new HashMap<String, CustomWindowStateMetaData>();
      }
      this.customWindowStates.put(windowState.getWindowState(), windowState);
   }

   @XmlElement(name = "security-constraint")
   public List<SecurityConstraintMetaData> getSecurityConstraints()
   {
      return securityConstraints;
   }

   public void setSecurityConstraints(List<SecurityConstraintMetaData> securityConstraints)
   {
      this.securityConstraints = securityConstraints;
   }
   
   public void addSecurityConstraint(SecurityConstraintMetaData securityConstraint)
   {
      if(this.securityConstraints == null)
      {
         this.securityConstraints = new ArrayList<SecurityConstraintMetaData>();
      }
      this.securityConstraints.add(securityConstraint);
   }
}
