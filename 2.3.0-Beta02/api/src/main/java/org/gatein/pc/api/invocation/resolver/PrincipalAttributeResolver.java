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
package org.gatein.pc.api.invocation.resolver;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 7228 $
 */
public class PrincipalAttributeResolver extends AbstractSessionAttributeResolver
{

   /** . */
   private String cachedPrincipalName;

   /** . */
   private String cachedMapKey;

   public PrincipalAttributeResolver(HttpServletRequest req)
   {
      super(req);
   }

   protected String getMapKey()
   {
      Principal principal = req.getUserPrincipal();

      //
      if (cachedMapKey != null)
      {
         if (cachedPrincipalName == null)
         {
            if (principal != null)
            {
               cachedMapKey = null;
            }
         }
         else
         {
            if (principal == null || (cachedPrincipalName.equals(principal.getName()) == false))
            {
               cachedMapKey = null;
            }
         }
      }

      //
      if (cachedMapKey == null)
      {
         if (principal == null)
         {
            cachedMapKey = "portal.principal";
            cachedPrincipalName = null;
         }
         else
         {
            cachedMapKey = "portal.principal." + principal.getName();
            cachedPrincipalName = principal.getName();
         }
      }

      //
      return cachedMapKey;
   }
}