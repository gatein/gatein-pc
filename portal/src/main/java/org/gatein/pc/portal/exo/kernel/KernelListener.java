/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2009, Red Hat Middleware, LLC, and individual                    *
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
package org.gatein.pc.portal.exo.kernel;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.StandaloneContainer;

/**
 * This is class is basically the
 * org.exoplatform.services.portletcontainer.test.listeners.AppListener
 * 
 * This class will setup the eXo Kernel when the webapp is loaded
 * for testing. If using the real eXo Portal, this will already be
 * setup by the portal itself.
 * 
 * @author <a href="mailto:mwringe@redhat.com">Matt Wringe</a>
 * @version $Revision$
 */
public class KernelListener implements ServletContextListener
{

   public void contextDestroyed(ServletContextEvent arg0)
   {
      try
      {
         Object[][] components =
         {{ServletContextListener.class.getName(), arg0.getServletContext()}};
         StandaloneContainer.getInstance(Thread.currentThread().getContextClassLoader(), components);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void contextInitialized(ServletContextEvent arg0)
   {
      // FIXME contextInitialized
   }

}

