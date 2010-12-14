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
package org.gatein.pc.test.tck;

import org.gatein.pc.controller.state.PortletPageNavigationalStateSerialization;
import org.gatein.pc.controller.state.PortletPageNavigationalState;
import org.gatein.common.io.Serialization;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Set;
import java.util.HashSet;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class TCKPageNavigationalStateSerialization implements Serialization<PortletPageNavigationalState>
{

   /** . */
   private final PortletPageNavigationalStateSerialization defaultSerialization;

   public TCKPageNavigationalStateSerialization(TCKStateControllerContext context)
   {
      this.defaultSerialization = new PortletPageNavigationalStateSerialization(context.defaultStateControllerContext);
   }

   public void serialize(PortletPageNavigationalState pageNavigationalState, OutputStream out) throws IOException, IllegalArgumentException
   {
      TCKPortletPageNavigationalState tckPageNavigationalState = (TCKPortletPageNavigationalState)pageNavigationalState;

      //
      DataOutputStream data = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);

      //
      defaultSerialization.serialize(tckPageNavigationalState.defaultState, out);

      //
      data.writeInt(tckPageNavigationalState.involvedPortlets.size());
      for (String involvedPortlet : tckPageNavigationalState.involvedPortlets)
      {
         data.writeUTF(involvedPortlet);
      }

      // Need a flush
      data.flush();
   }

   public PortletPageNavigationalState unserialize(InputStream in) throws IOException, IllegalArgumentException
   {
      //
      DataInputStream data = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);

      //
      PortletPageNavigationalState defaultState = defaultSerialization.unserialize(in);

      //
      int size = data.readInt();
      Set<String> involvedPortlets = new HashSet<String>(size);
      while (size-- > 0)
      {
         String involvedPortlet = data.readUTF();
         involvedPortlets.add(involvedPortlet);
      }

      //
      return new TCKPortletPageNavigationalState(defaultState, involvedPortlets);
   }
}
