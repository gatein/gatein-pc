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
package org.gatein.pc.portlet.impl.info;

import org.apache.log4j.Logger;
import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.common.i18n.LocaleFormat;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.common.i18n.ResourceBundleManager;
import org.gatein.common.reflect.NoSuchClassException;
import org.gatein.common.util.ConversionException;
import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.pc.portlet.impl.metadata.CustomPortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.CustomWindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.ListenerMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.PublicRenderParameterMetaData;
import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionMetaData;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionReferenceMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMappingMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletInfoMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletPreferenceMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletPreferencesMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SecurityRoleRefMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportedLocaleMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportsMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.WindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.security.SecurityConstraintMetaData;
import org.gatein.pc.api.info.MetaInfo;
import org.gatein.pc.api.info.RuntimeOptionInfo;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerInfoBuilder
{

   /** . */
   private static final String[] KEYS = {MetaInfo.TITLE, MetaInfo.SHORT_TITLE, MetaInfo.KEYWORDS};

   /** . */
   private static final String JAVAX_PORTLET = "javax.portlet.";

   /** . */
   private static final String[] BUNDLE_KEYS = {JAVAX_PORTLET + MetaInfo.TITLE, JAVAX_PORTLET + MetaInfo.SHORT_TITLE, JAVAX_PORTLET + MetaInfo.KEYWORDS};

   /** . */
   private static final List<ListenerMetaData> EMPTY_LISTENER_LIST = Collections.emptyList();

   /** . */
   private static final List<PublicRenderParameterMetaData> EMPTY_PUBLIC_RENDER_PARAMETER_LIST = Collections.emptyList();

   /** . */
   private static final List<EventDefinitionMetaData> EMPTY_EVENT_DEFINITION_LIST = Collections.emptyList();

   /** . */
   private static final List<SecurityRoleRefMetaData> EMPPTY_SECURITY_ROLE_REF_LIST = Collections.emptyList();

   /** . */
   private static final List<SecurityConstraintMetaData> EMPTY_SECURITY_CONSTRAINT_LIST = Collections.emptyList();

   /** . */
   private static final List<SupportedLocaleMetaData> EMPTY_SUPPORTED_LOCALE_LIST = Collections.emptyList();

   /** . */
   private static final List<PortletModeMetaData> EMPTY_PORTLET_MODE_LIST = Collections.emptyList();

   /** . */
   private static final List<WindowStateMetaData> EMPTY_WINDOW_STATE_LIST = Collections.emptyList();

   /** . */
   private static final Map<String, PortletPreferenceMetaData> EMPTY_PORTLET_PREFERENCE_MAP = Collections.emptyMap();

   /** . */
   private static final PortletPreferencesMetaData EMPTY_PORTLET_PREFERENCES = new PortletPreferencesMetaData();

   /** . */
   private static final List<InitParamMetaData> EMPTY_INIT_PARAM_LIST = Collections.emptyList();

   /** . */
   private static final List<String> EMPTY_STRING_LIST = Collections.emptyList();

   /** . */
   private static final List<EventDefinitionReferenceMetaData> EMPTY_EVENT_DEFINITION_REFERENCE_LIST = Collections.emptyList();

   /** . */
   private static final List<FilterMappingMetaData> EMPTY_FILTER_MAPPING_LIST = Collections.emptyList();

   /** . */
   private static final Map<String, FilterMetaData> EMPTY_FILTER_MAP = Collections.emptyMap();

   /** . */
   private static final Map<String, ContainerRuntimeMetaData> EMPTY_CONTAINER_RUNTIME_OPTION_MAP = Collections.emptyMap();

   /** . */
   private static final Map<String, CustomPortletModeMetaData> EMPTY_CUSTOM_PORTLET_MODES = Collections.emptyMap();

   /** . */
   private static final Map<String, CustomWindowStateMetaData> EMPTY_CUSTOM_WINDOW_STATES = Collections.emptyMap();

   /** . */
   private final PortletApplication10MetaData portletApplicationMD;

   /** . */
   private final ContainerInfoBuilderContext context;

   /** . */
   private final Map<QName, ContainerEventInfo> events;

   /** . */
   private final Map<String, ContainerParameterInfo> publicParameters;

   /** . */
   private ContainerPortletApplicationInfo portletApplication;

   /** . */
   private final String portletApplicationId;

   /** . */
   private final Map<String, ContainerPortletInfo> portlets;

   /** . */
   private final Map<String, RuntimeOptionInfo> applicationOptions;

   /** . */
   private final Map<Mode, ContainerModeInfo> customModes;

   /** . */
   private final Map<org.gatein.pc.api.WindowState, ContainerWindowStateInfo> customWindowStates;

   /** . */
   private final Logger log = Logger.getLogger(ContainerInfoBuilder.class);

   public ContainerInfoBuilder(
      String portletApplicationId,
      PortletApplication10MetaData portletApplicationMD,
      ContainerInfoBuilderContext context)
   {
      this.portletApplicationId = portletApplicationId;
      this.portletApplicationMD = portletApplicationMD;
      this.context = context;
      this.events = new LinkedHashMap<QName, ContainerEventInfo>();
      this.publicParameters = new LinkedHashMap<String, ContainerParameterInfo>();
      this.portlets = new LinkedHashMap<String, ContainerPortletInfo>();
      this.applicationOptions = new HashMap<String, RuntimeOptionInfo>();
      this.customModes = new HashMap<org.gatein.pc.api.Mode, ContainerModeInfo>();
      this.customWindowStates = new HashMap<org.gatein.pc.api.WindowState, ContainerWindowStateInfo>();
   }

   public ContainerPortletApplicationInfo getApplication()
   {
      return portletApplication;
   }

   public Collection<ContainerPortletInfo> getPortlets()
   {
      return portlets.values();
   }

   private void fixMetaData(PortletApplication10MetaData portletApplicationMD)
   {
      if (portletApplicationMD instanceof PortletApplication20MetaData)
      {
         PortletApplication20MetaData tmp = (PortletApplication20MetaData)portletApplicationMD;
         if (tmp.getEvents() == null)
         {
            tmp.setEvents(EMPTY_EVENT_DEFINITION_LIST);
         }
         if (tmp.getPublicRenderParameters() == null)
         {
            tmp.setPublicRenderParameters(EMPTY_PUBLIC_RENDER_PARAMETER_LIST);
         }
         if (tmp.getDefaultNamespace() == null)
         {
            try
            {
               tmp.setDefaultNamespace(new URI(XMLConstants.NULL_NS_URI));
            }
            catch (URISyntaxException e)
            {
               throw new Error(e);
            }
         }
         if (tmp.getListeners() == null)
         {
            tmp.setListeners(EMPTY_LISTENER_LIST);
         }
         if (tmp.getFilters() == null)
         {
            tmp.setFilters(EMPTY_FILTER_MAP);
         }
         for (FilterMetaData filterMD : tmp.getFilters().values())
         {
            if (filterMD.getInitParams() == null)
            {
               filterMD.setInitParams(EMPTY_INIT_PARAM_LIST);
            }
         }
         if (tmp.getFilterMapping() == null)
         {
            tmp.setFilterMapping(EMPTY_FILTER_MAPPING_LIST);
         }
         if (tmp.getContainerRuntimeOptions() == null)
         {
            tmp.setContainerRuntimeOptions(EMPTY_CONTAINER_RUNTIME_OPTION_MAP);
         }
      }

      //
      if (portletApplicationMD.getCustomPortletModes() == null)
      {
         portletApplicationMD.setCustomPortletModes(EMPTY_CUSTOM_PORTLET_MODES);
      }
      if (portletApplicationMD.getCustomWindowStates() == null)
      {
         portletApplicationMD.setCustomWindowStates(EMPTY_CUSTOM_WINDOW_STATES);
      }
   }

   private void fixMetaData(PortletMetaData portletMD)
   {
      if (portletMD.getSecurityRoleRef() == null)
      {
         portletMD.setSecurityRoleRef(EMPPTY_SECURITY_ROLE_REF_LIST);
      }
      if (portletMD.getPortletApplication().getSecurityConstraints() == null)
      {
         portletMD.getPortletApplication().setSecurityConstraints(EMPTY_SECURITY_CONSTRAINT_LIST);
      }
      if (portletMD.getSupportedLocale() == null)
      {
         portletMD.setSupportedLocale(EMPTY_SUPPORTED_LOCALE_LIST);
      }
      for (SupportsMetaData supportsMD : portletMD.getSupports())
      {
         if (supportsMD.getPortletModes() == null)
         {
            supportsMD.setPortletModes(EMPTY_PORTLET_MODE_LIST);
         }
         if (supportsMD.getWindowStates() == null)
         {
            supportsMD.setWindowStates(EMPTY_WINDOW_STATE_LIST);
         }
      }
      if (portletMD.getPortletPreferences() == null)
      {
         portletMD.setPortletPreferences(EMPTY_PORTLET_PREFERENCES);
      }
      if (portletMD.getPortletPreferences().getPortletPreferences() == null)
      {
         portletMD.getPortletPreferences().setPortletPreferences(EMPTY_PORTLET_PREFERENCE_MAP);
      }
      if (portletMD.getInitParams() == null)
      {
         portletMD.setInitParams(EMPTY_INIT_PARAM_LIST);
      }
      if (portletMD.getSupportedPublicRenderParameters() == null)
      {
         portletMD.setSupportedPublicRenderParameters(EMPTY_STRING_LIST);
      }
      for (PortletPreferenceMetaData portletPreferenceMD : portletMD.getPortletPreferences().getPortletPreferences().values())
      {
         if (portletPreferenceMD.getValue() == null)
         {
            portletPreferenceMD.setValue(EMPTY_STRING_LIST);
         }
      }

      //
      if (portletApplicationMD instanceof PortletApplication20MetaData)
      {
         if (portletMD.getSupportedProcessingEvent() == null)
         {
            portletMD.setSupportedProcessingEvent(EMPTY_EVENT_DEFINITION_REFERENCE_LIST);
         }
         if (portletMD.getSupportedPublishingEvent() == null)
         {
            portletMD.setSupportedPublishingEvent(EMPTY_EVENT_DEFINITION_REFERENCE_LIST);
         }
         if (portletMD.getContainerRuntimeOptions() == null)
         {
            portletMD.setContainerRuntimeOptions(EMPTY_CONTAINER_RUNTIME_OPTION_MAP);
         }
      }
   }

   private QName getName(QName name, String localPart)
   {
      if (name == null)
      {
         String namespaceURI = ((PortletApplication20MetaData)portletApplicationMD).getDefaultNamespace().toString();
         return new QName(namespaceURI, localPart);
      }
      else
      {
         return name;
      }
   }

   private List<Locale> getSupportedLocales(PortletMetaData portletMD)
   {
      List<Locale> locales = new ArrayList<Locale>();
      for (SupportedLocaleMetaData supportedLocaleMD : portletMD.getSupportedLocale())
      {
         Locale locale;
         try
         {
            locale = LocaleFormat.DEFAULT.getLocale(supportedLocaleMD.getLocale());
            locales.add(locale);
         }
         catch (ConversionException e)
         {
            // FIXME -- basic for now
            log.error("Could not convert supported locale (" + supportedLocaleMD.getLocale() + ") for portlet: " + portletMD.getPortletName(), e);
         }

      }
      return locales;
   }

   public void build()
   {
      portletApplication = build(portletApplicationMD);

      //
      for (PortletMetaData portletMD : portletApplicationMD.getPortletCollection())
      {
         ContainerPortletInfo portlet = build(portletMD);
         portlets.put(portlet.getName(), portlet);
      }
   }

   private ContainerPortletApplicationInfo build(PortletApplication10MetaData portletApplicationMD)
   {
      fixMetaData(portletApplicationMD);

      //
      String defaultNamespace = XMLConstants.NULL_NS_URI;
      List<ContainerListenerInfo> listeners = Collections.emptyList();
      Map<String, ContainerFilterInfo> allFilters = new HashMap<String, ContainerFilterInfo>();
      if (this.portletApplicationMD instanceof PortletApplication20MetaData)
      {
         PortletApplication20MetaData tmp = (PortletApplication20MetaData)this.portletApplicationMD;

         //
         defaultNamespace = tmp.getDefaultNamespace() != null ? tmp.getDefaultNamespace().toString() : null;

         //
         for (EventDefinitionMetaData eventDefinitionMD : tmp.getEvents())
         {
            try
            {
               ContainerEventInfo event = build(eventDefinitionMD);
               events.put(event.getName(), event);
            }
            catch (NoSuchClassException e)
            {
               log.error("Cannot load event class " + e.getClassName(), e);
            }
         }

         //
         for (PublicRenderParameterMetaData parameterMD : tmp.getPublicRenderParameters())
         {
            ContainerParameterInfo parameter = build(parameterMD);

            //
            publicParameters.put(parameter.getId(), parameter);
         }

         //
         Map<String, RuntimeOptionInfo> applicationOptions = build(tmp.getContainerRuntimeOptions().values());
         if (applicationOptions != null)
         {
            this.applicationOptions.putAll(applicationOptions);
         }

         //
         listeners = new ArrayList<ContainerListenerInfo>(tmp.getListeners().size());
         for (ListenerMetaData listenerMD : tmp.getListeners())
         {
            ContainerListenerInfo listener = build(listenerMD);
            listeners.add(listener);
         }
         listeners = Collections.unmodifiableList(listeners);

         //
         for (FilterMetaData filterMD : tmp.getFilterCollection())
         {
            ContainerFilterInfo filter = build(filterMD);
            allFilters.put(filter.getName(), filter);
         }
      }

      //
      Map<String, UserAttributeMetaData> userAttributesMD = portletApplicationMD.getUserAttributes();
      Set<String> supportedUserAttributes;
      if (userAttributesMD != null)
      {
         supportedUserAttributes = Collections.unmodifiableSet(userAttributesMD.keySet());
      }
      else
      {
         supportedUserAttributes = Collections.emptySet();
      }
      ContainerUserInfo user = new ContainerUserInfo(supportedUserAttributes);

      // Build custom mode infos for reuse in portlet info
      for (CustomPortletModeMetaData customPortletModeMD : portletApplicationMD.getCustomPortletModes().values())
      {
         org.gatein.pc.api.Mode mode = org.gatein.pc.api.Mode.create(customPortletModeMD.getPortletMode());

         //
         LocalizedString description = customPortletModeMD.getDescription();

         //
         ContainerModeInfo modeInfo;
         if (customPortletModeMD.isPortalManaged())
         {
            if (description != null)
            {
               modeInfo = new ContainerModeInfo(mode, description);
            }
            else
            {
               modeInfo = new ContainerModeInfo(mode);
            }
         }
         else
         {
            ResourceBundleManager bundleMgr = context.getBundleManager();

            //
            LocalizedString displayName = bundleMgr.getLocalizedValue(
               "javax.portlet.app.custom-portlet-mode." + mode + ".decoration-name",
               "" + mode);

            //
            if (description != null)
            {
               modeInfo = new ContainerPortletManagedModeInfo(mode, description, displayName);
            }
            else
            {
               modeInfo = new ContainerPortletManagedModeInfo(mode, displayName);
            }
         }

         //
         customModes.put(mode, modeInfo);
      }

      // Build custom window state infos for reuse in portlet info
      for (CustomWindowStateMetaData customPortletModeMD : portletApplicationMD.getCustomWindowStates().values())
      {
         org.gatein.pc.api.WindowState windowState = WindowState.create(customPortletModeMD.getWindowState());

         //
         LocalizedString description = customPortletModeMD.getDescription();

         //
         ContainerWindowStateInfo windowStateInfo;
         if (description != null)
         {
            windowStateInfo = new ContainerWindowStateInfo(windowState, description);
         }
         else
         {
            windowStateInfo = new ContainerWindowStateInfo(windowState);
         }

         //
         customWindowStates.put(windowState, windowStateInfo);
      }

      //
      return new ContainerPortletApplicationInfo(
         portletApplicationId,
         defaultNamespace,
         allFilters.values(),
         listeners,
         user);
   }

   private ContainerListenerInfo build(ListenerMetaData listenerMD)
   {
      return new ContainerListenerInfo(
         listenerMD.getListenerClass(),
         listenerMD.getDisplayName(),
         listenerMD.getDescription());
   }

   private ContainerFilterInfo build(FilterMetaData filterMD)
   {
      Map<String, String> initParameters = new HashMap<String, String>();
      for (InitParamMetaData initParamMD : filterMD.getInitParams())
      {
         initParameters.put(initParamMD.getName(), initParamMD.getValue());
      }

      //
      return new ContainerFilterInfo(
         filterMD.getFilterName(),
         filterMD.getFilterClass(),
         Collections.unmodifiableSet(new HashSet<LifeCyclePhase>(filterMD.getLifecycle())),
         filterMD.getDisplayName(),
         filterMD.getDescription(),
         Collections.unmodifiableMap(initParameters)
      );
   }

   private ContainerParameterInfo build(PublicRenderParameterMetaData parameterMD)
   {
      QName name = getName(parameterMD.getQname(), parameterMD.getName());
      ResourceBundleManager bundleMgr = context.getBundleManager();

      LocalizedString description = bundleMgr.getLocalizedValue(
         "javax.portlet.app.public-render-parameter." + name + ".description",
         getDefaultStringFor(parameterMD.getDescription(), "Description of public render parameter " + name));

      //
      return new ContainerParameterInfo(
         parameterMD.getIdentifier(),
         name,
         parameterMD.getAlias(),
         description
      );
   }

   private String getDefaultStringFor(LocalizedString desc, String defaultValue)
   {
      return desc != null ? desc.getDefaultString() : defaultValue;
   }

   private ContainerEventInfo build(EventDefinitionMetaData eventDefinitionMD) throws NoSuchClassException
   {
      QName name = getName(eventDefinitionMD.getQname(), eventDefinitionMD.getName());
      ResourceBundleManager bundleMgr = context.getBundleManager();

      //
      String valueType = eventDefinitionMD.getValueType();
      ContainerTypeInfo type = null;
      if (valueType != null)
      {
         Class clazz = context.getClass(valueType);
         type = new ContainerTypeInfo(clazz);
      }

      //
      LocalizedString displayName = bundleMgr.getLocalizedValue(
         "javax.portlet.app.event-definition." + name + ".display-name",
         "Event " + name);
      LocalizedString description = bundleMgr.getLocalizedValue(
         "javax.portlet.app.event-definition." + name + ".description",
         getDefaultStringFor(eventDefinitionMD.getDescription(), "Description of event " + name));

      //
      return new ContainerEventInfo(name, type, displayName, description);
   }

   private ContainerPortletInfo build(PortletMetaData portletMD)
   {
      fixMetaData(portletMD);

      //
      ContainerCapabilitiesInfo containerCapabilities = buildContainerCapabilities(portletMD);
      ContainerPreferencesInfo containerPreferences = buildContainerPreferences(portletMD);
      ContainerMetaInfo containerMeta = buildContainerMeta(portletMD);
      ContainerSecurityInfo containerSecurity = buildContainerSecurityInfo(portletMD);
      ContainerCacheInfo containerCache = buildContainerCache(portletMD);

      //
      Map<String, String> initParameters = Collections.emptyMap();
      for (InitParamMetaData initParamMD : portletMD.getInitParams())
      {
         if (initParameters.isEmpty())
         {
            initParameters = new HashMap<String, String>();
         }
         initParameters.put(initParamMD.getName(), initParamMD.getValue());
      }
      if (initParameters.size() > 0)
      {
         initParameters = Collections.unmodifiableMap(initParameters);
      }

      //
      ContainerPortletInfo containerPortletInfo;
      if (portletApplicationMD instanceof PortletApplication20MetaData)
      {
         ContainerEventingInfo containerEvents = buildContainerEventsInfo(portletMD);
         ContainerNavigationInfo containerNavigation = buildContainerNavigationInfo(portletMD);

         //
         List<String> filters = Collections.emptyList();
         for (FilterMappingMetaData filterMappingMD : ((PortletApplication20MetaData)portletApplicationMD).getFilterMapping())
         {
            for (String portletName : filterMappingMD.getPortletNames())
            {
               boolean matches;
               if (portletName.length() == 0)
               {
                  // Do smth
                  matches = false;
               }
               else if (portletName.endsWith("*"))
               {
                  String prefix = portletName.substring(0, portletName.length() - 1);
                  matches = portletMD.getPortletName().startsWith(prefix);
               }
               else
               {
                  matches = portletMD.getPortletName().equals(portletName);
               }

               // Add the filter
               if (matches)
               {
                  if (filters.isEmpty())
                  {
                     filters = new ArrayList<String>();
                  }
                  filters.add(filterMappingMD.getName());
               }
            }
         }
         if (filters.size() > 0)
         {
            filters = Collections.unmodifiableList(filters);
         }

         //
         Map<String, RuntimeOptionInfo> options = build(portletMD.getContainerRuntimeOptions().values());
         if (options != null)
         {
            for (Map.Entry<String, RuntimeOptionInfo> entry : applicationOptions.entrySet())
            {
               if (!options.containsKey(entry.getKey()))
               {
                  options.put(entry.getKey(), entry.getValue());
               }
            }
            options = Collections.unmodifiableMap(options);
         }
         else
         {
            options = applicationOptions;
         }

         //
         containerPortletInfo = new ContainerPortletInfo(
            containerCapabilities,
            containerPreferences,
            containerMeta,
            containerSecurity,
            containerCache,
            containerEvents,
            containerNavigation,
            filters,
            portletMD.getPortletName(),
            context.getApplicationName(),
            portletMD.getPortletClass(),
            initParameters,
            null,
            context.getBundleManager(portletMD),
            options
         );
      }
      else
      {
         containerPortletInfo = new ContainerPortletInfo(
            containerCapabilities,
            containerPreferences,
            containerMeta,
            containerSecurity,
            containerCache,
            portletMD.getPortletName(),
            portletMD.getPortletClass(),
            context.getApplicationName(),
            Collections.unmodifiableMap(initParameters),
            context.getBundleManager(portletMD)
         );
      }

      //
      return containerPortletInfo;
   }

   private Map<String, RuntimeOptionInfo> build(Collection<ContainerRuntimeMetaData> optionsMD)
   {
      Map<String, RuntimeOptionInfo> options = null;
      for (ContainerRuntimeMetaData containerRuntimeMD : optionsMD)
      {
         RuntimeOptionInfo optionInfo = build(containerRuntimeMD);
         if (options == null)
         {
            options = new HashMap<String, RuntimeOptionInfo>();
         }
         options.put(optionInfo.getName(), optionInfo);
      }
      return options;
   }

   private ContainerRuntimeOptionInfo build(ContainerRuntimeMetaData containerRuntimeMD)
   {
      return new ContainerRuntimeOptionInfo(containerRuntimeMD.getName(), Collections.unmodifiableList(containerRuntimeMD.getValues()));
   }

   private ContainerNavigationInfo buildContainerNavigationInfo(PortletMetaData portletMD)
   {
      ContainerNavigationInfo navigation = new ContainerNavigationInfo();

      //
      for (String parameterId : portletMD.getSupportedPublicRenderParameters())
      {
         ContainerParameterInfo parameter = publicParameters.get(parameterId);

         //
         if (parameter == null)
         {
            // Do something
         }
         else
         {
            navigation.addPublicParameter(parameter);
         }
      }

      //
      return navigation;
   }

   private ContainerEventingInfo buildContainerEventsInfo(PortletMetaData portletMD)
   {
      ContainerEventingInfo portletEvents = new ContainerEventingInfo();

      //
      for (EventDefinitionReferenceMetaData eventDefinitionReferenceMD : portletMD.getSupportedProcessingEvent())
      {
         QName name = getName(eventDefinitionReferenceMD.getQname(), eventDefinitionReferenceMD.getName());
         ContainerEventInfo event = events.get(name);

         //
         if (event != null)
         {
            portletEvents.addConsumedEvent(event);
         }
         else
         {
            log.error("Portlet " + portletMD.getPortletName() + " references the event " + name + " that is not " +
               "declared at the application level");
         }
      }

      //
      for (EventDefinitionReferenceMetaData eventDefinitionReferenceMD : portletMD.getSupportedPublishingEvent())
      {
         QName name = getName(eventDefinitionReferenceMD.getQname(), eventDefinitionReferenceMD.getName());
         ContainerEventInfo event = events.get(name);

         //
         if (event != null)
         {
            portletEvents.addProducedEvent(event);
         }
         else
         {
            log.error("Portlet " + portletMD.getPortletName() + " references the event " + name + " that is not " +
               "declared at the application level");
         }
      }

      //
      return portletEvents;
   }

   private ContainerSecurityInfo buildContainerSecurityInfo(PortletMetaData portletMD)
   {
      ContainerSecurityInfo containerSecurity = new ContainerSecurityInfo();

      // Security role ref
      for (SecurityRoleRefMetaData securityRoleRefMD : portletMD.getSecurityRoleRef())
      {
         containerSecurity.addRoleRef(securityRoleRefMD.getRoleName(), securityRoleRefMD.getRoleLink());
      }

      //
      for (SecurityConstraintMetaData securityConstraintMD : portletMD.getPortletApplication().getSecurityConstraints())
      {
         if (securityConstraintMD.getPortletList().getPortletNames().contains(portletMD.getPortletName()))
         {
            containerSecurity.addTransportGuarantee(securityConstraintMD.getUserDataConstraint().getTransportQuarantee());
         }
      }

      //
      return containerSecurity;
   }

   private ContainerCacheInfo buildContainerCache(PortletMetaData portletMD)
   {
      int expirationCache = portletMD.getExpirationCache();

      //
      if (expirationCache < 0 && expirationCache != -1)
      {
         // log.warn("Seen bad caching expiration value " + expirationTimeSecs + " disable caching instead");
         expirationCache = 0;
      }

      //
      return new ContainerCacheInfo(expirationCache);
   }

   private ContainerCapabilitiesInfo buildContainerCapabilities(PortletMetaData portletMD)
   {
      ContainerCapabilitiesInfo capabilities = new ContainerCapabilitiesInfo();

      //
      for (SupportedLocaleMetaData supportedLocaleMD : portletMD.getSupportedLocale())
      {
         try
         {
            capabilities.addLocale(LocaleFormat.DEFAULT.getLocale(supportedLocaleMD.getLocale()));
         }
         catch (ConversionException e)
         {
            // FIXME -- basic for now
            log.error("Could not convert supported locale (" + supportedLocaleMD.getLocale() + ") for portlet: " + portletMD.getPortletName(), e);
         }
      }

      //
      for (SupportsMetaData supportsMD : portletMD.getSupports())
      {
         // Get the mime type
         String mimeType = supportsMD.getMimeType().toLowerCase();

         // Add the content type to the view mode
         // because each content type must handle this view
         capabilities.add(mimeType, Mode.VIEW);

         // Then process each mode
         for (PortletModeMetaData modeMD : supportsMD.getPortletModes())
         {
            ContainerModeInfo mode = customModes.get(modeMD.getPortletMode());

            //
            if (mode != null)
            {
               capabilities.add(mimeType, mode);
            }
            else
            {
               capabilities.add(mimeType, modeMD.getPortletMode());
            }
         }

         // then process window states
         for (WindowStateMetaData windowStateMD : supportsMD.getWindowStates())
         {
            ContainerWindowStateInfo windowStateInfo = customWindowStates.get(windowStateMD.getWindowState());

            //
            if (windowStateInfo != null)
            {
               capabilities.add(mimeType, windowStateInfo);
            }
            else
            {
               capabilities.add(mimeType, windowStateMD.getWindowState());
            }
         }

         // Override those as also now they must be supported
         capabilities.add(mimeType, org.gatein.pc.api.WindowState.NORMAL);
         capabilities.add(mimeType, org.gatein.pc.api.WindowState.MINIMIZED);
         capabilities.add(mimeType, org.gatein.pc.api.WindowState.MAXIMIZED);
      }

      //
      return capabilities;
   }

   private ContainerMetaInfo buildContainerMeta(PortletMetaData portletMD)
   {
      ContainerMetaInfo containerMeta = new ContainerMetaInfo();

      //
      ResourceBundleManager bundleMgr = context.getBundleManager(portletMD);

      // Capture inline values
      PortletInfoMetaData portletInfoMD = portletMD.getPortletInfo();
      String[] inlines = null;
      if (portletInfoMD != null)
      {
         // JBoss XB would give null for an empty title, but we know that
         // there must be a title according to the schema
         String title = portletInfoMD.getTitle();
         if (title == null)
         {
            title = "";
         }

         //
         inlines = new String[]{
            title,
            portletInfoMD.getShortTitle(),
            portletInfoMD.getKeywords()
         };
      }

      // Construct info from resource bundle manager
      for (int i = 0; i < KEYS.length; i++)
      {
         String key = KEYS[i];

         //
         Map<Locale, String> tmp = new HashMap<Locale, String>();

         //
         List<Locale> locales = getSupportedLocales(portletMD);

         // Add english locale
         locales.add(Locale.ENGLISH);

         // Feed with the known locales (perhaps should try more locales)
         for (Locale locale : locales)
         {
            ResourceBundle bundle = bundleMgr.getResourceBundle(locale);

            //
            if (bundle != null)
            {
               try
               {
                  String value = bundle.getString(BUNDLE_KEYS[i]);
                  tmp.put(locale, value);
               }
               catch (MissingResourceException ignore)
               {
               }
            }
         }

         // Add the inline value if it is present
         if (inlines != null && inlines[i] != null)
         {
            tmp.put(Locale.ENGLISH, inlines[i]);
         }

         //
         LocalizedString ls = new LocalizedString(tmp, Locale.ENGLISH);
         containerMeta.addMetaValue(key, ls);
      }

      // Add stuff coming from deployment descriptor
      containerMeta.addMetaValue(MetaInfo.DESCRIPTION, portletMD.getDescription());
      containerMeta.addMetaValue(MetaInfo.DISPLAY_NAME, portletMD.getDisplayName());

      //
      return containerMeta;
   }

   private ContainerPreferencesInfo buildContainerPreferences(PortletMetaData portletMD)
   {
      PortletPreferencesMetaData preferencesMD = portletMD.getPortletPreferences();

      //
      ContainerPreferencesInfo containerPreferences = null;
      if (preferencesMD != null)
      {
         //
         containerPreferences = new ContainerPreferencesInfo(preferencesMD.getPreferenceValidator());

         //
         ResourceBundleManager bundleMgr = context.getBundleManager(portletMD);

         //
         for (PortletPreferenceMetaData portletPreferenceMD : preferencesMD.getPortletPreferences().values())
         {
            List<String> value = portletPreferenceMD.getValue();
            LocalizedString displayName = bundleMgr.getLocalizedValue("javax.portlet.preference.name." + portletPreferenceMD.getName(), portletPreferenceMD.getName());
            LocalizedString description = bundleMgr.getLocalizedValue("javax.portlet.preference.description." + portletPreferenceMD.getName(), portletPreferenceMD.getName());
            containerPreferences.addContainerPreference(portletPreferenceMD.getName(), value, portletPreferenceMD.isReadOnly(), displayName, description);
         }
      }

      //
      return containerPreferences;
   }
}
