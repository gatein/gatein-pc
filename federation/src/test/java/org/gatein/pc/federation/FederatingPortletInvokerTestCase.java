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
package org.gatein.pc.federation;

import org.gatein.pc.federation.FederatedPortletInvoker;
import org.gatein.pc.federation.FederatingPortletInvoker;
import org.gatein.pc.federation.impl.FederatingPortletInvokerService;
import org.gatein.pc.portlet.support.PortletInvokerSupport;
import org.gatein.pc.portlet.support.PortletSupport;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.MetaInfo;
import org.gatein.pc.portlet.support.info.PortletInfoSupport;
import org.gatein.common.i18n.LocalizedString;

import java.util.Collection;
import java.util.Set;
import java.util.Locale;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Create;
import org.jboss.unit.api.pojo.annotations.Destroy;
import org.jboss.unit.api.pojo.annotations.Test;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class FederatingPortletInvokerTestCase
{

   /** . */
   private FederatingPortletInvoker federatingInvoker;

   /** . */
   private PortletInvokerSupport federatedInvoker;

   /** . */
   private PortletSupport federatedPortlet;

   /** . */
   private Portlet portlet;

   /** . */
   private PortletContext portletContext;

   @Create
   public void setUp() throws Exception
   {
      federatingInvoker = new FederatingPortletInvokerService();
      federatedInvoker = new PortletInvokerSupport();

      // Configure
      PortletInfoSupport fooInfo = new PortletInfoSupport();
      fooInfo.getMeta().setDisplayName("FooPortlet");

      // Wire
      federatedPortlet = federatedInvoker.addPortlet("MyPortlet", fooInfo);
      federatingInvoker.registerInvoker("foo", federatedInvoker);

      // Basic setup
      Set portlets = federatingInvoker.getPortlets();
      assertNotNull(portlets);
      assertEquals(1, portlets.size());
      portlet = (Portlet)portlets.iterator().next();
      assertNotNull(portlet);
      portletContext = portlet.getContext();
      assertNotNull(portletContext);
   }

   @Destroy
   public void tearDown() throws Exception
   {
      federatedPortlet = null;
      federatedInvoker = null;
      federatingInvoker = null;
      portlet = null;
      portletContext = null;
   }

   @Test
   public void testFederation() throws PortletInvokerException
   {
      Collection federateds = federatingInvoker.getFederatedInvokers();
      assertNotNull(federateds);
      assertEquals(1, federateds.size());
      FederatedPortletInvoker federated = (FederatedPortletInvoker)federateds.iterator().next();
      assertNotNull(federated);
      assertEquals("foo", federated.getId());
      assertEquals(federatedInvoker, federated.getPortletInvoker());
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
   public void testGetPortlet() throws PortletInvokerException
   {
      Portlet samePortlet = federatingInvoker.getPortlet(portletContext);
      assertNotNull(samePortlet);
      assertEquals(samePortlet.getContext(), portletContext);
   }

   public void testInvoke() throws PortletInvokerException
   {
   }
}
