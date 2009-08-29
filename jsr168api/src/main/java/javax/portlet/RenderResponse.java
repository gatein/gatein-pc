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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * The <CODE>RenderResponse</CODE> defines an object to assist a portlet in sending a response to the portal. It extends
 * the <CODE>PortletResponse</CODE> interface to provide specific render response functionality to portlets.<br> The
 * portlet container creates a <CODE>RenderResponse</CODE> object and passes it as argument to the portlet's
 * <CODE>render</CODE> method.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 5441 $
 * @see RenderRequest
 * @see PortletResponse
 */
public interface RenderResponse extends PortletResponse
{
   /**
    * Property to set the expiration time in seconds for this response using the <code>setProperty</code> method.
    * <p/>
    * If the expiration value is set to 0, caching is disabled for this portlet; if the value is set to -1, the cache
    * does not expire.
    * <p/>
    * The value is <code>"portlet.expiration-cache"</code>.
    */
   public static final String EXPIRATION_CACHE = "portlet.expiration-cache";

   /**
    * Returns the MIME type that can be used to contribute markup to the render response.
    * <p/>
    * If no content type was set previously using the {@link #setContentType} method this method retuns
    * <code>null</code>.
    *
    * @return the MIME type of the response, or <code>null</code> if no content type is set
    * @see #setContentType
    */

   String getContentType();

   /**
    * Creates a portlet URL targeting the portlet. If no portlet mode, window state or security modifier is set in the
    * PortletURL the current values are preserved. If a request is triggered by the PortletURL, it results in a render
    * request.
    * <p/>
    * The returned URL can be further extended by adding portlet-specific parameters and portlet modes and window
    * states.
    * <p/>
    * The created URL will per default not contain any parameters of the current render request.
    *
    * @return a portlet render URL
    */
   PortletURL createRenderURL();

   /**
    * Creates a portlet URL targeting the portlet. If no portlet mode, window state or security modifier is set in the
    * PortletURL the current values are preserved. If a request is triggered by the PortletURL, it results in an action
    * request.
    * <p/>
    * The returned URL can be further extended by adding portlet-specific parameters and portlet modes and window
    * states.
    * <p/>
    * The created URL will per default not contain any parameters of the current render request.
    *
    * @return a portlet action URL
    */
   PortletURL createActionURL();

   /**
    * The value returned by this method should be prefixed or appended to elements, such as JavaScript variables or
    * function names, to ensure they are unique in the context of the portal page.
    *
    * @return the namespace
    */
   String getNamespace();

   /**
    * This method sets the title of the portlet.
    * <p/>
    * The value can be a text String
    *
    * @param title portlet title as text String or resource URI
    */
   void setTitle(String title);

   /**
    * Sets the MIME type for the render response. The portlet must set the content type before calling {@link
    * #getWriter} or {@link #getPortletOutputStream}.
    * <p/>
    * Calling <code>setContentType</code> after <code>getWriter</code> or <code>getOutputStream</code> does not change
    * the content type.
    *
    * @param type the content MIME type
    * @throws java.lang.IllegalArgumentException
    *          if the given type is not in the list returned by <code>PortletRequest.getResponseContentTypes</code>
    * @see RenderRequest#getResponseContentTypes
    * @see #getContentType
    */
   void setContentType(String type) throws IllegalArgumentException;

   /**
    * Returns the name of the charset used for the MIME body sent in this response.
    * <p/>
    * <p>See <a href="http://ds.internic.net/rfc/rfc2045.txt">RFC 2047</a> for more information about character encoding
    * and MIME.
    *
    * @return a <code>String</code> specifying the name of the charset, for example, <code>ISO-8859-1</code>
    */
   String getCharacterEncoding();

   /**
    * Returns a PrintWriter object that can send character text to the portal.
    * <p/>
    * Before calling this method the content type of the render response must be set using the {@link #setContentType}
    * method.
    * <p/>
    * Either this method or {@link #getPortletOutputStream} may be called to write the body, not both.
    *
    * @return a <code>PrintWriter</code> object that can return character data to the portal
    * @throws java.io.IOException if an input or output exception occurred
    * @throws java.lang.IllegalStateException
    *                             if the <code>getPortletOutputStream</code> method has been called on this response, or
    *                             if no content type was set using the <code>setContentType</code> method.
    * @see #setContentType
    * @see #getPortletOutputStream
    */
   PrintWriter getWriter() throws IOException, IllegalStateException;

   /**
    * Returns the locale assigned to the response.
    *
    * @return Locale of this response
    */
   Locale getLocale();

   /**
    * Sets the preferred buffer size for the body of the response. The portlet container will use a buffer at least as
    * large as the size requested.
    * <p/>
    * This method must be called before any response body content is written; if content has been written, or the
    * portlet container does not support buffering, this method may throw an <code>IllegalStateException</code>.
    *
    * @param size the preferred buffer size
    * @throws java.lang.IllegalStateException
    *          if this method is called after content has been written, or the portlet container does not support
    *          buffering
    * @see #getBufferSize
    * @see #flushBuffer
    * @see #isCommitted
    * @see #reset
    */
   void setBufferSize(int size) throws IllegalStateException;

   /**
    * Returns the actual buffer size used for the response.  If no buffering is used, this method returns 0.
    *
    * @return the actual buffer size used
    * @see #setBufferSize
    * @see #flushBuffer
    * @see #isCommitted
    * @see #reset
    */
   int getBufferSize();

   /**
    * Forces any content in the buffer to be written to the client.  A call to this method automatically commits the
    * response.
    *
    * @throws java.io.IOException if an error occured when writing the output
    * @see #setBufferSize
    * @see #getBufferSize
    * @see #isCommitted
    * @see #reset
    */
   void flushBuffer() throws IOException;

   /**
    * Clears the content of the underlying buffer in the response without clearing properties set. If the response has
    * been committed, this method throws an <code>IllegalStateException</code>.
    *
    * @throws IllegalStateException if this method is called after response is comitted
    * @see #setBufferSize
    * @see #getBufferSize
    * @see #isCommitted
    * @see #reset
    */
   void resetBuffer() throws IllegalStateException;

   /**
    * Returns a boolean indicating if the response has been committed.
    *
    * @return a boolean indicating if the response has been committed
    * @see #setBufferSize
    * @see #getBufferSize
    * @see #flushBuffer
    * @see #reset
    */
   boolean isCommitted();

   /**
    * Clears any data that exists in the buffer as well as the properties set. If the response has been committed, this
    * method throws an <code>IllegalStateException</code>.
    *
    * @throws java.lang.IllegalStateException
    *          if the response has already been committed
    * @see #setBufferSize
    * @see #getBufferSize
    * @see #flushBuffer
    * @see #isCommitted
    */
   void reset() throws IllegalStateException;

   /**
    * Returns a <code>OutputStream</code> suitable for writing binary data in the response. The portlet container does
    * not encode the binary data.
    * <p/>
    * Before calling this method the content type of the render response must be set using the {@link #setContentType}
    * method.
    * <p/>
    * Calling <code>flush()</code> on the OutputStream commits the response.
    * <p/>
    * Either this method or {@link #getWriter} may be called to write the body, not both.
    *
    * @throws java.lang.IllegalStateException
    *                             if the <code>getWriter</code> method has been called on this response, or if no
    *                             content type was set using the <code>setContentType</code> method.
    * @throws java.io.IOException if an input or output exception occurred
    * @return a <code>OutputStream</code> for writing binary data
    * @see #setContentType
    * @see #getWriter
    */
   OutputStream getPortletOutputStream() throws IllegalStateException, IOException;
}
