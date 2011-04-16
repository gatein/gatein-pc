/*
* JBoss, a division of Red Hat
* Copyright 2006, Red Hat Middleware, LLC, and individual contributors as indicated
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.gatein.pc.portlet.impl.jsr286.taglib;

import javax.servlet.jsp.tagext.VariableInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletPreferences;
import java.util.Map;

/**
 * @author <a href="mailto:boleslaw dot dawidowicz at redhat anotherdot com">Boleslaw Dawidowicz</a>
 * @version : 0.1 $
 */
public class DefineObjects286TagTEI extends TagExtraInfo
{
   public static final String portletConfigVariableName = "portletConfig";
   public static final String renderRequestVariableName = "renderRequest";
   public static final String renderResponseVariableName = "renderResponse";
   public static final String actionRequestVariableName = "actionRequest";
   public static final String actionResponseVariableName = "actionResponse";
   public static final String resourceRequestVariableName = "resourceRequest";
   public static final String resourceResponseVariableName = "resourceResponse";
   public static final String eventRequestVariableName = "eventRequest";
   public static final String eventResponseVariableName = "eventResponse";
   public static final String portletSessionVariableName = "portletSession";
   public static final String portletSessionScopeVariableName = "portletSessionScope";
   public static final String portletPreferencesVariableName = "portletPreferences";
   public static final String portletPreferencesValuesVariableName = "portletPreferencesValues";

   public VariableInfo[] getVariableInfo(TagData data)
   {
      VariableInfo info1 = new VariableInfo(portletConfigVariableName,
         PortletConfig.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info2 = new VariableInfo(renderRequestVariableName,
         RenderRequest.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info3 = new VariableInfo(renderResponseVariableName,
         RenderResponse.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info4 = new VariableInfo(actionRequestVariableName,
         ActionRequest.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info5 = new VariableInfo(actionResponseVariableName,
         ActionResponse.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info6 = new VariableInfo(resourceRequestVariableName,
         ResourceRequest.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info7 = new VariableInfo(resourceResponseVariableName,
         ResourceResponse.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info8 = new VariableInfo(eventRequestVariableName,
         EventRequest.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info9 = new VariableInfo(eventResponseVariableName,
         EventResponse.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info10 = new VariableInfo(portletSessionVariableName,
         PortletSession.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info11 = new VariableInfo(portletSessionScopeVariableName,
         Map.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info12 = new VariableInfo(portletPreferencesVariableName,
         PortletPreferences.class.getName(), true, VariableInfo.AT_END);
      VariableInfo info13 = new VariableInfo(portletPreferencesValuesVariableName,
         Map.class.getName(), true, VariableInfo.AT_END);



      VariableInfo[] info = {info1, info2, info3, info4, info5, info6, info7, info8, info9, info10, info11, info12, info13};
      return info;
   }
}
