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

package org.gatein.pc.test.portlet.jsr286.tck.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.ServletServiceTestAction;
import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTS1;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertNotNull;
import static org.jboss.unit.api.Assert.assertNull;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.assertTrue;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import java.util.Locale;
import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
@TestCase({
   Assertion.JSR286_243,
   Assertion.JSR286_244,
   Assertion.JSR286_245,
   Assertion.JSR286_246,
   Assertion.JSR286_247,
   Assertion.JSR286_248,
   Assertion.JSR286_249,
   Assertion.JSR286_250,
   Assertion.JSR286_251,
   Assertion.JSR286_252,
   //Assertion.JSR286_253,
   Assertion.JSR286_254,
   Assertion.JSR286_255,
   Assertion.JSR286_256,
   Assertion.JSR286_257,
   Assertion.JSR286_258,
   //Assertion.JSR286_259,
   Assertion.JSR286_260,
   Assertion.JSR286_261
   })
public class ForwardFromActionEventObjectsTestCase
{
   private int actionCount = 0;

   private int eventCount = 0;

   public ForwardFromActionEventObjectsTestCase(PortletTestCase seq)
   {

      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Invoke render with header
            InvokeGetResponse action = new InvokeGetResponse(response.createActionURL().toString());
            action.addHeader("myheader").addElement("render-value");
            return action;


         }
      });

      seq.bindAction(1, UTP1.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            actionCount++;


            //we dispatch to servlet and assertions will be done there
            String queryString = "?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/universalServletA" + queryString);
            response.setEvent("Event",null);
            dispatcher.forward(request, response);

         }
      });

      seq.bindAction(1, UTP1.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            eventCount++;

            //we dispatch to servlet and assertions will be done there
            String queryString = "?key1=k1value1&key2=k2value1";
            PortletRequestDispatcher dispatcher = ((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/universalServletA" + queryString);
            dispatcher.forward(request, response);

         }
      });


      seq.bindAction(1, UTS1.SERVICE_JOIN_POINT, new ServletServiceTestAction()
      {
         protected DriverResponse run(Servlet servlet, HttpServletRequest request, HttpServletResponse response, PortletTestContext context) throws ServletException, IOException
         {
            check(request, response);

            //Only if both action and even was invoked
            if (actionCount == 1 && eventCount == 1)
            {
               return new EndTestResponse();
            }
            else
            {
               return null;
            }
         }
      });
   }

   public void check(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
   {
      //we get this object to assert some of it's method compare wieth http request methods
      PortletRequest portletRequest = (PortletRequest)request.getAttribute("javax.portlet.request");
      PortletResponse portletResponse = (PortletResponse)request.getAttribute("javax.portlet.response");

      assertNotNull(portletRequest);
      assertNotNull(portletResponse);

      //SPEC:258
      //Those should do nothing - some of the getters will be checked later
      response.setContentType("lolo");
      response.setCharacterEncoding("toto");
      response.setContentLength(22);
      response.setLocale(Locale.TRADITIONAL_CHINESE);
      response.sendError(404,"bobo");
      response.setDateHeader("bobo",11);
      response.addDateHeader("bobo",21);
      response.setHeader("bobo", "toto");
      response.addHeader("bobo", "bubu");
      response.setIntHeader("toto",13);
      response.addIntHeader("lala",25);
      response.setStatus(505);
      response.setBufferSize(12);
      response.resetBuffer();
      response.reset();
      response.flushBuffer();


      //SPEC:243
      assertNull(request.getRemoteAddr());
      assertNull(request.getRemoteHost());
      assertNull(request.getRealPath("toto"));
      assertNull(request.getLocalAddr());
      assertNull(request.getLocalName());
      assertNull(request.getRequestURL());

      //SPEC:244
      assertEquals(0, request.getRemotePort());
      assertEquals(0, request.getLocalPort());

      //SPEC:245

      assertEquals(null, request.getPathInfo());
      //TODO:NYI
      //assertEquals("",request.getPathTranslated());
      assertEquals("key1=k1value1&key2=k2value1", request.getQueryString());

      assertEquals("/test-jsr286-tck-dispatcher/universalServletA",request.getRequestURI());
      assertEquals("/universalServletA", request.getServletPath());

      //SPEC:246
      assertEquals(portletRequest.getScheme(), request.getScheme());
      assertEquals(portletRequest.getServerName(), request.getServerName());
      assertEquals(portletRequest.getServerPort(), request.getServerPort());

      // getAttributeNames, setAttribute, getAttribute, removeAttribute
      request.setAttribute("key1", "k1atrr1");
      request.setAttribute("key2", "k2attr2");
      List attrNames = new LinkedList();
      Enumeration attrEnum = request.getAttributeNames();
      while (attrEnum.hasMoreElements())
      {
         attrNames.add(attrEnum.nextElement());
      }
      assertNotNull(request.getAttribute("key1"));
      assertNotNull(request.getAttribute("key2"));

      assertTrue(attrNames.contains("key1"));
      assertTrue(attrNames.contains("key2"));

      request.removeAttribute("key1");
      assertNull(request.getAttribute("key1"));
      assertNotNull(request.getAttribute("key2"));

      //getLocale, getLocales
      assertEquals(portletRequest.getLocale(), request.getLocale());

      List portletLocales = new LinkedList();
      List servletLocales = new LinkedList();
      Enumeration pl = portletRequest.getLocales();
      while (pl.hasMoreElements())
      {
         portletLocales.add(pl.nextElement());
      }
      Enumeration sl = request.getLocales();
      while (sl.hasMoreElements())
      {
         servletLocales.add(sl.nextElement());
      }

      assertTrue(portletLocales.equals(servletLocales));

      assertEquals(portletRequest.isSecure(), request.isSecure());
      assertEquals(portletRequest.getAuthType(), request.getAuthType());
      assertEquals(portletRequest.getContextPath(), request.getContextPath());
      assertEquals(portletRequest.getRemoteUser(), request.getRemoteUser());
      assertEquals(portletRequest.getUserPrincipal(), request.getUserPrincipal());
      assertEquals(portletRequest.getRequestedSessionId(), request.getRequestedSessionId());
      assertEquals(portletRequest.isRequestedSessionIdValid(), request.isRequestedSessionIdValid());
      assertEquals(portletRequest.getCookies(), request.getCookies());

      //SPEC:247
      //in this assertions we use parameters passed in query string of dispatcher
      List paramNames = new LinkedList();
      Enumeration paramEnum = request.getParameterNames();
      while (paramEnum.hasMoreElements())
      {
         paramNames.add(paramEnum.nextElement());
      }
      assertTrue(paramNames.contains("key1"));
      assertTrue(paramNames.contains("key2"));

      assertEquals("k1value1", request.getParameter("key1"));
      assertEquals(new String[]{"k1value1"}, request.getParameterValues("key1"));

      Map paramNamesMap = request.getParameterMap();
      assertTrue(paramNamesMap.containsKey("key1"));
      assertTrue(paramNamesMap.containsKey("key2"));



      //Action only
      if (actionCount == 1 && eventCount == 0)
      {
         ActionRequest actionRequest = (ActionRequest)portletRequest;

         //SPEC:248
         try
         {
            // request was already read
            request.setCharacterEncoding("utf8");
            assertTrue(false);
         }
         catch (IllegalStateException e)
         {
            //expected
         }
         assertEquals(actionRequest.getCharacterEncoding(), request.getCharacterEncoding());
         assertEquals(actionRequest.getContentType(), request.getContentType());
         //TODO: How to check if its the same behaviour as in PR? Its not the same object.
         assertNotNull(request.getInputStream());
         assertEquals(actionRequest.getContentLength(), request.getContentLength());
         assertEquals(actionRequest.getMethod(), request.getMethod());
         // Underlaying impl invoke getImputStream so cannot test in single request
         //assertEquals(actionRequest.getReader(), request.getReader());
      }

      //Event only
      if (actionCount == 1 && eventCount == 1)
      {
         EventRequest eventRequest = (EventRequest)portletRequest;

         //SPEC:249
         request.setCharacterEncoding("utf8");
         assertNull(request.getCharacterEncoding());
         assertNull(request.getContentType());
         assertNull(request.getInputStream());
         assertNull(request.getReader());

         //SPEC:250
         assertEquals(0, request.getContentLength());

         //SPEC:251
         assertEquals(eventRequest.getMethod(), request.getMethod());
      }

      //SPEC:252
      assertEquals(portletRequest.getProperty("myheader"), request.getHeader("myheader"));
      List rheaders = Collections.list(request.getHeaders("myheader"));
      List pheaders = Collections.list(portletRequest.getProperties("myheader"));

      for (Iterator iterator = pheaders.iterator(); iterator.hasNext();)
      {
         Object o = iterator.next();
         assertTrue(rheaders.contains(o));
      }

      List headerNames = Collections.list(request.getHeaderNames());
      Enumeration propertyNamesE = portletRequest.getPropertyNames();
      while (propertyNamesE.hasMoreElements())
      {
         Object o = propertyNamesE.nextElement();
         assertTrue(headerNames.contains(o));
      }
      assertTrue(headerNames.contains("myheader"));

      //TODO: seems to not be implemented
      //assertEquals("What?", request.getDateHeader("What?"));
      //assertEquals("What?", request.getIntHeader("What?"));

      //SPEC:253
      //TODO:
      //TODO: The following methods of the HttpServletRequest must provide the
      //TODO: functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession,
      //TODO: isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl.


      //SPEC:254
      assertEquals("HTTP/1.1", request.getProtocol());

      //SPEC:255
      assertNull(response.encodeRedirectURL("lolo"));
      assertNull(response.encodeRedirectUrl("bobo"));
      assertNull(response.getCharacterEncoding());
      assertNull(response.getContentType());
      assertNull(response.getLocale());
      //TODO:fail
      //assertNull(response.getBufferSize());


      //SPEC:256
      //If it work it will break the result no?
      Writer writer = response.getWriter();
      writer.write("lolo a toto a bobo");
      writer.flush();

      OutputStream os = response.getOutputStream();
      os.write(24);
      os.write(new byte[]{25, 29, 25});
      os.flush();


      //SPEC:257
      assertEquals(portletResponse.encodeURL("http://www.jboss.com/"), response.encodeURL("http://www.jboss.com/"));
      assertEquals(portletResponse.encodeURL("http://www.jboss.com/"), response.encodeUrl("http://www.jboss.com/"));

      //SPEC:259
      //TODO: addCookie



      //SPEC:260
      assertEquals(false, response.containsHeader("toto"));

      //SPEC:261
      assertEquals(false, response.isCommitted());
   }
}
