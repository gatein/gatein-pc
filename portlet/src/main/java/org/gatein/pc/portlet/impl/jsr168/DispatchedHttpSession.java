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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import javax.portlet.PortletSession;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class DispatchedHttpSession implements HttpSession
{

   /** . */
   private final DispatchedHttpServletRequest dispatchedRequest;

   /** . */
   private final PortletSession delegate;

   /** . */
   private final int scope;

   public DispatchedHttpSession(DispatchedHttpServletRequest dispatchedRequest, PortletSession delegate, int scope)
   {
      this.dispatchedRequest = dispatchedRequest;
      this.delegate = delegate;
      this.scope = scope;
   }

   public long getCreationTime()
   {
      return delegate.getCreationTime();
   }

   public String getId()
   {
      return delegate.getId();
   }

   public long getLastAccessedTime()
   {
      return delegate.getLastAccessedTime();
   }

   public ServletContext getServletContext()
   {
      return ((HttpServletRequest)dispatchedRequest.getRequest()).getSession().getServletContext();
   }

   public void setMaxInactiveInterval(int i)
   {
      delegate.setMaxInactiveInterval(i);
   }

   public int getMaxInactiveInterval()
   {
      return delegate.getMaxInactiveInterval();
   }

   public HttpSessionContext getSessionContext()
   {
      throw new UnsupportedOperationException();
   }

   public Object getAttribute(String s)
   {
      return delegate.getAttribute(s, scope);
   }

   public Object getValue(String s)
   {
      return getAttribute(s);
   }

   public Enumeration getAttributeNames()
   {
      return delegate.getAttributeNames(scope);
   }

   public String[] getValueNames()
   {
      ArrayList<String> names = Collections.list(delegate.getAttributeNames());

      //
      return names.toArray(new String[names.size()]);
   }

   public void setAttribute(String s, Object o)
   {
      delegate.setAttribute(s, o, scope);
   }

   public void putValue(String s, Object o)
   {
      setAttribute(s, o);
   }

   public void removeAttribute(String s)
   {
      delegate.removeAttribute(s, scope);
   }

   public void removeValue(String s)
   {
      removeAttribute(s);
   }

   public void invalidate()
   {
      delegate.invalidate();
   }

   public boolean isNew()
   {
      return delegate.isNew();
   }

   boolean isValid()
   {
      return true;
   }
}
