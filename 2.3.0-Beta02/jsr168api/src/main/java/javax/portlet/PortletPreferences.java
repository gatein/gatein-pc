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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * The <CODE>PortletPreferences</CODE> interface allows the portlet to store configuration data. It is not the purpose
 * of this interface to replace general purpose databases.
 * <p/>
 * There are two different types of preferences: <ul> <li>modifiable preferences - these preferences can be changed by
 * the portlet in any standard portlet mode (<code>EDIT, HELP, VIEW</code>). Per default every preference is modifiable.
 * <li>read-only preferences - these preferences cannot be changed by the portlet in any standard portlet mode, but may
 * be changed by administrative modes. Preferences are read-only, if the are defined in the deployment descriptor with
 * <code>read-only</code> set to <code>true</code>, or if the portlet container restricts write access. </ul>
 * <p/>
 * Changes are persisted when the <code>store</code> method is called. The <code>store</code> method can only be invoked
 * within the scope of a <code>processAction</code> call. Changes that are not persisted are discarded when the
 * <code>processAction</code> or <code>render</code> method ends.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PortletPreferences
{
   /**
    * Returns a <code>Map</code> of the preferences.
    * <p/>
    * The values in the returned <code>Map</code> are from type String array (<code>String[]</code>).
    * <p/>
    * If no preferences exist this method returns an empty <code>Map</code>.
    *
    * @return an immutable <code>Map</code> containing preference names as keys and preference values as map values, or
    *         an empty <code>Map</code> if no preference exist. The keys in the preference map are of type String. The
    *         values in the preference map are of type String array (<code>String[]</code>).
    */
   Map getMap();

   /**
    * Returns all of the keys that have an associated value, or an empty <code>Enumeration</code> if no keys are
    * available.
    *
    * @return an Enumeration of the keys that have an associated value, or an empty <code>Enumeration</code> if no keys
    *         are available.
    */
   Enumeration getNames();

   /**
    * Returns the first String value associated with the specified key of this preference. If there is one or more
    * preference values associated with the given key it returns the first associated value. If there are no preference
    * values associated with the given key, or the backing preference database is unavailable, it returns the given
    * default value.
    *
    * @param key key for which the associated value is to be returned
    * @param def the value to be returned in the event that there is no value available associated with this
    *            <code>key</code>.
    * @return the value associated with <code>key</code>, or <code>def</code> if no value is associated with
    *         <code>key</code>, or the backing store is inaccessible.
    * @throws java.lang.IllegalArgumentException
    *          if <code>key</code> is <code>null</code>. (A <code>null</code> value for <code>def</code> <i>is</i>
    *          permitted.)
    * @see #getValues(String,String[])
    */
   String getValue(String key, String def) throws IllegalArgumentException;

   /**
    * Returns the String array value associated with the specified key in this preference.
    * <p/>
    * <p>Returns the specified default if there is no value associated with the key, or if the backing store is
    * inaccessible.
    * <p/>
    * <p>If the implementation supports <i>stored defaults</i> and such a default exists and is accessible, it is used
    * in favor of the specified default.
    *
    * @param key key for which associated value is to be returned.
    * @param def the value to be returned in the event that this preference node has no value associated with
    *            <code>key</code> or the associated value cannot be interpreted as a String array, or the backing store
    *            is inaccessible.
    * @return the String array value associated with <code>key</code>, or <code>def</code> if the associated value does
    *         not exist.
    * @throws java.lang.IllegalArgumentException
    *          if <code>key</code> is <code>null</code>.  (A <code>null</code> value for <code>def</code> <i>is</i>
    *          permitted.)
    * @see #getValue(String,String)
    */
   String[] getValues(String key, String[] def) throws IllegalArgumentException;

   /**
    * Returns true, if the value of this key cannot be modified by the user.
    * <p/>
    * Modifiable preferences can be changed by the portlet in any standard portlet mode (<code>EDIT, HELP, VIEW</code>).
    * Per default every preference is modifiable.
    * <p/>
    * Read-only preferences cannot be changed by the portlet in any standard portlet mode, but inside of custom modes it
    * may be allowed changing them. Preferences are read-only, if they are defined in the deployment descriptor with
    * <code>read-only</code> set to <code>true</code>, or if the portlet container restricts write access.
    *
    * @return false, if the value of this key can be changed, or if the key is not known
    * @throws java.lang.IllegalArgumentException
    *          if <code>key</code> is <code>null</code>.
    */
   boolean isReadOnly(String key) throws IllegalArgumentException;

   /**
    * Resets or removes the value associated with the specified key.
    * <p/>
    * If this implementation supports stored defaults, and there is such a default for the specified preference, the
    * given key will be reset to the stored default.
    * <p/>
    * If there is no default available the key will be removed.
    *
    * @param key to reset
    * @throws java.lang.IllegalArgumentException
    *                           if key is <code>null</code>.
    * @throws ReadOnlyException if this preference cannot be modified for this request
    */
   void reset(String key) throws IllegalArgumentException, ReadOnlyException;

   /**
    * Associates the specified String value with the specified key in this preference.
    * <p/>
    * The key cannot be <code>null</code>, but <code>null</code> values for the value parameter are allowed.
    *
    * @param key   key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    * @throws ReadOnlyException if this preference cannot be modified for this request
    * @throws java.lang.IllegalArgumentException
    *                           if key is <code>null</code>, or <code>key.length()</code> or <code>value.length</code>
    *                           are to long. The maximum length for key and value are implementation specific.
    * @see #setValues(String,String[])
    */
   void setValue(String key, String value) throws IllegalArgumentException, ReadOnlyException;

   /**
    * Associates the specified String array value with the specified key in this preference.
    * <p/>
    * The key cannot be <code>null</code>, but <code>null</code> values in the values parameter are allowed.
    *
    * @param key    key with which the  value is to be associated
    * @param values values to be associated with key
    * @throws java.lang.IllegalArgumentException
    *                           if key is <code>null</code>, or <code>key.length()</code> is to long or
    *                           <code>value.size</code> is to large.  The maximum length for key and maximum size for
    *                           value are implementation specific.
    * @throws ReadOnlyException if this preference cannot be modified for this request
    * @see #setValue(String,String)
    */
   void setValues(String key, String[] values) throws IllegalArgumentException, ReadOnlyException;

   /**
    * Commits all changes made to the preferences via the <code>set</code> methods in the persistent store.
    * <p/>
    * If this call returns succesfull, all changes are made persistent. If this call fails, no changes are made in the
    * persistent store. This call is an atomic operation regardless of how many preference attributes have been
    * modified.
    * <p/>
    * All changes made to preferences not followed by a call to the <code>store</code> method are discarded when the
    * portlet finishes the <code>processAction</code> method.
    * <p/>
    * If a validator is defined for this preferences in the deployment descriptor, this validator is called before the
    * actual store is performed to check wether the given preferences are vaild. If this check fails a
    * <code>ValidatorException</code> is thrown.
    *
    * @throws java.io.IOException if changes cannot be written into the backend store
    * @throws ValidatorException  if the validation performed by the associated validator fails
    * @throws java.lang.IllegalStateException
    *                             if this method is called inside a render call
    * @see PreferencesValidator
    */
   void store() throws IOException, ValidatorException;
}
