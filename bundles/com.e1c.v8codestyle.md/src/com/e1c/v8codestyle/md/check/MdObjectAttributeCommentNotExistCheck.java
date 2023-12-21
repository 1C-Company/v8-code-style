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
 *     Vadim Gocnharov - issue #487
 *******************************************************************************/

package com.e1c.v8codestyle.md.check;

import java.util.Set;
import java.util.TreeSet;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__ATTRIBUTES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT__ATTRIBUTES;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.DbObjectAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.Document;
import com._1c.g5.v8.dt.metadata.mdclass.DocumentAttribute;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check top Metadata object (Catalog or Document) have attribute named "Comment"
 *
 * @author Vadim Goncharov
 */
public class MdObjectAttributeCommentNotExistCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "md-object-attribute-comment-not-exist"; //$NON-NLS-1$

    public static final String PARAM_CHECK_DOCUMENTS = "checkDocuments"; //$NON-NLS-1$
    public static final String PARAM_CHECK_CATALOGS = "checkCatalogs"; //$NON-NLS-1$
    public static final String PARAM_ATTRIBUTE_NAMES_LIST = "attributesList"; //$NON-NLS-1$

    public static final String DEFAULT_CHECK_DOCUMENTS = Boolean.toString(true);
    public static final String DEFAULT_CHECK_CATALOGS = Boolean.toString(false);
    private static final Set<String> ATTRIBUTE_NAMES_LIST = Set.of("Комментарий", //$NON-NLS-1$
        "Comment"); //$NON-NLS-1$
    private static final String DELIMITER = ","; //$NON-NLS-1$
    public static final String DEFAULT_ATTRIBUTE_NAMES_LIST = String.join(DELIMITER, ATTRIBUTE_NAMES_LIST);

    public MdObjectAttributeCommentNotExistCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdObjectAttributeCommentNotExist_title)
            .description(Messages.MdObjectAttributeCommentNotExist_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(531, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(SkipAdoptedInExtensionMdObjectExtension.instance())
            .extension(new TopObjectFilterExtension())
            .parameter(PARAM_ATTRIBUTE_NAMES_LIST, String.class, DEFAULT_ATTRIBUTE_NAMES_LIST,
                Messages.MdObjectAttributeCommentNotExist_Param_Attribute_name_list);

        builder.topObject(DOCUMENT)
            .checkTop()
            .features(DOCUMENT__ATTRIBUTES)
            .parameter(PARAM_CHECK_DOCUMENTS, Boolean.class, DEFAULT_CHECK_DOCUMENTS,
                Messages.MdObjectAttributeCommentNotExist_Param_Check_Documents);

        builder.topObject(CATALOG)
            .checkTop()
            .features(CATALOG__ATTRIBUTES)
            .parameter(PARAM_CHECK_CATALOGS, Boolean.class, DEFAULT_CHECK_CATALOGS,
                Messages.MdObjectAttributeCommentNotExist_Param_Check_Catalogs);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        boolean attributeExist = false;

        boolean checkCatalogs = parameters.getBoolean(PARAM_CHECK_CATALOGS);
        boolean checkDocuments = parameters.getBoolean(PARAM_CHECK_DOCUMENTS);
        Set<String> attributeNamesList = getListOfAttributeNames(parameters);

        if (monitor.isCanceled())
        {
            return;
        }

        if (object instanceof Document && checkDocuments)
        {
            Document document = (Document)object;
            EList<DocumentAttribute> attributes = document.getAttributes();
            for (DbObjectAttribute attribute : attributes)
            {

                if (monitor.isCanceled())
                {
                    return;
                }

                if (isAttributeNamedComment(attribute, attributeNamesList))
                {
                    attributeExist = true;
                    break;
                }

            }

            if (!monitor.isCanceled() && !attributeExist)
            {
                resultAcceptor.addIssue(
                    Messages.MdObjectAttributeCommentNotExist_Md_Object_attribute_Comment_does_not_exist, object);
            }
        }
        else if (object instanceof Catalog && checkCatalogs)
        {
            Catalog catalog = (Catalog)object;
            EList<CatalogAttribute> attributes = catalog.getAttributes();
            for (DbObjectAttribute attribute : attributes)
            {

                if (monitor.isCanceled())
                {
                    return;
                }

                if (isAttributeNamedComment(attribute, attributeNamesList))
                {
                    attributeExist = true;
                    break;
                }
            }

            if (!monitor.isCanceled() && !attributeExist)
            {
                resultAcceptor.addIssue(
                    Messages.MdObjectAttributeCommentNotExist_Md_Object_attribute_Comment_does_not_exist, object);
            }
        }

    }

    private boolean isAttributeNamedComment(DbObjectAttribute attribute, Set<String> attributeNamesList)
    {
        return attributeNamesList.contains(attribute.getName());
    }

    private Set<String> getListOfAttributeNames(ICheckParameters parameters)
    {
        Set<String> attributeNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        String paramAttributesString = parameters.getString(PARAM_ATTRIBUTE_NAMES_LIST);
        Set<String> paramsAttributeNames = Set.of(paramAttributesString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$

        attributeNames.addAll(paramsAttributeNames);

        return attributeNames;
    }

}
