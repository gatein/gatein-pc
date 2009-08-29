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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * The <CODE>PortletContext</CODE> interface defines a portlet view of the portlet container. The
 * <CODE>PortletContext</CODE> also makes resources available to the portlet. Using the context, a portlet can access
 * the portlet log, and obtain URL references to resources.
 * <p/>
 * <p>There is one context per "portlet application" per Java Virtual Machine.  (A "portlet application" is a collection
 * of portlets, servlets, and content installed under a specific subset of the server URL namespace, such as
 * <code>/catalog</code>. They are possibly installed via a <code>.war</code> file.) As a web application, a portlet
 * application also has a servlet context. The portlet context leverages most of its functionality from the servlet
 * context of the portlet application.
 * <p/>
 * Attibutes stored in the context are global for <I>all</I> users and <I>all</I> components in the portlet
 * application.
 * <p/>
 * In the case of a web application marked "distributed" in its deployment descriptor, there will be one context
 * instance for each virtual machine.  In this situation, the context cannot be used as a location to share global
 * information (because the information is not truly global). Use an external resource, such as a database to achieve
 * sharing on a global scope.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 */
public interface PortletContext
{
   /**
    * Returns the name and version of the portlet container in which the portlet is running.
    * <p/>
    * <p/>
    * The form of the returned string is <code>containername/versionnumber</code>.
    *
    * @return the string containing at least name and version number
    */
   String getServerInfo();

   /**
    * Returns a {@link PortletRequestDispatcher} object that acts as a wrapper for the resource located at the given
    * path. A <code>PortletRequestDispatcher</code> object can be used include the resource in a response. The resource
    * can be dynamic or static.
    * <p/>
    * <p>The pathname must begin with a slash (<code> / </code>) and is interpreted as relative to the current context
    * root.
    * <p/>
    * <p>This method returns <code>null</code> if the <code>PortletContext</code> cannot return a
    * <code>PortletRequestDispatcher</code> for any reason.
    *
    * @param path a <code>String</code> specifying the pathname to the resource
    * @return a <code>PortletRequestDispatcher</code> object that acts as a wrapper for the resource at the specified
    *         path.
    * @see PortletRequestDispatcher
    */

   PortletRequestDispatcher getRequestDispatcher(String path);

   /**
    * Returns a {@link PortletRequestDispatcher} object that acts as a wrapper for the named servlet.
    * <p/>
    * <p>Servlets (and also JSP pages) may be given names via server administration or via a web application deployment
    * descriptor.
    * <p/>
    * <p>This method returns <code>null</code> if the <code>PortletContext</code> cannot return a
    * <code>PortletRequestDispatcher</code> for any reason.
    *
    * @param name a <code>String</code> specifying the name of a servlet to be wrapped
    * @return a <code>PortletRequestDispatcher</code> object that acts as a wrapper for the named servlet
    * @see PortletRequestDispatcher
    */
   PortletRequestDispatcher getNamedDispatcher(String name);

   /**
    * Returns the resource located at the given path as an InputStream object. The data in the InputStream can be of any
    * type or length. The method returns null if no resource exists at the given path.
    * <p/>
    * In order to access protected resources the path has to be prefixed with <code>/WEB-INF/</code> (for example
    * <code>/WEB-INF/myportlet/myportlet.jsp</code>). Otherwise, the direct path is used (for example
    * <code>/myportlet/myportlet.jsp</code>).
    *
    * @param path the path to the resource
    * @return the input stream
    */
   InputStream getResourceAsStream(String path);

   /**
    * Returns the major version of the Portlet API that this portlet container supports.
    *
    * @return the major version
    * @see #getMinorVersion()
    */
   int getMajorVersion();

   /**
    * Returns the minor version of the Portlet API that this portlet container supports.
    *
    * @return the minor version
    * @see #getMajorVersion()
    */
   int getMinorVersion();

