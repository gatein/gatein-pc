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

import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.PublicRenderParameterMetaData;
import org.jboss.unit.api.pojo.annotations.Test;

import static org.jboss.unit.api.Assert.*;
/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
public class RenderParameterTestEverythingTestCase extends AbstractMetaDataTestCase
{

   @Test
   public void test02()
   {
      try
      {

         String xmlFile = "metadata/renderParameter/portlet2.xml";

         PortletApplication20MetaData md = unmarshall20(xmlFile);
         assertNotNull(md);
         assertTrue(md instanceof PortletApplication20MetaData);
         assertEquals("2.0", md.getVersion());

         PublicRenderParameterMetaData prp1 = md.getPublicRenderParameters().get(0);
         assertNotNull(prp1);
         
         assertEquals("blah", prp1.getIdentifier());
         assertEquals("renderParameter1", prp1.getName());
         
         
         PublicRenderParameterMetaData prp2 = md.getPublicRenderParameters().get(1);
         assertEquals("foo", prp2.getQname().getLocalPart());
         assertEquals("x", prp2.getQname().getPrefix());
         assertEquals("http://someurl.com", prp2.getQname().getNamespaceURI());

         assertEquals("fooo", prp1.getAlias().get(0).getLocalPart());
         assertEquals("rP1", prp1.getAlias().get(1).getLocalPart());

         assertEquals("foo", prp2.getAlias().get(0).getLocalPart());
         assertEquals("http://someurl.alias.com", prp2.getAlias().get(0).getNamespaceURI());
         assertEquals("s", prp2.getAlias().get(0).getPrefix());
         
         assertEquals("render parameter foo", prp1.getDescription().getDefaultString());

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

}
