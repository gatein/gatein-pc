/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.pc.mc.metadata;

import org.gatein.pc.portlet.impl.metadata.ListenerMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ListenerTestCase extends AbstractMetaDataTestCase
{

   public void test021()
   {
      try
      {
         PortletApplication20MetaData md = _unmarshall10("metadata/listener/portlet2.xml");
         List<ListenerMetaData> listeners = md.getListeners();
         assertNotNull(listeners);
         assertEquals(1, listeners.size());
         ListenerMetaData listenerMD = listeners.get(0);
         assertNotNull(listenerMD);
         assertEquals("MyListener", listenerMD.getListenerClass());
      }
      catch (Exception e)
      {
        throw fail(e, "No exception expected");
      }
   }

}
