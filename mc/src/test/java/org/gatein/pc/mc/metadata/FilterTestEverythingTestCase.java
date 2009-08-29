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

import org.gatein.pc.api.LifeCyclePhase;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.common.InitParamMetaData;
import org.gatein.pc.portlet.impl.metadata.filter.FilterMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class FilterTestEverythingTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test01()
   {
      try
      {
         unmarshall10("metadata/filter/portlet-filter1.xml");
         // no filters in jsr 168
         fail();
      }
      catch (Exception e)
      {
         // ok
      }
   }

   @Test
   public void test02()
   {
      try
      {
         String xmlFile = "metadata/filter/portlet-filter2.xml";

         PortletApplication20MetaData md = unmarshall20(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);

         FilterMetaData filter = md.getFilter("testFilter");
         assertNotNull(filter);
         assertEquals("org.jboss.portal.meta.NoExistingClass", filter.getFilterClass());
         assertEquals("testFilter", filter.getFilterName());
         assertEquals(LifeCyclePhase.ACTION, filter.getLifecycle().get(0));
         assertEquals(LifeCyclePhase.RENDER, filter.getLifecycle().get(1));
         
         assertEquals("test", filter.getDescription().getDefaultString());
         assertEquals("bla", filter.getDescription().getString(new Locale("de"), false));

         assertEquals("foo", filter.getDisplayName().getString(new Locale("fr"), false));
         assertEquals("foobar", filter.getDisplayName().getDefaultString());
         
         InitParamMetaData ip = filter.getInitParams().get(0);
         assertEquals("eins", ip.getId());
         assertEquals("foo", ip.getName());
         assertEquals("bar", ip.getValue());
         assertNotNull(ip.getDescription());
         
         InitParamMetaData ip2 = filter.getInitParams().get(1);
         assertEquals("test", ip2.getName());
         assertEquals("testing", ip2.getValue());
         assertNull(ip2.getId());
         
         // 
         FilterMetaData filter2 = md.getFilter("testFilterZwei");
         assertEquals("testFilterZwei", filter2.getFilterName());
         assertEquals(LifeCyclePhase.ACTION, filter2.getLifecycle().get(0));
         
         // Filter mapping            
         assertEquals("Portlet1", md.getFilterMapping().get(0).getPortletNames().get(0));
         assertEquals("Portlet2", md.getFilterMapping().get(0).getPortletNames().get(1));

         assertEquals("Portlet2", md.getFilterMapping().get(1).getPortletNames().get(0));

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

}
