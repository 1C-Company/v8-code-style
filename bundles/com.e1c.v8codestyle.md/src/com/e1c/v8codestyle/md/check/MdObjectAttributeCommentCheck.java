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
 *     Vadim Goncharov - issue #133
 *******************************************************************************/

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE__MULTI_LINE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_FEATURE__TYPE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG_ATTRIBUTE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.DOCUMENT_ATTRIBUTE;

import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.mcore.StringQualifiers;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.BasicFeature;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.DefinedType;
import com._1c.g5.v8.dt.metadata.mdclass.DocumentAttribute;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * Check attributes of catalogs and documents that named "Comment".
 * The attribute must be of the unlimited string type. Multiline edit must be enabled.
 *
 * @author Vadim Goncharov
 */
public class MdObjectAttributeCommentCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "md-object-attribute-comment-incorrect-type"; //$NON-NLS-1$

    private static final String PARAM_CHECK_DOCUMENTS = "checkDocuments"; //$NON-NLS-1$
    private static final String PARAM_CHECK_CATALOGS = "checkCatalogs"; //$NON-NLS-1$
    private static final String PARAM_ATTRIBUTES_LIST = "attributesList"; //$NON-NLS-1$

    private static final String DEFAULT_CHECK_DOCUMENTS = Boolean.toString(true);
    private static final String DEFAULT_CHECK_CATALOGS = Boolean.toString(false);

    private static final Set<String> COMMENT_ATTRIBUTES_LIST = Set.of("Комментарий", //$NON-NLS-1$
        "Comment"); //$NON-NLS-1$
    private static final String DELIMITER = ","; //$NON-NLS-1$
    private static final String DEFAULT_ATTRIBUTES_LIST = String.join(DELIMITER, COMMENT_ATTRIBUTES_LIST);

    public MdObjectAttributeCommentCheck()
    {
        super();
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdObjectAttributeCommentCheck_title)
            .description(Messages.MdObjectAttributeCommentCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(531, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .extension(new TopObjectFilterExtension())
            .parameter(PARAM_ATTRIBUTES_LIST, String.class, DEFAULT_ATTRIBUTES_LIST,
                Messages.MdObjectAttributeCommentCheck_Attribute_list);

        builder.topObject(CATALOG)
            .containment(CATALOG_ATTRIBUTE)
            .features(BASIC_FEATURE__TYPE, BASIC_FEATURE__MULTI_LINE)
            .parameter(PARAM_CHECK_CATALOGS, Boolean.class, DEFAULT_CHECK_CATALOGS,
                Messages.MdObjectAttributeCommentCheck_Check_catalogs_param);

        builder.topObject(DOCUMENT)
            .containment(DOCUMENT_ATTRIBUTE)
            .features(BASIC_FEATURE__TYPE, BASIC_FEATURE__MULTI_LINE)
            .parameter(PARAM_CHECK_DOCUMENTS, Boolean.class, DEFAULT_CHECK_DOCUMENTS,
                Messages.MdObjectAttributeCommentCheck_Check_documents_param);

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

        boolean checkCatalogs = parameters.getBoolean(PARAM_CHECK_CATALOGS);
        boolean checkDocuments = parameters.getBoolean(PARAM_CHECK_DOCUMENTS);
        Set<String> attributeList = getListOfAttributes(parameters);

        BasicFeature attribute = (BasicFeature)object;
        String attributeName = attribute.getName();

        if (!attributeList.contains(attributeName))
        {
            return;
        }

        boolean isDocument = checkDocuments && isDocumentAttribute(object);
        boolean isCatalog = checkCatalogs && isCatalogAttribute(object);

        if (!monitor.isCanceled() && (isDocument || isCatalog))
        {
            checkAttribute(attribute, resultAceptor);
        }

    }

    private void checkAttribute(BasicFeature attribute, ResultAcceptor resultAceptor)
    {
        checkAttritubeType(attribute, resultAceptor);
        checkAttributeIsMultiline(attribute, resultAceptor);
    }

    private void checkAttritubeType(BasicFeature attribute, ResultAcceptor resultAceptor)
    {
        TypeDescription typeDesc = attribute.getType();
        if (McoreUtil.isCompoundType(typeDesc))
        {
            String msg = MessageFormat.format(Messages.MdObjectAttributeCommentCheck_message,
                Messages.MdObjectAttributeCommentCheck_Is_compound_type);
            resultAceptor.addIssue(msg, BASIC_FEATURE__TYPE);
            return;
        }

        TypeItem item = typeDesc.getTypes().get(0);
        if (IEObjectTypeNames.DEFINED_TYPE.equals(McoreUtil.getTypeCategory(item)))
        {
            EObject definedType = item.eContainer();
            while (definedType != null && !(definedType instanceof DefinedType))
            {
                definedType = definedType.eContainer();
            }
            if (definedType instanceof DefinedType)
            {
                typeDesc = ((DefinedType)definedType).getType();
            }
        }

        StringQualifiers qualifiers = typeDesc.getStringQualifiers();
        if (qualifiers == null)
        {
            String msg = MessageFormat.format(Messages.MdObjectAttributeCommentCheck_message,
                Messages.MdObjectAttributeCommentCheck_Not_a_String);
            resultAceptor.addIssue(msg, BASIC_FEATURE__TYPE);
            return;
        }

        if (qualifiers.getLength() != 0)
        {
            String msg = MessageFormat.format(Messages.MdObjectAttributeCommentCheck_message,
                Messages.MdObjectAttributeCommentCheck_String_is_not_unlimited);
            resultAceptor.addIssue(msg, BASIC_FEATURE__TYPE);
        }

    }

    private void checkAttributeIsMultiline(BasicFeature attribute, ResultAcceptor resultAceptor)
    {
        if (!attribute.isMultiLine())
        {
            String msg = MessageFormat.format(Messages.MdObjectAttributeCommentCheck_message,
                Messages.MdObjectAttributeCommentCheck_Multiline_edit_is_not_enabled);
            resultAceptor.addIssue(msg, BASIC_FEATURE__MULTI_LINE);
        }
    }

    private Set<String> getListOfAttributes(ICheckParameters parameters)
    {
        Set<String> attributes = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        String paramAttributesString = parameters.getString(PARAM_ATTRIBUTES_LIST);
        Set<String> paramsAttributes = Set.of(paramAttributesString.replace(" ", "").split(DELIMITER)); //$NON-NLS-1$ //$NON-NLS-2$

        attributes.addAll(paramsAttributes);

        return attributes;
    }

    private boolean isCatalogAttribute(Object object)
    {
        return object instanceof CatalogAttribute;
    }

    private boolean isDocumentAttribute(Object object)
    {
        return object instanceof DocumentAttribute;
    }

}
