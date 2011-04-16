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

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * The additional variable definitions for the actionURL tag for the JSR 168 Portlet specification.
 *
 * @author <a href="mailto:sgwood@ix.netcom.com">Sherman Wood</a>
 * @version $Revision: 5448 $
 */
public class GenerateURLTagTEI extends TagExtraInfo
{

   public boolean isValid(TagData data)
   {
      return isWindowStateValid(data) &&
         isPortletModeValid(data) &&
         isSecureValid(data);
   }

   public boolean isWindowStateValid(TagData data)
   {
      return true;
   }

   public boolean isPortletModeValid(TagData data)
   {
      return true;
   }

   public boolean isSecureValid(TagData data)
   {
      Object o = data.getAttribute("secure");
      if (o != null && o != TagData.REQUEST_TIME_VALUE)
      {
         String s = (String)o;
         if (s.toLowerCase().equals("true") ||
            s.toLowerCase().equals("false"))
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return true;
      }
   }

   public VariableInfo[] getVariableInfo(TagData data)
   {
      String varName = data.getAttributeString("var");
      if (varName == null)
      {
         return null;
      }

      VariableInfo info1
         = new VariableInfo(varName,
         "String",
         true,
         VariableInfo.AT_END);
      VariableInfo[] info = {info1};
      return info;
   }
}
