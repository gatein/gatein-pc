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

package org.gatein.pc.embed.renderparameter;

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
public class RenderParameterTestCase extends AbstractTestCase
{

   static int phase;

   @Deployment
   public static WebArchive deployment()
   {
      return deployment(RenderParameterPortlet1.class, RenderParameterPortlet2.class);
   }

   @Drone
   WebDriver driver;

   @Test
   public void init()
   {
      phase = 0;
   }

   @Test
   @RunAsClient
   @InSequence(0)
   public void testSetPortlet1(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = renderURL(deploymentURL, RenderParameterPortlet1.class, RenderParameterPortlet2.class);
      driver.get(url.toString());
   }

   @Test
   @InSequence(1)
   public void testNone()
   {
      Assert.assertNull(RenderParameterPortlet1.foo);
      Assert.assertNull(RenderParameterPortlet2.foo);
      phase = 1;
   }

   @Test
   @RunAsClient
   @InSequence(2)
   public void setPortlet1() throws Exception
   {
      WebElement link = driver.findElement(By.id("url"));
      link.click();
   }

   @Test
   @InSequence(3)
   public void testPortlet1()
   {
      Assert.assertNotNull(RenderParameterPortlet1.foo);
      Assert.assertEquals(Collections.singletonList("foo_value_1"), Arrays.asList(RenderParameterPortlet1.foo));
      Assert.assertNull(RenderParameterPortlet2.foo);
      phase = 2;
   }

   @Test
   @RunAsClient
   @InSequence(4)
   public void setPortlet2() throws Exception
   {
      WebElement link = driver.findElement(By.id("url"));
      link.click();
   }

   @Test
   @InSequence(5)
   public void testPortlet2()
   {
      Assert.assertNotNull(RenderParameterPortlet1.foo);
      Assert.assertEquals(Collections.singletonList("foo_value_1"), Arrays.asList(RenderParameterPortlet1.foo));
      Assert.assertNotNull(RenderParameterPortlet2.foo);
      Assert.assertEquals(Collections.singletonList("foo_value_2"), Arrays.asList(RenderParameterPortlet2.foo));
   }
}
