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
package org.gatein.pc.api;

/**
 * Defines how a container URL should be formatted when rendered. 
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class URLFormat
{

   /** . */
   private final Boolean wantSecure;

   /** . */
   private final Boolean wantAuthenticated;

   /** . */
   private final Boolean wantRelative;

   /** . */
   private final Boolean wantEscapeXML;

   public URLFormat(Boolean wantSecure, Boolean wantAuthenticated, Boolean wantRelative, Boolean wantEscapeXML)
   {
      this.wantSecure = wantSecure;
      this.wantAuthenticated = wantAuthenticated;
      this.wantRelative = wantRelative;
      this.wantEscapeXML = wantEscapeXML;
   }

   public Boolean getWantSecure()
   {
      return wantSecure;
   }

   public Boolean getWantAuthenticated()
   {
      return wantAuthenticated;
   }

   public Boolean getWantRelative()
   {
      return wantRelative;
   }

   public Boolean getWantEscapeXML()
   {
      return wantEscapeXML;
   }
}
