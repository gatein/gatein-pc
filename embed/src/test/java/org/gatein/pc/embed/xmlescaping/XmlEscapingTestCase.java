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

package org.gatein.pc.embed.xmlescaping;

import org.gatein.common.io.IOTools;
import org.gatein.pc.embed.AbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class XmlEscapingTestCase extends AbstractTestCase
{

   @Deployment
   public static WebArchive deployment()
   {
      return deployment(XmlEscapingPortlet.class);
   }

   @Test
   @RunAsClient
   public void testInteraction(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = renderURL(deploymentURL, XmlEscapingPortlet.class);
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.connect();
      junit.framework.Assert.assertEquals(200, conn.getResponseCode());
      byte[] bytes = IOTools.getBytes(conn.getInputStream());
      String s = new String(bytes, "UTF-8");
      Pattern p = Pattern.compile("FOO(.+?)FOO");
      List<String> urls = new ArrayList<String>();
      Matcher m = p.matcher(s);
      while (m.find()) {
         urls.add(m.group(1));
      }
      Assert.assertEquals(3, urls.size());
      String url1 = urls.get(0);
      String url2 = urls.get(1);
      String url3 = urls.get(2);
      int pos = url1.indexOf('=');
      int i = url1.indexOf('&', pos);
      Assert.assertNotSame(-1, i);
      Assert.assertEquals('&', url2.charAt(i));
      Assert.assertEquals("&amp;", url3.substring(i, i + 5));
   }
}
