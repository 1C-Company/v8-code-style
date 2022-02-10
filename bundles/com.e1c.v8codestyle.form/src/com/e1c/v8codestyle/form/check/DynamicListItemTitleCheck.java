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
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.dcs.model.core.DcsPackage.Literals.LOCAL_STRING;
import static com._1c.g5.v8.dt.dcs.model.core.DcsPackage.Literals.PRESENTATION;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DATA_ITEM__DATA_PATH;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DYNAMIC_LIST_EXT_INFO;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_FIELD;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.TITLED__TITLE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.bm.core.BmUriUtil;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IDependentProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.dcs.model.core.Presentation;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchemaDataSetField;
import com._1c.g5.v8.dt.dcs.model.schema.DataSetField;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com._1c.g5.v8.dt.form.model.PropertyInfo;
import com._1c.g5.v8.dt.form.service.datasourceinfo.IDataSourceInfoAssociationService;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;
import com.e1c.g5.v8.dt.check.context.OnModelObjectAssociationContextCollector;
import com.e1c.g5.v8.dt.check.context.OnModelObjectRemovalContextCollector;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * The check find form items binded to dynamic list with custom field that should have set title.
 * Such field cannot be mapped to some MD object (or other type with standard presentation) to get synonym
 * and will show it's name to the user. So, form item or dynamic list field (since 8.3.19) must have title.
 *
 * @author Dmitriy Marmyshev
 */
