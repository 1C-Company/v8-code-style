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
package com.e1c.v8codestyle.ql.itests;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.ql.check.CamelCaseStringLiteral;

/**
 * Test {@link CamelCaseStringLiteral} class that checks string literal contains only camel-case words
 * or non-word symbols.
 *
 * @author Dmitriy Marmyshev
 */
public class JoinToSubQueryTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "ql-join-to-sub-query"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "JoinToSubQuery";

    @Test
    public void testJoinToSubQuery() throws Exception
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);
        IBmObject object = getTopObjectByFqn("CommonForm.Form.Form", project);
        assertNotNull(object);
        Marker marker = getFirstNestedMarker(CHECK_ID, object.bmGetId(), project);
        assertNotNull(marker);
    }
}
