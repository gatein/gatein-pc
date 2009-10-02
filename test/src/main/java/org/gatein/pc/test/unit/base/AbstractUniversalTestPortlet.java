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
package org.gatein.pc.test.unit.base;

import org.apache.log4j.Logger;
import org.gatein.pc.test.unit.JoinPoint;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.unit.actions.PortletResourceTestAction;
import org.gatein.pc.test.unit.actions.PortletTestAction;
import org.gatein.pc.test.unit.TestAction;
import org.gatein.pc.test.unit.PortletTestContext;
import org.jboss.portal.test.framework.server.NodeId;
import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.PortletTestDriver;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.Failure;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;

/**
 * Universal test portlet that enables to reuse portlet instances across several tests
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 */
public abstract class AbstractUniversalTestPortlet implements Portlet, EventPortlet, ResourceServingPortlet
{

   public static String getPortletName()
   {
      StackTraceElement elt = new Throwable().getStackTrace()[1];
      String className = elt.getClassName();
      int index = className.indexOf('.');
      if (index != -1)
      {
         className = className.substring(index + 1);
      }
      return className;
   }

   /**
    * The runtime status of a portlet.
    */
   public static enum RuntimeStatus
   {
      INSTANTIATED,
      STARTED,
      STOPPED
   }

   /** . */
   private final Logger log = createLogger();

