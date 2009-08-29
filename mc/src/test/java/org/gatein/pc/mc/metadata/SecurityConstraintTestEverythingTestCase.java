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

import org.gatein.pc.api.TransportGuarantee;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.metadata.security.SecurityConstraintMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;
/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class SecurityConstraintTestEverythingTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test01()
   {
      try
      {

         String xmlFile = "metadata/security/portlet1.xml";

         PortletApplication10MetaData md = unmarshall10(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication10MetaData);
         assertEquals("1.0", md.getVersion());

         SecurityConstraintMetaData scd1 = md.getSecurityConstraints().get(0);
         assertNotNull(scd1);
         assertEquals("test", scd1.getDisplayName().getDefaultString());
         assertEquals("Test", scd1.getDisplayName().getString(new Locale("de"), false));
         assertEquals(TransportGuarantee.NONE, scd1.getUserDataConstraint().getTransportQuarantee());
         
         assertEquals("foo", scd1.getPortletList().getPortletNames().get(0));
         assertEquals("foobar", scd1.getPortletList().getPortletNames().get(1));
         assertEquals("foo", scd1.getId());
         
         SecurityConstraintMetaData scd2 = md.getSecurityConstraints().get(1);
         assertNotNull(scd2);
         assertEquals(TransportGuarantee.INTEGRAL, scd2.getUserDataConstraint().getTransportQuarantee());
         assertEquals("foo", scd2.getPortletList().getPortletNames().get(0));
         assertEquals("fooConstraint", scd1.getUserDataConstraint().getDescription().getDefaultString());
         assertEquals("FooConstraint", scd1.getUserDataConstraint().getDescription().getString(new Locale("de"), false));

         try
         {
            scd2.getPortletList().getPortletNames().get(1);
            fail("2nd security constraint should only contain one element,");
         }
         catch (IndexOutOfBoundsException e)
         {
            // expected java.lang.IndexOutOfBoundsException
         }

         PortletMetaData pmd1 = md.getPortlet("foo");
         assertNotNull(pmd1);

         PortletMetaData pmd2 = md.getPortlet("foobar");
         assertNotNull(pmd2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

   /*
   public void test011()
   {
      try
      {

         String xmlFile = "security/portlet1-fail1.xml";
         String namespace = PortletMetaDataConstants.PORTLET_JSR_168_NS;
         
         unmarshall(xmlFile, namespace, PortletApplication10MetaData.class);
   
         fail();         
      }
      catch(Exception e)
      {
         // OK
      }
   }

   public void test012()
   {
      try
      {

         String xmlFile = "security/portlet1-fail1.xml";
         String namespace = PortletMetaDataConstants.PORTLET_JSR_168_NS;
         
         unmarshall(xmlFile, namespace, PortletApplication10MetaData.class);
         fail();         
      }
      catch(Exception e)
      {
         // OK
      }
   }
   */

   @Test
   public void test02()
   {
      try
      {

         String xmlFile = "metadata/security/portlet2.xml";

         PortletApplication10MetaData md = unmarshall10( xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());

         SecurityConstraintMetaData scd1 = md.getSecurityConstraints().get(0);
         assertNotNull(scd1);
         assertEquals("test", scd1.getDisplayName().getDefaultString());
         assertEquals("Test", scd1.getDisplayName().getString(new Locale("de"), false));
         assertEquals("foo", scd1.getId());
         
         assertEquals(TransportGuarantee.NONE, scd1.getUserDataConstraint().getTransportQuarantee());
         assertEquals("foo", scd1.getPortletList().getPortletNames().get(0));
         assertEquals("foobar", scd1.getPortletList().getPortletNames().get(1));
         

         SecurityConstraintMetaData scd2 = md.getSecurityConstraints().get(1);
         assertNotNull(scd2);
         assertEquals(TransportGuarantee.INTEGRAL, scd2.getUserDataConstraint().getTransportQuarantee());
         assertEquals("foo", scd2.getPortletList().getPortletNames().get(0));
         assertEquals("fooConstraint", scd1.getUserDataConstraint().getDescription().getDefaultString());
         assertEquals("FooConstraint", scd1.getUserDataConstraint().getDescription().getString(new Locale("de"), false));
         
         try
         {
            scd2.getPortletList().getPortletNames().get(1);
            fail("2nd security constraint should only contain one element,");
         }
         catch (IndexOutOfBoundsException e)
         {
            // expected java.lang.IndexOutOfBoundsException
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }
   /*  
     public void test021()
     {
        try
        {

           String xmlFile = "security/portlet2-fail1.xml";
           String namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS;
           
           unmarshall(xmlFile, namespace, PortletApplication20MetaData.class);
           fail();  
        }
        catch(Exception e)
        {
           // OK
        }
     }
     
     public void test022()
     {
        try
        {

           String xmlFile = "security/portlet2-fail2.xml";
           String namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS;
           
           unmarshall(xmlFile, namespace, PortletApplication20MetaData.class);
           fail();  
        }
        catch(Exception e)
        {
           // OK
        }
     }
     */
}
