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

import org.gatein.pc.api.info.TypeInfo;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.AnnotatedElement;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerTypeInfo implements TypeInfo
{

   /** . */
   private final Class type;

   /** . */
   private final XmlRootElement annotation;

   public ContainerTypeInfo(Class type)
   {
      this.type = type;
      this.annotation = ((AnnotatedElement)type).getAnnotation(XmlRootElement.class);
   }

   public Class getType()
   {
      return type;
   }

   public String getName()
   {
      return type.getClass().getName();
   }

   public XmlRootElement getXMLBinding()
   {
      return annotation;
   }
}
