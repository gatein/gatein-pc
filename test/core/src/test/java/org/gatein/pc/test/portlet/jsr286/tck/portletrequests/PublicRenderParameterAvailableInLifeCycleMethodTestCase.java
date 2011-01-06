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
package org.gatein.pc.test.portlet.jsr286.tck.portletrequests;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP5;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokePostResponse;
import org.jboss.unit.remote.http.HttpRequest;
import static org.jboss.unit.api.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 *  lxxxv: 
 *
 * A portlet can access the public render parameters in any lifecycle method via the 
 * getPublicParameterMap method of the portlet request. In addition the portlet can access
 * public render parameters via the getParameter and getParameterMap methods. In the
 * case of a processAction or serveResource call the public parameters are merged with
 * the action / resource parameters set on the action / resource URL. If a action or resource
 * parameter has the same name as a public render parameter the public render parameter
 * values must be the last entries in the parameter value array.
 *
 * lxxxiv:
 *
 * If the portlet was the target of a render URL and this render URL has set a specific public
 * render parameter the portlet must receive at least this render parameter 
 *
 * lxxxvi:
 *
 * Portlets can access a merged set of public and private parameters via the getParameter
 * methods on the PortletRequest or separated as maps of private parameters via the
 * getPrivateParameterMap method and public parameters via the
 * getPublicParameterMap method.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.JSR168_84,
   Assertion.JSR168_85,
   Assertion.JSR168_86
   })
public class PublicRenderParameterAvailableInLifeCycleMethodTestCase
{

   /** . */
   private Map<String, String[]> expectedClientDataMap;

   /** . */
   private Map<String, String[]> expectedClientDataPrivateMap;

   /** . */
   private Map<String, String[]> expectedPublicMap;

   public PublicRenderParameterAvailableInLifeCycleMethodTestCase(PortletTestCase seq)
   {
      expectedClientDataMap = new HashMap<String, String[]>();
      expectedClientDataMap.put("foo",new String[]{"foo_value1","foo_value2"});
      expectedClientDataMap.put("bar",new String[]{"is_bar_value1","is_bar_value2","bar_value1","bar_value2"});
      expectedClientDataMap.put("juu",new String[]{"form_juu_value1","form_juu_value2","juu_value1","juu_value2"});
      expectedClientDataMap.put("daa",new String[]{"is_daa_value1","is_daa_value2","form_daa_value1","form_daa_value2","daa_value1","daa_value2"});

      //
      expectedClientDataPrivateMap = new HashMap<String, String[]>();
      expectedClientDataPrivateMap.put("bar",new String[]{"is_bar_value1","is_bar_value2"});
      expectedClientDataPrivateMap.put("juu",new String[]{"form_juu_value1","form_juu_value2"});
      expectedClientDataPrivateMap.put("daa",new String[]{"is_daa_value1","is_daa_value2","form_daa_value1","form_daa_value2"});

      //
      expectedPublicMap = new HashMap<String, String[]>();
      expectedPublicMap.put("foo",new String[]{"foo_value1","foo_value2"});
      expectedPublicMap.put("bar",new String[]{"bar_value1","bar_value2"});
      expectedPublicMap.put("juu",new String[]{"juu_value1","juu_value2"});
      expectedPublicMap.put("daa",new String[]{"daa_value1","daa_value2"});

      //
      seq.bindAction(0, UTP5.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL renderURL = response.createRenderURL();
            renderURL.setParameter("foo", new String[]{"foo_value1","foo_value2"});
            renderURL.setParameter("bar", new String[]{"bar_value1","bar_value2"});
            renderURL.setParameter("juu", new String[]{"juu_value1","juu_value2"});
            renderURL.setParameter("daa", new String[]{"daa_value1","daa_value2"});
            return new InvokeGetResponse(renderURL.toString());
         }
      });
      seq.bindAction(1, UTP5.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletURL actionURL = response.createActionURL();
            actionURL.setParameter("bar", new String[]{"is_bar_value1","is_bar_value2"});
            actionURL.setParameter("daa", new String[]{"is_daa_value1","is_daa_value2"});

            //
            InvokePostResponse post = new InvokePostResponse(actionURL.toString());
            HttpRequest.Form form = new HttpRequest.Form();
            form.addParameter("juu", new String[]{"form_juu_value1","form_juu_value2"});
            form.addParameter("daa", new String[]{"form_daa_value1","form_daa_value2"});
            post.setBody(form);

            //
            return post;
         }
      });
      seq.bindAction(2, UTP5.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedClientDataMap, request);
            assertEquals(expectedClientDataPrivateMap, request.getPrivateParameterMap());
            assertEquals(expectedPublicMap, request.getPublicParameterMap());
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(2, UTP5.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedPublicMap, request);
            assertEquals(Collections.emptyMap(), request.getPrivateParameterMap());
            assertEquals(expectedPublicMap, request.getPublicParameterMap());
         }
      });
      seq.bindAction(2, UTP5.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertParameterMap(expectedPublicMap, request);
            assertEquals(Collections.emptyMap(), request.getPrivateParameterMap());
            assertEquals(expectedPublicMap, request.getPublicParameterMap());

            //
            ResourceURL resourceURL = response.createResourceURL();
            resourceURL.setParameter("bar", new String[]{"is_bar_value1","is_bar_value2"});
            resourceURL.setParameter("daa", new String[]{"is_daa_value1","is_daa_value2"});

            //
            InvokePostResponse post = new InvokePostResponse(resourceURL.toString());
            HttpRequest.Form form = new HttpRequest.Form();
            form.addParameter("juu", new String[]{"form_juu_value1","form_juu_value2"});
            form.addParameter("daa", new String[]{"form_daa_value1","form_daa_value2"});
            post.setBody(form);

            //
            return post;
         }
      });
      seq.bindAction(3, UTP5.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(expectedClientDataMap, request);
            assertEquals(expectedClientDataPrivateMap, request.getPrivateParameterMap());
            assertEquals(expectedPublicMap, request.getPublicParameterMap());

            //
            return new EndTestResponse();
         }
      });
   }
}
