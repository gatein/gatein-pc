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
package org.gatein.pc.portlet.impl.metadata.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionReferenceMetaData;
import org.gatein.pc.portlet.impl.metadata.adapter.LocalizedStringAdapter;
import org.gatein.pc.portlet.impl.metadata.adapter.ContainerRuntimeAdapter;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.common.DescribableMetaData;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
@XmlType(name = "portletType",
      propOrder = {"id", "description", "portletName", "displayName", "portletClass", "initParams", "expirationCache", "cacheScope",
      "supports", "supportedLocale", "resourceBundle", "portletInfo", "portletPreferences", "securityRoleRef",
      "supportedProcessingEvent", "supportedPublishingEvent", "supportedPublicRenderParameters",
      "containerRuntimeOptions"})
public class PortletMetaData extends DescribableMetaData
{

   /** The portlet id */
   private String id;

   /** The portlet name */
   private String portletName;

   /** The portlet class */
   private String portletClass;

   /** The portlet display name */
   private LocalizedString displayName;

   /** The portlet init parameters */
   private List<InitParamMetaData> initParams;

   /** The portlet expiration cache */
   private int expirationCache;

   /** The cache scope */
   private PortletCacheScopeEnum cacheScope = PortletCacheScopeEnum.PRIVATE;

   /** The supports */
   private List<SupportsMetaData> supports;

   /** The supported locale */
   private List<SupportedLocaleMetaData> supportedLocale;

   /** The resource bundle */
   private String resourceBundle;

   /** The portlet info */
   private PortletInfoMetaData portletInfo;

   /** The portlet preferences */
   private PortletPreferencesMetaData portletPreferences;

   /** The portlet security role references */
   private List<SecurityRoleRefMetaData> securityRoleRef;

   /** The portlet event supported processing event */
   private List<EventDefinitionReferenceMetaData> supportedProcessingEvent;

   /** The portlet event supported publishing event */
   private List<EventDefinitionReferenceMetaData> supportedPublishingEvent;

   /** The portlet supported public render parameters */
   private List<String> supportedPublicRenderParameters;

   /** The portlet container runtime options */
   private Map<String, ContainerRuntimeMetaData> containerRuntimeOptions;
   
   /** Reference to the PortletApplicationMetaData */
   private PortletApplication10MetaData portletApplication;

   public PortletMetaData() {}
   
   public PortletMetaData(String id)
   {
      this.id = id;
   }
   
   @XmlAttribute(name = "id")
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @XmlElement(name = "portlet-name",
         required = true)
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   public String getPortletName()
   {
      return portletName;
   }

   public void setPortletName(String portletName)
   {
      this.portletName = portletName;
   }

   @XmlElement(name = "portlet-class",
         required = true)
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   public String getPortletClass()
   {
      return portletClass;
   }

   public void setPortletClass(String portletClass)
   {
      this.portletClass = portletClass;
   }

   @XmlElement(name = "display-name")
   @XmlJavaTypeAdapter(LocalizedStringAdapter.class)
   public LocalizedString getDisplayName()
   {
      return displayName;
   }

   public void setDisplayName(LocalizedString displayNames)
   {
      this.displayName = displayNames;
   }

   @XmlElement(name = "init-param")
   public List<InitParamMetaData> getInitParams()
   {
      return initParams;
   }

   public void setInitParams(List<InitParamMetaData> initParams)
   {
      this.initParams = initParams;
   }
   
   public void addInitParam(InitParamMetaData initParam)
   {
      if (this.initParams == null)
      {
         this.initParams = new ArrayList<InitParamMetaData>();
      }
      this.initParams.add(initParam);
   }

   @XmlElement(name = "expiration-cache")
   public int getExpirationCache()
   {
      return expirationCache;
   }

   public void setExpirationCache(int expirationCache)
   {
      this.expirationCache = expirationCache;
   }

   @XmlElement(name = "cache-scope",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS, type = PortletCacheScopeEnum.class)
   public PortletCacheScopeEnum getCacheScope()
   {
      return cacheScope;
   }

   public void setCacheScope(PortletCacheScopeEnum cacheScope)
   {
      this.cacheScope = cacheScope;
   }

   @XmlElement(name = "supports",
         required = true)
   public List<SupportsMetaData> getSupports()
   {
      return supports;
   }

   public void setSupports(List<SupportsMetaData> supports)
   {
      this.supports = supports;
   }
   
   public void addSupport(SupportsMetaData support)
   {
      if (this.supports == null)
      {
         this.supports = new ArrayList<SupportsMetaData>();
      }
      this.supports.add(support);
   }

   @XmlElement(name = "supported-locale")
//   @XmlJavaTypeAdapter(SupportedLocaleAdapter.class)
   public List<SupportedLocaleMetaData> getSupportedLocale()
   {
      return supportedLocale;
   }

