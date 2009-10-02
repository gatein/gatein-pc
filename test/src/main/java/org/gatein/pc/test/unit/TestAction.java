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

import javax.portlet.PortletRequest;
import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.jboss.unit.api.Assert.*;
import org.gatein.common.util.Tools;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 7018 $
 */
public abstract class TestAction
{

   /** . */
   private Logger log = null;

   protected final Logger getLogger()
   {
      if (log == null)
      {
         log = Logger.getLogger(getClass());
      }
      return log;
   }

   /**
    * Check that the parameter map contains the value expected. The check is done with the different
    * ways that the <code>PortletRequest</code> API provides:
    *
    * <ul>
    * <li><code>PortletRequest.getParameter(String)</li>
    * <li><code>PortletRequest.getParameterValues(String)</li>
    * <li><code>PortletRequest.getParameterNames()</li>
    * <li><code>PortletRequest.getParameterMap()</li>
    * </ul>
    *
    * This method allows a full coverage of the API.
    *
    * @param expectedMap the expected map
    * @param request the actual portlet request
    * @throws AssertionError if the provided request does not match the expected map state
    */
   protected final void assertParameterMap(Map<String, String[]> expectedMap, PortletRequest request) throws AssertionError
   {
      assertNotNull(request);

      // Test getParameter(String name)
      for (Map.Entry<String, String[]> entry : expectedMap.entrySet())
      {
         String expectedValue = entry.getValue()[0];
         String actualValue = request.getParameter(entry.getKey());
         assertEquals("Was expecting value " + expectedValue + " for key " + entry.getKey() + " but instead have " +
            actualValue, expectedValue, actualValue);
      }

      // Test getParameterValues(String name)
      for (Map.Entry<String, String[]> entry : expectedMap.entrySet())
      {
         String[] expectedValues = entry.getValue();
         assertEquals(expectedValues, request.getParameterValues(entry.getKey()));
      }

      // Test parameter names, we use list in order to catch eventually a wrong Enumeration returned by the request
      List<String> names = Tools.toList(request.getParameterNames());
      List<String> expectedNames = new ArrayList<String>(expectedMap.keySet());
      Collections.sort(names);
      Collections.sort(expectedNames);
      assertEquals(expectedNames, names);

      //
      Map<String, String[]> map = request.getParameterMap();
      assertParameterMap(expectedMap, map);
   }

   /**
    * Check that the two parameter maps are equals.
    *
    * @param expectedMap the expected map
    * @param map the actual map
    * @throws AssertionError if the provided map is not equals to the expected map
    */
   protected final void assertParameterMap(Map<String, String[]> expectedMap, Map<String, String[]> map) throws AssertionError
   {
      assertNotNull(map);
      assertEquals(expectedMap.keySet(), map.keySet());
//      assertEquals(map.keySet(), expectedMap.keySet());
      for (Map.Entry<String, String[]> entry : expectedMap.entrySet())
      {
         String[] expectedValues = map.get(entry.getKey());
         assertEquals(entry.getValue(), expectedValues);
      }
   }

   /**
    * Build a cookie map from the request.
    *
    * @param request the request
    * @return the cookie map
    */
   protected final  Map<String, String> createCookieMap(PortletRequest request)
   {
      Map<String, String> cookieMap = new HashMap<String, String>();
      Cookie[] cookies = request.getCookies();
      if (cookies != null)
      {
         for (Cookie cookie : cookies)
         {
            cookieMap.put(cookie.getName(), cookie.getValue());
         }
      }
      return cookieMap;
   }
}
