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

import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.FragmentResponse;
import org.gatein.pc.portlet.PortletInvokerInterceptor;
import org.gatein.pc.portlet.aspects.cache.ContentRef;
import org.gatein.pc.portlet.aspects.cache.StrongContentRef;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.api.invocation.response.RevalidateMarkupResponse;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.StateString;
import org.gatein.pc.api.ParametersStateString;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.spi.UserContext;
import org.gatein.pc.api.cache.CacheControl;
import org.gatein.common.util.ParameterMap;
import org.gatein.pc.api.WindowState;
import org.gatein.pc.api.Mode;

import java.io.Serializable;
import java.util.Map;

/**
 * Cache markup on the portal.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public class ConsumerCacheInterceptor extends PortletInvokerInterceptor
{

   public PortletInvocationResponse invoke(PortletInvocation invocation) throws IllegalArgumentException, PortletInvokerException
   {
      // Compute the cache key
      String scopeKey = "cached_markup." + invocation.getWindowContext().getId();

      // We use the principal scope to avoid security issues like a user loggedout seeing a cached entry
      // by a previous logged in user
      UserContext userContext = invocation.getUserContext();

      //
      if (invocation instanceof RenderInvocation)
      {
         RenderInvocation renderInvocation = (RenderInvocation)invocation;

         //
         StateString navigationalState = renderInvocation.getNavigationalState();
         Map<String, String[]> publicNavigationalState = renderInvocation.getPublicNavigationalState();
         WindowState windowState = renderInvocation.getWindowState();
         Mode mode = renderInvocation.getMode();

         //
         CacheEntry cachedEntry = (CacheEntry)userContext.getAttribute(scopeKey);

         //
         if (cachedEntry != null)
         {
            // Check time validity for fragment
            boolean useEntry = false;
            StateString entryNavigationalState = cachedEntry.navigationalState;
            Map<String, String[]> entryPublicNavigationalState = cachedEntry.publicNavigationalState;

            // Then check nav state equality
            if (navigationalState == null)
            {
               if (entryNavigationalState == null)
               {
                  useEntry = true;
               }
               else if (entryNavigationalState instanceof ParametersStateString)
               {
                  // We consider a parameters state string empty equivalent to a null value
                  useEntry = ((ParametersStateString)entryNavigationalState).getSize() == 0;
               }
            }
            else if (entryNavigationalState == null)
            {
               if (navigationalState instanceof ParametersStateString)
               {
                  useEntry = ((ParametersStateString)navigationalState).getSize() == 0;
               }
            }
            else
            {
               useEntry = navigationalState.equals(entryNavigationalState);
            }

            // Check public nav state equality
            if (useEntry)
            {
               if (publicNavigationalState == null)
               {
                  if (entryPublicNavigationalState == null)
                  {
                     //
                  }
                  else
                  {
                     useEntry = entryPublicNavigationalState.size() == 0;
                  }
               }
               else if (entryPublicNavigationalState == null)
               {
                  useEntry = publicNavigationalState.size() == 0;
               }
               else
               {
                  ParameterMap publicPM = ParameterMap.wrap(publicNavigationalState);
                  ParameterMap entryPM = ParameterMap.wrap(entryPublicNavigationalState);
                  useEntry = publicPM.equals(entryPM);
               }
            }

            // Then check window state equality
            useEntry &= windowState.equals(cachedEntry.windowState);

            // Then check mode equality
            useEntry &= mode.equals(cachedEntry.mode);

            // Clean if it is null
            if (!useEntry)
            {
               cachedEntry = null;
               userContext.setAttribute(scopeKey, null);
            }
         }

         //
         final ContentResponse cachedContent = cachedEntry != null ? cachedEntry.contentRef.getContent() : null;

         // If no valid content we must invoke
         long now = System.currentTimeMillis();
         if (cachedContent == null || cachedEntry.expirationTimeMillis < now)
         {
            // Set validation token for revalidation only we have have a content
            if (cachedContent != null)
            {
               renderInvocation.setValidationToken(cachedEntry.validationToken);
            }

            // Invoke
            PortletInvocationResponse response = super.invoke(invocation);

            // If we had a cached content that was revalidated we substitute
            if (response instanceof RevalidateMarkupResponse && cachedContent != null)
            {
               // Normally we receive such response when the validation token was set which implies we have an existing content
               // We substitute with the appropriate content response
               RevalidateMarkupResponse revalidate = (RevalidateMarkupResponse)response;
               CacheControl control = revalidate.getCacheControl();
               if (cachedContent instanceof FragmentResponse)
               {
                  response = new FragmentResponse((FragmentResponse)cachedContent, control);
               }
               else
               {
                  response = new ContentResponse(cachedContent, control);
               }
            }

            // If we have a content cache it whenever it is possible
            if (response instanceof ContentResponse)
            {
               ContentResponse contentResponse = (ContentResponse)response;
               CacheControl control = contentResponse.getCacheControl();

               //
               if (control != null)
               {
                  // Compute expiration time, i.e when it will expire
                  long expirationTimeMillis = 0;
                  if (control.getExpirationSecs() == -1)
                  {
                     expirationTimeMillis = Long.MAX_VALUE;
                  }
                  else if (control.getExpirationSecs() > 0)
                  {
                     expirationTimeMillis = System.currentTimeMillis() + control.getExpirationSecs() * 1000;
                  }

                  // Cache if we can
                  if (expirationTimeMillis > 0)
                  {
                     // Use validation token if any
                     String validationToken = null;
                     if (control.getValidationToken() != null)
                     {
                        validationToken = control.getValidationToken();
                     }
                     else if (cachedEntry != null)
                     {
                        validationToken = cachedEntry.validationToken;
                     }

                     CacheEntry cacheEntry = new CacheEntry(
                        navigationalState,
                        publicNavigationalState,
                        windowState,
                        mode,
                        contentResponse,
                        expirationTimeMillis,
                        validationToken);
                     userContext.setAttribute(scopeKey, cacheEntry);
                  }

               }
            }

            //
            return response;
         }
         else
         {
            // Use the cached content
            return cachedContent;
         }
      }
      else
      {
         // Invalidate
         userContext.setAttribute(scopeKey, null);

         // Invoke
         return super.invoke(invocation);
      }
   }

   /**
    * Encapsulate cache information.
    */
   private static class CacheEntry implements Serializable
   {

      /** The entry navigational state. */
      private final StateString navigationalState;

      /** . */
      private final WindowState windowState;

      /** . */
      private final org.gatein.pc.api.Mode mode;

      /** . */
      private final Map<String, String[]> publicNavigationalState;

      /** The timed content. */
      private final ContentRef contentRef;

      /** . */
      private final long expirationTimeMillis;

      /** . */
      private final String validationToken;

      public CacheEntry(
         StateString navigationalState,
         Map<String, String[]> publicNavigationalState,
         org.gatein.pc.api.WindowState windowState,
         org.gatein.pc.api.Mode mode,
         ContentResponse content,
         long expirationTimeMillis,
         String validationToken)
      {
         if (expirationTimeMillis <= 0)
         {
            throw new IllegalArgumentException();
         }
         this.navigationalState = navigationalState;
         this.windowState = windowState;
         this.mode = mode;
         this.publicNavigationalState = publicNavigationalState;
         this.contentRef = new StrongContentRef(content);
         this.expirationTimeMillis = expirationTimeMillis;
         this.validationToken = validationToken;
      }
   }
}
