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
package org.gatein.pc.test.portlet.jsr168.ext.neverexpiringcache;

import org.gatein.pc.test.portlet.jsr168.ext.common.AbstractActionInvalidatesTestCase;
import org.gatein.pc.test.portlet.framework.UTP2;
import org.gatein.pc.test.portlet.framework.UTP3;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_NEVER_EXPIRING_CACHE_1
   })
public class ActionInvalidatesNeverExpiringCacheTestCase extends AbstractActionInvalidatesTestCase
{
   public ActionInvalidatesNeverExpiringCacheTestCase(PortletTestCase seq)
   {
      super(seq, UTP2.RENDER_JOIN_POINT, UTP2.ACTION_JOIN_POINT, UTP3.RENDER_JOIN_POINT);
   }
}
