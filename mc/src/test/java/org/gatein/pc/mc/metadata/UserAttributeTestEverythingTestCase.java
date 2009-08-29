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

import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.UserAttributeMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class UserAttributeTestEverythingTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test01()
   {
      try
      {
         String xmlFile = "metadata/userAttribute/portlet1.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);

         UserAttributeMetaData umb = md.getUserAttributes().get("blub");
         assertNotNull(umb);
         assertEquals("notFoo", umb.getId());
         assertNull(umb.getDescription());

         UserAttributeMetaData umd = md.getUserAttributes().get("foo");
         assertNotNull(umd);
         assertEquals("realFoo", umd.getId());
         assertEquals("foobar", umd.getDescription().getDefaultString());
         assertEquals("fuhbar", umd.getDescription().getString(new Locale("de"), true));

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
         String xmlFile = "metadata/userAttribute/portlet2.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);

         UserAttributeMetaData umb = md.getUserAttributes().get("blub");
         assertNotNull(umb);
         assertEquals("notFoo", umb.getId());
         assertNull(umb.getDescription());

         UserAttributeMetaData umd = md.getUserAttributes().get("foo");
         assertNotNull(umd);
         assertEquals("realFoo", umd.getId());
         assertEquals("foobar", umd.getDescription().getDefaultString());
         assertEquals("fuhbar", umd.getDescription().getString(new Locale("de"), true));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

}
