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

package org.gatein.pc.test;

import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.impl.deployment.PortletApplicationDeployer;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class TestPortletApplicationDeployer extends PortletApplicationDeployer
{

   /** Keep track of deployers. */
   private static final ArrayList<TestPortletApplicationDeployer> deployers = new ArrayList<TestPortletApplicationDeployer>();

   /** . */
   private static final HashMap<String, ServletContext> deployments = new HashMap<String, ServletContext>();

   public static void deploy(ServletContext deployment)
   {
      if (!deployments.containsKey(deployment.getContextPath()))
      {
         deployments.put(deployment.getContextPath(), deployment);
         synchronized (deployers)
         {
            for (TestPortletApplicationDeployer deployer : deployers)
            {
               deployer.add(deployment);
            }
         }
      }
   }

   public synchronized static void undeploy(ServletContext deployment)
   {
      if (deployments.remove(deployment.getContextPath()) != null)
      {
         synchronized (deployers)
         {
            for (TestPortletApplicationDeployer deployer : deployers)
            {
               deployer.remove(deployment);
            }
         }
      }
   }

   public TestPortletApplicationDeployer()
   {
   }

   @Override
   public void start()
   {
      synchronized (deployers)
      {
         super.start();

         //
         for (ServletContext deployment : deployments.values())
         {
            add(deployment);
         }

         //
         deployers.add(this);
      }
   }

   @Override
   public void stop()
   {
      synchronized (deployers)
      {
         deployers.remove(this);

         //
         for (ServletContext deployment : deployments.values())
         {
            remove(deployment);
         }

         //
         super.stop();
      }
   }
}
