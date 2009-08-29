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
package org.gatein.pc.portlet.aspects;

import org.gatein.pc.api.TransportGuarantee;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.SecurityInfo;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.api.invocation.response.InsufficientTransportGuaranteeResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.spi.SecurityContext;

/**
 * Implement security constaint defined by the portlet spec.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class SecureTransportInterceptor extends PortletInvokerInterceptor
{

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      PortletContainer container = (PortletContainer)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
      PortletInfo containerInfo = container.getInfo();
      SecurityInfo securityInfo = containerInfo.getSecurity();
      SecurityContext securityContext = invocation.getSecurityContext();
      boolean invoke = true;
      if (!securityContext.isSecure())
      {
         if (securityInfo.containsTransportGuarantee(TransportGuarantee.CONFIDENTIAL))
         {
            invoke = false;
         }
         else if (securityInfo.containsTransportGuarantee(TransportGuarantee.INTEGRAL))
         {
            invoke = false;
         }
      }

      //
      if (invoke)
      {
         // Invoke
         return super.invoke(invocation);
      }
      else
      {
         // Significate to the caller that this component should be executed with an higher security level
         return new InsufficientTransportGuaranteeResponse();
      }
   }
}
