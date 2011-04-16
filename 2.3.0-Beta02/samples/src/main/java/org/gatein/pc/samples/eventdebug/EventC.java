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
package org.gatein.pc.samples.eventdebug;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import java.io.Serializable;

/**
 * @author <a href="mailto:whales@redhat.com">Wesley Hales</a>
 * @version $Revision: 630 $
 */
@XmlRootElement
public class EventC implements Serializable
{
   private String id;

   public static final QName QNAME = new QName("urn:jboss:portal:samples:eventc", "EventC");

   public EventC(String id)
   {
      this.id = id;
   }

   public String getId()
   {
      return id;
   }
}
