/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.gatein.pc.portlet.aspects;

import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.portlet.PortletInvokerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * This is a port of http://svn.exoplatform.org/projects/portlet-container/branches/2.1.x/component/plugins/pc/src/main/java/org/exoplatform/services/portletcontainer/plugins/pc/aop/PortletSessionIdentityScopingCommand.java
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class SessionInvalidatorInterceptor extends PortletInvokerInterceptor
{

   private static final String IDENTITY_TOKEN = "javax.portlet.identity.token";

   private final static Logger log = LoggerFactory.getLogger(SessionInvalidatorInterceptor.class);

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      //req = RequestContext.<PortalRequestContext>getCurrentInstance().getRequest();
      HttpServletRequest req = invocation.getDispatchedRequest();

      check(req);
      try
      {
         return super.invoke(invocation);
      }
      finally
      {
         update(req);
      }
   }

   public void update(HttpServletRequest request)
   {
      String portalIdentity = request.getRemoteUser();
      boolean trace = log.isTraceEnabled();
      String contextPath = request.getContextPath();
      HttpSession session = request.getSession(false);
      if (session != null)
      {
         String id = session.getId();
         String sessionIdentity = (String) session.getAttribute(IDENTITY_TOKEN);
         if (portalIdentity != null)
         {
            if (!portalIdentity.equals(sessionIdentity))
            {
               if (trace)
               {
                  log.trace("Updating portlet session " + id + " (" + contextPath + ") from " + sessionIdentity + " to " + portalIdentity);
               }

               //
               session.setAttribute(IDENTITY_TOKEN, portalIdentity);
            }
         }
         else
         {
            if (sessionIdentity != null)
            {
               if (trace)
               {
                  log.trace("Updating portlet session " + id + " (" + contextPath + ") by removing the " + sessionIdentity + " value");
               }

               //
               session.removeAttribute(IDENTITY_TOKEN);
            }
         }
      }
   }

   public void check(HttpServletRequest request)
   {
      boolean trace = log.isTraceEnabled();
      String portalIdentity = request.getRemoteUser();
      String contextPath = request.getContextPath();
      HttpSession session = request.getSession(false);
      if (session != null)
      {
         String id = session.getId();
         String sessionIdentity = (String) session.getAttribute(IDENTITY_TOKEN);

         //
         if (portalIdentity == null)
         {
            if (sessionIdentity != null)
            {
               // It means that user is anonymous and the portlet session is still associated to a previous identity
               if (trace)
               {
                  log.trace("Detected user logout for session " + id + " (" + contextPath + ")");
               }

               purge(session);
            }
         }
         else
         {
            if (sessionIdentity != null && !sessionIdentity.equals(portalIdentity))
            {
               // It means that we don't have the same identity in portal and portlet session
               if (trace)
               {
                  log.trace("Detected different user for session " + id + " (" + contextPath + ")");
               }

               purge(session);
            }
         }
      }
   }

   @SuppressWarnings("unchecked")
   private void purge(HttpSession session)
   {
      for (String name : (Iterable<String>) Collections.list(session.getAttributeNames()))
      {
         session.removeAttribute(name);
      }
   }
}
