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
package org.gatein.pc.portlet.impl.jsr168.taglib;

import javax.portlet.PortletResponse;
import javax.servlet.jsp.JspException;
import java.io.IOException;

/**
 * The namespace tag for the JSR 168 Portlet specification.
 * <p/>
 * This tag produces a unique value for the current portlet.
 * <p/>
 * This tag should be used for named elements in the portlet output (such as Javascript functions and variables). The
 * namespacing ensures that the given name is uniquely associated with this portlet and avoids name conflicts with other
 * elements on the portal page or with other portlets on the page.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @version $Revision: 5448 $
 * @jsp.tag name="namespace" body-content="empty"
 */
public class NamespaceTag extends PortletTag
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 4865294291730910307L;

   public int doStartTag() throws JspException
   {
      return SKIP_BODY;
   }

   public int doEndTag() throws JspException
   {
      try
      {
         PortletResponse resp = getPortletResponse();
         String namespace = resp.getNamespace();
         pageContext.getOut().print(namespace);
      }
      catch (IOException e)
      {
      }
      return EVAL_PAGE;
   }
}
