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

import org.gatein.pc.api.TransportGuarantee;
import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.SecurityInfo;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6720 $
 */
public class SecurityInfoTest extends AbstractInfoTest
{

   public SecurityInfoTest()
   {
      super("SecurityInfoTest");
   }

   public void execute()
   {
      //This asserts only .isRemotable() as rest is checked in other tests

      ManagedPortletContainer container = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("SecuredPortlet1");
      PortletInfo info = container.getInfo();
      SecurityInfo secInfo = info.getSecurity();

      secInfo = info.getSecurity();
      assertEquals(2, secInfo.getTransportGuarantees().size());
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.CONFIDENTIAL));
      //assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.INTEGRAL));
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.NONE));


      container = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("SecuredPortlet2");
      info = container.getInfo();

      secInfo = info.getSecurity();
      assertEquals(3, secInfo.getTransportGuarantees().size());
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.INTEGRAL));
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.CONFIDENTIAL));
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.NONE));


      container = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("SecuredPortlet3");
      info = container.getInfo();
      secInfo = info.getSecurity();
      assertEquals(2, secInfo.getTransportGuarantees().size());
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.INTEGRAL));
      assertTrue(secInfo.containsTransportGuarantee(TransportGuarantee.NONE));
   }
}
