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
package org.gatein.pc.test.portlet.jsr168.tck.portletmode;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.jsr168.tck.portletmode.spec.NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertNull;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletModeException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase(Assertion.JSR168_38)
public class NotInvokeModeDeclaredForNotSupportedMarkupTestCase
{
   public NotInvokeModeDeclaredForNotSupportedMarkupTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletModeException
         {
            NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet.url = response.createRenderURL();
            NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet.url.setPortletMode(PortletMode.EDIT);
            return null;
         }
      });

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //just repost so other portlet can create url in first render
            PortletURL url = response.createRenderURL();
            return new InvokeGetResponse(url.toString());

         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //invoke render url from different portlet to be able to call specific mode for it
            //it has Mode set to EDIT
            return new InvokeGetResponse(NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet.url.toString());
         }
      });

      seq.bindAction(2, NotInvokeModeDeclaredForNotSupportedMarkupHelperPortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletModeException
         {
            if (request.getPortletMode().equals(PortletMode.EDIT))
            {
               //mark that we were invoked
               UTP1.holder = Boolean.TRUE;
            }
            return null;
         }
      });

      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            //check that other portlet wasn't invoked
            assertNull(UTP1.holder);
            return new EndTestResponse();
         }
      });
   }
}
