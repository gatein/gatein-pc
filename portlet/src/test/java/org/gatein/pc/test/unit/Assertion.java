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
package org.gatein.pc.test.unit;

import javax.portlet.PortletURL;
import javax.portlet.BaseURL;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.CacheControl;
import javax.portlet.ClientDataRequest;
import javax.portlet.Event;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.MimeResponse;
import javax.portlet.PortalContext;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PreferencesValidator;
import javax.portlet.ProcessAction;
import javax.portlet.ProcessEvent;
import javax.portlet.RenderMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;
import javax.portlet.ResourceURL;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.ActionResponseWrapper;
import javax.portlet.filter.EventFilter;
import javax.portlet.filter.EventRequestWrapper;
import javax.portlet.filter.EventResponseWrapper;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.portlet.filter.RenderRequestWrapper;
import javax.portlet.filter.RenderResponseWrapper;
import javax.portlet.filter.ResourceFilter;
import javax.portlet.filter.ResourceRequestWrapper;
import javax.portlet.filter.ResourceResponseWrapper;

/**
 * Define TCK assertions.
 *
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version $Revision: 1.1 $
 */
public enum Assertion
{

   JSR168_4(new TCK(4), "After the portlet object is instantiated, the portlet container must" +
      " initialize the portlet before invoking it to handle requests"),

   JSR168_5(new TCK(5), "During initialization, the portlet object may throw an" +
      " UnavailableException or a PortletException. In this case, the portlet container must not place the portlet object" +
      " into active service and it must release the portlet object."),

   JSR168_6(new TCK(6), "The destroy method must not be called because the initialization is" +
      " considered unsuccessful."),


   JSR168_7(new TCK(7),""),

   JSR168_8(new TCK(8), "A RuntimeException thrown during initialization must be handled as a" +
      " PortletException."),

   JSR168_9(new TCK(9), "If the client request is triggered by an action URL, the" +
      " portal/portlet-container must first trigger the action request by invoking the processAction method of the" +
      " targeted portlet."),

   JSR168_10(new TCK(10), "The portal/portlet-container must wait until the action request finishes." +
      " Then, the portal/portlet-container must trigger the render request by invoking the render method for all the" +
      " portlets in the portal page with the possible exception of portlets for which their content is being cached."),

   JSR168_11(new TCK(11), "If the client request is triggered by a render URL, the" +
      " portal/portlet-container must invoke the render method for all the portlets in the portal page with the possible" +
      " exception of portlets for which their content is being cached."),

   JSR168_12(new TCK(12), "While processing an action request, the portlet may instruct the" +
      " portal/portlet-container to redirect the user to a specific URL. If the portlet issues a redirection, when the" +
      " processAction method concludes, the portal/portlet-container must send the redirection back to the user agent"),

   JSR168_17(new TCK(17), Status.disabled("spec?"), "A PortletException signals that an error has occurred during the processing" +
      " of the request and that the portlet container should take appropriate measures to clean up the request. If a portlet" +
      " throws an exception in the processAction method, all operations on the ActionResponse must be ignored and the render" +
      " method must not be invoked within the current client request"),

   JSR168_18(new TCK(18), Status.disabled("spec?"), "If a permanent unavailability is indicated by the UnavailableException, the" +
      " portlet container must remove the portlet from service immediately, call the portlet's destroy method, and release" +
      " the portlet object."),

   JSR168_19(new TCK(19), Status.disabled("spec?"), "A RuntimeException thrown during the request handling must be handled as a" +
      " PortletException"),

   JSR168_24(new TCK(24), "If the root resource bundle does not contain the resources for these values and " +
      "the values are defined inline, the portlet container must add the inline values as resources of the root " +
      "resource bundle."),

   JSR168_25(new TCK(25), "If the portlet definition does not define a resource bundle and the information" +
      " is defined inline in the deployment descriptor, the portlet container must create a ResourceBundle and populate" +
      " it, with the inline values, using the keys defined in the PLT.25.10 Resource Bundles Section."),

   JSR168_26(new TCK(26), "A render URL is an optimization for a special type of action URLs. The" +
      " portal/portletcontainer must not invoke the processAction method of the targeted portlet."),

   JSR168_27(new TCK(27), "The portal/portlet-container must ensure that all the parameters set when" +
      " constructing the render URL become render parameters of the subsequent render requests for the portlet."),

   JSR168_28(new TCK(28), "Portlets can add application specific parameters to the PortletURL" +
      " objects using the setParameter and setParameters methods. A call to any of the setParameter methods must replace" +
      " any parameter with the same name previously set."),

   JSR168_30(new TCK(30), Status.jbossUntestable("why?"), ""),

   JSR168_31(new TCK(31), Status.jbossUntestable("why?"), ""),

   JSR168_29(new TCK(29), "All the parameters a portlet adds to a PortletURL object must be made" +
      " available to the portlet as request parameters"),

   JSR168_32(new TCK(32), "A portlet cannot create a portlet URL using a portlet mode that is not" +
      " defined as supported by the portlet or that the user it is not allowed to use. The setPortletMode methods must" +
      " throw a PortletModeException in that situation."),

   JSR168_34(new TCK(34), "A portlet cannot create a portlet URL using a window state that is not" +
      " supported by the portlet container. The setWindowState method must throw a WindowStateException if that is the" +
      " case"),

   JSR168_35(new TCK(35), "The setSecure method of the PortletURL interface allows a portlet to" +
      " indicate if the portlet URL has to be a secure URL or not (i.e. HTTPS or HTTP). If the setSecure method is not" +
      " used, the portlet URL must be of the same security level of the current request."),

   JSR168_36(new TCK(36), "The GenericPortlet class implementation of the render method dispatches" +
      " requests to the doView, doEdit or doHelp method depending on the portlet mode indicated in the request using the" +
      " doDispatch method"),

   JSR168_33(new TCK(33), Status.duplicate(JSR168_36, ""), ""),

   JSR168_37(new TCK(37), Status.duplicate(JSR168_36, "RequestDispatchingDependingOnModePortlet as VIEW mode isn't specified in descriptor"), ""),

   JSR168_38(new TCK(38), Status.disabled("spec?"), "The portlet must not be invoked in a portlet mode that has not been" +
      " declared as supported for a given markup type"),

   JSR168_40(new TCK(40), Status.disabled("PortletURL.setWindowState throws an exception because it is not mapped"), "If a custom window state defined in the deployment descriptor is not" +
      " mapped to a custom window state provided by the portal, portlets must not be invoked in that window state."
   ),

   JSR168_43(new TCK(43), "The initialization parameters accessible through the PortletContext must" +
      " be the same that are accessible through the ServletContext of the portlet application."),

   JSR168_44(new TCK(44), "Context attributes set using the PortletContext must be stored in the" +
      " ServletContext of the portlet application. A direct consequence of this is that data stored in the ServletContext" +
      " by servlets or JSPs is accessible to portlets through the PortletContext and vice versa."),

   JSR168_45(new TCK(45), "The PortletContext must offer access to the same set of resources the" +
      " ServletContext exposes."),

   JSR168_46(new TCK(46), "The PortletContext must handle the same temporary working directory the" +
      " ServletContext handles. It must be accessible as a context attribute using the same constant defined in the" +
      " Servlet Specification 2.3 SVR 3 Servlet Context Chapter, javax.servlet.context.tempdir."),

   JSR168_47(new TCK(47), Status.postponed("spec?"), ""),

   JSR168_48(new TCK(48), Status.postponed("spec?"), ""),

   JSR168_49(new TCK(49), Status.postponed("spec?"), ""),

   JSR168_50(new TCK(50), "The portlet-container must not propagate parameters received in an action" +
      " request to subsequent render requests of the portlet."),

   JSR168_51(new TCK(51), "If a portlet receives a render request that is the result of a client" +
      " request targeted to another portlet in the portal page, the parameters must be the same parameters as of the" +
      " previous render request."),

   JSR168_52(new TCK(52), "If a portlet receives a render request following an action request as" +
      " part of the same client request, the parameters received with render request must be the render parameters set" +
      " during the action request."),

   JSR168_53(new TCK(53), Status.postponed("spec?"), ""),

   JSR168_54(new TCK(54), "A portlet must not see any parameter targeted to other portlets."),

   JSR168_55(new TCK(55), "The getParameterValues method returns an array of String objects" +
      " containing all the parameter values associated with a parameter name. The value returned from the getParameter" +
      " method must be the first value in the array of String objects returned by getParameterValues"),

   JSR168_56(new TCK(56), "If there is a single parameter value associated with a parameter name the" +
      " method returns must return an array of size one containing the parameter value"),

   JSR168_57(new TCK(57), Status.specUntestable(""), ""),

   JSR168_58(new TCK(58), "The context path of a request is exposed via the request object. The" +
      " context path is the path prefix associated with the deployed portlet application. If the portlet application is" +
      " rooted at the base of the web server URL namespace (also known as \"default\" context), this path must be an empty" +
      " string"),

   JSR168_59(new TCK(59), "Otherwise, it must be the path the portlet application is rooted to, the" +
      " path must start with a '/' and it must not end with a '/' character."),

   JSR168_60(new TCK(60), "If the user is not authenticated the getAuthType method must return null"),

   JSR168_61(new TCK(61), "If the portlet container supports additional content types for the" +
      " portlet's output, it must declare the additional content types through the getResponseContentTypes method of the" +
      " request object. The returned Enumeration of strings should contain the content types the portlet container" +
      " supports in order of preference. The first element of the enumeration must be the same content type returned by" +
      " the getResponseContentType method."),

   JSR168_62(new TCK(62), Status.jbossUntestable("PORTAL DOESN'T SUPPORT ADDITIONAL CONTENT TYPES"), ""),

   JSR168_63(new TCK(63), "Only one of the two methods, getPortletInputStream or getReader, can be" +
      " used during an action request. If the input stream is obtained, a call to the getReader must throw an" +
      " IllegalStateException. Similarly, if the reader is obtained, a call to the getPortletInputStream must throw an" +
      " IllegalStateException."),

   JSR168_64(new TCK(64), Status.postponed(""), ""),

   JSR168_65(new TCK(65), "The sendRedirect method instructs the portal/portlet-container to set the" +
      " appropriate headers and content body to redirect the user to a different URL. A fully qualified URL or a full path" +
      " URL must be specified. If a relative path URL is given, an IllegalArgumentException must be thrown."),

   JSR168_66(new TCK(66), "If the sendRedirect method is called after the setPortletMode," +
      " setWindowState, setRenderParameter or setRenderParameters methods of the ActionResponse interface, an" +
      " IllegalStateException must be thrown and the redirection must not be executed"),

   JSR168_67(new TCK(67), "The setPortletMode method allows a portlet to change its current portlet" +
      " mode. The new portlet mode would be effective in the following render request. If a portlet attempts to set a" +
      " portlet mode that is not allowed to switch to, a PortletModeException must be thrown."),

   JSR168_68(new TCK(68), "The setWindowState method allows a portlet to change its current window" +
      " state. The new window state would be effective in the following render request. If a portlet attempts to set a" +
      " window state that it is not allowed to switch to, a WindowStateException must be thrown."),

   JSR168_69(new TCK(69), "If the setPortletMode or setWindowState methods are called after the" +
      " sendRedirect method has been called an IllegalStateException must be thrown."),

   JSR168_70(new TCK(70), "If the exception is caught by the portlet, the redirection must be" +
      " executed."),

   JSR168_71(new TCK(71), Status.disabled("This is disabled as after propagating IllegalStateException to PortletContainer" +
   " '500' code is returned which doesn't allow us to end test properly"), "If the setPortletMode or setWindowState methods are called after the" +
      " sendRedirect method has been called an IllegalStateException must be thrown. (...) If the exception is propagated" +
      " back to the portlet-container, the redirection must not be executed."),

   JSR168_72(new TCK(72), "Using the setRenderParameter and setRenderParameters methods of the" +
      " ActionResponse interface portlets may set render parameters during an action request. A call to any of the" +
      " setRenderParameter methods must replace any parameter with the same name previously set."),

   JSR168_73(new TCK(73), "If no render parameters are set during the processAction invocation, the" +
      " render request must not contain any request parameters."),

   JSR168_74(new TCK(74), "If the setRenderParameter or setRenderParameters methods are called after" +
      " the sendRedirect method has been called an IllegalStateException must be thrown"),

   // done only partially - see SPEC:71 comment for the reason
   JSR168_75(new TCK(75), "If the exception is caught by the portlet, the redirection must be" +
      " executed. If the exception is propagated back to the portlet-container, the redirection must not be executed."),

   JSR168_76(new TCK(76), "A portlet must set the content type of the response using the" +
      " setContentType method of the RenderResponse interface. The setContentType method must throw an" +
      " IllegalArgumentException if the content type set does not match (including wildcard matching) any of the content" +
      " types returned by the getResponseContentType method of the PortleRequest object"),

   JSR168_77(new TCK(77), "If the getWriter or getPortletOutputStream methods are called before the" +
      " setContentType method, they must throw an IllegalStateException."),

   JSR168_78(new TCK(78), "If the portlet has set a content type, the getContentType method must" +
      " return it. Otherwise, the getContentType method must return null."),

   JSR168_79(new TCK(79), "A portlet may generate its content by writing to the OutputStream or to" +
      " the Writer of the RenderResponse object. A portlet must use only one of these objects. The portlet container must" +
      " throw an IllegalStateException if a portlet attempts to use both."),

   JSR168_80(new TCK(80), Status.disabled("no buffering is implemented yet"), "The getBufferSize method returns the size of the underlying buffer being" +
      " used. If no buffering is being used, this method must return the int value of 0 (zero)"),

   JSR168_81(new TCK(81), Status.disabled("no buffering is implemented yet"), "The portlet can request a preferred buffer size by using the" +
      " setBufferSize method. The buffer assigned is not required to be the size requested by the portlet, but must be at" +
      " least as large as the size requested."),

   JSR168_82(new TCK(82), Status.postponed(""), ""),

   JSR168_83(new TCK(83), Status.disabled("no buffering is implemented yet"), "If the response is committed and the reset or resetBuffer method is" +
      " called, an IllegalStateException must be thrown."),

   JSR168_84(new TCK(84), Status.postponed(""), ""),

   JSR168_85(new TCK(85), Status.postponed(""), ""),

   JSR168_86(new TCK(86), "The getNamespace method must provide the portlet with a mechanism that" +
      " ensures the uniqueness of the returned string in the whole portal page"),

   JSR168_87(new TCK(87), "The getNamespace method must return the same value if invoked multiple" +
      " times within a render request."),

   JSR168_88(new TCK(88), "The getNamespace method must return a valid identifier as defined in the" +
      " 3.8 Identifier Section of the Java Language Specification Second Edition."),

   JSR168_90(new TCK(90), "Preference attributes are String array objects. Preferences attributes can" +
      " be set to null."),

   JSR168_91(new TCK(91), "The getMap method returns an immutable Map of String keys and String[]" +
      " values containing all current preference values. Preferences values must not be modified if the values in the Map" +
      " are altered."),

   // Does not cover restore to default test yet.
   JSR168_92(new TCK(92), "The reset method must reset a preference attribute to its default value." +
      " If there is no default value, the preference attribute must be deleted."),

   JSR168_93(new TCK(93), "If a preference attribute is read only, the setValue, setValues and reset" +
      " methods must throw a ReadOnlyException when the portlet is in any of the standard modes"),

   JSR168_94(new TCK(94), Status.disabled("Tested in every over case in this chapter... DONE"), ""),

   JSR168_95(new TCK(95), Status.postponed("why?"), ""),

   JSR168_96(new TCK(96), "All changes made to PortletPreferences object not followed by a call to" +
      " the store method must be discarded when the portlet finishes the processAction method."),

   JSR168_97(new TCK(97), "If the store method is invoked within the scope of a render method" +
      " invocation, it must throw an IllegalStateException."),

   JSR168_98(new TCK(98), Status.postponed("why?"), ""),

   JSR168_99(new TCK(99), "If a preference attribute definition does not contain the read-only" +
      " element set to true, the preference attribute is modifiable when the portlet is processing an action request in" +
      " any of the standard portlet modes (VIEW, EDIT or HELP)."),

   JSR168_100(new TCK(100), "Portlets are not restricted to use preference attributes defined in the" +
      " deployment descriptor. They can programmatically add preference attributes using names not defined in the" +
      " deployment descriptor. These preferences attributes must be treated as modifiable attributes."),

   JSR168_101(new TCK(101), "If a portlet definition includes a validator, the portlet container must" +
      " create a single validator instance per portlet definition. (according to spec: Testable=false ;)"),

   JSR168_102(new TCK(102), Status.specUntestable(""), ""),

   JSR168_103(new TCK(103), "When a validator is associated with the preferences of a portlet" +
      " definition, the store method of the PortletPreferences implementation must invoke the validate method of the" +
      " validator before writing the changes to the persistent store."),

   JSR168_104(new TCK(104), "If the validation fails, the PreferencesValidator implementation must" +
      " throw a ValidatorException. If a ValidatorException is thrown, the portlet container must cancel the store" +
      " operation and it must propagate the exception to the portlet"),

   JSR168_105(new TCK(105), "If the validation is successful, the store operation must be completed"),

   JSR168_106(new TCK(106), "For portlets within the same portlet application, a portlet container" +
      " must ensure that every portlet request generated as result of a group of requests originated from the portal to" +
      " complete a single client request receive or acquire the same session"),

   JSR168_107(new TCK(107), "In addition, if within these portlet requests more than one portlet" +
      " creates a session, the session object must be the same for all the portlets in the same portlet application"),

   JSR168_108(new TCK(108), "PortletSession objects must be scoped at the portlet application context level."),

   JSR168_109(new TCK(109), "The portlet container must not share the PortletSession object or the " +
      "attributes stored in it among different portlet applications or among different user sessions."),

   JSR168_110(new TCK(110), "Any object stored in the session using the APPLICATION_SCOPE is" +
      " available to any other portlet that belongs to the same portlet application and that handles a request identified" +
      " as being a part of the same session."),

   JSR168_111(new TCK(111), "Objects stored in the session using the PORTLET_SCOPE must be available" +
      " to the portlet during requests for the same portlet window that the objects where stored from."),

   JSR168_112(new TCK(112), "The object must be stored in the APPLICATION_SCOPE with the following" +
      " fabricated attribute name 'javax.portlet.p.<ID>?<ATTRIBUTE_NAME>'. <ID> is a unique identification for the portlet" +
      " window (assigned by the portal/portlet-container) that must not contain a '?' character."),

   JSR168_113(new TCK(113), "The PortletSession must store all attributes in the HttpSession of the" +
      " portlet application. A direct consequence of this is that data stored in the HttpSession by servlets or JSPs is" +
      " accessible to portlets through the PortletSession in the portlet application scope."),

