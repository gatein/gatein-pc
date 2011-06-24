/*
 * JBoss, a division of Red Hat
 * Copyright 2011, Red Hat Middleware, LLC, and individual
 * contributors as indicated by the @authors tag. See the
 * copyright.txt in the distribution for a full listing of
 * individual contributors.
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
package org.gatein.pc.test.portlet.state;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.gatein.common.util.Tools;
import org.gatein.common.i18n.LocalizedString;
import org.gatein.pc.api.InvalidPortletIdException;
import org.gatein.pc.api.NoSuchPortletException;
import org.gatein.pc.api.Portlet;
import org.gatein.pc.api.PortletContext;
import org.gatein.pc.api.PortletInvokerException;
import org.gatein.pc.api.PortletStateType;
import org.gatein.pc.api.StatefulPortletContext;
import org.gatein.pc.api.state.PropertyMap;
import org.gatein.pc.portlet.impl.state.StateConverterV0;
import org.gatein.pc.portlet.support.info.PortletInfoSupport;
import org.gatein.pc.portlet.support.PortletSupport;
import org.gatein.pc.api.info.MetaInfo;
import org.gatein.pc.api.invocation.ActionInvocation;
import org.gatein.pc.api.invocation.PortletInvocation;
import org.gatein.pc.api.invocation.response.PortletInvocationResponse;
import org.gatein.pc.portlet.state.AbstractPropertyContext;
import org.gatein.pc.portlet.state.StateConverter;
import org.gatein.pc.api.state.AccessMode;
import org.gatein.pc.api.state.DestroyCloneFailure;
import org.gatein.pc.api.state.PropertyChange;
import org.gatein.pc.api.state.PropertyContext;
import org.gatein.pc.portlet.state.SimplePropertyMap;
import org.gatein.pc.portlet.state.producer.PortletState;

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
public abstract class AbstractStatefulPortletInvokerTestCase extends TestCase
{
   public static final String PORTLET_ID = "/foo.PortletId";
   public static final String NON_EXISTING_PORTLET_ID = "/foo.NonExistingPortletId";
   public static final String INVALID_PORTLET_ID = "/foo.InvalidPortletId";

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
   protected abstract PortletContext exportPortletContext(PortletContext contextToImport) throws PortletInvokerException;

   /**
    *
    */
   protected abstract PortletContext importPortletContext(PortletContext contextToImport) throws PortletInvokerException;


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
            AssertionFailedError afe = new AssertionFailedError();
            afe.initCause(e);
            throw afe;
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
            AssertionFailedError afe = new AssertionFailedError();
            afe.initCause(e);
            throw afe;
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

   public void testDestroyPOP() throws Exception
   {
      PortletContext popCtx = createPOPRef();
      List failures = destroyClones(Collections.singletonList(popCtx));
      assertEquals(1, failures.size());
      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
      assertEquals(getPortletId(popCtx), failure.getPortletId());
   }

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
//      PortletContext ccpId = getProducer().wrapCCP(PORTLET_ID);
//      List failures = getProducer().destroyClones(Collections.singletonList(ccpId));
//      assertEquals(1, failures.size());
//      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
//      assertEquals(ccpId.getId(), failure.getPortletId());
//   }

   public void testDestroyNonExistingCCP() throws Exception
   {
      PortletContext ccpCtx = createNonExistingLocalCCPRef();
      List failures = destroyClones(Collections.singletonList(ccpCtx));
      assertEquals(1, failures.size());
      DestroyCloneFailure failure = (DestroyCloneFailure)failures.get(0);
      assertEquals(getPortletId(ccpCtx), failure.getPortletId());
   }

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

   public void testInvokeCloneBeforeWritePOPWithUpdate() throws Exception
   {
      invokeCloneBeforeWriteWithUpdate(true);
   }

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

   public void testExportNullPortletContext() throws Exception
   {
      try
      {
         exportPortletContext(null);
         fail("Was expecting an illegal arguement exception.");
      }
      catch (IllegalArgumentException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testExportsNonExisitngPOP() throws Exception
   {
      PortletContext popCTX = createNonExistingPOPRef();
      try
      {
         exportPortletContext(popCTX);
         fail("Was expecting a NoSuchPortletException.");
      }
      catch (NoSuchPortletException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testExportNonExisitngCCP() throws Exception
   {
      PortletContext ccpCTX = createNonExistingLocalCCPRef();
      try
      {
         exportPortletContext(ccpCTX);
         fail("Was expecting a NoSuchPortletException.");
      }
      catch (NoSuchPortletException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testExportInvalidPOP() throws Exception
   {
      PortletContext popCtx = createInvalidPOPRef();
      try
      {
         exportPortletContext(popCtx);
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException expected)
      {
      }
      assertNoExistingState();
   }

   public void testExportPortlet() throws Exception
   {
      PropertyMap expectedProperties = new SimplePropertyMap();
      expectedProperties.setProperty("abc", Arrays.asList("def"));

      PortletInfoSupport info = new PortletInfoSupport();
      info.getMeta().setDisplayName("MyPortlet");
      PortletContext popCtx = createPOPRef(info);

      PortletContext export0Ctx = exportPortletContext(popCtx);

      //Make sure we get back the ID for the original portlet
      assertEquals(PORTLET_ID, export0Ctx.getId());
      //check by doing an import
      checkWithImportPortlet(export0Ctx, popCtx, new SimplePropertyMap());


      //add a preference to the portlet to make it store a state
      addPreference(popCtx, "abc", Arrays.asList("def"));
      PortletContext export1Ctx = exportPortletContext(popCtx);

      //Make sure we get back the ID for the original portlet
      assertEquals(PORTLET_ID, export1Ctx.getId());
      //check by doing an import
      checkWithImportPortlet(export1Ctx, popCtx, expectedProperties);

      PortletContext ccp1Ctx = createClone(popCtx);
      PortletContext export2Ctx = exportPortletContext(ccp1Ctx);

      //Make sure we get back the ID for the original portlet
      assertEquals(PORTLET_ID, export2Ctx.getId());
      //Check by doing an import
      checkWithImportPortlet(export2Ctx, ccp1Ctx, expectedProperties);

      PortletContext ccp2Ctx = createClone(ccp1Ctx);
      //make sure that adding a property to the already cloned ccp1Ctx doesn't interfere with exports
      PropertyChange[] propertyChanges = new PropertyChange[1];
      propertyChanges[0] = PropertyChange.newUpdate("123", Arrays.asList("456"));
      ccp1Ctx = setProperties(ccp1Ctx, propertyChanges);
      assertTrue(getProperties(ccp1Ctx).containsKey("123"));
      assertFalse(getProperties(export2Ctx).containsKey("123"));

      PortletContext export3Ctx = exportPortletContext(ccp2Ctx);

      //Make sure we get back the ID for the original portlet
      assertEquals(PORTLET_ID, export3Ctx.getId());
      //Check by doing an import
      checkWithImportPortlet(export3Ctx, ccp2Ctx, expectedProperties);
   }

   protected void checkWithImportPortlet(PortletContext exportedPortletContext, PortletContext originalPortletContext, PropertyMap expectedProperties) throws Exception
   {
      PortletContext importedPortletContext = importPortletContext(exportedPortletContext);

      Portlet importedPortlet = getPortlet(importedPortletContext);

      PortletContext portletContext = importedPortlet.getContext();

      //check that the portlet info for the original and new imported portlet are the same (which is assumed to ensure that the contexts refer to the same portlet)
      assertEquals(getPortlet(originalPortletContext).getInfo(), getPortlet(portletContext).getInfo());

      if (originalPortletContext instanceof StatefulPortletContext)
      {
         StatefulPortletContext statefulExpected = (StatefulPortletContext)originalPortletContext;

         assertTrue(portletContext instanceof StatefulPortletContext);
         StatefulPortletContext statefulPortletContext = (StatefulPortletContext)portletContext;

         //Check that the states are the same
         StateConverter sc = new StateConverterV0();
         PortletState state = sc.unmarshall(PortletStateType.OPAQUE, (byte[])statefulPortletContext.getState());
         PortletState expectedState = sc.unmarshall(PortletStateType.OPAQUE, (byte[])statefulExpected.getState());

         assertEquals(expectedState.getPortletId(), state.getPortletId());
         assertEquals(expectedState.getProperties(), state.getProperties());
         assertEquals(expectedState.getTerminationTime(), state.getTerminationTime());
         assertEquals(expectedState.getClass(), state.getClass());
      }

      PropertyMap properties = getProperties(portletContext);
      assertEquals(expectedProperties, properties);
      assertEquals(getProperties(originalPortletContext), properties);

   }

   public void testImportNullPortletContext() throws Exception
   {
      try
      {
         importPortletContext(null);
         fail("Was expecting an illegal arguement exception.");
      }
      catch (IllegalArgumentException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testImportsNonExisitngPOP() throws Exception
   {
      PortletContext popCTX = createNonExistingPOPRef();
      try
      {
         importPortletContext(popCTX);
         fail("Was expecting a NoSuchPortletException.");
      }
      catch (NoSuchPortletException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testImportNonExisitngCCP() throws Exception
   {
      PortletContext ccpCTX = createNonExistingLocalCCPRef();
      try
      {
         importPortletContext(ccpCTX);
         fail("Was expecting a NoSuchPortletException.");
      }
      catch (NoSuchPortletException e)
      {
         //expected
      }
      assertNoExistingState();
   }

   public void testImportInvalidPOP() throws Exception
   {
      PortletContext popCtx = createInvalidPOPRef();
      try
      {
         importPortletContext(popCtx);
         fail("was expecting an InvalidPortletIdException");
      }
      catch (InvalidPortletIdException expected)
      {
      }
      assertNoExistingState();
   }

   public void testImport() throws Exception
   {
      //This will create the portlet into the container and check that it doesn't have any properties set
      PortletContext popCtx = createPOPRef(new PortletInfoSupport());
      assertTrue(getProperties(popCtx).isEmpty());

      //Create the state bytes manually and create the portletcontext.
      //Tests what happens if a stateful portlet is export on one machine and imported into another
      StateConverter sc = new StateConverterV0();
      PropertyMap propertyMap = new SimplePropertyMap();
      propertyMap.setProperty("test", Arrays.asList("123"));
      PortletState portletState = new PortletState(PORTLET_ID, propertyMap);
      byte[] stateBytes = sc.marshall(PortletStateType.OPAQUE, portletState);

      StatefulPortletContext portletContext = StatefulPortletContext.create(PORTLET_ID, PortletStateType.OPAQUE, stateBytes);

      //import portlet
      PortletContext importedPortletContext = importPortletContext(portletContext);

      //Make sure that this new portlet has the properties we want
      assertEquals(propertyMap, getProperties(importedPortletContext));
   }
}
