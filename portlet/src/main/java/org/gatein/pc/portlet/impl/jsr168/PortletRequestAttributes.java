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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.pc.portlet.impl.info.ContainerUserInfo;
import org.gatein.pc.api.spi.UserContext;
import org.gatein.pc.api.spi.SecurityContext;
import org.gatein.pc.portlet.container.PortletContainer;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;

/**
 * Hold the portlet request attributes and holds only the modified state. That is the reason motivating the
 * presence of an <code>HttpServletRequest</code> on the methods returning data reading the state. The object
 * can be used either during the render request where the dispatched request will be used. It can also be used
 * during a request dispatch made from the portlet to a servlet, in that situation the request provided will
 * be the one valid during the dispatching operation.
 *
 * The other motivation of this class is to hold the state of the attributes that have been modified by the
 * request to the portlet container, so it will not write in the portal request attributes.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletRequestAttributes
{

   /** Constant object to mark that a request attribute is removed. */
   protected static final Object REMOVED_ATTRIBUTE = new Object();

   /** . */
   private final SecurityContext securityContext;

   /** . */
   private final PortletContainer container;

   /** . */
   private final UserContext userContext;

   /** . */
   private final HttpServletRequestWrapper wreq;

   /** The lazy request attributes map added or removed during the request of the portlet. */
   private Map<String, Object> attributes;

   public PortletRequestAttributes(
      SecurityContext securityContext,
      PortletContainer container,
      UserContext userContext,
      HttpServletRequestWrapper wreq)
   {
      if (securityContext == null)
      {
         throw new IllegalArgumentException("No null portlet request allowed");
      }
      if (container == null)
      {
         throw new IllegalArgumentException("No null container allowed");
      }
      if (userContext == null)
      {
         throw new IllegalArgumentException("No null user context allowed");
      }
      if (wreq == null)
      {
         throw new IllegalArgumentException("No null wrapped request allowed");
      }

      //
      this.securityContext = securityContext;
      this.container = container;
      this.userContext = userContext;
      this.wreq = wreq;
   }

   public Object getAttribute(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name must not be null");
      }
      if (PortletRequest.USER_INFO.equals(name))
      {
         if (!securityContext.isAuthenticated())
         {
            return null;
         }

         //
         Map<String, String> infos = userContext.getInformations();

         //
         if (infos != null)
         {
            // Get portlet application metadata
            PortletApplicationImpl portletApp = (PortletApplicationImpl)container.getPortletApplication();
            ContainerUserInfo userInfo = portletApp.getInfo().getUser();

            // Clone the map
            infos = new HashMap<String, String>(infos);

            // Keep only what is of interest with respect to what the portlet app defines
            infos.keySet().retainAll(userInfo.getSupportedUserAttributes());

            // Make it immutable
            infos = Collections.unmodifiableMap(infos);
         }
         else
         {
            infos = Collections.emptyMap();
         }

         //
         return infos;
      }
      else
      {
         Object value = null;
         if (attributes != null)
         {
            value = attributes.get(name);
         }
         if (value == null && wreq != null)
         {
            value = wreq.getAttribute(name);
         }
         else if (value == REMOVED_ATTRIBUTE)
         {
            value = null;
         }
         return value;
      }
   }

   public Iterator<String> getAttributeNames()
   {
      // Copy the attribute names to avoid ConcurrentModificationException
      // one test in the TCK getPortalObjectContext the Enumeration then dispatch the call to a
      // servlet where it use the Enumeration and it throws a CME if we don't copy
      Set<String> names = new HashSet<String>();

      //
      for (Enumeration e = wreq.getAttributeNames();e.hasMoreElements();)
      {
         // Fixme : when migrated to servlet 2.5
         names.add((String)e.nextElement());
      }

      //
      if (attributes != null)
      {
         for (Map.Entry<String, Object> entry : attributes.entrySet())
         {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (value == REMOVED_ATTRIBUTE)
            {
               names.remove(name);
            }
            else
            {
               names.add(name);
            }
         }
      }

      //
      if (securityContext.isAuthenticated())
      {
         names.add(PortletRequest.USER_INFO);
      }

      //
      return names.iterator();
   }

   public void setAttribute(String name, Object value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name must not be null");
      }
      if (!PortletRequest.USER_INFO.equals(name))
      {
         if (value == null)
         {
            value = REMOVED_ATTRIBUTE;
         }
         if (attributes == null)
         {
            attributes = new HashMap<String, Object>();
         }
         attributes.put(name, value);
      }
   }

   public void removeAttribute(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name must not be null");
      }
      setAttribute(name, null);
   }

   public void setAttributeMap(Map<String, Object> map)
   {
      for (Map.Entry<String, Object> attribute : map.entrySet())
      {
         setAttribute(attribute.getKey(), attribute.getValue());
      }
   }

   public Map<String, Object> getAttributeMap()
   {
      Map<String, Object> map = Collections.emptyMap();

      //
      if (attributes != null)
      {
         for (Map.Entry<String, Object> attribute : attributes.entrySet())
         {
            if (attribute.getValue() != REMOVED_ATTRIBUTE)
            {
               if (map.size() == 0)
               {
                  map = new HashMap<String, Object>(attributes.size());
               }
               map.put(attribute.getKey(), attribute.getValue());
            }
         }
      }

      //
      return map;
   }
}
