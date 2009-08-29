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
package org.gatein.pc.test.portlet.jsr168.tck.portletsession;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertNull;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertTrue;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_110, Assertion.JSR168_111, Assertion.JSR168_112, Assertion.JSR168_117, Assertion.JSR168_118, Assertion.JSR168_119})
public class SessionAttributesTestCase
{
   public SessionAttributesTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            request.getPortletSession(true).setAttribute("key1", "k1value1", PortletSession.APPLICATION_SCOPE);
            request.getPortletSession().setAttribute("key2", "k2value1", PortletSession.PORTLET_SCOPE);
            PortletURL url = response.createActionURL();
            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context)
         {
            assertEquals("k1value1", request.getPortletSession().getAttribute("key1", PortletSession.APPLICATION_SCOPE));
            assertEquals("k2value1", request.getPortletSession().getAttribute("key2"));
         }
      });

      seq.bindAction(1, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //assert that we have access to APLICATION_SCOPE and not to PORTLET_SCOPE attributes
            assertEquals("k1value1", request.getPortletSession().getAttribute("key1", PortletSession.APPLICATION_SCOPE));
            assertNull(request.getPortletSession().getAttribute("key2"));
            return null;
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //in this request count we do an assert in SessionAttributesHelperPortlet
            //so just repaint to let it finish
            PortletURL url = response.createRenderURL();
            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //invoke servlet
            String path = request.getContextPath();
            return new InvokeGetResponse(path + "/universalServletA");
         }
      });

      seq.bindAction(3, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            //assert that we have access to APLICATION_SCOPE and not to PORTLET_SCOPE attributes
            assertEquals("k1value1", request.getSession().getAttribute("key1"));
            assertNull(request.getSession().getAttribute("key2"));

            Enumeration names = request.getSession().getAttributeNames();

            //first we must get our PORTLET_SCOPE name from APPLICATION_SCOPE names
            //it should look like 'javax.portlet.p.<ID>?key2'
            String name = null;
            while (names.hasMoreElements())
            {
               String s = (String)names.nextElement();
               if (s.endsWith("key2"))
               {
                  name = s;
                  break;
               }
            }
            //assert there was one
            assertNotNull(name);
            //and we access correct content using it
            assertEquals("k2value1", request.getSession().getAttribute(name));
            System.out.println("value: " + name);

            //then we must assert it is namespaced correctly
            assertTrue(Pattern.matches("javax.portlet.p.[^\\?]*\\?key2", name));
            return new EndTestResponse();
         }
      });
   }
}
