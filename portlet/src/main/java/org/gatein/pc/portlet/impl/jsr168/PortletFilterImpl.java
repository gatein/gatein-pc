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
package org.gatein.pc.portlet.impl.jsr168;

import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletFilterContext;
import org.gatein.pc.portlet.container.object.PortletFilterObject;
import org.gatein.pc.portlet.impl.info.ContainerFilterInfo;
import org.gatein.pc.api.LifeCyclePhase;
import org.apache.log4j.Logger;

import javax.portlet.PortletContext;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletFilterImpl implements PortletFilterObject
{

   /** . */
   private static final Map<Class<? extends javax.portlet.filter.PortletFilter>, LifeCyclePhase> typeToPhase =
      new HashMap<Class<? extends javax.portlet.filter.PortletFilter>, LifeCyclePhase>();

   static
   {
      typeToPhase.put(ActionFilter.class, LifeCyclePhase.ACTION);
      typeToPhase.put(EventFilter.class, LifeCyclePhase.EVENT);
      typeToPhase.put(RenderFilter.class, LifeCyclePhase.RENDER);
      typeToPhase.put(ResourceFilter.class, LifeCyclePhase.RESOURCE);
   }

   /** . */
   private final Logger log;

   /** . */
   private final ContainerFilterInfo info;

   /** . */
   private PortletFilterContext context;

   /** . */
   private PortletApplicationImpl application;

   /** . */
   private PortletApplicationImpl.FilterLifecycle lifeCycle;

   public PortletFilterImpl(ContainerFilterInfo info)
   {
      this.info = info;
      this.log = Logger.getLogger(PortletFilterImpl.class);
   }

   public <T> T instance(Class<T> type)
   {
      LifeCyclePhase phase = typeToPhase.get(type);

      // It means we can cast unless the developers did not implement the correct interface
      if (info.getPhases().contains(phase))
      {
         if (type.isInstance(lifeCycle.getInstance()))
         {
            return type.cast(lifeCycle.getInstance());
         }
      }

      //
      return null;
   }

   public ContainerFilterInfo getInfo()
   {
      return info;
   }

   public String getId()
   {
      return info.getName();
   }

   public void setPortletApplication(PortletApplication application)
   {
      this.application = (PortletApplicationImpl)application;
   }

   public PortletApplication getPortletApplication()
   {
      return application;
   }

   public void setContext(PortletFilterContext context)
   {
      this.context = context;
   }

   public PortletFilterContext getContext()
   {
      return context;
   }

   public void start() throws Exception
   {
      PortletContext portletContext = application.portletContext;
      String className = info.getClassName();
      ClassLoader classLoader = application.getContext().getClassLoader();

      //
      PortletApplicationImpl.FilterLifecycle lifeCycle = new PortletApplicationImpl.FilterLifecycle(
         log,
         classLoader,
         className,
         "filter",
         info,
         portletContext);

      //
      lifeCycle.create();

      //
      this.lifeCycle = lifeCycle;
   }

   public void stop()
   {
      if (lifeCycle != null)
      {
         lifeCycle.destroy();

         //
         lifeCycle = null;
      }
   }
}
