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
package org.gatein.pc.portlet.impl.metadata;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.gatein.pc.portlet.impl.metadata.common.DescribableMetaData;
import org.gatein.pc.portlet.impl.metadata.PortletMetaDataConstants;

/**
 * @author <a href="mailto:emuckenh@redhat.com">Emanuel Muckenhuber</a>
 * @version $Revision$
 */
@XmlType(name = "custom-portlet-modeType")
public class CustomPortletModeMetaData extends DescribableMetaData
{

   /** The custom portlet mode id*/
   private String id;

   /** The portlet mode */
   private String portletMode;

   /** Is portal managed */
   private boolean portalManaged = true;

   public CustomPortletModeMetaData() {}
   
   public CustomPortletModeMetaData(String id)
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

   @XmlElement(name = "portlet-mode")
   public String getPortletMode()
   {
      return portletMode;
   }

   public void setPortletMode(String portletMode)
   {
      this.portletMode = portletMode;
   }

   @XmlElement(name = "portal-managed", namespace = PortletMetaDataConstants.PORTLET_JSR_286_NS)
   public boolean isPortalManaged()
   {
      return portalManaged;
   }

   public void setPortalManaged(boolean portalManaged)
   {
      this.portalManaged = portalManaged;
   }
   
}
