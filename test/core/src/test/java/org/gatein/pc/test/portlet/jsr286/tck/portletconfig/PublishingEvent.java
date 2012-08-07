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
package org.gatein.pc.test.portlet.jsr286.tck.portletconfig;

import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.web.AbstractUniversalTestPortlet;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.web.UTP2;
import org.gatein.common.util.Tools;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import static org.gatein.pc.test.unit.Assert.*;

import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletConfig;
import javax.xml.namespace.QName;
import java.util.Enumeration;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({
   Assertion.JSR286_30,
   Assertion.JSR286_31,
   Assertion.JSR286_32,
   Assertion.JSR286_33
   })
public class PublishingEvent
{

   static final Set<QName> EVENT_NAMES = Collections.unmodifiableSet(Tools.toSet(
      new QName("urn:explicit-namespace-1", "event1"),
      new QName("urn:explicit-namespace-2", "event2"),
      new QName("urn:default-namespace", "event3"),
      new QName("urn:default-namespace", "event4"),
      new QName("urn:default-namespace", "event5"),
      new QName("urn:default-namespace", "event6")
   ));


   public PublishingEvent(PortletTestCase seq)
   {
      seq.bindAction(0, UTP2.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected Response run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            PortletConfig cfg = ((AbstractUniversalTestPortlet)portlet).getPortletConfig();

            //
            Enumeration<QName> publishingEvents = cfg.getPublishingEventQNames();
            assertNotNull(publishingEvents);
            List<QName> publishingEventList = Tools.toList(publishingEvents);
            assertEquals(EVENT_NAMES.size(), publishingEventList.size());
            assertEquals(EVENT_NAMES, new HashSet<QName>(publishingEventList));

            //
            Enumeration<QName> processingEvents = cfg.getProcessingEventQNames();
            assertNotNull(processingEvents);
            assertFalse(processingEvents.hasMoreElements());

            //
            return new EndTestResponse();
         }
      });
   }
}
