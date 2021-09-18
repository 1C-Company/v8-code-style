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

import static com._1c.g5.v8.dt.bsl.common.Symbols.AFTER;
import static com._1c.g5.v8.dt.bsl.common.Symbols.AFTER_RUS;
import static com._1c.g5.v8.dt.bsl.common.Symbols.AROUND;
import static com._1c.g5.v8.dt.bsl.common.Symbols.AROUND_RUS;
import static com._1c.g5.v8.dt.bsl.common.Symbols.BEFORE;
import static com._1c.g5.v8.dt.bsl.common.Symbols.BEFORE_RUS;
import static com._1c.g5.v8.dt.bsl.common.Symbols.CHANGE_AND_VALIDATE;
import static com._1c.g5.v8.dt.bsl.common.Symbols.CHANGE_AND_VALIDATE_RUS;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA__SYMBOL;

import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.Pragma;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Checks the canonical spelling of method pragmas in extensions
 *
 * @author Aleksandr Kapralov
 */
public class CanonicalPragmaCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "bsl-canonical-pragma"; //$NON-NLS-1$

    private static final Map<String, String> PRAGMAS;

    static
    {
        final Map<String, String> pragmas = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        pragmas.put(BEFORE, BEFORE);
        pragmas.put(BEFORE_RUS, BEFORE_RUS);
        pragmas.put(AFTER, AFTER);
        pragmas.put(AFTER_RUS, AFTER_RUS);
        pragmas.put(AROUND, AROUND);
        pragmas.put(AROUND_RUS, AROUND_RUS);
        pragmas.put(CHANGE_AND_VALIDATE, CHANGE_AND_VALIDATE);
        pragmas.put(CHANGE_AND_VALIDATE_RUS, CHANGE_AND_VALIDATE_RUS);
        PRAGMAS = pragmas;
    }

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

        String canonicalSymbol = PRAGMAS.get(pragma.getSymbol());
        if (canonicalSymbol == null)
        {
            return;
        }

        // Case sensitive string comparison
        if (!pragma.getSymbol().equals(canonicalSymbol))
        {
            String errorMessage = MessageFormat
                .format(Messages.CanonicalPragmaCheck_Pragma_0_is_not_written_canonically, pragma.getSymbol());
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
            .module()
            .checkedObjectType(PRAGMA);
    }

}
