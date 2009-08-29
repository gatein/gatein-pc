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
package org.gatein.pc.test.portlet.jsr168.ext.expiringcache;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.framework.UTP10;
import org.gatein.pc.test.portlet.framework.UTP11;
import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.Failure;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_EXPIRING_CACHE_3
   })
public class ExpiringCacheTestCase
{

   final Set calls = new HashSet();
   String url;

   public ExpiringCacheTestCase(PortletTestCase seq)
   {
      //
      seq.bindAction(0, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Content is not cached
            calls.add("0");

            // Refresh
            url = response.createRenderURL().toString();
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(1, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should not be called
            calls.add("1");
            return null;
         }
      });
      seq.bindAction(1, UTP11.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(2, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Could be called depending on whether the portal
            // decides to invoke UTP2 or UTP3 first
            // so if it's called we need to disable cache otherwise the next
            // render will probably not be called
            response.setProperty(RenderResponse.EXPIRATION_CACHE, "0");
            return null;
         }
      });
      seq.bindAction(2, UTP11.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            try
            {
               assertFalse(calls.contains("1"));

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
      seq.bindAction(3, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("3");

            // Invoke the same but with different render parameter
            PortletURL tmp = response.createRenderURL();
            tmp.setParameter("abc", "def");
            url = tmp.toString();
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(4, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("4");

            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(5, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should not be called
            calls.add("5");
            return null;
         }
      });
      seq.bindAction(5, UTP11.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Refresh
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(6, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Could be called depending on whether the portal
            // decides to invoke UTP2 or UTP3 first
            // so if it's called we need to disable cache otherwise the next
            // render will probably not be called
            response.setProperty(RenderResponse.EXPIRATION_CACHE, "0");
            return null;
         }
      });
      seq.bindAction(6, UTP11.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            try
            {
               assertFalse(calls.contains("5"));

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
      seq.bindAction(7, UTP10.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should be called
            calls.add("7");

            //
            Set expected = Tools.toSet("0", "3", "4", "7");
            assertEquals(expected, calls);

            // Refresh
            return new EndTestResponse();
         }
      });
   }
}
