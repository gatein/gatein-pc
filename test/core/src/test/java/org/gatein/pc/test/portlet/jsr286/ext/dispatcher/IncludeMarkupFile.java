/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.test.portlet.jsr286.ext.dispatcher;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.web.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.unit.web.UTP1;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import static org.gatein.pc.test.unit.Assert.*;
import org.gatein.pc.test.unit.protocol.response.InvokeGetResponse;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 *
 * Disabled until we can be sure to have the default servlet that will make serving of the file
 * via request dispatching to it.
 */
@TestCase(value =Assertion.EXT_DISPATCHER_6, enabled = false)
public class IncludeMarkupFile
{
   public IncludeMarkupFile(PortletTestCase seq)
   {
      seq.bindAction(0, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletRequestDispatcher dispatcher = assertNotNull(((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/file.html"));
            dispatcher.include(request, response);

            //
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });
      seq.bindAction(1, UTP1.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            String body = new String(context.getResponseBody(), "UTF-8");

            //
            assertTrue("Expected " + body + " to contain @DISPATCHED_MARKUP@ token", body.contains("@DISPATCHED_MARKUP@"));
            
            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(2, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected Response run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            PortletRequestDispatcher dispatcher = assertNotNull(((AbstractUniversalTestPortlet)portlet).getPortletContext().getRequestDispatcher("/file.html"));
            dispatcher.include(request, response);

            //
            return new InvokeGetResponse(response.createResourceURL().toString());
         }
      });
      seq.bindAction(3, UTP1.RESOURCE_JOIN_POINT, new PortletResourceTestAction()
      {
         protected Response run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
         {
            String body = new String(context.getResponseBody(), "UTF-8");

            //
            assertTrue("Expected " + body + " to contain @DISPATCHED_MARKUP@ token", body.contains("@DISPATCHED_MARKUP@"));

            //
            return new EndTestResponse();
         }
      });
   }
}
