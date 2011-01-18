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
package org.gatein.pc.test.portlet.jsr286.ext.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertFalse;
import static org.jboss.unit.api.Assert.assertNull;
import static org.jboss.unit.api.Assert.assertTrue;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.Enumeration;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_DISPATCHER_8
   })
public class RequestHeaderAccessTestCase
{
   public RequestHeaderAccessTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            // Test the value is not there yet
            Set propertyNames = Tools.toSet(request.getPropertyNames());
            assertFalse(propertyNames.contains("myheader"));
            assertNull(request.getProperty("myheader"));
            assertFalse(request.getProperties("myheader").hasMoreElements());

            // Invoke render with header
            InvokeGetResponse render = new InvokeGetResponse(response.createRenderURL().toString());
            render.addHeader("myheader").addElement("render-value");
            return render;
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/universalServletA");
            response.setContentType("text/html");
            dispatcher.include(request, response);
            return null;
         }
      });

      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context)
         {
            // Test the header is here
            Set headerNames = Tools.toSet(request.getHeaderNames());
            assertTrue(headerNames.contains("myheader"));
            assertEquals("render-value", request.getHeader("myheader"));
            Enumeration values = request.getHeaders("myheader");
            assertTrue(values.hasMoreElements());
            assertEquals("render-value", values.nextElement());
            assertFalse(values.hasMoreElements());
            return new EndTestResponse();
         }
      });
   }
}