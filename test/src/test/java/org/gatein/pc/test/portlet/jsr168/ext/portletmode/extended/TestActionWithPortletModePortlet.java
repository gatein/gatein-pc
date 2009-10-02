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
package org.gatein.pc.test.portlet.jsr168.ext.portletmode.extended;

import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.annotations.TestActor;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 */
@TestActor(id=TestActionWithPortletModePortlet.NAME)
public class TestActionWithPortletModePortlet extends AbstractUniversalTestPortlet
{

   public static final String NAME = "TestActionWithPortletModePortlet";

   public final static JoinPoint RENDER_JOIN_POINT = new JoinPoint(org.gatein.pc.test.portlet.jsr168.ext.portletmode.extended.TestActionWithPortletModePortlet.NAME, JoinPointType.PORTLET_RENDER);

   public final static JoinPoint ACTION_JOIN_POINT = new JoinPoint(org.gatein.pc.test.portlet.jsr168.ext.portletmode.extended.TestActionWithPortletModePortlet.NAME, JoinPointType.PORTLET_ACTION);

   /*private AssertResult result;

   protected void doProcessAction(final ActionRequest req, ActionResponse resp) throws PortletException, PortletSecurityException, IOException
   {

      if (TestContext.getCurrentRequestCount() == 1 && result == null);
      {
         result = new AssertResult();
         result.execute(new AssertResult.Test()
         {
            public void run() throws Exception
            {
               // Test we get the right portlet mode
               assertEquals(PortletMode.EDIT, req.getPortletMode());
            }
         });
      }
   }

   protected void doRender(RenderRequest req, RenderResponse resp) throws PortletException, PortletSecurityException, IOException
   {
      if (result == null)
      {
         if (TestContext.getCurrentRequestCount() == 0)
         {
            PortletURL url = resp.createActionURL();
            url.setPortletMode(PortletMode.EDIT);
            InvokeGetResult result = new InvokeGetResult();
            result.setURL(url.toString());
            marshall(result, resp, TestContext.getCurrentTestCaseId());
         }
         else
         {
            marshall(new FailureResult("The assert result was expected to be not null"), resp, TestContext.getCurrentTestCaseId());
         }
      }
      else
      {
         if (TestContext.getCurrentRequestCount() == 0)
         {
            marshall(new FailureResult("The assert result was expected to be not null"), resp, TestContext.getCurrentTestCaseId());
         }
         else if (TestContext.getCurrentRequestCount() == 1)
         {
            marshall(result, resp, TestContext.getCurrentTestCaseId());
            result = null;

         }
      }
   }*/
}
