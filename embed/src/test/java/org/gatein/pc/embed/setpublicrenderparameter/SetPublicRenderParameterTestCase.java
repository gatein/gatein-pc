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

package org.gatein.pc.embed.setpublicrenderparameter;

import junit.framework.Assert;
import org.gatein.pc.embed.AbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class SetPublicRenderParameterTestCase extends AbstractTestCase
{

   @Deployment
   public static WebArchive deployment()
   {
      return deployment(PORTLET_APP_PROLOG +
         "<portlet>" +
         "<portlet-name>" + SetPublicRenderParameterPortlet1.class.getSimpleName() + "</portlet-name>" +
         "<portlet-class>" + SetPublicRenderParameterPortlet1.class.getName() + "</portlet-class>" +
         "<portlet-info>" +
         "<title>" + SetPublicRenderParameterPortlet1.class.getSimpleName() + "</title>" +
         "</portlet-info>" +
         "<supported-public-render-parameter>foo</supported-public-render-parameter>" +
         "</portlet>" +
         "<portlet>" +
         "<portlet-name>" + SetPublicRenderParameterPortlet2.class.getSimpleName() + "</portlet-name>" +
         "<portlet-class>" + SetPublicRenderParameterPortlet2.class.getName() + "</portlet-class>" +
         "<portlet-info>" +
         "<title>" + SetPublicRenderParameterPortlet2.class.getSimpleName() + "</title>" +
         "</portlet-info>" +
         "<supported-public-render-parameter>foo</supported-public-render-parameter>" +
         "</portlet>" +
         "<public-render-parameter>\n" +
         "<identifier>foo</identifier>\n" +
         "<name>Foo</name>\n" +
         "</public-render-parameter>\n" +
         PORTLET_APP_EPILOG);
   }

   @Drone
   WebDriver driver;

   @Test
   @InSequence(0)
   public void ini() throws Exception
   {
      Assert.assertNull(SetPublicRenderParameterPortlet1.foo);
      Assert.assertNull(SetPublicRenderParameterPortlet2.foo);
   }

   @Test
   @RunAsClient
   @InSequence(1)
   public void setPortlet1(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = renderURL(deploymentURL, SetPublicRenderParameterPortlet1.class, SetPublicRenderParameterPortlet2.class);
      driver.get(url.toString());
      WebElement link = driver.findElement(By.id("url"));
      link.click();
   }

   @Test
   @InSequence(2)
   public void testPortlets()
   {
      Assert.assertNotNull(SetPublicRenderParameterPortlet1.foo);
      Assert.assertEquals(Collections.singletonList("foo_value"), Arrays.asList(SetPublicRenderParameterPortlet1.foo));
      Assert.assertNotNull(SetPublicRenderParameterPortlet1.foo);
      Assert.assertEquals(Collections.singletonList("foo_value"), Arrays.asList(SetPublicRenderParameterPortlet1.foo));
   }
}
