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
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.PortletMode;
import javax.portlet.ResourceURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.WindowState;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_50})
public class ListenedURLUpdateTestCase
{
   public ListenedURLUpdateTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            URLGenerationListener1.delegate = updater1;
            URLGenerationListener2.delegate = updater2;

            //
            PortletURL actionURL = response.createActionURL();

            //
            String s = actionURL.toString();

            //
            assertEquals(null, actionURL.getPortletMode());
            assertEquals(null, actionURL.getWindowState());
            assertEquals(0, actionURL.getParameterMap().size());

            //
            return new InvokeGetResponse(s);
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(Collections.singletonMap("foo", new String[]{"fooAction"}), request);
            assertEquals(PortletMode.EDIT, request.getPortletMode());
            assertEquals(WindowState.MAXIMIZED, request.getWindowState());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletURL renderURL = response.createRenderURL();

            //
            String s = renderURL.toString();

            //
            assertEquals(null, renderURL.getPortletMode());
            assertEquals(null, renderURL.getWindowState());
            assertEquals(0, renderURL.getParameterMap().size());

            //
            return new InvokeGetResponse(s);
         }
      });
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(Collections.singletonMap("foo", new String[]{"fooRender"}), request);
            assertEquals(PortletMode.VIEW, request.getPortletMode());
            assertEquals(WindowState.NORMAL, request.getWindowState());

            //
            ResourceURL resourceURL = response.createResourceURL();

            //
            String s = resourceURL.toString();

            //
            assertEquals(ResourceURL.PAGE, resourceURL.getCacheability());
            assertEquals(0, resourceURL.getParameterMap().size());

            //
            return new InvokeGetResponse(s);
         }
      });
      seq.bindAction(3, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertParameterMap(Collections.singletonMap("foo", new String[]{"fooResource","fooRender"}), request);
            assertEquals("foo_resource_id", request.getResourceID());
            assertEquals(ResourceURL.PORTLET, request.getCacheability());


            return new EndTestResponse();
         }
      });
   }

   private static final PortletURLGenerationListener updater1 = new PortletURLGenerationListener()
   {
      public void filterActionURL(PortletURL portletURL)
      {
         try
         {
            portletURL.setPortletMode(PortletMode.EDIT);
         }
         catch (Exception e)
         {
         }
      }
      public void filterRenderURL(PortletURL portletURL)
      {
         try
         {
            portletURL.setPortletMode(PortletMode.VIEW);
         }
         catch (Exception e)
         {
         }
      }
      public void filterResourceURL(ResourceURL resourceURL)
      {
         try
         {
            resourceURL.setCacheability(ResourceURL.PORTLET);
         }
         catch (Exception e)
         {
         }
      }
   };

   private static final PortletURLGenerationListener updater2 = new PortletURLGenerationListener()
   {
      public void filterActionURL(PortletURL portletURL)
      {
         try
         {
            if (portletURL.getPortletMode() == PortletMode.EDIT)
            {
               portletURL.setWindowState(WindowState.MAXIMIZED);
               portletURL.setParameter("foo", "fooAction");
            }
         }
         catch (Exception e)
         {
         }
      }
      public void filterRenderURL(PortletURL portletURL)
      {
         try
         {
            if (portletURL.getPortletMode() == PortletMode.VIEW)
            {
               portletURL.setWindowState(WindowState.NORMAL);
               portletURL.setParameter("foo", "fooRender");
            }
         }
         catch (Exception e)
         {
         }
      }
      public void filterResourceURL(ResourceURL resourceURL)
      {
         try
         {
            if (ResourceURL.PORTLET.equals(resourceURL.getCacheability()))
            {
               resourceURL.setParameter("foo", "fooResource");
               resourceURL.setResourceID("foo_resource_id");
            }
         }
         catch (Exception e)
         {
         }
      }
   };

}