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
package org.gatein.pc.test.portlet.jsr286.ext.cache;

import org.gatein.pc.test.portlet.framework.UTP1;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.Failure;
import org.jboss.unit.api.Assert;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Calling render on the portlet with different render parameters invalidates the cache.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_CACHE_VALIDATION
   })
public class CacheValidationTestCase
{

   /** . */
   private boolean cached;

   public CacheValidationTestCase(PortletTestCase seq)
   {
      // Set two seconds of expiration
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            cached = true;

            //
            response.setContentType("text/html");
            PrintWriter pw = response.getWriter();
            pw.print("foocached");

            // Set cache for two seconds with an etag
            response.getCacheControl().setExpirationTime(2);
            response.getCacheControl().setETag("footag");

            //
            return null;
         }
      });
      seq.bindAction(0, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Trigger a resource so we are sure that the cached portlet will not be part of the whole request
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });

      //
      seq.bindAction(1, UTP2.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         @Override
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            try
            {
               // Wait for one second so the cached content will be aged of 1 second
               Thread.sleep(1000);

               // Now render the full page
               return new InvokeGetResponse(response.createRenderURL().toString());
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }
         }
      });

      // Now make the request to the full page after one second, the goal is to test also an issue whereby the request
      // to the cached content extends the expiration out of the box, as we will wait after that for one second, if that
      // problem occur, then the revalidation would not occur at all
      seq.bindAction(2, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Set cached to false to make test fail later
            cached = false;
            return null;
         }
      });
      seq.bindAction(2, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Trigger a resource so we are sure that the cached portlet will not be part of the whole request
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });

      //
      seq.bindAction(3, UTP2.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         @Override
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            // Check caching occurred
            Assert.assertTrue(cached);

            // Check it contains the cached content
            byte[] bytes = (byte[])context.getPayload().get("http.response.body");
            String cachedMarkup = new String(bytes, "UTF-8");
            Assert.assertTrue("Was expected " + cachedMarkup + " to contain foocached", cachedMarkup.contains("foocached"));

            // Wait for one second so the content should have just expired
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      // Assert we have the etag and revalidate response for two seconds
      seq.bindAction(4, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Check everything is as espected
            Assert.assertEquals("footag", request.getETag());

            // Revalidate markup for two seconds and invoke again
            response.getCacheControl().setUseCachedContent(true);
            response.getCacheControl().setExpirationTime(2);

            //
            return null;
         }
      });
      seq.bindAction(4, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Trigger a resource so we are sure that the cached portlet will not be part of the whole request
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });

      //
      seq.bindAction(5, UTP2.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         @Override
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            try
            {
               // Wait for one second so the cached content will be aged of 1 second
               Thread.sleep(1000);

               // Now render the full page
               return new InvokeGetResponse(response.createRenderURL().toString());
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }
         }
      });

      //
      seq.bindAction(6, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Set cached to false to make test fail later
            cached = false;
            return null;
         }
      });
      seq.bindAction(6, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Trigger a resource so we are sure that the cached portlet will not be part of the whole request
            // and we can make the 2 seconds pause without messing with the invalid entry we want to revalidate
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });

      //
      seq.bindAction(7, UTP2.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         @Override
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            // Check caching occurred
            Assert.assertTrue(cached);

            // Check it contains the cached content
            byte[] bytes = (byte[])context.getPayload().get("http.response.body");
            String cachedMarkup = new String(bytes, "UTF-8");
            Assert.assertTrue("Was expected " + cachedMarkup + " to contain foocached", cachedMarkup.contains("foocached"));

            // Wait for one second so the content should have just expired
            try
            {
               Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
               return new FailureResponse(Failure.createFailure(e));
            }

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      // Assert we have the etag and revalidate response for two seconds
      seq.bindAction(8, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Check everything is as espected
            Assert.assertEquals("footag", request.getETag());

            // Finish the test
            return new EndTestResponse();
         }
      });
   }
}
