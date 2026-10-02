/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__TITLE;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__VALUE_TYPE;
import static com._1c.g5.v8.dt.dcs.model.schema.DcsPackage.Literals.DATA_COMPOSITION_SCHEMA__DATA_SETS;

import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import com._1c.g5.v8.dt.dcs.model.core.Presentation;
import com._1c.g5.v8.dt.dcs.model.schema.DataCompositionSchema;
import com._1c.g5.v8.dt.dcs.model.schema.DataSet;
import com._1c.g5.v8.dt.dcs.model.schema.DataSetField;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.NumberQualifiers;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * Money string localization in data composition schema.
 * @author Ivan Sergeev
 */
public class MoneyStringDataCompositionCheck
    extends BasicCheck<Object>
{
    private static final String CHECK_ID = "money-string-localization-data-composition"; //$NON-NLS-1$
    private static final String MONEY_STRING_NAME = "Money string name"; //$NON-NLS-1$
    private static final Set<String> IMMUTABLE_MAP_MONEY_STRING =
        Set.of("Сумма", "Цена", "Себестоимость", "СуммаИзлишков", "Amount", "Price", "Cost", "SurplusAmount"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_NAMES = String.join(DELIMITER, IMMUTABLE_MAP_MONEY_STRING);

    @Inject
    public MoneyStringDataCompositionCheck()
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
        builder.title(Messages.MoneyStringDataCompositionCheck_Title)
            .description(Messages.MoneyStringDataCompositionCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new StandardCheckExtension(674, getCheckId(), CorePlugin.PLUGIN_ID))
            .parameter(MONEY_STRING_NAME, String.class, DEFAULT_NAMES,
                Messages.MoneyStringDataCompositionCheck_Parametr)
            .topObject(DATA_COMPOSITION_SCHEMA)
            .features(DATA_COMPOSITION_SCHEMA__DATA_SETS)
            .features(DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__TITLE)
            .features(DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__VALUE_TYPE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DataCompositionSchema dcs = (DataCompositionSchema)object;
        EList<DataSet> dataSets = dcs.getDataSets();
        for (DataSet dataSet : dataSets)
        {
            EList<DataSetField> fields = dataSet.getFields();
            for (DataSetField field : fields)
            {
                String findName = nameValue(field);
                if (findName == null)
                {
                    continue;
                }
                String namesParametr = parameters.getString(MONEY_STRING_NAME);
                String[] names = namesParametr.split(DELIMITER);
                for (String name : names)
                {
                    if (name.equalsIgnoreCase(findName))
                    {
                        if (typeCheck(field))
                        {
                            resultAcceptor.addIssue(Messages.MoneyStringDataCompositionCheck_Issue, field);
                        }
                    }
                }
            }
        }
    }

    private String nameValue(DataSetField field)
    {
        Object obj = field.eGet(DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__TITLE);
        if (obj instanceof Presentation presentation)
        {
            EMap<String, String> values = presentation.getLocalValue().getContent();
            for (Entry<String, String> entry : values)
            {
                return entry.getValue();
            }
        }
        return null;
    }

    private Boolean typeCheck(DataSetField field)
    {
        TypeDescription obj = (TypeDescription)field.eGet(DATA_COMPOSITION_SCHEMA_DATA_SET_FIELD__VALUE_TYPE);
        if (obj == null)
        {
            return null;
        }
        EList<TypeItem> types = obj.getTypes();
        Object qualifiers = obj.eGet(McorePackage.Literals.TYPE_DESCRIPTION__NUMBER_QUALIFIERS);
        for (TypeItem typeItem : types)
        {
            if ("Number".equalsIgnoreCase(typeItem.getName())) //$NON-NLS-1$
            {
                if (qualifiers instanceof NumberQualifiers numberQual)
                {
                    int precision = numberQual.getPrecision();
                    int scale = numberQual.getScale();
                    if (precision == 31 && scale == 2)
                    {
                        return false;
                    }
                }
            }
            else if ("DefinedType.ДенежнаяСуммаЛюбогоЗнака".equalsIgnoreCase(typeItem.getName()) //$NON-NLS-1$
                || "DefinedType.ДенежнаяСуммаНеотрицательная".equalsIgnoreCase(typeItem.getName())) //$NON-NLS-1$
            {
                return false;
            }
        }
        return true;
    }
}
