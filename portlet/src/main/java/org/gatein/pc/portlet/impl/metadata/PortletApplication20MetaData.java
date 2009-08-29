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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gatein.pc.portlet.impl.metadata.adapter.ContainerRuntimeAdapter;
import org.gatein.pc.portlet.impl.metadata.adapter.FilterAdapter;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.event.EventDefinitionMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMappingMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMetaData;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class PortletApplication20MetaData extends PortletApplication10MetaData
{
   /** The resource bundle (JSR 286) */
   private String resourceBundle;

   /** The default namespace URI (JSR 286) */
   private URI defaultNamespace;

   /** The filters (JSR 286) */
   private Map<String, FilterMetaData> filters;

   /** The filter mapping (JSR 286) */
   private List<FilterMappingMetaData> filterMapping;

   /** The events (JSR 286) */
   private List<EventDefinitionMetaData> events;

   /** The public render parameters */
   private List<PublicRenderParameterMetaData> publicRenderParameters;
   
   /** The url generation listener */
   private List<ListenerMetaData> listeners;

   /** The container runtime options */
   private Map<String, ContainerRuntimeMetaData> containerRuntimeOptions;

   @XmlElement(name = "resource-bundle", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public String getResourceBundle()
   {
      return resourceBundle;
   }

   public void setResourceBundle(String resourceBundle)
   {
      this.resourceBundle = resourceBundle;
   }

   @XmlElement(name = "default-namespace")
   public URI getDefaultNamespace()
   {
      return defaultNamespace;
   }

   public void setDefaultNamespace(URI defaultNamespace)
   {
      this.defaultNamespace = defaultNamespace;
   }

   @XmlElement(name = "filter", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   @XmlJavaTypeAdapter(FilterAdapter.class)
   public Map<String, FilterMetaData> getFilters()
   {
      return this.filters;
   }

   public void setFilters(Map<String, FilterMetaData> filters)
   {
      this.filters = filters;
   }

   public Collection<FilterMetaData> getFilterCollection()
   {
      return this.filters != null ? this.filters.values() : null;
   }

   public FilterMetaData getFilter(String filterName)
   {
      return this.filters.get(filterName);
   }
   
   public void addFilter(FilterMetaData filter)
   {
      if ( this.filters == null)
      {
         this.filters = new LinkedHashMap<String, FilterMetaData>();
      }
      this.filters.put(filter.getFilterName(), filter);
   }

   @XmlElement(name = "filter-mapping", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<FilterMappingMetaData> getFilterMapping()
   {
      return filterMapping;
   }

   public void setFilterMapping(List<FilterMappingMetaData> filterMapping)
   {
      this.filterMapping = filterMapping;
   }
   
   public void addFilterMapping(FilterMappingMetaData filterMapping)
   {
      if(this.filterMapping == null)
      {
         this.filterMapping = new ArrayList<FilterMappingMetaData>();
      }
      this.filterMapping.add(filterMapping);
   }
   
   @XmlElement(name = "event-definition", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<EventDefinitionMetaData> getEvents()
   {
      return events;
   }

   public void setEvents(List<EventDefinitionMetaData> events)
   {
      this.events = events;
   }
   
   public void addEventDefinition(EventDefinitionMetaData eventDefinition)
   {
      if(this.events == null)
      {
         this.events = new ArrayList<EventDefinitionMetaData>();
      }
      this.events.add(eventDefinition);
   }

   @XmlElement(name = "public-render-parameter", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<PublicRenderParameterMetaData> getPublicRenderParameters()
   {
      return publicRenderParameters;
   }

   public void setPublicRenderParameters(List<PublicRenderParameterMetaData> publicRenderParameters)
   {
      this.publicRenderParameters = publicRenderParameters;
   }
   
   public void addPublicRenderParameter(PublicRenderParameterMetaData renderParameter)
   {
      if( this.publicRenderParameters == null)
      {
         this.publicRenderParameters = new ArrayList<PublicRenderParameterMetaData>();
      }
      this.publicRenderParameters.add(renderParameter);
   }
   
   @XmlElement(name = "listener", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<ListenerMetaData> getListeners()
   {
      return listeners;
   }
   
   public void setListeners(List<ListenerMetaData> listeners)
   {
      this.listeners = listeners;
   }
   
   public void addListener(ListenerMetaData listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new ArrayList<ListenerMetaData>();
      }
      this.listeners.add(listener);
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

   public ContainerRuntimeMetaData getContainerRuntimeOption(String option)
   {
      return this.containerRuntimeOptions.get(option);
   }

   public Set<String> getContainerRuntimeOptionSet()
   {
      return this.containerRuntimeOptions != null ? this.containerRuntimeOptions.keySet() : null;
   }
   
   public void addContainerRuntime(ContainerRuntimeMetaData option)
   {
      if (this.containerRuntimeOptions == null)
      {
         this.containerRuntimeOptions = new HashMap<String, ContainerRuntimeMetaData>();
      }
      this.containerRuntimeOptions.put(option.getName(), option);
   }
}
