/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portlet.container;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ObjectSupport
{

   /** . */
   private final String id;

   /** . */
   private int started;

   /** . */
   private int stopped;

   /** . */
   Callback startCallback;

   /** . */
   Callback stopCallback;

   public ObjectSupport(String id)
   {
      this.id = id;
      this.startCallback = null;
      this.stopCallback = null;
   }

   public String getId()
   {
      return id;
   }

   public int getStarted()
   {
      return started;
   }

   public int getStopped()
   {
      return stopped;
   }

   public void start() throws Exception
   {
      started++;

      //
      if (startCallback != null)
      {
         startCallback.execute();
      }
   }

   public void stop()
   {
      stopped++;

      //
      if (stopCallback != null)
      {
         stopCallback.execute();
      }
   }

   public interface Callback
   {
      void execute();
   }

   public static Callback FAILURE_CALLBACK = new Callback()
   {
      public void execute()
      {
         throw new RuntimeException();
      }
   };
}
