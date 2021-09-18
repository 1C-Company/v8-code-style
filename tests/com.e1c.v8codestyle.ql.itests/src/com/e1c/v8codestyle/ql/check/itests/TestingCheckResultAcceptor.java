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
/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.e1c.g5.v8.dt.check.ICheckResultAcceptor;
import com.e1c.g5.v8.dt.check.Issue;

/**
 * @author Dmitriy Marmyshev
 *
 */
class TestingCheckResultAcceptor
    implements ICheckResultAcceptor
{
    final Map<Object, Collection<Issue>> issues = new HashMap<>();
    Iterable<?> iterable;

    @Override
    public void addIssue(Object object, Issue issue)
    {
        issues.computeIfAbsent(object, k -> new ArrayList<>()).add(issue);
    }

    @Override
    public void delegateChecks(Iterable<?> paramIterable)
    {
        this.iterable = paramIterable;
    }

}
