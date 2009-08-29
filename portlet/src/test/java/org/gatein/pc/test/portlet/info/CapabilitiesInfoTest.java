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
import org.gatein.pc.api.WindowState;
import org.gatein.common.net.media.MediaType;
import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.WindowStateInfo;

import static org.jboss.unit.api.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6720 $
 */
public class CapabilitiesInfoTest extends AbstractInfoTest
{

   public CapabilitiesInfoTest()
   {
      super("CapabilitiesInfoTest");
   }

   public void execute()
   {
      ManagedPortletContainer container = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("CapabilitiesPortlet");
      PortletInfo info = container.getInfo();
      CapabilitiesInfo capInfo = info.getCapabilities();

      //TODO: finish locales asserts
      // locales should at least contain Locale.ENGLISH
      Set allLocales = capInfo.getAllLocales();
      assertNotNull(allLocales);
      assertTrue(allLocales.contains(Locale.ENGLISH));

      //check mime types
      Set<MediaType> types = capInfo.getMediaTypes();
      String[] mt = new String[types.size()];
      int count = 0;
      for (Iterator i = types.iterator(); i.hasNext();)
      {
         Object o = i.next();
         mt[count++] = (String)o;
      }
      assertEquals(new String[]{"text/vnd.wap.wml", "text/html"}, mt);

      //check all modes
      Set modeInfos = capInfo.getAllModes();
      Set modes = extractModes(modeInfos);

      assertTrue(modes.contains(org.gatein.pc.api.Mode.VIEW));
      assertTrue(modes.contains(org.gatein.pc.api.Mode.HELP));
      assertTrue(modes.contains(Mode.EDIT));

      //check text/html modes
      modeInfos = capInfo.getModes(MediaType.TEXT_HTML);
      modes = extractModes(modeInfos);
      assertEquals(2, modes.size());
      assertTrue(modes.contains(org.gatein.pc.api.Mode.VIEW));
      assertTrue(modes.contains(org.gatein.pc.api.Mode.HELP));

      //check text/vnd.wap.wml modes
      modeInfos = capInfo.getModes(MediaType.create("text/vnd.wap.wml"));
      modes = extractModes(modeInfos);
      assertEquals(2, modes.size());
      assertTrue(modes.contains(org.gatein.pc.api.Mode.VIEW));
      assertTrue(modes.contains(org.gatein.pc.api.Mode.EDIT));

      //check undeclared
      modeInfos = capInfo.getModes(MediaType.create("text/undeclared"));
      modes = extractModes(modeInfos);
      assertEquals(0, modes.size());

      //check all window states
      Set stateInfos = capInfo.getAllWindowStates();
      Set states = extractWindowStates(stateInfos);
      assertEquals(3, states.size());
      assertTrue(states.contains(org.gatein.pc.api.WindowState.NORMAL));
      assertTrue(states.contains(org.gatein.pc.api.WindowState.MINIMIZED));
      assertTrue(states.contains(org.gatein.pc.api.WindowState.MAXIMIZED));

      //check for text/html
      stateInfos = capInfo.getWindowStates(MediaType.TEXT_HTML);
      states = extractWindowStates(stateInfos);
      assertEquals(3, states.size());
      assertTrue(states.contains(org.gatein.pc.api.WindowState.NORMAL));
      assertTrue(states.contains(WindowState.MINIMIZED));
      assertTrue(states.contains(org.gatein.pc.api.WindowState.MAXIMIZED));

      //simple check for text/vnd.wap.wml
      //TODO:is this really expected behaviour? shouldn't it return no states as this is unsupported by portal one?
      states = capInfo.getWindowStates(MediaType.create("text/vnd.wap.wml"));
      assertEquals(3, states.size());

      //check undeclared
      states = capInfo.getWindowStates(MediaType.create("undeclared/mime"));
      assertEquals(0, states.size());

      //fail();

      //throw new UnsupportedOperationException("CapabilitiesInfoTest: Finish implementation!");
   }

   public Set extractWindowStates(Set infos)
   {
      Set states = new HashSet();

      for (Iterator i = infos.iterator(); i.hasNext();)
      {
         WindowStateInfo info = (WindowStateInfo)i.next();
         org.gatein.pc.api.WindowState state = info.getWindowState();
         assertEquals(state.toString(), (info.getWindowStateName()));
         states.add(state);
      }
      return states;
   }

   public Set extractModes(Set infos)
   {
      Set modes = new HashSet();

      for (Iterator i = infos.iterator(); i.hasNext();)
      {
         ModeInfo info = (ModeInfo)i.next();
         Mode mode = info.getMode();
         assertEquals(mode.toString(), (info.getModeName()));
         modes.add(mode);
      }
      return modes;
   }
}
