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

package org.gatein.pc.embed.renderheader;

import junit.framework.Assert;
import org.gatein.pc.embed.AbstractTestCase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class RenderHeaderTestCase extends AbstractTestCase
{

   @Deployment()
   public static WebArchive deployment()
   {
      return deployment(RenderHeaderPortlet.class);
   }

   @Test
   @RunAsClient
   public void testInteraction(@ArquillianResource URL deploymentURL) throws Exception
   {
      URL url = deploymentURL.toURI().resolve("embed/RenderHeaderPortlet").toURL();
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.connect();
      Assert.assertEquals(200, conn.getResponseCode());
      Map<String, String> headers = responseHeaders(conn);
      Assert.assertTrue(headers.containsKey("foo"));
      Assert.assertEquals("bar", headers.get("foo"));
   }
}
