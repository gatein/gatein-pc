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

import org.gatein.common.concurrent.Valve;
import org.gatein.common.i18n.ResourceBundleManager;
import org.gatein.common.invocation.InvocationException;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.portlet.container.PortletApplication;
import org.gatein.pc.portlet.container.PortletInitializationException;
import org.gatein.pc.portlet.container.PortletContainerContext;
import org.gatein.pc.portlet.container.object.PortletContainerObject;
import org.gatein.pc.portlet.impl.jsr168.api.ActionRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.ActionResponseImpl;
import org.gatein.pc.portlet.impl.jsr168.api.PortletConfigImpl;
import org.gatein.pc.portlet.impl.jsr168.api.RenderRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.RenderResponseImpl;
import org.gatein.pc.portlet.impl.jsr168.api.EventRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.EventResponseImpl;
import org.gatein.pc.portlet.impl.jsr168.api.ResourceRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.ResourceResponseImpl;
import org.gatein.pc.portlet.impl.jsr168.api.FilterChainImpl;
import org.gatein.pc.portlet.impl.jsr168.api.PortletRequestImpl;
import org.gatein.pc.portlet.impl.jsr168.api.PortletResponseImpl;
import org.gatein.pc.portlet.impl.info.ContainerPortletInfo;
import org.gatein.pc.portlet.impl.info.ContainerPreferencesInfo;
import org.gatein.pc.portlet.aspects.ContextDispatcherInterceptor;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.response.ErrorResponse;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.SecurityErrorResponse;
import org.gatein.pc.api.invocation.response.UnavailableResponse;
import org.apache.log4j.Logger;

import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSecurityException;
import javax.portlet.PreferencesValidator;
import javax.portlet.UnavailableException;
import javax.portlet.EventPortlet;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.PortletRequest;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6365 $
 */
public class PortletContainerImpl implements PortletContainerObject
{

   /** Logger. */
   protected final Logger log;

   /** Return info. */
   final ContainerPortletInfo info;

   /** . */
   protected PortletApplicationImpl application;

   /** The portlet implementation class name. */
   protected String className;

   /** The portlet config. */
   protected PortletConfig config;

   /** The portlet instance. */
   protected Portlet portlet;

   /** The invocation valve. */
   protected final Valve valve;

   /** Are we started or not. */
   protected boolean started;

   /** User data constraint. */
   protected Set userDataConstraints;

   /** . */
   private final Invoker invoker = new Invoker();

   /** . */
   private List<ActionFilter> actionFilterList;

   /** . */
   private List<EventFilter> eventFilterList;

   /** . */
   private List<RenderFilter> renderFilterList;

   /** . */
   private List<ResourceFilter> resourceFilterList;

   /** . */
   private PortletContainerContext context;

   /** . */
   private Set<org.gatein.pc.portlet.impl.jsr168.PortletFilterImpl> filters;

   /**
    * The preference validator, this is not exposed as runtime meta data as it is only used by the JSR 168 portlet
    * container implementation.
    */
   protected PreferencesValidator preferencesValidator;

   public PortletContainerImpl(ContainerPortletInfo info)
   {
      if (info == null)
      {
         throw new IllegalArgumentException("No null info is accepted");
      }

      //
      this.info = info;
      this.valve = new Valve();
      this.log = Logger.getLogger("org.gatein.pc.container." + info.getClassName().replace('.', '_'));
      this.started = false;
      this.filters = new HashSet<PortletFilterImpl>();
   }

   public void setContext(PortletContainerContext context)
   {
      this.context = context;
   }

   public PortletContainerContext getContext()
   {
      return context;
   }

   public ContainerPortletInfo getInfo()
   {
      if (started)
      {
         return info;
      }

      //
      throw new IllegalStateException("Portlet " + info.getName() + " is not started");
   }

   public String getId()
   {
      return info.getName();
   }

   public void addPortletFilter(org.gatein.pc.portlet.container.PortletFilter filter)
   {
      filters.add((PortletFilterImpl)filter);
   }

   public void removePortletFilter(org.gatein.pc.portlet.container.PortletFilter filter)
   {
      filters.remove((PortletFilterImpl)filter);
   }

