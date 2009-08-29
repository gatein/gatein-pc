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
package org.gatein.pc.portlet.impl.metadata.filter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.adapter.LocalizedStringAdapter;
import org.gatein.pc.portlet.impl.metadata.common.DescribableMetaData;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */

@XmlType(name = "filterType", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS,
      propOrder = {"description", "displayName", "filterName", "filterClass", "lifecycle", "initParams"})
public class FilterMetaData extends DescribableMetaData
{
   /** The filter name */
   private String filterName;

   /** The filter class */
   private String filterClass;

   /** The filter lifecycle */
   private List<LifeCyclePhase> lifecycle;

   /** The filter display name */
   private LocalizedString displayName;

   /** The filter init parameters */
   private List<InitParamMetaData> initParams;

   @XmlElement(name = "filter-name", required = true)
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   public String getFilterName()
   {
      return filterName;
   }

   public void setFilterName(String filterName)
   {
      this.filterName = filterName;
   }

   @XmlElement(name = "filter-class", required = true)
   @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
   public String getFilterClass()
   {
      return filterClass;
   }

   public void setFilterClass(String filterClass)
   {
      this.filterClass = filterClass;
   }

   @XmlElement(name = "lifecycle", required = true)
   public List<LifeCyclePhase> getLifecycle()
   {
      return lifecycle;
   }

   public void setLifecycle(List<LifeCyclePhase> lifecycle)
   {
      this.lifecycle = lifecycle;
   }
   
   public void addLifecycle(LifeCyclePhase lifecycle)
   {
      if( this.lifecycle == null)
      {
         this.lifecycle = new ArrayList<LifeCyclePhase>();
      }
      this.lifecycle.add(lifecycle);
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
      if(this.initParams == null)
      {
         this.initParams = new ArrayList<InitParamMetaData>();
      }
      this.initParams.add(initParam);
   }

}