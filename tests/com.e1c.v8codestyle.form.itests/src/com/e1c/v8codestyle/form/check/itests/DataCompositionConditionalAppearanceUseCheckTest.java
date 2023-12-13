package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.dcs.model.settings.DataCompositionConditionalAppearance;
import com._1c.g5.v8.dt.dcs.model.settings.DataCompositionSettings;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.DataCompositionConditionalAppearanceUseCheck;

/**
 * Tests for {@link DataCompositionConditionalAppearanceUseCheck} check.
 *
 * @author Vadim Goncharov
 */
public class DataCompositionConditionalAppearanceUseCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "data-composition-conditional-appearance-use";
    private static final String PROJECT_NAME = "DataCompositionConditionalAppearanceUse";
    private static final String FQN_DL1 =
        "Catalog.TestCatalog1.Form.ListForm.Form.Attributes.List.ExtInfo.ListSettings";
    private static final String FQN_DL2 =
        "Catalog.TestCatalog2.Form.ListForm.Form.Attributes.List.ExtInfo.ListSettings";
    private static final String FQN_FORM1 = "Catalog.TestCatalog3.Form.ItemForm.Form.ConditionalAppearance";
    private static final String FQN_FORM2 = "Catalog.TestCatalog4.Form.ItemForm.Form.ConditionalAppearance";

    /**
     * Test the Dynamic List use the conditional appearance.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicListUseConditionalAppearance() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_DL1, dtProject);
        assertTrue(object instanceof DataCompositionSettings);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the Dynamic List do not use the conditional appearance.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicListDoNotUseConditionalAppearance() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_DL2, dtProject);
        assertTrue(object instanceof DataCompositionSettings);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNull(marker);
    }

    /**
     * Test the form use the conditional appearance.
     *
     * @throws Exception the exception
     */
    @Test
    @Ignore("G5V8DT-24042")
    public void testFormUseConditionalAppearance() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM1, dtProject);
        assertTrue(object instanceof DataCompositionConditionalAppearance);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNotNull(marker);
    }

    /**
     * Test the form don't use the conditional appearance.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFormDontUseConditionalAppearance() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM2, dtProject);
        assertNull(object);
    }

}
