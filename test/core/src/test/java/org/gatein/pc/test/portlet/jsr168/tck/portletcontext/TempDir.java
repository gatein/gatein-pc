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
import org.gatein.pc.test.unit.web.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.web.AbstractUniversalTestServlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.unit.web.UTP1;
import org.gatein.pc.test.unit.web.UTS1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import static org.gatein.pc.test.unit.Assert.assertNotNull;
import static org.gatein.pc.test.unit.Assert.assertEquals;

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
import java.io.File;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_46})
public class TempDir
{
   public TempDir(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            AbstractUniversalTestPortlet aport = (AbstractUniversalTestPortlet)portlet;
            File tempDir = (File)aport.getPortletContext().getAttribute("javax.servlet.context.tempdir");
            assertNotNull(tempDir);
            aport.getPortletContext().setAttribute("tempDirPath", tempDir.getAbsolutePath());
            PortletRequestDispatcher dispatcher = aport.getPortletContext().getNamedDispatcher("UniversalServletA");
            assertNotNull(dispatcher);


            dispatcher.include(request, response);
            Object o = (Boolean)UTP1.local.get();
            //assert that paths were compared successfully in Servlet
            assertEquals(Boolean.TRUE, o);


            return new EndTestResponse();
         }
      });

      seq.bindAction(0, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         public Response execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            AbstractUniversalTestServlet serv = ((AbstractUniversalTestServlet)servlet);
            String path = (String)serv.getServletContext().getAttribute("tempDirPath");
            File tempDir = (File)serv.getServletContext().getAttribute("javax.servlet.context.tempdir");
            if (path != null && tempDir != null)
            {
               UTP1.local.set(path.equals(tempDir.getAbsolutePath()) ? Boolean.TRUE : Boolean.FALSE);
            }
            return null;
         }

         protected Response run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            return null;
         }
      });
   }
}
