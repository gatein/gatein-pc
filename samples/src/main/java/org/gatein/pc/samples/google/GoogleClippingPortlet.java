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

package org.gatein.pc.samples.google;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.xml.namespace.QName;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple portlet using Google's search services to extract interesting information (weather, map, ...) from first
 * result.
 *
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision: 9912 $
 */
public class GoogleClippingPortlet extends GenericPortlet
{
   protected static final String A = "<a";
   private static final String A_TARGET_BLANK = "<a target='_blank'";

   private static final String ZIP = "zipcode";
   private static final String BEGINNING_STRING = "beginningString";
   private static final String QUERY = "query";

   public static final QName QNAME = new QName("urn:jboss:portal:samples:event", "ZipEvent");

   /**
    * gl=US forces use of US google site, hl=en forces results to be in English so that regardless of location the query
    * should result in the expected result.
    */
   private static final String DEFAULT_QUERY = "http://www.google.com/search?gl=US&hl=en&q=";
   private static final String DEFAULT_BEG = "<div id=res>";
   private static final String DEFAULT_ZIP = "94102";
   private static final String END_TABLE = "</table>";
   private static final String BEG_TABLE = "<table";

   @Override
   protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      String zip = getZipCode(renderRequest);

      String query = getQueryString(renderRequest, zip);

      URL url = new URL(query);

      URLConnection connection = url.openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0");

      BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
      String html = new String(getBytes(in, 16384), "UTF-8");
      in.close();

      String beg = getBegString(renderRequest);
      int begIndex = html.indexOf(beg);
      if (begIndex != -1)
      {
         html = process(html, begIndex + beg.length());
      }
      else
      {
         html = "<p class='portlet-font'>Couldn't retrieve result from Google. Check that '"
            + beg.replaceAll("<", "&lt;") + "' (value of the <code>beginningString</code> portlet preference) is "
            + "contained in the result of issued query ('" + query
            + "', based on the value of the <code>query</code> portlet preference). You can adjust these values in "
            + "<code>portlet.xml</code> to tweak this portlet output...</p>";
      }

      renderResponse.setContentType("text/html");
      PrintWriter printWriter = renderResponse.getWriter();
      printWriter.print(html);
   }

   private String getBegString(RenderRequest renderRequest)
   {
      return renderRequest.getPreferences().getValue(BEGINNING_STRING, DEFAULT_BEG);
   }

   private String process(String html, int begIndex) throws IOException
   {
      // extract table containing specific first result
      int tableIndex = html.indexOf(BEG_TABLE, begIndex);
      int endIndex = html.indexOf(END_TABLE, tableIndex);
      html = html.substring(tableIndex, endIndex + END_TABLE.length());
      html = postProcessHTML(html);

      return html;
   }

   protected String getZipCode(RenderRequest renderRequest)
   {
      String zip = renderRequest.getParameter(ZIP);
      if (zip == null)
      {
         zip = renderRequest.getPreferences().getValue(ZIP, DEFAULT_ZIP);
      }
      return zip;
   }

   @Override
   protected void doEdit(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException
   {
      renderResponse.setContentType("text/html");
      renderResponse.getWriter().print(
         "<div align='center'>\n" +
            "   <br/>\n" +
            "   <form method='post' action='" + renderResponse.createActionURL() + "'\n" +
            "      <font class='portlet-font'>Change location (zip code): </font>\n" +
            "      <input class='portlet-form-input-field' type='text' value='' size='12' name='" + ZIP + "'/>\n" +
            "      <input class='portlet-form-input-field' type='submit' name='submit' value='submit'>\n" +
            "   </form>\n" +
            "</div>");
   }

   @Override
   public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
   {
      String zip = actionRequest.getParameter(ZIP);

      if (null != zip)
      {
         PortletPreferences prefs = actionRequest.getPreferences();
         prefs.setValue(ZIP, zip);
         prefs.store();
      }

      // set zip as render parameter
      actionResponse.setRenderParameter(ZIP, zip);

      // request view
      actionResponse.setPortletMode(PortletMode.VIEW);

      // send out zip event
      actionResponse.setEvent(QNAME, zip);
   }

   protected String postProcessHTML(String html)
   {
      // links should open in new windows
      html = html.replaceAll(A, A_TARGET_BLANK);

      // src attributes should be absolute
      html = html.replaceAll("src=/", "src=http://google.com/");
      html = html.replaceAll("src=\"/", "src=\"http://google.com/");

      // forms should open in new windows and have an absolute action URL
      html = html.replaceAll("action=\"/", "target='_blank' action=\"http://google.com/");

      return html;
   }

   protected String getQueryString(RenderRequest renderRequest, String zip)
   {
      return renderRequest.getPreferences().getValue(QUERY, DEFAULT_QUERY) + zip;
   }

   private byte[] getBytes(InputStream in, int bufferSize) throws IOException, IllegalArgumentException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      byte[] buffer = new byte[bufferSize];
      while (true)
      {
         int i = in.read(buffer);
         if (i == 0)
         {
            continue;
         }
         if (i == -1)
         {
            break;
         }
         out.write(buffer, 0, i);
      }
      return out.toByteArray();
   }

}
