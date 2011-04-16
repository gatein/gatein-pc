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
package org.gatein.pc.test.portlet.jsr168.ext.expiringcache;

import org.gatein.pc.test.portlet.jsr168.ext.common.AbstractCacheMarkupTestCase;
import org.gatein.pc.test.portlet.jsr168.ext.common.NavigationalStateConfigurator;
import org.gatein.pc.test.portlet.framework.UTP6;
import org.gatein.pc.test.portlet.framework.UTP7;
import org.gatein.pc.test.unit.PortletTestCase;
import org.gatein.pc.test.unit.Assertion;
import org.gatein.pc.test.unit.annotations.TestCase;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
@TestCase({
   Assertion.EXT_EXPIRING_CACHE_6
   })
public class ExpiringCacheWithWindowStateTestCase extends AbstractCacheMarkupTestCase
{
   public ExpiringCacheWithWindowStateTestCase(PortletTestCase seq)
   {
      super(seq, UTP6.RENDER_JOIN_POINT, UTP6.ACTION_JOIN_POINT, UTP7.RENDER_JOIN_POINT, NavigationalStateConfigurator.WINDOW_STATE_CONFIGURATOR);
   }
}
