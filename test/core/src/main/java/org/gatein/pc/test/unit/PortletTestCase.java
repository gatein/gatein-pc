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

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * A sequence of test actions.
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6752 $
 */
public class PortletTestCase
{

   /** The test case name. */
   private final String name;

   /** . */
   private final ServletContext context;

   /** . */
   private final Map<Key, TestAction> bindings;

   public PortletTestCase(String name, ServletContext context)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Test case name must be provided");
      }

      //
      this.name = name;
      this.context = context;
      this.bindings = new HashMap<Key, TestAction>();
   }

   public String getName()
   {
      return name;
   }

   public ServletContext getContext()
   {
      return context;
   }

   public void bindAction(JoinPoint joinPoint, TestAction action)
   {
      internalBind(null, joinPoint, action);
   }

   public void bindAction(int requestCount, JoinPoint joinPoint, TestAction action)
   {
      internalBind(requestCount, joinPoint, action);
   }

   private void internalBind(Integer count, JoinPoint joinPoint, TestAction action)
   {
      if (action == null)
      {
         throw new IllegalArgumentException("Action can't be null");
      }

      // Check any global binding first
      if (bindings.containsKey(new Key(joinPoint)))
      {
         throw new IllegalStateException("Action for this joinpoint already defined globally");
      }

      //
      Key key = new Key(count, joinPoint);

      //
      if (bindings.containsKey(key))
      {
         throw new IllegalStateException("Action " + key + " for this joinpoint already defined in provided request count");
      }
      else
      {
         bindings.put(key, action);
      }
   }

   public String getActorId(int count, JoinPointType joinPointType)
   {
      for (Key key : bindings.keySet())
      {
         if (key.joinPoint.getType() == joinPointType)
         {
            if (key.count == null || key.count == count)
            {
               return key.joinPoint.getActorId();
            }
         }
      }
      return null;
   }

   public Set<JoinPoint> getJoinPoints(int count)
   {
      Set<JoinPoint> joinPoints = new HashSet<JoinPoint>();
      for (Key key : bindings.keySet())
      {
         if (key.count == count)
         {
            joinPoints.add(key.joinPoint);
         }
      }
      return joinPoints;
   }

   public TestAction getAction(int count, JoinPoint joinPoint)
   {
      // Try a timed action
      TestAction action = bindings.get(new Key(count, joinPoint));

      // Try a global action
      if (action == null)
      {
         action = bindings.get(new Key(joinPoint));
      }

      //
      return action;
   }

   private static class Key
   {

      /** . */
      private final Integer count;

      /** . */
      private final JoinPoint joinPoint;

      public Key(JoinPoint joinPoint)
      {
         this(null, joinPoint);
      }

      public Key(Integer count, JoinPoint joinPoint)
      {
         if (count != null && count < 0)
         {
            throw new IllegalArgumentException("Count value must be positive was " + count);
         }
         if (joinPoint == null)
         {
            throw new IllegalArgumentException("Joinpoint can't be null");
         }
         this.count = count;
         this.joinPoint = joinPoint;
      }

      public boolean equals(Object o)
      {
         if (this == o)
         {
            return true;
         }
         if (o instanceof Key)
         {
            Key that = (Key)o;
            return (this.count == null ? that.count == null : this.count.equals(that.count)) && this.joinPoint.equals(that.joinPoint);
         }
         return false;
      }

      public int hashCode()
      {
         int result;
         result = count != null ? count : 0;
         result = 29 * result + joinPoint.hashCode();
         return result;
      }

      @Override
      public String toString()
      {
         return getClass().getSimpleName() + "[count=" + count + ",jointPoint=" + joinPoint + "]";
      }
   }
}
