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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA__SYMBOL;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.common.Symbols;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com._1c.g5.v8.dt.lcore.util.CaseInsensitiveString;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks the canonical spelling of method pragmas in extensions
 *
 * @author Aleksandr Kapralov
 */
public class CanonicalPragmaCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "bsl-canonical-pragma"; //$NON-NLS-1$

    private static final List<CaseInsensitiveString> PRAGMAS = List.copyOf(Symbols.ANNOTATION_SYMBOLS);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled())
        {
            return;
        }

        Pragma pragma = (Pragma)object;

        int pragmaIndex = PRAGMAS.indexOf(new CaseInsensitiveString(pragma.getSymbol()));
        if (pragmaIndex == -1)
        {
            return;
        }

        String canonicalSymbol = PRAGMAS.get(pragmaIndex).getString();

        // Case sensitive string comparison
        if (!pragma.getSymbol().equals(canonicalSymbol))
        {
            String errorMessage = MessageFormat.format(
                Messages.CanonicalPragmaCheck_Pragma_0_is_not_written_canonically_correct_spelling_is_1,
                pragma.getSymbol(), canonicalSymbol);
            resultAceptor.addIssue(errorMessage, pragma, PRAGMA__SYMBOL);
        }

    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.CanonicalPragmaCheck_title)
            .description(Messages.CanonicalPragmaCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.BLOCKER)
            .issueType(IssueType.ERROR)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(PRAGMA);
    }

}
