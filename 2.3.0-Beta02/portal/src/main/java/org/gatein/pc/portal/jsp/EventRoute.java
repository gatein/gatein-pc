/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portal.jsp;

import javax.xml.namespace.QName;
import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;

/**
 * @author <a href="mailto:julien@jboss-portal.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class EventRoute
{

   /** . */
   private final EventRoute parent;

   /** . */
   private final QName name;

   /** . */
   private final Serializable payload;

   /** . */
   private final String source;

   /** . */
   private final String destination;

   /** . */
   final LinkedList<EventRoute> children;

   /** . */
   EventAcknowledgement acknowledgement;

   public EventRoute(
      EventRoute parent,
      QName name,
      Serializable payload,
      String source,
      String destination)
   {
      this.parent = parent;
      this.name = name;
      this.payload = payload;
      this.source = source;
      this.destination = destination;
      this.children = new LinkedList<EventRoute>();
   }

   public String getSource()
   {
      return source;
   }

   public String getDestination()
   {
      return destination;
   }

   public EventRoute getParent()
   {
      return parent;
   }

   public List<EventRoute> getChildren()
   {
      return children;
   }

   public QName getName()
   {
      return name;
   }

   public Serializable getPayload()
   {
      return payload;
   }

   public EventAcknowledgement getAcknowledgement()
   {
      return acknowledgement;
   }
}
