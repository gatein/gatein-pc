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
package org.gatein.pc.portlet.impl.jsr168.api;

import org.gatein.pc.api.invocation.RenderInvocation;
import org.gatein.pc.api.invocation.response.FragmentResponse;
import org.gatein.pc.api.invocation.response.ContentResponse;
import org.gatein.pc.api.invocation.response.ResponseProperties;
import org.gatein.pc.api.info.PortletInfo;
import org.gatein.pc.api.info.CapabilitiesInfo;
import org.gatein.pc.api.info.ModeInfo;
import org.gatein.pc.api.cache.CacheControl;

import javax.portlet.RenderResponse;
import javax.portlet.PortletMode;
import java.util.Collection;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6704 $
 */
public class RenderResponseImpl extends MimeResponseImpl implements RenderResponse
{

   /** . */
   protected Set<org.gatein.pc.api.Mode> responseNextModes;

   /** The title if any. */
   protected String responseTitle;

   public RenderResponseImpl(RenderInvocation invocation, PortletRequestImpl preq)
   {
      super(invocation, preq);

      //
      this.responseNextModes = null;
      this.responseTitle = null;
   }

   public void setTitle(String title)
   {
      this.responseTitle = title;
   }

   public void setNextPossiblePortletModes(Collection<PortletMode> portletModes)
   {
      if (portletModes != null && portletModes.size() > 0)
      {
         if (responseNextModes ==  null)
         {
            responseNextModes = new LinkedHashSet<org.gatein.pc.api.Mode>(portletModes.size());
         }
         else
         {
            responseNextModes.clear();
         }

         //
         for (PortletMode portletMode : portletModes)
         {
            if (portletMode != null)
            {
               org.gatein.pc.api.Mode mode = org.gatein.pc.api.Mode.create(portletMode.toString());

               //
               if (preq.supportedModes.contains(mode))
               {
                  responseNextModes.add(mode);
               }
            }
            else
            {
               // Log
            }
         }
      }
   }

   public void setContentType(String contentType)
   {
      // Remove the unused appended charset first
      int index = contentType.indexOf(';');
      if (index != -1)
      {
         contentType = contentType.substring(0, index);
      }

      // Get the response media type
//         ContentInfo info = invocation.getContext().getMarkupInfo();
//         MediaType responseMediaType = info.getContentType();

      // Check if the requested media type is allowed as a subtype of the main response
//         if (!responseMediaType.isAllowedSubType(requestedMediaType))
//         {
//            throw new IllegalArgumentException("Content type not accepted");
//         }

      //
      org.gatein.pc.api.Mode currentMode = preq.invocation.getMode();

      org.gatein.common.net.media.MediaType mediaType = org.gatein.common.net.media.MediaType.create(contentType);

      PortletInfo info = preq.container.getInfo();
      CapabilitiesInfo capabilities = info.getCapabilities();
      Set<ModeInfo> compatibleModes = capabilities.getModes(mediaType);
      for (ModeInfo modeInfo : compatibleModes)
      {
         if (currentMode.equals(modeInfo.getMode()))
         {
            // Set the content type
            super.setContentType(contentType);

            //
            return;
         }
      }

      //
      throw new IllegalArgumentException("Mime type " + contentType + " not accepted as content type");
   }

   protected ContentResponse createMarkupResponse(ResponseProperties properties, Map<String, Object> attributeMap, String contentType, byte[] bytes, String chars, CacheControl cacheControl)
   {
      return new FragmentResponse(
         properties,
         attributeMap,
         contentType,
         bytes,
         chars,
         responseTitle,
         cacheControl,
         responseNextModes != null ? responseNextModes : preq.supportedModes);
   }
}
