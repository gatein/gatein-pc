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
package org.gatein.pc.federation;

import junit.framework.TestCase;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.federation.impl.FederatingPortletInvokerService;
import org.gatein.pc.portlet.support.PortletInvokerSupport;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class OneInvokerNoPortletsTestCase extends TestCase
{

   public void testOneFederatedWithNoPortlets() throws PortletInvokerException
   {
      FederatingPortletInvoker federating = new FederatingPortletInvokerService();
      PortletInvokerSupport support = new PortletInvokerSupport();
      federating.registerInvoker("foo", support);

      //
      assertEquals(new HashSet(), federating.getPortlets());

      //
      Collection<String> federateds = federating.getFederatedInvokerIds();
      assertNotNull(federateds);
      assertEquals(1, federateds.size());

      String id = federateds.iterator().next();
      assertEquals("foo", id);
      assertEquals(support, federating.getFederatedInvoker(id).getPortletInvoker());
   }
}
