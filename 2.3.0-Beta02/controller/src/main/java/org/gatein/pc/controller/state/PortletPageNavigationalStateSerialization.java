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
package org.gatein.pc.controller.state;

import org.gatein.pc.api.Mode;
import org.gatein.pc.api.WindowState;
import org.gatein.common.io.Serialization;
import org.gatein.pc.api.StateString;

import javax.xml.namespace.QName;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implement serialization of a page navigational state in a compact manner.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PortletPageNavigationalStateSerialization implements Serialization<PortletPageNavigationalState>
{

   /** . */
   private static final Map<org.gatein.pc.api.WindowState, Integer> windowStateToCode = new HashMap<WindowState, Integer>();

   /** . */
   private static final Map<Mode, Integer> modeToCode = new HashMap<Mode, Integer>();

   /** . */
   private static final Map<Integer, org.gatein.pc.api.WindowState> codeToWindowState = new HashMap<Integer, org.gatein.pc.api.WindowState>();

   /** . */
   private static final Map<Integer, org.gatein.pc.api.Mode> codeToMode = new HashMap<Integer, Mode>();

   static
   {
      windowStateToCode.put(WindowState.NORMAL, 1);
      windowStateToCode.put(org.gatein.pc.api.WindowState.MINIMIZED, 2);
      windowStateToCode.put(WindowState.MAXIMIZED, 3);
      codeToWindowState.put(1, WindowState.NORMAL);
      codeToWindowState.put(2, org.gatein.pc.api.WindowState.MINIMIZED);
      codeToWindowState.put(3, org.gatein.pc.api.WindowState.MAXIMIZED);
      modeToCode.put(Mode.VIEW, 1);
      modeToCode.put(org.gatein.pc.api.Mode.EDIT, 2);
      modeToCode.put(Mode.HELP, 3);
      codeToMode.put(1, Mode.VIEW);
      codeToMode.put(2, Mode.EDIT);
      codeToMode.put(3, Mode.HELP);
   }

   /** . */
   private final StateControllerContext context;

   public PortletPageNavigationalStateSerialization(StateControllerContext context)
   {
      if (context == null)
      {
         throw new IllegalArgumentException();
      }
      this.context = context;
   }

   public void serialize(PortletPageNavigationalState pageNavigationalState, OutputStream out) throws IOException, IllegalArgumentException
   {
      DataOutputStream data = out instanceof DataOutputStream ? (DataOutputStream)out : new DataOutputStream(out);

      //
      data.writeInt(pageNavigationalState.getPortletWindowIds().size());
      for (String windowId : pageNavigationalState.getPortletWindowIds())
      {
         data.writeUTF(windowId);
         PortletWindowNavigationalState windowNS = pageNavigationalState.getPortletWindowNavigationalState(windowId);
         byte header = 0;
         int decision = 0;
         if (windowNS.getPortletNavigationalState() != null)
         {
            header |= 1;
            decision |= 1;
         }
         if (windowNS.getWindowState() != null)
         {
            Integer value = windowStateToCode.get(windowNS.getWindowState());
            if (value != null)
            {
               header |= value << 1;
            }
            else
            {
               header |= 4 << 1;
               decision |= 2;
            }
         }
         if (windowNS.getMode() != null)
         {
            Integer value = modeToCode.get(windowNS.getMode());
            if (value != null)
            {
               header |= value << 4;
            }
            else
            {
               header |= 4 << 4;
               decision |= 4;
            }
         }
         data.writeByte(header);
         if ((decision & 1) != 0)
         {
            windowNS.getPortletNavigationalState().writeTo(data);
         }
         if ((decision & 2) != 0)
         {
            data.writeUTF(windowNS.getWindowState().toString());
         }
         if ((decision & 4) != 0)
         {
            data.writeUTF(windowNS.getMode().toString());
         }
      }

      //
      Set<QName> publicNames = pageNavigationalState.getPublicNames();
      data.writeInt(publicNames.size());
      for (QName name : pageNavigationalState.getPublicNames())
      {
         data.writeUTF(name.getNamespaceURI());
         data.writeUTF(name.getLocalPart());
         String[] values = pageNavigationalState.getPublicNavigationalState(name);
         int length = values.length;
         data.writeInt(length);
         for (String value : values)
         {
            data.writeUTF(value);
         }
      }

      // Need to flush before returning otherwise bytes may stay in the buffer when we allocated
      // a data stream
      data.flush();
   }

   public PortletPageNavigationalState unserialize(InputStream in) throws IOException, IllegalArgumentException
   {
      DataInputStream data = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);

      //
      PortletPageNavigationalState pageNS = context.createPortletPageNavigationalState(true);

      //
      int size = data.readInt();
      while (size-- > 0)
      {
         String windowId = data.readUTF();
         byte header = data.readByte();
         StateString portletNS = null;
         if ((header & 1) != 0)
         {
            portletNS = StateString.create(data);
         }
         org.gatein.pc.api.WindowState windowState = null;
         int windowStateHeader = (header & (7 << 1)) >> 1;
         switch (windowStateHeader)
         {
            case 0:
               break;
            case 4:
               windowState = org.gatein.pc.api.WindowState.create(data.readUTF());
               break;
            default:
               windowState = codeToWindowState.get(windowStateHeader);
         }
         org.gatein.pc.api.Mode mode = null;
         int modeStateHeader = (header & (7 << (1 + 3))) >> (1 + 3);
         switch (modeStateHeader)
         {
            case 0:
               break;
            case 4:
               mode = Mode.create(data.readUTF());
               break;
            default:
               mode = codeToMode.get(modeStateHeader);
         }
         PortletWindowNavigationalState windowNS = new PortletWindowNavigationalState(portletNS, mode, windowState);
         pageNS.setPortletWindowNavigationalState(windowId, windowNS);
      }

      //
      size = data.readInt();
      while (size-- > 0)
      {
         String namespaceURI = data.readUTF();
         String localName = data.readUTF();
         QName name = new QName(namespaceURI, localName);
         int length = data.readInt();
         String[] values = new String[length];
         for (int i = 0; i < length; i++)
         {
            values[i] = data.readUTF();
         }
         pageNS.setPublicNavigationalState(name, values);
      }

      //
      return pageNS;
   }
}
