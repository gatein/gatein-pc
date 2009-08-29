/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portal.jsp.taglib;

import org.gatein.pc.portal.jsp.PortalRenderResponse;
import org.gatein.pc.portal.jsp.PortalResponse;
import org.gatein.pc.portal.jsp.PortalPrepareResponse;
import org.gatein.common.NotYetImplemented;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortalSimpleTagSupport extends SimpleTagSupport
{

   private final boolean pageScoped;

   public PortalSimpleTagSupport()
   {
      this(true);
   }

   public PortalSimpleTagSupport(boolean pageScoped)
   {
      this.pageScoped = pageScoped;
   }

   public boolean isPageScoped()
   {
      return pageScoped;
   }

   private PortalResponse getPortalResponse()
   {
      PageContext pageContext = (PageContext)getJspContext();
      return (PortalResponse)pageContext.getResponse();
   }

   protected final boolean isActive()
   {
      if (pageScoped)
      {
         PageTag pageTag = (PageTag)findAncestorWithClass(this, PageTag.class);

         //
         return pageTag != null && pageTag.status == PageTag.Status.ACTIVE;
      }
      else
      {
         return true;
      }
   }

   public final void doTag() throws JspException, IOException
   {
      if (isActive())
      {
         PortalResponse portalResponse = getPortalResponse();

         //
         if (portalResponse instanceof PortalPrepareResponse)
         {
            doTag((PortalPrepareResponse)portalResponse);
         }
         else
         {
            doTag((PortalRenderResponse)portalResponse);
         }
      }
   }

   public void doTag(PortalPrepareResponse prepareResponse) throws JspException, IOException
   {
      throw new NotYetImplemented();
   }

   public void doTag(PortalRenderResponse renderResponse) throws JspException, IOException
   {
      throw new NotYetImplemented();
   }
}
