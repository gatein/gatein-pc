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
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.fail;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.JSR168_142,
   Assertion.JSR168_143})
public class ErrorHandlingTestCase
{

   public ErrorHandlingTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });

      //
      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchRuntimeException(portlet, request, response);
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchRuntimeException(portlet, request, response);
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            dispatchAndCatchRuntimeException(portlet, request, response);
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, throwRuntimeException);
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchRuntimeException(portlet, request, response);
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(2, UTS1.SERVICE_JOIN_POINT, throwRuntimeException);

      //
      seq.bindAction(3, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchIOException(portlet, request, response);
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(3, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchIOException(portlet, request, response);
         }
      });
      seq.bindAction(3, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchIOException(portlet, request, response);
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(3, UTS1.SERVICE_JOIN_POINT, throwIOException);
      seq.bindAction(4, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchIOException(portlet, request, response);
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(4, UTS1.SERVICE_JOIN_POINT, throwIOException);

      //
      seq.bindAction(5, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchServletException(portlet, request, response);
            response.setEvent("Event", null);
         }
      });
      seq.bindAction(5, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchServletException(portlet, request, response);
         }
      });
      seq.bindAction(5, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException
         {
            dispatchAndCatchServletException(portlet, request, response);
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(5, UTS1.SERVICE_JOIN_POINT, throwServletException);
      seq.bindAction(6, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            dispatchAndCatchServletException(portlet, request, response);
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(6, UTS1.SERVICE_JOIN_POINT, throwServletException);

      //
      seq.bindAction(7, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException
         {
            return new EndTestResponse();
         }
      });
   }

   private void dispatchAndCatchServletException(Portlet portlet, PortletRequest request, PortletResponse response) throws IOException
   {
      PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
      assertNotNull(dispatcher);
      try
      {
         dispatcher.include(request, response);
         fail();
      }
      catch (PortletException e)
      {
         //expected
         if (!(e.getCause() instanceof ServletException))
         {
            fail();
         }
      }
   }

   private void dispatchAndCatchRuntimeException(Portlet portlet, PortletRequest request, PortletResponse response) throws PortletException, IOException
   {
      PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
      assertNotNull(dispatcher);
      try
      {
         dispatcher.include(request, response);
         fail();
      }
      catch (RuntimeException e)
      {
         //expected
      }
   }

   private void dispatchAndCatchIOException(Portlet portlet, PortletRequest request, PortletResponse response) throws PortletException, IOException
   {
      PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getNamedDispatcher("UniversalServletA");
      assertNotNull(dispatcher);
      try
      {
         dispatcher.include(request, response);
         fail();
      }
      catch (IOException e)
      {
         //expected
      }
   }

   private ServletServiceTestAction throwRuntimeException = new ServletServiceTestAction()
   {
      public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
      {
         //SPEC:142 - RuntimeException
         throw new RuntimeException();
      }
   };

   private static final ServletServiceTestAction throwIOException = new ServletServiceTestAction()
   {
      public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
      {
         //SPEC:142 - checked exception of type IOException
         throw new IOException();
      }
   };

   private static final ServletServiceTestAction throwServletException = new ServletServiceTestAction()
   {
      public DriverResponse execute(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
      {
         //SPEC:143 - ServletException
         throw new ServletException();
      }
   };
}