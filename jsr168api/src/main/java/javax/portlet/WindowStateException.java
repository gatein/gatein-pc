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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * The <CODE>WindowStateException</CODE> is thrown when a portlet tries to use a window state that is not supported by
 * the current runtime environment or the portlet.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5527 $
 */
public class WindowStateException extends PortletException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -7034467327478096995L;

   private transient WindowState state;

   /**
    * Constructs a new portlet state exception with the given text. The portlet container may use the text write it to a
    * log.
    *
    * @param text  the exception text
    * @param state the state causing the exception
    */
   public WindowStateException(String text, WindowState state)
   {
      super(text);
      this.state = state;
   }

   /**
    * Constructs a new portlet state exception when the portlet needs to do the following: <ul> <il>throw an exception
    * <li>include a message about the "root cause" that interfered with its normal operation <li>include a description
    * message </ul>
    *
    * @param text  the exception text
    * @param cause the root cause
    * @param state the state causing the exception
    */
   public WindowStateException(String text, Throwable cause, WindowState state)
   {
      super(text, cause);
      this.state = state;
   }

   /**
    * Constructs a new portlet state exception when the portlet needs to throw an exception. The exception message is
    * based on the localized message of the underlying exception.
    *
    * @param cause the root cause
    * @param state the state causing the exception
    */
   public WindowStateException(Throwable cause, WindowState state)
   {
      super(cause);
      this.state = state;
   }

   /**
    * Returns the portlet state causing this exception.
    *
    * @return the window state causing this exception
    */
   public WindowState getState()
   {
      return state;
   }

   private void writeObject(ObjectOutputStream out) throws IOException
   {
      out.writeUTF(state.toString());
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      String s = in.readUTF();
      state = new WindowState(s);
   }
}
