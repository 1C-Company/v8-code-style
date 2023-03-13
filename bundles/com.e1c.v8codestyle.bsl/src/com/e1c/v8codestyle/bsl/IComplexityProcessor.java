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
 *     Manaev Konstantin - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Method;

/**
 * The processor of method's complexity.
 * This processor compute complexity score by method.
 *
 *
 * @author Manaev Konstantin
 */
public interface IComplexityProcessor
{

    /**
     * Compute complexity score by method.
     *
     * @param method the method, cannot be {@code null}.
     * @param monitor the progress monitor, cannot be {@code null}.
     * @return score of complexity
     */
    int compute(Method method, IProgressMonitor monitor);
}
