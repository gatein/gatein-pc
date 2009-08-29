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

package org.gatein.pc.mc.metadata.factory;

import org.apache.log4j.Logger;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.pc.api.TransportGuarantee;
import org.gatein.pc.portlet.impl.metadata.CustomPortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.CustomWindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.ListenerMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.DEFAULT_LOCALE;
import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.PORTLET_JSR_286_NS;
import org.gatein.pc.portlet.impl.metadata.PublicRenderParameterMetaData;
import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.DescribableMetaData;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.common.LocalizedDescriptionMetaData;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionMetaData;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionReferenceMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMappingMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletCacheScopeEnum;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletInfoMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletPreferenceMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletPreferencesMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SecurityRoleRefMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportedLocaleMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportsMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.WindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.security.PortletCollectionMetaData;
import org.gatein.pc.portlet.impl.metadata.security.SecurityConstraintMetaData;
import org.gatein.pc.portlet.impl.metadata.security.UserDataConstraintMetaData;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication10MetaData;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication20MetaData;
import org.jboss.xb.binding.GenericObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class PortletApplicationModelFactory implements GenericObjectModelFactory
{

   /** LocalizedDescriptionMetaData container for descriptions */
   private LocalizedStringBuilder descriptions = new LocalizedStringBuilder();

   /** LocalizedDescriptionMetaData container for displayNames */
   private LocalizedStringBuilder displayNames = new LocalizedStringBuilder();

   /** Guess what? The logger. */
   private static final Logger log = Logger.getLogger(PortletApplicationModelFactory.class);

   public Object newRoot(Object root, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {

      String version = attrs.getValue("version");
      String id = attrs.getValue("id");

      // portlet-app 2.0 or 1.0
      PortletApplication10MetaData md = PORTLET_JSR_286_NS.equals(nsURI)
         ? new AnnotationPortletApplication20MetaData()
         : new AnnotationPortletApplication10MetaData();

      // Set portlet-app id
      md.setId(id);
      // Set portlet-app version
      md.setVersion(version);
      // return
      return md;
   }

   public Object completeRoot(Object root, UnmarshallingContext ctx, String nsURI, String name)
   {
      return root;
   }

   public Object newChild(Object object, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      String id = attrs.getValue("id");
      if (object instanceof DescribableMetaData)
      {

         if ("description".equals(localName))
         {
            String locale = attrs.getValue("xml:lang") != null ? attrs.getValue("xml:lang") : DEFAULT_LOCALE;
            LocalizedDescriptionMetaData localized = new LocalizedDescriptionMetaData(locale);
            this.descriptions.put(object, localized);
            return localized;
         }
      }

      // portlet-app
      if (object instanceof PortletApplication10MetaData)
      {
         if ("portlet".equals(localName))
         {
            return new PortletMetaData(id);
         }
         else if ("custom-portlet-mode".equals(localName))
         {
            return new CustomPortletModeMetaData(id);
         }
         else if ("custom-window-state".equals(localName))
         {
            return new CustomWindowStateMetaData(id);
         }
         else if ("user-attribute".equals(localName))
         {
            return new UserAttributeMetaData(id);
         }
         else if ("security-constraint".equals(localName))
         {
            return new SecurityConstraintMetaData(id);
         }
      }

      // portlet-app 2.0
      if (object instanceof PortletApplication20MetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         if ("public-render-parameter".equals(localName))
         {
            return new PublicRenderParameterMetaData(id);
         }
         else if ("event-definition".equals(localName))
         {
            return new EventDefinitionMetaData(id);
         }
         else if ("filter".equals(localName))
         {
            return new FilterMetaData();
         }
         else if ("filter-mapping".equals(localName))
         {
            return new FilterMappingMetaData();
         }
         else if ("listener".equals(localName))
         {
            return new ListenerMetaData(id);
         }
         else if ("container-runtime-option".equals(localName))
         {
            return new ContainerRuntimeMetaData();
         }
      }

      // portlet
      if (object instanceof PortletMetaData)
      {
         if ("init-param".equals(localName))
         {
            return new InitParamMetaData(id);
         }
         else if ("display-name".equals(localName))
         {
            String locale = attrs.getValue("xml:lang") != null ? attrs.getValue("xml:lang") : DEFAULT_LOCALE;
            LocalizedDescriptionMetaData localized = new LocalizedDescriptionMetaData(locale);
            this.displayNames.put(object, localized);
            return localized;
         }
         else if ("supports".equals(localName))
         {
            return new SupportsMetaData(id);
         }
         else if ("supported-locale".equals(localName))
         {
            return new SupportedLocaleMetaData();
         }
         else if ("portlet-info".equals(localName))
         {
            return new PortletInfoMetaData(id);
         }
         else if ("portlet-preferences".equals(localName))
         {
            return new PortletPreferencesMetaData(id);
         }
         else if ("security-role-ref".equals(localName))
         {
            return new SecurityRoleRefMetaData(id);
         }
         else if ("supported-processing-event".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            return new EventDefinitionReferenceMetaData(id);
         }
         else if ("supported-publishing-event".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            return new EventDefinitionReferenceMetaData(id);
         }
         else if ("container-runtime-option".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            return new ContainerRuntimeMetaData();
         }
      }

      // security-constraint
      if (object instanceof SecurityConstraintMetaData)
      {
         if ("portlet-collection".equals(localName))
         {
            return new PortletCollectionMetaData();
         }
         else if ("user-data-constraint".equals(localName))
         {
            return new UserDataConstraintMetaData(id);
         }
         else if ("display-name".equals(localName))
         {
            String locale = attrs.getValue("xml:lang") != null ? attrs.getValue("xml:lang") : DEFAULT_LOCALE;
            LocalizedDescriptionMetaData localized = new LocalizedDescriptionMetaData(locale);
            this.displayNames.put(object, localized);
            return localized;
         }
      }

      // supports
      if (object instanceof SupportsMetaData)
      {
         if ("portlet-mode".equals(localName))
         {
            return new PortletModeMetaData();
         }
         else if ("window-state".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            return new WindowStateMetaData();
         }
      }

      // preference (in portlet-preferences)
      if (object instanceof PortletPreferencesMetaData)
      {
         if ("preference".equals(localName))
         {
            return new PortletPreferenceMetaData(id);
         }
      }

      // filter
      if (object instanceof FilterMetaData)
      {
         if ("init-param".equals(localName))
         {
            return new InitParamMetaData(id);
         }
         else if ("display-name".equals(localName))
         {
            String locale = attrs.getValue("xml:lang") != null ? attrs.getValue("xml:lang") : DEFAULT_LOCALE;
            LocalizedDescriptionMetaData localized = new LocalizedDescriptionMetaData(locale);
            this.displayNames.put(object, localized);
            return localized;
         }
      }

      // listener
      if (object instanceof ListenerMetaData)
      {
         if ("display-name".equals(localName))
         {
            String locale = attrs.getValue("xml:lang") != null ? attrs.getValue("xml:lang") : DEFAULT_LOCALE;
            LocalizedDescriptionMetaData localized = new LocalizedDescriptionMetaData(locale);
            this.displayNames.put(object, localized);
            return localized;
         }
      }

      return null;
   }

   public void addChild(Object parent, Object child, UnmarshallingContext nav, String nsURI, String localName)
   {

      if (child instanceof DescribableMetaData)
      {
         DescribableMetaData md = (DescribableMetaData)child;
         LocalizedString d = this.descriptions.getLocalizedString(md);
         md.setDescription(d);
      }

      // portlet app
      if (parent instanceof PortletApplication10MetaData)
      {
         PortletApplication10MetaData md = (PortletApplication10MetaData)parent;
         // add portlet
         if (child instanceof PortletMetaData)
         {
            PortletMetaData portlet = (PortletMetaData)child;
            portlet.setDisplayName(this.displayNames.getLocalizedString(portlet));
            md.addPortlet(portlet);
         }
         // add custom-portlet-mode
         else if (child instanceof CustomPortletModeMetaData)
         {
            md.addCustomPortletMode((CustomPortletModeMetaData)child);
         }
         // add custom window state
         else if (child instanceof CustomWindowStateMetaData)
         {
            md.addCustomWindowState((CustomWindowStateMetaData)child);
         }
         // add user-attribute
         else if (child instanceof UserAttributeMetaData)
         {
            md.addUserAttribute((UserAttributeMetaData)child);
         }
         // add security-constraint
         else if (child instanceof SecurityConstraintMetaData)
         {
            SecurityConstraintMetaData security = (SecurityConstraintMetaData)child;
            // display-name
            security.setDisplayName(this.displayNames.getLocalizedString(security));
            md.addSecurityConstraint(security);
         }
      }

      // portlet-app 2.0
      if (parent instanceof PortletApplication20MetaData)
      {
         PortletApplication20MetaData md = (PortletApplication20MetaData)parent;
         // add public-render-parameter
         if (child instanceof PublicRenderParameterMetaData)
         {
            md.addPublicRenderParameter((PublicRenderParameterMetaData)child);
         }
         // add event-defintion
         else if (child instanceof EventDefinitionMetaData)
         {
            md.addEventDefinition((EventDefinitionMetaData)child);
         }
         // add filter
         else if (child instanceof FilterMetaData)
         {
            FilterMetaData filter = (FilterMetaData)child;
            filter.setDisplayName(this.displayNames.getLocalizedString(filter));
            md.addFilter(filter);
         }
         // add filter-mapping
         else if (child instanceof FilterMappingMetaData)
         {
            md.addFilterMapping((FilterMappingMetaData)child);
         }
         // add container-runtime-option
         else if (child instanceof ContainerRuntimeMetaData)
         {
            md.addContainerRuntime((ContainerRuntimeMetaData)child);
         }
         // add listener
         else if (child instanceof ListenerMetaData)
         {
            ListenerMetaData listener = (ListenerMetaData)child;
            listener.setDisplayName(this.displayNames.getLocalizedString(listener));
            md.addListener(listener);
         }
      }

      // portlet
      if (parent instanceof PortletMetaData)
      {
         PortletMetaData md = (PortletMetaData)parent;
         // init-param
         if (child instanceof InitParamMetaData)
         {
            md.addInitParam((InitParamMetaData)child);
         }
         // supports
         else if (child instanceof SupportsMetaData)
         {
            md.addSupport((SupportsMetaData)child);
         }
         // supported-locale
         else if (child instanceof SupportedLocaleMetaData)
         {
            md.addSupportedLocale((SupportedLocaleMetaData)child);
         }
         // portlet-info
         else if (child instanceof PortletInfoMetaData)
         {
            md.setPortletInfo((PortletInfoMetaData)child);
         }
         // portlet-preferences
         else if (child instanceof PortletPreferencesMetaData)
         {
            md.setPortletPreferences((PortletPreferencesMetaData)child);
         }
         // security-role-ref
         else if (child instanceof SecurityRoleRefMetaData)
         {
            md.addSecurityRoleRef((SecurityRoleRefMetaData)child);
         }
         // supported-events
         else if (child instanceof EventDefinitionReferenceMetaData)
         {
            if ("supported-processing-event".equals(localName))
            {
               md.addSupportedProcessingEvent((EventDefinitionReferenceMetaData)child);
            }
            else if ("supported-publishing-event".equals(localName))
            {
               md.addSupportedPublishingEvent((EventDefinitionReferenceMetaData)child);
            }
         }
         // container-runtime-option
         else if (child instanceof ContainerRuntimeMetaData)
         {
            md.addContainerRuntime((ContainerRuntimeMetaData)child);
         }
      }

      // adding user-data-constraint and portlet-name(s) to security-constraint
      if (parent instanceof SecurityConstraintMetaData)
      {
         SecurityConstraintMetaData md = (SecurityConstraintMetaData)parent;
         // add user-data-constraint
         if (child instanceof UserDataConstraintMetaData)
         {
            md.setUserDataConstraint((UserDataConstraintMetaData)child);
         }
         // add portlet-collection
         else if (child instanceof PortletCollectionMetaData)
         {
            md.setPortletList((PortletCollectionMetaData)child);
         }
      }

      // filter
      if (parent instanceof FilterMetaData)
      {
         FilterMetaData md = (FilterMetaData)parent;
         // add init-param
         if (child instanceof InitParamMetaData)
         {
            md.addInitParam((InitParamMetaData)child);
         }
      }

      // add portlet-mode and window-state
      if (parent instanceof SupportsMetaData)
      {
         SupportsMetaData md = (SupportsMetaData)parent;
         if (child instanceof PortletModeMetaData)
         {
            md.addPortletMode((PortletModeMetaData)child);
         }
         else if (child instanceof WindowStateMetaData)
         {
            md.addWindowState((WindowStateMetaData)child);
         }
      }

      // add preference
      if (parent instanceof PortletPreferencesMetaData)
      {
         PortletPreferencesMetaData md = (PortletPreferencesMetaData)parent;
         if (child instanceof PortletPreferenceMetaData)
         {
            md.addPortletPreference((PortletPreferenceMetaData)child);
         }
      }
   }

   public void setValue(Object object, UnmarshallingContext nav, String nsURI, String localName, String value)
   {

      // JSR 286 portlet-app attributes - default-namespace and resource-bundle.
      if (object instanceof PortletApplication20MetaData)
      {
         PortletApplication20MetaData md = (PortletApplication20MetaData)object;
         if ("resource-bundle".equals(localName))
         {
            md.setResourceBundle(value);
         }
         else if ("default-namespace".equals(localName))
         {
            try
            {
               md.setDefaultNamespace(new URI(value));
            }
            catch (URISyntaxException e)
            {
               log.error("Invalid syntax for default-namespace: " + value);
            }
         }
      }

      // portlet
      if (object instanceof PortletMetaData)
      {
         PortletMetaData md = (PortletMetaData)object;
         if ("portlet-name".equals(localName))
         {
            md.setPortletName(value);
         }
         else if ("portlet-class".equals(localName))
         {
            md.setPortletClass(value);
         }
         else if ("cache-scope".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            md.setCacheScope(PortletCacheScopeEnum.valueOf(value));
         }
         else if ("expiration-cache".equals(localName))
         {
            md.setExpirationCache(Integer.valueOf(value));
         }
         else if ("resource-bundle".equals(localName))
         {
            md.setResourceBundle(value);
         }
         else if ("supported-public-render-parameter".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            md.addSupportedPublicRenderParameter(value);
         }
      }

      if (object instanceof LocalizedDescriptionMetaData)
      {
         LocalizedDescriptionMetaData localized = (LocalizedDescriptionMetaData)object;
         if ("description".equals(localName))
         {
            localized.setDescription(value);
         }
         else if ("display-name".equals(localName))
         {
            localized.setDescription(value);
         }
      }

      // init-param
      if (object instanceof InitParamMetaData)
      {
         InitParamMetaData md = (InitParamMetaData)object;
         if ("name".equals(localName))
         {
            md.setName(value);
         }
         else if ("value".equals(localName))
         {
            md.setValue(value);
         }
      }

      // supports
      if (object instanceof SupportsMetaData)
      {
         SupportsMetaData md = (SupportsMetaData)object;
         if ("mime-type".equals(localName))
         {
            md.setMimeType(value);
         }
      }

      // supports --- portlet-mode
      if (object instanceof PortletModeMetaData)
      {
         PortletModeMetaData md = (PortletModeMetaData)object;
         if ("portlet-mode".equals(localName))
         {
            md.setPortletMode(org.gatein.pc.api.Mode.create(value));
         }
      }

      // supports --- window-state
      if (object instanceof WindowStateMetaData)
      {
         WindowStateMetaData md = (WindowStateMetaData)object;
         if ("window-state".equals(localName))
         {
            md.setWindowState(org.gatein.pc.api.WindowState.create(value));
         }
      }

      // supported-locale
      if (object instanceof SupportedLocaleMetaData)
      {
         SupportedLocaleMetaData md = (SupportedLocaleMetaData)object;
         if ("supported-locale".equals(localName))
         {
            md.setLocale(value);
         }
      }

      // portlet-info
      if (object instanceof PortletInfoMetaData)
      {
         PortletInfoMetaData md = (PortletInfoMetaData)object;
         if ("title".equals(localName))
         {
            md.setTitle(value);
         }
         else if ("short-title".equals(localName))
         {
            md.setShortTitle(value);
         }
         else if ("keywords".equals(localName))
         {
            md.setKeywords(value);
         }
      }

      // portlet-preferences
      if (object instanceof PortletPreferencesMetaData)
      {
         PortletPreferencesMetaData md = (PortletPreferencesMetaData)object;
         if ("preferences-validator".equals(localName))
         {
            md.setPreferenceValidator(value);
         }
      }

      // preference
      if (object instanceof PortletPreferenceMetaData)
      {
         PortletPreferenceMetaData md = (PortletPreferenceMetaData)object;
         if ("name".equals(localName))
         {
            md.setName(value);
         }
         else if ("value".equals(localName))
         {
            md.addValue(value);
         }
         else if ("read-only".equals(localName))
         {
            md.setReadOnly(Boolean.valueOf(value));
         }
      }

      // security-role-ref
      if (object instanceof SecurityRoleRefMetaData)
      {
         SecurityRoleRefMetaData md = (SecurityRoleRefMetaData)object;
         if ("role-name".equals(localName))
         {
            md.setRoleName(value);
         }
         else if ("role-link".equals(localName))
         {
            md.setRoleLink(value);
         }
      }

      // supported events
      if (object instanceof EventDefinitionReferenceMetaData)
      {
         EventDefinitionReferenceMetaData md = (EventDefinitionReferenceMetaData)object;
         if ("qname".equals(localName))
         {
            md.setQname(nav.resolveQName(value));
         }
         else if ("name".equals(localName))
         {
            md.setName(value);
         }
      }

      // custom-portlet-mode
      if (object instanceof CustomPortletModeMetaData)
      {
         CustomPortletModeMetaData mode = (CustomPortletModeMetaData)object;
         if ("portlet-mode".equals(localName))
         {
            mode.setPortletMode(value);
         }
         else if ("portal-managed".equals(localName) && PORTLET_JSR_286_NS.equals(nsURI))
         {
            mode.setPortalManaged(Boolean.parseBoolean(value));
         }
      }

      // custom-window-state
      if (object instanceof CustomWindowStateMetaData)
      {
         CustomWindowStateMetaData md = (CustomWindowStateMetaData)object;
         if ("window-state".equals(localName))
         {
            md.setWindowState(value);
         }
      }

      // user-attribute
      if (object instanceof UserAttributeMetaData)
      {
         UserAttributeMetaData md = (UserAttributeMetaData)object;
         if ("name".equals(localName))
         {
            md.setName(value);
         }
      }

      // user-data-constraint
      if (object instanceof UserDataConstraintMetaData)
      {
         UserDataConstraintMetaData md = (UserDataConstraintMetaData)object;
         if ("transport-guarantee".equals(localName))
         {
            md.setTransportQuarantee(TransportGuarantee.valueOf(value));
         }
      }

      // portlet-collection in security-constraint
      if (object instanceof PortletCollectionMetaData)
      {
         PortletCollectionMetaData md = (PortletCollectionMetaData)object;
         if ("portlet-name".equals(localName))
         {
            md.addPortletname(value);
         }
      }

      // filter
      if (object instanceof FilterMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         FilterMetaData md = (FilterMetaData)object;
         if ("filter-class".equals(localName))
         {
            md.setFilterClass(value);
         }
         else if ("filter-name".equals(localName))
         {
            md.setFilterName(value);
         }
         else if ("lifecycle".equals(localName))
         {
            int end = value.length() - "_PHASE".length();
            String lifeCycle = value.substring(0, end > 0 ? end : 0);
            try
            {
               md.addLifecycle(LifeCyclePhase.valueOf(lifeCycle));
            }
            catch (IllegalArgumentException e)
            {
               throw new IllegalArgumentException("Invalid value for lifecycle. Valid values are [RENDER_PHASE, RESOURCE_PHASE, ACTION_PHASE, EVENT_PHASE]");
            }
         }
      }

      // filter-mapping
      if (object instanceof FilterMappingMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         FilterMappingMetaData md = (FilterMappingMetaData)object;
         if ("filter-name".equals(localName))
         {
            md.setName(value);
         }
         if ("portlet-name".equals(localName))
         {
            md.addPortletName(value);
         }
      }

      // event-defintion
      if (object instanceof EventDefinitionMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         EventDefinitionMetaData md = (EventDefinitionMetaData)object;
         if ("name".equals(localName))
         {
            md.setName(value);
         }
         else if ("qname".equals(localName))
         {
            md.setQname(nav.resolveQName(value));
         }
         else if ("value-type".equals(localName))
         {
            md.setValueType(value);
         }
         else if ("alias".equals(localName))
         {
            md.addAlias(nav.resolveQName(value));
         }
      }

      // public-render-parameter
      if (object instanceof PublicRenderParameterMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         PublicRenderParameterMetaData md = (PublicRenderParameterMetaData)object;
         if ("identifier".equals(localName))
         {
            md.setIdentifier(value);
         }
         else if ("name".equals(localName))
         {
            md.setName(value);
         }
         else if ("qname".equals(localName))
         {
            md.setQname(nav.resolveQName(value));
         }
         else if ("alias".equals(localName))
         {
            md.addAlias(nav.resolveQName(value));
         }
      }

      // container-runtime-option
      if (object instanceof ContainerRuntimeMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         ContainerRuntimeMetaData md = (ContainerRuntimeMetaData)object;
         if ("name".equals(localName))
         {
            md.setName(value);
         }
         else if ("value".equals(localName))
         {
            md.addValue(value);
         }
      }

      // listener
      if (object instanceof ListenerMetaData && PORTLET_JSR_286_NS.equals(nsURI))
      {
         ListenerMetaData md = (ListenerMetaData)object;
         if ("listener-class".equals(localName))
         {
            md.setListenerClass(value);
         }
      }
   }
}