   JSR168_114(new TCK(114), "Conversely, data stored by portlets in the PortletSession in the portlet" +
      " application scope is accessible to servlets and JSPs through the HttpSession."),

   JSR168_115(new TCK(115), "If the HttpSession object is invalidated, the PortletSession object must" +
      " also be invalidated by the portlet container."),

   JSR168_116(new TCK(116), "If the PortletSession object is invalidated by a portlet, the portlet" +
      " container must invalidate the associated HttpSession object."),

   JSR168_117(new TCK(117), "The attribute names must be the same if APPLICATION_SCOPE scope is" +
      " used."),

   JSR168_118(new TCK(118), "The attribute name has to conform with the specified prefixing if" +
      " PORTLET_SCOPE is used"),

   JSR168_119(new TCK(119), "The variant of these methods that does not receive a scope must be" +
      " treated as PORTLET_SCOPE."),

   JSR168_120(new TCK(120), Status.jbossUntestable("why?"), ""),

   JSR168_121(new TCK(121), "The getRequestDispatcher method takes a String argument describing a" +
      " path within the scope of the PortletContext of a portlet application. This path must begin with a / and it is" +
      " relative to the PortletContext root."),

   JSR168_122(new TCK(122), "The getNamedDispatcher method takes a String argument indicating the" +
      " name of a servlet known to the PortletContext of the portlet application. If no resource can be resolved based on" +
      " the given path or name the methods must return null"),

   JSR168_123(new TCK(123), "The parameters associated with a PortletRequestDispatcher are scoped to" +
      " apply only for the duration of the include call."),

   JSR168_124(new TCK(124), "To include a servlet or a JSP, a portlet calls the include method of the" +
      " PortletRequestDispatcher interface. The parameters to these methods must be the request and response arguments" +
      " that were passed in via the render method of the Portlet interface."),

   JSR168_125(new TCK(125), "The portlet container must ensure that the servlet or JSP called through" +
      " a PortletRequestDispatcher is called in the same thread as the PortletRequestDispatcher include invocation."),

   JSR168_126(new TCK(126), "Servlets and JSPs included from portlets must be handled as HTTP GET requests."),

   JSR168_127(new TCK(127), "Except for servlets obtained by using the getNamedDispatcher method, a" +
      " servlet or JSP being used from within an include call has access to the path used to obtain the" +
      " PortletRequestDispatcher. The following request attributes must be set: javax.servlet.include.request_uri," +
      " javax.servlet.include.context_path javax.servlet.include.servlet_path, javax.servlet.include.path_info," +
      " javax.servlet.include.query_string"),

   JSR168_128(new TCK(128), "javax.portlet.config, javax.portlet.request, javax.portlet.response." +
      " These attributes must be the same Portlet API objects accessible to the portlet doing the include call."),

   JSR168_129(new TCK(129), "The following methods of the HttpServletRequest must return null:" +
      " getProtocol, getRemoteAddr, getRemoteHost, getRealPath, and getRequestURL."),

   JSR168_130(new TCK(130), "The following methods of the HttpServletRequest must return the path and"+
      " query string information used to obtain the PortletRequestDispatcher object: getPathInfo, getPathTranslated,"+
      " getQueryString, getRequestURI and getServletPath"),

   JSR168_131(new TCK(131), "The following methods of the HttpServletRequest must be equivalent to" +
      " the methods of the PortletRequest of similar name: getScheme, getServerName,getServerPort, getAttribute," +
      " getAttributeNames, setAttribute,removeAttribute, getLocale, getLocales, isSecure, getAuthType, getContextPath," +
      " getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid"),

