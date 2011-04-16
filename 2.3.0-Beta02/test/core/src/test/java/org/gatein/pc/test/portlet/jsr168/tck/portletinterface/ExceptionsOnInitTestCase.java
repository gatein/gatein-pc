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
package org.gatein.pc.test.portlet.jsr168.tck.portletinterface;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.jsr168.tck.portletinterface.spec.PortletExceptionDuringInitPortlet;
import org.gatein.pc.test.portlet.jsr168.tck.portletinterface.spec.RuntimeExceptionDuringInitPortlet;
import org.gatein.pc.test.portlet.jsr168.tck.portletinterface.spec.UnavailableExceptionDuringInitPortlet;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_5, Assertion.JSR168_6, Assertion.JSR168_8})
public class ExceptionsOnInitTestCase
{
   public ExceptionsOnInitTestCase(PortletTestCase seq)
   {
      //PortletExceptionDuringInitPortlet
      seq.bindAction(0, PortletExceptionDuringInitPortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletExceptionDuringInitPortlet.rendered = true;
            return null;
         }
      });

      //RuntimeExceptionDuringInitPortlet
      seq.bindAction(0, RuntimeExceptionDuringInitPortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            RuntimeExceptionDuringInitPortlet.rendered = true;
            return null;
         }
      });

      //UnavailableExceptionDuringInitPortlet
      seq.bindAction(0, UnavailableExceptionDuringInitPortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            UnavailableExceptionDuringInitPortlet.rendered = true;
            return null;
         }
      });

      //ControllerPortlet
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //portlets shouldn't render itself
            assertEquals(false, PortletExceptionDuringInitPortlet.rendered);
            assertEquals(false, UnavailableExceptionDuringInitPortlet.rendered);
            assertEquals(false, RuntimeExceptionDuringInitPortlet.rendered);

            //and shouldn't be destroyed as Exceptions on init() were throwed
            assertEquals(false, PortletExceptionDuringInitPortlet.destroyed);
            assertEquals(false, UnavailableExceptionDuringInitPortlet.destroyed);
            assertEquals(false, RuntimeExceptionDuringInitPortlet.destroyed);
            return new EndTestResponse();
         }
      });
   }
}
