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
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * The additional variable definitions for the defineObjects tag for the JSR 168 Portlet specification.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 */
public class DefineObjectsTagTEI extends TagExtraInfo
{
   public static final String portletConfigVariableName = "portletConfig";
   public static final String renderRequestVariableName = "renderRequest";
   public static final String renderResponseVariableName = "renderResponse";

   public VariableInfo[] getVariableInfo(TagData data)
   {
      VariableInfo info1 = new VariableInfo(portletConfigVariableName,
         PortletConfig.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info2 = new VariableInfo(renderRequestVariableName,
         RenderRequest.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info3 = new VariableInfo(renderResponseVariableName,
         RenderResponse.class.getName(), true, VariableInfo.AT_END);


      VariableInfo[] info = {info1, info2, info3};
      return info;
   }
}
