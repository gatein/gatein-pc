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
package org.gatein.pc.test.portlet.jsr168.tck.preferences.spec;

import javax.portlet.PortletPreferences;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 5448 $
 */
public class CreateASingleInstanceOfValidatorValidator implements PreferencesValidator
{

   /** . */
   private static WeakHashMap<ClassLoader, AtomicInteger> counters = new WeakHashMap<ClassLoader, AtomicInteger>();

   public static int getCreatedCount()
   {
      ClassLoader key = Thread.currentThread().getContextClassLoader();
      AtomicInteger counter = counters.get(key);
      return counter != null ? counter.get() : 0;
   }

   public CreateASingleInstanceOfValidatorValidator()
   {
      synchronized (CreateASingleInstanceOfValidatorValidator.class)
      {
         ClassLoader key = Thread.currentThread().getContextClassLoader();
         AtomicInteger counter = counters.get(key);
         if (counter == null)
         {
            counters.put(key, counter = new AtomicInteger());
         }
         counter.incrementAndGet();
      }
   }

   public void validate(PortletPreferences preferences) throws ValidatorException
   {
   }

}