   public void start() throws PortletInitializationException
   {
      // Set class name
      this.className = info.getClassName();

      //
      ContainerPreferencesInfo preferences = info.getPreferences();
      if (preferences != null)
      {
         String validatorClassName = preferences.getValidatorClassName();
         if (validatorClassName != null)
         {
            try
            {
               ClassLoader loader = application.getContext().getClassLoader();

               //
               if (validatorClassName != null)
               {
                  // Load the class
                  Class preferencesValidatorClass = loader.loadClass(validatorClassName);
                  preferencesValidator = (PreferencesValidator)preferencesValidatorClass.newInstance();
               }
            }
            catch (ClassNotFoundException e)
            {
               log.error("Class for preference validator not found", e);
            }
            catch (InstantiationException e)
            {
               log.error("Cannot instantiate preference validator", e);
            }
            catch (IllegalAccessException e)
            {
               throw new Error();
            }
         }
      }

      //
      ResourceBundleManager bundleManager = PortletResourceBundleFactory.createResourceBundleManager(info.getBundleManager(), info);

      // Portlet config object
      PortletConfig config = new PortletConfigImpl(info, application.info, application.portletContext, bundleManager);

      // Finally initialize the porlet instance
      try
      {
         log.debug("Loading portlet class " + className);
         Class portletClass = application.getContext().getClassLoader().loadClass(className);
         log.debug("Creating portlet object " + className);
         Portlet portlet = (Portlet)portletClass.newInstance();
         log.debug("Created portlet object " + className);
         initPortlet(portlet, config);
         log.debug("Initialized portlet object " + className);

         // We are safe, update state
         this.portlet = portlet;
         this.config = config;
         this.started = true;

         //
         buildFilterChains();

         // Let invocation flow in
         valve.open();
      }
      catch (IllegalAccessException e)
      {
         throw new PortletInitializationException("Portlet class not accessible " + className, e);
      }
      catch (ClassNotFoundException e)
      {
         throw new PortletInitializationException("Portlet class not found " + className, e);
      }
      catch (InstantiationException e)
      {
         throw new PortletInitializationException("Portlet class cannot be instantiated " + className, e);
      }
      catch (PortletException e)
      {
         throw new PortletInitializationException("The portlet " + getId() + " threw a portlet exception during init", e);
      }
      catch (RuntimeException e)
      {
         throw new PortletInitializationException("The portlet " + getId() + " threw a runtime exception during init", e);
      }
      catch (Error e)
      {
         throw new PortletInitializationException("The portlet " + getId() + " threw an error during init", e);
      }
   }

   private void buildFilterChains()
   {
      List<ActionFilter> actionFilterList = builderFilterList(ActionFilter.class);
      List<EventFilter> eventFilterList = builderFilterList(EventFilter.class);
      List<RenderFilter> renderFilterList = builderFilterList(RenderFilter.class);
      List<ResourceFilter> resourceFilterList = builderFilterList(ResourceFilter.class);

      // Add invoker
      actionFilterList.add(invoker);
      eventFilterList.add(invoker);
      renderFilterList.add(invoker);
      resourceFilterList.add(invoker);

      //
      this.actionFilterList = Collections.unmodifiableList(actionFilterList);
      this.eventFilterList = Collections.unmodifiableList(eventFilterList);
      this.renderFilterList = Collections.unmodifiableList(renderFilterList);
      this.resourceFilterList = Collections.unmodifiableList(resourceFilterList);
   }

   private <T extends PortletFilter> List<T> builderFilterList(Class<T> type)
   {
      ArrayList<T> list = new ArrayList<T>();
      for (String filterRef : info.getFilterRefs())
      {
         for (org.gatein.pc.portlet.impl.jsr168.PortletFilterImpl filter : filters)
         {
            if (filter.getId().equals(filterRef))
            {
               T filterInstance = filter.instance(type);
               if (filterInstance != null)
               {
                  list.add(filterInstance);
               }
            }
         }
      }
      return list;
   }

   public void stop()
   {
      // If the portlet is not started, we shouldn't be trying to stop it...
      if (started)
      {
         // Wait at most 60 seconds before all invocations are done
         log.debug("Trying to close the valve");
         boolean done = valve.closing(60000);
         if (!done)
         {
            log.warn("The valve is still holding invocations, continue anyway");
         }

         //
         valve.closed();

         //
         started = false;

         // Destroy the portlet object
         destroyPortlet(portlet);

         // Update state
         preferencesValidator = null;
         className = null;
         portlet = null;
         config = null;
         userDataConstraints = null;
      }
   }

   public PortletConfig getConfig()
   {
      return config;
   }

   public Portlet getPortlet()
   {
      return portlet;
   }

   // Cannot use covariant here as it will break the javabean property getter convention used by MC.
   public PortletApplication getPortletApplication()
   {
      return application;
   }

   public void setPortletApplication(PortletApplication application)
   {
      this.application = (PortletApplicationImpl)application;
   }

   public Valve getValve()
   {
      return valve;
   }

   public Set getUserDataConstraints()
   {
      return userDataConstraints;
   }

   public PreferencesValidator getPreferencesValidator()
   {
      return preferencesValidator;
   }

   public String toString()
   {
      return "PortletContainer[name=" + getId() + "]";
   }

