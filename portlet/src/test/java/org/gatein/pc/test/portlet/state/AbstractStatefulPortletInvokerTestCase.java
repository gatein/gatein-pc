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
package org.gatein.pc.test.portlet.state;

import org.gatein.common.util.Tools;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.InvalidPortletIdException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.support.info.PortletInfoSupport;
import org.gatein.pc.portlet.support.PortletSupport;
import org.gatein.pc.api.info.MetaInfo;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.portlet.state.AbstractPropertyContext;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.portlet.state.SimplePropertyMap;

import static org.jboss.unit.api.Assert.*;
import org.jboss.unit.api.pojo.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Arrays;

/**
 * @author <a href="mailto:julien@jboss.org">Julien Viet</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractStatefulPortletInvokerTestCase
{

   /** . */
   protected final boolean persistLocally;

   protected AbstractStatefulPortletInvokerTestCase(boolean persistLocally)
   {
      this.persistLocally = persistLocally;
   }

   /**
    *
    */
   protected abstract PortletSupport getPortletSupport(PortletContext portletRef) throws PortletInvokerException;

   /**
    *
    */
   protected abstract Portlet getPortlet(PortletContext portletRef) throws PortletInvokerException;

   /**
    *
    */
   protected abstract String getPortletId(PortletContext portletRef) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext createPOPRef(PortletInfoSupport portletInfo) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext createNonExistingPOPRef() throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext createInvalidPOPRef() throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext createLocalClone(PortletContext portletRef) throws Exception;

   /**
    *
    */
   protected abstract void destroyClone(PortletContext portletRef) throws Exception;

   /**
    *
    */
   protected abstract PortletContext createClone(PortletContext portletRef) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext setProperties(PortletContext portletRef, PropertyChange[] changes) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PropertyMap getProperties(PortletContext portletRef) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PropertyMap getProperties(PortletContext portletRef, Set keys) throws PortletInvokerException;

   /**
    *
    */
   protected abstract List destroyClones(List portletRefs) throws PortletInvokerException;

   /**
    *
    */
   protected abstract void assertNoExistingState();

   /**
    *
    */
   protected abstract void invoke(PortletInvocation invocation) throws PortletInvokerException;

   /**
    *
    */
   protected abstract ActionInvocation createAction(PortletContext portletRef, AccessMode accessMode);

   /**
    *
    */
   protected abstract PortletContext getImplicitClonedRef(ActionInvocation action);

   /**
    *
    */
   protected abstract PortletContext getModifiedPortletRef(ActionInvocation action);

   /**
    *
    */
   protected abstract void addPreference(PortletContext popRef, String key, List<String> defaultValue);

   /**
    *
    */
   protected abstract void addPreference(PortletContext popRef, String key, List<String> defaultValue, Boolean readOnly);

   /**
    *
    */
   protected final void assertCloneDoesNotExist(PortletContext ref)
   {
      if (persistLocally)
      {
         try
         {
            getProperties(ref);
            fail("Was expecting a NoSuchPortletException to be thrown");
         }
         catch (NoSuchPortletException expected)
         {
         }
         catch (PortletInvokerException e)
         {
            fail(e);
         }
      }
      else
      {
         // We cannot assert it because we really store the state on the consumer and keep no reference
         // on the producer, so the best we can do is to call it and assert that nothing wrong happens.
         try
         {
            getProperties(ref);
         }
         catch (PortletInvokerException e)
         {
            fail(e);
         }
      }
   }

   /**
    *
    */
   protected final PortletContext createLocalCCPRef() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      return createLocalClone(popCtx);
   }

   /**
    *
    */
   protected final PortletContext createNonExistingLocalCCPRef() throws Exception
   {
      PortletContext popRef = createPOPRef();
      PortletContext ccpRef = createLocalClone(popRef);
      destroyClone(ccpRef);
      return ccpRef;
   }

   /**
    *
    */
   protected final PortletContext createPOPRef() throws PortletInvokerException
   {
      PortletInfoSupport info = new PortletInfoSupport();
      return createPOPRef(info);
   }

   @Test
   public void testCloneWithNullContext() throws Exception
   {
      try
      {
         createClone(null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
      assertNoExistingState();
   }

   @Test
   public void testCloneNonExistingPOP() throws Exception
   {
      PortletContext popCtx = createNonExistingPOPRef();
      try
      {
         createClone(popCtx);
         fail("was expecting an NoSuchPortletException");
      }
      catch (NoSuchPortletException expected)
      {
      }
      assertNoExistingState();
   }

   @Test
   public void testCloneNonExistingCCP() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      try
      {
         createClone(ccpCtx);
         fail("was expecting an NoSuchPortletException");
      }
      catch (NoSuchPortletException expected)
      {
      }
      assertNoExistingState();
   }

//   @Test
//   public void testCloneInvalidCCP() throws Exception
//   {
//      PortletContext ccpCtx = getProducer().wrapCCP("InvalidPortletId");
//      try
//      {
//         getProducer().createClone(ccpCtx);
//         fail("was expecting an InvalidPortletIdException");
//      }
//      catch (InvalidPortletIdException expected)
//      {
//      }
//      assertNoExistingState();
//   }

   @Test
   public void testCloneInvalidPOP() throws Exception
   {
      PortletContext popCtx = createInvalidPOPRef();
      try
      {
         createClone(popCtx);
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException expected)
      {
      }
      assertNoExistingState();
   }

   @Test
   public void testClonePortlet() throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();
      info.getMeta().setDisplayName("MyPortlet");
      PortletContext popCtx = createPOPRef(info);
      addPreference(popCtx, "abc", Arrays.asList("def"));

      //
      PortletContext ccp1Ctx = createClone(popCtx);


      // Check state
      PropertyMap expected = new SimplePropertyMap();
      expected.setProperty("abc", Arrays.asList("def"));
      PropertyMap ccp1Props = getProperties(ccp1Ctx);
      ValueMapAssert.assertEquals(expected, ccp1Props);

      //
      Portlet ccp1 = getPortlet(ccp1Ctx);
      LocalizedString abc = ccp1.getInfo().getMeta().getMetaValue(MetaInfo.DISPLAY_NAME);
      assertEquals("MyPortlet", abc.getString(Locale.ENGLISH, true));
      _assertEquals(ccp1Ctx, ccp1.getContext());

      // Update state
      PropertyChange[] changes = new PropertyChange[]{PropertyChange.newUpdate("ghi", Arrays.asList("jkl"))};
      ccp1Ctx = setProperties(ccp1Ctx, changes);

      // Clone a CCP
      PortletContext ccp2Ctx = createClone(ccp1Ctx);

      // Check state
      expected = new SimplePropertyMap();
      expected.setProperty("abc", Arrays.asList("def"));
      expected.setProperty("ghi", Arrays.asList("jkl"));
      PropertyMap ccp2Props = getProperties(ccp2Ctx);
      ValueMapAssert.assertEquals(expected, ccp2Props);

      //
      Portlet ccp2 = getPortlet(ccp2Ctx);
      LocalizedString def = ccp2.getInfo().getMeta().getMetaValue(MetaInfo.DISPLAY_NAME);
      assertEquals("MyPortlet", def.getString(Locale.ENGLISH, true));
   }

   @Test
   public void testGetWithNullId() throws Exception
   {
      try
      {
         getPortlet(null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   @Test
   public void testGetNonExistingPOP() throws Exception
   {
      PortletContext pop = createNonExistingPOPRef();
      try
      {
         getPortlet(pop);
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException expected)
      {
      }
   }

   @Test
   public void testGetNonExistingCCP() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      try
      {
         getPortlet(ccpCtx);
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException expected)
      {
      }
   }

   @Test
   public void testGetInvalidPOP() throws Exception
   {
      PortletContext popCtx = createInvalidPOPRef();
      try
      {
         getPortlet(popCtx);
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException expected)
      {
      }
   }

//   @Test
//   public void testGetInvalidCCP() throws Exception
//   {
//      PortletContext ccpId = getProducer().wrapCCP("InvalidPortletId");
//      try
//      {
//         getProducer().getPortlet(ccpId.getId());
//         fail("was expecting an InvalidPortletIdException");
//      }
//      catch (InvalidPortletIdException expected)
//      {
//      }
//   }

   @Test
   public void testDestroyWithNullId() throws Exception
   {
      try
      {
         destroyClones(null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   @Test
   public void testDestroyPOP() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      List failures = destroyClones(Collections.singletonList(popCtx));
      assertEquals(1, failures.size());
      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
      assertEquals(getPortletId(popCtx), failure.getPortletId());
   }

   @Test
   public void testDestroyCCP() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      PortletContext ccpCtx = createClone(popCtx);
      List failures = destroyClones(Collections.singletonList(ccpCtx));
      assertEquals(0, failures.size());
      assertNoExistingState();
   }

//   @Test
//   public void testDestroyInvalidCCP() throws Exception
//   {
//      PortletContext ccpId = getProducer().wrapCCP("PortletId");
//      List failures = getProducer().destroyClones(Collections.singletonList(ccpId));
//      assertEquals(1, failures.size());
//      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
//      assertEquals(ccpId.getId(), failure.getPortletId());
//   }

   @Test
   public void testDestroyNonExistingCCP() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      List failures = destroyClones(Collections.singletonList(ccpCtx));
      assertEquals(1, failures.size());
      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
      assertEquals(getPortletId(ccpCtx), failure.getPortletId());
   }

   @Test
   public void testGetPropertiesWithNullPortlet() throws Exception
   {
      try
      {
         getProperties(null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
      try
      {
         getProperties(null, new HashSet());
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   @Test
   public void testGetNonExistingPOPProperties() throws Exception
   {
      PortletContext popCtx = createNonExistingPOPRef();
      try
      {
         getProperties(popCtx);
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException e)
      {
      }
      try
      {
         getProperties(popCtx, new HashSet());
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException e)
      {
      }
   }

   @Test
   public void testGetInvalidPOPProperties() throws Exception
   {
      PortletContext popCtx = createInvalidPOPRef();
      try
      {
         getProperties(popCtx);
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException e)
      {
      }
      try
      {
         getProperties(popCtx, new HashSet());
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException e)
      {
      }
   }

   @Test
   public void testGetNonExistingCCPProperties() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      try
      {
         getProperties(ccpCtx);
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException e)
      {
      }
      try
      {
         getProperties(ccpCtx, new HashSet());
         fail("was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException e)
      {
      }
   }

//   @Test
//   public void testGetInvalidCCPProperties() throws Exception
//   {
//      PortletContext ccpId = getProducer().wrapCCP("InvalidPortletId");
//      try
//      {
//         getProducer().getProperties(ccpId);
//         fail("was expecting a nInvalidPortletIdException");
//      }
//      catch (InvalidPortletIdException e)
//      {
//      }
//      try
//      {
//         getProducer().getProperties(ccpId, new HashSet());
//         fail("was expecting a nInvalidPortletIdException");
//      }
//      catch (InvalidPortletIdException e)
//      {
//      }
//   }

   @Test
   public void testGetPOPWithNullKeys() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      try
      {
         getProperties(popCtx, null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   @Test
   public void testGetCCPWithNullKeys() throws Exception
   {
      PortletContext ccpCtx = createLocalCCPRef();
      try
      {
         getProperties(ccpCtx, null);
         fail("was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException e)
      {
      }
   }

   @Test
   public void testGetPOPProperties() throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();
      PortletContext popCtx = createPOPRef(info);
      addPreference(popCtx, "abc", Arrays.asList("def"));
      addPreference(popCtx, "ghi", Arrays.asList("jkl"), Boolean.TRUE);

      //
      PropertyMap props = getProperties(popCtx);
      PropertyMap expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("def"));
      expectedProps.setProperty("ghi", Arrays.asList("jkl"));
      ValueMapAssert.assertEquals(expectedProps, props);

      //
      props = getProperties(popCtx, Tools.toSet("abc", "mno"));
      expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("def"));
      ValueMapAssert.assertEquals(expectedProps, props);
   }

   @Test
   public void testGetCCPProperties() throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();
      PortletContext popCtx = createPOPRef(info);
      addPreference(popCtx, "abc", Arrays.asList("def"));
      addPreference(popCtx, "ghi", Arrays.asList("jkl"));
      addPreference(popCtx, "mno", Arrays.asList("pqr"), Boolean.TRUE);
      addPreference(popCtx, "stu", Arrays.asList("vwx"), Boolean.TRUE);

      //
      PortletContext ccpCtx = createClone(popCtx);
      ccpCtx = setProperties(ccpCtx, new PropertyChange[]{
         PropertyChange.newUpdate("abc", Arrays.asList("_def")),
         PropertyChange.newReset("gho"),
         PropertyChange.newUpdate("mno", Arrays.asList("_pqr")),
         PropertyChange.newReset("stu")});

      //
      PropertyMap props = getProperties(ccpCtx);
      PropertyMap expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("_def"));
      expectedProps.setProperty("ghi", Arrays.asList("jkl"));
      expectedProps.setProperty("mno", Arrays.asList("pqr"));
      expectedProps.setProperty("stu", Arrays.asList("vwx"));
      ValueMapAssert.assertEquals(expectedProps, props);

      //
      props = getProperties(ccpCtx, Tools.toSet("abc", "mno", "yz"));
      expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("_def"));
      expectedProps.setProperty("mno", Arrays.asList("pqr"));
      ValueMapAssert.assertEquals(expectedProps, props);
   }

   @Test
   public void testSetPropertiesWithNullId() throws Exception
   {
      try
      {
         setProperties(null, new PropertyChange[0]);
         fail("Was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   @Test
   public void testSetPropertiesWithNullProperties() throws Exception
   {
      PortletContext ccpCtx = createLocalCCPRef();
      try
      {
         setProperties(ccpCtx, null);
         fail("Was expecting an IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   @Test
   public void testSetPOPProperties() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      try
      {
         setProperties(popCtx, new PropertyChange[0]);
         fail("Was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException expected)
      {
      }
   }

   @Test
   public void testSetNonExistingCCPProperties() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      try
      {
         setProperties(ccpCtx, new PropertyChange[0]);
         fail("Was expecting a NoSuchPortletException");
      }
      catch (NoSuchPortletException expected)
      {
      }
   }

   @Test
   public void testSetCCPProperties() throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();
      PortletContext popCtx = createPOPRef(info);
      addPreference(popCtx, "override_update", Arrays.asList("override_update_portlet_value"));
      addPreference(popCtx, "override_reset", Arrays.asList("override_reset_portlet_value"));
      addPreference(popCtx, "override_create", Arrays.asList("override_create_portlet_value"));
      addPreference(popCtx, "readonly_create", Arrays.asList("readonly_create_portlet_value"), Boolean.TRUE);

      //
      PortletContext ccpCtx = createClone(popCtx);
      ccpCtx = setProperties(ccpCtx, new PropertyChange[]{
         PropertyChange.newUpdate("override_update", Arrays.asList("override_update_clone_value")),
         PropertyChange.newUpdate("override_reset", Arrays.asList("override_reset_clone_value")),
         PropertyChange.newUpdate("dynamic_update", Arrays.asList("dynamic_update_clone_value")),
         PropertyChange.newUpdate("dynamic_reset", Arrays.asList("dynamic_reset_clone_value")),
      });

      //
      PropertyMap expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("override_update", Arrays.asList("override_update_clone_value"));
      expectedProps.setProperty("override_reset", Arrays.asList("override_reset_clone_value"));
      expectedProps.setProperty("override_create", Arrays.asList("override_create_portlet_value"));
      expectedProps.setProperty("readonly_create", Arrays.asList("readonly_create_portlet_value"));
      expectedProps.setProperty("dynamic_update", Arrays.asList("dynamic_update_clone_value"));
      expectedProps.setProperty("dynamic_reset", Arrays.asList("dynamic_reset_clone_value"));
      PropertyMap ccpProps = getProperties(ccpCtx);
      ValueMapAssert.assertEquals(expectedProps, ccpProps);

      //
      PropertyChange[] changes = new PropertyChange[]
      {
         PropertyChange.newUpdate("override_update", Arrays.asList("override_update_clone_value_2")),
         PropertyChange.newReset("override_reset"),
         PropertyChange.newUpdate("override_create", Arrays.asList("override_create_clone_value_2")),
         PropertyChange.newUpdate("dynamic_update", Arrays.asList("dynamic_update_clone_value_2")),
         PropertyChange.newReset("dynamic_reset"),
         PropertyChange.newUpdate("dynamic_create", Arrays.asList("dynamic_create_clone_value_2")),
         PropertyChange.newUpdate("readonly_create", Arrays.asList("readonly_create_clone_value_2")),
      };
      ccpCtx = setProperties(ccpCtx, changes);

      //
      expectedProps.clear();
      expectedProps.setProperty("override_update", Arrays.asList("override_update_clone_value_2"));
      expectedProps.setProperty("override_create", Arrays.asList("override_create_clone_value_2"));
      expectedProps.setProperty("override_reset", Arrays.asList("override_reset_portlet_value"));
      expectedProps.setProperty("dynamic_update", Arrays.asList("dynamic_update_clone_value_2"));
      expectedProps.setProperty("dynamic_create", Arrays.asList("dynamic_create_clone_value_2"));
      expectedProps.setProperty("readonly_create", Arrays.asList("readonly_create_portlet_value"));
      ccpProps = getProperties(ccpCtx);
      ValueMapAssert.assertEquals(expectedProps, ccpProps);
   }

   @Test
   public void testInvokeCloneBeforeWritePOPWithUpdate() throws Exception
   {
      invokeCloneBeforeWriteWithUpdate(true);
   }

   @Test
   public void testInvokeCloneBeforeWriteCCPWithUpdate() throws Exception
   {
      invokeCloneBeforeWriteWithUpdate(false);
   }

   public void invokeCloneBeforeWriteWithUpdate(boolean pop) throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();

      PortletSupport.InvocationHandler handler = new PortletSupport.InvocationHandler()
      {
         public PortletInvocationResponse invoke(PortletInvocation invocation)
         {
            AbstractPropertyContext props = (AbstractPropertyContext)invocation.getAttribute(PropertyContext.PREFERENCES_ATTRIBUTE);
            props.update(new PropertyChange[]{PropertyChange.newUpdate("abc", Arrays.asList("_def"))});
            return null;
         }
      };

      PortletContext ctx;
      if (pop)
      {
         PortletContext popCtx = createPOPRef(info);
         PortletSupport portletSupport = getPortletSupport(popCtx);
         portletSupport.addHandler(handler);
         addPreference(popCtx, "abc", Arrays.asList("def"));
         ctx = popCtx;
      }
      else
      {
         PortletContext popCtx = createPOPRef(info);
         PortletSupport portletSupport = getPortletSupport(popCtx);
         portletSupport.addHandler(handler);
         addPreference(popCtx, "abc", Arrays.asList("def"));
         PortletContext ccpCtx = createClone(popCtx);
         ctx = ccpCtx;
      }

      //
      ActionInvocation invocation = createAction(ctx, AccessMode.CLONE_BEFORE_WRITE);
      invoke(invocation);

      //
      PortletContext cloneRef = getImplicitClonedRef(invocation);
      assertNotNull(cloneRef);

      //
      PropertyMap blah = getProperties(cloneRef);
      PropertyMap expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("_def"));
      ValueMapAssert.assertEquals(expectedProps, blah);

      // Now we test the clone destruction
      destroyClone(cloneRef);

      // Assert clone ref does not exist anymore
      assertCloneDoesNotExist(cloneRef);
   }

   @Test
   public void testInvokeReadOnlyWithUpdate() throws Exception
   {
      final Boolean[] ise = {Boolean.FALSE};

      PortletInfoSupport info = new PortletInfoSupport();

      PortletSupport.InvocationHandler handler = new PortletSupport.InvocationHandler()
      {
         public PortletInvocationResponse invoke(PortletInvocation invocation)
         {
            try
            {
               AbstractPropertyContext props = (AbstractPropertyContext)invocation.getAttribute(PropertyContext.PREFERENCES_ATTRIBUTE);
               props.update(new PropertyChange[]{PropertyChange.newUpdate("abc", Arrays.asList("_def"))});
               return null;
            }
            catch (IllegalStateException e)
            {
               ise[0] = Boolean.TRUE;
               return null;
            }
         }
      };

      //
      PortletContext popCtx = createPOPRef(info);
      PortletSupport portletSupport = getPortletSupport(popCtx);
      portletSupport.addHandler(handler);
      addPreference(popCtx, "abc", Arrays.asList("def"));

      //
      ActionInvocation invocation = createAction(popCtx, AccessMode.READ_ONLY);
      invoke(invocation);

      //
//      Object cloneRef = instanceCtx.getPortletRef();
//      assertNull(cloneRef);
      assertEquals(Boolean.TRUE, ise[0]);
   }

   @Test
   public void testInvokeReadWriteWithUpdate() throws Exception
   {
      PortletInfoSupport info = new PortletInfoSupport();
      PortletSupport.InvocationHandler handler = new PortletSupport.InvocationHandler()
      {
         public PortletInvocationResponse invoke(PortletInvocation invocation)
         {
            AbstractPropertyContext props = (AbstractPropertyContext)invocation.getAttribute(PropertyContext.PREFERENCES_ATTRIBUTE);
            props.update(new PropertyChange[]{PropertyChange.newUpdate("abc", Arrays.asList("_def"))});
            return null;
         }
      };
      PortletContext popCtx = createPOPRef(info);
      PortletSupport portletSupport = getPortletSupport(popCtx);
      portletSupport.addHandler(handler);
      addPreference(popCtx, "abc", Arrays.asList("def"));
      
      //
      PortletContext ccpCtx = createClone(popCtx);

      //
      ActionInvocation invocation = createAction(ccpCtx, AccessMode.READ_WRITE);
      invoke(invocation);

      //
      ccpCtx = getModifiedPortletRef(invocation);
      assertNotNull(ccpCtx);

      //
      PropertyMap blah = getProperties(ccpCtx);
      PropertyMap expectedProps = new SimplePropertyMap();
      expectedProps.setProperty("abc", Arrays.asList("_def"));
      ValueMapAssert.assertEquals(expectedProps, blah);
   }

   private void _assertEquals(PortletContext expected, PortletContext actual)
   {
      if (expected == null)
      {
         assertNull("Portlet context should be null", actual);
      }
      else
      {
         assertNotNull("Portlet context should not be null but rather equals to " + expected, actual);

         // Test handle first
         assertEquals(expected.getId(), actual.getId());

         // Compare States
         Object expectedState = null;
         Object actualState = null;
         
         if (expected instanceof StatefulPortletContext)
         {  
            expectedState = ((StatefulPortletContext)expected).getState();
         }
         
         if (actual instanceof StatefulPortletContext)
         {
            actualState = ((StatefulPortletContext)actual).getState();
         }
         
         if (expectedState == null)
         {
            assertNull("Actual state should be null", actualState);
         }
         else
         {
            assertNotNull("Actual state should be not null", actualState);
            assertTrue(expectedState.equals(actualState));
         }
      }
   }
}
