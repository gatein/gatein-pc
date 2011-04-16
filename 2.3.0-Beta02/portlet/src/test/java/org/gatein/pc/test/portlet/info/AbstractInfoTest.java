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
package org.gatein.pc.test.portlet.info;

import org.gatein.pc.portlet.container.managed.PortletApplicationRegistry;
import org.jboss.unit.info.TestInfo;
import org.jboss.unit.info.impl.SimpleTestSuiteInfo;
import org.jboss.unit.remote.driver.RemoteTestDriver;
import org.jboss.unit.remote.RequestContext;
import org.jboss.unit.remote.ResponseContext;
import org.jboss.unit.driver.impl.composite.CompositeTestDriver;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.DriverCommand;
import org.jboss.unit.driver.DriverContext;
import org.jboss.unit.driver.DriverException;
import org.jboss.unit.driver.response.EndTestResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.driver.command.StartTestCommand;
import org.jboss.unit.Failure;
import org.jboss.unit.TestId;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractInfoTest implements RemoteTestDriver
{

   /** The test id. */
   protected final String testCaseId;

   /** The test info. */
   protected final TestInfo testInfo;

   /** . */
   protected PortletApplicationRegistry registry;

   /** The controller. */
   protected CompositeTestDriver testDriverContainer;

   /** Not really used for now, we need the concept of non http test context. */
   private RequestContext testContext;

   public AbstractInfoTest(String testCaseId)
   {
      if (testCaseId == null)
      {
         throw new IllegalArgumentException("No null test case id accepted");
      }

      //
      this.testCaseId = testCaseId;
      this.testInfo = new SimpleTestSuiteInfo(testCaseId);
   }

   public void pushContext(TestId testId, RequestContext requestContext)
   {
      this.testContext = requestContext;
   }

   public ResponseContext popContext(TestId testId)
   {
      return null;
   }

   public PortletApplicationRegistry getRegistry()
   {
      return registry;
   }

   public void setRegistry(PortletApplicationRegistry registry)
   {
      this.registry = registry;
   }

   public CompositeTestDriver getTestDriverRegistry()
   {
      return testDriverContainer;
   }


   public void initDriver(DriverContext driverContext) throws DriverException
   {
   }

   public void destroyDriver()
   {
   }

   public void setTestDriverRegistry(CompositeTestDriver testDriverContainer)
   {
      this.testDriverContainer = testDriverContainer;
   }

   public void create() throws Exception
   {
      testDriverContainer.mount(this);
   }

   public void destroy()
   {
      testDriverContainer.unmount(this);
   }

   public TestInfo getInfo()
   {
      return testInfo;
   }

   public DriverResponse invoke(TestId testId, DriverCommand cmd)
   {
      if (cmd instanceof StartTestCommand)
      {
         try
         {
            execute();

            //
            return new EndTestResponse();
         }
         catch (Exception e)
         {
            return new FailureResponse(Failure.createFailure(e));
         }
      }
      else
      {
         return new FailureResponse(Failure.createAssertionFailure("Unexpected command"));
      }
   }

   protected abstract void execute();
   
}
