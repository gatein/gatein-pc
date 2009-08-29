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

package org.gatein.pc.test.controller;

import org.gatein.common.io.IOTools;
import org.gatein.pc.api.invocation.response.ContentResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class ResourceRenderer extends AbstractMarkupRenderer
{

   /** . */
   private ContentResponse fragment;

   /** . */
   private boolean sendNoContentResponseOnEmptyResource;

   public ResourceRenderer(ContentResponse response, boolean sendNoContentResponseOnEmptyResource)
   {
      super(response.getProperties());

      //
      this.sendNoContentResponseOnEmptyResource = sendNoContentResponseOnEmptyResource;
      this.fragment = response;
   }

   protected void renderContent(HttpServletResponse resp) throws IOException
   {
      if (fragment.getType() == ContentResponse.TYPE_EMPTY)
      {
         if (sendNoContentResponseOnEmptyResource)
         {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
         }
         else
         {
            String contentType = fragment.getContentType();
            if (contentType != null)
            {
               resp.setContentType(contentType);
            }

            //
            ServletOutputStream out = null;
            try
            {
               out = resp.getOutputStream();
            }
            finally
            {
               IOTools.safeClose(out);
            }
         }
      }
      else
      {
         String contentType = fragment.getContentType();
         if (contentType != null)
         {
            resp.setContentType(contentType);
         }

         //
         if (fragment.getType() == ContentResponse.TYPE_BYTES)
         {
            ServletOutputStream out = null;
            try
            {
               out = resp.getOutputStream();
               out.write(fragment.getBytes());
            }
            finally
            {
               IOTools.safeClose(out);
            }
         }
         else
         {
            Writer writer = null;
            try
            {
               writer = resp.getWriter();
               writer.write(fragment.getChars());
            }
            finally
            {
               writer.close();
            }
         }
      }
   }
}
