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
package org.gatein.pc.test.portlet.jsr286.tck.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.common.util.Tools;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.fail;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.PortletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public abstract class DispatchedContainerAttributesTestCase
{

   /** . */
   private final Map<String, String> expectedAttributes;

   /** . */
   private final Map<String, String> expectedInfos;

   /** . */
   private final Map<String, Object> collectedAttributes;

   /** . */
   private final Map<String, Object> collectedInfos;

   public DispatchedContainerAttributesTestCase(
      PortletTestCase seq,
      Map<String, String> expectedAttributes)
   {
      this(seq, expectedAttributes, new HashMap<String, String>());
   }

   public DispatchedContainerAttributesTestCase(
      PortletTestCase seq,
      Map<String, String> expectedAttributes,
      Map<String, String> expectedInfos)
   {
      this.expectedAttributes = expectedAttributes;
      this.expectedInfos = expectedInfos;
      this.collectedAttributes = new HashMap<String, Object>();
      this.collectedInfos = new HashMap<String, Object>();

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (performTest(LifeCyclePhase.ACTION))
            {
               doTest(portlet, request, response);
            }

            //
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (performTest(LifeCyclePhase.EVENT))
            {
               doTest(portlet, request, response);
            }
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            if (performTest(LifeCyclePhase.RENDER))
            {
               doTest(portlet, request, response);
            }

            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, service);
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            if (performTest(LifeCyclePhase.RESOURCE))
            {
               doTest(portlet, request, response);
            }

            //
            return new EndTestResponse();
         }
      });
      seq.bindAction(2, UTS1.SERVICE_JOIN_POINT, service);
   }

   protected abstract void dispatch(PortletRequest request, PortletResponse response, PortletContext portletContext) throws IOException, PortletException;

   protected boolean performTest(LifeCyclePhase phase)
   {
      return true;
   }

   private void doTest(
      Portlet portlet,
      PortletRequest request,
      PortletResponse response) throws PortletException, IOException
   {
      PortletContext portletContext = ((AbstractUniversalTestPortlet)portlet).getPortletContext();
      dispatch(request, response, portletContext);

      //
      for (Map.Entry<String, String> entry : expectedAttributes.entrySet())
      {
         String name = entry.getKey();
         String expectedValue = entry.getValue();

         //
         if (expectedValue == null)
         {
            if (collectedAttributes.containsKey(name))
            {
               fail("The actual map " + collectedAttributes + " should not contain the name " + name);
            }
         }
         else
         {
            if (collectedAttributes.containsKey(name))
            {
               Object collectedValue = collectedAttributes.get(name);
               assertEquals("Was expecting a value " + expectedValue + " for key " + name + " but had instead " + collectedValue, expectedValue, collectedValue);
            }
            else
            {
               fail("The actual map " + collectedAttributes + " should contain the name " + name);
            }
         }
      }

      //
      for (Map.Entry<String, String> entry : expectedInfos.entrySet())
      {
         String key = entry.getKey();
         String expectedValue = entry.getValue();
         Object collectedValue = collectedInfos.get(key);
         assertEquals("Was expecting a value " + expectedValue + " for key " + key + " but had instead " + collectedValue, expectedValue, collectedValue);
      }
   }

   ServletServiceTestAction service = new ServletServiceTestAction()
   {
      protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
      {
         Set attributeNames =  Tools.toSet(request.getAttributeNames());
         collectedAttributes.clear();
         for (String name : expectedAttributes.keySet())
         {
            if (attributeNames.contains(name))
            {
               Object value = request.getAttribute(name);
               collectedAttributes.put(name, value);
            }
         }

         //
         collectedInfos.clear();
         collectedInfos.put("path_info", request.getPathInfo());
         collectedInfos.put("context_path", request.getContextPath());
         collectedInfos.put("query_string", request.getQueryString());
         collectedInfos.put("servlet_path", request.getServletPath());
         collectedInfos.put("request_uri", request.getRequestURI());

         //
         return null;
      }
   };
}