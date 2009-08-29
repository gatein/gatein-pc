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
package org.gatein.pc.test.portlet.session;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class ReplicatedValue implements Serializable
{

   /** . */
   public final String state;

   public ReplicatedValue(String state)
   {
      if (state == null)
      {
         throw new IllegalArgumentException();
      }
      this.state = state;
   }

   public String getState()
   {
      return state;
   }

   public int hashCode()
   {
      return state.hashCode();
   }

   public boolean equals(Object obj)
   {
      return obj instanceof ReplicatedValue && ((ReplicatedValue)obj).state.equals(state);
   }


   public String toString()
   {
      return "ReplicatedValue: [" + state + "]";
   }

   public static Object create(ClassLoader classLoader, String value) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
   {
      Class clazz = classLoader.loadClass(ReplicatedValue.class.getName());
      Constructor ctor = clazz.getConstructor(new Class[]{String.class});
      return ctor.newInstance(new Object[]{value});
   }
}
