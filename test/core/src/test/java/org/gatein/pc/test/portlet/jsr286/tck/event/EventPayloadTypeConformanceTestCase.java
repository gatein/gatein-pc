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
package org.gatein.pc.test.portlet.jsr286.tck.event;

import org.gatein.pc.test.unit.annotations.TestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.PortletTestContext;
import org.gatein.pc.test.unit.actions.PortletRenderTestAction;
import org.gatein.pc.test.unit.actions.PortletActionTestAction;
import org.gatein.pc.test.unit.actions.PortletEventTestAction;
import org.gatein.pc.test.portlet.framework.UTP6;
import org.jboss.unit.driver.DriverResponse;
import org.jboss.unit.driver.response.EndTestResponse;
import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.remote.driver.handler.http.response.InvokeGetResponse;

import javax.portlet.RenderRequest;
import javax.portlet.Portlet;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Event;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.net.URI;
import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
@TestCase({Assertion.JSR286_139})
public class EventPayloadTypeConformanceTestCase
{

   private static final URI uri = new File("").toURI();
   private static final UUID uuid = UUID.randomUUID();
   private static final Calendar calendar = Calendar.getInstance();
   private static final Date date = new Date();
   private static final QName name = new QName("foons", "foolocalname");

   /** . */
   private final List<Serializable> payloads = new ArrayList<Serializable>();

   {
      payloads.add(true);
      payloads.add(1); // int
      payloads.add((byte)2);
      payloads.add((long)3);
      payloads.add((float)4);
      payloads.add((double)5);
      payloads.add("somestring");
      payloads.add(uri);
      payloads.add(uuid);
      payloads.add(new BigInteger("6"));
      payloads.add(new BigDecimal("7"));
      payloads.add(calendar);
      payloads.add(date);
      payloads.add(name);
      payloads.add(new JAXBSerializable());
   }

   public EventPayloadTypeConformanceTestCase(PortletTestCase seq)
   {
      seq.bindAction(0, UTP6.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            return new InvokeGetResponse(response.createActionURL().toString());
         }
      });
      seq.bindAction(1, UTP6.ACTION_JOIN_POINT, new PortletActionTestAction()
      {
         protected void run(Portlet portlet, ActionRequest request, ActionResponse response, PortletTestContext context) throws PortletException, IOException
         {
            // Now try with a non JAXB serializable class
            try
            {
               response.setEvent("Bar", new Serializable()
               {
               });
               fail("Was expecting an IAE");
            }
            catch (IllegalArgumentException ignore)
            {
            }

            // Now try a bunch of classes
            for (Serializable payload : payloads)
            {
               response.setEvent("Bar", payload);
            }
         }
      });
      seq.bindAction(1, UTP6.EVENT_JOIN_POINT, new PortletEventTestAction()
      {
         protected void run(Portlet portlet, EventRequest request, EventResponse response, PortletTestContext context) throws PortletException, IOException
         {
            Event event = request.getEvent();
            if ("Bar".equals(event.getName()) && event.getValue() != null)
            {
               payloads.remove(event.getValue());
            }
         }
      });
      seq.bindAction(1, UTP6.RENDER_JOIN_POINT, new PortletRenderTestAction()
      {
         protected DriverResponse run(Portlet portlet, RenderRequest request, RenderResponse response, PortletTestContext context)
         {
            assertEquals(0, payloads.size());
            return new EndTestResponse();
         }
      });
   }
}