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
 *     Bombin Valentin - issue #119
 *******************************************************************************/

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.STANDARD_ATTRIBUTE__SYNONYM;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com._1c.g5.v8.dt.metadata.mdclass.StandardAttribute;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.inject.Inject;

/**
 * The check the {@link MdObject} has specified synonyms for owner and parent
 * for default language of the project.
 *
 * @author Bombin Valentin
 *
 */
public class MdStandardAttributeSynonymEmpty
    extends BasicCheck
{
    private static final String CHECK_ID = "md-standard-attribute-synonym-empty"; //$NON-NLS-1$
    private static final String OWNER_NAME = "Owner"; //$NON-NLS-1$
    private static final String PARENT_NAME = "Parent"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public MdStandardAttributeSynonymEmpty(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.MdOwnerAttributeSynonymEmpty_Title)
            .description(Messages.MdOwnerAttributeSynonymEmpty_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new TopObjectFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(CATALOG)
            .checkTop()
            .features(BASIC_DB_OBJECT__STANDARD_ATTRIBUTES, STANDARD_ATTRIBUTE__SYNONYM);
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
        if (monitor.isCanceled())
        {
            return;
        }
        checkParent((Catalog)object, resultAceptor, languageCode);
        checkOwner((Catalog)object, resultAceptor, languageCode);
    }

    private boolean hasAnyOwner(Catalog catalog)
    {
        return !catalog.getOwners().isEmpty();
    }

    private boolean hasParent(Catalog catalog)
    {
        return catalog.isHierarchical();
    }

    private StandardAttribute getStandardAttributeByName(Catalog catalog, String attributeName)
    {
        for (StandardAttribute attribute : catalog.getStandardAttributes())
        {
            if (attribute.getName().compareTo(attributeName) == 0)
            {
                return attribute;
            }
        }
        return null;
    }

    private String getSynonym(StandardAttribute attribute, String languageCode)
    {
        return attribute.getSynonym().get(languageCode);
    }

    private void checkParent(Catalog catalog, ResultAcceptor resultAceptor, String languageCode)
    {
        if (!hasParent(catalog))
        {
            return;
        }

        StandardAttribute attribute = getStandardAttributeByName(catalog, PARENT_NAME);

        if (attribute == null || StringUtils.isBlank(getSynonym(attribute, languageCode)))
        {
            resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_parent_ErrorMessage,
                BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
        }
    }

    private void checkOwner(Catalog catalog, ResultAcceptor resultAceptor, String languageCode)
    {
        if (!hasAnyOwner(catalog))
        {
            return;
        }

        StandardAttribute attribute = getStandardAttributeByName(catalog, OWNER_NAME);

        if (attribute == null || StringUtils.isBlank(getSynonym(attribute, languageCode)))
        {
            resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_owner_ErrorMessage,
                BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
        }
    }
}
