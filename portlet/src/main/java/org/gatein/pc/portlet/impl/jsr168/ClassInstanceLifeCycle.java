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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.pc.portlet.container.PortletInitializationException;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ClassInstanceLifeCycle<T>
{

   /** . */
   private Logger log;

   /** . */
   private Class<T> expectedClass;

   /** . */
   private ClassLoader classLoader;

   /** . */
   private String className;

   /** . */
   private String type;

   /** . */
   private T instance;

   public ClassInstanceLifeCycle(
      Logger log,
      Class<T> expectedClass,
      ClassLoader classLoader,
      String className,
      String type)
   {
      this.log = log;
      this.expectedClass = expectedClass;
      this.classLoader = classLoader;
      this.className = className;
      this.type = type;
   }

   public void create() throws PortletInitializationException
   {
      T instance;
      try
      {
         Class clazz = classLoader.loadClass(className);
         if (expectedClass.isAssignableFrom(clazz))
         {
            Class<? extends T> castedClass = clazz.asSubclass(expectedClass);
            Constructor<? extends T> ctor = castedClass.getConstructor();
            instance = ctor.newInstance();
         }
         else
         {
            String msg = "Cannot create " + type + " with class " + className + " because it does not implement the expected interface " + expectedClass.getName();
            throw new PortletInitializationException(msg);
         }
      }
      catch (InvocationTargetException e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because the class contructor threw an exception";
         throw new PortletInitializationException(msg, e.getCause());
      }
      catch (IllegalAccessException e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because the class is not accessible";
         throw new PortletInitializationException(msg, e);
      }
      catch (NoSuchMethodException e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because it does not have an no argument constructor";
         throw new PortletInitializationException(msg, e);
      }
      catch (ClassNotFoundException e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because the class cannot be loaded";
         throw new PortletInitializationException(msg, e);
      }
      catch (InstantiationException e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because it cannot be instantiated";
         throw new PortletInitializationException(msg, e);
      }
      catch (Error e)
      {
         String msg = "Cannot create " + type + " with class " + className + " because of an error";
         throw new PortletInitializationException(msg, e);
      }

      //
      final ClassLoader previousLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(classLoader);

         //
         start(instance);
      }
      catch (Exception e)
      {
         String msg = "The " + type + " threw an exception during init";
         throw new PortletInitializationException(msg, e);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(previousLoader);
      }

      //
      this.instance = instance;
   }

   public void destroy()
   {
      if (instance == null)
      {
         throw new IllegalStateException("No instance created previously");
      }

      //
      try
      {
         stop(instance);
      }
      catch (Error e)
      {
         log.error("Stopping the " + type + " threw an error", e);
      }
      catch (Exception e)
      {
         log.error("Stopping the " + type + " threw an exception", e);
      }
   }

   public T getInstance()
   {
      return instance;
   }

   protected void start(T instance) throws Exception
   {

   }

   protected void stop(T instance)
   {

   }
}
