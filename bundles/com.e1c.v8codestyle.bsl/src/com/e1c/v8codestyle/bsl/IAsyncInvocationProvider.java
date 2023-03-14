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
package com.e1c.v8codestyle.bsl;

import java.util.Collection;
import java.util.Map;

import com._1c.g5.v8.dt.platform.version.Version;

/**
 * Platform context asynchronous methods provider
 *
 * @author Artem Iliukhin
 */
public interface IAsyncInvocationProvider
{

    /**
     * Global context methods.
     *
     * @param version the version of platform
     * @return the asynchronous invocation names
     */
    Collection<String> getAsyncInvocationNames(Version version);

    /**
     * Methods with a list of types in which they are used.
     *
     * @param version the version of platform
     * @return the asynchronous type method names
     */
    Map<String, Collection<String>> getAsyncTypeMethodNames(Version version);

}
