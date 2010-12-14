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
package org.gatein.pc.test.portlet.jsr286.ext.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.PortletException;
import javax.portlet.RenderResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.PortletSession;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public abstract class AbstractDispatchedSessionTestCase
{
   public AbstractDispatchedSessionTestCase(
      PortletTestCase seq,
      JoinPoint portletJoinPoint,
      final int sessionScope)
   {
      seq.bindAction(0, portletJoinPoint, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
            assertNotNull(dispatcher);
            dispatcher.include(request, response);

            //
            PortletSession session = request.getPortletSession(false);
            assertNotNull(session);
            assertTrue(session.isNew());
            assertEquals("foo_dispatched_value", session.getAttribute("foo", sessionScope));

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(0, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            HttpSession session = request.getSession(false);
            assertNull(session);

            //
            session = request.getSession();
            assertNotNull(session);
            assertTrue(session.isNew());
            session.setAttribute("foo", "foo_dispatched_value");
            assertEquals("foo_dispatched_value", session.getAttribute("foo"));

            //
            return null;
         }
      });
      seq.bindAction(1, portletJoinPoint, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletSession session = request.getPortletSession(false);
            assertNotNull(session);
            assertFalse(session.isNew());
            assertEquals("foo_dispatched_value", session.getAttribute("foo", sessionScope));

            //
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
            assertNotNull(dispatcher);
            dispatcher.include(request, response);

            //
            try
            {
               session.isNew();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            return new EndTestResponse();
         }
      });
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            HttpSession session = request.getSession(false);
            assertNotNull(session);
            assertFalse(session.isNew());
            assertEquals("foo_dispatched_value", session.getAttribute("foo"));

            //
            session.invalidate();

            try
            {
               session.isNew();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            return null;
         }
      });
   }
}
