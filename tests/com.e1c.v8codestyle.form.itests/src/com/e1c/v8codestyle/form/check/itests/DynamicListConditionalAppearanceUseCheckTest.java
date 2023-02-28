package com.e1c.v8codestyle.form.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.form.check.DynamicListConditionalAppearanceUseCheck;

/**
 * Tests for {@link DynamicListConditionalAppearanceUseCheck} check.
 *
 * @author Vadim Goncharov
 */
public class DynamicListConditionalAppearanceUseCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "form-dynamic-list-conditional-appearance-use";
    private static final String PROJECT_NAME = "DynamicListConditionalAppearanceUse";
    private static final String FQN_FORM1 = "Catalog.TestCatalog1.Form.ListForm.Form";
    private static final String FQN_FORM2 = "Catalog.TestCatalog2.Form.ListForm.Form";
    
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

        IBmObject object = getTopObjectByFqn(FQN_FORM1, dtProject);
        assertTrue(object instanceof Form);

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

        IBmObject object = getTopObjectByFqn(FQN_FORM2, dtProject);
        assertTrue(object instanceof Form);

        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), dtProject);
        assertNull(marker);
    }

}
