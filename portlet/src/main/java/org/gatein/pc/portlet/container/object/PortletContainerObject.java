/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2008, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portlet.container.object;

import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletContainerContext;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.container.PortletFilter;

/**
 * Contains life cycle and wiring details for the kernel environment.
 * 
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public interface PortletContainerObject extends PortletContainer
{

   /**
    * Set/unset the application.
    *
    * @param application the related application
    */
   void setPortletApplication(PortletApplication application);

   /**
    * Set/unset the portlet container context.
    *
    * @param context the context
    */
   void setContext(PortletContainerContext context);

   /**
    * Add a filter.
    *
    * @param filter the portlet filter
    */
   void addPortletFilter(PortletFilter filter);

   /**
    * Remove a filter.
    *
    * @param filter the portlet filter
    */
   void removePortletFilter(PortletFilter filter);

   /**
    * Starts the portlet container.
    *
    * @throws Exception any exception preventing the start
    */
   void start() throws Exception;

   /**
    * Stops the portlet container.
    */
   void stop();
}
