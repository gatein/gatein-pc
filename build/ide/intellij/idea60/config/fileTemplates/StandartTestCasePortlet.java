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
package ${PACKAGE_NAME};

import org.jboss.portal.test.framework.container.server.result.AssertResult;
import org.jboss.portal.test.framework.container.server.result.InvokeGetResult;
import org.jboss.portal.test.framework.container.server.result.FailureResult;
import org.jboss.portal.test.framework.container.server.result.ServerResult;
import org.jboss.portal.test.framework.container.server.TestContext;
import org.jboss.portal.test.portlet.AbstractTestPortlet;


import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ValidatorException;
import javax.portlet.PortletURL;
import java.io.IOException;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 1951 $

 */
public class ${NAME} extends AbstractTestPortlet
{


   private AssertResult ares;

   public void doProcessAction(final ActionRequest request, ActionResponse response) throws PortletException, PortletSecurityException, IOException
   {
      int requestCount = TestContext.getCurrentRequestCount();
      if (ares == null)
      {
         if (requestCount == 1)
         {
            ares = new AssertResult();
            ares.execute(new AssertResult.Test()
            {
               public void run() throws Exception
               {

               }
            });
         }
      }
      else if (!ares.isFailed())
      {
         if (requestCount == 2)
         {
            ares.execute(new AssertResult.Test()
            {
               public void run() throws Exception
               {

               }
            });
         }
      }
   }

   public void doRender(final RenderRequest request, RenderResponse response) throws PortletException, PortletSecurityException, IOException
   {
      ServerResult result = null;

      int requestCount = TestContext.getCurrentRequestCount();
      if (ares == null)
      {
         if (requestCount == 0)
         {
            PortletURL url = response.createActionURL();
            result = new InvokeGetResult(url.toString());
            AbstractTestPortlet.marshall(result, response, TestContext.getCurrentTestCaseId());
         }
         else
         {
            AbstractTestPortlet.marshall(new FailureResult("The assert result was expected to be not null"), response, TestContext.getCurrentTestCaseId());
         }
      }
      else if (ares.isFailed())
      {
         AbstractTestPortlet.marshall(ares, response, TestContext.getCurrentTestCaseId());
      }
      else
      {
         if (requestCount == 0)
         {
            AbstractTestPortlet.marshall(new FailureResult("The assert result was expected to be not null"), response, TestContext.getCurrentTestCaseId());
         }
         else if (requestCount == 1)
         {
            PortletURL url = response.createActionURL();
            result = new InvokeGetResult(url.toString());
            AbstractTestPortlet.marshall(result, response, TestContext.getCurrentTestCaseId());
         }
         else if (requestCount == 2)
         {
            AbstractTestPortlet.marshall(ares, response, TestContext.getCurrentTestCaseId());
         }
      }
   }

}
