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
package org.gatein.pc.test.portlet.jsr168.ext.nocache;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.common.util.Tools;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.Failure;
import static org.jboss.unit.api.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractNoCacheTestCase
{

   final Set<String> calls = new HashSet<String>();
   String url;

   public AbstractNoCacheTestCase(
      PortletTestCase seq,
      JoinPoint p1renderjp,
      JoinPoint p1actionjp,
      JoinPoint p2renderjp)
   {
      //
      seq.bindAction(0, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("0");

            // Refresh
            url = response.createRenderURL().toString();
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(1, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("1");
            response.setProperty(RenderResponse.EXPIRATION_CACHE, "0");

            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(2, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("2");
            response.setProperty(RenderResponse.EXPIRATION_CACHE, "5");
            response.setProperty(RenderResponse.ETAG, "xyz");

            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(3, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should not be called
            calls.add("3");
            return null;
         }
      });
      seq.bindAction(3, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(4, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Could be called or not depending on the page rendering ordre
            return null;
         }
      });
      seq.bindAction(4, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            try
            {
               // Wait 5 seconds for the cache entry to be invalid
               Thread.sleep(5 * 1000);

               // Refresh
               return new InvokeGetResponse(url);
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }
         }
      });

      //
      seq.bindAction(5, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            assertEquals("xyz", request.getETag());
            assertTrue(Tools.toSet(request.getPropertyNames()).contains(RenderRequest.ETAG));
            assertEquals("xyz", request.getProperty(RenderRequest.ETAG));

            // Record we were called
            calls.add("5");

            // Revalidate existing content 
            response.setProperty(RenderResponse.EXPIRATION_CACHE, "5");
            response.setProperty(RenderResponse.USE_CACHED_CONTENT, "foo");

            //
            url = response.createRenderURL().toString();
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(6, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should not be called
            calls.add("6");
            return null;
         }
      });
      seq.bindAction(6, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(7, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Could be called or not depending on the page rendering ordre
            return null;
         }
      });
      seq.bindAction(7, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            try
            {
               // Wait 5 seconds for the cache entry to be invalid
               Thread.sleep(5 * 1000);

               // Refresh
               return new InvokeGetResponse(url);
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }
         }
      });

      //
      seq.bindAction(8, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            assertEquals("xyz", request.getETag());
            assertTrue(Tools.toSet(request.getPropertyNames()).contains(RenderRequest.ETAG));
            assertEquals("xyz", request.getProperty(RenderRequest.ETAG));

            // Record we were called
            calls.add("8");

            url = response.createActionURL().toString();
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(9, p1actionjp, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            // Should be called
            calls.add("9_action");
         }
      });
      seq.bindAction(9, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("9_render");
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      //
      seq.bindAction(10, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            return null;
         }
      });
      seq.bindAction(10, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            Set expected = Tools.toSet("0", "1", "2", "5", "8", "9_render", "9_action");
            assertEquals(expected, calls);

            // Refresh
            return new EndTestResponse();
         }
      });
   }
}
