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

import static org.jboss.unit.api.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ResourceURL;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;

/**
 * cxxx:
 * Only URLs with a cache level FULL are allowed in the response of the
 * serveResource call triggered via a ResourceURL with a cache level FULL. The
 * same restriction is true for all downstream URLs that result from this
 * serveResource call. Setting a cachability different from  must result in an
 * IllegalStateException.
 *
 * cxxxi:
 * Attempts to create URLs that are not of type FULL
 * or are not resource URLs in the current or a downstream response must result in
 * an IllegalStateException25
 *
 * cxxxii:
 * Creating other URLs, e.g. resource URLs of type  or
 * action or render URLs, must result in an IllegalStateException
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.JSR286_130,
   Assertion.JSR286_131,
   Assertion.JSR286_132
   })
public class DowngradeCacheabilityTestCase
{
   public DowngradeCacheabilityTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            ResourceURL resourceURL = response.createResourceURL();
            assertEquals(ResourceURL.PAGE, resourceURL.getCacheability());
            resourceURL.setCacheability(ResourceURL.PORTLET);
            assertEquals(ResourceURL.PORTLET, resourceURL.getCacheability());
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(1, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(ResourceURL.PORTLET, request.getCacheability());

            //
            try
            {
               response.createActionURL();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            try
            {
               response.createRenderURL();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            ResourceURL resourceURL = response.createResourceURL();
            assertEquals(ResourceURL.PORTLET, resourceURL.getCacheability());

            //
            try
            {
               resourceURL.setCacheability(ResourceURL.PAGE);
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }
            assertEquals(ResourceURL.PORTLET, resourceURL.getCacheability());

            //
            resourceURL.setCacheability(ResourceURL.PORTLET);
            assertEquals(ResourceURL.PORTLET, resourceURL.getCacheability());

            //
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(ResourceURL.PORTLET, request.getCacheability());
            ResourceURL resourceURL = response.createResourceURL();
            assertEquals(ResourceURL.PORTLET, resourceURL.getCacheability());
            resourceURL.setCacheability(ResourceURL.FULL);
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(3, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(ResourceURL.FULL, request.getCacheability());

            //
            try
            {
               response.createActionURL();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            try
            {
               response.createRenderURL();
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }

            //
            ResourceURL resourceURL = response.createResourceURL();
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());

            //
            try
            {
               resourceURL.setCacheability(ResourceURL.PORTLET);
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());

            //
            try
            {
               resourceURL.setCacheability(ResourceURL.PAGE);
               fail();
            }
            catch (IllegalStateException ignore)
            {
            }
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());

            //
            resourceURL.setCacheability(ResourceURL.FULL);
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());

            //
            return new InvokeGetResponse(resourceURL.toString());
         }
      });
      seq.bindAction(4, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            assertEquals(ResourceURL.FULL, request.getCacheability());
            ResourceURL resourceURL = response.createResourceURL();
            assertEquals(ResourceURL.FULL, resourceURL.getCacheability());
            return new EndTestResponse();
         }
      });
   }
}
