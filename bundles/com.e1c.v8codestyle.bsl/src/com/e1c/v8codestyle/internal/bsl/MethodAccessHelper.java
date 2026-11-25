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

import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;

/**
 * The method access helper.
 *
 * @author Artem Samohvalov
 */
public final class MethodAccessHelper
{

    /**
     * Checks if is method name matching.
     *
     * @param invocation the invocation, cannot be {@code null}
     * @param methodNameRu the method name ru, cannot be {@code null}
     * @param anyModule the any module
     * @return true, if is name matching
     */
    public static boolean isNameMatching(Invocation invocation, String methodNameRu, String methodNameEn)
    {
        return invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccess
            && matchMethodName(staticAccess.getName(), methodNameRu, methodNameEn);
    }

    /**
     * Checks if is method and source name matching (SourceName.MethodName()).
     *
     * @param invocation the invocation, cannot be {@code null}
     * @param methodNameRu the method name ru, cannot be {@code null}
     * @param methodNameEn the method name en, cannot be {@code null}
     * @param sourceNameRu the module name ru, cannot be {@code null}
     * @param sourceNameEn the module name en, cannot be {@code null}
     * @return true, if is name matching
     */
    public static boolean isNameMatching(Invocation invocation, String methodNameRu, String methodNameEn,
        String sourceNameRu, String sourceNameEn)
    {
        return invocation.getMethodAccess() instanceof DynamicFeatureAccess dynamicAccess
            && dynamicAccess.getSource() instanceof StaticFeatureAccess staticAccess
            && matchMethodName(dynamicAccess.getName(), methodNameRu, methodNameEn)
            && matchMethodName(staticAccess.getName(), sourceNameRu, sourceNameEn);
    }

    private static boolean matchMethodName(String name, String methodNameRu, String methodNameEn)
    {
        return methodNameRu.equalsIgnoreCase(name) || methodNameEn.equalsIgnoreCase(name);
    }

    private MethodAccessHelper()
    {
    }
}
