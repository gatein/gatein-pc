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
package org.gatein.pc.test.portlet.jsr286.tck.portleturl;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.PortletMode;
import javax.portlet.ResourceURL;
import javax.portlet.MimeResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_47,Assertion.JSR286_48,Assertion.JSR286_49,Assertion.JSR286_51})
public class URLGenerationListenerTestCase
{
   public URLGenerationListenerTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            URLGenerationListener1.delegate = CallbackQueue.createListener("l1");
            URLGenerationListener2.delegate = CallbackQueue.createListener("l2");

            //
            test(response, URLRenderer.ToString);
            test(response, URLRenderer.Write);
            test(response, URLRenderer.WriteXMLEspaced);

            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(1, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            test(response, URLRenderer.ToString);
            test(response, URLRenderer.Write);
            test(response, URLRenderer.WriteXMLEspaced);

            //
            return new EndTestResponse();
         }
      });
   }

   protected DriverResponse test(MimeResponse response, URLRenderer renderer) throws PortletException, IOException
   {
      CallbackQueue.clear();
      PortletURL actionURL = response.createActionURL();
      assertEquals(0, CallbackQueue.size());

      // Assert initial state
      renderer.render(actionURL);
      PortletURLSnapshot actionSnapshot1 = CallbackQueue.next();
      PortletURLSnapshot actionSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      actionSnapshot1.assertEquals(PortletURLSnapshot.createActionURL("l1", null, null, new HashMap<String, String[]>()));
      actionSnapshot2.assertEquals(PortletURLSnapshot.createActionURL("l2", null, null, new HashMap<String, String[]>()));

      // Assert portlet mode change is propagated
      actionURL.setPortletMode(PortletMode.EDIT);
      renderer.render(actionURL);
      actionSnapshot1 = CallbackQueue.next();
      actionSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      actionSnapshot1.assertEquals(PortletURLSnapshot.createActionURL("l1", PortletMode.EDIT, null, new HashMap<String, String[]>()));
      actionSnapshot2.assertEquals(PortletURLSnapshot.createActionURL("l2", PortletMode.EDIT, null, new HashMap<String, String[]>()));

      // Assert parameter change is propagated
      actionURL.setParameter("foo", "bar");
      renderer.render(actionURL);
      actionSnapshot1 = CallbackQueue.next();
      actionSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      actionSnapshot1.assertEquals(PortletURLSnapshot.createActionURL("l1", PortletMode.EDIT, null, Collections.singletonMap("foo", new String[]{"bar"})));
      actionSnapshot2.assertEquals(PortletURLSnapshot.createActionURL("l2", PortletMode.EDIT, null, Collections.singletonMap("foo", new String[]{"bar"})));

      //
      PortletURL renderURL = response.createRenderURL();
      assertEquals(0, CallbackQueue.size());

      // Assert initial state
      renderer.render(renderURL);
      PortletURLSnapshot renderSnapshot1 = CallbackQueue.next();
      PortletURLSnapshot renderSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      renderSnapshot1.assertEquals(PortletURLSnapshot.createRenderURL("l1", null, null, new HashMap<String, String[]>()));
      renderSnapshot2.assertEquals(PortletURLSnapshot.createRenderURL("l2", null, null, new HashMap<String, String[]>()));

      // Assert portlet mode change is propagated
      renderURL.setPortletMode(PortletMode.EDIT);
      renderer.render(renderURL);
      renderSnapshot1 = CallbackQueue.next();
      renderSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      renderSnapshot1.assertEquals(PortletURLSnapshot.createRenderURL("l1", PortletMode.EDIT, null, new HashMap<String, String[]>()));
      renderSnapshot2.assertEquals(PortletURLSnapshot.createRenderURL("l2", PortletMode.EDIT, null, new HashMap<String, String[]>()));

      // Assert parameter change is propagated
      renderURL.setParameter("foo", "bar");
      renderer.render(renderURL);
      renderSnapshot1 = CallbackQueue.next();
      renderSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      renderSnapshot1.assertEquals(PortletURLSnapshot.createRenderURL("l1", PortletMode.EDIT, null, Collections.singletonMap("foo", new String[]{"bar"})));
      renderSnapshot2.assertEquals(PortletURLSnapshot.createRenderURL("l2", PortletMode.EDIT, null, Collections.singletonMap("foo", new String[]{"bar"})));

      //
      ResourceURL resourceURL = response.createResourceURL();
      assertEquals(0, CallbackQueue.size());

      // Assert initial state
      resourceURL.toString();
      PortletURLSnapshot resourceSnapshot1 = CallbackQueue.next();
      PortletURLSnapshot resourceSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      resourceSnapshot1.assertEquals(PortletURLSnapshot.createResourceURL("l1", new HashMap<String, String[]>(), ResourceURL.PAGE));
      resourceSnapshot2.assertEquals(PortletURLSnapshot.createResourceURL("l2", new HashMap<String, String[]>(), ResourceURL.PAGE));

      // Assert cacheability change is propagated
      resourceURL.setCacheability(ResourceURL.PORTLET);
      resourceURL.toString();
      resourceSnapshot1 = CallbackQueue.next();
      resourceSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      resourceSnapshot1.assertEquals(PortletURLSnapshot.createResourceURL("l1",  new HashMap<String, String[]>(), ResourceURL.PORTLET));
      resourceSnapshot2.assertEquals(PortletURLSnapshot.createResourceURL("l2",  new HashMap<String, String[]>(), ResourceURL.PORTLET));

      // Assert parameter change is propagated
      resourceURL.setParameter("foo", "bar");
      resourceURL.toString();
      resourceSnapshot1 = CallbackQueue.next();
      resourceSnapshot2 = CallbackQueue.next();
      assertEquals(0, CallbackQueue.size());
      resourceSnapshot1.assertEquals(PortletURLSnapshot.createResourceURL("l1", Collections.singletonMap("foo", new String[]{"bar"}), ResourceURL.PORTLET));
      resourceSnapshot2.assertEquals(PortletURLSnapshot.createResourceURL("l2", Collections.singletonMap("foo", new String[]{"bar"}), ResourceURL.PORTLET));

      //
      return new EndTestResponse();
   }


}
