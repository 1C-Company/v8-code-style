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
package com.e1c.v8codestyle.form.fix;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.dcs.model.core.DcsFactory;
import com._1c.g5.v8.dt.dcs.model.core.LocalString;
import com._1c.g5.v8.dt.dcs.model.core.Presentation;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchemaDataSetField;
import com._1c.g5.v8.dt.dcs.model.schema.DataSetField;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DynamicListExtInfo;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.form.model.PropertyInfo;
import com._1c.g5.v8.dt.form.service.datasourceinfo.IDataSourceInfoAssociationService;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.form.check.DynamicListItemTitleCheck;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * This fix generate title text for dynamic list field presentation from field data path CamelCase.
 *
 * @author Dmitriy Marmyshev
 */
@QuickFix(checkId = DynamicListItemTitleCheck.CHECK_ID, supplierId = CorePlugin.PLUGIN_ID)
public class DynamicListFieldTitleGenerateFix
    extends DynamicListItemTitleGenerateFix
{
    private final IV8ProjectManager v8ProjectManager;

    private final IDataSourceInfoAssociationService dataSourceInfoAssociationService;

    /**
     * Instantiates a new dynamic list field title generate fix.
     *
     * @param v8ProjectManager the v8 project manager, cannot be {@code null}.
     * @param dataSourceInfoAssociationService the data source info association service, cannot be {@code null}.
     */
    @Inject
    public DynamicListFieldTitleGenerateFix(IV8ProjectManager v8ProjectManager,
        IDataSourceInfoAssociationService dataSourceInfoAssociationService)
    {
        super(v8ProjectManager);
        this.v8ProjectManager = v8ProjectManager;
        this.dataSourceInfoAssociationService = dataSourceInfoAssociationService;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description((context, session) -> {

            IV8Project v8Project = v8ProjectManager.getProject(session.getDtProject());
            if (Version.V8_3_19.isGreaterThan(v8Project.getVersion()))
            {
                return Messages.DynamicListFieldTitleGenerateFix_Default_title;
            }

            long id = context.getTargetObjectId();
            IBmObject object = session.getModelObject(id);
            if (object instanceof FormField)
            {
                DataCompositionSchemaDataSetField field = getDynamicListField((FormField)object);
                if (field != null)
                {
                    String name = field.getDataPath();
                    String title = StringUtils.nameToText(name);
                    return MessageFormat.format(Messages.DynamicListFieldTitleGenerateFix_title, title, name);
                }
            }
            return Messages.DynamicListFieldTitleGenerateFix_Default_title;
        });
    }

    @Override
    protected void applyChanges(FormField object, EStructuralFeature feature, BasicModelFixContext contex,
        IFixSession session)
    {
        String name = object.getName();
        String title = StringUtils.nameToText(name);

        IV8Project v8Project = v8ProjectManager.getProject(session.getDtProject());
        if (Version.V8_3_19.isGreaterThan(v8Project.getVersion()))
        {
            return;
        }

        Optional<String> languageCode = getDefaultLanguageCode(v8Project);
        if (languageCode.isPresent())
        {
            LocalString loacalString = getOrCreateLocalString(object);
            loacalString.getContent().put(languageCode.get(), title);
        }
    }

    private DataCompositionSchemaDataSetField getDynamicListField(FormField item)
    {
        Form form = (Form)((IBmObject)item).bmGetTopObject();
        AbstractDataPath dataPath = item.getDataPath();
        if (dataPath == null || dataPath.getSegments().size() != 2)
        {
            return null;
        }

        String segment = dataPath.getSegments().get(1);
        PropertyInfo attribute = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath, 0);
        DynamicListExtInfo custormQuery = (DynamicListExtInfo)((FormAttribute)attribute.getSource()).getExtInfo();

        for (DataSetField field : custormQuery.getFields())
        {
            if (field instanceof DataCompositionSchemaDataSetField
                && segment.equalsIgnoreCase(((DataCompositionSchemaDataSetField)field).getDataPath()))
            {
                return (DataCompositionSchemaDataSetField)field;
            }
        }
        return null;

    }

    private DataCompositionSchemaDataSetField getOrCreateDynamicListField(FormField item)
    {
        Form form = (Form)((IBmObject)item).bmGetTopObject();
        AbstractDataPath dataPath = item.getDataPath();

        String segment = dataPath.getSegments().get(1);
        PropertyInfo attribute = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath, 0);
        DynamicListExtInfo custormQuery = (DynamicListExtInfo)((FormAttribute)attribute.getSource()).getExtInfo();

        for (DataSetField field : custormQuery.getFields())
        {
            if (field instanceof DataCompositionSchemaDataSetField
                && segment.equalsIgnoreCase(((DataCompositionSchemaDataSetField)field).getDataPath()))
            {
                return (DataCompositionSchemaDataSetField)field;
            }
        }

        DataCompositionSchemaDataSetField field =
            com._1c.g5.v8.dt.dcs.model.schema.DcsFactory.eINSTANCE.createDataCompositionSchemaDataSetField();
        field.setDataPath(segment);
        field.setField(segment);
        custormQuery.getFields().add(field);
        return field;
    }

    private LocalString getOrCreateLocalString(FormField object)
    {
        DataCompositionSchemaDataSetField field = getOrCreateDynamicListField(object);

        Presentation title = field.getTitle();
        if (title == null)
        {
            title = DcsFactory.eINSTANCE.createPresentation();
            field.setTitle(title);
        }
        LocalString localString = title.getLocalValue();
        if (localString == null)
        {
            localString = DcsFactory.eINSTANCE.createLocalString();
            title.setLocalValue(localString);
        }
        return localString;
    }

}
