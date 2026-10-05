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
package com.e1c.v8codestyle.bsl;

import java.util.function.Consumer;

import com._1c.g5.v8.dt.bsl.model.Block;
import com._1c.g5.v8.dt.bsl.model.Invocation;

/**
 * The method call (invocation) processor
 * 
 * @author Artem Samohvalov
 */
public class MethodCallProcessor
    extends BlockReferencesProcessor
{
    private final Consumer<Invocation> callBack;

    public MethodCallProcessor(Block block, Consumer<Invocation> callBack)
    {
        super(block);

        this.callBack = callBack;
    }

    @Override
    protected void doProcessInternal(Invocation invocation)
    {
        callBack.accept(invocation);

        super.doProcessInternal(invocation);
    }
}
