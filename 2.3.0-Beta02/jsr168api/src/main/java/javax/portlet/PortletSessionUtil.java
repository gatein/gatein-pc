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
package javax.portlet;

/**
 * The <CODE>PortletSessionUtil</CODE>  class helps identify and decode attributes in the <CODE>PORTLET_SCOPE</CODE>
 * scope of the PortletSession when accessed through the HttpSession an from within calls to methods of the
 * HttpSessionBindingListener interface.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public class PortletSessionUtil
{

   /**
    * Returns the attribute name of an attribute in the <code>PORTLET_SCOPE</code>. If the attribute is in the
    * <code>APPLICATION_SCOPE</code> it returns the attribute name unchanged.
    *
    * @param name a string specifying the name of the encoded portlet attribute
    * @return the decoded attribute name
    */
   public static String decodeAttributeName(String name)
   {
      int position = name.indexOf('?');
      if (position != -1 && name.startsWith("javax.portlet.p."))
      {
         return name.substring(position + 1);
      }
      return name;
   }

   /**
    * Returns the portlet attribute scope from an encoded portlet attribute. <br>Possible return values are: <ul>
    * <li><code>PortletSession.APPLICATION_SCOPE</code></li> <li><code>PortletSession.PORTLET_SCOPE</code></li> </ul>
    *
    * @param name a string specifying the name of the encoded portlet attribute
    * @return the decoded attribute scope
    * @see PortletSession
    */
   public static int decodeScope(String name)
   {
      int position = name.indexOf('?');
      if (position != -1 && name.startsWith("javax.portlet.p."))
      {
         return PortletSession.PORTLET_SCOPE;
      }
      return PortletSession.APPLICATION_SCOPE;
   }
}
