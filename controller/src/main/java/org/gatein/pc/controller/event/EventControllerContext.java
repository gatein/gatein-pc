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
package org.gatein.pc.controller.event;

import org.gatein.pc.api.invocation.response.PortletInvocationResponse;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 630 $
 */
public interface EventControllerContext
{

   /** . */
   int EVENT_PRODUCER_NOT_AVAILABLE = 0;

   /** . */
   int EVENT_PRODUCER_INFO_NOT_AVAILABLE = 1;

   /** . */
   int EVENT_CONSUMER_NOT_AVAILABLE = 2;

   /** . */
   int EVENT_CONSUMER_INFO_NOT_AVAILABLE = 3;

   /** . */
   int PORTLET_DOES_NOT_CONSUME_EVENT = 4;

   /** . */
   int CONSUMED_EVENT_FLOODED = 5;

   /** . */
   int PRODUCED_EVENT_FLOODED = 6;

   /**
    * <p>Context call back  when an event is produced. The session
    * argument gives to the context the capability to queue events in response
    * of the produced event or to interrupt the session. It has also access
    * to the full history of distributed events in order to provide advanced
    * implementation of event cycle detection.</p>
    *
    * <p>During the invocation of this method, any runtime exception thrown will signal
    * a failure and the produced event will be discarded although the event
    * distribution will continue.</p>
    *
    * <p>During the invocation of this method, any error thrown will be propagated
    * to the portlet controller invoker.</p>
    *
    * @param context the session
    * @param producedEvent the produced event
    * @param sourceEvent the source event
    */
   void eventProduced(EventPhaseContext context, PortletWindowEvent producedEvent, PortletWindowEvent sourceEvent);

   /**
    * <p>Context call back  when an event is consumed by a portlet. The session argument
    * only provides querying capabilities and it is not possible to queue event
    * or interrupt the session.</p>
    *
    * <p>During the invocation of this method, any runtime exception thrown will
    * be ignored by the controller.</p>
    *
    * <p>During the invocation of this method, any error thrown will be propagated
    * to the portlet controller invoker.</p>
    *
    * @param context the session
    * @param consumedEvent the consumed event
    * @param consumerResponse the consumer response
    */
   void eventConsumed(EventPhaseContext context, PortletWindowEvent consumedEvent, PortletInvocationResponse consumerResponse);

   /**
    * <p>Context call back when an event failed to be delivered because the invoker threw an exception.
    * The session argument only provides querying capabilities and it is not possible to queue event
    * or interrupt the session.</p>
    *
    * <p>During the invocation of this method, any runtime exception thrown will
    * be ignored by the controller.</p>
    *
    * <p>During the invocation of this method, any error thrown will be propagated
    * to the portlet controller invoker.</p>
    *
    * @param context the session
    * @param failedEvent the failed event
    * @param throwable the throwable
    */
   void eventFailed(EventPhaseContext context, PortletWindowEvent failedEvent, Throwable throwable);

   /**
    * <p>Context call back when an event is discarded by the controller for a specific reason.
    * The session argument only provides querying capabilities and it is not possible to queue event
    * or interrupt the session.</p>
    *
    * <p>The cause value is an integer among the constants
    * <ul>
    *    <li>{@link #CONSUMED_EVENT_FLOODED}</li>
    *    <li>{@link #EVENT_CONSUMER_INFO_NOT_AVAILABLE}</li>
    *    <li>{@link #EVENT_CONSUMER_NOT_AVAILABLE}</li>
    *    <li>{@link #EVENT_PRODUCER_INFO_NOT_AVAILABLE}</li>
    *    <li>{@link #EVENT_PRODUCER_NOT_AVAILABLE}</li>
    *    <li>{@link #PORTLET_DOES_NOT_CONSUME_EVENT}</li>
    *    <li>{@link #PRODUCED_EVENT_FLOODED}</li>
    * <p/>
    *
    * <p>During the invocation of this method, any runtime exception thrown will
    * be ignored by the controller.</p>
    *
    * <p>During the invocation of this method, any error thrown will be propagated
    * to the portlet controller invoker.</p>
    *
    * @param context the session
    * @param discardedEvent the discarded event
    * @param cause the cause
    */
   void eventDiscarded(EventPhaseContext context, PortletWindowEvent discardedEvent, int cause);

}
