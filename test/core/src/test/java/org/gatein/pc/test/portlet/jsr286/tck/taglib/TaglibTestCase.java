/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.test.portlet.jsr286.tck.taglib;

import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.aspects.ContextDispatcherInterceptor;
import static org.jboss.unit.api.Assert.assertEquals;

import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.portlet.RenderRequest;
import javax.portlet.PortletException;
import java.io.Writer;
import java.io.IOException;


/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public abstract class TaglibTestCase
{

   protected String startTag;
   protected String endTag;
   protected String expectedResult;
   protected String[] expectedResults;


   protected void include(PortletRequestDispatcher dispatcher, RenderRequest request, RenderResponse response) throws IOException, PortletException
   {
      startTag = "<div id=" + response.getNamespace() + ">";
      endTag = "</div>";

      Writer writer = response.getWriter();

      writer.write(startTag);
      dispatcher.include(request, response);
      writer.write(endTag);
   }

   protected void assertResult(PortletTestContext context)
   {
      String prevResponse = new String(context.getResponseBody());

      String portletResp = prevResponse.substring(prevResponse.indexOf(startTag) + startTag.length(), prevResponse.indexOf(endTag));

      assertEquals(expectedResult, portletResp.trim());
   }

   protected void assertResults(PortletTestContext context)
   {
      String prevResponse = new String(context.getResponseBody());

      String portletResp = prevResponse.substring(prevResponse.indexOf(startTag) + startTag.length(), prevResponse.indexOf(endTag));

      String[] results = portletResp.split("<test_result_separator/>");

      // Trim all results
      for (int i = 0; i < results.length; i++)
      {
         results[i] = results[i].trim();

      }


      assertEquals(expectedResults, results);
   }

   protected PortletInvocation getInvocation(PortletRequest req)
   {
      // Get the invocation
      return (PortletInvocation)req.getAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);
   }
}
