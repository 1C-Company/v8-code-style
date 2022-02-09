/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.qfix.external;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue.IssueImpl;

import com._1c.g5.v8.dt.bsl.validation.BslValidationUtil;
import com._1c.g5.v8.dt.validation.marker.IMarkerWrapper;
import com._1c.g5.v8.dt.validation.marker.PlainEObjectMarker;
import com.e1c.g5.v8.dt.check.qfix.IFixContextFactory;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Context factory for {@link SingleVariantXtextBslModuleFixContext}
 *
 * @author Vadim Geraskin
 */
@Singleton
public class SingleVariantXtextBslModuleFixContextFactory
    implements IFixContextFactory<SingleVariantXtextBslModuleFixContext>
{
    @Inject
    private IXtextBslModuleFixProvider provider;

    @Override
    public final SingleVariantXtextBslModuleFixContext createContext(IMarkerWrapper marker, IFixSession session)
    {
        if (marker.getMarker() instanceof PlainEObjectMarker)
        {
            PlainEObjectMarker plainObjectMarker = (PlainEObjectMarker)marker.getMarker();
            URI uri = plainObjectMarker.getURI();
            IssueImpl issue = BslValidationUtil.createIssue(plainObjectMarker, CheckType.EXPENSIVE);
            return new SingleVariantXtextBslModuleFixContext(uri, issue, provider, session.getDtProject());
        }
        return null;
    }

    @Override
    public final Class<SingleVariantXtextBslModuleFixContext> getProvidedContextType()
    {
        return SingleVariantXtextBslModuleFixContext.class;
    }
}
