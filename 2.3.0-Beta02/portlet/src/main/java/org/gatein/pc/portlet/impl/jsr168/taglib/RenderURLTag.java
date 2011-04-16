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

import javax.portlet.PortletURL;
import javax.portlet.BaseURL;

/**
 * The renderURL tag for the JSR 168 Portlet specification.
 * <p/>
 * Creates a URL that must point to the current portlet and must trigger an render request with the supplied
 * parameters.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @version $Revision: 5448 $
 * @jsp.tag name="renderURL" body-content="JSP" tei-class="org.gatein.pc.portlet.impl.jsr168.taglib.GenerateURLTagTEI"
 */
public class RenderURLTag extends GenerateURLTag
{
   /** The serialVersionUID */
   private static final long serialVersionUID = -4138261559430217557L;
   public static String typeParameter = "render";

   /* (non-Javadoc)
    * @see org.gatein.pc.portlet.taglib.GenerateURLTag#addTypeParameter()
    */
   protected String getTypeValue()
   {
      return typeParameter;
   }
   
   protected BaseURL generateURL() throws Exception
   {
      PortletURL newPortletURL =  (PortletURL)super.generateURL();

      setWindowState(newPortletURL);

      setPortletMode(newPortletURL);

      return newPortletURL;
   }
}
