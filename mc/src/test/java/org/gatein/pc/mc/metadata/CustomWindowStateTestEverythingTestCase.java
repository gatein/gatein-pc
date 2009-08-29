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

import org.gatein.pc.portlet.impl.metadata.CustomWindowStateMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class CustomWindowStateTestEverythingTestCase extends AbstractMetaDataTestCase
{
   @Test
   public void test01()
   {
      try
      {

         String xmlFile = "metadata/customWindowState/portlet1.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());

         CustomWindowStateMetaData cws1 = md.getCustomWindowStates().get("windowState1");
         assertEquals("WindowState", cws1.getDescription().getDefaultString());
         assertEquals("windowState1", cws1.getWindowState());
         assertEquals("Offenes Fenster", cws1.getDescription().getString(new Locale("de"), false));
         assertEquals("foo", cws1.getId());
         CustomWindowStateMetaData cws2 = md.getCustomWindowStates().get("windowState2");
         assertNotNull(cws2);

         CustomWindowStateMetaData cws3 = md.getCustomWindowStates().get("windowState3");
         assertEquals("drei", cws3.getId());
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

         String xmlFile = "metadata/customWindowState/portlet2.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());

         CustomWindowStateMetaData cws1 = md.getCustomWindowStates().get("windowState1");
         assertEquals("WindowState", cws1.getDescription().getDefaultString());
         assertEquals("windowState1", cws1.getWindowState());
         assertEquals("Offenes Fenster", cws1.getDescription().getString(new Locale("de"), false));
         assertEquals("foo", cws1.getId());
         
         CustomWindowStateMetaData cws2 = md.getCustomWindowStates().get("windowState2");
         assertNotNull(cws2);

         CustomWindowStateMetaData cws3 = md.getCustomWindowStates().get("windowState3");
         assertEquals("drei", cws3.getId());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

}
