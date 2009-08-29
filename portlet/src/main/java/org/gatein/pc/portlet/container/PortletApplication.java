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
package org.gatein.pc.portlet.container;

import java.util.Collection;

/**
 * A portlet application exposed for management.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6733 $
 */
public interface PortletApplication
{
   /**
    * Return the web app id.
    *
    * @return the id
    */
   String getId();

   /**
    * Returns the set of related portlet containers.
    *
    * @return the portlet containers
    */
   Collection<? extends PortletContainer> getPortletContainers();

   /**
    * Returns a specific container or null if it does not exist
    *
    * @param containerId the container id
    * @return the portlet container
    */
   PortletContainer getPortletContainer(String containerId);

   /**
    * Returns the context of the portlet application.
    *
    * @return the context
    */
   PortletApplicationContext getContext();


}
