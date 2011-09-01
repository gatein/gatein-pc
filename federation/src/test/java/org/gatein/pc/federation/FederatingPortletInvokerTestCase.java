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
package org.gatein.pc.federation;

import junit.framework.TestCase;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class FederatingPortletInvokerTestCase extends TestCase
{

   private static final PortletContext PORTLET = PortletContext.createPortletContext("/webapp.portlet", false);
   private static final String INVOKER_ID = "foo";
   private static final PortletContext LOCAL_PORTLET = PortletContext.createPortletContext("/web.local", false);
   private static final PortletContext REFERENCED_PORTLET = PortletContext.reference(INVOKER_ID, PORTLET);
   private static final PortletContext REFERENCED_LOCAL_PORTLET = PortletContext.reference(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, LOCAL_PORTLET);

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

   public void setUp() throws Exception
   {
      federatingInvoker = new FederatingPortletInvokerService();

      // create 'foo' invoker and register it with federating service
      federatedInvokerDelegate = new PortletInvokerSupport();
      PortletInfoSupport fooInfo = new PortletInfoSupport();
      fooInfo.getMeta().setDisplayName("FooPortlet");
      portlet = federatedInvokerDelegate.addPortlet(PORTLET.getId(), fooInfo);
      portletContext = portlet.getContext();
      federatedInvoker = federatingInvoker.registerInvoker(INVOKER_ID, federatedInvokerDelegate);
      assertNotNull(federatedInvoker);
      assertEquals(INVOKER_ID, federatedInvoker.getId());

      // create 'local' invoker and register it with federating service
      localInvokerDelegate = new PortletInvokerSupport();
      PortletInfoSupport localInfo = new PortletInfoSupport();
      localInfo.getMeta().setDisplayName("LocalPortlet");
      localInvokerDelegate.addPortlet(LOCAL_PORTLET.getId(), localInfo);
      localInvoker = federatingInvoker.registerInvoker(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, localInvokerDelegate);
      assertNotNull(localInvoker);
      assertEquals(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, localInvoker.getId());
   }

   public void tearDown() throws Exception
   {
      federatedInvoker = null;
      localInvoker = null;
      federatingInvoker = null;
      portlet = null;
      portletContext = null;
   }

   public void testGetPortlets() throws PortletInvokerException
   {
      Set<Portlet> portlets = federatingInvoker.getPortlets();
      assertNotNull(portlets);
      assertEquals(2, portlets.size());


      for (Portlet portlet : portlets)
      {
         PortletContext context = portlet.getContext();
         assertTrue(REFERENCED_PORTLET.getId().equals(context.getId()) || REFERENCED_LOCAL_PORTLET.getId().equals(context.getId()));
      }
   }

   public void testGetStatus() throws PortletInvokerException
   {
      assertEquals(PortletStatus.OFFERED, federatingInvoker.getStatus(REFERENCED_PORTLET));
      assertEquals(PortletStatus.OFFERED, federatingInvoker.getStatus(REFERENCED_LOCAL_PORTLET));

      assertEquals(PortletStatus.OFFERED, federatedInvoker.getStatus(REFERENCED_PORTLET));
      assertEquals(PortletStatus.OFFERED, localInvoker.getStatus(REFERENCED_LOCAL_PORTLET));
   }

   public void testFederation() throws PortletInvokerException
   {
      Collection federateds = federatingInvoker.getFederatedInvokerIds();
      assertNotNull(federateds);
      assertEquals(2, federateds.size());

      FederatedPortletInvoker federated = federatingInvoker.getFederatedInvoker(INVOKER_ID);
      assertNotNull(federated);
      assertEquals(INVOKER_ID, federated.getId());
      assertEquals(federatedInvoker, federated);
      assertEquals(federatedInvokerDelegate, federated.getPortletInvoker());

      federated = federatingInvoker.getFederatedInvoker(PortletInvoker.LOCAL_PORTLET_INVOKER_ID);
      assertNotNull(federated);
      assertEquals(PortletInvoker.LOCAL_PORTLET_INVOKER_ID, federated.getId());
      assertEquals(localInvoker, federated);
      assertEquals(localInvokerDelegate, federated.getPortletInvoker());
   }

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

   public void testGetPortletOnFederatedInvoker() throws PortletInvokerException
   {
      PortletContext federatedContext = PortletContext.createPortletContext(federatedInvoker.getId() + PortletContext.INVOKER_SEPARATOR + portletContext.getId());
      Portlet samePortlet = federatedInvoker.getPortlet(federatedContext);
      assertNotNull(samePortlet);
      assertEquals(samePortlet.getContext(), federatedContext);
   }

   public void testGetLocalPortlets() throws PortletInvokerException
   {
      Set<Portlet> localPortlets = federatingInvoker.getLocalPortlets();
      assertEquals(1, localPortlets.size());
      Portlet localPortlet = localPortlets.iterator().next();
      assertNotNull(localPortlet);
      assertEquals(REFERENCED_LOCAL_PORTLET, localPortlet.getContext());
   }

   public void testGetRemotePortlets() throws PortletInvokerException
   {
      Set<Portlet> portlets = federatingInvoker.getRemotePortlets();
      assertEquals(1, portlets.size());
      Portlet portlet = portlets.iterator().next();
      assertNotNull(portlet);
      assertEquals(REFERENCED_PORTLET, portlet.getContext());
   }

   public void testDelegatedResolution() throws PortletInvokerException
   {
      // create an invoker to check NullInvokerHandler behavior
      final TestFederatedPortletInvoker remote = new TestFederatedPortletInvoker();
      PortletInfoSupport remoteInfo = new PortletInfoSupport();
      remoteInfo.getMeta().setDisplayName("RemotePortlet");
      final PortletContext context = PortletContext.createPortletContext("/app.RemotePortlet");
      Portlet portlet = remote.addPortlet(context.getId(), remoteInfo);

      // this invoker is not registered
      final String federatedId = "inexistent";
      assertNull(federatingInvoker.getFederatedInvoker(federatedId));

      federatingInvoker.setNullInvokerHandler(new NullInvokerHandler()
      {
         public FederatedPortletInvoker resolvePortletInvokerFor(String invokerId, FederatingPortletInvoker callingInvoker, String compoundPortletId) throws NoSuchPortletException
         {
            assertEquals(federatingInvoker, callingInvoker);
            return remote;
         }

         public boolean knows(String invokerId)
         {
            return federatedId.equals(invokerId);
         }

         public Collection<String> getKnownInvokerIds()
         {
            return Collections.singletonList(federatedId);
         }
      });

      assertEquals(portlet, federatingInvoker.getPortlet(PortletContext.createPortletContext(federatedId + PortletContext.INVOKER_SEPARATOR + context.getId())));
      assertEquals(portlet, federatingInvoker.getPortlet(PortletContext.reference(federatedId, context)));
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
         if (portletId.startsWith(getId() + PortletContext.INVOKER_SEPARATOR))
         {
            return super.getPortlet(PortletContext.createPortletContext(portletId.substring(portletId.indexOf(PortletContext.INVOKER_SEPARATOR) + 1)));
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