   /**
    * Returns the MIME type of the specified file, or <code>null</code> if the MIME type is not known. The MIME type is
    * determined by the configuration of the portlet container and may be specified in a web application deployment
    * descriptor. Common MIME types are <code>text/html</code> and <code>image/gif</code>.
    *
    * @param file a <code>String</code> specifying the name of a file
    * @return a <code>String</code> specifying the MIME type of the file
    */
   String getMimeType(String file);

   /**
    * Returns a <code>String</code> containing the real path for a given virtual path. For example, the path
    * <code>/index.html</code> returns the absolute file path of the portlet container file system.
    * <p/>
    * <p>The real path returned will be in a form appropriate to the computer and operating system on which the portlet
    * container is running, including the proper path separators. This method returns <code>null</code> if the portlet
    * container cannot translate the virtual path to a real path for any reason (such as when the content is being made
    * available from a <code>.war</code> archive).
    *
    * @param path a <code>String</code> specifying a virtual path
    * @return a <code>String</code> specifying the real path, or null if the transformation cannot be performed.
    */
   String getRealPath(String path);

   /**
    * Returns a directory-like listing of all the paths to resources within the web application longest sub-path of
    * which matches the supplied path argument. Paths indicating subdirectory paths end with a slash (<code>/</code>).
    * The returned paths are all relative to the root of the web application and have a leading slash. For example, for
    * a web application containing<br><br> <code> /welcome.html<br> /catalog/index.html<br> /catalog/products.html<br>
    * /catalog/offers/books.html<br> /catalog/offers/music.html<br> /customer/login.jsp<br> /WEB-INF/web.xml<br>
    * /WEB-INF/classes/com.acme.OrderPortlet.class,<br><br> </code>
    * <p/>
    * <code>getResourcePaths("/")</code> returns <code>{"/welcome.html", "/catalog/", "/customer/",
    * "/WEB-INF/"}</code><br> <code>getResourcePaths("/catalog/")</code> returns <code>{"/catalog/index.html",
    * "/catalog/products.html", "/catalog/offers/"}</code>.<br>
    *
    * @param path the partial path used to match the resources, which must start with a slash
    * @return a Set containing the directory listing, or <code>null</code> if there are no resources in the web
    *         application of which the path begins with the supplied path.
    */
   Set getResourcePaths(String path);

   /**
    * Returns a URL to the resource that is mapped to a specified path. The path must begin with a slash
    * (<code>/</code>) and is interpreted as relative to the current context root.
    * <p/>
    * <p>This method allows the portlet container to make a resource available to portlets from any source. Resources
    * can be located on a local or remote file system, in a database, or in a <code>.war</code> file.
    * <p/>
    * <p>The portlet container must implement the URL handlers and <code>URLConnection</code> objects that are necessary
    * to access the resource.
    * <p/>
    * <p>This method returns <code>null</code> if no resource is mapped to the pathname.
    * <p/>
    * <p>Some containers may allow writing to the URL returned by this method using the methods of the URL class.
    * <p/>
    * <p>The resource content is returned directly, so be aware that requesting a <code>.jsp</code> page returns the JSP
    * source code. Use a <code>RequestDispatcher</code> instead to include results of an execution.
    * <p/>
    * <p>This method has a different purpose than <code>java.lang.Class.getResource</code>, which looks up resources
    * based on a class loader. This method does not use class loaders.
    *
    * @param path a <code>String</code> specifying the path to the resource
    * @return the resource located at the named path, or <code>null</code> if there is no resource at that path
    * @throws MalformedURLException if the pathname is not given in the correct form
    */
   URL getResource(String path) throws MalformedURLException;

