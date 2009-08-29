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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <CODE>ValidatorException</CODE> is thrown by the <CODE>validate</CODE> method of a PreferencesValidator when the
 * validation of a preference failed.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public class ValidatorException extends PortletException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = 298939407901896813L;

   private static final Enumeration EMPTY_ENUMERATION = new Enumeration()
   {
      public boolean hasMoreElements()
      {
         return false;
      }

      public Object nextElement()
      {
         throw new NoSuchElementException();
      }
   };

   private Collection failedKeys;
   private Enumeration failedKeysEnumeration;

   /**
    * Constructs a new validator exception with the given text. The portlet container may use the text write it to a
    * log.
    * <p/>
    * The collection of failed keys may contain all failed keys, only the first key that failed validation, or may be
    * <code>null</code>.
    *
    * @param text       the exception text
    * @param failedKeys keys that failed the validation; may be <code>null</code>
    */
   public ValidatorException(String text, Collection failedKeys)
   {
      super(text);
      this.failedKeys = failedKeys;
      this.failedKeysEnumeration = null;
   }

   /**
    * Constructs a new portlet validator exception. Used, when the portlet needs to do one of the following: <ul>
    * <il>throw an exception <li>include a message about the "root cause" that interfered with its normal operation
    * <li>include a description message </ul>
    * <p/>
    * The Collection of failed keys may contain all failed keys, only the first key that failed validation, or may be
    * <code>null</code>.
    *
    * @param text       the exception text
    * @param cause      the root cause
    * @param failedKeys keys that failed the validation; may be <code>null</code>
    */
   public ValidatorException(String text, Throwable cause, Collection failedKeys)
   {
      super(text, cause);
      this.failedKeys = failedKeys;
   }

   /**
    * Constructs a new portlet validator exception when the portlet needs to throw an exception. The exception message
    * is based on the localized message of the underlying exception.
    * <p/>
    * The Collection of failed keys may contain all failed keys, only the first key that failed validation, or may be
    * <code>null</code>.
    *
    * @param cause      the root cause
    * @param failedKeys keys that failed the validation; may be <code>null</code>
    */
   public ValidatorException(Throwable cause, Collection failedKeys)
   {
      super(cause);
      this.failedKeys = failedKeys;
   }

   /**
    * Returns the keys that failed the validation.
    * <p/>
    * The Enumeration of failed keys may contain all failed keys, only the first key that failed validation, or an empty
    * <code>Enumeration</code> if no failed keys are available.
    *
    * @return the keys that failed validation, or an empty <code>Enumeration</code> if no failed keys are available.
    */
   public Enumeration getFailedKeys()
   {
      if (failedKeysEnumeration == null)
      {
         if (failedKeys != null)
         {
            final Iterator iterator = failedKeys.iterator();
            failedKeysEnumeration = new Enumeration()
            {
               public boolean hasMoreElements()
               {
                  return iterator.hasNext();
               }

               public Object nextElement()
               {
                  return iterator.next();
               }
            };
         }
         else
         {
            failedKeysEnumeration = EMPTY_ENUMERATION;
         }
      }
      return failedKeysEnumeration;
   }
}
