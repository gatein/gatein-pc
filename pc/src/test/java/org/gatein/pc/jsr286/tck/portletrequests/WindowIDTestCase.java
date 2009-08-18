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
package org.gatein.pc.jsr286.tck.portletrequests;

import static org.jboss.unit.api.Assert.assertEquals;

import java.io.IOException;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.gatein.pc.framework.UTP6;
import org.gatein.pc.unit.Assertion;
import org.gatein.pc.unit.PortletTestCase;
import org.gatein.pc.unit.PortletTestContext;
import org.gatein.pc.unit.actions.PortletRenderTestAction;
import org.gatein.pc.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase(Assertion.JSR286_97)
public class WindowIDTestCase
{

   public WindowIDTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP6.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            String windowID = request.getWindowID();
            WindowIDRetriever retriever = new WindowIDRetriever();
            request.getPortletSession().setAttribute("retriever", retriever);
            assertEquals(retriever.getWindowID(), windowID);
            return new EndTestResponse();
         }
      });
   }

   public static class WindowIDRetriever implements HttpSessionBindingListener
   {

      /** . */
      private String windowID;

      public void valueBound(HttpSessionBindingEvent event)
      {
         String name = event.getName();
         windowID = name.substring("javax.portlet.p.".length(), name.indexOf('?'));
      }

      public void valueUnbound(HttpSessionBindingEvent event)
      {
      }

      public String getWindowID()
      {
         return windowID;
      }
   }
}
