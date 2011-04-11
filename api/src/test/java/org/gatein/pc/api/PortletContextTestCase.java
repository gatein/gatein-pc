/*
 * JBoss, a division of Red Hat
 * Copyright 2011, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.gatein.pc.api;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class PortletContextTestCase extends TestCase
{
   public void testGetComponents()
   {
      PortletContext context = PortletContext.createPortletContext("/applicationName.portletName");
      assertEquals("/applicationName.portletName", context.getId());
      PortletContext.PortletContextComponents components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portletName", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      context = PortletContext.createPortletContext("\t\t\n    /applicationName.portletName   \t");
      assertEquals("/applicationName.portletName", context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portletName", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      try
      {
         PortletContext.createPortletContext("/");
         fail("invalid");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }

      String portletId = "applicationName" + PortletContext.INVOKER_SEPARATOR + "portletName";
      context = PortletContext.createPortletContext(portletId);
      assertEquals(portletId, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("applicationName", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertEquals("portletName", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      context = PortletContext.createPortletContext("/applicationName.portlet.Name");
      assertEquals("/applicationName.portlet.Name", context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portlet.Name", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      try
      {
         PortletContext.createPortletContext("/.");
         fail();
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }

      context = PortletContext.createPortletContext("/  applicationName\t.  portlet Name");
      assertEquals("/applicationName.portlet Name", context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portlet Name", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());
   }

   public void testPortletContextWithInvokerId()
   {
      String id = "local" + PortletContext.INVOKER_SEPARATOR + "/foo.bar";
      PortletContext context = PortletContext.createPortletContext(id);
      assertEquals(id, context.getId());
      PortletContext.PortletContextComponents components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("local", components.getInvokerName());
      assertEquals("foo", components.getApplicationName());
      assertEquals("bar", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      context = PortletContext.createPortletContext("   local\t  " + PortletContext.INVOKER_SEPARATOR + "  /  foo \t. \t\n bar");
      assertEquals("local" + PortletContext.INVOKER_SEPARATOR + "/foo.bar", context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("local", components.getInvokerName());
      assertEquals("foo", components.getApplicationName());
      assertEquals("bar", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      id = "local" + PortletContext.INVOKER_SEPARATOR + "foo.bar";
      context = PortletContext.createPortletContext(id);
      assertEquals(id, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("local", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertEquals("foo.bar", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());

      id = "local" + PortletContext.INVOKER_SEPARATOR + "/foo";
      context = PortletContext.createPortletContext(id);
      assertEquals(id, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("local", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertEquals("/foo", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());
   }

   public void testCreateFromAppAndPortletName()
   {
      PortletContext fromId = PortletContext.createPortletContext("/applicationName.portletName");

      PortletContext context = PortletContext.createPortletContext("applicationName", "portletName");
      assertEquals("/applicationName.portletName", context.getId());
      PortletContext.PortletContextComponents components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portletName", components.getPortletName());
      assertEquals(context, fromId);
      assertFalse(components.isCloned());
      assertNull(components.getStateId());
   }

   public void testShouldProperlyHandleApplicationNameStartingWithSlash()
   {
      PortletContext fromId = PortletContext.createPortletContext("/applicationName.portletName");

      PortletContext context = PortletContext.createPortletContext("/applicationName", "portletName");
      assertEquals("/applicationName.portletName", context.getId());
      PortletContext.PortletContextComponents components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertEquals("applicationName", components.getApplicationName());
      assertEquals("portletName", components.getPortletName());
      assertFalse(components.isCloned());
      assertNull(components.getStateId());
      assertEquals(context, fromId);
   }

   public void testShouldWorkWithoutInterpretation()
   {
      PortletContext context = PortletContext.createPortletContext("foo", false);
      PortletContext.PortletContextComponents components = context.getComponents();
      assertNotNull(components);
      assertFalse(components.isInterpreted());
      assertEquals("foo", context.getId());
      assertEquals("foo", components.getPortletName());
      assertEquals(components.getId(), context.getId());

      try
      {
         components.getApplicationName();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }

      try
      {
         components.getInvokerName();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }

      try
      {
         components.getStateId();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }

      try
      {
         components.isCloned();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }

      try
      {
         components.isConsumerCloned();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }

      try
      {
         components.isProducerCloned();
         fail("Unintrepreted so only component available is portlet name");
      }
      catch (IllegalStateException e)
      {
         // expected
      }
   }

   public void testAcceptProducerClones()
   {
      PortletContext context;
      PortletContext.PortletContextComponents components;

      checkClones(PortletContext.PRODUCER_CLONE_ID_PREFIX);

      String id = "foo" + PortletContext.INVOKER_SEPARATOR + PortletContext.CONSUMER_CLONE_ID;
      context = PortletContext.createPortletContext(id);
      assertEquals(id, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("foo", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertNull(components.getPortletName());
      assertTrue(components.isCloned());
      assertEquals(PortletContext.CONSUMER_CLONE_DUMMY_STATE_ID, components.getStateId());
   }

   private void checkClones(String clonePrefix)
   {
      PortletContext context;
      PortletContext.PortletContextComponents components;
      context = PortletContext.createPortletContext(clonePrefix + "clone");
      assertEquals(clonePrefix + "clone", context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertNull(components.getInvokerName());
      assertNull(components.getApplicationName());
      assertNull(components.getPortletName());
      assertTrue(components.isCloned());
      assertEquals("clone", components.getStateId());

      String id = "foo" + PortletContext.INVOKER_SEPARATOR + clonePrefix + "clone";
      context = PortletContext.createPortletContext(id);
      assertEquals(id, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("foo", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertNull(components.getPortletName());
      assertTrue(components.isCloned());
      assertEquals("clone", components.getStateId());

      context = PortletContext.createPortletContext("foo \t  \n  " + PortletContext.INVOKER_SEPARATOR + "   \t" + clonePrefix + "   \t\nclone");
      assertEquals(id, context.getId());
      components = context.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals("foo", components.getInvokerName());
      assertNull(components.getApplicationName());
      assertNull(components.getPortletName());
      assertTrue(components.isCloned());
      assertEquals("clone", components.getStateId());
   }

   public void testAcceptConsumerClones()
   {
      checkClones(PortletContext.CONSUMER_CLONE_ID_PREFIX);
   }

   public void testAcceptLocalConsumerClone()
   {
      PortletContext.PortletContextComponents components = PortletContext.LOCAL_CONSUMER_CLONE.getComponents();
      assertNotNull(components);
      assertTrue(components.isInterpreted());
      assertEquals(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, components.getInvokerName());
      assertNull(components.getApplicationName());
      assertNull(components.getPortletName());
      assertTrue(components.isCloned());
      assertEquals(PortletContext.CONSUMER_CLONE_DUMMY_STATE_ID, components.getStateId());
   }

   public void testCreateFromNullOrEmpty()
   {
      try
      {
         PortletContext.createPortletContext(null);
         fail("Attempting to create a PortletContext from null should have thrown an exception.");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }

      try
      {
         PortletContext.createPortletContext("");
         fail("Attempting to create a PortletContext from empty String should have thrown an exception.");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }
}
