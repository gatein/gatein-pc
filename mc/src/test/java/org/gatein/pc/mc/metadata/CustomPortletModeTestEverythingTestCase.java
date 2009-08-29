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

import java.util.Locale;

import org.gatein.pc.portlet.impl.metadata.CustomPortletModeMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class CustomPortletModeTestEverythingTestCase extends AbstractMetaDataTestCase
{

   
   @Test
   public void test01()
   {
      try
      {
         String xmlFile = "metadata/customPortletMode/portlet1.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());
         assertNotNull(md.getCustomPortletModes());

         CustomPortletModeMetaData cmd1 = md.getCustomPortletModes().get("Custom");
         assertNotNull(cmd1);
         assertEquals("mode1", cmd1.getId());
         assertEquals("Custom", cmd1.getPortletMode());
         assertEquals("portletMode1", cmd1.getDescription().getDefaultString());
         assertEquals("eigener portlet modus", cmd1.getDescription().getString(new Locale("de"), false));

         assertNotNull(md.getCustomPortletModes().get("Custom2"));
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

         String xmlFile = "metadata/customPortletMode/portlet2.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());

         CustomPortletModeMetaData cmd1 = md.getCustomPortletModes().get("Custom");
         assertNotNull(cmd1);
         assertEquals("Custom", cmd1.getPortletMode());
         assertEquals("portletMode1", cmd1.getDescription().getDefaultString());         
         assertEquals(true, cmd1.isPortalManaged());
         assertEquals("cmode1", cmd1.getId());
         
         CustomPortletModeMetaData cmd2 = md.getCustomPortletModes().get("Custom2");
         assertNotNull(cmd2);
         assertEquals("Custom2", cmd2.getPortletMode());
         assertEquals(false, cmd2.isPortalManaged());

         CustomPortletModeMetaData cmd3 = md.getCustomPortletModes().get("Custom3");
         assertNotNull(cmd3);
         assertEquals("Custom3", cmd3.getPortletMode());

         // default value
         assertEquals(true, cmd3.isPortalManaged());
         assertEquals("eigener portlet modus", cmd1.getDescription().getString(new Locale("de"), false));
         assertEquals("Portlet Mode Three", cmd3.getDescription().getDefaultString());


      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

   @Test
   public void test03()
   {
      try
      {

         String xmlFile = "metadata/customPortletMode/portlet1-fail.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         fail("portlet 2.0 properties are not allowed");
      }
      catch (Exception e)
      {
         // OK
      }
   }

}
