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

package org.gatein.pc.embed.htmlheader;

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

import java.net.URL;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class HtmlHeaderTestCase extends AbstractTestCase
{

   @Deployment()
   public static WebArchive deployment()
   {
      return deployment(HtmlHeaderPortlet.class);
   }

   @Drone
   WebDriver driver;

   @Test
   @RunAsClient
   public void testInteraction(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = renderURL(deploymentURL, HtmlHeaderPortlet.class);
      driver.get(url.toString());
      WebElement title = driver.findElement(By.tagName("title"));
      Assert.assertEquals("_title_", title.getText());
      WebElement script = driver.findElement(By.tagName("script"));
      Assert.assertTrue(script.getAttribute("src").endsWith("_src_"));
      Assert.assertEquals("_type_", script.getAttribute("type"));
      WebElement link = driver.findElement(By.tagName("link"));
      Assert.assertEquals("_charset_", link.getAttribute("charset"));
      Assert.assertTrue(link.getAttribute("href").endsWith("_href_"));
      Assert.assertEquals("_media_", link.getAttribute("media"));
      Assert.assertEquals("_rel_", link.getAttribute("rel"));
      Assert.assertEquals("_type_", link.getAttribute("type"));
      WebElement meta = driver.findElement(By.tagName("meta"));
      Assert.assertEquals("_name_", meta.getAttribute("name"));
      Assert.assertEquals("_http-equiv_", meta.getAttribute("http-equiv"));
      Assert.assertEquals("_content_", meta.getAttribute("content"));
      WebElement style = driver.findElement(By.tagName("style"));
      Assert.assertEquals("_type_", style.getAttribute("type"));
      Assert.assertEquals("_media_", style.getAttribute("media"));
      Assert.assertEquals("_style_", style.getText());
   }
}
