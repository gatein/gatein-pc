/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.gatein.pc.portlet.impl.deployment.staxnav;

import org.gatein.common.i18n.LocaleFormat;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.common.util.ConversionException;
import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.TransportGuarantee;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.portlet.impl.metadata.CustomPortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.CustomWindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.ListenerMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.PublicRenderParameterMetaData;
import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
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
import org.staxnav.Naming;
import org.staxnav.StaxNavException;
import org.staxnav.StaxNavigator;
import org.staxnav.StaxNavigatorImpl;
import org.staxnav.ValueType;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.DEFAULT_LOCALE;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class PortletApplicationMetaDataBuilder
{

   private static final EnumSet<Element> NAME_OR_QNAME = EnumSet.of(Element.name, Element.qname);

   private static final ValueType<PortletCacheScopeEnum> PORTLET_CACHE_SCOPE = ValueType.get(PortletCacheScopeEnum.class);

   private static final ValueType<TransportGuarantee> TRANSPORT_GUARANTEE = ValueType.get(TransportGuarantee.class);

   private static final ValueType<LifeCyclePhase> LIFE_CYCLE = new ValueType<LifeCyclePhase>()
   {
      @Override
      protected LifeCyclePhase parse(String s) throws Exception
      {
         if (s.endsWith("_PHASE"))
         {
            return LifeCyclePhase.valueOf(s.substring(0, s.length() - 6));
         }
         else
         {
            throw new IllegalArgumentException("Value " + s + " is not legal");
         }
      }
   };

   private static final QName XML_LANG = new QName("http://www.w3.org/XML/1998/namespace", "lang");

   private static final String PORTLET_1_0 = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
   private static final String PORTLET_2_0 = "http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd";

   private LocalizedString readLocalizedString(StaxNavigator<Element> nav, Element element) throws ConversionException
   {
      Map<Locale, String> descriptions = new LinkedHashMap<Locale, String>();
      while (nav.next(element))
      {
         String lang = nav.getAttribute(XML_LANG);
         String description = nav.getContent();
         Locale locale = LocaleFormat.DEFAULT.getLocale(lang == null ? DEFAULT_LOCALE : lang);
         descriptions.put(locale, description);
      }
      if (descriptions.size() > 0)
      {
         return new LocalizedString(descriptions, new Locale(DEFAULT_LOCALE));
      }
      else
      {
         return null;
      }
   }

   private QName readQName(StaxNavigator<Element> nav)
   {
      String val = nav.getContent();
      int pos = val.indexOf(':');
      if (pos == -1)
      {
         return new QName(val);
      }
      else
      {
         String prefix = val.substring(0, pos);
         String localPart = val.substring(pos + 1);
         String uri = nav.getNamespaceByPrefix(prefix);
         if (uri == null)
         {
            throw new UnsupportedOperationException("todo");
         }
         else
         {
            return new QName(uri, localPart, prefix);
         }
      }
   }

   private Iterable<InitParamMetaData> readInitParams(StaxNavigator<Element> nav) throws ConversionException
   {
      List<InitParamMetaData> list = Collections.emptyList();
      while (nav.next(Element.init_param))
      {
         InitParamMetaData initParamMD = new InitParamMetaData();
         initParamMD.setId(nav.getAttribute("id"));
         initParamMD.setDescription(readLocalizedString(nav, Element.description));
         initParamMD.setName(getContent(nav, Element.name));
         initParamMD.setValue(getContent(nav, Element.value));
         if (list.isEmpty())
         {
            list = new ArrayList<InitParamMetaData>();
         }
         list.add(initParamMD);
      }
      return list;
   }

   private String getContent(StaxNavigator<Element> nav, Element element)
   {
      if (nav.next(element))
      {
         return nav.getContent();
      }
      else
      {
         throw new StaxNavException(nav.getLocation(), "Was expecting elemnt " + element + " to be present");
      }
   }

   public PortletApplication20MetaData build(InputStream is) throws Exception
   {

      PortletApplication20MetaData md = new PortletApplication20MetaData();

      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLStreamReader stream = factory.createXMLStreamReader(is);
      StaxNavigator<Element> nav = new StaxNavigatorImpl<Element>(new Naming.Enumerated.Simple<Element>(Element.class, null), stream);
      
      nav.setTrimContent(true);

      // For now we do it this way
      // but it's not correct
      String defaultNS = nav.getNamespaceByPrefix("");
      int version;
      if (PORTLET_1_0.equals(defaultNS))
      {
         md.setVersion("1.0");
         version = 1;
      }
      else if (PORTLET_2_0.equals(defaultNS))
      {
         md.setVersion("2.0");
         version = 2;
      }
      else
      {
         throw new UnsupportedOperationException();
      }

      //
      assert Element.portlet_app == nav.getName();

      //
      if (nav.child() == null)
      {
         return md;
      }

      //
      for (StaxNavigator<Element> portletNav : nav.fork(Element.portlet))
      {
         PortletMetaData portletMD = new PortletMetaData();

         //
         portletMD.setId(portletNav.getAttribute("id"));
         portletMD.setDescription(readLocalizedString(portletNav, Element.description));
         portletMD.setPortletName(getContent(portletNav, Element.portlet_name));
         portletMD.setDisplayName(readLocalizedString(portletNav, Element.display_name));
         portletMD.setPortletClass(getContent(portletNav, Element.portlet_class));

         //
         for (InitParamMetaData initParamMD : readInitParams(portletNav))
         {
            portletMD.addInitParam(initParamMD);
         }

         //
         if (portletNav.next(Element.expiration_cache))
         {
            portletMD.setExpirationCache(Integer.parseInt(portletNav.getContent()));
         }
         if (portletNav.next(Element.cache_scope))
         {
            portletMD.setCacheScope(PortletCacheScopeEnum.valueOf(portletNav.getContent()));
         }

         //
         while (portletNav.next(Element.supports))
         {
            SupportsMetaData supportsMD = new SupportsMetaData();
            supportsMD.setId(portletNav.getAttribute("id"));
            supportsMD.setMimeType(getContent(portletNav, Element.mime_type));
            while (portletNav.next(Element.portlet_mode)) {
               PortletModeMetaData portletModeMD = new PortletModeMetaData();
               portletModeMD.setPortletMode(Mode.create(portletNav.getContent()));
               supportsMD.addPortletMode(portletModeMD);
            }
            while (portletNav.next(Element.window_state)) {
               WindowStateMetaData windowStateMD = new WindowStateMetaData();
               windowStateMD.setWindowState(WindowState.create(portletNav.getContent()));
               supportsMD.addWindowState(windowStateMD);
            }
            portletMD.addSupport(supportsMD);
         }

         //
         while (portletNav.next(Element.supported_locale))
         {
            SupportedLocaleMetaData supportedLocaleMD = new SupportedLocaleMetaData();
            supportedLocaleMD.setLocale(portletNav.getContent());
            portletMD.addSupportedLocale(supportedLocaleMD);
         }
         if (portletNav.next(Element.resource_bundle))
         {
            portletMD.setResourceBundle(portletNav.getContent());
         }

         //
         if (portletNav.next(Element.portlet_info))
         {
            PortletInfoMetaData portletInfoMD = new PortletInfoMetaData();
            if (portletNav.next(Element.title))
            {
               portletInfoMD.setTitle(portletNav.getContent());
            }
            if (portletNav.next(Element.short_title))
            {
               portletInfoMD.setShortTitle(portletNav.getContent());
            }
            if (portletNav.next(Element.keywords))
            {
               portletInfoMD.setKeywords(portletNav.getContent());
            }
            portletMD.setPortletInfo(portletInfoMD);
         }

         //
         if (portletNav.next(Element.portlet_preferences))
         {
            PortletPreferencesMetaData portletPreferencesMD = new PortletPreferencesMetaData();
            while (portletNav.next(Element.preference))
            {
               PortletPreferenceMetaData portletPreferenceMD = new PortletPreferenceMetaData();
               portletPreferenceMD.setName(getContent(portletNav, Element.name));
               while (portletNav.next(Element.value))
               {
                  portletPreferenceMD.addValue(portletNav.getContent());
               }
               if (portletNav.next(Element.read_only))
               {
                  portletPreferenceMD.setReadOnly(portletNav.parseContent(ValueType.BOOLEAN));
               }
               portletPreferencesMD.addPortletPreference(portletPreferenceMD);
            }
            if (portletNav.next(Element.preferences_validator))
            {
               portletPreferencesMD.setPreferenceValidator(portletNav.getContent());
            }
            portletMD.setPortletPreferences(portletPreferencesMD);
         }

         //
         while (portletNav.next(Element.security_role_ref))
         {
            SecurityRoleRefMetaData securityRoleRefMD = new SecurityRoleRefMetaData();
            securityRoleRefMD.setDescription(readLocalizedString(portletNav, Element.description));
            securityRoleRefMD.setRoleName(getContent(portletNav, Element.role_name));
            if (portletNav.next(Element.role_link))
            {
               securityRoleRefMD.setRoleLink(portletNav.getContent());
            }
            portletMD.addSecurityRoleRef(securityRoleRefMD);
         }

         //
         while (portletNav.next(Element.supported_processing_event) || portletNav.next(Element.supported_publishing_event))
         {
            boolean processing = portletNav.getName() == Element.supported_processing_event;
            EventDefinitionReferenceMetaData eventDefinitionReferenceMD = new EventDefinitionReferenceMetaData();
            switch (portletNav.next(NAME_OR_QNAME))
            {
               case name:
                  eventDefinitionReferenceMD.setName(portletNav.getContent());
                  break;
               case qname:
                  eventDefinitionReferenceMD.setQname(readQName(portletNav));
                  break;
            }
            if (processing)
            {
               portletMD.addSupportedProcessingEvent(eventDefinitionReferenceMD);
            }
            else
            {
               portletMD.addSupportedPublishingEvent(eventDefinitionReferenceMD);
            }
         }
         while (portletNav.next(Element.supported_public_render_parameter))
         {
            portletMD.addSupportedPublicRenderParameter(portletNav.getContent());
         }
         while (portletNav.next(Element.container_runtime_option))
         {
            ContainerRuntimeMetaData containerRuntimeOptionMD = new ContainerRuntimeMetaData();
            containerRuntimeOptionMD.setName(getContent(portletNav, Element.name));
            while (portletNav.next(Element.value))
            {
               containerRuntimeOptionMD.addValue(portletNav.getContent());
            }
            portletMD.addContainerRuntime(containerRuntimeOptionMD);
         }

         //
         md.addPortlet(portletMD);
      }

      //
      for (StaxNavigator<Element> customPortletModeNav : nav.fork(Element.custom_portlet_mode))
      {
         CustomPortletModeMetaData customPortletModeMD = new CustomPortletModeMetaData();
         customPortletModeMD.setId(customPortletModeNav .getAttribute("id"));
         customPortletModeMD.setDescription(readLocalizedString(customPortletModeNav , Element.description));
         customPortletModeMD.setPortletMode(getContent(customPortletModeNav, Element.portlet_mode));
         if (customPortletModeNav .next(Element.portal_managed))
         {
            customPortletModeMD.setPortalManaged(customPortletModeNav .parseContent(ValueType.BOOLEAN));
         }
         md.addCustomPortletMode(customPortletModeMD);
      }

      //
      for (StaxNavigator<Element> customWindowStateNav : nav.fork(Element.custom_window_state))
      {
         CustomWindowStateMetaData customWindowStateMD = new CustomWindowStateMetaData();
         customWindowStateMD.setId(customWindowStateNav.getAttribute("id"));
         customWindowStateMD.setDescription(readLocalizedString(customWindowStateNav, Element.description));
         customWindowStateMD.setWindowState(getContent(customWindowStateNav, Element.window_state));
         md.addCustomWindowState(customWindowStateMD);
      }

      //
      for (StaxNavigator<Element> userAttributeNav : nav.fork(Element.user_attribute))
      {
         UserAttributeMetaData userAttributeMD = new UserAttributeMetaData();
         userAttributeMD.setId(userAttributeNav.getAttribute("id"));
         userAttributeMD.setDescription(readLocalizedString(userAttributeNav, Element.description));
         userAttributeMD.setName(getContent(userAttributeNav, Element.name));
         md.addUserAttribute(userAttributeMD);
      }

      //
      for (StaxNavigator<Element> securityConstraintNav  : nav.fork(Element.security_constraint))
      {
         SecurityConstraintMetaData securityConstraintMD = new SecurityConstraintMetaData();
         securityConstraintMD.setId(securityConstraintNav.getAttribute("id"));
         securityConstraintMD.setDisplayName(readLocalizedString(securityConstraintNav, Element.display_name));
         if (securityConstraintNav.next() != Element.portlet_collection)
         {
            throw new StaxNavException(nav.getLocation(), "Was expecting a portlet-collection element instead of " + securityConstraintNav.getName());
         }
         PortletCollectionMetaData portletCollectionMD = new PortletCollectionMetaData();
         while (securityConstraintNav.next(Element.portlet_name))
         {
            portletCollectionMD.addPortletname(securityConstraintNav.getContent());
         }
         securityConstraintMD.setPortletList(portletCollectionMD);
         if (securityConstraintNav.next() != Element.user_data_constraint)
         {
            throw new StaxNavException(nav.getLocation(), "Was expecting a security-constraint element instead of " + securityConstraintNav.getName());
         }
         UserDataConstraintMetaData userDataConstraintMD = new UserDataConstraintMetaData();
         userDataConstraintMD.setDescription(readLocalizedString(securityConstraintNav, Element.description));
         if (securityConstraintNav.next() != Element.transport_guarantee)
         {
            throw new StaxNavException(nav.getLocation(), "Was expecting a transport-guarantee element instead of " + securityConstraintNav.getName());
         }
         userDataConstraintMD.setTransportGuarantee(securityConstraintNav.parseContent(TRANSPORT_GUARANTEE));
         securityConstraintMD.setUserDataConstraint(userDataConstraintMD);
         md.addSecurityConstraint(securityConstraintMD);
      }

      //
      if (nav.find(Element.resource_bundle))
      {
         md.setResourceBundle(nav.getContent());
         nav.next();
      }

      //
      for (StaxNavigator<Element> filterNav : nav.fork(Element.filter))
      {
         if (version < 2)
         {
            throw new Exception("Cannot declare filter with " + PORTLET_1_0 + " descriptor");
         }
         FilterMetaData filterMD = new FilterMetaData();
         filterMD.setDescription(readLocalizedString(filterNav, Element.description));
         filterMD.setDisplayName(readLocalizedString(filterNav, Element.display_name));
         filterMD.setFilterName(getContent(filterNav, Element.filter_name));
         filterMD.setFilterClass(getContent(filterNav, Element.filter_class));
         while (filterNav.next(Element.lifecycle))
         {
            filterMD.addLifecycle(filterNav.parseContent(LIFE_CYCLE));
         }
         for (InitParamMetaData initParamMD : readInitParams(filterNav))
         {
            filterMD.addInitParam(initParamMD);
         }
         md.addFilter(filterMD);
      }

      //
      for (StaxNavigator<Element> filterMappingNav : nav.fork(Element.filter_mapping))
      {
         if (version < 2)
         {
            throw new Exception("Cannot declare filter mapping with " + PORTLET_1_0 + " descriptor");
         }
         FilterMappingMetaData filterMappingMD = new FilterMappingMetaData();
         filterMappingMD.setName(getContent(filterMappingNav, Element.filter_name));
         while (filterMappingNav.next(Element.portlet_name))
         {
            filterMappingMD.addPortletName(filterMappingNav.getContent());
         }
         md.addFilterMapping(filterMappingMD);
      }

      //
      if (nav.find(Element.default_namespace))
      {
         md.setDefaultNamespace(new URI(nav.getContent()));
         nav.next();
      }

      //
      for (StaxNavigator<Element> eventDefinitionNav : nav.fork(Element.event_definition))
      {
         EventDefinitionMetaData eventDefinitionMD = new EventDefinitionMetaData();
         eventDefinitionMD.setId(eventDefinitionNav.getAttribute("id"));
         eventDefinitionMD.setDescription(readLocalizedString(eventDefinitionNav, Element.description));
         switch (eventDefinitionNav.next(NAME_OR_QNAME))
         {
            case name:
               eventDefinitionMD.setName(eventDefinitionNav.getContent());
               break;
            case qname:
               eventDefinitionMD.setQname(readQName(eventDefinitionNav));
               break;
         }
         while (eventDefinitionNav.next(Element.alias))
         {
            QName name = readQName(eventDefinitionNav);
            eventDefinitionMD.addAlias(name);
         }
         if (eventDefinitionNav.next(Element.value_type))
         {
            eventDefinitionMD.setValueType(eventDefinitionNav.getContent());
         }
         md.addEventDefinition(eventDefinitionMD);
      }

      //
      for (StaxNavigator<Element> publicRenderParameterNav : nav.fork(Element.public_render_parameter))
      {
         PublicRenderParameterMetaData publicRenderParameterMD = new PublicRenderParameterMetaData();
         publicRenderParameterMD.setId(publicRenderParameterNav.getAttribute("id"));
         publicRenderParameterMD.setDescription(readLocalizedString(publicRenderParameterNav, Element.description));
         publicRenderParameterMD.setIdentifier(getContent(publicRenderParameterNav, Element.identifier));
         switch (publicRenderParameterNav.next(NAME_OR_QNAME))
         {
            case name:
               publicRenderParameterMD.setName(publicRenderParameterNav.getContent());
               break;
            case qname:
               publicRenderParameterMD.setQname(readQName(publicRenderParameterNav));
               break;
         }
         while (publicRenderParameterNav.next(Element.alias))
         {
            QName name = readQName(publicRenderParameterNav);
            publicRenderParameterMD.addAlias(name);
         }
         md.addPublicRenderParameter(publicRenderParameterMD);
      }

      //
      for (StaxNavigator<Element> listenerNav : nav.fork(Element.listener))
      {
         ListenerMetaData listenerMD = new ListenerMetaData();
         listenerMD.setId(listenerNav.getAttribute("id"));
         listenerMD.setDescription(readLocalizedString(listenerNav, Element.description));
         listenerMD.setDisplayName(readLocalizedString(listenerNav, Element.display_name));
         listenerMD.setListenerClass(getContent(listenerNav, Element.listener_class));
         md.addListener(listenerMD);
      }

      //
      for (StaxNavigator<Element> containerRuntimeNav : nav.fork(Element.container_runtime_option))
      {
         ContainerRuntimeMetaData containerRuntimeOptionMD = new ContainerRuntimeMetaData();
         containerRuntimeOptionMD.setName(getContent(containerRuntimeNav, Element.name));
         while (containerRuntimeNav.next(Element.value))
         {
            containerRuntimeOptionMD.addValue(containerRuntimeNav.getContent());
         }
         md.addContainerRuntime(containerRuntimeOptionMD);
      }

      //
      return md;
   }
}
