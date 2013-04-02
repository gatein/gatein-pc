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
package org.gatein.pc.test.unit;

import java.io.Serializable;

/**
 * A failure, contains the description of a failure.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class Failure implements Serializable
{

   /** The optional failure message. */
   private final String message;

   /** The optional failure cause. */
   private final Throwable cause;

   /** The type of failure. */
   private final FailureType type;

   /** The non null stack trace. */
   private final Throwable stackTrace;

   public Failure(String message, FailureType type)
   {
      this(message, null, type);
   }

   public Failure(Throwable cause, FailureType type)
   {
      this(null, cause, type);
   }

   public Failure(String message, Throwable cause, FailureType type)
   {
      if (message == null)
      {
         if (cause != null)
         {
            message = cause.getMessage();
         }
      }

      //
      this.message = message;
      this.cause = cause;
      this.type = type;
      this.stackTrace = cause != null ? cause : new Exception(message != null ? message : "Failed at");
   }

   public String getMessage()
   {
      return message;
   }

   public Throwable getCause()
   {
      return cause;
   }

   public Throwable getStackTrace()
   {
      return stackTrace;
   }

   public FailureType getType()
   {
      return type;
   }

   public static Failure createFailure(Throwable cause)
   {
      if (cause instanceof AssertionError)
      {
         return new Failure(cause, FailureType.ASSERTION);
      }
      else
      {
         return new Failure(cause, FailureType.ERROR);
      }
   }

   public static Failure createFailure(String message, Throwable cause)
   {
      if (cause instanceof AssertionError)
      {
         return new Failure(message, cause, FailureType.ASSERTION);
      }
      else
      {
         return new Failure(message, cause, FailureType.ERROR);
      }
   }

   public static Failure createErrorFailure(String message)
   {
      return new Failure(message, FailureType.ERROR);
   }

   public static Failure createErrorFailure(String message, Throwable cause)
   {
      return new Failure(message, cause, FailureType.ERROR);
   }

   public static Failure createErrorFailure(Throwable cause)
   {
      return new Failure(cause, FailureType.ERROR);
   }

   public static Failure createAssertionFailure(Throwable cause)
   {
      return new Failure(cause, FailureType.ASSERTION);
   }

   public static Failure createAssertionFailure(String message)
   {
      return new Failure(message, FailureType.ASSERTION);
   }
}
