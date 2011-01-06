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
package org.gatein.pc.test.portlet.jsr286.tck.resourceserving;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokePostResponse;
import org.jboss.unit.remote.http.HttpRequest;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_128})
public class ResourceURLParametersTestCase
{

   public ResourceURLParametersTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            ResourceURL url = response.createResourceURL();

            //
            url.setParameter("resource", new String[]{"resource_value"});

            //
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(1, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedParameters = Collections.singletonMap("resource", new String[]{"resource_value"});
            assertParameterMap(expectedParameters, request);
            assertParameterMap(expectedParameters, request.getPrivateParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateRenderParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());

            //
            ResourceURL url = response.createResourceURL();

            //
            url.setParameter("resource", new String[]{"resource_value"});
            url.setParameter("resource_form", new String[]{"resource_form_value1"});

            //
            InvokePostResponse post = new InvokePostResponse(url.toString());
            post.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED);
            HttpRequest.Form form = new HttpRequest.Form();
            form.addParameter("form", new String[]{"form_value"});
            form.addParameter("resource_form", new String[]{"resource_form_value2"});
            post.setBody(form);

            //
            return post;
         }
      });
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedParameters = new HashMap<String, String[]>();
            expectedParameters.put("resource", new String[]{"resource_value"});
            expectedParameters.put("resource_form", new String[]{"resource_form_value1", "resource_form_value2"});
            expectedParameters.put("form", new String[]{"form_value"});
            assertParameterMap(expectedParameters, request);
            assertParameterMap(expectedParameters, request.getPrivateParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPrivateRenderParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());

            //
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("render", "render_value");
            renderURL.setParameter("resource_render", "resource_render_value2");
            renderURL.setParameter("form_render", "form_render_value2");
            renderURL.setParameter("resource_form_render", "resource_form_render_value3");

            //
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            ResourceURL url = response.createResourceURL();

            //
            url.setParameter("resource", new String[]{"resource_value"});
            url.setParameter("resource_render", new String[]{"resource_render_value1"});

            //
            return new InvokeGetResponse(url.toString());
         }
      });
      seq.bindAction(4, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedParameters = new HashMap<String, String[]>();
            expectedParameters.put("resource", new String[]{"resource_value"});
            expectedParameters.put("resource_render", new String[]{"resource_render_value1","resource_render_value2"});
            expectedParameters.put("render", new String[]{"render_value"});
            expectedParameters.put("form_render", new String[]{"form_render_value2"});
            expectedParameters.put("resource_form_render", new String[]{"resource_form_render_value3"});
            Map<String, String[]> expectedPrivateRenderParameters = new HashMap<String, String[]>();
            expectedPrivateRenderParameters.put("render", new String[]{"render_value"});
            expectedPrivateRenderParameters.put("resource_render", new String[]{"resource_render_value2"});
            expectedPrivateRenderParameters.put("form_render", new String[]{"form_render_value2"});
            expectedPrivateRenderParameters.put("resource_form_render", new String[]{"resource_form_render_value3"});
            assertParameterMap(expectedParameters, request);
            assertParameterMap(expectedParameters, request.getPrivateParameterMap());
            assertParameterMap(expectedPrivateRenderParameters, request.getPrivateRenderParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());

            //
            ResourceURL url = response.createResourceURL();

            //
            url.setParameter("resource", new String[]{"resource_value"});
            url.setParameter("resource_form", new String[]{"resource_form_value1"});
            url.setParameter("resource_render", new String[]{"resource_render_value1"});
            url.setParameter("resource_form_render", new String[]{"resource_form_render_value1"});

            //
            InvokePostResponse post = new InvokePostResponse(url.toString());
            post.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED);
            HttpRequest.Form form = new HttpRequest.Form();
            form.addParameter("form", new String[]{"form_value"});
            form.addParameter("resource_form", new String[]{"resource_form_value2"});
            form.addParameter("form_render", new String[]{"form_render_value1"});
            form.addParameter("resource_form_render", new String[]{"resource_form_render_value2"});
            post.setBody(form);

            //
            return post;
         }
      });
      seq.bindAction(5, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedParameters = new HashMap<String, String[]>();
            expectedParameters.put("resource", new String[]{"resource_value"});
            expectedParameters.put("resource_form", new String[]{"resource_form_value1","resource_form_value2"});
            expectedParameters.put("resource_render", new String[]{"resource_render_value1","resource_render_value2"});
            expectedParameters.put("resource_form_render", new String[]{"resource_form_render_value1","resource_form_render_value2","resource_form_render_value3"});
            expectedParameters.put("form", new String[]{"form_value"});
            expectedParameters.put("form_render", new String[]{"form_render_value1","form_render_value2"});
            expectedParameters.put("render", new String[]{"render_value"});
            Map<String, String[]> expectedPrivateRenderParameters = new HashMap<String, String[]>();
            expectedPrivateRenderParameters.put("render", new String[]{"render_value"});
            expectedPrivateRenderParameters.put("resource_render", new String[]{"resource_render_value2"});
            expectedPrivateRenderParameters.put("form_render", new String[]{"form_render_value2"});
            expectedPrivateRenderParameters.put("resource_form_render", new String[]{"resource_form_render_value3"});
            assertParameterMap(expectedParameters, request);
            assertParameterMap(expectedParameters, request.getPrivateParameterMap());
            assertParameterMap(expectedPrivateRenderParameters, request.getPrivateRenderParameterMap());
            assertParameterMap(new HashMap<String, String[]>(), request.getPublicParameterMap());

            //
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("render", "render_value");
            renderURL.setParameter("resource_render", "resource_render_value2");
            renderURL.setParameter("form_render", "form_render_value2");
            renderURL.setParameter("resource_form_render", "resource_form_render_value3");
            renderURL.setParameter("public", "public_value");
            renderURL.setParameter("resource_public", "resource_public_value2");
            renderURL.setParameter("form_public", "form_public_value2");
            renderURL.setParameter("resource_form_public", "resource_form_public_value3");

            //
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(6, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            //
            ResourceURL url = response.createResourceURL();

            //
            url.setParameter("resource", new String[]{"resource_value"});
            url.setParameter("resource_form", new String[]{"resource_form_value1"});
            url.setParameter("resource_render", new String[]{"resource_render_value1"});
            url.setParameter("resource_form_render", new String[]{"resource_form_render_value1"});
            url.setParameter("resource_public", new String[]{"resource_public_value1"});
            url.setParameter("resource_form_public", new String[]{"resource_form_public_value1"});

            //
            InvokePostResponse post = new InvokePostResponse(url.toString());
            post.setContentType(InvokePostResponse.APPLICATION_X_WWW_FORM_URLENCODED);
            HttpRequest.Form form = new HttpRequest.Form();
            form.addParameter("form", new String[]{"form_value"});
            form.addParameter("resource_form", new String[]{"resource_form_value2"});
            form.addParameter("form_render", new String[]{"form_render_value1"});
            form.addParameter("resource_form_render", new String[]{"resource_form_render_value2"});
            form.addParameter("form_public", new String[]{"form_public_value1"});
            form.addParameter("resource_form_public", new String[]{"resource_form_public_value2"});
            post.setBody(form);

            //
            return post;
         }
      });
      seq.bindAction(7, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Map<String, String[]> expectedParameters = new HashMap<String, String[]>();
            expectedParameters.put("resource", new String[]{"resource_value"});
            expectedParameters.put("form", new String[]{"form_value"});
            expectedParameters.put("resource_form", new String[]{"resource_form_value1","resource_form_value2"});
            expectedParameters.put("resource_render", new String[]{"resource_render_value1","resource_render_value2"});
            expectedParameters.put("resource_form_render", new String[]{"resource_form_render_value1","resource_form_render_value2","resource_form_render_value3"});
            expectedParameters.put("form_render", new String[]{"form_render_value1","form_render_value2"});
            expectedParameters.put("render", new String[]{"render_value"});
            expectedParameters.put("resource_public", new String[]{"resource_public_value1","resource_public_value2"});
            expectedParameters.put("resource_form_public", new String[]{"resource_form_public_value1","resource_form_public_value2","resource_form_public_value3"});
            expectedParameters.put("form_public", new String[]{"form_public_value1","form_public_value2"});
            expectedParameters.put("public", new String[]{"public_value"});
            Map<String, String[]> expectedPrivateRenderParameters = new HashMap<String, String[]>();
            expectedPrivateRenderParameters.put("resource_render", new String[]{"resource_render_value2"});
            expectedPrivateRenderParameters.put("resource_form_render", new String[]{"resource_form_render_value3"});
            expectedPrivateRenderParameters.put("form_render", new String[]{"form_render_value2"});
            expectedPrivateRenderParameters.put("render", new String[]{"render_value"});
            Map<String, String[]> expectedPrivateParameters = new HashMap<String, String[]>();
            expectedPrivateParameters.put("resource", new String[]{"resource_value"});
            expectedPrivateParameters.put("form", new String[]{"form_value"});
            expectedPrivateParameters.put("resource_form", new String[]{"resource_form_value1","resource_form_value2"});
            expectedPrivateParameters.put("resource_render", new String[]{"resource_render_value1","resource_render_value2"});
            expectedPrivateParameters.put("resource_form_render", new String[]{"resource_form_render_value1","resource_form_render_value2","resource_form_render_value3"});
            expectedPrivateParameters.put("form_render", new String[]{"form_render_value1","form_render_value2"});
            expectedPrivateParameters.put("render", new String[]{"render_value"});
            expectedPrivateParameters.put("resource_public", new String[]{"resource_public_value1"});
            expectedPrivateParameters.put("resource_form_public", new String[]{"resource_form_public_value1","resource_form_public_value2"});
            expectedPrivateParameters.put("form_public", new String[]{"form_public_value1"});
            Map<String, String[]> expectedPublicParameters = new HashMap<String, String[]>();
            expectedPublicParameters.put("resource_public", new String[]{"resource_public_value2"});
            expectedPublicParameters.put("resource_form_public", new String[]{"resource_form_public_value3"});
            expectedPublicParameters.put("form_public", new String[]{"form_public_value2"});
            expectedPublicParameters.put("public", new String[]{"public_value"});
            assertParameterMap(expectedParameters, request);
            assertParameterMap(expectedPrivateParameters, request.getPrivateParameterMap());
            assertParameterMap(expectedPrivateRenderParameters, request.getPrivateRenderParameterMap());
            assertParameterMap(expectedPublicParameters, request.getPublicParameterMap());

            //
            return new EndTestResponse();
         }
      });
   }
}
