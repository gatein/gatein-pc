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
package org.gatein.pc.test.portlet.jsr168.ext.common;

import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.common.util.Tools;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import static org.jboss.unit.api.Assert.assertEquals;

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
public class AbstractActionInvalidatesTestCase
{

   final Set calls = new HashSet();
   String url;


   public AbstractActionInvalidatesTestCase(
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
            // Content is not cached
            calls.add("0");

            // Refresh
            url = response.createActionURL().toString();
            return new InvokeGetResponse(response.createRenderURL().toString());
         }
      });

      //
      seq.bindAction(1, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Should no be called
            calls.add("1");
            return null;
         }
      });
      seq.bindAction(1, p2renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Invalidate
            return new InvokeGetResponse(url);
         }
      });

      //
      seq.bindAction(2, p1actionjp, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            //
            calls.add("2_action");
         }
      });
      seq.bindAction(2, p1renderjp, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws IOException, PortletException
         {
            // Content is not cached
            calls.add("2_render");

            //
            Set expected = Tools.toSet("0", "2_action", "2_render");
            assertEquals(expected, calls);

            // End test
            return new EndTestResponse();
         }
      });
   }
}
