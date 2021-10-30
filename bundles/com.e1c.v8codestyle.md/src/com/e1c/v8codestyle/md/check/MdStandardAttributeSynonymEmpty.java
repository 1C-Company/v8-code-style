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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EStructuralFeature;

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
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

public class MdStandardAttributeSynonymEmpty
    extends BasicCheck
{
    private static final String CHECK_ID = "md-owner-attribute-synonym-empty"; //$NON-NLS-1$
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

    private boolean hasOwner(Object object)
    {
        return !((Catalog)object).getOwners().isEmpty();
    }

    private boolean isHierarchical(Object object)
    {
        return ((Catalog)object).isHierarchical();
    }

    private StandardAttribute getStandardAttributeByName(Object object, String attributeName)
    {
        EList<StandardAttribute> standardAttributes = ((Catalog)object).getStandardAttributes();
        for (StandardAttribute standardAttribute : standardAttributes)
        {
            if (standardAttribute.getName().compareTo(attributeName) == 0)
            {
                return standardAttribute;
            }
        }
        return null;
    }

    private String getSynonym(StandardAttribute ownerAttribute, String languageCode)
    {
        return ownerAttribute.getSynonym().get(languageCode);
    }

    private void checkParent(Object object, ResultAcceptor resultAceptor, String languageCode)
    {
        // Не проверяем если справочник не иерархический
        if (!isHierarchical(object))
        {
            return;
        }

        String message = Messages.MdOwnerAttributeSynonymEmpty_ErrorMessage;
        EStructuralFeature feature = BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;

        StandardAttribute parentAttribute = getStandardAttributeByName(object, PARENT_NAME);

        // parentAttribute равно null если в стандартных атрибутах ничего не устанавливали владельцу
        // если не null, смотрим на синоним
        if (parentAttribute == null || StringUtils.isBlank(getSynonym(parentAttribute, languageCode)))
        {
            resultAceptor.addIssue(message, feature);
        }
    }

    private void checkOwner(Object object, ResultAcceptor resultAceptor, String languageCode)
    {
        // Если нет списка владельцев, то проверять нечего
        if (!hasOwner(object))
        {
            return;
        }

        String message = Messages.MdOwnerAttributeSynonymEmpty_ErrorMessage;
        EStructuralFeature feature = BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;

        StandardAttribute ownerAttribute = getStandardAttributeByName(object, OWNER_NAME);

        // ownerAttribute равно null если в стандартных атрибутах ничего не устанавливали владельцу
        // если не null, смотрим на синоним
        if (ownerAttribute == null || StringUtils.isBlank(getSynonym(ownerAttribute, languageCode)))
        {
            resultAceptor.addIssue(message, feature);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdOwnerAttributeSynonymEmpty_Title)
            .description(Messages.MdOwnerAttributeSynonymEmpty_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .topObject(CATALOG)
            .checkTop()
            .features(BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
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

        checkParent(object, resultAceptor, languageCode);
        checkOwner(object, resultAceptor, languageCode);
    }
}
