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
package org.gatein.pc.mc.metadata;

import java.util.List;
import java.util.Locale;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletCacheScopeEnum;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletInfoMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletPreferencesMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SecurityRoleRefMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportedLocaleMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.SupportsMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class PortletTestEverythingTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test01()
   {
      try
      {
         String xmlFile = "metadata/portlet/portlet1.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());

         Locale fr = new Locale("fr");
         Locale default_locale = new Locale(PortletMetaDataConstants.DEFAULT_LOCALE);

         PortletMetaData p1 = md.getPortlet("Portlet1");
         assertNotNull(p1);
         assertEquals(md, p1.getPortletApplication());
         assertEquals("fragmichnicht", p1.getId());
         assertEquals("Portlet1", p1.getPortletName());
         assertEquals(default_locale, p1.getDescription().getDefaultLocale());
         assertEquals("default", p1.getDescription().getDefaultString());
         assertEquals("defaut", p1.getDescription().getString(fr, false));
         assertEquals(default_locale, p1.getDisplayName().getDefaultLocale());
         assertEquals("display", p1.getDisplayName().getDefaultString());
         assertEquals("affichage", p1.getDisplayName().getString(fr, false));
         assertEquals("org.jboss.portal.test.portlet.TestPortlet", p1.getPortletClass());

         InitParamMetaData ip1 = p1.getInitParams().get(0);
         assertEquals(default_locale, ip1.getDescription().getDefaultLocale());
         assertEquals("first parameter", ip1.getDescription().getDefaultString());
         assertEquals("premier parametre", ip1.getDescription().getString(fr, false));
         assertEquals("one", ip1.getName());
         assertEquals("1", ip1.getValue());

         InitParamMetaData ip2 = p1.getInitParams().get(1);
         assertNotNull(ip2);
         assertEquals("second parameter", ip2.getDescription().getDefaultString());
         assertEquals("deuxieme parametre", ip2.getDescription().getString(fr, false));
         assertEquals("two", ip2.getName());
         assertEquals("2", ip2.getValue());

         // Expiration cache
         assertEquals(0, p1.getExpirationCache());

         SupportsMetaData smd1 = p1.getSupports().get(0);
         assertEquals("text/html", smd1.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd1.getPortletModes().get(0).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("EDIT"), smd1.getPortletModes().get(1).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd1.getPortletModes().get(2).getPortletMode());

         SupportsMetaData smd2 = p1.getSupports().get(1);
         assertEquals("text/wml", smd2.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd2.getPortletModes().get(0).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd2.getPortletModes().get(1).getPortletMode());

         assertEquals("MyResourceBundle", p1.getResourceBundle());

         List<SupportedLocaleMetaData> localeList = p1.getSupportedLocale();
         assertEquals(3, localeList.size());
         assertEquals("en", localeList.get(0).getLocale());
         assertEquals("fr", localeList.get(1).getLocale());
         assertEquals("fr_FR", localeList.get(2).getLocale());

         PortletInfoMetaData pimd = p1.getPortletInfo();
         assertNotNull(pimd);
         assertEquals("very long portlet title", pimd.getTitle());
         assertEquals("short portlet title", pimd.getShortTitle());
         assertEquals("a,b,c,d,e,f", pimd.getKeywords());

         PortletPreferencesMetaData ppmd = p1.getPortletPreferences();
         assertNotNull(ppmd);

         assertEquals("MyValidator", ppmd.getPreferenceValidator());
         assertEquals("1", ppmd.getPortletPreferences().get("one").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("two").getValue().get(0));
         assertEquals(false, ppmd.getPortletPreferences().get("two").isReadOnly());
         assertEquals("3", ppmd.getPortletPreferences().get("three").getValue().get(0));
         assertEquals(true, ppmd.getPortletPreferences().get("three").isReadOnly());
         assertEquals("1", ppmd.getPortletPreferences().get("all").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("all").getValue().get(1));
         assertEquals("3", ppmd.getPortletPreferences().get("all").getValue().get(2));

         SecurityRoleRefMetaData srrmd1 = p1.getSecurityRoleRef().get(0);
         assertNotNull(srrmd1);
         assertEquals("role with no link", srrmd1.getDescription().getDefaultString());
         assertEquals("role sans link", srrmd1.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITHOUT_LINK", srrmd1.getRoleName());

         SecurityRoleRefMetaData srrmd2 = p1.getSecurityRoleRef().get(1);
         assertNotNull(srrmd2);
         assertEquals("role with link", srrmd2.getDescription().getDefaultString());
         assertEquals("role avec link", srrmd2.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITH_LINK", srrmd2.getRoleName());
         assertEquals("ROLE_LINK", srrmd2.getRoleLink());

         // Portlet 2

         PortletMetaData p2 = md.getPortlet("Portlet2");
         assertNotNull(p2);
         assertEquals(md, p2.getPortletApplication());
         assertEquals("org.jboss.portal.test.portlet.TestSessionPortlet", p2.getPortletClass());
         assertEquals("text/html", p2.getSupports().get(0).getMimeType());
         assertEquals("test the portlet session", p2.getPortletInfo().getTitle());

         // default value should be 0
         assertEquals(0, p2.getExpirationCache());

         PortletMetaData p3 = md.getPortlet("Portlet3");
         assertNotNull(p3);
         assertEquals(md, p3.getPortletApplication());

         PortletMetaData p4 = md.getPortlet("Portlet4");
         assertNotNull(p4);
         assertEquals(md, p4.getPortletApplication());

         PortletMetaData p5 = md.getPortlet("Portlet5");
         assertNotNull(p5);
         assertEquals(md, p5.getPortletApplication());
         PortletPreferencesMetaData ppmd5 = p5.getPortletPreferences();
         assertEquals("1", ppmd5.getPortletPreferences().get("one").getValue().get(0));
         assertEquals("2", ppmd5.getPortletPreferences().get("two").getValue().get(0));
         assertEquals(false, ppmd5.getPortletPreferences().get("two").isReadOnly());
         assertEquals("3", ppmd5.getPortletPreferences().get("three").getValue().get(0));
         assertEquals(true, ppmd5.getPortletPreferences().get("three").isReadOnly());
         assertEquals("1", ppmd5.getPortletPreferences().get("all").getValue().get(0));
         assertEquals("2", ppmd5.getPortletPreferences().get("all").getValue().get(1));
         assertEquals("3", ppmd5.getPortletPreferences().get("all").getValue().get(2));

         PortletMetaData p6 = md.getPortlet("Portlet6");
         assertNotNull(p6);
         assertEquals(md, p6.getPortletApplication());
         assertEquals("Portlet6", p6.getResourceBundle());
         assertEquals(3, p6.getSupportedLocale().size());
         assertEquals("en", p6.getSupportedLocale().get(0).getLocale());
         assertEquals("fr", p6.getSupportedLocale().get(1).getLocale());
         assertEquals("fr_FR", p6.getSupportedLocale().get(2).getLocale());

         PortletMetaData p7 = md.getPortlet("Portlet7");
         assertNotNull(p7);

         PortletMetaData p8 = md.getPortlet("Portlet8");
         assertNotNull(p8);

         PortletMetaData p9 = md.getPortlet("Portlet9");
         assertNotNull(p9);

         PortletMetaData p10 = md.getPortlet("Portlet10");
         assertNotNull(p10);

         PortletMetaData p11 = md.getPortlet("Portlet11");
         assertNotNull(p11);

         PortletMetaData p12 = md.getPortlet("Portlet12");
         assertNotNull(p12);
         assertEquals(60, p12.getExpirationCache());

         PortletMetaData p13 = md.getPortlet("Portlet13");
         assertNotNull(p13);

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

   @Test
   public void test02()
   {
      try
      {
         String xmlFile = "metadata/portlet/portlet2.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());

         Locale fr = new Locale("fr");
         Locale default_locale = new Locale(PortletMetaDataConstants.DEFAULT_LOCALE);

         PortletMetaData p1 = md.getPortlet("Portlet1");
         assertNotNull(p1);
         assertEquals("fragmichnicht2", p1.getId());
         assertEquals("Portlet1", p1.getPortletName());
         assertEquals(default_locale, p1.getDescription().getDefaultLocale());
         assertEquals("default", p1.getDescription().getDefaultString());
         assertEquals("defaut", p1.getDescription().getString(fr, false));
         assertEquals(default_locale, p1.getDisplayName().getDefaultLocale());
         assertEquals("display", p1.getDisplayName().getDefaultString());
         assertEquals("affichage", p1.getDisplayName().getString(fr, false));
         assertEquals("org.jboss.portal.test.portlet.TestPortlet", p1.getPortletClass());

         InitParamMetaData ip1 = p1.getInitParams().get(0);
         assertEquals(default_locale, ip1.getDescription().getDefaultLocale());
         assertEquals("first parameter", ip1.getDescription().getDefaultString());
         assertEquals("premier parametre", ip1.getDescription().getString(fr, false));
         assertEquals("one", ip1.getName());
         assertEquals("1", ip1.getValue());

         InitParamMetaData ip2 = p1.getInitParams().get(1);
         assertNotNull(ip2);
         assertEquals("second parameter", ip2.getDescription().getDefaultString());
         assertEquals("deuxieme parametre", ip2.getDescription().getString(fr, false));
         assertEquals("two", ip2.getName());
         assertEquals("2", ip2.getValue());

         // Expiration cache
         assertEquals(0, p1.getExpirationCache());

         // cache Scope - JSR 286
         assertEquals(PortletCacheScopeEnum.PUBLIC, p1.getCacheScope());

         SupportsMetaData smd1 = p1.getSupports().get(0);
         assertEquals("text/html", smd1.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd1.getPortletModes().get(0).getPortletMode());
         assertEquals(Mode.create("EDIT"), smd1.getPortletModes().get(1).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd1.getPortletModes().get(2).getPortletMode());

         SupportsMetaData smd2 = p1.getSupports().get(1);
         assertEquals("text/wml", smd2.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd2.getPortletModes().get(0).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd2.getPortletModes().get(1).getPortletMode());

         List<SupportedLocaleMetaData> localeList = p1.getSupportedLocale();
         assertEquals(3, localeList.size());
         assertEquals("en", localeList.get(0).getLocale());
         assertEquals("fr", localeList.get(1).getLocale());
         assertEquals("fr_FR", localeList.get(2).getLocale());

         assertEquals("MyResourceBundle", p1.getResourceBundle());

         PortletInfoMetaData pimd = p1.getPortletInfo();
         assertNotNull(pimd);
         assertEquals("very long portlet title", pimd.getTitle());
         assertEquals("short portlet title", pimd.getShortTitle());
         assertEquals("a,b,c,d,e,f", pimd.getKeywords());

         PortletPreferencesMetaData ppmd = p1.getPortletPreferences();
         assertNotNull(ppmd);
         assertEquals("MyValidator", ppmd.getPreferenceValidator());
         assertEquals("1", ppmd.getPortletPreferences().get("one").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("two").getValue().get(0));
         assertEquals(false, ppmd.getPortletPreferences().get("two").isReadOnly());
         assertEquals("3", ppmd.getPortletPreferences().get("three").getValue().get(0));
         assertEquals(true, ppmd.getPortletPreferences().get("three").isReadOnly());
         assertEquals("1", ppmd.getPortletPreferences().get("all").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("all").getValue().get(1));
         assertEquals("3", ppmd.getPortletPreferences().get("all").getValue().get(2));

         SecurityRoleRefMetaData srrmd1 = p1.getSecurityRoleRef().get(0);
         assertNotNull(srrmd1);
         assertEquals("role with no link", srrmd1.getDescription().getDefaultString());
         assertEquals("role sans link", srrmd1.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITHOUT_LINK", srrmd1.getRoleName());

         SecurityRoleRefMetaData srrmd2 = p1.getSecurityRoleRef().get(1);
         assertNotNull(srrmd2);
         assertEquals("role with link", srrmd2.getDescription().getDefaultString());
         assertEquals("role avec link", srrmd2.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITH_LINK", srrmd2.getRoleName());
         assertEquals("ROLE_LINK", srrmd2.getRoleLink());

         // Portlet2

         PortletMetaData p2 = md.getPortlet("Portlet2");
         assertNotNull(p2);
         assertEquals("org.jboss.portal.test.portlet.TestSessionPortlet", p2.getPortletClass());
         assertEquals("text/html", p2.getSupports().get(0).getMimeType());
         assertEquals("test the portlet session", p2.getPortletInfo().getTitle());

         PortletMetaData p3 = md.getPortlet("Portlet3");
         assertNotNull(p3);

         PortletMetaData p4 = md.getPortlet("Portlet4");
         assertNotNull(p4);

         PortletMetaData p5 = md.getPortlet("Portlet5");
         assertNotNull(p5);
         PortletPreferencesMetaData ppmd5 = p5.getPortletPreferences();
         assertEquals("1", ppmd5.getPortletPreferences().get("one").getValue().get(0));
         assertEquals("2", ppmd5.getPortletPreferences().get("two").getValue().get(0));
         assertEquals(false, ppmd5.getPortletPreferences().get("two").isReadOnly());
         assertEquals("3", ppmd5.getPortletPreferences().get("three").getValue().get(0));
         assertEquals(true, ppmd5.getPortletPreferences().get("three").isReadOnly());
         assertEquals("1", ppmd5.getPortletPreferences().get("all").getValue().get(0));
         assertEquals("2", ppmd5.getPortletPreferences().get("all").getValue().get(1));
         assertEquals("3", ppmd5.getPortletPreferences().get("all").getValue().get(2));

         PortletMetaData p6 = md.getPortlet("Portlet6");
         assertNotNull(p6);
         assertEquals("Portlet6", p6.getResourceBundle());
         assertEquals(3, p6.getSupportedLocale().size());
         assertEquals("en", p6.getSupportedLocale().get(0).getLocale());
         assertEquals("fr", p6.getSupportedLocale().get(1).getLocale());
         assertEquals("fr_FR", p6.getSupportedLocale().get(2).getLocale());

         PortletMetaData p7 = md.getPortlet("Portlet7");
         assertNotNull(p7);

         PortletMetaData p8 = md.getPortlet("Portlet8");
         assertNotNull(p8);

         PortletMetaData p9 = md.getPortlet("Portlet9");
         assertNotNull(p9);

         PortletMetaData p10 = md.getPortlet("Portlet10");
         assertNotNull(p10);

         PortletMetaData p11 = md.getPortlet("Portlet11");
         assertNotNull(p11);

         PortletMetaData p12 = md.getPortlet("Portlet12");
         assertNotNull(p12);
         assertEquals(60, p12.getExpirationCache());

         PortletMetaData p13 = md.getPortlet("Portlet13");
         assertNotNull(p13);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

   @Test
   public void test021()
   {
      try
      {
         String xmlFile = "metadata/portlet/portlet2-jsr286.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());
         
         Locale fr = new Locale("fr");
         Locale default_locale = new Locale(PortletMetaDataConstants.DEFAULT_LOCALE);

         PortletMetaData p1 = md.getPortlet("Portlet1");
         assertNotNull(p1);
         assertEquals("fragmichnicht2", p1.getId());
         assertEquals("Portlet1", p1.getPortletName());
         assertEquals(default_locale, p1.getDescription().getDefaultLocale());
         assertEquals("default", p1.getDescription().getDefaultString());
         assertEquals("defaut", p1.getDescription().getString(fr, false));
         assertEquals(default_locale, p1.getDisplayName().getDefaultLocale());
         assertEquals("display", p1.getDisplayName().getDefaultString());
         assertEquals("affichage", p1.getDisplayName().getString(fr, false));
         assertEquals("org.jboss.portal.test.portlet.TestPortlet", p1.getPortletClass());

         InitParamMetaData ip1 = p1.getInitParams().get(0);
         assertEquals(default_locale, ip1.getDescription().getDefaultLocale());
         assertEquals("first parameter", ip1.getDescription().getDefaultString());
         assertEquals("premier parametre", ip1.getDescription().getString(fr, false));
         assertEquals("one", ip1.getName());
         assertEquals("1", ip1.getValue());

         InitParamMetaData ip2 = p1.getInitParams().get(1);
         assertNotNull(ip2);
         assertEquals("second parameter", ip2.getDescription().getDefaultString());
         assertEquals("deuxieme parametre", ip2.getDescription().getString(fr, false));
         assertEquals("two", ip2.getName());
         assertEquals("2", ip2.getValue());

         // Expiration cache
         assertEquals(0, p1.getExpirationCache());

         SupportsMetaData smd1 = p1.getSupports().get(0);
         assertEquals("text/html", smd1.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd1.getPortletModes().get(0).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("EDIT"), smd1.getPortletModes().get(1).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd1.getPortletModes().get(2).getPortletMode());
         // window state jsr 286
         assertEquals(WindowState.create("MAXIMIZED"), smd1.getWindowStates().get(0).getWindowState());
         assertEquals(WindowState.create("NORMAL"), smd1.getWindowStates().get(1).getWindowState());

         SupportsMetaData smd2 = p1.getSupports().get(1);
         assertEquals("foo", smd2.getId());
         assertEquals("text/wml", smd2.getMimeType());
         assertEquals(org.gatein.pc.api.Mode.create("VIEW"), smd2.getPortletModes().get(0).getPortletMode());
         assertEquals(org.gatein.pc.api.Mode.create("HELP"), smd2.getPortletModes().get(1).getPortletMode());
         // window state jsr 286
         assertEquals(WindowState.create("NORMAL"), smd2.getWindowStates().get(0).getWindowState());
         assertEquals(WindowState.create("CUSTOM"), smd2.getWindowStates().get(1).getWindowState());

         List<SupportedLocaleMetaData> localeList = p1.getSupportedLocale();
         assertEquals(3, localeList.size());
         assertEquals("en", localeList.get(0).getLocale());
         assertEquals("fr", localeList.get(1).getLocale());
         assertEquals("fr_FR", localeList.get(2).getLocale());

         assertEquals("MyResourceBundle", p1.getResourceBundle());

         PortletInfoMetaData pimd = p1.getPortletInfo();
         assertNotNull(pimd);
         assertEquals("very long portlet title", pimd.getTitle());
         assertEquals("short portlet title", pimd.getShortTitle());
         assertEquals("a,b,c,d,e,f", pimd.getKeywords());

         PortletPreferencesMetaData ppmd = p1.getPortletPreferences();
         assertNotNull(ppmd);
         assertEquals("MyValidator", ppmd.getPreferenceValidator());
         assertEquals("1", ppmd.getPortletPreferences().get("one").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("two").getValue().get(0));
         assertEquals(false, ppmd.getPortletPreferences().get("two").isReadOnly());
         assertEquals("3", ppmd.getPortletPreferences().get("three").getValue().get(0));
         assertEquals(true, ppmd.getPortletPreferences().get("three").isReadOnly());
         assertEquals("1", ppmd.getPortletPreferences().get("all").getValue().get(0));
         assertEquals("2", ppmd.getPortletPreferences().get("all").getValue().get(1));
         assertEquals("3", ppmd.getPortletPreferences().get("all").getValue().get(2));

         SecurityRoleRefMetaData srrmd1 = p1.getSecurityRoleRef().get(0);
         assertNotNull(srrmd1);
         assertEquals("role with no link", srrmd1.getDescription().getDefaultString());
         assertEquals("role sans link", srrmd1.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITHOUT_LINK", srrmd1.getRoleName());

         SecurityRoleRefMetaData srrmd2 = p1.getSecurityRoleRef().get(1);
         assertNotNull(srrmd2);
         assertEquals("role with link", srrmd2.getDescription().getDefaultString());
         assertEquals("role avec link", srrmd2.getDescription().getString(fr, false));
         assertEquals("ROLE_NAME_WITH_LINK", srrmd2.getRoleName());
         assertEquals("ROLE_LINK", srrmd2.getRoleLink());

         // JSR 286 properties
         assertEquals(PortletCacheScopeEnum.PUBLIC, p1.getCacheScope());
         assertEquals("http://example.com/testEvents", p1.getSupportedProcessingEvent().get(0).getQname()
               .getNamespaceURI());
         assertEquals("portletEvent", p1.getSupportedProcessingEvent().get(0).getQname().getLocalPart());
         assertEquals("x", p1.getSupportedProcessingEvent().get(0).getQname().getPrefix());
         assertEquals("foo", p1.getSupportedPublicRenderParameters().get(0));
         assertEquals("bar", p1.getSupportedPublicRenderParameters().get(1));
         assertEquals("foo2", p1.getSupportedPublicRenderParameters().get(2));
         assertEquals("foo2bar", p1.getSupportedPublicRenderParameters().get(3));

         assertTrue(p1.getContainerRuntimeOptions().containsKey("option1"));
         assertTrue(p1.getContainerRuntimeOptions().containsKey("option2"));
         assertEquals("value1", p1.getContainerRuntimeOptions().get("option1").getValues().get(0));
         assertEquals("value2", p1.getContainerRuntimeOptions().get("option1").getValues().get(1));
         assertEquals("value3", p1.getContainerRuntimeOptions().get("option2").getValues().get(0));

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

}
