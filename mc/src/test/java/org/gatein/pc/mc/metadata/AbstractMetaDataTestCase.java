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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.gatein.pc.mc.metadata.factory.PortletApplicationModelFactory;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication10MetaData;
import org.gatein.pc.mc.metadata.impl.AnnotationPortletApplication20MetaData;
import org.gatein.pc.mc.metadata.impl.ValueTrimmingFilter;
import org.gatein.pc.mc.staxnav.PortletApplicationMetaDataBuilder;
import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;

import static org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants.*;

import org.jboss.xb.binding.JBossXBException;
import org.jboss.xb.binding.Unmarshaller;
import org.jboss.xb.binding.UnmarshallerFactory;
import org.jboss.xb.binding.resolver.MutableSchemaResolver;
import org.jboss.xb.binding.sunday.unmarshalling.SingletonSchemaResolverFactory;
import org.jboss.xb.builder.JBossXBBuilder;
import org.xml.sax.SAXException;
import org.jboss.util.xml.JBossEntityResolver;

import javax.xml.stream.XMLStreamException;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public abstract class AbstractMetaDataTestCase extends TestCase
{

   /** Test parameter for using xml binding annotation. */
   public static final String ANNOTATION_BINDING = "annotation";

   /** Test parameter for using the ObjectModelFactory. */
   public static final String FACTORY_BINDING = "factory";

   /** The schema resolver factory. */
   protected static SingletonSchemaResolverFactory factory;

   /** The schema resolver. */
   protected static MutableSchemaResolver resolver;

   /** The unmarshaller. */
   protected Unmarshaller unmarshaller = null;

   /** Annotation or ObjectModelFactory parsing. */
   private String parser = ANNOTATION_BINDING;

   static
   {
      try
      {
         factory = SingletonSchemaResolverFactory.getInstance();
         resolver =  factory.getSchemaBindingResolver();

         JBossEntityResolver.registerEntity(PORTLET_JSR_168_NS, "metadata/portlet-app_1_0.xsd");
         JBossEntityResolver.registerEntity(PORTLET_JSR_286_NS, "metadata/portlet-app_2_0.xsd");

         /** SchemaResolver */
         resolver.mapSchemaLocation(PORTLET_JSR_168_NS, "portlet-app_1_0.xsd");
         resolver.mapSchemaLocation(PORTLET_JSR_286_NS, "portlet-app_2_0.xsd");
         resolver.mapLocationToClass(PORTLET_JSR_286_NS, AnnotationPortletApplication20MetaData.class);
         resolver.mapLocationToClass(PORTLET_JSR_168_NS, AnnotationPortletApplication10MetaData.class);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected PortletApplication20MetaData _unmarshall10(String file) throws JBossXBException, SAXException, IOException
   {
      return _unmarshall10(file, false);
   }

   protected PortletApplication20MetaData _unmarshall10(String file, boolean fail) throws JBossXBException, SAXException, IOException
   {
      try
      {
         PortletApplicationMetaDataBuilder builder = new PortletApplicationMetaDataBuilder();
         String path = getPath(file);
         URL url = new URL(path);
         InputStream in = url.openStream();
         assertNotNull(in);
         PortletApplication20MetaData build = builder.build(in);
         if (fail)
         {
            throw new AssertionFailedError("Was expecting unmarshalling of " + file + " to fail");
         }
         return build;
      }
      catch (Exception e)
      {
         if (fail)
         {
            // OK
            return null;
         }
         else
         {
            throw fail(e);
         }
      }
   }

   protected final Error fail(Throwable t)
   {
      AssertionFailedError afe = new AssertionFailedError();
      afe.initCause(t);
      throw afe;
   }

   protected final Error fail(Throwable t, String msg)
   {
      AssertionFailedError afe = new AssertionFailedError(msg);
      afe.initCause(t);
      throw afe;
   }

   protected PortletApplication10MetaData unmarshall10(String file) throws JBossXBException, SAXException, IOException
   {
      if (ANNOTATION_BINDING.equals(parser))
      {
         return this.unmarshallAnnotation(file, AnnotationPortletApplication10MetaData.class);
      }
      else if (FACTORY_BINDING.equals(parser))
      {
         return this.unmarshallWithFactory(file);
      }
      else
      {
         throw new IllegalArgumentException("Wrong parameter for parser: " + parser);
      }
   }

   protected PortletApplication20MetaData unmarshall20(String file) throws JBossXBException, SAXException, IOException
   {
      if (ANNOTATION_BINDING.equals(parser))
      {
         return this.unmarshallAnnotation(file, AnnotationPortletApplication20MetaData.class);
      }
      else if (FACTORY_BINDING.equals(parser))
      {
         return (PortletApplication20MetaData)this.unmarshallWithFactory(file);
      }
      else
      {
         throw new IllegalArgumentException("Wrong parameter for parser: " + parser);
      }
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

   private <T> T unmarshallAnnotation(String file, Class<T> clazz) throws JBossXBException
   {
	    /** validate */
	    unmarshaller = UnmarshallerFactory.newInstance().newUnmarshaller();
	    unmarshaller.setNamespaceAware(true);
	    unmarshaller.setSchemaValidation(true);
	    unmarshaller.setValidation(true);
     
	    /** unmarshal */
	    return clazz.cast(unmarshaller.unmarshal(getPath(file), JBossXBBuilder.build(clazz)));
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