   JSR168_132(new TCK(132), "The following methods of the HttpServletRequest must be equivalent to" +
      " the methods of the PortletRequest of similar name with the provision defined in PLT.16.1.1 Query Strings in" +
      " Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),

   JSR168_133(new TCK(133), "The following methods of the HttpServletRequest must do no operations" +
      " and return null: getCharacterEncoding, setCharacterEncoding, getContentType, getInputStream and getReader."),

   JSR168_134(new TCK(134), "The getContentLength method of the HttpServletRequest must return 0."),


   JSR168_137(new TCK(137), "The getMethod method of the HttpServletRequest must always return 'GET'"),

   JSR168_138(new TCK(138), "The following methods of the HttpServletResponse must return" +
      " null:encodeRedirectURL and encodeRedirectUrl"),

   JSR168_141(new TCK(141), "The getLocale method of the HttpServletResponse must be based on the" +
      " getLocale method of the RenderResponse."),

   JSR168_142(new TCK(142), "If the servlet or JSP that is the target of a request dispatcher throws" +
      " a runtime exception or a checked exception of type IOException, it must be propagated to the calling portlet."),

   JSR168_143(new TCK(143), "All other exceptions, including a ServletException, must be wrapped with" +
      " a PortletException. The root cause of the exception must be set to the original exception before being" +
      " propagated."),


   // ******************************************************************************************************************
   // PLT.2 Overview
   JSR286_1(new TCK(1, "PLT.2.5"), Status.specUntestable(""), "Portlet V2.0 containers must support deploying JSR 168 portlets and the JSR 168 deployment descriptor"),


   // PLT.5 The Portlet Interface and Additional Life
   JSR286_2(new TCK(2, "PLT.5.1"), Status.specUntestable(""), "For a portlet, not hosted in a distributed environment " +
      "(the default), the portlet container must instantiate and use only one portlet object per portlet definition"),
   JSR286_3(new TCK(3, "PLT.5.1"), Status.specUntestable(""), "In the case where a portlet is deployed as part of a" +
      " portlet application marked as distributable, in the web.xml deployment descriptor, a portlet container may " +
      "instantiate only one portlet object per portlet definition -in the deployment descriptor- per virtual machine (VM)."),
   JSR286_4(new TCK(4, "PLT.5.2.1"), Status.specUntestable(""), "The portlet container must load the portlet class using the same ClassLoader " +
      "the servlet container uses for the web application part of the portlet application."),
   JSR286_5(new TCK(5, "PLT.5.2.2"), JSR168_4, "After the portlet object is instantiated, the portlet container must initialize " +
      "the portlet before invoking it to handle requests."),
   JSR286_6(new TCK(6, "PLT.5.2.2.1"), JSR168_5, "During initialization, the portlet object may throw an UnavailableException " +
      "or a PortletException. In this case, the portlet container must not place the portlet object into active " +
      "service and it must release the portlet object."),
   JSR286_7(new TCK(7, "PLT.5.2.2.1"), JSR168_6, "The destroy method must not be called because the initialization is considered " +
      "unsuccessful."),
   JSR286_8(new TCK(8, "PLT.5.2.2.1"), JSR168_7, "The portlet container may reattempt to instantiate and initialize the portlets " +
      "at any time after a failure. The exception to this rule is when an UnavailableException indicates a minimum time " +
      "of unavailability. When this happens the portlet container must wait for 30 the specified time to pass before " +
      "creating and initializing a new portlet object."),
   JSR286_9(new TCK(9, "PLT.5.2.2.1"), JSR168_8, "A RuntimeException thrown during initialization must be handled as a " +
      "PortletException."),
   JSR286_10(new TCK(10, "PLT.5.2.3"), Status.specUntestable(""), "Once the destroy method is called on a portlet " +
      "object, the portlet container must not route any requests to that portlet object."),
   JSR286_11(new TCK(11, "PLT.5.2.3"), Status.specUntestable(""), "If the portlet container needs to enable the " +
      "portlet again, it must do so with a new portlet object, which is a new instance of the portlet’s class."),
   JSR286_12(new TCK(12, "PLT.5.2.3"), Status.specUntestable(""), "If the portlet object throws a RuntimeException " +
      "within the execution of the destroy method the portlet container must consider the portlet object successfully " +
      "destroyed."),
   JSR286_13(new TCK(13, "PLT.5.2.3"), Status.specUntestable(""), "After the destroy method completes, the portlet " +
      "container must release the portlet object so that it is eligible for garbage collection."),
   JSR286_14(new TCK(14, "PLT.5.4"), JSR168_9, "If the client request is triggered by an action URL, the portal/portlet-container " +
      "must first trigger the action request by invoking the processAction method of the targeted portlet."),
   JSR286_15(new TCK(15, "PLT.5.4"), "The portal/portlet-container must wait until the action request finishes. Then, " +
      "the portal/portlet-container should call the processEvent methods of the event receiving portlets and after the " +
      "event processing is finished must trigger the render request by invoking the render method for all the portlets " +
      "in the portal page with the possible exception of portlets for which their content is being cached."),
   JSR286_16(new TCK(16, "PLT.5.4"), JSR168_11, "If the client request is triggered by a render URL, the portal/portlet-container " +
      "must invoke the render method for all the portlets in the portal page with the possible exception of portlets " +
      "for which their content is being cached."),
   JSR286_17(new TCK(17, "PLT.5.4"), "If the client request is triggered by a resource URL, the portal/portlet-container " +
      "must invoke the serveResource method of the target portlet with the possible exception of content that has a " +
      "valid cache entry."),
   JSR286_18(new TCK(18, "PLT.5.4.1"), "If the portlet issues a redirection, when the processAction method concludes, " +
      "the portal/portlet-container must send the redirection back to the user agent"),
   JSR286_19(new TCK(19, "PLT.5.4.5.4"), "If no matching annotated method is found GenericPortlet will dispatch to " +
      "the following methods: doView for handling VIEW requests"),
   JSR286_20(new TCK(20, "PLT.5.4.5.4"), "If no matching annotated method is found GenericPortlet will dispatch to " +
      "the following methods: doEdit for handling EDIT requests"),
   JSR286_21(new TCK(21, "PLT.5.4.5.4"), "If no matching annotated method is found GenericPortlet will dispatch to " +
      "the following methods: doHelp for handling HELP requests"),
   JSR286_22(new TCK(22, "PLT.5.4.5.4"), "If the window state of the portlet (see PLT.9 Window States Chapter) is " +
      "MINIMIZED, the render method of the GenericPortlet does not invoke any of the portlet mode rendering methods"),
   JSR286_23(new TCK(23, "PLT.5.4.7"), "If a portlet throws an exception in the processAction or processEvent method, " +
      "all operations on the ActionResponse must be ignored including set events."),
   JSR286_24(new TCK(24, "PLT.5.4.7"), Status.specUntestable(""), "If a permanent unavailability is indicated by the " +
      "UnavailableException, the portlet container must remove the portlet from service immediately, call the portlet’s " +
      "destroy method, and release the portlet object"),
   JSR286_25(new TCK(25, "PLT.5.4.7"), JSR168_19, "A RuntimeException thrown during the request handling must be handled as a " +
      "PortletException."),

   // PLT.6 Portlet Config
   JSR286_26(new TCK(26, "PLT.6.2"), JSR168_24, "If the root resource bundle does not contain the resources for these values and " +
      "the values are defined inline, the portlet container must add the inline values as resources of the root " +
      "resource bundle."),
   JSR286_27(new TCK(27, "PLT.6.2"), JSR168_25, "If the portlet definition does not define a resource bundle and the information" +
      " is defined inline in the deployment descriptor, the portlet container must create a ResourceBundle and populate" +
      " it, with the inline values, using the keys defined in the PLT.25.10 Resource Bundles Section."),
   JSR286_28(new TCK(28, "PLT.6.3"), "The getDefaultNamespace method of the PortletConfig interface returns the " +
      "default namespace for events and public render parameters set in the portlet deployment descriptor with " +
      "the default-namespace element, or the XML default namespace XMLConstants.NULL_NS_URI if no default namespace" +
      " is provided in the portlet deployment descriptor."),
   JSR286_29(new TCK(29, "PLT.6.4"), "The getPublicRenderParameterNames method of the PortletConfig interface returns" +
      " the public render parameter names found in the portlet definition in the deployment descriptor with the " +
      "supported-public-render-parameter element or an empty enumeration if no public render parameters are defined" +
      " for the current portlet definition."),
   JSR286_30(new TCK(30, "PLT.6.5"), "The getPublishingEventQNames method of the PortletConfig interface returns the" +
      " publishing event QNames found in the portlet definition in the deployment descriptor with the " +
      "supported-publishing-event element or an empty enumeration if no publishing events are defined for the " +
      "current portlet definition."),
   JSR286_31(new TCK(31, "PLT.6.5"), "If the event was defined using the name element instead of the qname element " +
      "the defined default namespace must be added as namespace for the returned QName."),
   JSR286_32(new TCK(32, "PLT.6.6"), "The getProcessingEventQNames method of the PortletConfig interface returns " +
      "the processing event QNames found in the portlet definition in the deployment descriptor with the " +
      "supported-processing-event element or an empty enumeration if no processing events are defined for the current" +
      " portlet definition."),
   JSR286_33(new TCK(33, "PLT.6.6"), "If the event was defined using the name element instead of the qname element the" +
      " defined default namespace must be added as namespace for the returned Qname."),
   JSR286_34(new TCK(34, "PLT.6.7"), "The getSupportedLocales method of the PortletConfig interface returns the " +
      "supported locales found in the portlet definition in the deployment descriptor with the supported-locale " +
      "element or an empty enumeration if no supported locales are defined for the current portlet definition."),

   // PLT,7 Portlet URLs

   JSR286_35(new TCK(35, "PLT.7.1"), JSR168_26, "The portal/portlet-container must not invoke the processAction method of the " +
      "targeted portlet of a render URL."),
   JSR286_36(new TCK(36, "PLT.7.1"), JSR168_27, "The portal/portlet-container must ensure that all the parameters set when " +
      "constructing the render URL become render parameters of the subsequent render requests for the portlet."),
   JSR286_37(new TCK(37, "PLT.7.1.1"), JSR168_28, "A call to any of the setParameter methods must replace any parameter with " +
      "the same name previously set."),
   JSR286_38(new TCK(38, "PLT.7.1.1"), "All the parameters a portlet adds to a BaseURL object must be made available " +
      "to the portlet as request parameters."),
   JSR286_39(new TCK(39, "PLT.7.1.1"), JSR168_30, "The portlet-container must “x-www-form-urlencoded” encode parameter names and " +
      "values added to a BaseURL object."),
   JSR286_40(new TCK(40, "PLT.7.1.1"), JSR168_31, "If a portal/portlet-container encodes additional information as parameters, it " +
      "must namespace them properly to avoid collisions with the parameters set and used by the portlet."),
   JSR286_41(new TCK(41, "PLT.7.1.2"), JSR168_32, "A portlet cannot create a portlet URL using a portlet mode that is not defined " +
      "as supported by the portlet or that the user it is not allowed to use. The setPortletMode methods must throw a " +
      "PortletModeException in that situation."),
   JSR286_42(new TCK(42, "PLT.7.1.2"), "The change of portlet mode must be effective for the request triggered by the " +
      "portlet URL."),
   JSR286_43(new TCK(43, "PLT.7.1.2"), "If the portlet mode is not set for a URL, it must have the portlet mode of the " +
      "current request as default."),
   JSR286_44(new TCK(45, "PLT.7.1.2"), JSR168_34, "A portlet cannot create a portlet URL using a window state that is not supported " +
      "by the portlet container. The setWindowState method must throw a WindowStateException if that is the case."),
   JSR286_45(new TCK(44, "PLT.7.1.2"), "If the window state is not set for a URL, it must have the window state of the " +
      "current request as default."),
   JSR286_46(new TCK(46, "PLT.7.1.3"), JSR168_35, "If setSecure is called with true, the transport for the request triggered with " +
      "this URL must be secure (i.e. HTTPS)."),
   JSR286_47(new TCK(47, "PLT.7.2.1"), "If the portlet application has specified one or more PortletURLGenerationListener " +
      "classes in the portlet deployment descriptor the portlet container must call: the method filterActionURL method for " +
      "all action URLs before executing the write or toString method of these action URLs"),
   JSR286_48(new TCK(48, "PLT.7.2.1"), "If the portlet application has specified one or more PortletURLGenerationListener " +
      "classes in the portlet deployment descriptor the portlet container must call: the method filterRenderURL method " +
      "for all render URLs before executing the write or toString method of these render URLs"),
   JSR286_49(new TCK(49, "PLT.7.2.1"), "If the portlet application has specified one or more PortletURLGenerationListener " +
      "classes in the portlet deployment descriptor the portlet container must call: the method filterResourceURL method " +
      "for all resource URLs before executing the write or toString method of these resource URLs"),
   JSR286_50(new TCK(50, "PLT.7.2.1"), "The portlet container must provide the PortletURL or ResourceURL to generate to " +
      "the filter methods and execute the write or toString method on the updated PortletURL or ResourceURL that is the " +
      "outcome of the filter method call."),
   JSR286_51(new TCK(51, "PLT.7.2.2"), "If more than one listener is registered the portlet container must chain the " +
      "listeners in the order of how they appear in the deployment descriptor."),

   // PLT.8 Portlet Modes

   JSR286_52(new TCK(52, "PLT.8.5"), "If no matching annotated method is found GenericPortlet will dispatch to the " +
      "doView, doEdit or doHelp method depending on the portlet mode indicated in the request using the doDispatch " +
      "method or throws a PortletException if the mode is not VIEW, EDIT, or HELP."),
   JSR286_53(new TCK(53, "PLT.8.6"), JSR168_37, "As all portlets must support the VIEW portlet mode, VIEW does not have to be " +
      "indicated."),
   JSR286_54(new TCK(54, "PLT.8.6"), JSR168_38, "The portlet must not be invoked in a portlet mode that has not been declared " +
      "as supported for a given markup type."),
   JSR286_55(new TCK(55, "PLT.8.6"), Status.specUntestable(""), "The portlet container must ignore all references to " +
      "custom portlet modes that are not supported by the portal implementation, or that have no mapping to portlet " +
      "modes supported by the portal."),

   // PLT.9 Window States

   JSR286_56(new TCK(56, "PLT.9.4"), JSR168_40, "If a custom window state defined in the deployment descriptor is not mapped to " +
      "a custom window state provided by the portal, portlets must not be invoked in that window state."),
   JSR286_57(new TCK(57, "PLT.9.5"), "As all portlets must at least support the pre-defined window states NORMAL, " +
      "MAXIMIZED, MINIMIZED, these window states do not have to be indicated."),
   JSR286_58(new TCK(58, "PLT.9.5"), Status.specUntestable(""), "The portlet container must ignore all references " +
      "to custom window states that are not supported by the portal implementation, or that have no mapping to window " +
      "states supported by the portal"),

   // PLT.10 Portlet Context

   JSR286_59(new TCK(59, "PLT.10.1"), Status.specUntestable(""), "There is one instance of the PortletContext interface " +
      "associated with each portlet application deployed into a portlet container."),
   JSR286_60(new TCK(60, "PLT.10.1"), Status.specUntestable(""), "In cases where the container is distributed over many " +
      "virtual machines, a portlet application will have an instance of the PortletContext interface for each VM."),
   JSR286_61(new TCK(61, "PLT.10.3"), JSR168_43, "The initialization parameters accessible through the PortletContext must be the " +
      "same that are accessible through the ServletContext of the portlet application."),
   JSR286_62(new TCK(62, "PLT.10.3"), JSR168_44, "A direct consequence of this is that data stored in the ServletContext by " +
      "servlets or JSPs is accessible to portlets through the PortletContext and vice versa."),
   JSR286_63(new TCK(63, "PLT.10.3"), JSR168_45, "The PortletContext must offer access to the same set of resources the " +
      "ServletContext exposes."),
   JSR286_64(new TCK(64, "PLT.10.3"), JSR168_46, "It must be accessible as a context attribute using the same constant " +
      "defined in the Servlet Specification SVR 3 Servlet Context Chapter, javax.servlet.context.tempdir."),
   JSR286_65(new TCK(64, "PLT.10.3"), "The portlet context must follow the same behavior and functionality that " +
      "the servlet context has for virtual hosting and reloading considerations. (see Servlet Specification SVR 3 " +
      "Servlet Context Chapter)"),
   JSR286_66(new TCK(66, "PLT.10.4.4"), "For such use cases the Java Portlet Specification provides the action-scoped " +
      "request attributes as container runtime option with the intent to provide portlets with these request attributes " +
      "until a new action occurs. This container runtime option must be supported by portlet containers."),

   // PLT.11 Portlet Requests

   JSR286_67(new TCK(67, "PLT.11.1.1"), JSR168_48, "If a portlet receives a request from a client request targeted to the " +
      "portlet itself, the parameters must be the string parameters encoded in the URL (added when creating the " +
      "PortletURL) and the string parameters sent by the client to the portlet as part of the client request."),
   JSR286_68(new TCK(68, "PLT.11.1.1"), JSR168_49, "The parameters the request object returns must be \"x-www-formurlencoded\" " +
      "decoded."),
   JSR286_69(new TCK(69, "PLT.11.1.1"), JSR168_55, "The value returned from the getParameter method must be the first value in " +
      "the array of String objects returned by getParameterValues"),
   JSR286_70(new TCK(70, "PLT.11.1.1"), JSR168_56, "If there is a single parameter value associated with a parameter name the " +
      "method must return is an array of size one containing the parameter value."),
   JSR286_71(new TCK(71, "PLT.11.1.1"), "The getParameterMap method must return an unmodifiable Map object."),
   JSR286_72(new TCK(72, "PLT.11.1.1"), "If the request does not have any parameters, the getParameterMap must return " +
      "an empty Map object"),
   JSR286_73(new TCK(73, "PLT.11.1.1"), "Parameters set on the portlet URL and the post body are aggregated into the " +
      "request parameter set. Portlet URL parameters are presented before post body data."),
   JSR286_74(new TCK(74, "PLT.11.1.1.2"), "The portlet-container must not propagate parameters received in an action " +
      "or event request to subsequent render requests of the portlet."),
   JSR286_75(new TCK(75, "PLT.11.1.1.2"), "The portlet-container must not propagate parameters received in an action " +
      "to subsequent event requests of the portlet."),
   JSR286_76(new TCK(76, "PLT.11.1.1.2"), "The set render parameters must be provided to the processEvent and render " +
      "calls of at least the current client request."),
   JSR286_77(new TCK(77, "PLT.11.1.1.3"), "If a portlet receives a render request following an action or event " +
      "request as part of the same client request, the parameters received with render request must be the render " +
      "parameters set during the action or event request."),
   JSR286_78(new TCK(78, "PLT.11.1.1.3"), "If a portlet receives a render request that is the result of invoking " +
      "a render URL targeting this portlet the render parameters received with the render request must be the " +
      "parameters set on the render URL if these were not changed by the portlet as a result of an container event " +
      "received for this render URL."),
   JSR286_79(new TCK(79, "PLT.11.1.1.3"), "Commonly, portals provide controls to change the portlet mode and the " +
      "window state of portlets. The URLs these controls use are generated by the portal. Client requests triggered " +
      "by those URLs must be treated as render URLs and the existing render parameters must be preserved."),
   JSR286_80(new TCK(80, "PLT.11.1.1.3"), "A portlet must not see any parameter targeted to other portlets."),
   JSR286_81(new TCK(81, "PLT.11.1.2"), Status.specUntestable(""), "The supportedpublic-render-parameter element must " +
      "reference the identifier " +
      "of a public render parameter defined in the portlet application section in a public-render-parameter element"),
   JSR286_82(new TCK(82, "PLT.11.1.2"), "The portlet container must only send those public render parameters to a " +
      "portlet which the portlet has defined support for using supported-public-render-parameter element in the " +
      "portlet.xml."),
   JSR286_83(new TCK(83, "PLT.11.1.2"), "The portlet container must only share those render parameters of a portlet " +
      "which the portlet has declared as supported public render parameters using supported-public-render-parameter " +
      "element in the portlet.xml"),
   JSR286_84(new TCK(84, "PLT.11.1.2"), "If the portlet was the target of a render URL and this render URL has set " +
      "a specific public render parameter the portlet must receive at least this render parameter"),
   JSR286_85(new TCK(85, "PLT.11.1.2"), "If a action or resource parameter has the same name as a public render " +
      "parameter the public render parameter values must be the last entries in the parameter value array."),
   JSR286_86(new TCK(86, "PLT.11.1.2"), "Portlets can access a merged set of public and private parameters via the " +
      "getParameter methods on the PortletRequest or separated as maps of private parameters via the " +
      "getPrivateParameterMap method and public parameters via the getPublicParameterMap method."),
   JSR286_87(new TCK(87, "PLT.11.1.3"), JSR168_57, Status.specUntestable(""), "Extra parameters used by the portal/portlet-container must be invisible " +
      "to the portlets receiving the request."),
   JSR286_88(new TCK(88, "PLT.11.1.4.1"), "The portlet can access a map with user information attributes via the " +
      "request attribute PortletRequest.USER_INFO."),
   JSR286_89(new TCK(89, "PLT.11.1.4.2"), "The PortletRequest.CCPP_PROFILE request attribute must return a " +
      "javax.ccpp.Profile based on the current portlet request."),
   JSR286_90(new TCK(90, "PLT.11.1.4.4"), "The LIFECYCLE_PHASE request attribute of the PortletRequest interface " +
      "allows a portlet to determine the current lifecycle phase of this request. This attribute value must be " +
      "ACTION_PHASE if the current request is of type ActionRequest, EVENT_PHASE if the current request is of type " +
      "EventRequest, RENDER_PHASE if the current request is of type RenderRequest, and RESOURCE_SERVING_PHASE if the " +
      "current request is of type ResourceRequest."),
   JSR286_91(new TCK(91, "PLT.11.1.6"), JSR168_58, "If the portlet application is rooted at the base of the web server URL " +
      "namespace (also known as \"default\" context), this path must be an empty string."),
   JSR286_92(new TCK(92, "PLT.11.1.6"), JSR168_59, "Otherwise, it must be the path the portlet application is rooted to, the " +
      "path must start with a '/' and it must not end with a '/' character."),
   JSR286_93(new TCK(93, "PLT.11.1.7"), JSR168_60, "If the user is not authenticated the getAuthType method must return null. "),
   JSR286_94(new TCK(94, "PLT.11.1.8"), JSR168_61, "The first element of the enumeration must be the same content type returned " +
      "by the getResponseContentType method."),
   JSR286_95(new TCK(95, "PLT.11.1.8"), "If the getResponseContentType or getResponseContentTypes methods are exposed " +
      "via an ActionRequest, EventRequest, or RenderRequest the following additional restrictions apply: The content " +
      "type must only includes the MIME type, not the character set."),
   JSR286_96(new TCK(96, "PLT.11.1.8"), "If the getResponseContentType or getResponseContentTypes methods are " +
      "exposed via an ActionRequest, EventRequest, or RenderRequest the following additional restrictions apply: " +
      "The getResponseContentTypes method must return only the content types supported by the current portlet mode " +
      "of the portlet."),
   JSR286_97(new TCK(97, "PLT.11.1.12"), "The portlet window ID retrieved with the getWindowID method must be the " +
      "same as the one that is used by the portlet container for scoping the portlet-scope session attributes."),
   JSR286_98(new TCK(98, "PLT.11.2.1"), JSR168_63, "Only one of the two methods, getPortletInputStream or getReader, can be " +
      "used during an action request. If the input stream is obtained, a call to the getReader must throw an " +
      "IllegalStateException. Similarly, if the reader is obtained, a call to the getPortletInputStream must throw " +
      "an IllegalStateException."),
   JSR286_99(new TCK(99, "PLT.11.2.1"), "If the user request HTTP POST data is of type " +
      "application/x-www-form-urlencoded, this data has been already processed by the portal/portlet-container and " +
      "is available as request parameters. The getPortletInputStream and getReader methods must throw an " +
      "IllegalStateException if called."),

   // PLT.12 Portlet Responses

   JSR286_100(new TCK(100, "PLT.12.1.3"), JSR168_86, "The getNamespace method must provide the portlet with a mechanism that " +
      "ensures the uniqueness of the returned string in the whole portal page"),
   JSR286_101(new TCK(101, "PLT.12.1.3"), "The getNamespace method must return the same value for the lifetime of " +
      "the portlet window."),
   JSR286_102(new TCK(102, "PLT.12.1.3"), JSR168_88, "The getNamespace method must return a valid identifier as defined in the " +
      "3.8 Identifier Section of the Java Language Specification Second Edition."),
   JSR286_103(new TCK(103, "PLT.12.2.1"), JSR168_72, "A call to any of the setRenderParameter methods must replace any " +
      "parameter with the same name previously set."),
   JSR286_104(new TCK(104, "PLT.12.2.2"), JSR168_69, "If a portlet attempts to set a portlet mode that it is not allowed to switch " +
      "to, a PortletModeException must be thrown."),
   JSR286_105(new TCK(105, "PLT.12.2.2"), JSR168_68, "If a portlet attempts to set a window state that it is not allowed to switch " +
      "to, a WindowStateException must be thrown."),
   JSR286_106(new TCK(106, "PLT.12.3.1"), JSR168_65, "The sendRedirect(String location) method instructs the portal/portlet-container " +
      "to set the appropriate headers and content body to redirect the user to a different URL. A fully qualified URL " +
      "or a full path URL must be specified. If a relative path URL is given, an IllegalArgumentException must be thrown."),
   JSR286_107(new TCK(107, "PLT.12.3.1"), JSR168_66, "If the sendRedirect(String location) method is called after the setPortletMode, " +
      "setWindowState, setRenderParameter or setRenderParameters methods of the ActionResponse interface, an " +
      "IllegalStateException must be thrown and the redirection must not be executed."),
   JSR286_108(new TCK(108, "PLT.12.3.1"), "If a relative path URL is given, an IllegalArgumentException must be thrown."),
   JSR286_109(new TCK(109, "PLT.12.3.1"), "The portlet container must attach a render URL with the currently set " +
      "portlet mode, window state and render parameters on the ActionResponse and the current public render parameters."),
   JSR286_110(new TCK(110, "PLT.12.3.1"), "The attached URL must be available as query parameter value under the " +
      "key provided with the renderUrlParamName parameter."),
   JSR286_111(new TCK(111, "PLT.12.3.1"), "New values for portlet mode, window state, private or public render " +
      "parameters must be encoded in the attached render URLcxi,"),
   JSR286_112(new TCK(112, "PLT.12.3.1"), JSR168_76, "For the render response the setContentType method must throw an " +
      "IllegalArgumentException if the content type set does not match (including wildcard matching) any of the " +
      "content types returned by the getResponseContentType method of the PortletRequest objectcxii."),
   JSR286_113(new TCK(113, "PLT.12.3.1"), JSR168_78, "If the portlet has set a content type, the getContentType method must " +
      "return it. Otherwise, the getContentType method must return null."),
   JSR286_114(new TCK(114, "PLT.12.5.2"), JSR168_79, "A portlet may generate its content by writing to the OutputStream or to " +
      "the Writer of the MimeResponse object. A portlet must use only one of these objects. The portlet container must " +
      "throw an IllegalStateException if a portlet attempts to use both."),
   JSR286_115(new TCK(115, "PLT.12.5.5"), JSR168_80, "The getBufferSize method returns the size of the underlying buffer being " +
      "used. If no buffering is being used, this method must return the int value of 0 (zero)."),
   JSR286_116(new TCK(116, "PLT.12.5.5"), JSR168_81, "The portlet can request a preferred buffer size by using the setBufferSize " +
      "method. The buffer assigned is not required to be the size requested by the portlet, but must be at least as " +
      "large as the size requested."),
   JSR286_117(new TCK(117, "PLT.12.5.5"), JSR168_82, "The reset method clears data in the buffer when the response is not " +
      "committed. Properties set by the portlet prior to the reset call must be cleared as well."),
   JSR286_118(new TCK(118, "PLT.12.5.5"), JSR168_83, "If the response is committed and the reset or resetBuffer method is called, " +
      "an IllegalStateException must be thrown."),
   JSR286_119(new TCK(119, "PLT.12.5.5"), "The response and its associated buffer must be unchanged."),
   JSR286_120(new TCK(120, "PLT.12.5.5"), "When using a buffer, the container must immediately flush the contents of " +
      "a filled buffer to the portal application."),
   JSR286_121(new TCK(121, "PLT.12.6.1"), Status.specUntestable(""), "The setTitle method must be called before the " +
      "output of the portlet has been committed, if called after it should be ignored."),


   // PLT.13 Resource Serving

   JSR286_122(new TCK(122, "PLT.13.1.4"), "The portlet must be able to get the HTTP method with which this request " +
      "was made, for example, GET, POST, or PUT, via the getMethod call on the ResourceRequest."),
   JSR286_123(new TCK(123, "PLT.13.1.5"), "The portlet must be able to get the resource ID that was set on the resource " +
      "URL with the setResourceID method via the getResourceID method from the resource request."),
   JSR286_124(new TCK(124, "PLT.13.1.5"), "If no resource ID was set on the resource URL the getResourceID method must " +
      "return null."),
   JSR286_125(new TCK(125, "PLT.13.1.6"), "When an end user invokes such a resource URL the portlet container must " +
      "call the serveResource method of the portlet or return a valid cached result for this resource URL"),
   JSR286_126(new TCK(126, "PLT.13.1.6"), "The portlet container must not call the processAction or processEvent " +
      "method."),
   JSR286_127(new TCK(127, "PLT.13.1.6"), "ResourceURLs cannot change the current portlet mode, window state or " +
      "render parameters"),
   JSR286_128(new TCK(128, "PLT.13.1.6"), "If a parameter is set that has the same name as a render parameter that " +
      "this resource URL contains, the render parameter values must be the last entries in the parameter value array."),
   JSR286_129(new TCK(129, "PLT.13.1.7"), "If portlet URLs are included in the markup, portals / portlet containers " +
      "must create correct portlet URLs for all text-based markup types."),
   JSR286_130(new TCK(130, "PLT.13.1.7"), "Setting a cachability different from FULL must result in an " +
      "IllegalStateException"),
   JSR286_131(new TCK(131, "PLT.13.1.7"), "Attempts to create URLs that are not of type FULL or are not resource " +
      "URLs in the current or a downstream response must result in" +
      "an IllegalStateException"),
   JSR286_132(new TCK(132, "PLT.13.1.7"), "Creating other URLs, e.g. resource URLs of type PAGE or action or render " +
      "URLs, must result in an IllegalStateException"),


   // PLT.14 Serving Fragments through Portlets

   // NOTHING

   // PLT.15 Coordination between portlets

   JSR286_133(new TCK(133, "PLT.15.2.2"), "The event must always have a name and may optionally have a value."),
   JSR286_134(new TCK(134, "PLT.15.2.2"), "If the event has a value it must be based on the type defined in the " +
      "deployment descriptor." ),
   JSR286_135(new TCK(135, "PLT.15.2.3"), "The portlet can publish events via the StateAwareResponse.setEvent " +
      "method."),
   JSR286_136(new TCK(136, "PLT.15.2.3"), "It is also valid to call StateAwareResponse.setEvent multiple times " +
      "in the current processAction or processEvent method." ),
   JSR286_137(new TCK(137, "PLT.15.2.3"), "If only the local part is specified the namespace must be the default " +
      "namespace defined in the portlet deployment descriptor with the default namespace element."),
   JSR286_138(new TCK(138, "PLT.15.2.3"), "If no such element is provided in the portlet deployment descriptor the " +
      "XML default namespace javax.xml.XMLConstants.NULL_NS_URI must be assumed."),
   JSR286_139(new TCK(139, "PLT.15.2.3"), "Otherwise the setEvent method on the StateAwareResponse must throw a " +
      "java.lang.IllegalArgumentException."),
   JSR286_140(new TCK(140, "PLT.15.2.4.1"), "The event definition must contain an event name."),
   JSR286_141(new TCK(141, "PLT.15.2.4.1"), "The portlet container must use the event name entry in the portlet " +
      "deployment descriptor as event name when submitting an event to the portlet."),
   JSR286_142(new TCK(142, "PLT.15.2.4"), "The portlet can send events which are not declared in the portlet " +
      "deployment descriptor at runtime using the setEvent method on either the ActionResponse or EventResponse."),
   JSR286_143(new TCK(143, "PLT.15.2.5"), "Events are valid only in the current client request and the portlet " +
      "container must therefore deliver all events within the current client request."),
   JSR286_144(new TCK(144, "PLT.15.2.5"), "Event distribution must be serialized for a specific portlet window per " +
      "client request so that at any given time a portlet window is only processing one event in the processEvent " +
      "method for the current client request."),
   JSR286_145(new TCK(145, "PLT.15.2.5"), "Portlet event processing may occur after the processing of the action, " +
      "if the portlet was target of an action URL, and must be finished before the render phase."),
   JSR286_146(new TCK(146, "PLT.15.2.6"), "If a portlet throws an exception in the processEvent method, all operations" +
      " on the EventResponse must be ignored."),
   JSR286_147(new TCK(147, "PLT.15.2.6"), "If a permanent unavailability is indicated by the UnavailableException," +
      " the portlet container must remove the portlet from service immediately, call the portlet’s destroy method," +
      " and release the portlet object."),

   // PLT.17 Portlet Preferences

   JSR286_148(new TCK(148, "PLT.17.1"), "Preferences attributes can be set to null"),
   JSR286_149(new TCK(149, "PLT.17.1"), JSR168_91, "Preferences values must not be modified if the values in the Map are altered"),
   JSR286_150(new TCK(150, "PLT.17.1"), JSR168_92, "The reset method must reset a preference attribute to its default value. " +
      "If there is no default value, the preference attribute must be deleted."),
   JSR286_151(new TCK(151, "PLT.17.1"), JSR168_93, "If a preference attribute is read only, the setValue, setValues and reset " +
      "methods must throw a ReadOnlyException when the portlet is in any of the standard modes."),
   JSR286_152(new TCK(152, "PLT.17.1"), JSR168_94, "The store method must persist all the changes made to the PortletPreferences " +
      "object in the persistent store."),
   JSR286_153(new TCK(153, "PLT.17.1"), JSR168_95, "The store method must be conducted as an atomic transaction regardless of how " +
      "many preference attributes have been modified."),
   JSR286_154(new TCK(154, "PLT.17.1"), "All changes made to PortletPreferences object not followed by a call to the " +
      "store method must be discarded when the portlet finishes the processAction, processEvent, or serveResource method."),
   JSR286_155(new TCK(155, "PLT.17.1"), JSR168_97, "If the store method is invoked within the scope of a render method invocation, " +
      "it must throw an IllegalStateException."),
   JSR286_156(new TCK(156, "PLT.17.1"), "The PortletPreferences object must reflect the current values of the " +
      "persistent store when the portlet container invokes the processAction, processEvent, render and serveResource " +
      "methods of the portlet."),
   JSR286_157(new TCK(157, "PLT.17.3"), JSR168_99, "If a preference attribute definition does not contain the read-only element " +
      "set to true, the preference attribute is modifiable when the portlet is processing an action request in any " +
      "of the standard portlet modes (VIEW, EDIT or HELP)."),
   JSR286_158(new TCK(158, "PLT.17.3"), JSR168_100, "Portlets are not restricted to use preference attributes defined in the " +
      "deployment descriptor. They can programmatically add preference attributes using names not defined in the " +
      "deployment descriptor. These preferences attributes must be treated as modifiable attributes."),
   JSR286_159(new TCK(159, "PLT.17.4"), JSR168_103, "When a validator is associated with the preferences of a portlet definition, " +
      "the store method of the PortletPreferences implementation must invoke the validate method of the validator " +
      "before writing the changes to the persistent store."),
   JSR286_160(new TCK(160, "PLT.17.4"), JSR168_104, "If a ValidatorException is thrown, the portlet container must cancel the " +
      "store operation and it must propagate the exception to the portlet."),
   JSR286_161(new TCK(161, "PLT.17.4"), JSR168_105, "If the validation is successful, the store operation must be completed"),


   // PLT.18 Sessions

   JSR286_162(new TCK(162, "PLT.18.1"), JSR168_106, "For portlets within the same portlet application, a portlet container must " +
      "ensure that every portlet request generated as result of a group of requests originated from the portal to " +
      "complete a single client request receive or acquire the same session."),
   JSR286_163(new TCK(163, "PLT.18.1"), JSR168_107, "In addition, if within these portlet requests more than one portlet creates " +
      "a session, the session object must be the same for all the portlets in the same portlet application"),
   JSR286_164(new TCK(164, "PLT.18.2"), JSR168_108, "PortletSession objects must be scoped at the portlet application context level."),
   JSR286_165(new TCK(165, "PLT.18.2"), JSR168_109, "The portlet container must not share the PortletSession object or the " +
      "attributes stored in it among different portlet applications or among different user sessions."),
   JSR286_166(new TCK(166, "PLT.18.3"), JSR168_110, "Any object stored in the session using the APPLICATION_SCOPE is available " +
      "to any other portlet that belongs to the same portlet application and that handles a request identified as " +
      "being a part of the same session."),
   JSR286_167(new TCK(167, "PLT.18.3"), JSR168_111, "Objects stored in the session using the PORTLET_SCOPE must be available to " +
      "the portlet during requests for the same portlet window that the objects where stored from."),
   JSR286_168(new TCK(168, "PLT.18.3"), "The object must be stored in the APPLICATION_SCOPE with the following " +
      "fabricated attribute name ‘javax.portlet.p.<ID>?<ATTRIBUTE_NAME>’. <ID> is a unique identification for the " +
      "portlet window (assigned by the portal/portlet-container) that must be equal to the ID returned by the" +
      " PortletRequest.getWindowID() method and not contain a ‘?’ character."),
   JSR286_169(new TCK(169, "PLT.18.4"), JSR168_113, "The container must ensure that all attributes placed in the PortletSession " +
      "are also available in the HttpSession of the portlet application. A direct consequence of this is that data " +
      "stored in the HttpSession by servlets or JSPs of the Portlet Application is accessible to portlets through " +
      "the PortletSession in the portlet application scope."),
   JSR286_170(new TCK(170, "PLT.18.4"), JSR168_114, "Conversely, data stored by portlets in the PortletSession in the portlet " +
      "application scope is accessible to servlets and JSPs through the HttpSession."),
   JSR286_171(new TCK(171, "PLT.18.4"), JSR168_115, "If the HttpSession object is invalidated, the PortletSession object must " +
      "also be invalidated by the portlet container."),
   JSR286_172(new TCK(172, "PLT.18.4"), JSR168_116, "If the PortletSession object is invalidated by a portlet, the portlet " +
      "container must invalidate the associated HttpSession object."),
   JSR286_173(new TCK(173, "PLT.18.4.1"), JSR168_117, "The getAttribute, setAttribute, removeAttribute and getAttributeNames " +
      "methods of the PortletSession interface must provide the same functionality as the methods of the HttpSession " +
      "interface with identical names adhering to the following rules: The attribute names must be the same if " +
      "APPLICATION_SCOPE scope is used."),
   JSR286_174(new TCK(174, "PLT.18.4.1"), JSR168_118, "The getAttribute, setAttribute, removeAttribute and getAttributeNames " +
      "methods of the PortletSession interface must provide the same functionality as the methods of the HttpSession " +
      "interface with identical names adhering to the following rules: The attribute name has to conform with the " +
      "specified prefixing if PORTLET_SCOPE is used."),
   JSR286_175(new TCK(175, "PLT.18.4.1"), JSR168_119, "The getAttribute, setAttribute, removeAttribute and getAttributeNames " +
      "methods of the PortletSession interface must provide the same functionality as the methods of the HttpSession " +
      "interface with identical names adhering to the following rules: The variant of these methods that does not " +
      "receive a scope must be treated as PORTLET_SCOPE."),
   JSR286_176(new TCK(176, "PLT.18.9(servlet spec)"), "These considerations include Threading Issues, Distributed " +
      "Environments and Client Semantics."),



   // PLT.19 Dispatching Requests to Servlets and JSPs

   JSR286_177(new TCK(177, "PLT.19.1"), "The getRequestDispatcher method takes a String argument describing a path " +
      "within the scope of the PortletContext of a portlet application. This path must begin with a ‘/’ and it is " +
      "relative to the PortletContext root."),
   JSR286_178(new TCK(178, "PLT.19.1"), "If no resource can be resolved based on the given path or name the methods " +
      "must return null."),
   JSR286_179(new TCK(179, "PLT.19.1.1"), "The parameters associated with a PortletRequestDispatcher are scoped to " +
      "apply only for the duration of the include call."),
   JSR286_180(new TCK(180, "PLT.19.2"), "The parameters to these methods must be the request and response arguments " +
      "that were passed in via the corresponding lifecycle method (e.g. processAction, processEvent, serveResource, " +
      "render) , or the request and response arguments must be instances of the corresponding subclasses of the request " +
      "and response wrapper classes that were introduced for version 2.0 of the specification."),
   JSR286_181(new TCK(181, "PLT.19.2"), "The portlet container must ensure that the servlet or JSP called through a" +
      " PortletRequestDispatcher is called in the same thread as the PortletRequestDispatcher include invocation"),
   JSR286_182(new TCK(182, "PLT.19.3"), "Servlets and JSPs included from portlets in the render method must be handled " +
      "as HTTP GET requests."),
   JSR286_183(new TCK(183, "PLT.19.3.1"), "Except for servlets obtained by using the getNamedDispatcher method, a " +
      "servlet or JSP being used from within an include call has access to the path used to obtain the " +
      "PortletRequestDispatcher. The following request attributes must be set: javax.servlet.include.request_uri, " +
      "javax.servlet.include.context_path, javax.servlet.include.servlet_path, javax.servlet.include.path_info, " +
      "javax.servlet.include.query_string"),
   JSR286_184(new TCK(184, "PLT.19.3.2"), "These attributes must be the same Portlet API objects accessible to the " +
      "portlet doing the include call."),

   // PLT.19.3.3 Request and Response Objects for Included Servlets/JSPs from within the Action and Event processing Methods
   JSR286_185(new TCK(185, "PLT.19.3.3"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getRealPath, getLocalAddress, getLocalName, and getRequestURL."),
   JSR286_186(new TCK(186, "PLT.19.3.3"), "The following methods of the HttpServletRequest must return ‘0’: " +
      "getRemotePort and getLocalPort."),
   JSR286_187(new TCK(187, "PLT.19.3.3"), "The following methods of the HttpServletRequest must return the path " +
      "and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, " +
      "getPathTranslated, getQueryString, getRequestURI and getServletPath."),
   JSR286_188(new TCK(188, "PLT.19.3.3"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType," +
      "getContextPath, getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_189(new TCK(189, "PLT.19.3.3"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the PortletRequest of similar name with the provision defined in PLT.19.1.1 Query Strings " +
      "in Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_190(new TCK(190, "PLT.19.3.3"), "In case of an include from processAction, the following methods of the " +
      "HttpServletRequest must be based on the corresponding methods of the" +
      "ActionRequest: getCharacterEncoding, setCharacterEncoding, getContentType, getInputStream, getContentLength, " +
      "getMethod and getReader."),
   JSR286_191(new TCK(191, "PLT.19.3.3"), "In case of an include from processEvent, the following methods of the " +
      "HttpServletRequest must do no operations and/or return null: getCharacterEncoding, setCharacterEncoding, " +
      "getContentType, getInputStream and getReader"),
   JSR286_192(new TCK(192, "PLT.19.3.3"), "The getContentLength method of the HttpServletRequest must return 0."),
   JSR286_193(new TCK(193, "PLT.19.3.3"), "The getMethod method of the HTTPServletRequest must be based on the " +
      "corresponding method of the EventRequest, which must provide the name of the HTTP method with which the " +
      "original action request was made."),
   JSR286_194(new TCK(194, "PLT.19.3.3"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_195(new TCK(195, "PLT.19.3.3"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_196(new TCK(196, "PLT.19.3.3"), "The getProtocol method of the HttpServletRequest must always return " +
      "‘HTTP/1.1’.The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’."),
   JSR286_197(new TCK(197, "PLT.19.3.3"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL, encodeRedirectUrl, getCharacterEncoding, getContentType, getLocale, resetBuffer, reset."),
   JSR286_198(new TCK(198, "PLT.19.3.3"), "The following method of the HttpServletResponse must return 0: getBufferSize."),
   JSR286_199(new TCK(199, "PLT.19.3.3"), "The following methods of the HttpServletResponse must return an " +
      "outputstream / writer that ignores any output written to it: getOutputStream and getWriter."),
   JSR286_200(new TCK(200, "PLT.19.3.3"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the ActionResponse/EventResponse of similar name: encodeURL and encodeUrl."),
   JSR286_201(new TCK(201, "PLT.19.3.3"), Status.specUntestable("impl"), "The following methods of the HttpServletResponse must perform no " +
      "operations: setContentType, setCharacterEncoding, setContentLength, setLocale, addCookie, sendError, " +
      "sendRedirect, setDateHeader, addDateHeader, setHeader, addHeader, setIntHeader, addIntHeader, setStatus," +
      "setBufferSize and flushBuffer."),
   JSR286_202(new TCK(202, "PLT.19.3.3"), "The containsHeader method of the HttpServletResponse must return false."),
   JSR286_203(new TCK(203, "PLT.19.3.3"), "The isCommitted method of the HttpServletResponse must return true."),

   // PLT.19.3.4 Request and Response Objects for Included Servlets/JSPs from within the Render Method
   JSR286_204(new TCK(204, "PLT.19.3.4"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getLocalAddress, getLocalName, getRealPath, and getRequestURL."),
   JSR286_205(new TCK(205, "PLT.19.3.4"), "The following methods of the HttpServletRequest must return ‘0’: " +
      "getRemotePort and getLocalPort."),
   JSR286_206(new TCK(206, "PLT.19.3.4"), "The following methods of the HttpServletRequest must return the " +
      "path and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, " +
      "getPathTranslated, getQueryString, getRequestURI and getServletPath."),
   JSR286_207(new TCK(207, "PLT.19.3.4"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType, getContextPath, " +
      "getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_208(new TCK(208, "PLT.19.3.4"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the PortletRequest of similar name with the provision defined in PLT.18.1.1 Query Strings in " +
      "Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_209(new TCK(209, "PLT.19.3.4"), "The following methods of the HttpServletRequest must do no operations " +
      "and return null: getCharacterEncoding, setCharacterEncoding, getContentType, getInputStream and getReader."),
   JSR286_210(new TCK(210, "PLT.19.3.4"), "The getContentLength method of the HttpServletRequest must return 0"),
   JSR286_211(new TCK(211, "PLT.19.3.4"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_212(new TCK(212, "PLT.19.3.4"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_213(new TCK(213, "PLT.19.3.4"), "The getMethod method of the HttpServletRequest must always return ‘GET’."),
   JSR286_214(new TCK(214, "PLT.19.3.4"), "The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’."),
   JSR286_215(new TCK(215, "PLT.19.3.4"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL and encodeRedirectUrl."),
   JSR286_216(new TCK(216, "PLT.19.3.4"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the RenderResponse of similar name: getCharacterEncoding, setBufferSize, flushBuffer, " +
      "resetBuffer, reset, getBufferSize, isCommitted, getOutputStream, getWriter, encodeURL and encodeUrl."),
   JSR286_217(new TCK(217, "PLT.19.3.4"), Status.specUntestable("impl"), "The following methods of the HttpServletResponse must perform no " +
      "operations: setContentType, setContentLength, setLocale, addCookie, sendError, sendRedirect, setDateHeader, " +
      "addDateHeader, setHeader, addHeader, setIntHeader, addIntHeader and setStatus."),
   JSR286_218(new TCK(218, "PLT.19.3.4"), "The containsHeader method of the HttpServletResponse must return false."),
   JSR286_219(new TCK(219, "PLT.19.3.4"), "The getLocale method of the HttpServletResponse must be based on the " +
      "getLocale method of the RenderResponse."),

   // PLT.19.3.5 Request and Response Objects for Included Servlets/JSPs from within the ServeResource Method
   JSR286_220(new TCK(220, "PLT.19.3.5"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getLocalAddress, getLocalName, getRealPath, and getRequestURL."),
   JSR286_221(new TCK(221, "PLT.19.3.5"), "The following methods of the HttpServletRequest must return ‘0’:" +
      " getRemotePort and getLocalPort."),
   JSR286_222(new TCK(222, "PLT.19.3.5"), "The following methods of the HttpServletRequest must return the " +
      "path and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, " +
      "getPathTranslated, getQueryString, getRequestURI and getServletPath."),
   JSR286_223(new TCK(223, "PLT.19.3.5"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType," +
      "getContextPath, getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_224(new TCK(224, "PLT.19.3.5"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the ResourceRequest of similar name: getCharacterEncoding, setCharacterEncoding, getContentType, " +
      "getMethod, getContentLength and getReader."),
   JSR286_225(new TCK(225, "PLT.19.3.5"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the PortletRequest of similar name with the provision defined in PLT.18.1.1 Query Strings in " +
      "Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_226(new TCK(226, "PLT.19.3.5"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_227(new TCK(227, "PLT.19.3.5"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_228(new TCK(228, "PLT.19.3.5"), "The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’."),
   JSR286_229(new TCK(229, "PLT.19.3.5"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL and encodeRedirectUrl."),
   JSR286_230(new TCK(230, "PLT.19.3.5"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the ResourceResponse of similar name: getCharacterEncoding, setBufferSize, flushBuffer, " +
      "resetBuffer, reset, getBufferSize, isCommitted, getOutputStream, getWriter, getLocale, encodeURL and encodeUrl."),
   JSR286_231(new TCK(231, "PLT.19.3.5"), Status.specUntestable("impl"), "The following methods of the HttpServletResponse must perform no " +
      "operations: sendError, sendRedirect, addCookie, setDateHeader, addDateHeader, setHeader, addHeader, " +
      "setIntHeader, addIntHeader, setContentLength, setCharacterEncoding, setContentType, setLocale and setStatus."),
   JSR286_232(new TCK(232, "PLT.19.3.5"), "The containsHeader method of the HttpServletResponse must return false."),

   //
   JSR286_233(new TCK(233, "PLT.19.3.7"), "If the servlet or JSP that is the target of a request dispatcher throws a " +
      "runtime exception or a checked exception of type IOException, it must be propagated to the calling portlet."),
   JSR286_234(new TCK(234, "PLT.19.3.7"), "All other exceptions, including a ServletException, must be wrapped with a" +
      " PortletException. The root cause of the exception must be set to the original exception before being propagated."),
   JSR286_235(new TCK(235, "PLT.19.3.8"), "Thus the portlet acts as starting point of the include chain and the " +
      "included / forwarded servlet must get the path and query string information used to obtain the " +
      "PortletRequestDispatcher object."),
   JSR286_236(new TCK(236, "PLT.19.4"), "If output data exists in the response buffer that has not been committed, " +
      "the content must be cleared before the target servlet’s service method is called."),
   JSR286_237(new TCK(237, "PLT.19.4"), "If the response has been committed, an IllegalStateException must be thrown."),
   JSR286_238(new TCK(238, "PLT.19.4"), "Before the forward method of the RequestDispatcher interface returns, the " +
      "response content must be sent and committed, and closed by the portlet container."),
   JSR286_239(new TCK(239, "PLT.19.4.2"), "The following request attributes must be set: " +
      "javax.servlet.forward.request_uri, javax.servlet.forward.context_path, javax.servlet.forward.servlet_path, " +
      "javax.servlet.forward.path_info, javax.servlet.forward.query_string"),
   JSR286_240(new TCK(240, "PLT.19.4.2"), "The values of these attributes must be equal to the return values of the " +
      "HttpServletRequest methods getRequestURI, getContextPath, getServletPath, getPathInfo, getQueryString " +
      "respectively, invoked on the request object passed to the first servlet object in the forward call chain."),
   JSR286_241(new TCK(241, "PLT.19.4.2"), "These attributes are accessible from the forwarded servlet via the " +
      "getAttribute method on the request object. Note that these attributes must always reflect the information " +
      "in the target of the first forward servlet in the situation that multiple forwards and subsequent includes " +
      "are called."),
   JSR286_242(new TCK(242, "PLT.19.4.2"), "If the forwarded servlet was obtained by using the getNamedDispatcher" +
      " method, these attributes must not be set."),

   // PLT.19.4.3 Request and Response Objects for Forwarded Servlets/JSPs from within the Action and Event processing Methods
   JSR286_243(new TCK(243, "PLT.19.4.3"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getLocalAddress, getLocalName, getRealPath, and getRequestURL."),
   JSR286_244(new TCK(244, "PLT.19.4.3"), "The following methods of the HttpServletRequest must return ‘0’: " +
      "getRemotePort and getLocalPort."),
   JSR286_245(new TCK(245, "PLT.19.4.3"), "The following methods of the HttpServletRequest must return the path " +
      "and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, getPathTranslated, " +
      "getQueryString, getRequestURI and getServletPath."),
   JSR286_246(new TCK(246, "PLT.19.4.3"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType, getContextPath, " +
      "getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_247(new TCK(247, "PLT.19.4.3"), "The following methods of the HttpServletRequest must be equivalent to the " +
      "methods of the PortletRequest of similar name with the provision defined in PLT.18.1.1 Query Strings in Request " +
      "Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_248(new TCK(248, "PLT.19.4.3"), "In case of a forward from processAction, the following methods of the " +
      "HttpServletRequest must be based on the corresponding methods of the" +
      "ActionRequest: getCharacterEncoding, setCharacterEncoding, getContentType, getInputStream, getContentLength, " +
      "getMethod and getReader."),
   JSR286_249(new TCK(249, "PLT.19.4.3"), "In case of a forward from processEvent, the following methods of the " +
      "HttpServletRequest must do no operations and/or return null: getCharacterEncoding, setCharacterEncoding, " +
      "getContentType, getInputStream and getReader."),
   JSR286_250(new TCK(250, "PLT.19.4.3"), "The getContentLength method of the HttpServletRequest must return 0."),
   JSR286_251(new TCK(251, "PLT.19.4.3"), "The getMethod method of the HttpServletRequest must be based on the " +
      "corresponding method of the ActionRequest triggering this event."),
   JSR286_252(new TCK(252, "PLT.19.4.3"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_253(new TCK(253, "PLT.19.4.3"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_254(new TCK(254, "PLT.19.4.3"), "The getProtocol method of the HttpServletRequest must always return " +
      "‘HTTP/1.1’."),
   JSR286_255(new TCK(255, "PLT.19.4.3"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL, encodeRedirectUrl, getCharacterEncoding, getContentType, getLocale, and getBufferSize."),
   JSR286_256(new TCK(256, "PLT.19.4.3"), "The following methods of the HttpServletResponse must return an " +
      "outputstream / writer that ignores any output written to it: getOutputStream and getWriter."),
   JSR286_257(new TCK(257, "PLT.19.4.3"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the ActionResponse/EventResponse of similar name: encodeURL and encodeUrl."),
   JSR286_258(new TCK(258, "PLT.19.4.3"), Status.specUntestable("imp"), "The following methods of the HttpServletResponse must perform no " +
      "operations: resetBuffer, reset, setContentType, setContentLength," +
      "setCharacterEncoding, setLocale, sendError, setDateHeader, addDateHeader, setHeader, addHeader, setIntHeader, " +
      "addIntHeader, setStatus, setBufferSize and flushBuffer."),
   JSR286_259(new TCK(259, "PLT.19.4.3"), Status.specUntestable("impl"), "The addCookie method of the HttpServletResponse must be based on " +
      "addProperty method of the ActionResponse/EventResponse interface."),
   JSR286_260(new TCK(260, "PLT.19.4.3"), "The containsHeader method of the HttpServletResponse must return false."),
   JSR286_261(new TCK(261, "PLT.19.4.3"), "The isCommitted method of the HttpServletResponse must return false."),

   // PLT.19.4.4 Request and Response Objects for Forwarded Servlets/JSPs from within the Render Method
   JSR286_262(new TCK(262, "PLT.19.4.4"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getLocalAddress, getLocalName, getRealPath, and getRequestURL."),
   JSR286_263(new TCK(263, "PLT.19.4.4"), "The following methods of the HttpServletRequest must return ‘0’: " +
      "getRemotePort and getLocalPort."),
   JSR286_264(new TCK(264, "PLT.19.4.4"), "The following methods of the HttpServletRequest must return the " +
      "path and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, " +
      "getPathTranslated, getQueryString, getRequestURI and getServletPath."),
   JSR286_265(new TCK(265, "PLT.19.4.4"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType, " +
      "getContextPath, getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_266(new TCK(266, "PLT.19.4.4"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name with the provision defined in PLT.18.1.1 Query Strings " +
      "in Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_267(new TCK(267, "PLT.19.4.4"), "The following methods of the HttpServletRequest must do no operations " +
      "and return null: getCharacterEncoding, setCharacterEncoding, getContentType, getInputStream and getReader."),
   JSR286_268(new TCK(268, "PLT.19.4.4"), "The getContentLength method of the HttpServletRequest must return 0."),
   JSR286_269(new TCK(269, "PLT.19.4.4"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_270(new TCK(270, "PLT.19.4.4"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_271(new TCK(271, "PLT.19.4.4"), "The getMethod method of the HttpServletRequest must always return ‘GET’."),
   JSR286_272(new TCK(272, "PLT.19.4.4"), "The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’."),
   JSR286_273(new TCK(273, "PLT.19.4.4"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL and encodeRedirectUrl."),
   JSR286_274(new TCK(274, "PLT.19.4.4"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the RenderResponse of similar name: getCharacterEncoding, setBufferSize, flushBuffer, " +
      "resetBuffer, reset, getBufferSize, getLocale, isCommitted, getOutputStream, getWriter, setContentType, " +
      "encodeURL and encodeUrl."),
   JSR286_275(new TCK(275, "PLT.19.4.4"), Status.specUntestable("impl"), "The following methods of the HttpServletResponse must perform no " +
      "operations: setContentLength, setLocale, sendError, sendRedirect, and setStatus."),
   JSR286_276(new TCK(276, "PLT.19.4.4"), Status.specUntestable("impl"), "The containsHeader method of the HttpServletResponse must return false."),
   JSR286_277(new TCK(277, "PLT.19.4.4"), "The following methods of the HttpServletResponse must be based on " +
      "the properties provided by the setProperties/addProperties method of the RenderResponse interface: addCookie, " +
      "setDateHeader, addDateHeader, setHeader, addHeader, setIntHeader, addIntHeader."),

   // PLT.19.4.5 Request and Response Objects for Forwarded Servlets/JSPs from within the ServeResource Method
   JSR286_278(new TCK(278, "PLT.19.4.5"), "The following methods of the HttpServletRequest must return null: " +
      "getRemoteAddr, getRemoteHost, getLocalAddress, getLocalName, getRealPath, and getRequestURL."),
   JSR286_279(new TCK(279, "PLT.19.4.5"), "The following methods of the HttpServletRequest must return ‘0’: " +
      "getRemotePort and getLocalPort."),
   JSR286_280(new TCK(280, "PLT.19.4.5"), "The following methods of the HttpServletRequest must return the " +
      "path and query string information used to obtain the PortletRequestDispatcher object: getPathInfo, " +
      "getPathTranslated, getQueryString, getRequestURI and getServletPath."),
   JSR286_281(new TCK(281, "PLT.19.4.5"), "The following methods of the HttpServletRequest must be equivalent " +
      "to the methods of the PortletRequest of similar name: getScheme, getServerName, getServerPort, getAttribute, " +
      "getAttributeNames, setAttribute, removeAttribute, getLocale, getLocales, isSecure, getAuthType, " +
      "getContextPath, getRemoteUser, getUserPrincipal, getRequestedSessionId, isRequestedSessionIdValid, getCookies."),
   JSR286_282(new TCK(282, "PLT.19.4.5"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the ResourceRequest of similar name: getCharacterEncoding, setCharacterEncoding, getContentType, getMethod and getReader."),
   JSR286_283(new TCK(283, "PLT.19.4.5"), "The following methods of the HttpServletRequest must be equivalent to " +
      "the methods of the PortletRequest of similar name with the provision defined in PLT.18.1.1 Query Strings " +
      "in Request Dispatcher Paths Section: getParameter, getParameterNames, getParameterValues and getParameterMap."),
   JSR286_284(new TCK(284, "PLT.19.4.5"), "The following methods of the HttpServletRequest must be based on the " +
      "properties provided by the getProperties method of the PortletRequest interface: getHeader, getHeaders, " +
      "getHeaderNames, getDateHeader and getIntHeader."),
   JSR286_285(new TCK(285, "PLT.19.4.5"), "The following methods of the HttpServletRequest must provide the " +
      "functionality defined by the Servlet Specification: getRequestDispatcher, isUserInRole, getSession, " +
      "isRequestedSessionIdFromCookie, isRequestedSessionIdFromURL and isRequestedSessionIdFromUrl."),
   JSR286_286(new TCK(286, "PLT.19.4.5"), "The getProtocol method of the HttpServletRequest must always return ‘HTTP/1.1’."),
   JSR286_287(new TCK(287, "PLT.19.4.5"), "The following methods of the HttpServletResponse must return null: " +
      "encodeRedirectURL and encodeRedirectUrl."),
   JSR286_288(new TCK(288, "PLT.19.4.5"), "The following methods of the HttpServletResponse must be equivalent " +
      "to the methods of the ResourceResponse of similar name: getCharacterEncoding, setContentType, setBufferSize, " +
      "flushBuffer, resetBuffer, reset, getBufferSize, isCommitted, getOutputStream, getWriter, getLocale, encodeURL " +
      "and encodeUrl."),
   JSR286_289(new TCK(289, "PLT.19.4.5"), Status.specUntestable("impl"), "The following methods of the HttpServletResponse must perform no operations: " +
      "sendError, sendRedirect."),
   JSR286_290(new TCK(290, "PLT.19.4.5"), Status.specUntestable("impl"), "The containsHeader method of the HttpServletResponse must return false."),

   //
   JSR286_291(new TCK(291, "PLT.19.5"), "Since the Java Servlet Specification V2.4 you can specify servlet filters " +
      "for request dispatcher include calls. Portlet containers must support this capability for included servlets " +
      "via the PortletRequestDispatcher."),


   // PLT.20 Portlet Filter

   JSR286_292(new TCK(292, "PLT.20.2.1"), "After deployment of the portlet application, and before a request causes " +
      "the portlet container to access a portlet, the portlet container must locate the list of portlet filters that " +
      "must be applied to the portlet as described below."),
   JSR286_293(new TCK(293, "PLT.20.2.1"), "The portlet container must ensure that it has instantiated a filter of the " +
      "appropriate class for each filter in the list, and called its init(FilterConfig config) method"),
   JSR286_294(new TCK(294, "PLT.20.2.1"), "Depending on the target method of doFilter call the PortletRequest and " +
      "PortletResponse must be instances of the following interfaces: ActionRequest and ActionResponse for " +
      "processAction calls, EventRequest and EventResponse for processEvent calls, RenderRequest and RenderResponse " +
      "for render calls, ResourceRequest and ResourceResponse for serveResource calls"),
   JSR286_295(new TCK(295, "PLT.20.2.1"), "Before a filter instance can be removed from service by the portlet " +
      "container, the portlet container must first call the destroy method on the filter to enable the filter to " +
      "release any resources and perform other cleanup operations."),
   JSR286_296(new TCK(296, "PLT.20.2.2"), "When a filter invokes the doFilter method on the portlet container’s " +
      "filter chain implementation, the container must ensure that the request and " +
      "response object that it passes to the next component in the filter chain, or to the target portlet if the " +
      "filter was the last in the chain, is the same object that was passed into the doFilter method by the calling " +
      "filter or one of the above mentioned wrappers."),
   JSR286_297(new TCK(297, "PLT.20.2.4"), "The portlet container must instantiate exactly one instance of the Java " +
      "class defining the filter per filter declaration in the deployment descriptor."),
   JSR286_298(new TCK(298, "PLT.20.2.4"), "Filters can be associated with groups of portlets using the ‘*’ character as " +
      "a wildcard at the end of a string to indicate that the filter must be applied to any portlet whose name starts " +
      "with the characters before the “*” character."),
   JSR286_299(new TCK(299, "PLT.20.2.4"), "The portlet container is free to add additional filters at any place in this " +
      "filter chain, but must not remove filters matching a specific portlet."),
   JSR286_300(new TCK(300, "PLT.20.2.5"), "A portlet filter can be applied to different lifecycle method calls: " +
      "processAction, processEvent, render, serveResource"),
   JSR286_301(new TCK(301, "PLT.20.2.5"), "Thus the filter must define the lifecycle method for which the filter " +
      "is written in the <lifecycle> element of the <filter> element."),

   // PLT.21 User Information

   JSR286_302(new TCK(302, "PLT.21.2"), Status.specUntestable("impl"), "If the request is done in the context of " +
      "an un-authenticated user, calls to the getAttribute method of the request using the USER_INFO constant must " +
      "return null."),


   // PLT.22 Caching



   // PLT.23 Portlet Applications

   JSR286_303(new TCK(303, "PLT.23.2"), Status.specUntestable(""), "The portlet container must enforce a one to one " +
      "correspondence between a portlet application and a PortletContext."),
   JSR286_304(new TCK(304, "PLT.23.2"), Status.specUntestable(""), "If the application is a distributed application, " +
      "the portlet container must create an instance per VM."),
   JSR286_305(new TCK(305, "PLT.23.5"), Status.specUntestable(""), "The portlet container must use the same classloader " +
      "the servlet container uses for the web application resources for loading the portlets and related resources " +
      "within the portlet application."),
   JSR286_306(new TCK(306, "PLT.23.5(servlet spec)"), "The portlet container must ensure that requirements defined " +
      "in the Servlet Specification SRV.9.7.1 and SRV.9.7.2 Sections are fulfilled."),


   // PLT.24 Security


   JSR286_307(new TCK(307, "PLT.24.2"), Status.postponed("No security/auth tests for now"), "The values that the Portlet" +
      " API getRemoteUser and getUserPrincipal methods " +
      "return the same values returned by the equivalent methods of the servlet response object."),
   JSR286_308(new TCK(308, "PLT.24.3"), Status.postponed("No security/auth tests for now"), "The container uses the " +
      "mapping of security-role-ref to security-role when " +
      "determining the return value of the call."),
   JSR286_309(new TCK(309, "PLT.24.3"), Status.postponed("No security/auth tests for now"), "If the security-role-ref" +
      " element does not define a role-link element, the " +
      "container must default to checking the role-name element argument against the list of securityrole elements " +
      "defined in the web.xml deployment descriptor of the portlet application."),
   JSR286_310(new TCK(310, "PLT.24.4"), Status.postponed("No security/auth tests for now"), "When it is specified, the " +
      "container must propagate the security identity of " +
      "the caller to the EJB layer in terms of the security role name defined in the run-as element."),
   JSR286_311(new TCK(311, "PLT.24.4"), Status.postponed("No security/auth tests for now"), "The security role name must " +
      "be one of the security role names defined for the " +
      "web.xml deployment descriptor."),

   

   // PLT.25 Packaging and Deployment Descriptor

   // PLT.26 Portlet Tag Library


   JSR286_312(new TCK(312, "PLT.26.1"), Status.disabled("Every testcase test this"), "The portlet-container must provide " +
      "an implementation of the portlet tag library"),
   JSR286_313(new TCK(313, "PLT.26.1"), "The defineObjects tag must define the following variables in the JSP page: " +
      "[REF TO THE SPEC]"),
   JSR286_314(new TCK(314, "PLT.26.2"), Status.specUntestable(""), "The defineObjects tag must not define any " +
      "attribute and it must not contain any body content"),
   JSR286_315(new TCK(315, "PLT.26.2"), "The portlet actionURL tag creates a URL that must point to the current " +
      "portlet and must trigger an action request with the supplied parameters"),
   JSR286_316(new TCK(316, "PLT.26.2"), "If the specified window state is illegal for the current request, a" +
      " JspException must be thrown."),
   JSR286_317(new TCK(317, "PLT.26.2"), "If a window state is notset for a URL, it should stay the same as the " +
      "window state of the current request."),
   JSR286_318(new TCK(318, "PLT.26.2"), "portletMode (Type: String, non-required) – indicates the portlet mode " +
      "that the portlet must have when this link is executed, if no error condition ocurred"),
   JSR286_319(new TCK(319, "PLT.26.2"), "If the specified portlet mode is illegal for the current request, a " +
      "JspException must be thrown."),
   JSR286_320(new TCK(320, "PLT.26.2"), "If a portlet mode is not set for a URL, it must stay the same as the mode of " +
      "the current request."),
   JSR286_321(new TCK(321, "PLT.26.2"), "If the result is exported as a JSP scoped variable, defined via the var " +
      "attributes, nothing is written to the current JspWriter."),
   JSR286_322(new TCK(322, "PLT.26.2"), "If the given variable name already exists in the scope of the page or it " +
      "is used within an iteration loop, the new value overwrites the old one."),
   JSR286_323(new TCK(323, "PLT.26.2"), Status.specUntestable(""), "If the specified security setting is not supported " +
      "by the run-time environment, a JspException must be thrown"),
   JSR286_324(new TCK(324, "PLT.26.2"), Status.specUntestable(""), "copyCurrentRenderParameters (Type: boolean, " +
      "non-required) – if set to true requests that the private render parameters of the portlet of the current " +
      "request must be attached to this URL."),
   JSR286_325(new TCK(325, "PLT.26.2"), Status.specUntestable(""), "If additional <portlet:param> tags are specified " +
      "parameters with the same name as an existing render parameter will get merged and the value defined in " +
      "additional <portlet:param> tags must be pre-pended."),
   JSR286_326(new TCK(326, "PLT.26.2"), Status.specUntestable(""), "escapeXml (Type: boolean, non-required) – " +
      "determines whether characters <,>,&,’,” in the resulting output should be converted to their corresponding " +
      "character entity codes (‘<’ gets converted to ‘&lt;’, ‘>’ gets converted to ‘&gt;’ ‘&’ gets converted to " +
      "‘&amp;’, ‘‘’ gets converted to ‘&#039;’, ‘”’ gets converted to ‘&#034;’)."),
   JSR286_327(new TCK(327, "PLT.26.3"), "The portlet renderURL tag creates a URL that must point to the current " +
      "portlet and must trigger a render request with the supplied parameters."),
   JSR286_328(new TCK(328, "PLT.26.3"), "If the specified window state is illegal for the current request, a " +
      "JspException must be thrown."),
   JSR286_329(new TCK(329, "PLT.26.3"), "If a window state is not set for a URL, it should stay the same as the " +
      "window state of the current request."),
   JSR286_330(new TCK(330, "PLT.26.3"), "portletMode (Type: String, non-required) – indicates the portlet mode that " +
      "the portlet must have when this link is executed, if not error condition ocurred."),
   JSR286_331(new TCK(331, "PLT.26.3"), "If the specified portlet mode is illegal for the current request, a " +
      "JspException must be thrown."),
   JSR286_332(new TCK(332, "PLT.26.3"), "If a portlet mode is not set for a URL, it must stay the same as the " +
      "mode of the current request."),
   JSR286_333(new TCK(333, "PLT.26.3"), "If the result is exported as a JSP scoped variable, defined via the var " +
      "attributes, nothing is written to the current JspWriter."),
   JSR286_334(new TCK(334, "PLT.26.3"), "If the given variable name already exists in the scope of the page or it " +
      "is used within an iteration loop, the new value overwrites the old one"),
   JSR286_335(new TCK(335, "PLT.26.3"), Status.specUntestable(""), "If the security is not set for a URL, it must " +
      "stay the same as the security setting of the current request."),
   JSR286_336(new TCK(336, "PLT.26.3"), Status.specUntestable(""), "copyCurrentRenderParameters (Type: boolean, " +
      "non-required) – if set to true requests that the private render parameters of the portlet of the current " +
      "request must attached to this URL."),
   JSR286_337(new TCK(337, "PLT.26.3"), Status.specUntestable(""), "If additional <portlet:param> tags are specified " +
      "parameters with the same name as an existing render parameter will get merged and the value defined in " +
      "additional <portlet:param> tags must be pre-pended."),
   JSR286_338(new TCK(338, "PLT.26.3"), Status.specUntestable(""), "escapeXml (Type: boolean, non-required) – " +
      "deterrmines whether characters <,>,&,’,” in the resulting output should be converted to their corresponding " +
      "character entity codes (‘<’ gets converted to ‘&lt;’, ‘>’ gets converted to ‘&gt;’ ‘&’ gets converted to " +
      "‘&amp;’, ‘‘’ gets converted to ‘&#039;’, ‘”’ gets converted to ‘&#034;’)."),
   JSR286_339(new TCK(339, "PLT.26.4"), "The portlet resourceURL tag creates a URL that must point to the current " +
      "portlet and must trigger a serveResource request with the supplied parameters"),
   JSR286_340(new TCK(340, "PLT.26.4"), "The resourceURL must preserve the current portlet mode, window state and " +
      "render parameters."),
   JSR286_341(new TCK(341, "PLT.26.4"), "If such a parameter has the same name as a render parameter in this URL, " +
      "the render parameter value must be the last value in the attribute value array."),
   JSR286_342(new TCK(342, "PLT.26.4"), "If the result is exported as a JSP scoped variable, defined via the var " +
      "attributes, nothing is written to the current JspWriter"),
   JSR286_343(new TCK(343, "PLT.26.4"), "If the given variable name already exists in the scope of the page or it " +
      "is used within an iteration loop, the new value overwrites the old one."),
   JSR286_344(new TCK(344, "PLT.26.4"), Status.specUntestable(""), "If the security is not set for a URL, it must " +
      "stay the same as the security setting of the current request."),
   JSR286_345(new TCK(345, "PLT.26.4"), Status.specUntestable(""), "escapeXml (Type: boolean, non-required) – " +
      "determines whether characters <,>,&,’,” in the resulting output should be converted to their corresponding " +
      "character entity codes (‘<’ gets converted to ‘&lt;’, ‘>’ gets converted to ‘&gt;’ ‘&’ gets converted to " +
      "‘&amp;’, ‘‘’ gets converted to ‘&#039;’, ‘”’ gets converted to ‘&#034;’)."),
   JSR286_346(new TCK(346, "PLT.26.5"), "This tag produces a unique value for the current portlet and must match " +
      "the value of PortletResponse.getNamespace method."),
   JSR286_347(new TCK(347, "PLT.26.6"), "This tag defines a parameter that may be added to an actionURL, renderURL " +
      "or resourceURL."),
   JSR286_348(new TCK(348, "PLT.26.6"), Status.specUntestable(""), "The param tag must not contain any body content."),
   JSR286_349(new TCK(349, "PLT.26.6"), "If the param tag has an empty value the specified parameter name must be " +
      "removed from the URL."),
   JSR286_350(new TCK(350, "PLT.26.6"), "If the same name of a parameter occurs more than once within an actionURL, " +
      "renderURL or resourceURL the values must be delivered as parameter value array with the values in the order " +
      "of the declaration within the URL tag."),

   // ******************************************************************************************************************


   API286_ACTION_REQUEST_1(new API(ActionRequest.class), "ACTION_NAME"),

   API286_ACTION_RESPONSE_1(new API(ActionResponse.class), "sendRedirect()"),

   API286_BASE_URL_1(new API(BaseURL.class), "setParameter()"),
   API286_BASE_URL_2(new API(BaseURL.class), "setSecure()"),
   API286_BASE_URL_3(new API(BaseURL.class), "toString()"),
   API286_BASE_URL_4(new API(BaseURL.class), "getParameterMap()"),
   API286_BASE_URL_5(new API(BaseURL.class), "write()"),
   API286_BASE_URL_6(new API(BaseURL.class), "addProperty()"),
   API286_BASE_URL_7(new API(BaseURL.class), "setProperty()"),
   API286_BASE_URL_8(new API(BaseURL.class), "setParameters()"),

   API286_CACHE_CONTROL_1(new API(CacheControl.class), "getETag()"),
   API286_CACHE_CONTROL_2(new API(CacheControl.class), "getExpiriationTime()"),
   API286_CACHE_CONTROL_3(new API(CacheControl.class), "isPublicScope()"),
   API286_CACHE_CONTROL_4(new API(CacheControl.class), "setETag()"),
   API286_CACHE_CONTROL_5(new API(CacheControl.class), "setExpiriationTime()"),
   API286_CACHE_CONTROL_6(new API(CacheControl.class), "setPublicScope()"),
   API286_CACHE_CONTROL_7(new API(CacheControl.class), "setUseCachedContent()"),
   API286_CACHE_CONTROL_8(new API(CacheControl.class), "useCachedContent()"),
   
   API286_CLIENT_DATA_REQUEST_1(new API(ClientDataRequest.class), "getCharacterEncoding()"),
   API286_CLIENT_DATA_REQUEST_2(new API(ClientDataRequest.class), "getContentLength()"),
   API286_CLIENT_DATA_REQUEST_3(new API(ClientDataRequest.class), "getContentType()"),
   API286_CLIENT_DATA_REQUEST_4(new API(ClientDataRequest.class), "getMethod()"),
   API286_CLIENT_DATA_REQUEST_5(new API(ClientDataRequest.class), "getPortletInputStream()"),
   API286_CLIENT_DATA_REQUEST_6(new API(ClientDataRequest.class), "getReader()"),
   API286_CLIENT_DATA_REQUEST_7(new API(ClientDataRequest.class), "setCharacterEncoding()"),
   
   API286_EVENT_1(new API(Event.class), "getName()"),
   API286_EVENT_2(new API(Event.class), "getQName()"),
   API286_EVENT_3(new API(Event.class), "getValue()"),

   API286_EVENT_PORTLET_1(new API(EventPortlet.class), "processEvent()"),

   API286_EVENT_REQUEST_1(new API(EventRequest.class), "getEvent()"),
   API286_EVENT_REQUEST_2(new API(EventRequest.class), "getMethod()"),

   API286_EVENT_RESPONSE_1(new API(EventResponse.class), "setRenderParameters()"),

   API286_GENERIC_PORTLET_1(new API(GenericPortlet.class), "destroy()"),
   API286_GENERIC_PORTLET_2(new API(GenericPortlet.class), "doDispatch()"),
   API286_GENERIC_PORTLET_3(new API(GenericPortlet.class), "doEdit()"),
   API286_GENERIC_PORTLET_4(new API(GenericPortlet.class), "doHeaders()"),
   API286_GENERIC_PORTLET_5(new API(GenericPortlet.class), "doHelp()"),
   API286_GENERIC_PORTLET_6(new API(GenericPortlet.class), "doView()"),
   API286_GENERIC_PORTLET_7(new API(GenericPortlet.class), "getContainerRuntimeOptions()"),
   API286_GENERIC_PORTLET_8(new API(GenericPortlet.class), "getDefaultNamespace()"),
   API286_GENERIC_PORTLET_9(new API(GenericPortlet.class), "getInitParameter()"),
   API286_GENERIC_PORTLET_10(new API(GenericPortlet.class), "getInitParameterNames()"),
   API286_GENERIC_PORTLET_11(new API(GenericPortlet.class), "getNextPossiblePortletModes()"),
   API286_GENERIC_PORTLET_12(new API(GenericPortlet.class), "getPortletConfig()"),
   API286_GENERIC_PORTLET_13(new API(GenericPortlet.class), "getPortletContext()"),
   API286_GENERIC_PORTLET_14(new API(GenericPortlet.class), "getPortletName()"),
   API286_GENERIC_PORTLET_15(new API(GenericPortlet.class), "getProcessingEventQNames()"),
   API286_GENERIC_PORTLET_16(new API(GenericPortlet.class), "getPublicRenderParameterNames()"),
   API286_GENERIC_PORTLET_17(new API(GenericPortlet.class), "getPublishingEventQNames()"),
   API286_GENERIC_PORTLET_18(new API(GenericPortlet.class), "getResourceBundle()"),
   API286_GENERIC_PORTLET_19(new API(GenericPortlet.class), "getSupportedLocales()"),
   API286_GENERIC_PORTLET_20(new API(GenericPortlet.class), "getTitle()"),
   API286_GENERIC_PORTLET_21(new API(GenericPortlet.class), "init()"),
   API286_GENERIC_PORTLET_22(new API(GenericPortlet.class), "processAction()"),
   API286_GENERIC_PORTLET_23(new API(GenericPortlet.class), "processEvent()"),
   API286_GENERIC_PORTLET_24(new API(GenericPortlet.class), "render()"),
   API286_GENERIC_PORTLET_25(new API(GenericPortlet.class), "serveResource()"),

   API286_MIME_RESPONSE_1(new API(MimeResponse.class), "CACHE_SCOPE"),
   API286_MIME_RESPONSE_2(new API(MimeResponse.class), "ETAG"),
   API286_MIME_RESPONSE_3(new API(MimeResponse.class), "EXPIRIATION_CACHE"),
   API286_MIME_RESPONSE_4(new API(MimeResponse.class), "MARKUP_HEAD_ELEMENT"),
   API286_MIME_RESPONSE_5(new API(MimeResponse.class), "NAMESPACED_RESPONSE"),
   API286_MIME_RESPONSE_6(new API(MimeResponse.class), "PRIVATE_SCOPE"),
   API286_MIME_RESPONSE_7(new API(MimeResponse.class), "PUBLIC_SCOPE"),
   API286_MIME_RESPONSE_8(new API(MimeResponse.class), "USE_CACHED_CONTENT"),
   API286_MIME_RESPONSE_9(new API(MimeResponse.class), "createActionURL()"),
   API286_MIME_RESPONSE_10(new API(MimeResponse.class), "createRenderURL()"),
   API286_MIME_RESPONSE_11(new API(MimeResponse.class), "flushBuffer()"),
   API286_MIME_RESPONSE_12(new API(MimeResponse.class), "getBufferSize()"),
   API286_MIME_RESPONSE_13(new API(MimeResponse.class), "getCacheControl()"),
   API286_MIME_RESPONSE_14(new API(MimeResponse.class), "getCharacterEncoding()"),
   API286_MIME_RESPONSE_15(new API(MimeResponse.class), "getContentType()"),
   API286_MIME_RESPONSE_16(new API(MimeResponse.class), "getLocale()"),
   API286_MIME_RESPONSE_17(new API(MimeResponse.class), "getPortletOutputStream()"),
   API286_MIME_RESPONSE_18(new API(MimeResponse.class), "getWriter()"),
   API286_MIME_RESPONSE_19(new API(MimeResponse.class), "isCommited()"),
   API286_MIME_RESPONSE_20(new API(MimeResponse.class), "reset()"),
   API286_MIME_RESPONSE_21(new API(MimeResponse.class), "resetBuffer()"),
   API286_MIME_RESPONSE_22(new API(MimeResponse.class), "setBufferSize()"),
   API286_MIME_RESPONSE_23(new API(MimeResponse.class), "setContentType()"),


   API286_PORTAL_CONTEXT_1(new API(PortalContext.class), "MARKUP_HEAD_ELEMENT_SUPPORT"),
   API286_PORTAL_CONTEXT_2(new API(PortalContext.class), "getPortalInfo()"),
   API286_PORTAL_CONTEXT_3(new API(PortalContext.class), "getProperty()"),
   API286_PORTAL_CONTEXT_4(new API(PortalContext.class), "getPropertyNames()"),
   API286_PORTAL_CONTEXT_5(new API(PortalContext.class), "getSupportedPortletModes()"),
   API286_PORTAL_CONTEXT_6(new API(PortalContext.class), "getSupportedWindowStates()"),

   API286_PORTLET_CONTEXT_1(new API(PortalContext.class), "getAttribute()"),
   API286_PORTLET_CONTEXT_2(new API(PortalContext.class), "getAttributeNames()"),
   API286_PORTLET_CONTEXT_3(new API(PortalContext.class), "getContainerRuntimeOptions()"),
   API286_PORTLET_CONTEXT_4(new API(PortalContext.class), "getInitParameter()"),
   API286_PORTLET_CONTEXT_5(new API(PortalContext.class), "getInitParameterNames()"),
   API286_PORTLET_CONTEXT_6(new API(PortalContext.class), "getMajorVersion()"),
   API286_PORTLET_CONTEXT_7(new API(PortalContext.class), "getMimeType()"),
   API286_PORTLET_CONTEXT_8(new API(PortalContext.class), "getMinorVersion()"),
   API286_PORTLET_CONTEXT_9(new API(PortalContext.class), "getNamedDispatcher()"),
   API286_PORTLET_CONTEXT_10(new API(PortalContext.class), "getPortletContextName()"),
   API286_PORTLET_CONTEXT_11(new API(PortalContext.class), "getRealPath()"),
   API286_PORTLET_CONTEXT_12(new API(PortalContext.class), "getRequestDispatcher()"),
   API286_PORTLET_CONTEXT_13(new API(PortalContext.class), "getResource()"),
   API286_PORTLET_CONTEXT_14(new API(PortalContext.class), "getResourceAsStream()"),
   API286_PORTLET_CONTEXT_15(new API(PortalContext.class), "getResourcePaths()"),
   API286_PORTLET_CONTEXT_16(new API(PortalContext.class), "getServerInfo()"),
   API286_PORTLET_CONTEXT_17(new API(PortalContext.class), "removeAttribute()"),
   API286_PORTLET_CONTEXT_18(new API(PortalContext.class), "setAttribute()"),

   API286_PORTLET_1(new API(Portlet.class), "destroy()"),
   API286_PORTLET_2(new API(Portlet.class), "init()"),
   API286_PORTLET_3(new API(Portlet.class), "processAction()"),
   API286_PORTLET_4(new API(Portlet.class), "render()"),

   API286_PORTLET_CONFIG_1(new API(PortletConfig.class), "getContainerRuntimeOptions()"),
   API286_PORTLET_CONFIG_2(new API(PortletConfig.class), "getDefaultNamespace()"),
   API286_PORTLET_CONFIG_3(new API(PortletConfig.class), "getInitParameter()"),
   API286_PORTLET_CONFIG_4(new API(PortletConfig.class), "getInitParameterNames()"),
   API286_PORTLET_CONFIG_5(new API(PortletConfig.class), "getPortletContext()"),
   API286_PORTLET_CONFIG_6(new API(PortletConfig.class), "getPortletName()"),
   API286_PORTLET_CONFIG_7(new API(PortletConfig.class), "getProcessingEventQNames()"),
   API286_PORTLET_CONFIG_8(new API(PortletConfig.class), "getPublicRenderParameterNames()"),
   API286_PORTLET_CONFIG_9(new API(PortletConfig.class), "getPublishingEventQNames()"),
   API286_PORTLET_CONFIG_10(new API(PortletConfig.class), "getResourceBundle()"),
   API286_PORTLET_CONFIG_11(new API(PortletConfig.class), "getSupportedLocales()"),

   API286_PORTLET_MODE_1(new API(PortletMode.class), "EDIT"),
   API286_PORTLET_MODE_2(new API(PortletMode.class), "HELP"),
   API286_PORTLET_MODE_3(new API(PortletMode.class), "VIEW"),
   API286_PORTLET_MODE_4(new API(PortletMode.class), "equals()"),
   API286_PORTLET_MODE_5(new API(PortletMode.class), "hashCode()"),
   API286_PORTLET_MODE_6(new API(PortletMode.class), "toString()"),
   API286_PORTLET_MODE_7(new API(PortletMode.class), "PortletMode()"),

   API286_PORTLET_PREFERENCES_1(new API(PortletPreferences.class), "getMap()"),
   API286_PORTLET_PREFERENCES_2(new API(PortletPreferences.class), "getNames()"),
   API286_PORTLET_PREFERENCES_3(new API(PortletPreferences.class), "getValue()"),
   API286_PORTLET_PREFERENCES_4(new API(PortletPreferences.class), "getValues()"),
   API286_PORTLET_PREFERENCES_5(new API(PortletPreferences.class), "isReadOnly()"),
   API286_PORTLET_PREFERENCES_6(new API(PortletPreferences.class), "reset()"),
   API286_PORTLET_PREFERENCES_7(new API(PortletPreferences.class), "setValue()"),
   API286_PORTLET_PREFERENCES_8(new API(PortletPreferences.class), "setValues()"),
   API286_PORTLET_PREFERENCES_9(new API(PortletPreferences.class), "store()"),

   API286_PORTLET_REQUEST_1(new API(PortletRequest.class), "ACTION_PHASE"),
   API286_PORTLET_REQUEST_2(new API(PortletRequest.class), "ACTION_SCOPE_ID"),
   API286_PORTLET_REQUEST_3(new API(PortletRequest.class), "BASIC_AUTH"),
   API286_PORTLET_REQUEST_4(new API(PortletRequest.class), "CCPP_PROFILE"),
   API286_PORTLET_REQUEST_5(new API(PortletRequest.class), "CLIENT_CERT_AUTH"),
   API286_PORTLET_REQUEST_6(new API(PortletRequest.class), "DIGEST_AUTH"),
   API286_PORTLET_REQUEST_7(new API(PortletRequest.class), "EVENT_PHASE"),
   API286_PORTLET_REQUEST_8(new API(PortletRequest.class), "FORM_AUTH"),
   API286_PORTLET_REQUEST_9(new API(PortletRequest.class), "LIFECYCLE_PHASE"),
   API286_PORTLET_REQUEST_10(new API(PortletRequest.class), "RENDER_HEADERS"),
   API286_PORTLET_REQUEST_11(new API(PortletRequest.class), "RENDER_MARKUP"),
   API286_PORTLET_REQUEST_12(new API(PortletRequest.class), "RENDER_PART"),
   API286_PORTLET_REQUEST_13(new API(PortletRequest.class), "RENDER_PHASE"),
   API286_PORTLET_REQUEST_14(new API(PortletRequest.class), "RESOURCE_PHASE"),
   API286_PORTLET_REQUEST_15(new API(PortletRequest.class), "USER_INFO"),
   API286_PORTLET_REQUEST_16(new API(PortletRequest.class), "getAttribute()"),
   API286_PORTLET_REQUEST_17(new API(PortletRequest.class), "getAttributeNames()"),
   API286_PORTLET_REQUEST_18(new API(PortletRequest.class), "getAuthType()"),
   API286_PORTLET_REQUEST_19(new API(PortletRequest.class), "getContextPath()"),
   API286_PORTLET_REQUEST_20(new API(PortletRequest.class), "getCookies()"),
   API286_PORTLET_REQUEST_21(new API(PortletRequest.class), "getLocale()"),
   API286_PORTLET_REQUEST_22(new API(PortletRequest.class), "getLocales()"),
   API286_PORTLET_REQUEST_23(new API(PortletRequest.class), "getParameter()"),
   API286_PORTLET_REQUEST_24(new API(PortletRequest.class), "getParameterMap()"),
   API286_PORTLET_REQUEST_25(new API(PortletRequest.class), "getParameterNames()"),
   API286_PORTLET_REQUEST_26(new API(PortletRequest.class), "getParameterValues()"),
   API286_PORTLET_REQUEST_27(new API(PortletRequest.class), "getPortalContext()"),
   API286_PORTLET_REQUEST_28(new API(PortletRequest.class), "getPortletMode()"),
   API286_PORTLET_REQUEST_29(new API(PortletRequest.class), "getPortletSession()"),
   API286_PORTLET_REQUEST_30(new API(PortletRequest.class), "getPreferences()"),
   API286_PORTLET_REQUEST_31(new API(PortletRequest.class), "getPrivateParameterMap()"),
   API286_PORTLET_REQUEST_32(new API(PortletRequest.class), "getProperties()"),
   API286_PORTLET_REQUEST_33(new API(PortletRequest.class), "getProperty()"),
   API286_PORTLET_REQUEST_34(new API(PortletRequest.class), "getPropertyNames()"),
   API286_PORTLET_REQUEST_35(new API(PortletRequest.class), "getPublicParameterMap()"),
   API286_PORTLET_REQUEST_36(new API(PortletRequest.class), "getRemoteUser()"),
   API286_PORTLET_REQUEST_37(new API(PortletRequest.class), "getRequestedSessionId()"),
   API286_PORTLET_REQUEST_38(new API(PortletRequest.class), "getResponseContentType()"),
   API286_PORTLET_REQUEST_39(new API(PortletRequest.class), "getResponseContentTypes()"),
   API286_PORTLET_REQUEST_40(new API(PortletRequest.class), "getScheme()"),
   API286_PORTLET_REQUEST_41(new API(PortletRequest.class), "getServerName()"),
   API286_PORTLET_REQUEST_42(new API(PortletRequest.class), "getServerPort()"),
   API286_PORTLET_REQUEST_43(new API(PortletRequest.class), "getUserPrincipal()"),
   API286_PORTLET_REQUEST_44(new API(PortletRequest.class), "getWindowID()"),
   API286_PORTLET_REQUEST_45(new API(PortletRequest.class), "getWindowState()"),
   API286_PORTLET_REQUEST_46(new API(PortletRequest.class), "isPortletModeAllowed()"),
   API286_PORTLET_REQUEST_47(new API(PortletRequest.class), "isRequestedSessionIdValid()"),
   API286_PORTLET_REQUEST_48(new API(PortletRequest.class), "isSecure()"),
   API286_PORTLET_REQUEST_49(new API(PortletRequest.class), "isUserInRole()"),
   API286_PORTLET_REQUEST_50(new API(PortletRequest.class), "isWindowStateAllowed()"),
   API286_PORTLET_REQUEST_51(new API(PortletRequest.class), "removeAttribute()"),
   API286_PORTLET_REQUEST_52(new API(PortletRequest.class), "setAttribute"),
   
   API286_PORTLET_REQUEST_DISPATCHER_1(new API(PortletRequestDispatcher.class), "forward()"),
   API286_PORTLET_REQUEST_DISPATCHER_2(new API(PortletRequestDispatcher.class), "include()"),
   
   API286_PORTLET_RESPONSE_1(new API(PortletResponse.class), "addProperty()"),
   API286_PORTLET_RESPONSE_2(new API(PortletResponse.class), "createElement()"),
   API286_PORTLET_RESPONSE_3(new API(PortletResponse.class), "encodeURL()"),
   API286_PORTLET_RESPONSE_4(new API(PortletResponse.class), "getNamespace()"),
   API286_PORTLET_RESPONSE_5(new API(PortletResponse.class), "setProperty()"),
   
   API286_PORTLET_SESSION_1(new API(PortletSession.class), "APPLICATION_SCOPE"),
   API286_PORTLET_SESSION_2(new API(PortletSession.class), "PORTLET_SCOPE"),
   API286_PORTLET_SESSION_3(new API(PortletSession.class), "getAttribute()"),
   API286_PORTLET_SESSION_4(new API(PortletSession.class), "getAttributeMap()"),
   API286_PORTLET_SESSION_5(new API(PortletSession.class), "getAttributeNames()"),
   API286_PORTLET_SESSION_6(new API(PortletSession.class), "getCreationTime()"),
   API286_PORTLET_SESSION_7(new API(PortletSession.class), "getId()"),
   API286_PORTLET_SESSION_8(new API(PortletSession.class), "getLastAccessedTime()"),
   API286_PORTLET_SESSION_9(new API(PortletSession.class), "getMaxInactiveInterval()"),
   API286_PORTLET_SESSION_10(new API(PortletSession.class), "getPortletContext()"),
   API286_PORTLET_SESSION_11(new API(PortletSession.class), "invalidate()"),
   API286_PORTLET_SESSION_12(new API(PortletSession.class), "isNew()"),
   API286_PORTLET_SESSION_13(new API(PortletSession.class), "removeAttribute()"),
   API286_PORTLET_SESSION_14(new API(PortletSession.class), "setAttribute()"),
   API286_PORTLET_SESSION_15(new API(PortletSession.class), "setMaxInactiveInterval()"),
   
   API286_PORTLET_SESSION_UTIL_1(new API(PortletSessionUtil.class), "decodeAttributeName()"),
   API286_PORTLET_SESSION_UTIL_2(new API(PortletSessionUtil.class), "decodeScope()"),

   API286_PORTLET_URL_1(new API(PortletURL.class), "getPortletMode()"),
   API286_PORTLET_URL_2(new API(PortletURL.class), "getWindowState()"),
   API286_PORTLET_URL_3(new API(PortletURL.class), "removePublicRenderParameter()"),
   API286_PORTLET_URL_4(new API(PortletURL.class), "setPortletMode()"),
   API286_PORTLET_URL_5(new API(PortletURL.class), "setWindowState()"),

   API286_PORTLET_URL_GENERATION_LISTENER_1(new API(PortletURLGenerationListener.class), "filterActionURL()"),
   API286_PORTLET_URL_GENERATION_LISTENER_2(new API(PortletURLGenerationListener.class), "filterRenderURL()"),
   API286_PORTLET_URL_GENERATION_LISTENER_3(new API(PortletURLGenerationListener.class), "filterResourceURL()"),

   API286_PREFERENCES_VALIDATOR_1(new API(PreferencesValidator.class), "validate()"),

   API286_PROCESS_ACTION_1(new API(ProcessAction.class), "name"),

   API286_PROCESS_EVENT_1(new API(ProcessEvent.class), "name"),
   API286_PROCESS_EVENT_2(new API(ProcessEvent.class), "qname"),

   //TODO:
   //API286_P3P_USER_INFOS(new API(PortletRequest.P3PUserInfos.class), ""),

   API286_RENDER_MODE_1(new API(RenderMode.class), "name"),

   API286_RENDER_REQUEST_1(new API(RenderRequest.class), "ETAG"),

   API286_RENDER_RESPONSE_1(new API(RenderResponse.class), "setContentType()"),
   API286_RENDER_RESPONSE_2(new API(RenderResponse.class), "setNextPossiblePortletModes()"),
   API286_RENDER_RESPONSE_3(new API(RenderResponse.class), "setTitle()"),

   API286_RESOURCE_REQUEST_1(new API(ResourceRequest.class), "getCacheability()"),
   API286_RESOURCE_REQUEST_2(new API(ResourceRequest.class), "getETag()"),
   API286_RESOURCE_REQUEST_3(new API(ResourceRequest.class), "getPrivateRenderParameterMap()"),
   API286_RESOURCE_REQUEST_4(new API(ResourceRequest.class), "getResourceID()"),
   API286_RESOURCE_REQUEST_5(new API(ResourceRequest.class), "getResponseContentType()"),
   API286_RESOURCE_REQUEST_6(new API(ResourceRequest.class), "getResponseContentTypes()"),

   API286_RESOURCE_RESPONSE_1(new API(ResourceResponse.class), "setContentType()"),
   API286_RESOURCE_RESPONSE_2(new API(ResourceResponse.class), "setNExtPossiblePortletModes()"),
   API286_RESOURCE_RESPONSE_3(new API(ResourceResponse.class), "setTitle()"),

   API286_RESOURCE_SERVING_PORTLET_1(new API(ResourceServingPortlet.class), "serveResource()"),

   API286_RESOURCE_URL_1(new API(ResourceURL.class), "FULL"),
   API286_RESOURCE_URL_2(new API(ResourceURL.class), "PAGE"),
   API286_RESOURCE_URL_3(new API(ResourceURL.class), "PORTLET"),
   API286_RESOURCE_URL_4(new API(ResourceURL.class), "SHARED"),
   API286_RESOURCE_URL_5(new API(ResourceURL.class), "getCacheability()"),
   API286_RESOURCE_URL_6(new API(ResourceURL.class), "setCacheability()"),
   API286_RESOURCE_URL_7(new API(ResourceURL.class), "setResourceID()"),

   API286_STATE_AWARE_RESPONSE_1(new API(StateAwareResponse.class), "getPortletMode()"),
   API286_STATE_AWARE_RESPONSE_2(new API(StateAwareResponse.class), "getRenderParameterMap()"),
   API286_STATE_AWARE_RESPONSE_3(new API(StateAwareResponse.class), "getWindowState()"),
   API286_STATE_AWARE_RESPONSE_4(new API(StateAwareResponse.class), "removePublicRenderParameter()"),
   API286_STATE_AWARE_RESPONSE_5(new API(StateAwareResponse.class), "setEvent()"),
   API286_STATE_AWARE_RESPONSE_6(new API(StateAwareResponse.class), "setPortletMode()"),
   API286_STATE_AWARE_RESPONSE_7(new API(StateAwareResponse.class), "setRenderParameter()"),
   API286_STATE_AWARE_RESPONSE_8(new API(StateAwareResponse.class), "setRenderParameters()"),
   API286_STATE_AWARE_RESPONSE_9(new API(StateAwareResponse.class), "setWindowState()"),
   
   API286_WINDOW_STATE_1(new API(WindowState.class), "WindowState()"),
   API286_WINDOW_STATE_2(new API(WindowState.class), "MAXIMIZED"),
   API286_WINDOW_STATE_3(new API(WindowState.class), "MINIMIZED"),
   API286_WINDOW_STATE_4(new API(WindowState.class), "NORMAL"),
   API286_WINDOW_STATE_5(new API(WindowState.class), "equals()"),
   API286_WINDOW_STATE_6(new API(WindowState.class), "hashCode()"),
   API286_WINDOW_STATE_7(new API(WindowState.class), "toString()"),


   // Portlet Filter

   API286_ACTION_FILTER_1(new API(ActionFilter.class), "doFilter()"),

   //API286_ACTION_REQUEST_WRAPPER(new API(ActionRequestWrapper.class), ""),

   API286_ACTION_RESPONSE_WRAPPER_1(new API(ActionResponseWrapper.class), "getPortletMode()"),
   API286_ACTION_RESPONSE_WRAPPER_2(new API(ActionResponseWrapper.class), "getRenderParameterMap()"),
   API286_ACTION_RESPONSE_WRAPPER_3(new API(ActionResponseWrapper.class), "getResponse()"),
   API286_ACTION_RESPONSE_WRAPPER_4(new API(ActionResponseWrapper.class), "getWindowState()"),
   API286_ACTION_RESPONSE_WRAPPER_5(new API(ActionResponseWrapper.class), "removePublicRenderParameter()"),
   API286_ACTION_RESPONSE_WRAPPER_6(new API(ActionResponseWrapper.class), "sendRedirect()"),
   API286_ACTION_RESPONSE_WRAPPER_7(new API(ActionResponseWrapper.class), "setEvent()"),
   API286_ACTION_RESPONSE_WRAPPER_8(new API(ActionResponseWrapper.class), "setPortletMode()"),
   API286_ACTION_RESPONSE_WRAPPER_9(new API(ActionResponseWrapper.class), "setRenderParameter()"),
   API286_ACTION_RESPONSE_WRAPPER_10(new API(ActionResponseWrapper.class), "setRenderParameters()"),
   API286_ACTION_RESPONSE_WRAPPER_11(new API(ActionResponseWrapper.class), "setResponse()"),
   API286_ACTION_RESPONSE_WRAPPER_12(new API(ActionResponseWrapper.class), "setWindowState()"),

   API286_EVENT_FILTER_1(new API(EventFilter.class), "doFilter()"),

   API286_EVENT_REQUEST_WRAPPER_1(new API(EventRequestWrapper.class), "getEvent()"),
   API286_EVENT_REQUEST_WRAPPER_2(new API(EventRequestWrapper.class), "getMethod()"),
   API286_EVENT_REQUEST_WRAPPER_3(new API(EventRequestWrapper.class), "getRequest()"),
   API286_EVENT_REQUEST_WRAPPER_4(new API(EventRequestWrapper.class), "setRequest()"),

   API286_EVENT_RESPONSE_WRAPPER_1(new API(EventResponseWrapper.class), "getPortletMode()"),
   API286_EVENT_RESPONSE_WRAPPER_2(new API(EventResponseWrapper.class), "getRenderParameterMap()"),
   API286_EVENT_RESPONSE_WRAPPER_3(new API(EventResponseWrapper.class), "getResponse()"),
   API286_EVENT_RESPONSE_WRAPPER_4(new API(EventResponseWrapper.class), "getWindowState()"),
   API286_EVENT_RESPONSE_WRAPPER_5(new API(EventResponseWrapper.class), "removePublicRenderParameter()"),
   API286_EVENT_RESPONSE_WRAPPER_6(new API(EventResponseWrapper.class), "sendRedirect()"),
   API286_EVENT_RESPONSE_WRAPPER_7(new API(EventResponseWrapper.class), "setEvent()"),
   API286_EVENT_RESPONSE_WRAPPER_8(new API(EventResponseWrapper.class), "setPortletMode()"),
   API286_EVENT_RESPONSE_WRAPPER_9(new API(EventResponseWrapper.class), "setRenderParameter()"),
   API286_EVENT_RESPONSE_WRAPPER_10(new API(EventResponseWrapper.class), "setRenderParameters()"),
   API286_EVENT_RESPONSE_WRAPPER_11(new API(EventResponseWrapper.class), "setResponse()"),
   API286_EVENT_RESPONSE_WRAPPER_12(new API(EventResponseWrapper.class), "setWindowState()"),

   API286_FILTER_CHAIN_1(new API(FilterChain.class), "doFilter()"),

   API286_FILTER_CONFIG_1(new API(FilterConfig.class), "getFilterName()"),
   API286_FILTER_CONFIG_2(new API(FilterConfig.class), "getInitParameter()"),
   API286_FILTER_CONFIG_3(new API(FilterConfig.class), "getInitParameterNames()"),
   API286_FILTER_CONFIG_4(new API(FilterConfig.class), "getPortletContext()"),

   API286_PORTLET_FILTER_1(new API(PortletFilter.class), "destroy()"),
   API286_PORTLET_FILTER_2(new API(PortletFilter.class), "init()"),

   API286_RENDER_REQUEST_WRAPPER_1(new API(RenderRequestWrapper.class), "getETag()"),
   API286_RENDER_REQUEST_WRAPPER_2(new API(RenderRequestWrapper.class), "getRequest()"),
   API286_RENDER_REQUEST_WRAPPER_3(new API(RenderRequestWrapper.class), "setRequest()"),

   API286_RENDER_RESPONSE_WRAPPER_1(new API(RenderResponseWrapper.class), "createActionURL()"),
   API286_RENDER_RESPONSE_WRAPPER_2(new API(RenderResponseWrapper.class), "createRenderURL()"),
   API286_RENDER_RESPONSE_WRAPPER_3(new API(RenderResponseWrapper.class), "createResourceURL()"),
   API286_RENDER_RESPONSE_WRAPPER_4(new API(RenderResponseWrapper.class), "flushBuffer()"),
   API286_RENDER_RESPONSE_WRAPPER_5(new API(RenderResponseWrapper.class), "getBufferSize()"),
   API286_RENDER_RESPONSE_WRAPPER_6(new API(RenderResponseWrapper.class), "getCacheControl()"),
   API286_RENDER_RESPONSE_WRAPPER_7(new API(RenderResponseWrapper.class), "getCharacterEncoding()"),
   API286_RENDER_RESPONSE_WRAPPER_8(new API(RenderResponseWrapper.class), "getContentType()"),
   API286_RENDER_RESPONSE_WRAPPER_9(new API(RenderResponseWrapper.class), "getLocale()"),
   API286_RENDER_RESPONSE_WRAPPER_10(new API(RenderResponseWrapper.class), "getPortletOutputStream()"),
   API286_RENDER_RESPONSE_WRAPPER_11(new API(RenderResponseWrapper.class), "getResponse()"),
   API286_RENDER_RESPONSE_WRAPPER_12(new API(RenderResponseWrapper.class), "getWriter()"),
   API286_RENDER_RESPONSE_WRAPPER_13(new API(RenderResponseWrapper.class), "isCommited()"),
   API286_RENDER_RESPONSE_WRAPPER_14(new API(RenderResponseWrapper.class), "reset()"),
   API286_RENDER_RESPONSE_WRAPPER_15(new API(RenderResponseWrapper.class), "resetBuffer()"),
   API286_RENDER_RESPONSE_WRAPPER_16(new API(RenderResponseWrapper.class), "setBufferSize()"),
   API286_RENDER_RESPONSE_WRAPPER_17(new API(RenderResponseWrapper.class), "setContentType()"),
   API286_RENDER_RESPONSE_WRAPPER_18(new API(RenderResponseWrapper.class), "setNextPossiblePortletModes()"),
   API286_RENDER_RESPONSE_WRAPPER_19(new API(RenderResponseWrapper.class), "setResponse()"),
   API286_RENDER_RESPONSE_WRAPPER_20(new API(RenderResponseWrapper.class), "setTitle()"),

   API286_RESOURCE_FILTER_1(new API(ResourceFilter.class), "doFilter()"),

   API286_RESOURCE_REQUEST_WRAPPER_1(new API(ResourceRequestWrapper.class), "getCacheability()"),
   API286_RESOURCE_REQUEST_WRAPPER_2(new API(ResourceRequestWrapper.class), "getCharacterEncoding()"),
   API286_RESOURCE_REQUEST_WRAPPER_3(new API(ResourceRequestWrapper.class), "getContentLength()"),
   API286_RESOURCE_REQUEST_WRAPPER_4(new API(ResourceRequestWrapper.class), "getContentType()"),
   API286_RESOURCE_REQUEST_WRAPPER_5(new API(ResourceRequestWrapper.class), "getETag()"),
   API286_RESOURCE_REQUEST_WRAPPER_6(new API(ResourceRequestWrapper.class), "getMethod()"),
   API286_RESOURCE_REQUEST_WRAPPER_7(new API(ResourceRequestWrapper.class), "getPortletInputStream()"),
   API286_RESOURCE_REQUEST_WRAPPER_8(new API(ResourceRequestWrapper.class), "getPrivateRenderParameterMap()"),
   API286_RESOURCE_REQUEST_WRAPPER_9(new API(ResourceRequestWrapper.class), "getReader()"),
   API286_RESOURCE_REQUEST_WRAPPER_10(new API(ResourceRequestWrapper.class), "getRequest()"),
   API286_RESOURCE_REQUEST_WRAPPER_11(new API(ResourceRequestWrapper.class), "getResourceID()"),
   API286_RESOURCE_REQUEST_WRAPPER_12(new API(ResourceRequestWrapper.class), "setCharacterEncoding()"),
   API286_RESOURCE_REQUEST_WRAPPER_13(new API(ResourceRequestWrapper.class), "setRequest()"),

   API286_RESOURCE_RESPONSE_WRAPPER_1(new API(ResourceResponseWrapper.class), "createActionURL()"),
   API286_RESOURCE_RESPONSE_WRAPPER_2(new API(ResourceResponseWrapper.class), "createRenderURL()"),
   API286_RESOURCE_RESPONSE_WRAPPER_3(new API(ResourceResponseWrapper.class), "createResourceURL()"),
   API286_RESOURCE_RESPONSE_WRAPPER_4(new API(ResourceResponseWrapper.class), "flushBuffer()"),
   API286_RESOURCE_RESPONSE_WRAPPER_5(new API(ResourceResponseWrapper.class), "getBufferSize()"),
   API286_RESOURCE_RESPONSE_WRAPPER_6(new API(ResourceResponseWrapper.class), "getCacheControl()"),
   API286_RESOURCE_RESPONSE_WRAPPER_7(new API(ResourceResponseWrapper.class), "getCharacterEncoding()"),
   API286_RESOURCE_RESPONSE_WRAPPER_8(new API(ResourceResponseWrapper.class), "getContentType()"),
   API286_RESOURCE_RESPONSE_WRAPPER_9(new API(ResourceResponseWrapper.class), "getLocale()"),
   API286_RESOURCE_RESPONSE_WRAPPER_10(new API(ResourceResponseWrapper.class), "getPortletOutputStream()"),
   API286_RESOURCE_RESPONSE_WRAPPER_11(new API(ResourceResponseWrapper.class), "getResponse()"),
   API286_RESOURCE_RESPONSE_WRAPPER_12(new API(ResourceResponseWrapper.class), "getWriter()"),
   API286_RESOURCE_RESPONSE_WRAPPER_13(new API(ResourceResponseWrapper.class), "isCommited()"),
   API286_RESOURCE_RESPONSE_WRAPPER_14(new API(ResourceResponseWrapper.class), "reset()"),
   API286_RESOURCE_RESPONSE_WRAPPER_15(new API(ResourceResponseWrapper.class), "resetBuffer()"),
   API286_RESOURCE_RESPONSE_WRAPPER_16(new API(ResourceResponseWrapper.class), "setBufferSize()"),
   API286_RESOURCE_RESPONSE_WRAPPER_17(new API(ResourceResponseWrapper.class), "setCharacterEncoding()"),
   API286_RESOURCE_RESPONSE_WRAPPER_18(new API(ResourceResponseWrapper.class), "setContentLength()"),
   API286_RESOURCE_RESPONSE_WRAPPER_19(new API(ResourceResponseWrapper.class), "setContentType()"),
   API286_RESOURCE_RESPONSE_WRAPPER_20(new API(ResourceResponseWrapper.class), "setLocale()"),
   API286_RESOURCE_RESPONSE_WRAPPER_21(new API(ResourceResponseWrapper.class), "setResponse()"),



   // Ext tests



   EXT_EXPIRING_CACHE_1(new EXT("Expiring cache"), "Action URL invalidates expiring cache"),
   EXT_EXPIRING_CACHE_2(new EXT("Expiring cache"), "Overriding the expiration cache to 0 disable the cache on a render or an action/render."),
   EXT_EXPIRING_CACHE_3(new EXT("Expiring cache"), "Cache expiriation time"),
   EXT_EXPIRING_CACHE_4(new EXT("Expiring cache"), "Calling portlet with different portlet mode invalidates the cache"),
   EXT_EXPIRING_CACHE_5(new EXT("Expiring cache"), "Calling portlet with different render parameters invalidates the cache"),
   EXT_EXPIRING_CACHE_6(new EXT("Expiring cache"), "Calling portlet with different window state invalidates the cache"),

   EXT_NEVER_EXPIRING_CACHE_1(new EXT("Never expiring cache"), "Action URL invalidates never expiring cache"),
   EXT_NEVER_EXPIRING_CACHE_2(new EXT("Never expiring cache"), "Overriding the expiration cache to 0 disable the cache on a render or an action/render"),
   EXT_NEVER_EXPIRING_CACHE_3(new EXT("Never expiring cache"), "Calling portlet with different portlet mode invalidates the cache"),
   EXT_NEVER_EXPIRING_CACHE_4(new EXT("Never expiring cache"), "Calling portlet with different render parameters invalidates the cache"),
   EXT_NEVER_EXPIRING_CACHE_5(new EXT("Never expiring cache"), "Calling portlet with different window state invalidates the cache"),

   EXT_NO_CACHE_1(new EXT("No Cache"), "Caching behaviour with explicit no cache setting in portlet.xml"),
   EXT_NO_CACHE_2(new EXT("No Cache"), "Caching behaviour with implicit no cache setting in portlet.xml"),

   EXT_PORTLET_CONFIG_1(new EXT("PortletConfig"), "Obtain resource bundle during portlet init()"),
   EXT_PORTLET_CONFIG_2(new EXT("PortletConfig"), "Portlet with no resource bundle and empty <title></title> tag"),
   EXT_PORTLET_CONFIG_3(new EXT("PortletConfig"), "Cascade and fallback mechanisms for resource bundle"),
   EXT_PORTLET_CONFIG_4(new EXT("PortletConfig"), "Simple resource bundle test case with empty <title/> tag."),

   EXT_PORTLET_MODE_1(new EXT("PortletMode"), "Check portlet mode during action phase"),
   EXT_PORTLET_MODE_2(new EXT("PortletMode"), "Null portlet mode can be set during action phase"),
   EXT_PORTLET_MODE_3(new EXT("PortletMode"), "RenderRequest.isPortletModeAllowed(null) returns false"),
   EXT_PORTLET_MODE_4(new EXT("PortletMode"), "Can set the portlet mode on render URL before having set the content type on the response"),
   EXT_PORTLET_MODE_5(new EXT("PortletMode"), "Can set null portlet mode during render phase"),
   EXT_PORTLET_MODE_6(new EXT("PortletMode"), "Can use set the portlet mode on action URL before having set the content type on the response"),
   EXT_PORTLET_MODE_7(new EXT("PortletMode"), "Test custom portlet modes wether they are portal or portlet managed."),

   EXT_PORTLET_REQUESTS_1(new EXT("PortletRequest"), "Test that a POST request having a content type set to x-www-form-urlencoded will make the body content unavailable " +
      " as an input stream or a reader and the parameters are decoded."),
   EXT_PORTLET_REQUESTS_2(new EXT("PortletRequest"), "Test that a POST request having a content type not set to x-www-form-urlencoded will make the body content " +
      " available as a input stream."),
   EXT_PORTLET_REQUESTS_3(new EXT("PortletRequest"), "Test that a POST request having a content type not set to x-www-form-urlencoded will make the body content " +
      " available as a reader."),
   EXT_PORTLET_REQUESTS_4(new EXT("PortletRequest"), "Test request attribute scoping"),
   EXT_PORTLET_REQUESTS_5(new EXT("PortletRequest"), "Test manipulating request attributes"),
   EXT_PORTLET_REQUESTS_6(new EXT("PortletRequest"), "Show that we can access request headers from the portlet request properties."),
   EXT_PORTLET_REQUESTS_7(new EXT("PortletRequest"), "Test request parameters"),
   EXT_PORTLET_REQUESTS_8(new EXT("PortletRequest"), "Test access to cookies in headers"),
   EXT_PORTLET_REQUESTS_9(new EXT("PortletRequest"), "Show that we can access request headers from the portlet request properties."),
   EXT_PORTLET_REQUESTS_10(new EXT("PortletRequest"), "If a portlet wants to delete a public render parameter it needs to use the" +
      " removePublic method on the or the PortletURL."),
   EXT_PORTLET_REQUESTS_11(new EXT("PortletRequest"), " Test that removePublicRenderParameter method on PortletURL removes the render parameter" +
      " in the context of a render url and does nothing in the context of an action url."),
   EXT_PORTLET_REQUESTS_12(new EXT("PortletRequest"), "For serveResource requests the portlet must receive any resource parameters that were" +
      " explicitly set on the ResourceURL that triggered the request. If the cacheability level of" +
      " that resource URL (see PLT.13.7) was PORTLET or PAGE, the portlet must also receive the" +
      " render parameters present in the request in which the URL was created." +
      " If a resource parameter is set that has the same name as a render parameter, the render" +
      " parameter must be the last entry in the parameter value array."),

   EXT_PORTLET_RESPONSES_1(new EXT("PortletResponse"), "Test that character encoding is ignored as specified by the spec in PLT.12.3.1"),
   EXT_PORTLET_RESPONSES_2(new EXT("PortletResponse"), "Not setting the content type before getting a stream will use the content type defined" +
      " by the portlet request response content type."),
   EXT_PORTLET_RESPONSES_3(new EXT("PortletResponse"), "Test adding coockies to response"),
   EXT_PORTLET_RESPONSES_4(new EXT("PortletResponse"), "Test adding HTTP headers"),

   EXT_PREFERENCES_1(new EXT("PortletPreferences"), "Asserts that isReadOnly() returns correct values during both render and action phase."),
   EXT_PREFERENCES_2(new EXT("PortletPreferences"), "PortletPreferences.getMap()"),
   EXT_PREFERENCES_3(new EXT("PortletPreferences"), "Store persists all changes"),

   EXT_SESSION_1(new EXT("PortletSession"), "The goal is to test that cross context session attributes are set in container and are accessible from the direct" +
      " servlet."),
   EXT_SESSION_2(new EXT("PortletSession"), "Test that HTTPSession invalidation invalidates PortletSession"),
   EXT_SESSION_3(new EXT("PortletSession"), "Obtain PortletSession with 'create' option equals false"),
   EXT_SESSION_4(new EXT("PortletSession"), "Test that PORTLET_SCOPE attributes are accessible in APPLICATION_SCOPE"),
   EXT_SESSION_5(new EXT("PortletSession"), "Test that a session does not exist the first time the portlet is accessed."),

   EXT_TAGLIB_1(new EXT("Taglib"), "Test ActionURL tag implementation"),
   EXT_TAGLIB_2(new EXT("Taglib"), "Test DefineObjects tag implementation"),
   EXT_TAGLIB_3(new EXT("Taglib"), "Test Namespace tag implementation"),
   EXT_TAGLIB_4(new EXT("Taglib"), "Test RenderURL tag implementation"),

   EXT_DISPATCHER_1(new EXT("Dispatcher"), "Test passing session attributes between servlet and portlet with APPLICATION_SCOPE"),
   EXT_DISPATCHER_2(new EXT("Dispatcher"), "Test creating response content in dispatched servlet with forward and include"),
   EXT_DISPATCHER_3(new EXT("Dispatcher"), "Test dispatch to servlet filter"),
   EXT_DISPATCHER_4(new EXT("Dispatcher"), "Test filter chains"),
   EXT_DISPATCHER_5(new EXT("Dispatcher"), "Test request.getRequestURI in dispatched servlet"),
   EXT_DISPATCHER_6(new EXT("Dispatcher"), "Test include markup file (HTML)"),
   EXT_DISPATCHER_7(new EXT("Dispatcher"), "Test passing session attributes between servlet and portlet with PORTLET_SCOPE with javax.portlet.servletDefaultSessionScope runtime" +
      "option set"),
   EXT_DISPATCHER_8(new EXT("Dispatcher"), "Test access to request headers in dispatched servlet"),

   EXT_EVENT_1(new EXT("Event"), "Test payload passed to the event"),

   EXT_PORTLET_CONTEXT_1(new EXT("PortletContext"), "Test scope eviction with javax.portlet.actionScopedRequestAttributes runtime parameter"),

   EXT_PORTLET_FILTER_1(new EXT("PortletFilter"), "Test that the init parameters of a filter are correct."),
   EXT_PORTLET_FILTER_2(new EXT("PortletFilter"), "Test that a portlet filter mapped 2 times in the descriptor is applied on" +
      " each portlet."),

   EXT_PORTLET_INTERFACE_1(new EXT("Portlet interface"), "Test that portlet is not called after throwing UnavailableException"),

   ;

   public String toString()
   {
      return this.name();
   }

   /**
    * An abstract reference.
    */
   public abstract static class Ref
   {
      public abstract String toString();
   }

   /**
    * A reference to a JSR TCK test.
    */
   public static class TCK extends Ref
   {

      /** . */
      private final int index;

      /** . */
      private final String section;

      public TCK(int index, String section)
      {
         this.index = index;
         this.section = section;
      }

      public TCK(int index)
      {
         this.index = index;
         this.section = null;
      }

      public int getIndex()
      {
         return index;
      }

      public String getSection()
      {
         return section;
      }

      public String toString()
      {
         return "TCK[index=" + index + ";section=" + section +  "]";
      }
   }

   public static class API extends Ref
   {

      /** . */
      private Class clazz;

      public API(Class clazz)
      {
         this.clazz = clazz;
      }

      public Class getClazz()
      {
         return clazz;
      }

      public String toString()
      {
         return "API[" + clazz.getSimpleName() + "]";
      }
   }

   public static class EXT extends Ref
   {

      private final String section;

      public EXT(String section)
      {
         this.section = section;
      }

      public String toString()
      {
         return "EXT[" + section + "]";
      }
   }

   /**
    * The status of an assertion.
    */
   public abstract static class Status
   {
      protected Status()
      {
      }

      private static Inactive disabled(String cause)
      {
         return new Inactive(Inactive.Type.DISABLED, cause);
      }

      private static Inactive todo(String cause)
      {
         return new Inactive(Inactive.Type.TODO, cause);
      }

      private static Inactive postponed(String cause)
      {
         return new Inactive(Inactive.Type.POSTPONED, cause);
      }

      private static Inactive specUntestable(String cause)
      {
         return new Untestable(Untestable.Kind.SPEC, cause);
      }

      private static Inactive jbossUntestable(String cause)
      {
         return new Untestable(Untestable.Kind.JBOSS, cause);
      }

      private static Duplicate duplicate(Assertion target, String description)
      {
         return new Duplicate(target, description);
      }
   }

   public static class Active extends Status
   {
      public String toString()
      {
         return "ACTIVE";
      }
   }

   public static class Inactive extends Status
   {

      /** . */
      private final Type type;

      /** . */
      private final String msg;

      public enum Type
      {
         POSTPONED, TODO, DISABLED, UNTESTABLE
      }

      public Inactive(Type type, String msg)
      {
         this.type = type;
         this.msg = msg;
      }

      public Type getType()
      {
         return type;
      }

      public String getMessage()
      {
         return msg;
      }

      public String toString()
      {
         return "INACTIVE[" + type.name() + ";" + getMessage() + "]";
      }
   }

   public static class Untestable extends Inactive
   {

      public enum Kind
      {
         JBOSS, SPEC
      }

      /** . */
      private final Kind kind;

      public Untestable(Kind kind, String msg)
      {
         super(Inactive.Type.UNTESTABLE, msg);

         //
         this.kind = kind;
      }

      public Kind getKind()
      {
         return kind;
      }


      public String toString()
      {
         return "UNTESTABLE[" + kind.name() + ";" + getMessage() + "]";
      }
   }

   public static class Duplicate extends Active
   {

      /** . */
      private final Assertion target;

      /** . */
      private final String description;

      private Duplicate(Assertion target, String description)
      {
         this.target = target;
         this.description = description;
      }

      public Assertion getTarget()
      {
         return target;
      }

      public String getDescription()
      {
         return description;
      }

      public String toString()
      {
         return "DUPLICATED[" + target != null ? target.name() : "" + ";" + description + "]";
      }
   }

   /** . */
   private final Ref ref;

   /** . */
   private final String description;

   /** . */
   private Status status;

   /** . */
   private Assertion assertion;

   Assertion(Ref ref, String description)
   {
      this(ref, new Active(), description);
   }

   Assertion(Ref ref, Assertion assertion, String description)
   {
      this(ref, assertion, new Active(), description);
   }

   Assertion(Ref ref, Status status, String description)
   {
      this.ref = ref;
      this.description = description;
      this.status = status;
   }

   Assertion(Ref ref, Assertion assertion, Status status, String description)
   {
      this.ref = ref;
      this.description = description;
      this.status = status;
      this.assertion = assertion;
   }



   public Ref getRef()
   {
      return ref;
   }

   public String getDescription()
   {
      return description;
   }

   public Status getStatus()
   {
      return status;
   }

   public Assertion getAssertion()
   {
      return assertion;
   }


}
