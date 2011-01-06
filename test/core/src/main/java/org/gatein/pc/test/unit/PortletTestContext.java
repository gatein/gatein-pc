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

import org.jboss.portal.test.framework.server.NodeId;
import org.gatein.common.NotYetImplemented;
import org.gatein.pc.test.unit.JoinPointType;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.FailureResponse;
import org.jboss.unit.remote.RequestContext;
import org.jboss.unit.remote.ResponseContext;
import org.jboss.unit.remote.http.HttpHeaders;

import java.net.MalformedURLException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class PortletTestContext
{

   /** . */
   final String testName;

   /** . */
   final PortletTestCase portletTestCase;

   /** . */
   final Set<JoinPoint> invoked;

   /** . */
   RequestContext requestContext;

   /** . */
   ResponseContext responseContext;

   public PortletTestContext(String testName, PortletTestCase portletTestCase, RequestContext requestContext)
   {
      if (requestContext == null)
      {
         throw new IllegalArgumentException("No request context to wrap");
      }
      this.testName = testName;
      this.portletTestCase = portletTestCase;
      this.requestContext = requestContext;
      this.invoked = new HashSet<JoinPoint>();
   }

   public String getTestName()
   {
      return testName;
   }

   private static class JoinPointInvocation
   {

      /** . */
      private final int requestCount;

      /** . */
      private final JoinPoint joinPoint;

      private JoinPointInvocation(int requestCount, JoinPoint joinPoint)
      {
         this.requestCount = requestCount;
         this.joinPoint = joinPoint;
      }

      public boolean equals(Object obj)
      {
         if (obj == this)
         {
            return true;
         }
         if (obj instanceof JoinPointInvocation)
         {
            JoinPointInvocation that = (JoinPointInvocation)obj;
            return requestCount == that.requestCount && joinPoint.equals(that.joinPoint);
         }
         return false;
      }

      public int hashCode()
      {
         return requestCount + joinPoint.hashCode();
      }
   }

   public void setInvoked(JoinPoint joinPoint)
   {
//      invocations.add(new JoinPointInvocation(requestCount, joinPoint));
      invoked.add(joinPoint);
   }

   public String getActorId(JoinPointType joinPointType)
   {
      return portletTestCase.getActorId(requestContext.getRequestCount(), joinPointType);
   }

   // We don't expose it as it is can be used in a wrong manner, rather use update response method
   private void setResponse(DriverResponse response)
   {
      responseContext = new ResponseContext(response, new HashMap<String, Serializable>());
   }

   public DriverResponse getResponse()
   {
      return responseContext != null ? responseContext.getResponse() : null;
   }

   /**
    * Update the context response with the provided response. The update will occur
    * if there is no previous existing response. If there is an existing response
    * this one will be overwrited only if it is not a failure and the provided
    * response is a failure.
    *
    * @param response the new response
    */
   public void updateResponse(DriverResponse response)
   {
      if (response == null)
      {
         throw new IllegalArgumentException();
      }

      DriverResponse existingResponse = getResponse();

      //
      if (existingResponse instanceof FailureResponse)
      {
         // We keep the existing failure, since we want it reported
      }
      else if (response instanceof FailureResponse || existingResponse == null)
      {
         // We have a failure response and the context contains no response or a non failure response
         setResponse(response);
      }
   }

   public int getRequestCount()
   {
      return requestContext.getRequestCount();
   }

   public String rewriteURLForNode(String url, NodeId nodeId) throws MalformedURLException
   {
//      return testContext.rewriteURLForNode(url, nodeId);
      throw new NotYetImplemented("todo");
   }

   public String getParameter(String parameterName)
   {
      if (parameterName == null)
      {
         throw new IllegalArgumentException();
      }
      return requestContext.getParametrization().get(parameterName);
   }

   public Map<String, Serializable> getPayload()
   {
      return requestContext.getPayload();
   }

   public byte[] getResponseBody()
   {
      return (byte[])getPayload().get("http.response.body");
   }

   public HttpHeaders getResponseHeaders()
   {
      return (HttpHeaders)getPayload().get("http.response.headers");
   }
}
