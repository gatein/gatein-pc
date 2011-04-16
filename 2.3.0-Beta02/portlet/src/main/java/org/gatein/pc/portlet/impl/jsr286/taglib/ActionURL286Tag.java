/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.portlet.impl.jsr286.taglib;

import javax.portlet.PortletURL;
import javax.portlet.BaseURL;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class ActionURL286Tag extends GenerateURL286Tag
{
   /** The serialVersionUID */
   private static final long serialVersionUID = 7920291581022525492L;
   public static String typeParameter = "action";

   private String name;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   protected void setName(PortletURL portletURL)
   {
      if (name != null)
      {
         portletURL.setParameter("javax.portlet.action", name);
      }
   }

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

      setCopyCurrentRenderParameters(newPortletURL);

      removeTagParametersWithEmptyValue(newPortletURL);

      setName(newPortletURL);

      return newPortletURL;
   }
}
