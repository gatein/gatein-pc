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

import java.util.Enumeration;

/**
 * The <CODE>PortletSession</CODE> interface provides a way to identify a user across more than one request and to store
 * transient information about that user.
 * <p/>
 * A <code>PortletSession</code> is created per user client per portlet application.
 * <p/>
 * A portlet can bind an object attribute into a <code>PortletSession</code> by name. The <code>PortletSession</code>
 * interface defines two scopes for storing objects: <ul> <li><code>APPLICATION_SCOPE</code>
 * <li><code>PORTLET_SCOPE</code> </ul> All objects stored in the session using the <code>APPLICATION_SCOPE</code> must
 * be available to all the portlets, servlets and JSPs that belongs to the same portlet application and that handles a
 * request identified as being a part of the same session. Objects stored in the session using the
 * <code>PORTLET_SCOPE</code> must be available to the portlet during requests for the same portlet window that the
 * objects where stored from. Attributes stored in the <code>PORTLET_SCOPE</code> are not protected from other web
 * components of the portlet application. They are just conveniently namespaced.
 * <p/>
 * The portlet session is based on the <code>HttpSession</code>. Therefore all <code>HttpSession</code> listeners do
 * apply to the portlet session and attributes set in the portlet session are visible in the <code>HttpSession</code>
 * and vice versa.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PortletSession
{
   /**
    * This constant defines an application wide scope for the session attribute. <code>APPLICATION_SCOPE</code> session
    * attributes enable Portlets within one portlet application to share data.
    * <p/>
    * Portlets may need to prefix attributes set in this scope with some ID, to avoid overwriting each other's
    * attributes in the case where two portlets of the same portlet definition are created.
    * <p/>
    * Value: <code>0x01</code>
    */
   int APPLICATION_SCOPE = 0x01;

   /**
    * This constant defines the scope of the session attribute to be private to the portlet and its included resources.
    * <p/>
    * Value: <code>0x02</code>
    */
   int PORTLET_SCOPE = 0x02;

   /**
    * Returns the object bound with the specified name in this session under the <code>PORTLET_SCOPE</code>, or
    * <code>null</code> if no object is bound under the name in that scope.
    *
    * @param name a string specifying the name of the object
    * @throws java.lang.IllegalStateException
    *          if this method is called on an invalidated session.
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    * @return the object with the specified name for the <code>PORTLET_SCOPE</code>.
    */
   Object getAttribute(String name) throws IllegalStateException, IllegalArgumentException;

   /**
    * Returns the object bound with the specified name in this session, or <code>null</code> if no object is bound under
    * the name in the given scope.
    *
    * @param name  a string specifying the name of the object
    * @param scope session scope of this attribute
    * @throws java.lang.IllegalStateException
    *          if this method is called on an invalidated session
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    * @return the object with the specified name
    */
   Object getAttribute(String name, int scope) throws IllegalStateException, IllegalArgumentException;

   /**
    * Returns an <code>Enumeration</code> of String objects containing the names of all the objects bound to this
    * session under the <code>PORTLET_SCOPE</code>, or an empty <code>Enumeration</code> if no attributes are
    * available.
    *
    * @throws java.lang.IllegalStateException
    *          if this method is called on an invalidated session
    * @return an <code>Enumeration</code> of <code>String</code> objects specifying the names of all the objects bound
    * to this session, or an empty <code>Enumeration</code> if no attributes are available.
    */
   Enumeration getAttributeNames() throws IllegalStateException;

   /**
    * Returns an <code>Enumeration</code> of String objects containing the names of all the objects bound to this
    * session in the given scope, or an empty <code>Enumeration</code> if no attributes are available in the given
    * scope.
    *
    * @param scope session scope of the attribute names
    * @throws java.lang.IllegalStateException
    *          if this method is called on an invalidated session
    * @return an <code>Enumeration</code> of <code>String</code> objects specifying the names of all the objects bound
    * to this session, or an empty <code>Enumeration</code> if no attributes are available in the given scope.
    */
   Enumeration getAttributeNames(int scope) throws IllegalStateException;

   /**
    * Returns the time when this session was created, measured in milliseconds since midnight January 1, 1970 GMT.
    *
    * @throws java.lang.IllegalStateException
    *          if this method is called on an invalidated session
    * @return a <code>long</code> specifying when this session was created, expressed in milliseconds since 1/1/1970
    * GMT
    */
   long getCreationTime() throws IllegalStateException;

   /**
    * Returns a string containing the unique identifier assigned to this session.
    *
    * @return a string specifying the identifier assigned to this session
    */
   String getId();

   /**
    * Returns the last time the client sent a request associated with this session, as the number of milliseconds since
    * midnight January 1, 1970 GMT.
    * <p/>
    * <p>Actions that your portlet takes, such as getting or setting a value associated with the session, do not affect
    * the access time.
    *
    * @return a <code>long</code> representing the last time the client sent a request associated with this session,
    * expressed in milliseconds since 1/1/1970 GMT
    */
   long getLastAccessedTime();

   /**
    * Returns the maximum time interval, in seconds, for which the portlet container keeps this session open between
    * client accesses. After this interval, the portlet container invalidates the session.  The maximum time interval
    * can be set with the <code>setMaxInactiveInterval</code> method. A negative time indicates the session should never
    * timeout.
    *
    * @return an integer specifying the number of seconds this session remains open between client requests
    * @see      #setMaxInactiveInterval
    */
   int getMaxInactiveInterval();

   /**
    * Returns the portlet application context associated with this session.
    *
    * @return the portlet application context
    */
   PortletContext getPortletContext();

   /**
    * Invalidates this session (all scopes) and unbinds any objects bound to it.
    * <p/>
    * Invalidating the portlet session will result in invalidating the underlying <code>HttpSession</code>
    *
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has already been invalidated
    */
   void invalidate() throws IllegalStateException;

   /**
    * Returns true if the client does not yet know about the session or if the client chooses not to join the session.
    *
    * @return <code>true</code> if the server has created a session, but the client has not joined yet.
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has already been invalidated
    */
   boolean isNew() throws IllegalStateException;

   /**
    * Removes the object bound with the specified name under the <code>PORTLET_SCOPE</code> from this session. If the
    * session does not have an object bound with the specified name, this method does nothing.
    *
    * @param name the name of the object to be removed from this session in the <code> PORTLET_SCOPE</code>.
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has been invalidated
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    */
   void removeAttribute(String name) throws IllegalStateException, IllegalArgumentException;

   /**
    * Removes the object bound with the specified name and the given scope from this session. If the session does not
    * have an object bound with the specified name, this method does nothing.
    *
    * @param name  the name of the object to be removed from this session
    * @param scope session scope of this attribute
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has been invalidated
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    */
   void removeAttribute(String name, int scope) throws IllegalStateException, IllegalArgumentException;

   /**
    * Binds an object to this session under the <code>PORTLET_SCOPE</code>, using the name specified. If an object of
    * the same name in this scope is already bound to the session, that object is replaced.
    * <p/>
    * <p>After this method has been executed, and if the new object implements <code>HttpSessionBindingListener</code>,
    * the container calls <code>HttpSessionBindingListener.valueBound</code>. The container then notifies any
    * <code>HttpSessionAttributeListeners</code> in the web application. <p>If an object was already bound to this
    * session that implements <code>HttpSessionBindingListener</code>, its <code>HttpSessionBindingListener.valueUnbound</code>
    * method is called.
    * <p/>
    * <p>If the value is <code>null</code>, this has the same effect as calling <code>removeAttribute()</code>.
    *
    * @param name  the name to which the object is bound under the <code>PORTLET_SCOPE</code>; this cannot be
    *              <code>null</code>.
    * @param value the object to be bound
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has been invalidated
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    */
   void setAttribute(String name, Object value) throws IllegalStateException, IllegalArgumentException;

   /**
    * Binds an object to this session in the given scope, using the name specified. If an object of the same name in
    * this scope is already bound to the session, that object is replaced.
    * <p/>
    * <p>After this method has been executed, and if the new object implements <code>HttpSessionBindingListener</code>,
    * the container calls <code>HttpSessionBindingListener.valueBound</code>. The container then notifies any
    * <code>HttpSessionAttributeListeners</code> in the web application. <p>If an object was already bound to this
    * session that implements <code>HttpSessionBindingListener</code>, its <code>HttpSessionBindingListener.valueUnbound</code>
    * method is called.
    * <p/>
    * <p>If the value is <code>null</code>, this has the same effect as calling <code>removeAttribute()</code>.
    *
    * @param name  the name to which the object is bound; this cannot be <code>null</code>.
    * @param value the object to be bound
    * @param scope session scope of this attribute
    * @throws java.lang.IllegalStateException
    *          if this method is called on a session which has been invalidated
    * @throws java.lang.IllegalArgumentException
    *          if name is <code>null</code>.
    */
   void setAttribute(String name, Object value, int scope) throws IllegalStateException, IllegalArgumentException;

   /**
    * Specifies the time, in seconds, between client requests, before the portlet container invalidates this session. A
    * negative time indicates the session should never timeout.
    *
    * @param interval An integer specifying the number of seconds
    */
   void setMaxInactiveInterval(int interval);
}