public class DynamicListItemTitleCheck
    extends BasicCheck
{
    public static final String CHECK_ID = "form-dynamic-list-item-title"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    private final IDataSourceInfoAssociationService dataSourceInfoAssociationService;

    @Inject
    public DynamicListItemTitleCheck(IV8ProjectManager v8ProjectManager,
        IDataSourceInfoAssociationService dataSourceInfoAssociationService)
    {
        this.v8ProjectManager = v8ProjectManager;
        this.dataSourceInfoAssociationService = dataSourceInfoAssociationService;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DynamicListItemTitleCheck_title)
            .description(Messages.DynamicListItemTitleCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipBaseFormExtension())
            .extension(new DynamicListChangeExtension())
            .topObject(FORM)
            .containment(FORM_FIELD)
            .features(TITLED__TITLE, DATA_ITEM__DATA_PATH);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        FormField field = (FormField)object;
        AbstractDataPath dataPath = field.getDataPath();
        if (dataPath == null || dataPath.getSegments().size() != 2 || dataPath.getObjects().size() != 2)
        {
            return;
        }

        String languageCode = getDefaultLanguageCode(field);
        if (!isTitleEmpty(field.getTitle(), languageCode))
        {
            return;
        }

        Form form = (Form)((IBmObject)field).bmGetTopObject();
        if (!dataSourceInfoAssociationService.isRelatedDynamicList(form, dataPath)
            || !dataSourceInfoAssociationService.isPathResolved(form, dataPath) || monitor.isCanceled())
        {
            return;
        }

        PropertyInfo attribute = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath, 0);
        if (!isCustomQuery(attribute.getSource()) || monitor.isCanceled())
        {
            return;
        }
        DynamicListExtInfo custormQuery = (DynamicListExtInfo)((FormAttribute)attribute.getSource()).getExtInfo();

        String segment = dataPath.getSegments().get(1);
        DataPathReferredObject refObject = dataPath.getObjects().get(1);
        EObject source = refObject.getObject();

        if (isSourceUnknownOrSegmentNotEquals(segment, source)
            && isDcsFieldTitleIsEmpty(custormQuery, segment, languageCode))
        {
            resultAceptor.addIssue(Messages.DynamicListItemTitleCheck_message, field, TITLED__TITLE);
        }

    }

    private boolean isCustomQuery(Object source)
    {
        return source instanceof FormAttribute && ((FormAttribute)source).getExtInfo() instanceof DynamicListExtInfo
            && ((DynamicListExtInfo)((FormAttribute)source).getExtInfo()).isCustomQuery();
    }

    private String getDefaultLanguageCode(EObject context)
    {
        IV8Project project = v8ProjectManager.getProject(context);
        if (project.getDefaultLanguage() == null && project instanceof IDependentProject)
        {
            return ((IDependentProject)project).getParent().getDefaultLanguage().getLanguageCode();
        }
        else if (project.getDefaultLanguage() != null)
        {
            return project.getDefaultLanguage().getLanguageCode();
        }
        return null;
    }

    private boolean isTitleEmpty(EMap<String, String> title, String languageCode)
    {
        return title == null || languageCode != null && StringUtils.isBlank(title.get(languageCode));
    }

    private boolean isSourceUnknownOrSegmentNotEquals(String segment, EObject source)
    {
        return source == null
            || source instanceof NamedElement && !segment.equalsIgnoreCase(((NamedElement)source).getName());
    }

    private boolean isDcsFieldTitleIsEmpty(DynamicListExtInfo custormQuery, String segment, String languageCode)
    {
        for (DataSetField field : custormQuery.getFields())
        {
            if (field instanceof DataCompositionSchemaDataSetField
                && segment.equalsIgnoreCase(((DataCompositionSchemaDataSetField)field).getDataPath()))
            {
                Presentation title = ((DataCompositionSchemaDataSetField)field).getTitle();
                return title == null || StringUtils.isBlank(title.getValue()) && (title.getLocalValue() == null
                    || isTitleEmpty(title.getLocalValue().getContent(), languageCode));
            }
        }
        return true;
    }

    private static final class DynamicListChangeExtension
        implements IBasicCheckExtension
    {
        @Override
        public void configureContextCollector(ICheckDefinition definition)
        {
            OnModelObjectAssociationContextCollector newObjectCollector = (bmObject, bmEvent, contextSession) -> {
                IBmObject top = bmObject.bmGetTopObject();
                if (top instanceof Form)
                {
                    addItems(contextSession, (FormItemContainer)top);
                }
            };
            definition.addModelAssociationContextCollector(newObjectCollector, DYNAMIC_LIST_EXT_INFO);
            definition.addModelAssociationContextCollector(newObjectCollector, DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD);
            definition.addModelAssociationContextCollector(newObjectCollector, PRESENTATION);
            definition.addModelAssociationContextCollector(newObjectCollector, LOCAL_STRING);
            OnModelFeatureChangeContextCollector changeCollector = (bmObject, feature, bmEvent, contextSession) -> {
                IBmObject top = bmObject.bmGetTopObject();
                if (top instanceof Form)
                {
                    addItems(contextSession, (FormItemContainer)top);
                }
            };
            definition.addModelFeatureChangeContextCollector(changeCollector, DYNAMIC_LIST_EXT_INFO);
            definition.addModelFeatureChangeContextCollector(changeCollector, DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD);
            definition.addModelFeatureChangeContextCollector(changeCollector, PRESENTATION);
            definition.addModelFeatureChangeContextCollector(changeCollector, LOCAL_STRING);

            OnModelObjectRemovalContextCollector removeCollector =
                (removedObjectUri, removedObjectEClass, bmEvent, contextSession, transaction) -> {
                    URI topUri = removedObjectUri.trimFragment().appendFragment(BmUriUtil.TOP_OBJECT_PATH);
                    IBmObject top = transaction.getObjectByUri(topUri);
                    if (top instanceof Form)
                    {
                        addItems(contextSession, (FormItemContainer)top);
                    }
                };
            definition.addModelRemovalContextCollector(removeCollector, DYNAMIC_LIST_EXT_INFO);
        }

        private void addItems(CheckContextCollectingSession contextSession, FormItemContainer container)
        {
            for (FormItem item : container.getItems())
            {
                if (item instanceof FormField)
                {
                    contextSession.addModelCheck(item);
                }
                else if (item instanceof FormItemContainer)
                {
                    addItems(contextSession, (FormItemContainer)item);
                }
            }
        }
    }
}
