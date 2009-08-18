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
package org.gatein.pc.api.info;

import java.util.Map;

/**
 * Runtime meta data for a portlet.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 * @since 2.4
 */
public interface PortletInfo
{

   /**
    * Returns the portlet name.
    *
    * @return the portlet name
    */
   String getName();

   /**
    * Returns the portlet application name.
    *
    * @return the portlet application name
    */
   String getApplicationName();

   /**
    * Retrieves the capabilities of supported by the described portlet.
    *
    * @return the capabilities of the portlet
    */
   CapabilitiesInfo getCapabilities();

   /**
    * Retrieves information about the preferences supported by the described portlet, it must return null if the portlet
    * cannot be personalized.
    *
    * @return the preferences meta data of the portlet
    */
   PreferencesInfo getPreferences();

   /**
    * Retrieves the portlet description (title, keywords, etc).
    *
    * @return the portlet description
    */
   MetaInfo getMeta();

   /**
    * Retrieves the security information for the described portlet.
    *
    * @return the security information
    */
   SecurityInfo getSecurity();

   /**
    * Retrieves caching-related information for the described portlet.
    *
    * @return the caching related information
    */
   CacheInfo getCache();

   /**
    * Return the eventing information.
    *
    * @return the eventing information
    */
   EventingInfo getEventing();

   /**
    * Returns the navigation information.
    *
    * @return the navigation information
    */
   NavigationInfo getNavigation();

   /**
    * Returns a generic attachment on the portlet info.
    *
    * @param type the parameter type
    * @return the attachment or null
    * @throws IllegalArgumentException if the parameter type is null
    */
   <T> T getAttachment(Class<T> type) throws IllegalArgumentException;

   /**
    * Retrieves the runtime container options set by the associated portlet, if any.
    *
    * @return the runtime container options set by the associated portlet, if any
    */
   Map<String, RuntimeOptionInfo> getRuntimeOptionsInfo();
}
