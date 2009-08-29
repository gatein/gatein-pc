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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.portlet.impl.jsr168.ResourceRequestParameterMap;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;
import org.gatein.common.util.MultiValuedPropertyMap;
import org.gatein.common.util.ParameterMap;

import javax.portlet.ResourceRequest;
import java.util.Map;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ResourceRequestImpl extends ClientDataRequestImpl implements ResourceRequest
{

   /** . */
   private final ResourceInvocation resourceInvocation;

   public ResourceRequestImpl(PortletContainerImpl container, ResourceInvocation invocation)
   {
      super(container, invocation);

      //
      this.resourceInvocation = invocation;
   }

   public String getResourceID()
   {
      return resourceInvocation.getResourceId();
   }

   public Map<String, String[]> getPrivateRenderParameterMap()
   {
      ParameterMap parameters = ((ResourceRequestParameterMap)requestParameterMap).getPrivateRenderParameters();

      //
      if (parameters != null)
      {
         return Collections.unmodifiableMap(parameters);
      }
      else
      {
         return Collections.emptyMap();
      }
   }

   public String getCacheability()
   {
      return ResourceURLImpl.toJSR168(resourceInvocation.getCacheLevel());
   }

   public String getETag()
   {
      return resourceInvocation.getValidationToken();
   }

   protected void initProperties(MultiValuedPropertyMap<String> properties)
   {
      if (resourceInvocation.getValidationToken() != null)
      {
         properties.setValue(ETAG, resourceInvocation.getValidationToken());
      }
   }
}
