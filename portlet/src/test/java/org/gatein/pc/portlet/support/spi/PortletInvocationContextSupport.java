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
package org.gatein.pc.portlet.support.spi;

import org.gatein.common.invocation.AbstractInvocationContext;
import org.gatein.common.util.MarkupInfo;
import org.gatein.pc.api.ContainerURL;
import org.gatein.pc.api.URLFormat;
import org.gatein.pc.api.spi.PortletInvocationContext;

import java.io.Writer;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5064 $
 */
public class PortletInvocationContextSupport extends AbstractInvocationContext implements PortletInvocationContext
{

   public MarkupInfo getMarkupInfo()
   {
      throw new UnsupportedOperationException();
   }

   public String encodeResourceURL(String url) throws IllegalArgumentException
   {
      throw new UnsupportedOperationException();
   }

   public String renderURL(ContainerURL containerURL, URLFormat format)
   {
      throw new UnsupportedOperationException();
   }

   public void renderURL(Writer writer, ContainerURL containerURL, URLFormat format) throws IOException
   {
      throw new UnsupportedOperationException();
   }
}
