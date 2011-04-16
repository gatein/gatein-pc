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

import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.portlet.impl.jsr168.PortletContainerImpl;
import org.gatein.common.util.MultiValuedPropertyMap;

import javax.portlet.RenderRequest;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6697 $
 */
public class RenderRequestImpl extends PortletRequestImpl implements RenderRequest
{

   /** . */
   private final String validationToken;

   public RenderRequestImpl(PortletContainerImpl container, RenderInvocation invocation)
   {
      super(container, invocation);

      //
      this.validationToken = invocation.getValidationToken();
   }

   public String getETag()
   {
      return validationToken;
   }

   protected void initProperties(MultiValuedPropertyMap<String> properties)
   {
      if (validationToken != null)
      {
         properties.setValue(ETAG, validationToken);
      }
   }
}
