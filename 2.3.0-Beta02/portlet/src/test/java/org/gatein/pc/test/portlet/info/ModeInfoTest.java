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
package org.gatein.pc.test.portlet.info;

import org.gatein.pc.api.Mode;
import org.gatein.common.net.media.MediaType;
import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;

import static org.jboss.unit.api.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.com">Boleslaw Dawidowicz</a>
 * @version $Revision: 7954 $
 */
public class ModeInfoTest extends AbstractInfoTest
{

   public ModeInfoTest()
   {
      super("ModeInfoTest");
   }

   public void execute()
   {
      ManagedPortletContainer container1 = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("ModePortlet1");
      ManagedPortletContainer container2 = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("ModePortlet2");
      CapabilitiesInfo capaInfo = container1.getInfo().getCapabilities();

      //Portlet 1 with VIEW, EDIT, HELP
      Set modeInfos = capaInfo.getModes(MediaType.TEXT_HTML);

      //shoule be 3 modes
      assertEquals(3, modeInfos.size());

      Set portletModes = new HashSet();
      for (Iterator i = modeInfos.iterator(); i.hasNext();)
      {
         ModeInfo mode = (ModeInfo)i.next();
         portletModes.add(mode.getMode());
         assertEquals(mode.getModeName(), mode.getMode().toString());
      }


      assertTrue(portletModes.contains(org.gatein.pc.api.Mode.VIEW));
      assertTrue(portletModes.contains(Mode.HELP));
      assertTrue(portletModes.contains(Mode.EDIT));

      //Portlet 2 with only VIEW
      capaInfo = container2.getInfo().getCapabilities();
      modeInfos = capaInfo.getModes(MediaType.TEXT_HTML);

      //shoule be 1 mode
      assertEquals(1, modeInfos.size());


      portletModes = new HashSet();
      for (Iterator i = modeInfos.iterator(); i.hasNext();)
      {
         ModeInfo mode = (ModeInfo)i.next();
         portletModes.add(mode.getMode());
         assertEquals(mode.getModeName(), mode.getMode().toString());
      }

      assertTrue(portletModes.contains(Mode.VIEW));
      assertTrue(!portletModes.contains(Mode.HELP));
      assertTrue(!portletModes.contains(Mode.EDIT));
   }
}
