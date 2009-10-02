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
package org.gatein.pc.test.portlet.jsr168.ext.session;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The goal is to test that cross context session attributes are set in container and are accessible from the direct
 * servlet.
 * <p/>
 * 1/ portlet put key=value in the http session 2/ portlet ask the client to perform get on /servlet 3/ servlet check
 * that key=value
 * 
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_SESSION_1
   })
public class CrossContextSessionAttributeTestCase
{
   public CrossContextSessionAttributeTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletSession session = request.getPortletSession();
            session.setAttribute("key", "value", PortletSession.APPLICATION_SCOPE);
            String path = request.getContextPath();
            InvokeGetResponse igr = new InvokeGetResponse(path + "/universalServletA");
            return igr;
         }
      });

      /**
       * This servlet is normally accessed after that the CrossContextSessionAttributePortlet asked
       * the client to do it. This portlet should have put in the session an attribute that this servlet
       * will assert the presence of.
       */
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            HttpSession session = request.getSession();
            assertEquals("value", session.getAttribute("key"));
            return new EndTestResponse();
         }
      });
   }
}
