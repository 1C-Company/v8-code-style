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
 *     1C-Soft LLC - initial API and implementation
 *     Bombin Valentin - issue #462
 *******************************************************************************/

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__COMMENT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__SYNONYM;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * The check the {@link MdObject} has specified synonyms for owner and parent
 * for default language of the project.
 *
 * @author Bombin Valentin
 *
 */
public class MdCyrillicYoLetterInProperty
    extends BasicCheck
{
    private static final String CHECK_ID = "md-cyryllic-yo-letter"; //$NON-NLS-1$

    private static final String CYRILLIC_YO_LOWER = "ё"; //$NON-NLS-1$

    private static final String CYRILLIC_YO_UPPER = "Ё"; //$NON-NLS-1$

    private static final String LANG_RU = "ru"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public MdCyrillicYoLetterInProperty(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdCyrillicYoLetterInProperty_Title)
            .description(Messages.MdCyrillicYoLetterInProperty_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new TopObjectFilterExtension())
            .topObject(MD_OBJECT)
            .checkTop()
            .features(MD_OBJECT__NAME);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        MdObject mdObject = (MdObject)object;
        if (mdObject.getObjectBelonging() != ObjectBelonging.NATIVE)
        {
            return;
        }
        IV8Project project = v8ProjectManager.getProject(mdObject);
        String languageCode = project.getDefaultLanguage().getLanguageCode();
        if (monitor.isCanceled() || !languageCode.equals(LANG_RU))
        {
            return;
        }

        if (isContainsYoLetter(mdObject.getName()))
        {
            resultAceptor.addIssue(Messages.MdCyrillicYoLetterInProperty_Error, MD_OBJECT__NAME);

        }

        if (isContainsYoLetter(mdObject.getComment()))
        {
            resultAceptor.addIssue(Messages.MdCyrillicYoLetterInProperty_Error, MD_OBJECT__COMMENT);

        }

        String ruSynonym = mdObject.getSynonym().get(languageCode);
        if (ruSynonym != null && isContainsYoLetter(ruSynonym))
        {
            resultAceptor.addIssue(Messages.MdCyrillicYoLetterInProperty_Error, MD_OBJECT__SYNONYM);

        }
    }

    private boolean isContainsYoLetter(String text)
    {
        return StringUtils.isNotEmpty(text)
            && (text.indexOf(CYRILLIC_YO_UPPER) != -1 || text.indexOf(CYRILLIC_YO_LOWER) != -1);
    }

}
