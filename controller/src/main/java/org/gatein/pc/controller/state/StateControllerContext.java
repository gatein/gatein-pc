package org.gatein.pc.controller.state;

import org.gatein.pc.controller.ControllerContext;

import java.util.Map;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public interface StateControllerContext
{

   /**
    * <p>Update the public navigational state of a portlet window. The interpretation of what should be updated is left up
    * to the implementor. An example of implementation would use the mapping between qname and name provided by the referenced
    * portlet info.</p>
    *
    * <p>The update argument values with a length of zero should be treated as removals.</p>
    *
    * @param controller the controller
    * @param page the page
    * @param portletWindowId the portlet window id
    * @param update the updates
    * @throws IllegalArgumentException if an argument is not valid
    * @throws IllegalStateException if the page state is read only
    */
   void updatePublicNavigationalState(
      ControllerContext controller,
      PageNavigationalState page,
      String portletWindowId,
      Map<String, String[]> update);

   /**
    * Obtain the public navigational state of a portlet window. The interpretation of what should be retrieved is left up
    * to the implementor. An example of implementation would use the mapping between qnames and name provided by the
    * referenced portlet info.
    *
    * @param controller the controller
    * @param page the page
    * @param windowId the portlet window id  @return the portlet public navigational state
    * @throws IllegalArgumentException if an argument is not valid
    */
   Map<String, String[]> getPublicWindowNavigationalState(
      ControllerContext controller,
      PageNavigationalState page,
      String windowId);
}
