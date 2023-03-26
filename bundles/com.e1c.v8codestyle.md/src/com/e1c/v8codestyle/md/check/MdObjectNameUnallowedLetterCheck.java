/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__COMMENT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__SYNONYM;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check in Russian locale, names, synonyms and comments of metadata objects do not contain the letter "ё".
 *
 * @author Olga Bozhko
 */
public class MdObjectNameUnallowedLetterCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "mdo-ru-name-unallowed-letter"; //$NON-NLS-1$
    private static final String LANGUAGE_KEY_RU = "ru"; //$NON-NLS-1$
    private static final String UNALLOWED_LETTER = "ё"; //$NON-NLS-1$
    private static final String ISSUE_MESSAGE =
        Messages.MdObjectNameUnallowedLetterCheck_Ru_locale_unallowed_letter_used_for_name_synonym_or_comment;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdObjectNameUnallowedLetterCheck_title)
            .description(Messages.MdObjectNameUnallowedLetterCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(474, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .topObject(MD_OBJECT)
            .features(MD_OBJECT__NAME, MD_OBJECT__SYNONYM, MD_OBJECT__COMMENT);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        MdObject mdObject = (MdObject)object;
        if (hasUnallowedLetter(mdObject.getName()))
        {
            resultAceptor.addIssue(ISSUE_MESSAGE, MD_OBJECT__NAME);
        }
        if (hasUnallowedLetter(mdObject.getSynonym().get(LANGUAGE_KEY_RU)))
        {
            resultAceptor.addIssue(ISSUE_MESSAGE, MD_OBJECT__SYNONYM);
        }
        if (hasUnallowedLetter(mdObject.getComment()))
        {
            resultAceptor.addIssue(ISSUE_MESSAGE, MD_OBJECT__COMMENT);
        }
    }

    private static boolean hasUnallowedLetter(String testableString)
    {
        return testableString != null && testableString.contains(UNALLOWED_LETTER);

    }
}
