/*
 * JBoss, a division of Red Hat
 * Copyright 2010, Red Hat Middleware, LLC, and individual
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
package org.gatein.pc.federation;

import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvoker;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletStatus;
import org.gatein.pc.api.info.MetaInfo;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.federation.impl.FederatingPortletInvokerService;
import org.gatein.pc.portlet.support.PortletInvokerSupport;
import org.gatein.pc.portlet.support.info.PortletInfoSupport;
import org.jboss.unit.api.pojo.annotations.Create;
import org.jboss.unit.api.pojo.annotations.Destroy;
import org.jboss.unit.api.pojo.annotations.Test;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import static org.jboss.unit.api.Assert.*;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class FederatingPortletInvokerTestCase
{

   /** . */
   private FederatingPortletInvoker federatingInvoker;

   private FederatedPortletInvoker federatedInvoker;
   private PortletInvokerSupport federatedInvokerDelegate;

   private FederatedPortletInvoker localInvoker;
   private PortletInvokerSupport localInvokerDelegate;

   /** . */
   private Portlet portlet;

   /** . */
   private PortletContext portletContext;

   @Create
   public void setUp() throws Exception
   {
      federatingInvoker = new FederatingPortletInvokerService();

      // create 'foo' invoker and register it with federating service
      federatedInvokerDelegate = new PortletInvokerSupport();
      PortletInfoSupport fooInfo = new PortletInfoSupport();
      fooInfo.getMeta().setDisplayName("FooPortlet");
      portlet = federatedInvokerDelegate.addPortlet("MyPortlet", fooInfo);
      portletContext = portlet.getContext();
      federatedInvoker = federatingInvoker.registerInvoker("foo", federatedInvokerDelegate);
      assertNotNull(federatedInvoker);
      assertEquals("foo", federatedInvoker.getId());

      // create 'local' invoker and register it with federating service
      localInvokerDelegate = new PortletInvokerSupport();
      PortletInfoSupport localInfo = new PortletInfoSupport();
      localInfo.getMeta().setDisplayName("LocalPortlet");
      localInvokerDelegate.addPortlet("MyLocalPortlet", localInfo);
      localInvoker = federatingInvoker.registerInvoker(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, localInvokerDelegate);
      assertNotNull(localInvoker);
      assertEquals(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, localInvoker.getId());
   }

   @Destroy
   public void tearDown() throws Exception
   {
      federatedInvoker = null;
      localInvoker = null;
      federatingInvoker = null;
      portlet = null;
      portletContext = null;
   }

   @Test
   public void testGetPortlets() throws PortletInvokerException
   {
      Set<Portlet> portlets = federatingInvoker.getPortlets();
      assertNotNull(portlets);
      assertEquals(2, portlets.size());

      for (Portlet portlet : portlets)
      {
         PortletContext context = portlet.getContext();
         String id = context.getId();
         assertTrue("foo.MyPortlet".equals(id) || (PortletInvoker.LOCAL_PORTLET_INVOKER_ID + ".MyLocalPortlet").equals(id));
      }
   }

   @Test
   public void testGetStatus() throws PortletInvokerException
   {
      assertEquals(PortletStatus.OFFERED, federatingInvoker.getStatus(PortletContext.createPortletContext("foo.MyPortlet")));
      assertEquals(PortletStatus.OFFERED, federatingInvoker.getStatus(PortletContext.createPortletContext(PortletInvoker.LOCAL_PORTLET_INVOKER_ID + ".MyLocalPortlet")));

      assertEquals(PortletStatus.OFFERED, federatedInvoker.getStatus(PortletContext.createPortletContext("foo.MyPortlet")));
      assertEquals(PortletStatus.OFFERED, localInvoker.getStatus(PortletContext.createPortletContext(PortletInvoker.LOCAL_PORTLET_INVOKER_ID + ".MyLocalPortlet")));
   }

   @Test
   public void testFederation() throws PortletInvokerException
   {
      Collection federateds = federatingInvoker.getFederatedInvokers();
      assertNotNull(federateds);
      assertEquals(2, federateds.size());

      FederatedPortletInvoker federated = federatingInvoker.getFederatedInvoker("foo");
      assertNotNull(federated);
      assertEquals("foo", federated.getId());
      assertEquals(federatedInvoker, federated);
      assertEquals(federatedInvokerDelegate, federated.getPortletInvoker());

      federated = federatingInvoker.getFederatedInvoker(PortletInvoker.LOCAL_PORTLET_INVOKER_ID);
      assertNotNull(federated);
      assertEquals(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, federated.getId());
      assertEquals(localInvoker, federated);
      assertEquals(localInvokerDelegate, federated.getPortletInvoker());
   }

   @Test
   public void testInfo() throws PortletInvokerException
   {
      PortletInfo info = portlet.getInfo();
      assertNotNull(info);
      MetaInfo metaInfo = info.getMeta();
      assertNotNull(metaInfo);
      LocalizedString description = metaInfo.getMetaValue(MetaInfo.DISPLAY_NAME);
      assertNotNull(description);
      assertEquals(Locale.ENGLISH, description.getDefaultLocale());
      assertEquals("FooPortlet", description.getDefaultString());
   }

   @Test
   public void testGetPortletNonFederatedContext() throws PortletInvokerException
   {
      try
      {
         federatingInvoker.getPortlet(portletContext);
         fail("Non federated context shouldn't be resolved by federating service");
      }
      catch (IllegalArgumentException e)
      {
         // expected
      }
   }

   @Test
   public void testGetPortletOnFederatedInvoker() throws PortletInvokerException
   {
      PortletContext federatedContext = PortletContext.createPortletContext(federatedInvoker.getId() + "." + portletContext.getId());
      Portlet samePortlet = federatedInvoker.getPortlet(federatedContext);
      assertNotNull(samePortlet);
      assertEquals(samePortlet.getContext(), federatedContext);
   }

   @Test
   public void testGetLocalPortlets() throws PortletInvokerException
   {
      Set<Portlet> localPortlets = federatingInvoker.getLocalPortlets();
      assertEquals(1, localPortlets.size());
      Portlet localPortlet = localPortlets.iterator().next();
      assertNotNull(localPortlet);
      assertEquals("local.MyLocalPortlet", localPortlet.getContext().getId());
   }

   @Test
   public void testGetRemotePortlets() throws PortletInvokerException
   {
      Set<Portlet> portlets = federatingInvoker.getRemotePortlets();
      assertEquals(1, portlets.size());
      Portlet portlet = portlets.iterator().next();
      assertNotNull(portlet);
      assertEquals("foo.MyPortlet", portlet.getContext().getId());
   }

   @Test
   public void testDelegatedResolution() throws PortletInvokerException
   {
      // create an invoker to check NullInvokerHandler behavior
      final TestFederatedPortletInvoker remote = new TestFederatedPortletInvoker();
      PortletInfoSupport remoteInfo = new PortletInfoSupport();
      remoteInfo.getMeta().setDisplayName("RemotePortlet");
      Portlet portlet = remote.addPortlet("RemotePortlet", remoteInfo);

      // this invoker is not registered
      assertNull(federatingInvoker.getFederatedInvoker("inexistent"));

      federatingInvoker.setNullInvokerHandler(new NullInvokerHandler()
      {
         public FederatedPortletInvoker resolvePortletInvokerFor(String compoundPortletId, String invokerId, FederatingPortletInvoker callingInvoker) throws NoSuchPortletException
         {
            assertEquals(federatingInvoker, callingInvoker);
            return remote;
         }
      });

      assertEquals(portlet, federatingInvoker.getPortlet(PortletContext.createPortletContext("inexistent.RemotePortlet")));
   }

   private class TestFederatedPortletInvoker extends PortletInvokerSupport implements FederatedPortletInvoker
   {

      public String getId()
      {
         return "inexistent";
      }

      @Override
      public Portlet getPortlet(PortletContext portletContext) throws IllegalArgumentException, PortletInvokerException
      {
         // fake dereferencing of compound portlet id
         String portletId = portletContext.getId();
         if (portletId.startsWith(getId() + "."))
         {
            return super.getPortlet(PortletContext.createPortletContext(portletId.substring(portletId.indexOf('.') + 1)));
         }
         else
         {
            throw new NoSuchPortletException(portletId);
         }
      }

      public PortletInvoker getPortletInvoker()
      {
         return null;
      }
   }
}
