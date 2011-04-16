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

import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.common.i18n.LocalizedString;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerFilterInfo
{

   /** . */
   private final String name;

   /** . */
   private final String className;

   /** . */
   private final Set<LifeCyclePhase> phases;

   /** . */
   private final LocalizedString displayName;

   /** . */
   private final LocalizedString description;

   /** . */
   private final Map<String, String> parameters;

   public ContainerFilterInfo(
      String name,
      String className,
      Set<LifeCyclePhase> phases,
      LocalizedString displayName,
      LocalizedString description,
      Map<String, String> parameters)
   {
      this.name = name;
      this.className = className;
      this.phases = phases;
      this.displayName = displayName;
      this.description = description;
      this.parameters = parameters;
   }

   public String getName()
   {
      return name;
   }

   public String getClassName()
   {
      return className;
   }

   public Set<LifeCyclePhase> getPhases()
   {
      return phases;
   }

   public LocalizedString getDisplayName()
   {
      return displayName;
   }

   public LocalizedString getDescription()
   {
      return description;
   }

   public Map<String, String> getParameters()
   {
      return parameters;
   }
}
