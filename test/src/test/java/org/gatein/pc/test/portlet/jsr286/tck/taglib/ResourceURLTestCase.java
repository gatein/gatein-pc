/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.test.portlet.jsr286.tck.taglib;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ResourceURL;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
@TestCase({Assertion.JSR286_339})
public class ResourceURLTestCase extends TaglibTestCase
{
   public ResourceURLTestCase(PortletTestCase seq)
   {

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            response.setContentType("text/html");
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().
               getRequestDispatcher("/resourceURL.jsp");

            ResourceURL pu1 = response.createResourceURL();

            pu1.setSecure(true);
            pu1.setCacheability("FULL");

            ResourceURL pu2 = response.createResourceURL();

            pu2.setSecure(true);
            pu2.setResourceID("testId1");


            ResourceURL pu3 = response.createResourceURL();
            pu3.setSecure(false);

            pu3.setParameter("testParam", new String[] {"testParamValue", "testParamValue2"});
            pu3.setParameter("secondParam", "testParamValue");
            pu3.setProperty("testProperty", "testPropValue");
            pu3.setProperty("testProperty", "testPropValue2");
            pu3.setProperty("secondProperty", "testPropValue");


            StringWriter sw = new StringWriter();

            // Create session
            request.getPortletSession();

            // escapeXml=true
            pu1.write(sw, true);
            String url1 = response.encodeURL(sw.toString());

            sw = new StringWriter();

            // escapeXml=true
            pu2.write(sw, true);
            String url2 = response.encodeURL(sw.toString());

            sw = new StringWriter();

            // escapeXml=false
            pu3.write(sw, false);
            String url3 = response.encodeURL(sw.toString());


            expectedResults = new String[]{ url1, url2, url3};

            include(dispatcher, request, response);


            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            response.setContentType("text/html");

            assertResults(context);

            return new EndTestResponse();
         }
      });


   }
}
