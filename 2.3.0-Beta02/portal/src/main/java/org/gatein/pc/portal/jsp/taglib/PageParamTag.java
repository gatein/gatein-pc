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
package org.gatein.pc.portal.jsp.taglib;

import org.gatein.pc.portal.jsp.PortalRenderResponse;
import org.gatein.pc.portal.jsp.PortalPrepareResponse;
import org.gatein.pc.portal.jsp.PageParameterDef;

import javax.servlet.jsp.JspException;
import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public class PageParamTag extends PortalSimpleTagSupport
{

   /** . */
   private String namespaceURIAttr;

   /** . */
   private String localNameAttr;

   /** . */
   private String valueAttr;

   /** . */
   private String frozenAttr;

   public String getNamespaceURI()
   {
      return namespaceURIAttr;
   }

   public void setNamespaceURI(String namespaceURI)
   {
      this.namespaceURIAttr = namespaceURI;
   }

   public String getLocalName()
   {
      return localNameAttr;
   }

   public void setLocalName(String localName)
   {
      this.localNameAttr = localName;
   }

   public String getValue()
   {
      return valueAttr;
   }

   public void setValue(String value)
   {
      this.valueAttr = value;
   }

   public String getFrozen()
   {
      return frozenAttr;
   }

   public void setFrozen(String frozen)
   {
      this.frozenAttr = frozen;
   }

   public void doTag(PortalRenderResponse renderResponse) throws JspException, IOException
   {
   }

   public void doTag(PortalPrepareResponse prepareResponse) throws JspException, IOException
   {
      QName name = new QName(namespaceURIAttr, localNameAttr);
      boolean frozen = "true".equals(frozenAttr);
      PageParameterDef parameterDef = new PageParameterDef(name, valueAttr, frozen);
      prepareResponse.setPageParameterDef(parameterDef);
   }
}
