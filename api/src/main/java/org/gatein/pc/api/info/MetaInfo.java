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
package org.gatein.pc.api.info;

import org.gatein.common.i18n.LocalizedString;

/**
 * Portlet metadata (display-name, title, short title, keywords).
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 6818 $
 * @since 2.4
 */
public interface MetaInfo
{

   /** Key for display name. */
   String DISPLAY_NAME = "display-name";

   /** Key for title. */
   String TITLE = "title";

   /** Key for short title. */
   String SHORT_TITLE = "short-title";

   /** Key for locale specific keywords associated with this portlet. The keywords are separated by commas. */
   String KEYWORDS = "keywords";

   /** Key for description. */
   String DESCRIPTION = "description";

   /**
    * Return the meta value of the portlet for a specific key.
    *
    * @param key the key
    * @return an internationalized value
    */
   LocalizedString getMetaValue(String key);
}
