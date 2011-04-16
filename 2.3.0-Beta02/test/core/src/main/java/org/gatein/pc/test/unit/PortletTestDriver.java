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
package org.gatein.pc.test.unit;

import org.jboss.unit.remote.driver.RemoteTestDriver;
import org.jboss.unit.remote.driver.RemoteTestDriverServer;
import org.jboss.unit.remote.driver.CompositeRemoteTestDriver;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;
import org.jboss.unit.remote.RequestContext;
import org.jboss.unit.remote.ResponseContext;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.TestDriver;
import org.jboss.unit.driver.DriverContext;
import org.jboss.unit.driver.DriverCommand;
import org.jboss.unit.driver.DriverException;
import org.jboss.unit.driver.impl.composite.CompositeTestDriver;
import org.jboss.unit.info.TestCaseInfo;
import org.jboss.unit.info.TestInfo;
import org.jboss.unit.info.impl.SimpleTestCaseInfo;
import org.jboss.unit.TestId;

/**
 * Registry of action sequences. Every sequence is binded with test id
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6720 $
 */
public class PortletTestDriver extends CompositeTestDriver
{

   /** . */
   private static PortletTestContext context;

   /** . */
   private final RemoteTestDriverServer server;

   /** . */
   private String path;

   public PortletTestDriver(String name, RemoteTestDriverServer parent)
   {
      this(name, parent, "/test");
   }

   public PortletTestDriver(String name, RemoteTestDriverServer parent, String path)
   {
      super(name);

      //
      this.server = parent;
      this.path = path;
   }

   public void start()
   {
      server.setDelegate(new CompositeRemoteTestDriver(this));
   }

   public void stop()
   {
      server.setDelegate(null);
   }

   /**
    * Adding sequence to registry. Sequence can be null as this will clear the binding
    *
    * @param testCaseName
    * @param portletTestCase
    */
   public void addTestCase(String testCaseName, PortletTestCase portletTestCase)
   {
      if (testCaseName == null)
      {
         throw new IllegalArgumentException("Test name must be provided");
      }
      if (portletTestCase == null)
      {
         throw new IllegalArgumentException("Portlet test can't be null");
      }
      if (getTestCase(testCaseName) != null)
      {
         throw new IllegalArgumentException("Portlet test cannot be bound twice");
      }

      //
      try
      {
         TestCaseEntry entry = new TestCaseEntry(testCaseName, portletTestCase);

         //
         mount(entry);
      }
      catch (DriverException e)
      {
         e.printStackTrace();
      }
   }

   public void removeTestCase(String testName)
   {
      TestDriver a = drivers.get(testName);

      //
      unmount(a);
   }

   public PortletTestCase getTestCase(String testName)
   {
      if (testName == null)
      {
         throw new IllegalArgumentException("Test name can't be null");
      }
      TestCaseEntry entry = (TestCaseEntry)drivers.get(testName);

      //
      return entry != null ? entry.portletTestCase : null;
   }

   /**
    * The current portlet test context statically available.
    *
    * @return the portlet test context.
    */
   public static PortletTestContext getPortletTestContext()
   {
      return context;
   }

   private class TestCaseEntry implements RemoteTestDriver
   {

      /** . */
      private final String name;

      /** . */
      private final TestCaseInfo info;

      /** . */
      private final PortletTestCase portletTestCase;

      public TestCaseEntry(String name, PortletTestCase portletTestCase)
      {
//         TestInfo info = new TestInfo(name);
//         for (Iterator i = info.getParameterNames().iterator();i.hasNext();)
//         {
//            String parameterName = (String)i.next();
//            info.addParameter(new TestParameterInfo(parameterName));
//         }

         //
         this.portletTestCase = portletTestCase;
         this.name = name;
         this.info = new SimpleTestCaseInfo(name);
      }

      public void initDriver(DriverContext driverContext)
      {
      }

      public void destroyDriver()
      {
      }

      public void pushContext(TestId testId, RequestContext requestContext)
      {
         context = new PortletTestContext(name, getTestCase(name), requestContext);
      }

      public ResponseContext popContext(TestId testId)
      {
//         // Ensuite that everything was called
//         Set<JoinPoint> expected = portletTestCase.getJoinPoints(context.requestContext.getRequestCount());
//
//         if (!expected.equals(context.invoked))
//         {
//            Set<JoinPoint> notInvoked = new HashSet<JoinPoint>(expected);
//            notInvoked.removeAll(context.invoked);
//
//            StringBuffer msg = new StringBuffer("Those joinpoints should have been invoked [");
//            for (Iterator<JoinPoint> i = notInvoked.iterator();i.hasNext();)
//            {
//               JoinPoint joinPoint = i.next();
//               msg.append(joinPoint);
//               if (i.hasNext())
//               {
//                  msg.append(',');
//               }
//            }
//            msg.append(']');
//
//            //
//            return new ResponseContext(new FailureResponse(Failure.createErrorFailure(msg.toString())), new HashMap<String, Serializable>());
//         }

         //
         return context.responseContext;
      }

      public DriverResponse invoke(TestId testId, DriverCommand driverCommand)
      {
         return new InvokeGetResponse("/portlet-test");
      }

      public TestInfo getInfo()
      {
         return info;
      }
   }
}
