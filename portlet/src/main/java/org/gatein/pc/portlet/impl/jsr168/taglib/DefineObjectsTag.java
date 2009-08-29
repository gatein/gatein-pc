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

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.jsp.JspException;

/**
 * The defineObjects tag for the JSR 168 Portlet specification.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 * @jsp.tag name="defineObjects" body-content="empty" tei-class="org.gatein.pc.portlet.impl.jsr168.taglib.DefineObjectsTagTEI"
 */
public class DefineObjectsTag extends PortletTag
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -8640865649772583292L;

   public int doStartTag() throws JspException
   {
      return SKIP_BODY;
   }

   public int doEndTag() throws JspException
   {

      RenderRequest rreq = (RenderRequest)getPortletRequest();
      RenderResponse rresp = (RenderResponse)getPortletResponse();
      PortletConfig portletConfig = getConfig();
      pageContext.setAttribute(DefineObjectsTagTEI.renderRequestVariableName, rreq);
      pageContext.setAttribute(DefineObjectsTagTEI.renderResponseVariableName, rresp);
      pageContext.setAttribute(DefineObjectsTagTEI.portletConfigVariableName, portletConfig);
      
      return EVAL_PAGE;
   }
}