   public void setSupportedLocale(List<SupportedLocaleMetaData> supportedLocale)
   {
      this.supportedLocale = supportedLocale;
   }
   
   public void addSupportedLocale(SupportedLocaleMetaData supportedLocale)
   {
      if(this.supportedLocale == null)
      {
         this.supportedLocale = new ArrayList<SupportedLocaleMetaData>();
      }
      this.supportedLocale.add(supportedLocale);
   }

   @XmlElement(name = "resource-bundle")
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   public String getResourceBundle()
   {
      return resourceBundle;
   }

   public void setResourceBundle(String resourceBundle)
   {
      this.resourceBundle = resourceBundle;
   }

   @XmlElement(name = "portlet-info")
   public PortletInfoMetaData getPortletInfo()
   {
      return portletInfo;
   }

   public void setPortletInfo(PortletInfoMetaData portletInfo)
   {
      this.portletInfo = portletInfo;
   }

   @XmlElement(name = "portlet-preferences")
   public PortletPreferencesMetaData getPortletPreferences()
   {
      return portletPreferences;
   }

   public void setPortletPreferences(PortletPreferencesMetaData portletPreferences)
   {
      this.portletPreferences = portletPreferences;
   }

   @XmlElement(name = "security-role-ref")
   public List<SecurityRoleRefMetaData> getSecurityRoleRef()
   {
      return securityRoleRef;
   }

   public void setSecurityRoleRef(List<SecurityRoleRefMetaData> securityRoleRef)
   {
      this.securityRoleRef = securityRoleRef;
   }
   
   public void addSecurityRoleRef(SecurityRoleRefMetaData securityRoleRef)
   {
      if (this.securityRoleRef == null)
      {
         this.securityRoleRef = new ArrayList<SecurityRoleRefMetaData>();
      }
      this.securityRoleRef.add(securityRoleRef);
   }

   @XmlElement(name = "supported-processing-event",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<EventDefinitionReferenceMetaData> getSupportedProcessingEvent()
   {
      return supportedProcessingEvent;
   }

   public void setSupportedProcessingEvent(List<EventDefinitionReferenceMetaData> supportedProcessingEvent)
   {
      this.supportedProcessingEvent = supportedProcessingEvent;
   }
   
   public void addSupportedProcessingEvent(EventDefinitionReferenceMetaData eventRef)
   {
      if (this.supportedProcessingEvent == null)
      {
         this.supportedProcessingEvent = new ArrayList<EventDefinitionReferenceMetaData>();
      }
      this.supportedProcessingEvent.add(eventRef);
   }

   @XmlElement(name = "supported-publishing-event",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<EventDefinitionReferenceMetaData> getSupportedPublishingEvent()
   {
      return supportedPublishingEvent;
   }

   public void setSupportedPublishingEvent(List<EventDefinitionReferenceMetaData> supportedPublishingEvent)
   {
      this.supportedPublishingEvent = supportedPublishingEvent;
   }
   
   public void addSupportedPublishingEvent(EventDefinitionReferenceMetaData eventRef)
   {
      if(this.supportedPublishingEvent == null)
      {
         this.supportedPublishingEvent = new ArrayList<EventDefinitionReferenceMetaData>();
      }
      this.supportedPublishingEvent.add(eventRef);
   }

   @XmlElement(name = "supported-public-render-parameter",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<String> getSupportedPublicRenderParameters()
   {
      return supportedPublicRenderParameters;
   }

   public void setSupportedPublicRenderParameters(List<String> supportedPublicRenderParameters)
   {
      this.supportedPublicRenderParameters = supportedPublicRenderParameters;
   }
   
   public void addSupportedPublicRenderParameter(String parameter)
   {
      if (this.supportedPublicRenderParameters == null)
      {
         this.supportedPublicRenderParameters = new ArrayList<String>();
      }
      this.supportedPublicRenderParameters.add(parameter);
   }

   @XmlElement(name = "container-runtime-option",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   @XmlJavaTypeAdapter(ContainerRuntimeAdapter.class)
   public Map<String, ContainerRuntimeMetaData> getContainerRuntimeOptions()
   {
      return containerRuntimeOptions;
   }

   public void setContainerRuntimeOptions(Map<String, ContainerRuntimeMetaData> containerRuntimeOptions)
   {
      this.containerRuntimeOptions = containerRuntimeOptions;
   }
   
   public void addContainerRuntime(ContainerRuntimeMetaData containerRuntimeOption)
   {
      if ( this.containerRuntimeOptions == null)
      {
         this.containerRuntimeOptions = new HashMap<String, ContainerRuntimeMetaData>();
      }
      this.containerRuntimeOptions.put(containerRuntimeOption.getName(), containerRuntimeOption);
   }
   
   /** . */
   public PortletApplication10MetaData getPortletApplication()
   {
      return portletApplication;
   }
   
   public void setPortletApplication(PortletApplication10MetaData portletApplication)
   {
      this.portletApplication = portletApplication;
   }
}
