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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.pc.portlet.impl.metadata.PortletApplication10MetaData;
import org.gatein.pc.portlet.impl.metadata.PortletApplication20MetaData;
import org.gatein.pc.portlet.impl.metadata.portlet.PortletMetaData;
import org.gatein.pc.portlet.impl.info.ContainerInfoBuilderContext;
import org.gatein.wci.WebApp;
import org.gatein.common.i18n.ResourceBundleManager;
import org.gatein.common.i18n.ResourceBundleFactory;
import org.gatein.common.reflect.NoSuchClassException;

import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ContainerInfoBuilderContextImpl implements ContainerInfoBuilderContext
{

   /** . */
   private final PortletApplication10MetaData metaData;

   /** . */
   private final WebApp webApp;

   /** . */
   private final Map<String, ResourceBundleManager> portletBundleMgrs;

   /** . */
   private ResourceBundleManager applicationBundleMgr;

   /** . */
   private final String applicationName;

   public ContainerInfoBuilderContextImpl(PortletApplication10MetaData metaData, WebApp webApp)
   {
      this.metaData = metaData;
      this.webApp = webApp;
      this.portletBundleMgrs = new HashMap<String, ResourceBundleManager>();
      this.applicationName = webApp.getContextPath().substring(1);
   }

   public String getApplicationName()
   {
      return applicationName;
   }

   public ResourceBundleManager getBundleManager()
   {
      if (applicationBundleMgr == null)
      {
         String baseName = ((PortletApplication20MetaData)metaData).getResourceBundle();
         ResourceBundleFactory rbf = new SimpleResourceBundleFactory(webApp.getClassLoader(), baseName);
         applicationBundleMgr = new ResourceBundleManager(null, rbf);
      }
      return applicationBundleMgr;
   }

   public ResourceBundleManager getBundleManager(PortletMetaData portletMD)
   {
      ResourceBundleManager bundleMgr = portletBundleMgrs.get(portletMD.getPortletName());
      if (bundleMgr == null)
      {
         String baseName = portletMD.getResourceBundle();
         ResourceBundleFactory rbf = new SimpleResourceBundleFactory(webApp.getClassLoader(), baseName);
         bundleMgr = new ResourceBundleManager(null, rbf);
         portletBundleMgrs.put(portletMD.getPortletName(), bundleMgr);
      }
      return bundleMgr;
   }

   public Class getClass(String className) throws IllegalArgumentException, NoSuchClassException
   {
      if (className == null)
      {
         throw new IllegalArgumentException("No null class name accepted");
      }
      try
      {
         return webApp.getClassLoader().loadClass(className);
      }
      catch (ClassNotFoundException e)
      {
         throw new NoSuchClassException(className, e);
      }
      catch (NoClassDefFoundError e)
      {
         throw new NoSuchClassException(className, e);
      }
   }
}
