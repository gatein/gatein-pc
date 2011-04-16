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

package org.gatein.pc.mc.metadata.impl;

import org.jboss.xb.binding.GenericObjectModelFactory;
import org.jboss.xb.binding.UnmarshallingContext;
import org.xml.sax.Attributes;

/**
 * Filter <code>setValue</code> method by doing a trimming before calling the next model.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision$
 */
public class ValueTrimmingFilter implements GenericObjectModelFactory
{

   private final GenericObjectModelFactory next;

   public ValueTrimmingFilter(GenericObjectModelFactory next) throws IllegalArgumentException
   {
      if (next == null)
      {
         throw new IllegalArgumentException("Cannot be null");
      }
      this.next = next;
   }

   public Object newChild(Object object, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      return next.newChild(object, nav, nsURI, localName, attrs);
   }

   public void addChild(Object parent, Object child, UnmarshallingContext nav, String nsURI, String localName)
   {
      next.addChild(parent, child, nav, nsURI, localName);
   }

   public void setValue(Object object, UnmarshallingContext nav, String nsURI, String localName, String value)
   {
      value = value.trim();
      next.setValue(object, nav, nsURI, localName, value);
   }

   public Object newRoot(Object root, UnmarshallingContext nav, String nsURI, String localName, Attributes attrs)
   {
      return next.newRoot(root, nav, nsURI, localName, attrs);
   }

   public Object completeRoot(Object root, UnmarshallingContext nav, String nsURI, String localName)
   {
      return next.completeRoot(root, nav, nsURI, localName);
   }

}