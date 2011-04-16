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
package org.gatein.pc.test.portlet.state;

import org.gatein.pc.api.state.PropertyMap;

import java.util.HashSet;
import java.util.List;

import org.jboss.unit.api.Assert;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5448 $
 */
public class ValueMapAssert
{
   public static void assertEquals(PropertyMap vm1, PropertyMap vm2)
   {
      if (vm1 == null)
      {
         if (vm2 != null)
         {
            Assert.fail("Value map should be null");
         }
      }
      else
      {
         if (vm2 == null)
         {
            Assert.fail("Value map should not be null");
         }
         Assert.assertEquals("Value maps don't have the same keys", new HashSet<String>(vm1.keySet()), new HashSet<String>(vm2.keySet()));
         for (String key : vm1.keySet())
         {
            List<String> v1 = vm1.getProperty(key);
            List<String> v2 = vm2.getProperty(key);
            Assert.assertEquals("Values for key " + key + " are not equals", v1, v2);
         }
      }
   }


}
