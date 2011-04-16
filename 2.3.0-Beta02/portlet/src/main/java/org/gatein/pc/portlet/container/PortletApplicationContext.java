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

import javax.servlet.ServletContext;

/**
 * The context provided to a portlet application by its envronment. For now it manages the application external resources
 * such as the servlet context and the application classloader. It manages also the life cycle of the
 * application and its components.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6733 $
 */
public interface PortletApplicationContext
{
   /**
    * Return the servlet context.
    *
    * @return the servlet context
    */
   ServletContext getServletContext();

   /**
    * Returns the context path of the web application
    * 
    * @return the context path
    */
   String getContextPath();
   
   /**
    * Return the classloader.
    *
    * @return the classloader
    */
   ClassLoader getClassLoader();

   /**
    * Attempt to start the portlet application.
    */
   void managedStart();

   /**
    * Stop the portlet application.
    */
   void managedStop();
}
