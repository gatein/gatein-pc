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
package org.gatein.pc.management;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class PortletInfo
{

   private long renderRequestProcessingTime;

   private long actionRequestProcessingTime;

   private long maxActionTime;

   private long maxRenderTime;

   private String maxRenderRequestUri;

   private String maxActionRequestUri;

   private int actionRequestCount;

   private int renderRequestCount;

   private int renderErrorCount;

   private int actionErrorCount;

   public long getRenderRequestProcessingTime()
   {
      return renderRequestProcessingTime;
   }

   public long getActionRequestProcessingTime()
   {
      return actionRequestProcessingTime;
   }

   public long getMaxRenderTime()
   {
      return maxRenderTime;
   }

   public long getMaxActionTime()
   {
      return maxActionTime;
   }

   public String getMaxRenderRequestUri()
   {
      return maxRenderRequestUri;
   }

   public String getMaxActionRequestUri()
   {
      return maxActionRequestUri;
   }

   public synchronized float getAverageRenderTime()
   {
      return (float)renderRequestProcessingTime/renderRequestCount;
   }
   
   public synchronized float getAverageActionTime()
   {
      return (float)actionRequestProcessingTime/actionRequestCount;
   }

   public int getRenderRequestCount()
   {
      return renderRequestCount;
   }

   public int getActionRequestCount()
   {
      return actionRequestCount;
   }

   public int getRenderErrorCount()
   {
      return renderErrorCount;
   }

   public int getActionErrorCount()
   {
      return actionErrorCount;
   }

   public synchronized void newRenderCall(long time, boolean error)
   {
      renderRequestProcessingTime += time;
      if (time > maxRenderTime)
      {
         maxRenderTime = time;
      }
      renderRequestCount++; 
      if (error)
      {
         renderErrorCount++;
      }
   }

   public synchronized void newActionCall(long time, boolean error)
   {
      actionRequestProcessingTime += time;
      if (time > maxActionTime)
      {
         maxActionTime = time;
      }
      actionRequestCount++; 
      if (error)
      {
         actionErrorCount++;
      }
   }

   public boolean isUsed()
   {
      return getActionRequestCount() + getRenderRequestCount() > 0;
   }
}
