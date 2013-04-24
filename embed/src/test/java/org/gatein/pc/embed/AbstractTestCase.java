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

package org.gatein.pc.embed;

import junit.framework.AssertionFailedError;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

import javax.portlet.Portlet;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
@RunWith(Arquillian.class)
public abstract class AbstractTestCase
{

   /** . */
   public static final String PORTLET_APP_PROLOG = "<portlet-app xmlns=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd\"\n" +
      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
      "xsi:schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd http://java.sun.com/xml/ns/portlet/portlet-app_2_0.xsd\"\n" +
      "version=\"2.0\">\n";

   /** . */
   public static final String PORTLET_APP_EPILOG = "</portlet-app>\n";

   public static WebArchive deployment(Class<? extends Portlet>... portlets)
   {
      StringBuilder descriptor = new StringBuilder();
      descriptor.append(PORTLET_APP_PROLOG);
      for (Class<? extends Portlet> portlet : portlets)
      {
         descriptor.append("<portlet>\n");
         descriptor.append("<portlet-name>").append(portlet.getSimpleName()).append("</portlet-name>\n");
         descriptor.append("<portlet-class>").append(portlet.getName()).append("</portlet-class>\n");
         descriptor.append("<portlet-info>\n");
         descriptor.append("<title>").append(portlet.getSimpleName()).append("</title>\n");
         descriptor.append("</portlet-info>\n");
         descriptor.append("</portlet>\n");
      }
      descriptor.append(PORTLET_APP_EPILOG);
      return deployment(descriptor.toString());
   }

   public static WebArchive deployment(String descriptor)
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class);
      war.setWebXML(new ByteArrayAsset(("" +
         "<web-app\n" +
         "xmlns=\"http://java.sun.com/xml/ns/javaee\"\n" +
         "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
         "xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd\"\n" +
         "version=\"3.0\">" +
         "<servlet>\n" +
         "<servlet-name>EmbedServlet</servlet-name>\n" +
         "<servlet-class>" + EmbedServlet.class.getName() + "</servlet-class>\n" +
         "<load-on-startup>0</load-on-startup>\n" +
         "</servlet>\n" +
         "<servlet-mapping>\n" +
         "<servlet-name>EmbedServlet</servlet-name>\n" +
         "<url-pattern>/embed/*</url-pattern>\n" +
         "</servlet-mapping>\n" +
         "</web-app>\n").getBytes()));
      war.addAsWebInfResource(new ByteArrayAsset(descriptor.getBytes()), "portlet.xml");
      return war;
   }

   public static Map<String, String> responseHeaders(HttpURLConnection conn) {
      Map<String, String> headers = Collections.emptyMap();
      for (int i=0; ; i++) {
         String name = conn.getHeaderFieldKey(i);
         String value = conn.getHeaderField(i);
         if (name == null && value == null) {
            break;
         }
         if (name != null) {
            if (headers.isEmpty()) {
               headers = new HashMap<String, String>();
            }
            headers.put(name, value);
         }
      }
      return headers;
   }

   /**
    * Create a render URL for the specified portlet, delegating to {@link #renderURL(java.net.URL, Iterable)}.
    *
    * @param deploymentURL the deployment URL
    * @param portlet the portlet
    * @return the URL
    */
   protected URL renderURL(URL deploymentURL, Class<? extends Portlet> portlet)
   {
      return renderURL(deploymentURL, Collections.<Class<? extends Portlet>>singletonList(portlet));
   }

   /**
    * Create a render URL for the specified portlets, delegating to {@link #renderURL(java.net.URL, Iterable)}.
    *
    * @param deploymentURL the deployment URL
    * @param portlet1 the portlet numero 1
    * @param portlet2 the portlet numero 2
    * @return the URL
    */
   protected URL renderURL(URL deploymentURL, Class<? extends Portlet> portlet1, Class<? extends Portlet> portlet2)
   {
      ArrayList<Class<? extends Portlet>> portlets = new ArrayList<Class<? extends Portlet>>();
      portlets.add(portlet1);
      portlets.add(portlet2);
      return renderURL(deploymentURL, portlets);
   }

   /**
    * Create a render URL for the specified portlets.
    *
    * @param deploymentURL the deployment URL
    * @param portlets the portlets
    * @return the URL
    */
   protected URL renderURL(URL deploymentURL, Iterable<Class<? extends Portlet>> portlets)
   {
      StringBuilder path = new StringBuilder("embed");
      for (Class<? extends Portlet> portlet : portlets)
      {
         path.append("/").append(portlet.getSimpleName());
      }
      try
      {
         return deploymentURL.toURI().resolve(path.toString()).toURL();
      }
      catch (Exception e)
      {
         AssertionFailedError afe = new AssertionFailedError();
         afe.initCause(e);
         throw afe;
      }
   }
}
