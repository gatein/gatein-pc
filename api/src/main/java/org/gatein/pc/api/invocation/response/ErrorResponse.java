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
package org.gatein.pc.api.invocation.response;

import org.gatein.common.util.Exceptions;
import org.apache.log4j.Logger;

/**
 * Application level error.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7395 $
 */
public class ErrorResponse extends PortletInvocationResponse
{
   /** The logged throwable if any. */
   private final Throwable cause;

   /** There must be one error message. */
   private final String message;

   public ErrorResponse(Throwable cause)
   {
      if (cause == null)
      {
         throw new IllegalArgumentException("There must be a cause");
      }
      this.cause = cause;
      this.message = cause.getMessage();
   }

   public ErrorResponse(String message, Throwable cause)
   {
      if (message == null)
      {
         throw new IllegalArgumentException("There must be a message");
      }
      if (cause == null)
      {
         throw new IllegalArgumentException("There must be a cause");
      }
      this.cause = cause;
      this.message = message;
   }

   public ErrorResponse(String message)
   {
      if (message == null)
      {
         throw new IllegalArgumentException("Message cannot be null.");
      }
      this.message = message;
      this.cause = null;
   }

   /** The throwable. Can be a null object. */
   public Throwable getCause()
   {
      return cause;
   }

   /** The logged message. */
   public String getMessage()
   {
      if (message == null)
      {
         return cause.getClass().getName();
      }

      return message;
   }

   /**
    * Logs an error message to the specified logger, using either the message or this ErrorResult's Throwable if
    * available.
    *
    * @param log
    * @param logMessage
    */
   public void logErrorTo(Logger log, String logMessage)
   {
      if (cause != null)
      {
         log.error(logMessage, cause);
      }
      else
      {
         log.error(logMessage + "\n" + message);
      }
   }

   /**
    * Provides an HTML description of the error, using either the message or the encapsulated Throwable.
    *
    * @return
    */
   public String toHTML()
   {
      if (cause != null)
      {
         return Exceptions.toHTML(cause);
      }
      else
      {
         StringBuffer sb = new StringBuffer(255);
         sb.append("<div><code>").append(message).append("</code></div>");
         return sb.toString();
      }
   }
}