   /**
    * Returns the portlet container attribute with the given name, or null if there is no attribute by that name. An
    * attribute allows a portlet container to give the portlet additional information not already provided by this
    * interface. A list of supported attributes can be retrieved using <code>getAttributeNames</code>.
    * <p/>
    * <p>The attribute is returned as a <code>java.lang.Object</code> or some subclass. Attribute names should follow
    * the same convention as package names. The Java Portlet API specification reserves names matching
    * <code>java.*</code>, <code>javax.*</code>, and <code>sun.*</code>.
    *
    * @param name a <code>String</code> specifying the name of the attribute
    * @return an <code>Object</code> containing the value of the attribute, or <code>null</code> if no attribute exists
    *         matching the given name
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    * @see #getAttributeNames
    */
   Object getAttribute(String name) throws IllegalArgumentException;

   /**
    * Returns an <code>Enumeration</code> containing the attribute names available within this portlet context, or an
    * emtpy <code>Enumeration</code> if no attributes are available. Use the {@link #getAttribute} method with an
    * attribute name to get the value of an attribute.
    *
    * @return an <code>Enumeration</code> of attribute names
    * @see      #getAttribute
    */
   Enumeration getAttributeNames();

   /**
    * Returns a String containing the value of the named context-wide initialization parameter, or <code>null</code> if
    * the parameter does not exist. This method provides configuration information which may be useful for an entire
    * "portlet application".
    *
    * @return a <code>String</code> containing the value of the initialization parameter, or <code>null</code> if the
    *         parameter does not exist.
    * @param   name   a <code>String</code> containing the name of the requested parameter
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    * @see #getInitParameterNames
    */
   String getInitParameter(String name) throws IllegalArgumentException;

   /**
    * Returns the names of the context initialization parameters as an <code>Enumeration</code> of String objects, or an
    * empty Enumeration if the context has no initialization parameters.
    *
    * @return an <code>Enumeration</code> of <code>String</code> objects containing the names of the context
    *         initialization parameters
    * @see #getInitParameter
    */
   Enumeration getInitParameterNames();

   /**
    * Writes the specified message to a portlet log file, usually an event log. The name and type of the portlet log
    * file is specific to the portlet container.
    * <p/>
    * This method mapps to the <code>ServletContext.log</code> method. The portlet container may in addition log this
    * message in a portlet container specific log file.
    *
    * @param msg a <code>String</code> specifying the message to be written to the log file
    */
   void log(String msg);

   /**
    * Writes an explanatory message and a stack trace for a given Throwable exception to the portlet log file. The name
    * and type of the portlet log file is specific to the portlet container, usually an event log.
    * <p/>
    * This method is mapped to the <code>ServletContext.log</code> method. The portlet container may in addition log
    * this message in a portlet container specific log file.
    *
    * @param msg       a <code>String</code> that describes the error or exception
    * @param throwable the <code>Throwable</code> error or exception
    */
   void log(String msg, Throwable throwable);

   /**
    * Removes the attribute with the given name from the portlet context. After removal, subsequent calls to {@link
    * #getAttribute} to retrieve the attribute's value will return <code>null</code>.
    *
    * @param name a <code>String</code> specifying the name of the attribute to be removed
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    */
   void removeAttribute(String name) throws IllegalArgumentException;

   /**
    * Binds an object to a given attribute name in this portlet context. If the name specified is already used for an
    * attribute, this method removes the old attribute and binds the name to the new attribute.
    * <p/>
    * If a null value is passed, the effect is the same as calling <code>removeAttribute()</code>.
    * <p/>
    * <p>Attribute names should follow the same convention as package names. The Java Portlet API specification reserves
    * names matching <code>java.*</code>, <code>javax.*</code>, and <code>sun.*</code>.
    *
    * @param name   a <code>String</code> specifying the name of the attribute
    * @param object an <code>Object</code> representing the attribute to be bound
    * @exception java.lang.IllegalArgumentException if name is <code>null</code>.
    */
   void setAttribute(String name, Object object) throws IllegalArgumentException;

   /**
    * Returns the name of this portlet application correponding to this PortletContext as specified in the
    * <code>web.xml</code> deployment descriptor for this web application by the <code>display-name</code> element.
    *
    * @return The name of the web application or null if no name has been declared in the deployment descriptor.
    */
   String getPortletContextName();
}
