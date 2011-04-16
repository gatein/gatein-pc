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

package org.gatein.pc.test.portlet;

import org.gatein.pc.api.Mode;
import org.gatein.pc.portlet.impl.info.ContainerCapabilitiesInfo;

import java.util.Collection;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Test;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 6720 $
 * @since 2.4
 */
// @Test
public class ContentTypesTestCase
{
   private ContainerCapabilitiesInfo contentTypes;

   public ContentTypesTestCase()
   {
      contentTypes = new ContainerCapabilitiesInfo();
      ContainerCapabilitiesInfo md = new ContainerCapabilitiesInfo();
      md.add("text/html", org.gatein.pc.api.Mode.EDIT);
      md.add("text/html", Mode.HELP );
      md.add("text/html", org.gatein.pc.api.Mode.VIEW);
      md.add("text/xml", org.gatein.pc.api.Mode.VIEW);
      md.add("*", org.gatein.pc.api.Mode.VIEW);
//      md.add("*/*", new Mode[]{});
//      md.add("text/*", new Mode[]{});

      // should this be allowed?
//      md.add(new ContentTypeMetaData("pipo/pipo", new Mode[]{}));
   }

   @Test
   public void testGetAllModes()
   {
      Collection modes = contentTypes.getAllModes();
      assertEquals(3, modes.size());
      assertTrue(modes.contains(org.gatein.pc.api.Mode.EDIT));
      assertTrue(modes.contains(Mode.HELP));
      assertTrue(modes.contains(Mode.VIEW));
   }

   @Test
   public void testIsModeSupported()
   {
   }

   @Test
   public void testGetSupportedModes()
   {
   }

   public void testIsContentTypeSupported()
   {
   }


   @Test
   public void testIsModeSupportedFor()
   {
   }
}
