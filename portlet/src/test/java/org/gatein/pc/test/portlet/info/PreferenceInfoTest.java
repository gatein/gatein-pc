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

import org.gatein.pc.portlet.container.managed.ManagedPortletContainer;
import org.gatein.pc.portlet.impl.info.ContainerPreferenceInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferencesInfo;
import org.gatein.pc.api.info.PortletInfo;

import static org.jboss.unit.api.Assert.*;

import java.util.Locale;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

/**
 * @author <a href="mailto:boleslaw.dawidowicz@jboss.org">Boleslaw Dawidowicz</a>
 * @version $Revision: 6720 $
 */
public class PreferenceInfoTest extends AbstractInfoTest
{

   public PreferenceInfoTest()
   {
      super("PreferenceInfoTest");
   }

   public void execute()
   {
      ManagedPortletContainer container = registry.getManagedPortletApplication("/test-info").getManagedPortletContainer("PreferenceInfoPortlet");

      //
      PortletInfo info = container.getInfo();
      ContainerPreferencesInfo prefsInfo = (ContainerPreferencesInfo)info.getPreferences();

      //
      ContainerPreferenceInfo prefInfo = prefsInfo.getContainerPreference("localized_pref");
      assertEquals("localized_pref", prefInfo.getKey());
      assertEquals("english localized description", prefInfo.getDescription().getString(Locale.ENGLISH, false));
      assertEquals("polish localized description", prefInfo.getDescription().getString(new Locale("pl"), false));
      assertEquals("english_localized_name", prefInfo.getDisplayName().getString(Locale.ENGLISH, false));
      assertEquals("polish_localized_name", prefInfo.getDisplayName().getString(new Locale("pl"), false));

      //
      Set keys = prefsInfo.getKeys();
      assertTrue(keys.contains("localized_pref"));
      assertTrue(keys.contains("single_pref"));
      assertTrue(keys.contains("multi_pref"));
      assertTrue(keys.contains("single_pref_bis"));
      assertTrue(keys.contains("multi_pref_bis"));

      //
      prefInfo = prefsInfo.getContainerPreference("single_pref");
      assertEquals("single_pref", prefInfo.getKey());
      List<String> values = prefInfo.getDefaultValue();
      assertEquals(Arrays.asList("single_pref_value"), values);
      assertTrue(!prefInfo.isReadOnly().booleanValue());

      //
      prefInfo = prefsInfo.getContainerPreference("multi_pref");
      assertEquals("multi_pref", prefInfo.getKey());
      values = prefInfo.getDefaultValue();
      assertEquals(Arrays.asList("multi_pref_value_1", "multi_pref_value_2"), values);
      assertTrue(!prefInfo.isReadOnly().booleanValue());

      //
      prefInfo = prefsInfo.getContainerPreference("single_pref_bis");
      assertEquals("single_pref_bis", prefInfo.getKey());
      values = prefInfo.getDefaultValue();
      assertEquals(Arrays.asList("single_pref_value"), values);
      assertTrue(prefInfo.isReadOnly().booleanValue());

      //
      prefInfo = prefsInfo.getContainerPreference("multi_pref_bis");
      assertEquals("multi_pref_bis", prefInfo.getKey());
      values = prefInfo.getDefaultValue();
      assertEquals(Arrays.asList("multi_pref_value_1", "multi_pref_value_2"), values);
      assertTrue(prefInfo.isReadOnly().booleanValue());
   }
}
