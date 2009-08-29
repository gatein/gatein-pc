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
package org.gatein.pc.portlet.aspects;

import org.gatein.common.util.UUIDGenerator;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.portlet.container.ContainerPortletInvoker;
import org.gatein.pc.portlet.container.PortletContainer;
import org.gatein.pc.portlet.impl.info.ContainerPortletInfo;
import org.gatein.pc.api.info.RuntimeOptionInfo;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.EventInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.ResourceInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.UpdateNavigationalStateResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class RequestAttributeConversationInterceptor extends PortletInvokerInterceptor
{

   /** . */
   private static final UUIDGenerator generator = new UUIDGenerator();

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      PortletContainer container = (PortletContainer)invocation.getAttribute(ContainerPortletInvoker.PORTLET_CONTAINER);
      ContainerPortletInfo containerInfo = (ContainerPortletInfo)container.getInfo();

      //
      Map<String, RuntimeOptionInfo> options = containerInfo.getRuntimeOptionsInfo();
      RuntimeOptionInfo scopingOption = options.get(RuntimeOptionInfo.JAVAX_PORTLET_ACTION_SCOPED_REQUEST_ATTRIBUTES);

      //
      int maxScope = 0;
      if (scopingOption != null && "true".equals(scopingOption.getValues().get(0)))
      {
         maxScope = 10;

         //
         List<String> values = scopingOption.getValues();
         if (values.size() >= 3 && RuntimeOptionInfo.NUMBER_OF_CACHED_SCOPES.equals(values.get(1)))
         {
            try
            {
               maxScope = Integer.parseInt(values.get(2));
            }
            catch (NumberFormatException e)
            {
               // Log that
            }
         }
      }

      //
      if (maxScope > 0)
      {
         if (invocation instanceof ActionInvocation)
         {
            return invoke(maxScope, (ActionInvocation)invocation);
         }
         else if (invocation instanceof EventInvocation)
         {
            return invoke(maxScope, (EventInvocation)invocation);
         }
         else if (invocation instanceof RenderInvocation)
         {
            return invoke((RenderInvocation)invocation);
         }
         else if (invocation instanceof ResourceInvocation)
         {
            return invoke((ResourceInvocation)invocation);
         }
         else
         {
            return super.invoke(invocation);
         }
      }
      else
      {
         return super.invoke(invocation);
      }
   }

   private PortletInvocationResponse invoke(int maxScope, ActionInvocation actionInvocation) throws IllegalArgumentException, PortletInvokerException
   {
      return invokeWithConversation(maxScope, new Conversation(), actionInvocation);
   }

   private PortletInvocationResponse invoke(int maxScope, EventInvocation eventInvocation) throws IllegalArgumentException, PortletInvokerException
   {
      Conversation conversation = loadConversation(eventInvocation);

      //
      if (conversation == null || conversation.rendered)
      {
         conversation = new Conversation();
      }

      //
      return invokeWithConversation(maxScope, conversation, eventInvocation);
   }

   private PortletInvocationResponse invoke(RenderInvocation renderInvocation) throws IllegalArgumentException, PortletInvokerException
   {
      Conversation conversation = loadConversation(renderInvocation);

      //
      if (conversation != null)
      {
         conversation.rendered = true;

         //
         return invokeWithConversation(Integer.MAX_VALUE, conversation, renderInvocation);
      }
      else
      {
         return super.invoke(renderInvocation);
      }
   }

   private PortletInvocationResponse invoke(ResourceInvocation resourceInvocation) throws IllegalArgumentException, PortletInvokerException
   {
      Conversation conversation = loadConversation(resourceInvocation);

      //
      if (conversation != null)
      {
         return invokeWithConversation(Integer.MAX_VALUE, conversation, resourceInvocation);
      }
      else
      {
         return super.invoke(resourceInvocation);
      }
   }

   private Conversation loadConversation(PortletInvocation invocation)
   {
      ParametersStateString parameters = (ParametersStateString)invocation.getNavigationalState();

      //
      if (parameters != null)
      {
         String id = parameters.getValue("javax.portlet.as");

         //
         if (id != null)
         {
            // Get the http session if any
            HttpServletRequest request = invocation.getDispatchedRequest();
            HttpSession session = request.getSession(false);

            //
            if (session != null)
            {
               // Generate key
               String key = "org.gatein.pc.conversation." + id;

               // Get attributes
               return (Conversation)session.getAttribute(key);
            }
         }
      }

      //
      return null;
   }

   private PortletInvocationResponse invokeWithConversation(
      int maxScope,
      Conversation conversation,
      PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      invocation.setRequestAttributes(conversation.getAttributes());

      // Set the id parameter
      ParametersStateString inNS = (ParametersStateString)invocation.getNavigationalState();
      if (inNS == null)
      {
         inNS = ParametersStateString.create();
         invocation.setNavigationalState(inNS);
      }
      inNS.setValue("javax.portlet.as", conversation.id);

      //
      PortletInvocationResponse response = super.invoke(invocation);

      //
      if (response instanceof UpdateNavigationalStateResponse)
      {
         UpdateNavigationalStateResponse update = (UpdateNavigationalStateResponse)response;

         //
         Map<String, Object> attributes = update.getAttributes();

         //
         if (attributes != null && attributes.size() > 0)
         {
            ParametersStateString outNS = (ParametersStateString)update.getNavigationalState();
            outNS.setValue("javax.portlet.as", conversation.id);

            //
            conversation.setAttributes(attributes);

            //
            if (!conversation.stored && maxScope > 0)
            {
               // Get the http session
               HttpServletRequest request = invocation.getDispatchedRequest();
               HttpSession session = request.getSession();

               // Make a first can
               int size = 0;
               for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();)
               {
                  String name = (String)e.nextElement();
                  if (name.startsWith("org.gatein.pc.conversation."))
                  {
                     size++;
                  }
               }

               // Destroy existing conversations if needed
               if (size >= maxScope)
               {
                  LinkedList<Conversation> allConversations = null;
                  for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();)
                  {
                     String name = (String)e.nextElement();
                     if (name.startsWith("org.gatein.pc.conversation."))
                     {
                        if (allConversations == null)
                        {
                           allConversations = new LinkedList<Conversation>();
                        }
                        allConversations.add((Conversation)session.getAttribute(name));
                     }
                  }

                  //
                  if (allConversations != null)
                  {
                     Collections.sort(allConversations, evictor);

                     // Remove until we have something satisfactory
                     while (allConversations.size() >= maxScope)
                     {
                        Conversation toRemove = allConversations.removeLast();
                        String key = "org.gatein.pc.conversation." + toRemove.id;
                        session.removeAttribute(key);
                     }
                  }
               }

               //
               String key = "org.gatein.pc.conversation." + conversation.id;
               session.setAttribute(key, conversation);
            }
         }
         else
         {
            if (conversation.stored)
            {
               // Get the http session
               HttpServletRequest request = invocation.getDispatchedRequest();
               HttpSession session = request.getSession();

               //
               String key = "org.gatein.pc.conversation." + conversation.id;
               session.removeAttribute(key);
            }
         }
      }

      //
      return response;
   }

   private static class Conversation implements HttpSessionBindingListener
   {

      /** . */
      private static final Map<String, Object> EMPTY_ATTRIBUTES = Collections.emptyMap();

      /** . */
      private Map<String, Object> attributes;

      /** . */
      private Map<String, Object> unmodifiableAttributes;

      /** . */
      private final String id = generator.generateKey();

      /** . */
      private boolean rendered = false;

      /** . */
      private final long creationDateMillis = System.currentTimeMillis();

      private boolean stored;

      private Conversation()
      {
         this.attributes = null;
         this.unmodifiableAttributes = EMPTY_ATTRIBUTES;
      }

      private Conversation(Map<String, Object> attributes)
      {
         this.attributes = new HashMap<String, Object>(attributes);
         this.unmodifiableAttributes = Collections.unmodifiableMap(attributes);
      }

      public Map<String, Object> getAttributes()
      {
         return unmodifiableAttributes;
      }

      public void setAttributes(Map<String, Object> attributes)
      {
         if (this.attributes == null)
         {
            this.attributes = new HashMap<String, Object>(attributes);
            this.unmodifiableAttributes = Collections.unmodifiableMap(this.attributes);
         }
         else
         {
            this.attributes.clear();
            this.attributes.putAll(attributes);
         }
      }

      public void valueBound(HttpSessionBindingEvent event)
      {
         stored = true;
      }

      public void valueUnbound(HttpSessionBindingEvent event)
      {
         stored = false;
      }
   }

   private static final Comparator<Conversation> evictor = new Comparator<Conversation>()
   {
      public int compare(Conversation o1, Conversation o2)
      {
         if (o1.creationDateMillis > o2.creationDateMillis)
         {
            return -1;
         }
         else if (o1.creationDateMillis == o2.creationDateMillis)
         {
            return 0;
         }
         else
         {
            return 1;
         }
      }
   };
}
