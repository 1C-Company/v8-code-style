/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.dcs.util.DcsUtil;
import com._1c.g5.v8.dt.ql.model.QuerySchema;
import com._1c.g5.v8.dt.ql.model.QuerySchemaSelectQuery;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck.QueryOwner;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.ql.check.TempTableHasIndex;

/**
 * Test {@link TempTableHasIndex} class that checks selection query that put to new temporary table and has indexes.
 *
 * @author Dmitriy Marmyshev
 * @author Vadim Goncharov
 */
public class TempTableHasIndexTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String PROJECT_NAME = "QlEmptyProject";

    private static final String FOLDER = "/resources/";

    private static final String PARAMETER_EXCLUDE_TABLE_NAME_PATTERN = "excludeObjectNamePattern";

    private static final String PARAMETER_MAX_TOP = "maxTop";

    private static final int MAX_TOP_DEFAULT = 1000;

    private TestingCheckResultAcceptor resultAcceptor;

    private TestingQlResultAcceptor qlResultAcceptor;

    private TestingCheckParameters defaultParameters;

    private TempTableHasIndex check;

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Before
    public void setupCheck() throws Exception
    {
        resultAcceptor = new TestingCheckResultAcceptor();
        qlResultAcceptor = new TestingQlResultAcceptor();
        defaultParameters = new TestingCheckParameters(
            Map.of(PARAMETER_EXCLUDE_TABLE_NAME_PATTERN, "", PARAMETER_MAX_TOP, MAX_TOP_DEFAULT));
        QlBasicDelegateCheck.setResultAcceptor((o, f) -> qlResultAcceptor);
        check = new TempTableHasIndex();
    }

    @Test
    public void testTempTableWithoutIndex() throws Exception
    {
        IDtProject project = dtProjectManager.getDtProject(PROJECT_NAME);

        String queryText =
            new String(getClass().getResourceAsStream(FOLDER + "temp-table-has-no-index.ql").readAllBytes(),
                StandardCharsets.UTF_8);

        QuerySchema querySchema = DcsUtil.getQuerySchema(queryText, project);
        assertNotNull(querySchema);
        assertEquals(2, querySchema.getQueries().size());

        QlBasicDelegateCheck.setOwner(new QueryOwner(querySchema, null));
        EObject selectQuery = querySchema.getQueries().get(1);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);

        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertTrue(qlResultAcceptor.getMarkers().isEmpty());

        selectQuery = querySchema.getQueries().get(0);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);
        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertFalse(qlResultAcceptor.getMarkers().isEmpty());

    }

    @Test
    public void testTempTableWithoutIndexWithRecordCount() throws Exception
    {
        IDtProject project = dtProjectManager.getDtProject(PROJECT_NAME);

        String queryText =
            new String(getClass().getResourceAsStream(FOLDER + "temp-table-has-no-index-top.ql").readAllBytes(),
                StandardCharsets.UTF_8);

        QuerySchema querySchema = DcsUtil.getQuerySchema(queryText, project);
        assertNotNull(querySchema);
        assertEquals(2, querySchema.getQueries().size());

        QlBasicDelegateCheck.setOwner(new QueryOwner(querySchema, null));
        EObject selectQuery = querySchema.getQueries().get(1);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);

        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertTrue(qlResultAcceptor.getMarkers().isEmpty());

        selectQuery = querySchema.getQueries().get(0);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);
        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertFalse(qlResultAcceptor.getMarkers().isEmpty());

        qlResultAcceptor.getMarkers().clear();
        TestingCheckParameters newParameters =
            new TestingCheckParameters(Map.of(PARAMETER_EXCLUDE_TABLE_NAME_PATTERN, "", PARAMETER_MAX_TOP, 110000));
        check.check(selectQuery, resultAcceptor, newParameters, new NullProgressMonitor());

        assertTrue(qlResultAcceptor.getMarkers().isEmpty());

    }

    @Test
    public void testTempTableWithIndex() throws Exception
    {
        IDtProject project = dtProjectManager.getDtProject(PROJECT_NAME);

        String queryText = new String(getClass().getResourceAsStream(FOLDER + "temp-table-has-index.ql").readAllBytes(),
            StandardCharsets.UTF_8);

        QuerySchema querySchema = DcsUtil.getQuerySchema(queryText, project);
        assertNotNull(querySchema);
        assertEquals(2, querySchema.getQueries().size());

        QlBasicDelegateCheck.setOwner(new QueryOwner(querySchema, null));
        EObject selectQuery = querySchema.getQueries().get(1);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);

        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertTrue(qlResultAcceptor.getMarkers().isEmpty());

        selectQuery = querySchema.getQueries().get(0);
        assertTrue(selectQuery instanceof QuerySchemaSelectQuery);
        check.check(selectQuery, resultAcceptor, defaultParameters, new NullProgressMonitor());

        assertTrue(qlResultAcceptor.getMarkers().isEmpty());
    }

}
