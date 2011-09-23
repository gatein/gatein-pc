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

package org.gatein.pc.portlet.deployment;

import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.common.ContainerRuntimeMetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
public class ContainerRuntimeOptionTestCase extends AbstractMetaDataTestCase
{

   public void test021() throws Exception
   {
      PortletApplication20MetaData md = unmarshall("metadata/runtimeoption/portlet.xml");
      PortletMetaData portlet = md.getPortlet("portlet-name");
      assertNotNull(portlet);
      Map<String, ContainerRuntimeMetaData> m = portlet.getContainerRuntimeOptions();
      assertEquals(Collections.singleton("portlet_container_runtime_option_name"), m.keySet());
      ContainerRuntimeMetaData option = m.get("portlet_container_runtime_option_name");
      assertEquals(Arrays.asList("portlet_container_runtime_option_value"), option.getValues());
      assertNull(md.getContainerRuntimeOptions());
   }
}
