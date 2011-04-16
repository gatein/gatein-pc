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

import org.gatein.pc.api.info.EventInfo;
import org.gatein.pc.api.info.TypeInfo;
import org.gatein.common.i18n.LocalizedString;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventInfoSupport implements EventInfo
{

   /** . */
   private QName name;

   /** . */
   private TypeInfoSupport type;

   /** . */
   private LocalizedString displayName;

   /** . */
   private LocalizedString description;

   /** . */
   private List<QName> aliases;

   public EventInfoSupport(QName name)
   {
      this.name = name;
      this.type = null;
      this.aliases = new ArrayList<QName>();
      this.displayName = new LocalizedString("Event " + name);
      this.description = new LocalizedString("Description of event " + name);
   }

   public EventInfoSupport(QName name, TypeInfoSupport type)
   {
      this.name = name;
      this.type = type;
      this.aliases = new ArrayList<QName>();
      this.displayName = new LocalizedString("Event " + name);
      this.description = new LocalizedString("Description of event " + name);
   }

   public EventInfoSupport(QName name, String type)
   {
      this.name = name;
      this.type = new TypeInfoSupport(type);
      this.aliases = new ArrayList<QName>();
   }

   public QName getName()
   {
      return name;
   }

   public TypeInfo getType()
   {
      return type;
   }

   public LocalizedString getDisplayName()
   {
      return displayName;
   }

   public LocalizedString getDescription()
   {
      return description;
   }

   public Collection<QName> getAliases()
   {
      return aliases;
   }

   public void setDisplayName(LocalizedString displayName)
   {
      this.displayName = displayName;
   }

   public void setDescription(LocalizedString description)
   {
      this.description = description;
   }

   public void addAlias(QName alias)
   {
      aliases.add(alias);
   }
}
