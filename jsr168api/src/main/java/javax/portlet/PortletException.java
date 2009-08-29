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
 * The <CODE>PortletException</CODE> class defines a general exception that a portlet can throw when it is unable to
 * perform its operation successfully.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public class PortletException extends Exception
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -5042678869822737513L;

   /** Constructs a new portlet exception. */
   public PortletException()
   {
      super();
   }

   /**
    * Constructs a new portlet exception with the given text. The portlet container may use the text write it to a log.
    *
    * @param text the exception text
    */
   public PortletException(String text)
   {
      super(text);
   }

   /**
    * Constructs a new portlet exception when the portlet needs to do the following: <ul> <li>throw an exception
    * <li>include the "root cause" exception <li>include a description message </ul>
    *
    * @param text  the exception text
    * @param cause the root cause
    */
   public PortletException(String text, Throwable cause)
   {
      super(text, cause);
   }

   /**
    * Constructs a new portlet exception when the portlet needs to throw an exception. The exception's message is based
    * on the localized message of the underlying exception.
    *
    * @param cause the root cause
    */
   public PortletException(Throwable cause)
   {
      super(cause);
   }
}
