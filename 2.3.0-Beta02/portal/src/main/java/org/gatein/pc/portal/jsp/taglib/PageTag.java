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

import javax.servlet.jsp.JspException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PageTag extends PortalBodyTagSupport
{

   enum Status
   {
      ACTIVE, SUSPENDED
   }

   /** . */
   Map<QName, String> params;

   /** . */
   String maximizedId;

   /** . */
   String content;

   /** . */
   Status status = Status.ACTIVE;

   public int doStartTag(PortalRenderResponse renderResponse) throws JspException
   {
      maximizedId = renderResponse.getMaximizedWindowId();

      //
      if (maximizedId != null)
      {
         return EVAL_BODY_BUFFERED;
      }
      else
      {
         return EVAL_BODY_INCLUDE;
      }
   }

   public int doEndTag(PortalRenderResponse renderResponse) throws JspException
   {
      if (maximizedId != null)
      {
         try
         {
            pageContext.getOut().write(content);
         }
         catch (IOException e)
         {
            throw new JspException(e);
         }
      }

      //
      return EVAL_PAGE;
   }

   public int doStartTag(PortalPrepareResponse portalResponse) throws JspException
   {
      return EVAL_BODY_INCLUDE;
   }

   public int doEndTag(PortalPrepareResponse portalResponse) throws JspException
   {
      return EVAL_PAGE;
   }
}