   /** Joinpoint for render phase. */
   private final JoinPoint renderJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_RENDER);

   /** Joinpoint for event phase. */
   private final JoinPoint eventJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_EVENT);

   /** Joinpoint for action phase. */
   private final JoinPoint actionJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_ACTION);

   /** Joinpoint for resource serving. */
   private final JoinPoint resourceJoinPoint = JoinPoint.createJoinPoint(getClass(), JoinPointType.PORTLET_RESOURCE);

   /** To keep PortletConfig for some tests*-. */
   private PortletConfig config;

   /** Indicate the status of the portlet. */
   private RuntimeStatus runtimeStatus;

   /** Construct a test portlet with a test id computed with the method <code>getComponentId()</code>. */
   public AbstractUniversalTestPortlet()
   {
      runtimeStatus = RuntimeStatus.INSTANTIATED;
   }

   public void init(PortletConfig config) throws PortletException
   {
      this.config = config;
      this.runtimeStatus = RuntimeStatus.STARTED;
   }

   /**
    * Returns the PortletConfig object of this portlet.
    *
    * @return the PortletConfig object of this portlet
    */
   public PortletConfig getPortletConfig()
   {
      return config;
   }

   /**
    * Returns the <code>PortletContext</code> of the portlet application the portlet is in.
    *
    * @return the portlet application context
    */
   public PortletContext getPortletContext()
   {
      return config.getPortletContext();
   }

   /**
    * invokes current action from Sequence
    *
    * @param req
    * @param resp
    * @throws PortletException
    * @throws PortletSecurityException
    * @throws IOException
    */
   public final void processAction(ActionRequest req, ActionResponse resp) throws PortletException, IOException
   {
      PortletTestContext ctx = PortletTestDriver.getPortletTestContext();

      //
      log.debug("JoinPoint '" + actionJoinPoint + "' invoked for '"
         + ctx.getTestName() + "' testId and '" +
         +ctx.getRequestCount() + "' request count");

      //
      PortletTestCase portletTestCase = getSequence(ctx.getTestName());

      //
      if (portletTestCase != null)
      {
         //
         TestAction action = portletTestCase.getAction(ctx.getRequestCount(), NodeId.locate(), actionJoinPoint);

         //
         DriverResponse response;
         if (action instanceof PortletTestAction)
         {
            log.debug("Found action for jointpoint " + actionJoinPoint + " : " + action);

            //
            response = ((PortletTestAction)action).execute(this, req, resp, ctx);
         }
         else if (action instanceof PortletActionTestAction)
         {
            log.debug("Found action for jointpoint " + actionJoinPoint + " : " + action);

            //
            response = ((PortletActionTestAction)action).execute(this, req, resp, ctx);
         }
         else
         {
            response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
               " " + NodeId.locate() + " " + actionJoinPoint + " does not have the right type but has the type " +
               action.getClass().getName()));
         }

         //
         if (response != null)
         {
            ctx.updateResponse(response);
         }
      }
   }

   public void processEvent(EventRequest req, EventResponse resp) throws PortletException, IOException
   {
      PortletTestContext ctx = PortletTestDriver.getPortletTestContext();

      //
      if (ctx.getResponse() == null)
      {
         log.debug("JoinPoint '" + eventJoinPoint + "' invoked for '"
            + ctx.getTestName() + "' testId and '" +
            +ctx.getRequestCount() + "' request count");

         //
         PortletTestCase portletTestCase = getSequence(ctx.getTestName());

         //
         if (portletTestCase != null)
         {
            //
            TestAction action = portletTestCase.getAction(ctx.getRequestCount(), NodeId.locate(), eventJoinPoint);

            //
            if (action != null)
            {
               DriverResponse response = null;
               if (action instanceof PortletTestAction)
               {
                  log.debug("Found action for jointpoint " + eventJoinPoint + " : " + action);

                  //
                  response = ((PortletTestAction)action).execute(this, req, resp, ctx);
               }
               else if (action instanceof PortletEventTestAction)
               {
                  log.debug("Found action for jointpoint " + eventJoinPoint + " : " + action);

                  //
                  response = ((PortletEventTestAction)action).execute(this, req, resp, ctx);
               }
               else if (action == null)
               {
                  response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
                     " " + NodeId.locate() + " " + actionJoinPoint + " is null"));
               }
               else
               {
                  response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
                     " " + NodeId.locate() + " " + actionJoinPoint + " does not have the right type but has the type " +
                     action.getClass().getName()));
               }

               //
               if (response != null)
               {
                  ctx.updateResponse(response);
               }
            }
         }
      }
   }

   /**
    * Invokes current action from Sequence. If failed AssertResult was returned in previouse portlet action phase it
    * will be marshalled.
    *
    * @param req
    * @param resp
    * @throws PortletException
    * @throws PortletSecurityException
    * @throws IOException
    */
   public final void render(RenderRequest req, RenderResponse resp) throws PortletException, IOException
   {
      PortletTestContext ctx = PortletTestDriver.getPortletTestContext();

      //
      log.debug("JoinPoint '" + renderJoinPoint +  " invoked for '"
         + ctx.getTestName() + "' testId and '" +
         +ctx.getRequestCount() + "' request count");

      // Reset AssertResult at begining of test
      if (ctx.getRequestCount() == 0)
      {
         reset();
      }

      // Get the action
      TestAction action = null;
      PortletTestCase portletTestCase = getSequence(ctx.getTestName());
      if (portletTestCase != null)
      {
         action = portletTestCase.getAction(ctx.getRequestCount(), NodeId.locate(), renderJoinPoint);
      }

      //
      if (action != null)
      {
         // Get the result
         DriverResponse response;
         if (action instanceof PortletTestAction)
         {
            log.debug("Found action for joinpoint " + renderJoinPoint + " : " + action);

            //
            response = ((PortletTestAction)action).execute(this, req, resp, ctx);
         }
         else if (action instanceof PortletRenderTestAction)
         {
            log.debug("Found action for joinpoint " + renderJoinPoint + " : " + action);

            //
            response = ((PortletRenderTestAction)action).execute(this, req, resp, ctx);
         }
         else if (action == null)
         {
            response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
            " " + NodeId.locate() + " " + resourceJoinPoint + " is null"));
         }
         else
         {
            response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
            " " + NodeId.locate() + " " + resourceJoinPoint + " does not have the right type but has the type " +
            action.getClass().getName()));
         }

         // If we have one result it is meant to be returned to the client
         if (response != null)
         {
            ctx.updateResponse(response);
         }
      }
      else
      {
         // As we are not involved we don't want content to be cached that would prevent
         // the test to run when it should be invoked
         resp.setProperty(RenderResponse.EXPIRATION_CACHE, "0");
      }
   }

   public void serveResource(ResourceRequest req, ResourceResponse resp) throws PortletException, IOException
   {
      PortletTestContext ctx = PortletTestDriver.getPortletTestContext();

      //
      log.debug("JoinPoint '" + resourceJoinPoint +  " invoked for '"
         + ctx.getTestName() + "' testId and '" +
         +ctx.getRequestCount() + "' request count");

      // Get the action
      TestAction action = null;
      PortletTestCase portletTestCase = getSequence(ctx.getTestName());
      if (portletTestCase != null)
      {
         action = portletTestCase.getAction(ctx.getRequestCount(), NodeId.locate(), resourceJoinPoint);

      }

      //
      DriverResponse response;
      if (action instanceof PortletTestAction)
      {
         log.debug("Found action for joinpoint " + resourceJoinPoint + " : " + action);

         // Get the result that must exist since it is the only joinpoint invoked during this request
         response = ((PortletTestAction)action).execute(this, req, resp, ctx);
      }
      else if (action instanceof PortletResourceTestAction)
      {
         log.debug("Found action for joinpoint " + resourceJoinPoint + " : " + action);

         // Get the result that must exist since it is the only joinpoint invoked during this request
         response = ((PortletResourceTestAction)action).execute(this, req, resp, ctx);
      }
      else if (action == null)
      {
         response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
            " " + NodeId.locate() + " " + resourceJoinPoint + " is null"));
      }
      else
      {
         response = new FailureResponse(Failure.createAssertionFailure("Action for " + ctx.getRequestCount() +
            " " + NodeId.locate() + " " + resourceJoinPoint + " does not have the right type but has the type " +
            action.getClass().getName()));
      }

      //
      if (response != null)
      {
         ctx.updateResponse(response);
      }
   }

   public void destroy()
   {
      this.runtimeStatus = RuntimeStatus.STOPPED;
   }

   public RuntimeStatus getRuntimeStatus()
   {
      return runtimeStatus;
   }

   /**
    * Returns SequenceRegistry which should be injected into ServletContext Returns null if there is no SequenceRegistry
    * in context
    *
    * @return SequenceRegistry
    */
   private PortletTestDriver getSequenceRegistry()
   {
      return (PortletTestDriver)getPortletContext().getAttribute("SequenceRegistry");
   }

   /**
    * Returns Sequence for current testId Returns null if there is no Sequence for current testId
    *
    * @return
    * @throws PortletException if there is no SequenceRegistry
    */
   private PortletTestCase getSequence(String testId) throws PortletException
   {
      PortletTestDriver registry = getSequenceRegistry();
      if (registry == null)
      {
         log.error("No SequenceRegistry object found in current context");
         throw new PortletException("No SequenceRegistry object found in context");
      }
      return registry.getTestCase(testId);
   }

   public JoinPoint getActionJointpoint()
   {
      return actionJoinPoint;
   }

   public JoinPoint getRenderJointpoint()
   {
      return renderJoinPoint;
   }

   protected void reset()
   {
   }

   /** Can be subclassed to provide an alternative way to create the logger. */
   protected Logger createLogger()
   {
      if (log != null)
      {
         throw new IllegalStateException("The logger should not be re");
      }
      return Logger.getLogger(getClass());
   }

   /** Return the logger. */
   public final Logger getLogger()
   {
      return log;
   }
}

