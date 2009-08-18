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
package org.gatein.pc.jsr168.tck.portletmode;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.gatein.pc.jsr168.tck.portletmode.spec.RequestDispatchingDependingOnModePortlet;
import org.gatein.pc.unit.Assertion;
import org.gatein.pc.unit.JoinPoint;
import org.gatein.pc.unit.JoinPointType;
import org.gatein.pc.unit.PortletTestCase;
import org.gatein.pc.unit.PortletTestContext;
import org.gatein.pc.unit.actions.PortletActionTestAction;
import org.gatein.pc.unit.actions.PortletRenderTestAction;
import org.gatein.pc.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_36, Assertion.JSR168_37})
public class RequestDispatchingDependingOnModePortletTestCase
{
   public RequestDispatchingDependingOnModePortletTestCase(PortletTestCase seq)
   {
      seq.bindAction(new JoinPoint(RequestDispatchingDependingOnModePortlet.NAME, JoinPointType.PORTLET_ACTION), new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            throw new AssertionError();
         }
      });
      seq.bindAction(new JoinPoint(RequestDispatchingDependingOnModePortlet.NAME, JoinPointType.PORTLET_RENDER), new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context) throws PortletException, IOException
         {
            throw new AssertionError();
         }
      });
   }
}
