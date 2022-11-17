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
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DATA_ITEM__TITLE_LOCATION;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.DYNAMIC_LIST_EXT_INFO;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_FIELD;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_FIELD__TYPE;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.TITLED__TITLE;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

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
import com._1c.g5.v8.dt.form.model.FormElementTitleLocation;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.FormItemContainer;
import com._1c.g5.v8.dt.form.model.ManagedFormFieldType;
import com._1c.g5.v8.dt.form.model.PropertyInfo;
import com._1c.g5.v8.dt.form.service.datasourceinfo.IDataSourceInfoAssociationService;
import com._1c.g5.v8.dt.mcore.DuallyNamedElement;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.metadata.dbview.DbViewElement;
import com._1c.g5.v8.dt.metadata.dbview.DbViewFieldFieldDef;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.ql.model.DbViewFieldFieldDefFromQuery;
import com._1c.g5.v8.dt.ql.model.QuerySchemaExpression;
import com._1c.g5.v8.dt.ql.model.QuerySchemaOperator;
import com._1c.g5.v8.dt.ql.model.QuerySchemaSelectQuery;
import com._1c.g5.v8.dt.ql.model.StarExpression;
import com._1c.g5.v8.dt.ql.resource.QlMapper;
import com._1c.g5.v8.dt.ql.typesystem.IDynamicDbViewFieldComputer;
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

    private final QlMapper qlMapper;

    private final IDynamicDbViewFieldComputer dynamicDbViewFieldComputer;

    @Inject
    public DynamicListItemTitleCheck(IV8ProjectManager v8ProjectManager,
        IDataSourceInfoAssociationService dataSourceInfoAssociationService, QlMapper qlMapper,
        IDynamicDbViewFieldComputer dynamicDbViewFieldComputer)
    {
        this.v8ProjectManager = v8ProjectManager;
        this.dataSourceInfoAssociationService = dataSourceInfoAssociationService;
        this.qlMapper = qlMapper;
        this.dynamicDbViewFieldComputer = dynamicDbViewFieldComputer;
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
            .extension(new StandardCheckExtension(765, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipBaseFormExtension())
            .extension(new DynamicListChangeExtension())
            .topObject(FORM)
            .containment(FORM_FIELD)
            .features(TITLED__TITLE, DATA_ITEM__DATA_PATH, DATA_ITEM__TITLE_LOCATION, FORM_FIELD__TYPE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        FormField field = (FormField)object;
        AbstractDataPath dataPath = field.getDataPath();
        if (field.getTitleLocation() == FormElementTitleLocation.NONE
            || field.getType() == ManagedFormFieldType.PICTURE_FIELD || dataPath == null
            || dataPath.getSegments().size() != 2 || dataPath.getObjects().size() != 2)
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
        PropertyInfo fieldAttribute = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath, 1);

        if (!isSourceKnownAndSegmentEquals(segment, source, fieldAttribute)
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

    private boolean isSourceKnownAndSegmentEquals(String segment, EObject source, PropertyInfo attribute)
    {
        if (isNameEquals(source, segment))
        {
            return true;
        }

        if (attribute != null)
        {
            // no alias or alias is equals to table field name
            if (isNameEquals(attribute.getSource(), segment))
            {
                Object querySource = attribute.getSource();
                if (querySource instanceof DbViewFieldFieldDefFromQuery)
                {
                    EObject mdObject = ((DbViewFieldFieldDefFromQuery)querySource).getMdObject();
                    return mdObject != null
                        && isNameEquals(((DbViewFieldFieldDefFromQuery)querySource).getMdObject(), segment)
                        || mdObject == null && isSelectAllQuery((DbViewFieldFieldDefFromQuery)querySource)
                        || mdObject == null && isNameEquals(getSourceObject((EObject)querySource), segment);

                }
                return true;
            }
        }

        return false;
    }

    private boolean isSelectAllQuery(DbViewFieldFieldDefFromQuery querySource)
    {
        QuerySchemaSelectQuery select = EcoreUtil2.getContainerOfType(querySource, QuerySchemaSelectQuery.class);
        if (select == null)
        {
            return false;
        }
        for (QuerySchemaOperator operator : select.getOperators())
        {
            for (QuerySchemaExpression field : operator.getSelectFields())
            {
                if (field.getExpression() instanceof StarExpression)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private EObject getSourceObject(EObject object)
    {
        Set<EObject> sources = qlMapper.getSourceObjects(object);
        for (EObject source : sources)
        {
            DbViewElement dbModel = null;
            if (source instanceof QuerySchemaExpression)
            {
                dbModel = dynamicDbViewFieldComputer.computeDbView(((QuerySchemaExpression)source).getExpression());
            }
            else
            {
                dbModel = dynamicDbViewFieldComputer.computeDbView(source);
            }
            if (dbModel instanceof DbViewFieldFieldDef)
            {
                return dbModel;
            }

            if (dbModel != null && dbModel.getMdObject() != null)
            {
                return dbModel.getMdObject();
            }
        }

        return null;
    }

    private boolean isNameEquals(Object object, String name)
    {
        return object instanceof MdObject && name.equalsIgnoreCase(((MdObject)object).getName())
            || object instanceof NamedElement && name.equalsIgnoreCase(((NamedElement)object).getName())
            || object instanceof DuallyNamedElement && name.equalsIgnoreCase(((DuallyNamedElement)object).getNameRu());
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
            definition.addGenericModelAssociationContextCollector(newObjectCollector, DYNAMIC_LIST_EXT_INFO, FORM);
            definition.addGenericModelAssociationContextCollector(newObjectCollector,
                DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD, FORM);
            definition.addGenericModelAssociationContextCollector(newObjectCollector, PRESENTATION, FORM);
            definition.addGenericModelAssociationContextCollector(newObjectCollector, LOCAL_STRING, FORM);
            OnModelFeatureChangeContextCollector changeCollector = (bmObject, feature, bmEvent, contextSession) -> {
                IBmObject top = bmObject.bmGetTopObject();
                if (top instanceof Form)
                {
                    addItems(contextSession, (FormItemContainer)top);
                }
            };
            definition.addGenericModelFeatureChangeContextCollector(changeCollector, DYNAMIC_LIST_EXT_INFO, FORM);
            definition.addGenericModelFeatureChangeContextCollector(changeCollector,
                DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD, FORM);
            definition.addGenericModelFeatureChangeContextCollector(changeCollector, PRESENTATION, FORM);
            definition.addGenericModelFeatureChangeContextCollector(changeCollector, LOCAL_STRING, FORM);

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
