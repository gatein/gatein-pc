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
package org.gatein.pc.test.portlet.session;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.jboss.portal.test.framework.server.NodeId;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertNull;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase
public class TestReplicateMutableValueTestCase
{
   public TestReplicateMutableValueTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, NodeId.PORTS_01, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException
         {
            assertNull(request.getPortletSession(false));
            PortletSession session = request.getPortletSession();
            assertNotNull(session);
            MutableValue value = new MutableValue("abcdef");
            session.setAttribute("mutable", value);
            value.setString("fedcba");
            PortletURL portletURL = response.createRenderURL();
            String url = context.rewriteURLForNode(portletURL.toString(), NodeId.PORTS_02);
            return new InvokeGetResponse(url);
         }
      });
      seq.bindAction(1, NodeId.PORTS_02, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletSession session = request.getPortletSession();
            assertNotNull(session);
            assertEquals(new MutableValue("fedcba"), session.getAttribute("mutable"));
            return new EndTestResponse();
         }
      });
   }
}
