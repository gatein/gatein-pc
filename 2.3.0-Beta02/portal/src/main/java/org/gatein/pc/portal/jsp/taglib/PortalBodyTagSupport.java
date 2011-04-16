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
import org.gatein.pc.portal.jsp.PortalPrepareResponse;
import org.gatein.pc.portal.jsp.PortalResponse;
import org.gatein.common.NotYetImplemented;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortalBodyTagSupport extends BodyTagSupport
{

   private PortalResponse getPortalResponse()
   {
      return (PortalResponse)pageContext.getResponse();
   }

   public final int doStartTag() throws JspException
   {
      return doStartTag(getPortalResponse());
   }

   public final int doEndTag() throws JspException
   {
      return doEndTag(getPortalResponse());
   }

   public int doStartTag(PortalResponse portalResponse) throws JspException
   {
      PageTag pageTag = (PageTag)findAncestorWithClass(this, PageTag.class);

      //
      if (pageTag == null && this instanceof PageTag)
      {
         pageTag = (PageTag)this;

         //
         pageTag.status = PageTag.Status.ACTIVE;
      }

      //
      if (pageTag == null || pageTag.status != PageTag.Status.ACTIVE)
      {
         return SKIP_BODY;
      }
      else
      {
         if (portalResponse instanceof PortalPrepareResponse)
         {
            return doStartTag((PortalPrepareResponse)portalResponse);
         }
         else
         {
            return doStartTag((PortalRenderResponse)portalResponse);
         }
      }
   }

   public int doEndTag(PortalResponse portalResponse) throws JspException
   {
      PageTag pageTag = (PageTag)findAncestorWithClass(this, PageTag.class);

      //
      if (pageTag == null && this instanceof PageTag)
      {
         pageTag = (PageTag)this;
      }

      //
      if (pageTag != null && pageTag.status == PageTag.Status.ACTIVE)
      {
         if (portalResponse instanceof PortalPrepareResponse)
         {
            return doEndTag((PortalPrepareResponse)portalResponse);
         }
         else
         {
            return doEndTag((PortalRenderResponse)portalResponse);
         }
      }
      else
      {
         return EVAL_PAGE;
      }
   }

   public int doStartTag(PortalPrepareResponse prepareResponse) throws JspException
   {
      throw new NotYetImplemented();
   }

   public int doEndTag(PortalPrepareResponse prepareResponse) throws JspException
   {
      throw new NotYetImplemented();
   }

   public int doStartTag(PortalRenderResponse renderResponse) throws JspException
   {
      throw new NotYetImplemented();
   }

   public int doEndTag(PortalRenderResponse renderResponse) throws JspException
   {
      throw new NotYetImplemented();
   }
}
