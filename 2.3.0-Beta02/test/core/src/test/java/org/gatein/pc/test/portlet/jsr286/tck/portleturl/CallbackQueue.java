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
package org.gatein.pc.test.portlet.jsr286.tck.portleturl;

import org.jboss.unit.api.Assert;

import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import java.util.LinkedList;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class CallbackQueue
{

   /** . */
   private static LinkedList<PortletURLSnapshot> queue = new LinkedList<PortletURLSnapshot>();

   public static PortletURLSnapshot next()
   {
      Assert.assertTrue(queue.size() > 0);
      return queue.removeFirst();
   }

   public static void clear()
   {
      queue.clear();
   }

   public static int size()
   {
      return queue.size();
   }

   public static PortletURLGenerationListener createListener(final String id)
   {
      return new PortletURLGenerationListener()
      {
         public void filterActionURL(PortletURL portletURL)
         {
            queue.addLast(PortletURLSnapshot.createActionURL(id, portletURL));
         }

         public void filterRenderURL(PortletURL portletURL)
         {
            queue.addLast(PortletURLSnapshot.createRenderURL(id, portletURL));
         }

         public void filterResourceURL(ResourceURL resourceURL)
         {
            queue.addLast(PortletURLSnapshot.createResourceURL(id, resourceURL));
         }
      };
   }
}
