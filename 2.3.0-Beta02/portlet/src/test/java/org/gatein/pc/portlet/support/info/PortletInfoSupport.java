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
package org.gatein.pc.portlet.support.info;

import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.RuntimeOptionInfo;

import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class PortletInfoSupport implements PortletInfo
{

   private String name;
   private String applicationName;
   private PreferencesInfoSupport preferencesSupport = new PreferencesInfoSupport();
   private SecurityInfoSupport securitySupport = new SecurityInfoSupport();
   private CacheInfoSupport cacheSupport = new CacheInfoSupport();
   private MetaInfoSupport metaSupport = new MetaInfoSupport();
   private EventingInfoSupport eventsSupport = new EventingInfoSupport();
   private NavigationInfoSupport navigationSupport = new NavigationInfoSupport();

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getApplicationName()
   {
      return applicationName;
   }

   public void setApplicationName(String applicationName)
   {
      this.applicationName = applicationName;
   }

   public CapabilitiesInfo getCapabilities()
   {
      throw new UnsupportedOperationException("Implement me");
   }

   public PreferencesInfoSupport getPreferences()
   {
      return preferencesSupport;
   }

   public MetaInfoSupport getMeta()
   {
      return metaSupport;
   }

   public SecurityInfoSupport getSecurity()
   {
      return securitySupport;
   }

   public CacheInfoSupport getCache()
   {
      return cacheSupport;
   }

   public EventingInfoSupport getEventing()
   {
      return eventsSupport;
   }

   public NavigationInfoSupport getNavigation()
   {
      return navigationSupport;
   }

   public <T> T getAttachment(Class<T> type) throws IllegalArgumentException
   {
      if (type == null)
      {
         throw new IllegalArgumentException();
      }
      return null;
   }

   public Map<String, RuntimeOptionInfo> getRuntimeOptionsInfo()
   {
      throw new UnsupportedOperationException("Implement me");
   }
}
