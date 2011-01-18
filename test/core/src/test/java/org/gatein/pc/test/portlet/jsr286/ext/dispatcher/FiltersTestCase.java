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
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import java.io.IOException;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_DISPATCHER_4
   })
public class FiltersTestCase
{
   public FiltersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getRequestDispatcher("/noop");
            assertNotNull(prd);
            ServletFilter.ids.clear();
            prd.include(request, response);
            assertEquals(Tools.toSet("INCLUDE_URL_PATTERN_FILTER", "INCLUDE_NAMED_FILTER"), ServletFilter.ids);

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getNamedDispatcher("NoopServlet");
            assertNotNull(prd);
            ServletFilter.ids.clear();
            prd.include(request, response);
            assertEquals(Collections.singleton("INCLUDE_NAMED_FILTER"), ServletFilter.ids);

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getRequestDispatcher("/noop");
            assertNotNull(prd);
            ServletFilter.ids.clear();
            prd.forward(request, response);
            assertEquals(Tools.toSet("INCLUDE_URL_PATTERN_FILTER", "INCLUDE_NAMED_FILTER"), ServletFilter.ids);

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            PortletRequestDispatcher prd = request.getPortletSession().getPortletContext().getNamedDispatcher("NoopServlet");
            assertNotNull(prd);
            ServletFilter.ids.clear();
            prd.forward(request, response);
            assertEquals(Collections.singleton("INCLUDE_NAMED_FILTER"), ServletFilter.ids);

            //
            return new EndTestResponse();
         }
      });
   }
}