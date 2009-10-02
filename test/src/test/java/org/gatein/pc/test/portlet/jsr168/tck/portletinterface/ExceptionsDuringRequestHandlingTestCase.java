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
package org.gatein.pc.test.portlet.jsr168.tck.portletinterface;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;

/**
 * This case is based on:
 * - ExceptionsDuringRequestHandlingControllerPortlet
 * - PortletExceptionDuringRequestHandlingPortlet
 * - RuntimeExceptionDuringRequestHandlingPortlet
 * - UnavailableExceptionDuringProcessActionPortlet
 * - UnavailableExceptionDuringRenderPortlet
 *
 * This test is disabled. Specification doesn't defined strictly portal behaviour when
 * one of the portlets throws PortletException. Currently in JBoss Portal if one portlet
 * throws an PortletException than the rest of portlet of the page is not rendered - code 500
 * is returned. Tests are based on different behaviour where rest of portlets are rendered.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({Assertion.JSR168_17, Assertion.JSR168_18, Assertion.JSR168_19})
public class ExceptionsDuringRequestHandlingTestCase
{
   public ExceptionsDuringRequestHandlingTestCase(PortletTestCase seq)
   {
      // suite.addTest(new PortletTestCase("ExceptionsDuringRequestHandlingPortlet"));
   }
}
