/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogForm;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.ModuleStructureEventFormRegionsCheck;

/**
 * Tests for {@link ModuleStructureEventFormRegionsCheck} check.
 *
 * @author Artem Iliukhin
 *
 */
public class ModuleStructureEventFormRegionsCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CHECK_ID = "module-structure-form-event-regions"; //$NON-NLS-1$
    private static final String PROJECT_NAME = "ModuleStructureEventFormRegionsCheck";

    private static final String FQN_CATALOG = "Catalog.Catalog";
    private static final String FQN_CATALOG_WRONG_REGION = "Catalog.CatalogInWrongRegion";
    private static final String FQN_CATALOG_WRONG_METHOD = "Catalog.CatalogInWrongMethod";

    private static final String FQN_CATALOG_FIELD = "Catalog.CatalogField";
    private static final String FQN_CATALOG_FIELD_WRONG_REGION = "Catalog.CatalogFieldInWrongRegion";
    private static final String FQN_CATALOG_FIELD_WRONG_METHOD = "Catalog.CatalogFieldInWrongMethod";

    private static final String FQN_CATALOG_COMMAND = "Catalog.CatalogCommand";
    private static final String FQN_CATALOG_COMMAND_WRONG_REGION = "Catalog.CatalogCommandInWrongRegion";
    private static final String FQN_CATALOG_COMMAND_WRONG_METHOD = "Catalog.CatalogCommandInWrongMethod";

    private static final String FQN_CATALOG_TABLE = "Catalog.CatalogTable";
    private static final String FQN_CATALOG_TABLE_WRONG_REGION = "Catalog.CatalogTableInWrongRegion";
    private static final String FQN_CATALOG_TABLE_WRONG_METHOD = "Catalog.CatalogTableInWrongMethod";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testFormEventInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }

    @Test
    public void testFormEventInWrongRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_WRONG_REGION, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormEventInWrongMethod() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_WRONG_METHOD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormFieldEventInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_FIELD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }

    @Test
    public void testFormFieldEventInWrongRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_FIELD_WRONG_REGION, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormFieldEventInWrongMethod() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_FIELD_WRONG_METHOD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormCommandEventInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_COMMAND, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }

    @Test
    public void testFormCommandEventInWrongRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_COMMAND_WRONG_REGION, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormFieldCommandInWrongMethod() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_COMMAND_WRONG_METHOD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormTableEventInRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_TABLE, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNull(marker);
    }

    @Test
    public void testFormTableEventInWrongRegion() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_TABLE_WRONG_REGION, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

    @Test
    public void testFormTableCommandInWrongMethod() throws Exception
    {
        IDtProject dtProject = dtProjectManager.getDtProject(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject mdObject = getTopObjectByFqn(FQN_CATALOG_TABLE_WRONG_METHOD, dtProject);
        assertTrue(mdObject instanceof Catalog);

        List<CatalogForm> forms = ((Catalog)mdObject).getForms();
        assertEquals(forms.isEmpty(), false);

        CatalogForm form = forms.get(0);
        assertNotNull(form);

        Module module = form.getForm().getModule();
        assertNotNull(module);

        List<Method> methods = module.allMethods();
        assertFalse(methods.isEmpty());

        Method method = methods.get(0);
        assertNotNull(method);

        Marker marker = getFirstMarker(CHECK_ID, method, getProject());
        assertNotNull(marker);
    }

}