   /** Initialize the portlet. */
   private void initPortlet(Portlet portlet, PortletConfig config) throws PortletException
   {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Set the war loader for the request
         ClassLoader newLoader = application.getContext().getClassLoader();
         Thread.currentThread().setContextClassLoader(newLoader);
         portlet.init(config);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(oldLoader);
      }
   }

   /** Destroy the portlet. */
   private void destroyPortlet(Portlet portlet)
   {
      ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         // Set the war loader for the request
         ClassLoader newLoader = application.getContext().getClassLoader();
         Thread.currentThread().setContextClassLoader(newLoader);
         if (portlet != null)
         {
            portlet.destroy();
         }
         else
         {
            log.debug("Cannot call destroy, portlet was null");
         }
      }
      catch (RuntimeException e)
      {
         log.error("The portlet threw a runtime exception", e);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(oldLoader);
      }
   }

   public PortletInvocationResponse dispatch(PortletInvocation invocation) throws PortletInvokerException, InvocationException
   {
      HttpServletRequest dreq = invocation.getDispatchedRequest();

      //
      PortletRequestImpl req;
      PortletResponseImpl resp;
      String phase;
      FilterChainImpl<?> chain;
      if (invocation instanceof ActionInvocation)
      {
         req = new ActionRequestImpl(this, (ActionInvocation)invocation);
         resp = new ActionResponseImpl((ActionInvocation)invocation, req);
         phase = PortletRequest.ACTION_PHASE;
         chain = new FilterChainImpl<ActionFilter>(actionFilterList, ActionFilter.class);
      }
      else if (invocation instanceof RenderInvocation)
      {
         req = new RenderRequestImpl(this, (RenderInvocation)invocation);
         resp = new RenderResponseImpl((RenderInvocation)invocation, req);
         phase = PortletRequest.RENDER_PHASE;
         chain = new FilterChainImpl<RenderFilter>(renderFilterList, RenderFilter.class);
      }
      else if (invocation instanceof EventInvocation)
      {
         req = new EventRequestImpl(this, (EventInvocation)invocation);
         resp = new EventResponseImpl((EventInvocation)invocation, req);
         phase = PortletRequest.EVENT_PHASE;
         chain = new FilterChainImpl<EventFilter>(eventFilterList, EventFilter.class);
      }
      else if (invocation instanceof ResourceInvocation)
      {
         req = new ResourceRequestImpl(this, (ResourceInvocation)invocation);
         resp = new ResourceResponseImpl((ResourceInvocation)invocation, req);
         phase = PortletRequest.RESOURCE_PHASE;
         chain = new FilterChainImpl<ResourceFilter>(resourceFilterList, ResourceFilter.class);
      }
      else
      {
        throw new InvocationException("Unexpected invocation " + invocation);
      }

      //
      try
      {
         dreq.setAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION, invocation);
         dreq.setAttribute(Constants.JAVAX_PORTLET_CONFIG, config);
         dreq.setAttribute(Constants.JAVAX_PORTLET_REQUEST, req);
         dreq.setAttribute(Constants.JAVAX_PORTLET_RESPONSE, resp);
         dreq.setAttribute(Constants.JAVAX_PORTLET_LIFECYCLE_PHASE, phase);

         //
         chain.doFilter(req, resp);

         //
         return resp.getResponse();
      }
      catch (NoClassDefFoundError e)
      {
         //
         return new ErrorResponse(e);
      }
      catch (Exception e)
      {
         log.error("The portlet threw an exception", e);

         //
         if (e instanceof PortletSecurityException)
         {
            return new SecurityErrorResponse(e);
         }
         else if (e instanceof UnavailableException)
         {
            UnavailableException ue = (UnavailableException)e;
            if (ue.isPermanent())
            {
               return new UnavailableResponse();
            }
            else
            {
               return new UnavailableResponse(ue.getUnavailableSeconds());
            }
         }
         else
         {
            // The exception is either a PortletException, an IOException or a RuntimeException
            return new ErrorResponse(e);
         }
      }
      finally
      {
         dreq.removeAttribute(ContextDispatcherInterceptor.REQ_ATT_COMPONENT_INVOCATION);
         dreq.removeAttribute(Constants.JAVAX_PORTLET_CONFIG);
         dreq.removeAttribute(Constants.JAVAX_PORTLET_REQUEST);
         dreq.removeAttribute(Constants.JAVAX_PORTLET_RESPONSE);
         dreq.removeAttribute(Constants.JAVAX_PORTLET_LIFECYCLE_PHASE);
      }
   }

   private class Invoker implements ActionFilter, EventFilter, RenderFilter, ResourceFilter
   {

      public void doFilter(ActionRequest req, ActionResponse resp, FilterChain chain) throws IOException, PortletException
      {
         portlet.processAction(req, resp);
      }

      public void doFilter(EventRequest req, EventResponse resp, FilterChain chain) throws IOException, PortletException
      {
         EventPortlet eventPortlet = (EventPortlet)portlet;
         eventPortlet.processEvent(req, resp);
      }

      public void doFilter(RenderRequest req, RenderResponse resp, FilterChain chain) throws IOException, PortletException
      {
         portlet.render(req, resp);
      }

      public void doFilter(ResourceRequest req, ResourceResponse resp, FilterChain chain) throws IOException, PortletException
      {
         ResourceServingPortlet servingPortlet = (ResourceServingPortlet)portlet;
         servingPortlet.serveResource(req, resp);
      }

      public void init(FilterConfig filterConfig) throws PortletException
      {
      }

      public void destroy()
      {
      }
   }

}
