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
package org.gatein.pc.test.portlet.jsr168.tck.portletcontext;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.base.AbstractUniversalTestServlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertEquals;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_45})
public class AccessSameSetOfResourcesTestCase
{
   public AccessSameSetOfResourcesTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            AbstractUniversalTestPortlet aport = (AbstractUniversalTestPortlet)portlet;
            //we get resource and pass it to servlet
            URL url = aport.getPortletContext().getResource("/simple_resource.txt");
            assertNotNull(url);
            aport.getPortletContext().setAttribute("resource", url);

            //and we construct resource content and pass to servlet
            InputStream is = aport.getPortletContext().getResourceAsStream("/simple_resource.txt");
            assertNotNull(is);
            byte[] byteArray = new byte[is.available()];
            is.read(byteArray);
            aport.getPortletContext().setAttribute("content", byteArray);

            //then we dispatch to servlet
            PortletRequestDispatcher dispatcher = aport.getPortletContext().getNamedDispatcher("UniversalServletA");
            assertNotNull(dispatcher);

            dispatcher.include(request, response);
            Object o = (Boolean)UTP1.local.get();
            //assert out assertion was correct in Servlet
            assertEquals(Boolean.TRUE, o);


            return new EndTestResponse();
         }
      });

      seq.bindAction(0, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            AbstractUniversalTestServlet serv = ((AbstractUniversalTestServlet)servlet);
            URL portletUrl = (URL)serv.getServletContext().getAttribute("resource");
            URL url = serv.getServletContext().getResource("/simple_resource.txt");

            byte[] portletByteArray = (byte[])serv.getServletContext().getAttribute("content");
            InputStream is = serv.getServletContext().getResourceAsStream("/simple_resource.txt");
            byte[] byteArray = new byte[is.available()];
            is.read(byteArray);

            if (portletUrl.equals(url) &&
               Arrays.equals(portletByteArray, byteArray))
            {
               UTP1.local.set(Boolean.TRUE);
            }
            else
            {
               UTP1.local.set(Boolean.FALSE);
            }
            return null;
         }

         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            return null;
         }
      });
   }
}
