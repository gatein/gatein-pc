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
package org.gatein.pc.test.portlet.jsr168.ext.portletconfig;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.base.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.portlet.jsr168.ext.portletconfig.extended.ResourceBundleCascadePortlet;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.assertEquals;
import static org.jboss.unit.api.Assert.fail;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletConfig;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_PORTLET_CONFIG_3
   })
public class ResourceBundleCascadeTestCase
{
   public ResourceBundleCascadeTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, ResourceBundleCascadePortlet.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletConfig cfg = ((AbstractUniversalTestPortlet)portlet).getPortletConfig();

            ResourceBundle bundle_en = cfg.getResourceBundle(Locale.ENGLISH);
            ResourceBundle bundle_it = cfg.getResourceBundle(Locale.ITALIAN);
            ResourceBundle bundle_zz = cfg.getResourceBundle(new Locale("zz"));
            ResourceBundle bundle_en_GB = cfg.getResourceBundle(Locale.UK);
            ResourceBundle bundle_de = cfg.getResourceBundle(Locale.GERMAN);
            ResourceBundle bundle_de_DE = cfg.getResourceBundle(Locale.GERMANY);
            ResourceBundle bundle_fr = cfg.getResourceBundle(Locale.FRENCH);
            ResourceBundle bundle_fr_FR = cfg.getResourceBundle(Locale.FRANCE);
            ResourceBundle bundle_fr_FR_aa = cfg.getResourceBundle(new Locale(Locale.FRANCE.getLanguage(), Locale.FRANCE.getCountry(), "aa"));

            assertEquals(Locale.ENGLISH, bundle_en.getLocale());
            assertEquals(Locale.ENGLISH, bundle_it.getLocale());
            assertEquals(Locale.ENGLISH, bundle_zz.getLocale());
            assertEquals(Locale.UK, bundle_en_GB.getLocale());
            assertEquals(Locale.GERMAN, bundle_de.getLocale());
            assertEquals(Locale.GERMANY, bundle_de_DE.getLocale());
            assertEquals(Locale.FRENCH, bundle_fr.getLocale());
            assertEquals(Locale.FRANCE, bundle_fr_FR.getLocale());
            assertEquals(Locale.FRANCE, bundle_fr_FR_aa.getLocale());

            assertEquals("title", bundle_en.getString("javax.portlet.title"));
            assertEquals("title", bundle_it.getString("javax.portlet.title"));
            assertEquals("title", bundle_zz.getString("javax.portlet.title"));
            assertEquals("title", bundle_en_GB.getString("javax.portlet.title"));
            assertEquals("title", bundle_de.getString("javax.portlet.title"));
            assertEquals("title", bundle_de_DE.getString("javax.portlet.title"));
            assertEquals("title", bundle_fr.getString("javax.portlet.title"));
            assertEquals("title", bundle_fr_FR.getString("javax.portlet.title"));
            assertEquals("title", bundle_fr_FR_aa.getString("javax.portlet.title"));

            assertEquals("short-title", bundle_en.getString("javax.portlet.short-title"));
            assertEquals("short-title", bundle_it.getString("javax.portlet.short-title"));
            assertEquals("short-title", bundle_zz.getString("javax.portlet.short-title"));
            assertEquals("short-title", bundle_de.getString("javax.portlet.short-title"));
            assertEquals("short-title_de_DE", bundle_de_DE.getString("javax.portlet.short-title"));
            assertEquals("short-title_fr", bundle_fr.getString("javax.portlet.short-title"));
            assertEquals("short-title_fr_FR", bundle_fr_FR.getString("javax.portlet.short-title"));
            assertEquals("short-title_fr_FR", bundle_fr_FR_aa.getString("javax.portlet.short-title"));

            assertThrowsMissingResourceException(bundle_en);
            assertThrowsMissingResourceException(bundle_it);
            assertThrowsMissingResourceException(bundle_zz);
            assertThrowsMissingResourceException(bundle_de);
            assertThrowsMissingResourceException(bundle_de_DE);
            assertThrowsMissingResourceException(bundle_fr);
            assertThrowsMissingResourceException(bundle_fr_FR);
            assertThrowsMissingResourceException(bundle_fr_FR_aa);
            return new EndTestResponse();
         }

         private void assertThrowsMissingResourceException(ResourceBundle bundle)
         {
            try
            {
               bundle.getString("javax.portlet.keywords");
               fail();
            }
            catch (MissingResourceException expected)
            {
            }
         }
      });
   }
}
