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
package javax.portlet;

/**
 * The <CODE>PreferencesValidator</CODE> allows to validate the set of preferences of the associated portlet just before
 * they are stored in the persistent store.
 * <p/>
 * The portlet container invokes the <code>validate</code> method as part of the invocation of the <code>store</code>
 * method of the <code>PortletPreferences</code>.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PreferencesValidator
{
   /**
    * If the preferences values are successfully validated the call to this method must finish gracefully. Otherwise it
    * must throw a <code>ValidatorException</code>.
    *
    * @param preferences preferences to validate
    * @throws ValidatorException if the given preferences contains invalid settings
    */
   void validate(PortletPreferences preferences) throws ValidatorException;
}
