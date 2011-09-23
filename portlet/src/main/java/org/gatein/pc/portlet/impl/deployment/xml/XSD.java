/*
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.gatein.pc.portlet.impl.deployment.xml;

import org.gatein.common.logging.Logger;
import org.gatein.common.logging.LoggerFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class XSD
{

   /** . */
   private static final Logger log = LoggerFactory.getLogger(XSD.class);

   private final static Map<String, String> a = new HashMap<String, String>();

   static
   {
      a.put("http://www.w3.org/2001/xml.xsd", "xml.xsd");
      a.put("XMLSchema.dtd", "XMLSchema.dtd");
      a.put("datatypes.dtd", "datatypes.dtd");
   }

   /** . */
   public static final XSD PORTLET_1_0 = new XSD(XSD.class.getResource("portlet-app_1_0.xsd")).fetch();

   /** . */
   public static final XSD PORTLET_2_0 = new XSD(XSD.class.getResource("portlet-app_2_0.xsd")).fetch();

   /** . */
   private final FutureTask<Schema> schema;

   public XSD(final URL url)
   {
      this.schema = new FutureTask<Schema>(new Callable<Schema>()
      {
         public Schema call() throws Exception
         {
            SchemaFactory factory =  SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new LSResourceResolver()
            {
               public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI)
               {
                  String resolvedName = a.get(systemId);
                  if (resolvedName != null)
                  {
                     try
                     {
                        InputStream in = XSD.class.getResourceAsStream(resolvedName);
                        System.out.println("Resolved systemId=" + systemId);
                        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
                        DOMImplementationLS ls = (DOMImplementationLS)registry.getDOMImplementation("LS");
                        LSInput input = ls.createLSInput();
                        input.setByteStream(in);
                        return input;
                     }
                     catch (Exception e)
                     {
                        log.error("Could not obtain xml.xsd", e);
                     }
                  }
                  return null;
               }
            });
            return factory.newSchema(url);
         }
      });
   }

   public XSD fetch()
   {
      new Thread()
      {
         public void run()
         {
            schema.run();
         }
      }.start();
      return this;
   }

   public XSD fetch(Executor executor)
   {
      executor.execute(schema);
      return this;
   }

   public Validator getValidator()
   {
      try
      {
         return schema.get().newValidator();
      }
      catch (InterruptedException e)
      {
         throw new AssertionError(e);
      }
      catch (ExecutionException e)
      {
         throw new AssertionError(e.getCause());
      }
   }

   public void validate(Source source) throws SAXException, IOException
   {
      getValidator().validate(source);
   }
}
