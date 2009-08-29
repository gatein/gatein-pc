/*
* JBoss, a division of Red Hat
* Copyright 2008, Red Hat Middleware, LLC, and individual contributors as indicated
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

package org.gatein.pc.api.info;

import org.gatein.common.util.Tools;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:chris.laprun@jboss.com">Chris Laprun</a>
 * @version $Revision$
 */
public interface RuntimeOptionInfo
{
   String JAVAX_PORTLET_ACTION_SCOPED_REQUEST_ATTRIBUTES = "javax.portlet.actionScopedRequestAttributes";
   String NUMBER_OF_CACHED_SCOPES = "numberOfCachedScopes";
   String JAVAX_PORTLET_ESCAPE_XML = "javax.portlet.escapeXml";
   String JAVAX_PORTLET_SERVLET_DEFAULT_SESSION_SCOPE = "javax.portlet.servletDefaultSessionScope";

   String ORG_JBOSS_PORTLETCONTAINER_REMOTABLE = "org.jboss.portletcontainer.remotable";

   Set<String> SUPPORTED_OPTIONS = Collections.unmodifiableSet(Tools.toSet(
      JAVAX_PORTLET_ACTION_SCOPED_REQUEST_ATTRIBUTES,
      JAVAX_PORTLET_ESCAPE_XML,
      JAVAX_PORTLET_SERVLET_DEFAULT_SESSION_SCOPE,
      ORG_JBOSS_PORTLETCONTAINER_REMOTABLE));

   String getName();

   List<String> getValues();
}
