/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.EventHandlerBooleanParamCheck;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Test {@link EventHandlerBooleanParamCheck} check that find problems of use boolean parameter.
 *
 * @author Dmitriy Marmyshev
 */
public class EventHandlerBooleanParamCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String CHECK_ID = "event-heandler-boolean-param";

    private static final String PARAM_CHECK_EVENT_ONLY = "checkEventOnly"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "EventHandlerBooleanParam";

    private static final String FQN_CATALOG_PRODUCTS = "Catalog.Products";

    private static final String FQN_CATALOG_PRODUCTS_FORM = "Catalog.Products.Form.ItemForm.Form";

    protected static final String FQN_COMMON_MODULE = "CommonModule.Common";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Check boolean parameter in Object module reserved event handlers that use is correct.
     * Both compliant and noncompliant parameters are renamed.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogObjectModuleEvents() throws Exception
    {
        enableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getObjectModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(1);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Manager module reserved event handlers that use is correct.
     * Compliant parameters are renamed and usual, equals standard event param name.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogManagerModuleEvents() throws Exception
    {
        enableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getManagerModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(1);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Common module event handlers of event subscription that use is correct.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCommonModuleEvents() throws Exception
    {
        enableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        // Noncompliant but disabled in check's parameters
        Method compliantMethod = methods.get(1);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Manager module reserved event handlers that use is correct.
     * Compliant parameters are renamed and usual, equals standard event param name.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogFormModuleEvents() throws Exception
    {
        enableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(2, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        assertTrue(noncompliantMethod.allStatements().get(1) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(1);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(15), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(2);
        assertEquals(2, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        assertTrue(compliantMethod.allStatements().get(1) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(1);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(3);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Object module reserved event handlers that use is correct.
     * Both compliant and noncompliant parameters are renamed.
     * This test disable check's parameter {@code checkEventOnly}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogObjectModuleEventsNotEventOnly() throws Exception
    {
        disableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getObjectModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(1);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Manager module reserved event handlers that use is correct.
     * Compliant parameters are renamed and usual, equals standard event param name.
     * This test disable check's parameter {@code checkEventOnly}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogManagerModuleEventsNotEventOnly() throws Exception
    {
        disableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS, dtProject);
        assertTrue(mdObject instanceof Catalog);
        Module module = ((Catalog)mdObject).getManagerModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(1);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Common module event handlers of event subscription that use is correct.
     * Also checks usual method which parameter name equals with checking name.
     * This test disable check's parameter {@code checkEventOnly}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCommonModuleEventsNotEventOnly() throws Exception
    {
        disableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(getTestConfigurationName());
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_COMMON_MODULE, dtProject);
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        // Noncompliant works only if enabled in check's parameters
        noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(12), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(2);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    /**
     * Check boolean parameter in Manager module reserved event handlers that use is correct.
     * Compliant parameters are renamed and usual, equals standard event param name.
     * This test disable check's parameter {@code checkEventOnly}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogFormModuleEventsNotEventOnly() throws Exception
    {
        disableCheckParameterEventOnly();
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_PRODUCTS_FORM, dtProject);
        assertTrue(mdObject instanceof Form);
        Module module = ((Form)mdObject).getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertEquals(4, methods.size());

        Method noncompliantMethod = methods.get(0);
        assertEquals(2, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        SimpleStatement statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        Marker marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        assertTrue(noncompliantMethod.allStatements().get(1) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(1);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        noncompliantMethod = methods.get(1);
        assertEquals(1, noncompliantMethod.allStatements().size());
        assertTrue(noncompliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)noncompliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNotNull(marker);
        assertEquals(Integer.valueOf(15), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));

        Method compliantMethod = methods.get(2);
        assertEquals(2, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        assertTrue(compliantMethod.allStatements().get(1) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(1);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

        compliantMethod = methods.get(3);
        assertEquals(1, compliantMethod.allStatements().size());
        assertTrue(compliantMethod.allStatements().get(0) instanceof SimpleStatement);
        statement = (SimpleStatement)compliantMethod.allStatements().get(0);

        marker = getFirstMarker(CHECK_ID, statement.getRight(), dtProject);
        assertNull(marker);

    }

    private void disableCheckParameterEventOnly() throws CoreException
    {
        setCheckParameterEventOnly(false);
    }

    private void enableCheckParameterEventOnly() throws CoreException
    {
        setCheckParameterEventOnly(true);
    }

    private void setCheckParameterEventOnly(boolean value) throws CoreException
    {

        IDtProject dtProject = dtProjectManager.getDtProject(getTestConfigurationName());
        IProject project = dtProject.getWorkspaceProject();
        ICheckSettings settings = checkRepository.getSettings(cuid(CHECK_ID), project);
        settings.getParameters().get(PARAM_CHECK_EVENT_ONLY).setValue(Boolean.toString(value));
        checkRepository.applyChanges(Collections.singleton(settings), project);
        waitForDD(dtProject);
    }

    private CheckUid cuid(String checkId)
    {
        return new CheckUid(checkId, BslPlugin.PLUGIN_ID);
    }

}
