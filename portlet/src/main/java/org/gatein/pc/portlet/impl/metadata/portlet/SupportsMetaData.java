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
package org.gatein.pc.portlet.impl.metadata.portlet;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
@XmlType(name = "supportsType")
public class SupportsMetaData
{

   /** The supports id */
   @XmlAttribute(name = "id")
   private String id;

   /** The mime type */
   private String mimeType;

   /** The portletModes */
   private List<PortletModeMetaData> portletModes;

   /** The window states */
   private List<WindowStateMetaData> windowStates;
   
   public SupportsMetaData() {}
   
   public SupportsMetaData(String id)
   {
      this.id = id;
   }

   @XmlAttribute(name = "id")
   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   @XmlElement(name = "mime-type")
   public String getMimeType()
   {
      return mimeType;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   @XmlElement(name = "portlet-mode")
   public List<PortletModeMetaData> getPortletModes()
   {
      return this.portletModes;
   }

   public void setPortletModes(List<PortletModeMetaData> portletModes)
   {
      this.portletModes = portletModes;
   }
   
   public void addPortletMode(PortletModeMetaData portletMode)
   {
      if (this.portletModes == null)
      {
         this.portletModes = new ArrayList<PortletModeMetaData>();
      }
      this.portletModes.add(portletMode);
   }

   @XmlElement(name = "window-state",
         namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public List<WindowStateMetaData> getWindowStates()
   {
      return this.windowStates;
   }

   public void setWindowStates(List<WindowStateMetaData> windowStates)
   {
      this.windowStates = windowStates;
   }

   public void addWindowState(WindowStateMetaData windowState)
   {
      if (this.windowStates == null)
      {
         this.windowStates = new ArrayList<WindowStateMetaData>();
      }
      this.windowStates.add(windowState);
   }
}
