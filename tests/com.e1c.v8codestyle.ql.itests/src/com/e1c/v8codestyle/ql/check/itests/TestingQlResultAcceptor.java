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
package com.e1c.v8codestyle.ql.check.itests;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.util.Triple;
import org.eclipse.xtext.util.Tuples;

import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck.IQlResultAcceptor;

/**
 * Testing result acceptor implementation for QL check tests.
 *
 * @author Dmitriy Marmyshev
 */
class TestingQlResultAcceptor
    implements IQlResultAcceptor
{

    final List<Triple<String, EObject, EStructuralFeature>> featuredMessages = new ArrayList<>();

    @Override
    public void addIssue(String message)
    {
        featuredMessages.add(Tuples.create(message, null, null));
    }

    @Override
    public void addIssue(String message, EObject target, EStructuralFeature feature)
    {
        featuredMessages.add(Tuples.create(message, target, feature));
    }

    @Override
    public void addIssue(String message, EObject target, EStructuralFeature manyFeature, int index)
    {
        featuredMessages.add(Tuples.create(message, target, manyFeature));
    }

    @Override
    public void addIssue(String message, int length)
    {
        featuredMessages.add(Tuples.create(message, null, null));
    }

    @Override
    public void addIssue(String message, int lineNumber, int offset, int length)
    {
        featuredMessages.add(Tuples.create(message, null, null));
    }

}
