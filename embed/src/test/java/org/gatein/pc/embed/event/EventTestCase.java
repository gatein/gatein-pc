/*
 * Copyright (C) 2012 eXo Platform SAS.
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

package org.gatein.pc.embed.event;

import org.gatein.pc.embed.AbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.xml.namespace.QName;
import java.net.URL;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class EventTestCase extends AbstractTestCase
{

   @Deployment
   public static WebArchive deployment()
   {
      return deployment(PORTLET_APP_PROLOG +
         "<portlet>" +
         "<portlet-name>" + EventPortlet.class.getSimpleName() + "</portlet-name>" +
         "<portlet-class>" + EventPortlet.class.getName() + "</portlet-class>" +
         "<portlet-info>" +
         "<title>" + EventPortlet.class.getSimpleName() + "</title>" +
         "</portlet-info>" +
         "<supported-processing-event>" +
         "<qname>foo</qname>" +
         "</supported-processing-event>" +
         "</portlet>" +
         "<event-definition>" +
         "<qname>foo</qname>" +
         "<value-type>java.lang.String</value-type>" +
         "</event-definition>" +
         PORTLET_APP_EPILOG);
   }

   @Drone
   WebDriver driver;

   @Test
   @InSequence(0)
   public void init()
   {
      Assert.assertEquals(0, EventPortlet.count);
      Assert.assertNull(EventPortlet.qname);
      Assert.assertNull(EventPortlet.payload);
   }

   @Test
   @RunAsClient
   @InSequence(1)
   public void testInteraction(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = deploymentURL.toURI().resolve("embed/EventPortlet").toURL();
      driver.get(url.toString());
      WebElement link = driver.findElement(By.id("url"));
      link.click();
   }

   @Test
   @InSequence(2)
   public void testInvoked()
   {
      Assert.assertEquals(1, EventPortlet.count);
      Assert.assertEquals(new QName("foo"), EventPortlet.qname);
      Assert.assertEquals("bar", EventPortlet.payload);
   }
}
