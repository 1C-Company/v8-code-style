/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.check.MoneyStringLocalizationCheck;

/**
 * Tests for {@link MoneyStringLocalizationCheck} check.
 *
 * @author Ivan Sergeev
 */
public class MoneyStringLocalizationCheckTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String PROJECT_NAME = "MoneyStringLocalizationCheckTest";

    private static final String FORM_WITH_ISSUES = "CommonForm.Form.Form";

    private static final String FORM_CORRECT = "CommonForm.FormCorrect.Form";

    private static final String VALUE_TABLE_FORM_WITH_ISSUES = "CommonForm.FormValueTable.Form";

    private static final String VALUE_TABLE_FORM_CORRECT = "CommonForm.FormValueTableCorrect.Form";

    private static final String CHECK_ID = "money-string-localization";

    private static final String PROCEDURE = "Процедура1";

    private static final String PROCEDURE_INLINE_COLUMN = "ДобавлениеКолонкиИнлайн";

    private static final String PROCEDURE_COLUMN_VARIABLE = "ДобавлениеКолонкиЧерезПеременную";

    private static final String PROCEDURE_QUALIFIED_ACCESS = "ПрисваиваниеПоКвалифицированномуДоступу";

    private static final String PROCEDURE_OTHER_FIELD = "ДругоеПоле";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test number type description assigned to a money form attribute.
     *
     * @throws Exception the exception
     */
    @Test
    public void testWrongAssignment() throws Exception
    {
        SimpleStatement statement = getStatement(FORM_WITH_ISSUES, PROCEDURE, 0);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNotNull(marker);
    }

    /**
     * Test money field type description assigned to a money form attribute.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCorrectAssignment() throws Exception
    {
        SimpleStatement statement = getStatement(FORM_CORRECT, PROCEDURE, 0);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNull(marker);
    }

    /**
     * Test inline number type description of a value table money column.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInlineColumnType() throws Exception
    {
        SimpleStatement statement = getStatement(VALUE_TABLE_FORM_WITH_ISSUES, PROCEDURE_INLINE_COLUMN, 0);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNotNull(marker);
    }

    /**
     * Test inline money field type description of a value table money column.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInlineColumnTypeCorrect() throws Exception
    {
        SimpleStatement statement = getStatement(VALUE_TABLE_FORM_CORRECT, PROCEDURE_INLINE_COLUMN, 0);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNull(marker);
    }

    /**
     * Test number type description of a value table money column set from a variable.
     *
     * @throws Exception the exception
     */
    @Test
    public void testColumnTypeFromVariable() throws Exception
    {
        SimpleStatement assignment = getStatement(VALUE_TABLE_FORM_WITH_ISSUES, PROCEDURE_COLUMN_VARIABLE, 0);
        Marker marker = getFirstMarker(CHECK_ID, assignment, getProject());
        assertNotNull(marker);

        SimpleStatement columnAdd = getStatement(VALUE_TABLE_FORM_WITH_ISSUES, PROCEDURE_COLUMN_VARIABLE, 1);
        marker = getFirstMarker(CHECK_ID, columnAdd, getProject());
        assertNull(marker);
    }

    /**
     * Test money field type description of a value table money column set from a variable.
     *
     * @throws Exception the exception
     */
    @Test
    public void testColumnTypeFromVariableCorrect() throws Exception
    {
        SimpleStatement assignment = getStatement(VALUE_TABLE_FORM_CORRECT, PROCEDURE_COLUMN_VARIABLE, 0);
        Marker marker = getFirstMarker(CHECK_ID, assignment, getProject());
        assertNull(marker);

        SimpleStatement columnAdd = getStatement(VALUE_TABLE_FORM_CORRECT, PROCEDURE_COLUMN_VARIABLE, 1);
        marker = getFirstMarker(CHECK_ID, columnAdd, getProject());
        assertNull(marker);
    }

    /**
     * Test number type description assigned to a value table money column by qualified access.
     *
     * @throws Exception the exception
     */
    @Test
    public void testQualifiedAccessAssignment() throws Exception
    {
        SimpleStatement statement = getStatement(VALUE_TABLE_FORM_WITH_ISSUES, PROCEDURE_QUALIFIED_ACCESS, 1);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNotNull(marker);
    }

    /**
     * Test non money field is not reported.
     *
     * @throws Exception the exception
     */
    @Test
    public void testOtherFieldNotReported() throws Exception
    {
        SimpleStatement statement = getStatement(VALUE_TABLE_FORM_CORRECT, PROCEDURE_OTHER_FIELD, 0);
        Marker marker = getFirstMarker(CHECK_ID, statement, getProject());
        assertNull(marker);
    }

    private SimpleStatement getStatement(String fqn, String procedure, int index) throws Exception
    {
        IBmObject object = getTopObjectByFqn(fqn, getProject());
        assertNotNull(object);
        assertTrue(object instanceof Form);
        Module module = ((Form)object).getModule();
        assertNotNull(module);

        Method method = null;
        for (Method candidate : module.allMethods())
        {
            if (procedure.equalsIgnoreCase(candidate.getName()))
            {
                method = candidate;
                break;
            }
        }
        assertNotNull(method);

        List<Statement> statements = method.allStatements();
        assertTrue(statements.size() > index);
        return (SimpleStatement)statements.get(index);
    }
}
