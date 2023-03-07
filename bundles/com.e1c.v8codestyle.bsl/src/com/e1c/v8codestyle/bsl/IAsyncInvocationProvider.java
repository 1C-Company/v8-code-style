/**
 * Copyright (C) 2023, 1C
 */
package com.e1c.v8codestyle.bsl;

import java.util.Collection;
import java.util.Map;

import com._1c.g5.v8.dt.platform.version.Version;

/**
 * Platform context asynchronic methods provider
 *
 * @author Artem Iliukhin
 */
public interface IAsyncInvocationProvider
{

    // Global context methods
    Collection<String> getAsyncInvocationNames(Version version);

    // Methods with a list of types in which they are used
    Map<String, Collection<String>> getAsyncTypeMethodNames(Version version);

}
