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
package org.gatein.pc.test.portlet.jsr168.tck.portletrequests;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertNull;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_51, Assertion.JSR168_54})
public class ParametersDuringRenderNotTargetedTestCase
{

   public ParametersDuringRenderNotTargetedTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return null;
         }
      });

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //just repost so other portlet can create url in first render
            PortletURL url = response.createRenderURL();

            //we are setting params to ourselves to test if they will last while request to another porltet
            url.setParameter("portlet1key1", "p1k1value1");
            url.setParameter("portlet1key2", new String[]{"p1k2value1", "p1k2value2", "p1k2value3"});
            return new InvokeGetResponse(url.toString());
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return null;
         }
      });
      seq.bindAction(1, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //invoke render url from different portlet to be able to pass some params to it
            PortletURL portletURL = response.createRenderURL();
            portletURL.setParameter("portlet2key1", "p2k1value1");
            portletURL.setParameter("portlet2key2", new String[]{"p2k2value1", "p2k2value2", "p2k2value3"});
            return new InvokeGetResponse(portletURL.toString());
         }
      });

      seq.bindAction(2, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals("p2k1value1", request.getParameter("portlet2key1"));
            assertEquals(new String[]{"p2k2value1", "p2k2value2", "p2k2value3"}, request.getParameterValues("portlet2key2"));

            //
            return null;
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //we test if we have params set during requestCount==0, becouse our portlet
            //wasn't targeted in current render request

            //assert that we didn't received params targeted to other portlet
            assertNull(request.getParameter("portlet2key1"));
            assertNull(request.getParameter("portlet2key2"));

            assertEquals("p1k1value1", request.getParameter("portlet1key1"));
            assertEquals(new String[]{"p1k2value1", "p1k2value2", "p1k2value3"}, request.getParameterValues("portlet1key2"));

            //and just repost so other portlet render invokation can be finished
            PortletURL url = response.createRenderURL();
            return new InvokeGetResponse(url.toString());

         }
      });

      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //second portlet asserted if it received correct parameters during render request
            //we simply end test
            return new EndTestResponse();
         }
      });
   }
}
