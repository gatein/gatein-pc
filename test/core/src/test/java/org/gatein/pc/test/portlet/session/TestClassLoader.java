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

import org.gatein.common.io.IOTools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7374 $
 */
public class TestClassLoader extends ClassLoader
{

   /** . */
   private Class replicatedValueClass;

   public Class loadClass(String name) throws ClassNotFoundException
   {
      if (ReplicatedValue.class.getName().equals(name))
      {
         synchronized (this)
         {
            if (replicatedValueClass == null)
            {
               InputStream in = null;
               try
               {
                  in = ReplicatedValue.class.getClassLoader().getResourceAsStream(ReplicatedValue.class.getName().replace('.', '/') + ".class");
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  IOTools.copy(in, baos);
                  byte[] bytes = baos.toByteArray();
                  replicatedValueClass = defineClass(ReplicatedValue.class.getName(), bytes, 0, bytes.length);
               }
               catch (IOException e)
               {
                  throw new ClassNotFoundException("", e);
               }
               finally
               {
                  IOTools.safeClose(in);
               }
            }
            return replicatedValueClass;
         }
      }
      else
      {
         return super.loadClass(name);
      }
   }
}
