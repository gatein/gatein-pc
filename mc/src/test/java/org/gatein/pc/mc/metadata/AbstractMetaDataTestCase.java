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
package org.gatein.pc.mc.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.gatein.pc.mc.metadata.factory.PortletApplicationModelFactory;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication10MetaData;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication20MetaData;
import org.gatein.pc.mc.metadata.impl.ValueTrimmingFilter;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;

import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.*;

import org.jboss.unit.api.pojo.annotations.Parameter;
import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.sunday.unmarshalling.DefaultSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SingletonSchemaResolverFactory;
import org.xml.sax.SAXException;
import static org.jboss.unit.api.Assert.*;
import org.jboss.util.xml.JBossEntityResolver;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public abstract class AbstractMetaDataTestCase
{

   /** Test parameter for using xml binding annotation. */
   public static final String ANNOTATION_BINDING = "annotation";

   /** Test parameter for using the ObjectModelFactory. */
   public static final String FACTORY_BINDING = "factory";

   /** The schema resolver factory. */
   protected static SingletonSchemaResolverFactory factory;

   /** The schema resolver. */
   protected static DefaultSchemaResolver resolver;

   /** The unmarshaller. */
   protected Unmarshaller unmarshaller = null;

   /** Annotation or ObjectModelFactory parsing. */
   private String parser;

   @Parameter(name = "parser")
   public void setParser(String parser)
   {
      this.parser = parser;
   }

   static
   {
      try
      {
         factory = SingletonSchemaResolverFactory.getInstance();
         resolver = (DefaultSchemaResolver) factory.getSchemaBindingResolver();

         /** SchemaResolver */
      // The two following lines are not effective, we need for now to register xsd globally
//         resolver.addSchemaLocation(PORTLET_JSR_168_NS, "portlet-app_1_0.xsd");
//         resolver.addSchemaLocation(PORTLET_JSR_286_NS, "portlet-app_2_0.xsd");
         JBossEntityResolver.registerEntity(PORTLET_JSR_168_NS, "metadata/portlet-app_1_0.xsd");
         JBossEntityResolver.registerEntity(PORTLET_JSR_286_NS, "metadata/portlet-app_2_0.xsd");

         resolver.addClassBinding(PORTLET_JSR_286_NS, AnnotationPortletApplication20MetaData.class);
         resolver.addClassBinding(PORTLET_JSR_168_NS, AnnotationPortletApplication10MetaData.class);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected PortletApplication10MetaData unmarshall10(String file) throws JBossXBException, SAXException, IOException
   {
      if (ANNOTATION_BINDING.equals(parser))
      {
         return this.unmarshallAnnotation(file);
      }
      else if (FACTORY_BINDING.equals(parser))
      {
         return this.unmarshallWithFactory(file);
      }
      else
      {
         throw new IllegalArgumentException("Wrong parameter for parser.");
      }
   }

   protected PortletApplication20MetaData unmarshall20(String file) throws JBossXBException, SAXException, IOException
   {
      if (ANNOTATION_BINDING.equals(parser))
      {
         return (PortletApplication20MetaData)this.unmarshallAnnotation(file);
      }
      else if (FACTORY_BINDING.equals(parser))
      {
         return (PortletApplication20MetaData)this.unmarshallWithFactory(file);
      }
      else
      {
         throw new IllegalArgumentException("Wrong parameter for parser.");
      }
   }

   private PortletApplication10MetaData unmarshallAnnotation(String file) throws JBossXBException, SAXException,
         IOException
   {
      /** validate */
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.setNamespaceAware(true);
      unmarshaller.setSchemaValidation(true);
      unmarshaller.setValidation(true);

      /** unmarshal */
      return (PortletApplication10MetaData) unmarshaller.unmarshal(getPath(file), resolver);
   }

   private PortletApplication10MetaData unmarshallWithFactory(String file) throws JBossXBException
   {
      /** validate */
      unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
      unmarshaller.setNamespaceAware(true);
      unmarshaller.setSchemaValidation(true);
      unmarshaller.setValidation(true);

      PortletApplicationModelFactory factory = new PortletApplicationModelFactory();
      /** unmarshal */
      return (PortletApplication10MetaData) unmarshaller.unmarshal(getStream(file), new ValueTrimmingFilter(factory),
            null);
   }

   protected String getPath(String file)
   {
      URL url = Thread.currentThread().getContextClassLoader().getResource(file);
      if (url == null)
      {
         fail(file + " not found.");
      }
      return url.toString();
   }

   protected InputStream getStream(String file)
   {
      return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
   }

}
