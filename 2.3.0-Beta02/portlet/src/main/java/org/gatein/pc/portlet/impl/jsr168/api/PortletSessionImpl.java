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
package org.gatein.pc.portlet.impl.jsr168.api;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6697 $
 */
public class PortletSessionImpl implements PortletSession
{

   /** . */
   private HttpSession session;

   /** . */
   private String prefix;

   /** . */
   private PortletContext context;

   public PortletSessionImpl(HttpSession session, String id, PortletContext context)
   {
      this.session = session;
      this.prefix = "javax.portlet.p." + id + "?";
      this.context = context;
   }

   public Object getAttribute(String s)
   {
      return getAttribute(s, PORTLET_SCOPE);
   }

   public Object getAttribute(String s, int i)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("Name must not be null");
      }
      if (i == PORTLET_SCOPE)
      {
         s = prefix + s;
      }
      return session.getAttribute(s);
   }

   public Enumeration<String> getAttributeNames()
   {
      return getAttributeNames(PORTLET_SCOPE);
   }

   public Enumeration<String> getAttributeNames(int scope)
   {
      if (scope == APPLICATION_SCOPE)
      {
         return (Enumeration<String>)session.getAttributeNames();
      }
      else
      {
         return new Enumeration<String>()
         {
            private Enumeration e;
            private String next;

            {
               e = session.getAttributeNames();
               next = null;
               next();
            }

            public boolean hasMoreElements()
            {
               return next != null;
            }

            public String nextElement()
            {
               String result = next;
               next = null;
               next();
               return result;
            }

            private void next()
            {
               while (e.hasMoreElements())
               {
                  String attribute = (String)e.nextElement();
                  if (attribute.startsWith(prefix))
                  {
                     next = attribute.substring(prefix.length());
                     break;
                  }
               }
            }
         };
      }
   }

   public long getCreationTime()
   {
      return session.getCreationTime();
   }

   public String getId()
   {
      return session.getId();
   }

   public long getLastAccessedTime()
   {
      return session.getLastAccessedTime();
   }

   public int getMaxInactiveInterval()
   {
      return session.getMaxInactiveInterval();
   }

   public void invalidate()
   {
      // Invalidate the underlying HTTP session
      session.invalidate();
   }

   public boolean isNew()
   {
      return session.isNew();
   }

   public void removeAttribute(String s)
   {
      removeAttribute(s, PORTLET_SCOPE);
   }

   public void removeAttribute(String s, int i)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("Name must not be null");
      }
      if (i == PORTLET_SCOPE)
      {
         s = prefix + s;
      }
      session.removeAttribute(s);
   }

   public void setAttribute(String s, Object o)
   {
      setAttribute(s, o, PORTLET_SCOPE);
   }

   public void setAttribute(String s, Object o, int i)
   {
      if (s == null)
      {
         throw new IllegalArgumentException("Name must not be null");
      }
      if (i == PORTLET_SCOPE)
      {
         s = prefix + s;
      }
      session.setAttribute(s, o);
   }

   public void setMaxInactiveInterval(int i)
   {
      session.setMaxInactiveInterval(i);
   }

   public PortletContext getPortletContext()
   {
      return context;
   }

   public Map<String, Object> getAttributeMap()
   {
      return getAttributeMap(PortletSession.PORTLET_SCOPE);
   }

   public Map<String, Object> getAttributeMap(int i)
   {
      Enumeration attributes = getAttributeNames(i);
      Map<String, Object> attrs = new HashMap<String, Object>();
      while (attributes.hasMoreElements())
      {
         String name = (String)attributes.nextElement();
         attrs.put(name, getAttribute(name));
      }
      return Collections.unmodifiableMap(attrs);
   }

   /**
    * Detect validity of the session based on the underlying session.
    *
    * @return true if the session is valid
    */
   boolean isValid()
   {
      try
      {
         session.isNew();
         return true;
      }
      catch (IllegalStateException e)
      {
         return false;
      }
   }
}
