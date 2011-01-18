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
package org.gatein.pc.test.unit.actions;

import org.gatein.pc.test.unit.TestAction;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.common.NotYetImplemented;
import org.gatein.common.io.IOTools;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.Failure;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 */
public abstract class PortletResourceTestAction extends TestAction
{

   /**
    * This field is used to determine if the action will attempt to send a response after the action has been executed.
    * The attempt to send a response will be done if:
    * <ul>
    * <li>this field has a value equals to true</li>
    * <li>no content type has been set on the response during the execution of the action</li>
    * <li>the execution of the action is succesfull or is a failure (i.e it will not be done on Errors other than
    * <code>java.lang.AssertionError</code></li>
    * </ul>
    */
   private final boolean attemptToSendResponse;

   protected PortletResourceTestAction(boolean attemptToSendResponse)
   {
      this.attemptToSendResponse = attemptToSendResponse;
   }

   protected PortletResourceTestAction()
   {
      this(true);
   }

   public final DriverResponse execute(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
   {
      // We attempty to do it
      boolean sendResponse = false;

      //
      try
      {
         DriverResponse driverResponse = runWithRuntimeException(portlet, request, response, context);
         sendResponse = attemptToSendResponse;
         return driverResponse;
      }
      catch (AssertionError t)
      {
         getLogger().error("The test case failed", t);

         // We will send a response
         sendResponse = attemptToSendResponse;

         //
         return new FailureResponse(Failure.createFailure(t));
      }
      finally
      {
         if (sendResponse)
         {
            if (response.getContentType() == null)
            {
               response.setContentType("text/html");
            }

            //
            boolean gotWriter = false;
            try
            {
               Writer writer = response.getWriter();
               gotWriter = true;
               IOTools.safeClose(writer);
            }
            catch (IllegalStateException ignore)
            {
            }
            if (!gotWriter)
            {
               try
               {
                  OutputStream out = response.getPortletOutputStream();
                  IOTools.safeClose(out);
               }
               catch (IllegalStateException ignore)
               {
               }
               catch (IOException ignore)
               {
               }
            }
         }
      }
   }

   protected DriverResponse runWithRuntimeException(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
   {
      try
      {
         return run(portlet, request, response, context);
      }
      catch (Exception e)
      {
         if (e instanceof PortletException)
         {
            throw (PortletException)e;
         }
         if (e instanceof IOException)
         {
            throw (IOException)e;
         }
         throw new AssertionError(e);
      }
   }

   protected DriverResponse run(Portlet portlet, ResourceRequest request, ResourceResponse response, PortletTestContext context) throws PortletException, IOException
   {
      throw new NotYetImplemented();
   }
}