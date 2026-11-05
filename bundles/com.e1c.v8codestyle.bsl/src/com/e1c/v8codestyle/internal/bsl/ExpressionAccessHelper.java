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
package com.e1c.v8codestyle.internal.bsl;

import java.util.Optional;

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;

/**
 * The expression access helper.
 *
 * @author Artem Samohvalov
 */
public class ExpressionAccessHelper
{

    /**
     * Returns the first (leftmost) static feature access of the expression chain.
     * SomeModule.SomeVariable[0].Add() => SomeModule
     *
     * @param expression the expression
     * @return the base variable name
     */
    public static Optional<StaticFeatureAccess> getBaseFeatureAccess(Expression expression)
    {
        Expression current = expression;

        while (current != null)
        {
            if (current instanceof StaticFeatureAccess staticAccess)
            {
                return Optional.of(staticAccess);
            }

            if (current instanceof DynamicFeatureAccess dynamicAccess)
            {
                current = dynamicAccess.getSource();
            }
            else if (current instanceof IndexAccess indexAccess)
            {
                current = indexAccess.getSource();
            }
            else
            {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private ExpressionAccessHelper()
    {
    }
}
