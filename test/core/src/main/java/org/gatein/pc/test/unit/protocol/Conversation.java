package org.gatein.pc.test.unit.protocol;

import junit.framework.AssertionFailedError;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpMethodParams;

import org.apache.log4j.Logger;

import org.gatein.common.io.IOTools;
import org.gatein.pc.test.unit.protocol.request.Request;
import org.gatein.pc.test.unit.protocol.response.Response;
import org.gatein.pc.test.unit.protocol.request.DoGetRequest;
import org.gatein.pc.test.unit.protocol.request.DoMethodRequest;
import org.gatein.pc.test.unit.protocol.request.DoPostRequest;
import org.gatein.pc.test.unit.Failure;
import org.gatein.pc.test.unit.protocol.response.EndTestResponse;
import org.gatein.pc.test.unit.protocol.response.FailureResponse;
import org.gatein.pc.test.unit.protocol.response.HTTPDriverResponse;
import org.gatein.pc.test.unit.protocol.response.InvokeGetResponse;
import org.gatein.pc.test.unit.protocol.response.InvokeMethodResponse;
import org.gatein.pc.test.unit.protocol.response.InvokePostResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class Conversation
{

   /** . */
   private final Logger log = Logger.getLogger(getClass());

   /** . */
   private final URL baseURL;

   /** . */
   private final HttpClient client;

   /** . */
   private final String testName;

   /** . */
   private URL driverURL;

   public Conversation(URL baseURL, String testName)
   {
      HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
      client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, null);
      client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("test", "test"));

      //
      this.client = client;
      this.baseURL = baseURL;
      this.testName = testName;
   }

   public void performInteractions()
   {
      Response response;
      try
      {
         // Compute driver URL
         driverURL = baseURL.toURI().resolve("driver").toURL();

         //
         ClientRequestContext context = new ClientRequestContext(new DoGetRequest(baseURL.toURI().resolve("portal")));

         // Temporary hack to increment request count
         context = new ClientRequestContext(context);
         response = handleCommand(context);
      }
      catch (Throwable e)
      {
         AssertionFailedError afe = new AssertionFailedError();
         afe.initCause(e);
         throw afe;
      }

      //
      if (response instanceof EndTestResponse)
      {
         // OK good
      }
      else if (response instanceof FailureResponse)
      {
         Failure failure = ((FailureResponse)response).getFailure();
         AssertionFailedError afe = new AssertionFailedError(failure.getMessage());
         afe.initCause(failure.getStackTrace());
         throw afe;
      }
      else
      {
         throw new UnsupportedOperationException("Response " + response + " not implemented");
      }
   }

   /**
    * Trigger an interaction with the server here.
    *
    * @param commandContext the command context
    * @return the driver response
    * @throws Exception for now any exception
    */
   public final Response handleCommand(ClientRequestContext commandContext) throws Exception
   {
      // Invoke
      ClientResponseContext respCtx = invoke(commandContext);

      //
      Response response = respCtx.getResponse();

      //
      if (response instanceof EndTestResponse || response instanceof FailureResponse)
      {
         return response;
      }

      //
      ClientRequestContext nextCommandContext;
      if (response instanceof HTTPDriverResponse)
      {
         HTTPDriverResponse resp = (HTTPDriverResponse)response;
         if (resp instanceof InvokeMethodResponse)
         {
            if (resp instanceof InvokeGetResponse)
            {
               InvokeGetResponse igr = (InvokeGetResponse)resp;
               nextCommandContext = new ClientRequestContext(respCtx, new DoGetRequest(igr.getURI(), igr.getHeaders()));
            }
            else
            {
               InvokePostResponse ipr = (InvokePostResponse)resp;
               Body dpcb = ipr.getBody();
               nextCommandContext = new ClientRequestContext(respCtx, new DoPostRequest(ipr.getURI(), ipr.getContentType(), dpcb));
            }
         }
         else
         {
            nextCommandContext = null;
         }
      }
      else
      {
         nextCommandContext = null;
      }

      //
      if (nextCommandContext == null)
      {
         return new FailureResponse(Failure.createErrorFailure("Response " + response + " was not handled"));
      }
      else
      {
         commandContext = nextCommandContext;
      }

      //
      return handleCommand(commandContext);
   }

   private ClientResponseContext invoke(ClientRequestContext commandContext) throws Exception
   {
      Request command = commandContext.getCommand();

      //
      if (command instanceof DoMethodRequest)
      {
         DoMethodRequest method = (DoMethodRequest)command;
         URI uri = method.getURI();
         URL url = getURL(uri);
         if (command instanceof DoPostRequest)
         {
            DoPostRequest doPostCmd = (DoPostRequest)command;
            PostMethod post = null;
            try
            {
               post = new PostMethod(url.toString());
               post.setFollowRedirects(false);
               Body body = doPostCmd.getBody();
               if (doPostCmd.getContentType() != null)
               {
                  post.addRequestHeader("Content-Type", doPostCmd.getContentType());
               }
               if (body instanceof Body.Raw)
               {
                  Body.Raw rb = (Body.Raw)body;
                  ByteArrayRequestEntity entity = new ByteArrayRequestEntity(rb.getBytes());
                  post.setRequestEntity(entity);
               }
               else if (body instanceof Body.Form)
               {
                  Body.Form fb = (Body.Form)body;
                  Collection<NameValuePair> tmp = new ArrayList<NameValuePair>();
                  for (Object o : fb.getParameterNames())
                  {
                     String name = (String)o;
                     String[] values = fb.getParameterValues(name);
                     for (String value : values)
                     {
                        NameValuePair nvp = new NameValuePair(name, value);
                        tmp.add(nvp);
                     }
                  }
                  NameValuePair[] nvps = tmp.toArray(new NameValuePair[tmp.size()]);
                  post.setRequestBody(nvps);
               }
               executeHTTPMethod(commandContext, post);
               return decodeHTTPResponse(commandContext, post);
            }
            finally
            {
               if (post != null)
               {
                  post.releaseConnection();
               }
            }
         }
         else
         {
            DoGetRequest doGetCmd = (DoGetRequest)command;
            GetMethod get = null;
            try
            {
               get = new GetMethod(url.toString());
               HashMap<String, Header> headers = doGetCmd.getHeaders();
               for (Header header : headers.values())
               {
                  get.addRequestHeader(header);
               }
               get.setFollowRedirects(false);
               executeHTTPMethod(commandContext, get);
               return decodeHTTPResponse(commandContext, get);
            }
            finally
            {
               if (get != null)
               {
                  get.releaseConnection();
               }
            }
         }
      }
      else
      {
         return commandContext.createResponseContext(new FailureResponse(Failure.createErrorFailure("Unexpected command")));
      }
   }

   private URL getURL(URI uri) throws URISyntaxException, MalformedURLException
   {
      if (!uri.isAbsolute())
      {
         uri = baseURL.toURI().resolve(uri);
      }
      return uri.toURL();
   }

   private ClientResponseContext decodeHTTPResponse(ClientRequestContext commandContext, HttpMethod httpMethod) throws Exception
   {

      //
      HttpURLConnection conn  = (HttpURLConnection)driverURL.openConnection();
      conn.connect();
      Response response;
      if (conn.getResponseCode() == 200)
      {
         InputStream in = conn.getInputStream();
         byte[] bytes = IOTools.getBytes(in);
         response = (Response)IOTools.unserialize(bytes);
      }
      else
      {
         response = null;
      }

      //
      int status = httpMethod.getStatusCode();
      switch (status)
      {
         case 200:
            // HTTP response is ok
            if (response == null)
            {
               response = new FailureResponse(Failure.createErrorFailure("No result for test " + testName + " in the response"));
            }

            //
            log.info("# Received '200' code");
            break;
         case 302:
            // Send redirect
            if (response == null)
            {
               // Satisfy the 302 code
               Header locationHeader = httpMethod.getResponseHeader("location");
               if (locationHeader != null)
               {
                  String redirectLocation = locationHeader.getValue();
                  log.info("# Received '302' code --> " + redirectLocation);
                  DoGetRequest cmd = new DoGetRequest(new URI(redirectLocation));

                  // We should somehow stuff the response in the next payload
                  // but it's not yet proven it's usefull

                  // For now we don't add any contextual payload as
                  // 302 is some kind of implicit redirect response
                  return invoke(new ClientRequestContext(commandContext.getResponseContext(), cmd));
               }
               else
               {
                  // The response is invalid
                  response = new FailureResponse(Failure.createErrorFailure("302 Code with corrupted data"));
               }
            }
            else
            {
               // If any result has been setup during the action it overrides the 302 code
               log.info("# Received Result object which overrides the 302");
            }
            break;
         case 500:
            log.info("# Received '500' code");
            response = new FailureResponse(Failure.createErrorFailure("Received '500' code at " + httpMethod.getURI()));
            break;
         case 404:
            log.info("# Received '404' code");
            response = new FailureResponse(Failure.createErrorFailure("Received '404' code at " + httpMethod.getURI()));
            break;
         default:
            response = new FailureResponse(Failure.createErrorFailure("Unexpected http code " + status + " at " + httpMethod.getURI()));
            break;
      }

      //
      ClientResponseContext responseCtx = commandContext.createResponseContext(response);

      //
      byte[] body = httpMethod.getResponseBody();
      responseCtx.setPayload("http.response.body", body);

      //
      HashMap<String, Header> _headers = new HashMap<String, Header>();
      for (Header header : httpMethod.getResponseHeaders())
      {
         _headers.put(header.getName(), header);
      }
      responseCtx.setPayload("http.response.headers", _headers);

      //
      return responseCtx;
   }

   private int executeHTTPMethod(ClientRequestContext commandContext, HttpMethod method) throws Exception
   {
      // Push context
      HttpURLConnection conn = (HttpURLConnection)new URL(
         driverURL + "?test=" + testName + "&step=" + commandContext.getRequestCount()
      ).openConnection();
      byte[] payload = IOTools.serialize(commandContext.getPayload());
      conn.setRequestProperty("Content-Type", "application/octet-stream");
      conn.setRequestProperty("Content-Length", "" + payload.length);
      conn.setDoOutput(true);
      conn.connect();
      OutputStream out = conn.getOutputStream();
      IOTools.copy(new ByteArrayInputStream(payload), out);
      out.close();
      IOTools.getBytes(conn.getInputStream());

      //
      log.info("# Invoking test case over http " + method.getURI());
      int status = client.executeMethod(method);

      // Force to read the response body before we close the connection
      // otherwise the content will be lost
      method.getResponseBody();

      //
      return status;
   }
}
