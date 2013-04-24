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

package org.gatein.pc.embed.resourceheader;

import junit.framework.Assert;
import org.gatein.pc.embed.AbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class ResourceHeaderTestCase extends AbstractTestCase
{

   @Deployment()
   public static WebArchive deployment()
   {
      return deployment(ResourceHeaderPortlet.class);
   }

   @Drone
   WebDriver driver;

   @Test
   @RunAsClient
   public void testInteraction(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = renderURL(deploymentURL, ResourceHeaderPortlet.class);
      driver.get(url.toString());
      WebElement link = driver.findElement(By.id("url"));
      url = new URL(link.getAttribute("href"));
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setInstanceFollowRedirects(false);
      conn.connect();
      Assert.assertEquals(500, conn.getResponseCode());
      Map<String, String> headers = responseHeaders(conn);
      Assert.assertTrue(headers.containsKey("foo"));
      Assert.assertEquals("bar", headers.get("foo"));
   }
}
