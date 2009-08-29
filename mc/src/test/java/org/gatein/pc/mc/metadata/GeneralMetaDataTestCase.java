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

import java.net.URI;
import java.util.Locale;

import org.gatein.pc.portlet.impl.metadata.ListenerMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.PublicRenderParameterMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class GeneralMetaDataTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test01()
   {
      try
      {
         PortletApplication10MetaData md = this.unmarshall10("metadata/general/portlet1.xml");
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("No exception expected");
      }
   }

   @Test
   public void test02()
   {
      try
      {
         PortletApplication10MetaData md = this.unmarshall10("metadata/general/portlet2.xml");
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());
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
         PortletApplication20MetaData md = unmarshall20("metadata/general/portlet2-jsr286.xml");
         assertEquals("2.0", md.getVersion());
         assertTrue(md instanceof PortletApplication20MetaData);

         assertEquals("MyResourceBundle", md.getResourceBundle());
         assertEquals(new URI("foobar"), md.getDefaultNamespace());
         
         PublicRenderParameterMetaData prp1 = md.getPublicRenderParameters().get(0);

         assertEquals("Public render parameter one", prp1.getDescription().getDefaultString());
         assertEquals("param1", prp1.getId());
         assertEquals("param1", prp1.getName());
         assertEquals("Parameter1", prp1.getAlias().get(0).getLocalPart());
         assertEquals("Parameter2", prp1.getAlias().get(1).getLocalPart());
         assertEquals("blub", prp1.getIdentifier());
         
         assertEquals("foobar", md.getContainerRuntimeOption("foo").getValues().get(0));
         assertEquals("foobar2", md.getContainerRuntimeOption("foo").getValues().get(1));

         ListenerMetaData listener1 = md.getListeners().get(0);
         assertNotNull(listener1);
         assertEquals("listener.MyListener", listener1.getListenerClass());
         assertEquals("Mein Zuh\u00f6rer Eins", listener1.getDisplayName().getString(new Locale("de"), false));
         assertEquals("Beschreibung", listener1.getDescription().getString(new Locale("de"), false));
            
         assertNotNull(md.getListeners().get(1));
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }
   
   @Test
   public void test01_generated()
   {
      try
      {
         PortletApplication10MetaData md = unmarshall10("metadata/general/portlet-app_1_0.xml");

         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());
         assertNotNull(md.getCustomPortletModes());

      }
      catch(Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }
   
   @Test
   public void test02_generated()
   {
      try
      {
         PortletApplication10MetaData md = this.unmarshall10("metadata/general/portlet-app_2_0.xml");
         assertEquals("2.0", md.getVersion());
         assertTrue(md instanceof PortletApplication20MetaData);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }
}
