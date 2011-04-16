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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * The <CODE>PortletModeException</CODE> is thrown when a portlet tries to use or set a portlet mode that is not
 * supported by the current runtime environment or the portlet. s *
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5527 $
 */
public class PortletModeException extends PortletException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -6950436767343780823L;

   private transient PortletMode mode;

   /**
    * Constructs a new portlet mode exception with the given text and the portlet mode that caused this exception. The
    * portlet container may use the text and portlet mode write it to a log.
    *
    * @param text the exception text
    * @param mode the mode causing the exception
    */
   public PortletModeException(String text, PortletMode mode)
   {
      super(text);
      this.mode = mode;
   }

   /**
    * Constructs a new portlet mode exception when the portlet needs to do the following: <ul> <il>throw an exception
    * <li>include a message about the "root cause" that interfered with its normal operation <li>include a description
    * message <li>include the portlet mode that caused this exception </ul>
    *
    * @param text  the exception text
    * @param cause the root cause
    * @param mode  the mode causing the exception
    */
   public PortletModeException(String text, Throwable cause, PortletMode mode)
   {
      super(text, cause);
      this.mode = mode;
   }

   /**
    * Constructs a new portlet mode exception when the portlet needs to throw an exception. The exception message is
    * based on the localized message of the underlying exception and the portlet mode that caused this exception.
    *
    * @param cause the root cause
    * @param mode  the mode causing the exception
    */
   public PortletModeException(Throwable cause, PortletMode mode)
   {
      super(cause);
      this.mode = mode;
   }

   /**
    * Returns the unsupported portlet mode causing this exception.
    *
    * @return the portlet mode that caused this exception
    */
   public PortletMode getMode()
   {
      return mode;
   }

   private void writeObject(ObjectOutputStream out) throws IOException
   {
      out.writeUTF(mode.toString());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      String s = in.readUTF();
      mode = new PortletMode(s);
   }
}
