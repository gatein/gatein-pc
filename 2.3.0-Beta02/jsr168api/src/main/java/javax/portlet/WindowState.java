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

import java.util.Locale;

/**
 * The <CODE>WindowState</CODE> class represents the possible window states that a portlet window can assume.
 * <p/>
 * This class defines a standard set of the most basic portlet window states. Additional window states may be defined by
 * calling the constructor of this class. If a portal/portlet-container does not support a custom window state defined
 * in the portlet application deployment descriptor, the custom window state will be ignored by the portal/portlet
 * container.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6047 $
 */
public class WindowState
{

   /**
    * The <code>NORMAL</code> window state indicates that a portlet may be sharing the page with other portlets. It may
    * also indicate that the target device has limited display capabilities. Therefore, a portlet should restrict the
    * size of its rendered output in this window state.
    * <p/>
    * The string value for this state is <code>"normal"</code>.
    */
   public static final WindowState NORMAL = new WindowState("normal");

   /**
    * When a portlet is in <code>MINIMIZED</code> window state, the portlet should only render minimal output or no
    * output at all.
    * <p/>
    * The string value for this state is <code>"minimized"</code>.
    */
   public static final WindowState MINIMIZED = new WindowState("minimized");

   /**
    * The <code>MAXIMIZED</code> window state is an indication that a portlet may be the only portlet being rendered in
    * the portal page, or that the portlet has more space compared to other portlets in the portal page. A portlet may
    * generate richer content when its window state is <code>MAXIMIZED</code>.
    * <p/>
    * The string value for this state is <code>"maximized"</code>.
    */
   public static final WindowState MAXIMIZED = new WindowState("maximized");

   private String name;

   /**
    * Creates a new window state with the given name.
    * <p/>
    * Upper case letters in the name are converted to lower case letters.
    *
    * @param name The name of the portlet mode
    */
   public WindowState(String name)
   {
      if (name == null)
      {
         throw new NullPointerException();
      }
      this.name = name.toLowerCase(Locale.ENGLISH);
   }

   /**
    * Compares the specified object with this window state for equality. Returns <code>true</code> if the Strings
    * <code>equals</code> method for the String representing the two window states returns <code>true</code>.
    *
    * @param o the window state to compare this window state with.
    * @return true, if the specified object is equal with this window state.
    */
   public boolean equals(Object o)
   {
      if (o == this)
      {
         return true;
      }
      if (o instanceof WindowState)
      {
         WindowState that = (WindowState)o;
         return name.equals(that.name);
      }
      return false;
   }

   /**
    * Returns the hash code value for this window state. The hash code is constructed by producing the hash value of the
    * String value of this window state.
    *
    * @return hash code value for this window state
    */
   public int hashCode()
   {
      return name.hashCode();
   }

   /**
    * Returns a String representation of this window state. Window state names are always lower case names.
    *
    * @return String representation of this window state.
    */
   public String toString()
   {
      return name;
   }
}
