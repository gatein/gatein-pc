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
import org.gatein.pc.mc.staxnav.PortletApplicationMetaDataBuilder;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;

import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public abstract class AbstractMetaDataTestCase extends TestCase
{

   /** Test parameter for using xml binding annotation. */
   public static final String ANNOTATION_BINDING = "annotation";

   /** Annotation or ObjectModelFactory parsing. */
   private String parser = ANNOTATION_BINDING;

   protected PortletApplication20MetaData _unmarshall10(String file) throws SAXException, IOException
   {
      return _unmarshall10(file, false);
   }

   protected PortletApplication20MetaData _unmarshall10(String file, boolean fail) throws SAXException, IOException
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
