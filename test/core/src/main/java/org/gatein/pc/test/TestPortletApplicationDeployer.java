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

import org.gatein.wci.WebApp;
import org.gatein.pc.portlet.impl.deployment.PortletApplicationDeployer;
import org.gatein.wci.WebAppLifeCycleEvent;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public class TestPortletApplicationDeployer extends PortletApplicationDeployer
{

   /** Keep track of deployers. */
   private static final ArrayList<TestPortletApplicationDeployer> deployers = new ArrayList<TestPortletApplicationDeployer>();

   /** . */
   private static final HashSet<WebApp> deployments = new HashSet<WebApp>();

   public static void deploy(WebApp deployment)
   {
      if (deployments.add(deployment))
      {
         synchronized (deployers)
         {
            for (TestPortletApplicationDeployer deployer : deployers)
            {
               deployer.doDeploy(deployment);
            }
         }
      }
   }

   public synchronized static void undeploy(WebApp deployment)
   {
      if (deployments.remove(deployment))
      {
         synchronized (deployers)
         {
            for (TestPortletApplicationDeployer deployer : deployers)
            {
               deployer.doUndeploy(deployment);
            }
         }
      }
   }

   public TestPortletApplicationDeployer()
   {
   }

   private void doDeploy(WebApp deployment)
   {
      onEvent(new WebAppLifeCycleEvent(deployment, WebAppLifeCycleEvent.ADDED));
   }

   private void doUndeploy(WebApp deployment)
   {
      onEvent(new WebAppLifeCycleEvent(deployment, WebAppLifeCycleEvent.REMOVED));
   }

   @Override
   public void start()
   {
      synchronized (deployers)
      {
         super.start();

         //
         for (WebApp deployment : deployments)
         {
            doDeploy(deployment);
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
         for (WebApp deployment : deployments)
         {
            doUndeploy(deployment);
         }

         //
         super.stop();
      }
   }
}
