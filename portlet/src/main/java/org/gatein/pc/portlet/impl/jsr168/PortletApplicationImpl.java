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

import org.gatein.pc.portlet.impl.jsr168.api.PortletContextImpl;
import org.gatein.pc.portlet.impl.jsr168.api.PortletURLGenerationListenerChain;
import org.gatein.pc.portlet.impl.jsr168.api.FilterConfigImpl;
import org.gatein.pc.portlet.impl.info.ContainerPortletApplicationInfo;
import org.gatein.pc.portlet.impl.info.ContainerListenerInfo;
import org.gatein.pc.portlet.impl.info.ContainerFilterInfo;
import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletApplicationContext;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.container.PortletInitializationException;
import org.gatein.pc.portlet.container.object.PortletApplicationObject;
import org.apache.log4j.Logger;

import javax.portlet.PortletContext;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.filter.PortletFilter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7226 $
 */
public class PortletApplicationImpl implements PortletApplicationObject
{

   /** . */
   protected final ContainerPortletApplicationInfo info;

   /** Logger. */
   protected final Logger log;

   /** . */
   protected PortletApplicationContext context;

   /** . */
   protected LinkedHashMap<String, PortletContainer> portlets;

   /** . */
   protected PortletContextImpl portletContext;

   /** . */
   protected PortletURLGenerationListener urlListener;

   /**
    * @param info the portlet application info
    */
   public PortletApplicationImpl(ContainerPortletApplicationInfo info)
   {
      this.info = info;
      this.portlets = new LinkedHashMap<String, PortletContainer>();
      this.log = Logger.getLogger(PortletApplication.class.getName() + "." + info.getId().replace('.', '_'));
   }

   public void setContext(PortletApplicationContext context)
   {
      this.context = context;
   }

   public String getId()
   {
      return info.getId();
   }

   public void addPortletFilter(org.gatein.pc.portlet.container.PortletFilter filter)
   {
      //
   }

   public void removePortletFilter(org.gatein.pc.portlet.container.PortletFilter filter)
   {
      //
   }

   public ContainerPortletApplicationInfo getInfo()
   {
      return info;
   }

   public void addPortletContainer(PortletContainer container)
   {
      portlets.put(container.getId(), container);
   }

   public void removePortletContainer(PortletContainer container)
   {
      portlets.remove(container.getId());
   }

   //

   public void start() throws Exception
   {
      PortletContextImpl portletContext = new PortletContextImpl(context.getServletContext());

      //
      List<PortletURLGenerationListener> listeners = Collections.emptyList();
      for (ContainerListenerInfo listenerInfo : info.getListeners())
      {
         try
         {
            String className = listenerInfo.getClassName();

            ClassInstanceLifeCycle<PortletURLGenerationListener> lifeCycle = new ClassInstanceLifeCycle<PortletURLGenerationListener>(
               log,
               PortletURLGenerationListener.class,
               context.getClassLoader(),
               className,
               "listener"
            );

            //
            lifeCycle.create();

            //
            if (listeners.size() == 0)
            {
               listeners = new LinkedList<PortletURLGenerationListener>();
            }

            //
            listeners.add(lifeCycle.getInstance());
         }
         catch (PortletInitializationException e)
         {
            log.error(e.getMessage(), e);
         }
      }

      //
      this.portletContext = portletContext;
      this.urlListener = listeners.size() == 0 ? null : new PortletURLGenerationListenerChain(Collections.unmodifiableList(listeners));
   }

   public void stop()
   {
      this.portletContext = null;
      this.urlListener = null;
   }

   public PortletContext getPortletContext()
   {
      return portletContext;
   }

   public <T> T getListener(Class<T> type)
   {
      if (type == PortletURLGenerationListener.class)
      {
         return type.cast(urlListener);
      }

      //
      return null;
   }

/*
   public <T extends PortletFilter> T getFilter(String filterName, Class<T> phase)
   {
      FilterLifecycle lifeCycle = filters.get(new FilterKey(filterName, phase));

      //
      if (lifeCycle == null)
      {
         return null;
      }

      // Should be ok
      return phase.cast(lifeCycle.getInstance());
   }
*/

   // WebApp implementation ********************************************************************************************

   public Set<PortletContainer> getPortletContainers()
   {
      return new HashSet<PortletContainer>(portlets.values());
   }

   public PortletContainer getPortletContainer(String name)
   {
      return portlets.get(name);
   }

   public PortletApplicationContext getContext()
   {
      return context;
   }

   // Container implementation *****************************************************************************************

   public String toString()
   {
      return "PortletApplication[" + getId() + "]";
   }

   static class FilterLifecycle extends ClassInstanceLifeCycle<PortletFilter>
   {

      /** . */
      private final ContainerFilterInfo info;

      /** . */
      private final PortletContext portletContext;

      FilterLifecycle(
         Logger log,
         ClassLoader classLoader,
         String className,
         String type,
         ContainerFilterInfo info,
         PortletContext portletContext)
      {
         super(log, PortletFilter.class, classLoader, className, type);

         //
         this.info = info;
         this.portletContext = portletContext;
      }

      public void start(PortletFilter instance) throws Exception
      {
         FilterConfigImpl config = new FilterConfigImpl(info, portletContext);

         //
         instance.init(config);
      }

      public void stop(PortletFilter instance)
      {
         instance.destroy();
      }
   }

}
