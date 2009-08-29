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
 * The <CODE>PortletMode</CODE> class represents the possible modes that a portlet can assume.
 * <p/>
 * A portlet mode indicates the function a portlet is performing. Normally, portlets perform different tasks and create
 * different content depending on the function they are currently performing. When invoking a portlet, the portlet
 * container provides the current portlet mode to the portlet.
 * <p/>
 * Portlets can programmatically change their portlet mode when processing an action request.
 * <p/>
 * This class defines the default portlet modes <code>EDIT, HELP, VIEW</code>. Additional portlet modes may be defined
 * by calling the constructor of this class. If a portal/portlet-container does not support a custom portlet mode
 * defined in the portlet application deployment descriptor, the custom portlet mode will be ignored by the
 * portal/portlet container.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6047 $
 */
public class PortletMode
{

   /**
    * Within the <code>EDIT</code> portlet mode, a portlet should provide content and logic that lets a user customize
    * the behavior of the portlet. The EDIT portlet mode may include one or more screens among which users can navigate
    * to enter their customization data.
    * <p/>
    * Typically, portlets in <code>EDIT</code> portlet mode will set or update portlet preferences.
    * <p/>
    * This mode is optional.
    * <p/>
    * The string value for this mode is <code>"edit"</code>.
    */
   public static final PortletMode EDIT = new PortletMode("edit");

   /**
    * When in <code>HELP</code> portlet mode, a portlet should provide help information about the portlet. This help
    * information could be a simple help screen explaining the entire portlet in coherent text or it could be
    * context-sensitive help.
    * <p/>
    * This mode is optional.
    * <p/>
    * The string value for this mode is <code>"help"</code>.
    */
   public static final PortletMode HELP = new PortletMode("help");

   /**
    * The expected functionality for a portlet in <code>VIEW</code> portlet mode is to generate markup reflecting the
    * current state of the portlet. For example, the <code>VIEW</code> portlet mode of a portlet may include one or more
    * screens that the user can navigate and interact with, or it may consist of static content that does not require
    * any user interaction.
    * <p/>
    * This mode must be supported by the portlet.
    * <p/>
    * The string value for this mode is <code>"view"</code>.
    */
   public static final PortletMode VIEW = new PortletMode("view");

   private String name;

   /**
    * Creates a new portlet mode with the given name.
    * <p/>
    * Upper case letters in the name are converted to lower case letters.
    *
    * @param name The name of the portlet mode
    */
   public PortletMode(String name)
   {
      if (name == null)
      {
         throw new NullPointerException();
      }
      this.name = name.toLowerCase(Locale.ENGLISH);
   }

   /**
    * Compares the specified object with this portlet mode for equality. Returns <code>true</code> if the Strings
    * <code>equals</code> method for the String representing the two portlet modes returns <code>true</code>.
    *
    * @param o portlet mode to compare this portlet mode with
    * @return true, if the specified object is equal with this portlet mode
    */
   public boolean equals(Object o)
   {
      if (o == this)
      {
         return true;
      }
      if (o instanceof PortletMode)
      {
         PortletMode that = (PortletMode)o;
         return name.equals(that.name);
      }
      return false;
   }

   /**
    * Returns the hash code value for this portlet mode. The hash code is constructed by producing the hash value of the
    * String value of this mode.
    *
    * @return hash code value for this portlet mode
    */
   public int hashCode()
   {
      return name.hashCode();
   }

   /**
    * Returns a String representation of this portlet mode. Portlet mode names are always lower case names.
    *
    * @return String representation of this portlet mode
    */
   public String toString()
   {
      return name;
   }
}
