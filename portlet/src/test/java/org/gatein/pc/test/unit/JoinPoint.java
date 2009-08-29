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

import org.gatein.pc.test.unit.JoinPointType;
import org.gatein.pc.test.unit.annotations.TestActor;

import java.lang.reflect.AnnotatedElement;

/**
 * Defines a binding point for component id and its method.
 *
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 5510 $
 */
public class JoinPoint
{

   /** Id of actor like "portlet A", "Servlet B". */
   private final String actorId;

   /** method in component like "render" and etc. */
   private final JoinPointType type;

   public JoinPoint(String actorId, JoinPointType type)
   {
      if (actorId == null)
      {
         throw new IllegalArgumentException("Actor id value cannot be null");
      }
      if (type == null)
      {
         throw new IllegalArgumentException("Type cannot be null");
      }

      //
      this.actorId = actorId;
      this.type = type;
   }

   public String getActorId()
   {
      return actorId;
   }

   public JoinPointType getType()
   {
      return type;
   }

   public int hashCode()
   {
      return actorId.hashCode() * 43 + type.hashCode();
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof JoinPoint))
      {
         throw new IllegalArgumentException("Cannot compare with different object class");
      }
      JoinPoint j = (JoinPoint)obj;
      return actorId.equals(j.actorId) && type == j.type;
   }

   public String toString()
   {
      return actorId + "_" + type;
   }

   public static JoinPoint createJoinPoint(Class annotatedClass, JoinPointType type)
   {
      TestActor testActorAnnotation = ((AnnotatedElement)annotatedClass).getAnnotation(TestActor.class);

      //
      if (testActorAnnotation == null)
      {
         throw new IllegalStateException("No annotation @TestPortlet found on portlet class " + annotatedClass.getName());
      }

      //
      String actorId = testActorAnnotation.id();

      //
      return new JoinPoint(actorId, type);
   }
}

