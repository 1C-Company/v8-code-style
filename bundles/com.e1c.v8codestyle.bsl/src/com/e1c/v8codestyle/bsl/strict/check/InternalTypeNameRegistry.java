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
package com.e1c.v8codestyle.bsl.strict.check;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com._1c.g5.v8.dt.platform.IEObjectDynamicTypeNames;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.google.inject.Singleton;

/**
 * The registry of internal type name.
 *
 * @author Dmitriy Marmyshev
 */
@Singleton
public class InternalTypeNameRegistry
{
    private Map<String, String> typeToInternalTypeNames;

    private Collection<String> allRefTypeSetParentTypeNames;

    /**
     * Gets the internal type name by the given public type name.
     *
     * @param typeName the public type name
     * @return the internal type name
     */
    @Nullable
    public String getInternalTypeName(String typeName)
    {
        if (typeName == null)
        {
            return null;
        }

        if (typeToInternalTypeNames == null)
        {
            init();
        }
        return typeToInternalTypeNames.get(typeName);
    }

    /**
     * Provide set of all reference type names of parent types.
     *
     * @return the collection of all reference type names of parent types.
     */
    @NonNullByDefault
    public Collection<String> allRefTypeSetParentTypeNames()
    {
        if (allRefTypeSetParentTypeNames == null)
        {
            init();
        }

        return allRefTypeSetParentTypeNames;
    }

    private synchronized void init()
    {
        if (typeToInternalTypeNames != null && allRefTypeSetParentTypeNames != null)
        {
            return;
        }

        Map<String, String> internalTypeNames = new HashMap<>();
        internalTypeNames.put(IEObjectTypeNames.CATALOG_OBJ, IEObjectDynamicTypeNames.CATALOG_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CATALOG_REF, IEObjectDynamicTypeNames.CATALOG_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CATALOG_SELECTION,
            IEObjectDynamicTypeNames.CATALOG_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CATALOG_LIST, IEObjectDynamicTypeNames.CATALOG_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CATALOG_MANAGER, IEObjectDynamicTypeNames.CATALOG_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_OBJ, IEObjectDynamicTypeNames.DOCUMENT_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_REF, IEObjectDynamicTypeNames.DOCUMENT_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_SELECTION,
            IEObjectDynamicTypeNames.DOCUMENT_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_LIST, IEObjectDynamicTypeNames.DOCUMENT_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_MANAGER, IEObjectDynamicTypeNames.DOCUMENT_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_OBJ,
            IEObjectDynamicTypeNames.COC_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_REF,
            IEObjectDynamicTypeNames.COC_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_SELECTION,
            IEObjectDynamicTypeNames.COC_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_LIST,
            IEObjectDynamicTypeNames.COC_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CHARACTERISTIC_TYPES_MANAGER,
            IEObjectDynamicTypeNames.COC_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_OBJ, IEObjectDynamicTypeNames.COA_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_REF, IEObjectDynamicTypeNames.COA_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_SELECTION,
            IEObjectDynamicTypeNames.COA_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_LIST, IEObjectDynamicTypeNames.COA_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_MANAGER,
            IEObjectDynamicTypeNames.COA_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_ACCOUNTS_EXT_DIMENSION_TYPES,
            IEObjectDynamicTypeNames.COA_EXT_DIM_TYPE_NAME);
        internalTypeNames.put("ChartOfAccountsExtDimensionTypesRow", //$NON-NLS-1$
            IEObjectDynamicTypeNames.COA_EXT_DIM_ROW_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_OBJ,
            IEObjectDynamicTypeNames.CHART_OF_CALCULATION_TYPES_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_MANAGER,
            IEObjectDynamicTypeNames.CHART_OF_CALCULATION_TYPES_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_RECORD,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_SELECTION,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_LIST,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_MANAGER,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_RECORD_SET,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_RECORD_KEY,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CALCULATION_REGISTER_RECALCS,
            IEObjectDynamicTypeNames.CALCULATION_REGISTER_RECALCS_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.RECALCULATION_RECORD,
            IEObjectDynamicTypeNames.RECALCULATION_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.RECALCULATION_MANAGER,
            IEObjectDynamicTypeNames.RECALCULATION_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.RECALCULATION_RECORD_SET,
            IEObjectDynamicTypeNames.RECALCULATION_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_SELECTION,
            IEObjectDynamicTypeNames.INFORMAION_REG_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_LIST,
            IEObjectDynamicTypeNames.INFORMAION_REG_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_RECORD_SET,
            IEObjectDynamicTypeNames.INFORMAION_REG_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_MANAGER,
            IEObjectDynamicTypeNames.INFORMAION_REG_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_RECORD_KEY,
            IEObjectDynamicTypeNames.INFORMAION_REG_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_RECORD,
            IEObjectDynamicTypeNames.INFORMAION_REG_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INFORMATION_REGISTER_RECORD_MANAGER,
            IEObjectDynamicTypeNames.INFORMAION_REG_RECORD_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ENUM_REF, IEObjectDynamicTypeNames.ENUM_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ENUM_LIST, IEObjectDynamicTypeNames.ENUM_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ENUM_MANAGER, IEObjectDynamicTypeNames.ENUM_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_SELECTION,
            IEObjectDynamicTypeNames.ACCUM_REG_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_LIST,
            IEObjectDynamicTypeNames.ACCUM_REG_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_RECORD_SET,
            IEObjectDynamicTypeNames.ACCUM_REG_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_MANAGER,
            IEObjectDynamicTypeNames.ACCUM_REG_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_RECORD_KEY,
            IEObjectDynamicTypeNames.ACCUM_REG_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCUMULATION_REGISTER_RECORD,
            IEObjectDynamicTypeNames.ACCUM_REG_RECORD_TYPE_NAME);
        internalTypeNames.put("AccountingRegisterSelection", //$NON-NLS-1$
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCOUNTING_REGISTER_LIST,
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCOUNTING_REGISTER_RECORD_SET,
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCOUNTING_REGISTER_MANAGER,
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCOUNTING_REGISTER_RECORD_KEY,
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.ACCOUNTING_REGISTER_RECORD,
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_RECORD_TYPE_NAME);
        internalTypeNames.put("AccountingRegisterExtDimensions", //$NON-NLS-1$
            IEObjectDynamicTypeNames.ACCOUNTING_REGISTER_EXT_DIMENSIONS_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CONSTANT_MANAGER, IEObjectDynamicTypeNames.CONSTANT_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CONSTANT_VALUE_MANAGER,
            IEObjectDynamicTypeNames.CONSTANT_VALUE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CONSTANT_VALUE_KEY,
            IEObjectDynamicTypeNames.CONSTANT_VALUE_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXCHANGE_PLAN_OBJ,
            IEObjectDynamicTypeNames.EXCHANGE_PLAN_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXCHANGE_PLAN_REF,
            IEObjectDynamicTypeNames.EXCHANGE_PLAN_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXCHANGE_PLAN_SELECTION,
            IEObjectDynamicTypeNames.EXCHANGE_PLAN_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXCHANGE_PLAN_LIST,
            IEObjectDynamicTypeNames.EXCHANGE_PLAN_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXCHANGE_PLAN_MANAGER,
            IEObjectDynamicTypeNames.EXCHANGE_PLAN_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.BUSINESS_PROCESS_OBJ, IEObjectDynamicTypeNames.BP_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.BUSINESS_PROCESS_REF, IEObjectDynamicTypeNames.BP_REF_TYPE_NAME);
        internalTypeNames.put("BusinessProcessSelection", IEObjectDynamicTypeNames.BP_SELECTION_TYPE_NAME); //$NON-NLS-1$
        internalTypeNames.put(IEObjectTypeNames.BUSINESS_PROCESS_LIST, IEObjectDynamicTypeNames.BP_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.BUSINESS_PROCESS_MANAGER,
            IEObjectDynamicTypeNames.BP_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.BUSINESS_PROCESS_ROUTE_POINT_REF,
            IEObjectDynamicTypeNames.BP_ROUTEPOINT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.TASK_OBJ, IEObjectDynamicTypeNames.TASK_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.TASK_REF, IEObjectDynamicTypeNames.TASK_REF_TYPE_NAME);
        internalTypeNames.put("TaskSelection", IEObjectDynamicTypeNames.TASK_SELECTION_TYPE_NAME); //$NON-NLS-1$
        internalTypeNames.put(IEObjectTypeNames.TASK_LIST, IEObjectDynamicTypeNames.TASK_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.TASK_MANAGER, IEObjectDynamicTypeNames.TASK_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.SEQUENCE_RECORD, IEObjectDynamicTypeNames.SEQUENCE_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.SEQUENCE_MANAGER, IEObjectDynamicTypeNames.SEQUENCE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.SEQUENCE_RECORD_SET,
            IEObjectDynamicTypeNames.SEQUENCE_RECORD_SET_TYPE_NAME);
        internalTypeNames.put("DocumentJournalSelection", //$NON-NLS-1$
            IEObjectDynamicTypeNames.DOCUMENT_JOURNAL_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_JOURNAL_LIST,
            IEObjectDynamicTypeNames.DOCUMENT_JOURNAL_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DOCUMENT_JOURNAL_MANAGER,
            IEObjectDynamicTypeNames.DOCUMENT_JOURNAL_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.REPORT_OBJ, IEObjectDynamicTypeNames.REPORT_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.REPORT_MANAGER, IEObjectDynamicTypeNames.REPORT_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DATA_PROCESSOR_OBJ,
            IEObjectDynamicTypeNames.DATA_PROCESSOR_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.DATA_PROCESSOR_MANAGER,
            IEObjectDynamicTypeNames.DATA_PROCESSOR_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.FILTER_CRITERION_LIST,
            IEObjectDynamicTypeNames.FILTER_CRITERION_LIST_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.FILTER_CRITERION_MANAGER,
            IEObjectDynamicTypeNames.FILTER_CRITERION_MANAGER_TYPE_NAME);
        internalTypeNames.put("WSReferenceManager", IEObjectDynamicTypeNames.WS_REFERENCE_MANAGER_TYPE_NAME); //$NON-NLS-1$
        internalTypeNames.put(IEObjectTypeNames.SETTINGS_STORAGE_MANAGER,
            IEObjectDynamicTypeNames.SETTINGS_STORAGE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_REF,
            IEObjectDynamicTypeNames.CALCULATION_TYPE_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_SELECTION,
            IEObjectDynamicTypeNames.CALCULATION_TYPE_SELECTION_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.CHART_OF_CALCULATION_TYPES_LIST,
            IEObjectDynamicTypeNames.CALCULATION_TYPE_LIST_TYPE_NAME);
        internalTypeNames.put("DisplacingCalculationTypes", //$NON-NLS-1$
            IEObjectDynamicTypeNames.CALCULATION_TYPE_DISPLACING_CALCULATION_TYPES);
        internalTypeNames.put("DisplacingCalculationTypesRow", //$NON-NLS-1$
            IEObjectDynamicTypeNames.CALCULATION_TYPE_DISPLACING_CALCULATION_TYPES_ROW);
        internalTypeNames.put("BaseCalculationTypes", IEObjectDynamicTypeNames.CALCULATION_TYPE_BASE_CALCULATION_TYPES); //$NON-NLS-1$
        internalTypeNames.put("BaseCalculationTypesRow", //$NON-NLS-1$
            IEObjectDynamicTypeNames.CALCULATION_TYPE_BASE_CALCULATION_TYPES_ROW);
        internalTypeNames.put("LeadingCalculationTypes", //$NON-NLS-1$
            IEObjectDynamicTypeNames.CALCULATION_TYPE_LEADING_CALCULATION_TYPES);
        internalTypeNames.put("LeadingCalculationTypesRow", //$NON-NLS-1$
            IEObjectDynamicTypeNames.CALCULATION_TYPE_LEADING_CALCULATION_TYPES_ROW);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_MANAGER_TYPE_NAME);
        internalTypeNames.put("ExternalDataSourceTablesManager", //$NON-NLS-1$
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLES_MANAGER_TYPE_NAME);
        internalTypeNames.put("ExternalDataSourceCubesManager", //$NON-NLS-1$
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBES_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_SET,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_KEY,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_RECORD_MANAGER_TYPE_NAME);
        internalTypeNames.put("ExternalDataSourceCubeDimensionsTablesManager", //$NON-NLS-1$
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_CUBE_DIMENSION_TABLES_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_DIMENSION_TABLE_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_DIMENSION_TABLE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_DIMENSION_TABLE_OBJECT,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_DIMENSION_TABLE_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_CUBE_DIMENSION_TABLE_REF,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_DIMENSION_TABLE_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_OBJECT,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_OBJECT_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_REF,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_REF_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_SET,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_SET_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_KEY,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_KEY_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_MANAGER,
            IEObjectDynamicTypeNames.EXTERNAL_DATA_SOURCE_TABLE_RECORD_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INTEGRATION_SERVICE_MANAGER,
            IEObjectDynamicTypeNames.INTEGRATION_SERVICE_MANAGER_TYPE_NAME);
        internalTypeNames.put(IEObjectTypeNames.INTEGRATION_SERVICE_MANAGER_CHANNEL,
            IEObjectDynamicTypeNames.INTEGRATION_SERVICE_CHANNEL_MANAGER_TYPE_NAME);

        typeToInternalTypeNames = Map.copyOf(internalTypeNames);

        allRefTypeSetParentTypeNames =
            Set.of(IEObjectDynamicTypeNames.CATALOG_REF_TYPE_NAME, IEObjectDynamicTypeNames.DOCUMENT_REF_TYPE_NAME,
                IEObjectDynamicTypeNames.ENUM_REF_TYPE_NAME, IEObjectDynamicTypeNames.COC_REF_TYPE_NAME,
                IEObjectDynamicTypeNames.COA_REF_TYPE_NAME, IEObjectDynamicTypeNames.CALCULATION_TYPE_REF_TYPE_NAME,
                IEObjectDynamicTypeNames.BP_REF_TYPE_NAME, IEObjectDynamicTypeNames.BP_ROUTEPOINT_TYPE_NAME,
                IEObjectDynamicTypeNames.TASK_REF_TYPE_NAME, IEObjectDynamicTypeNames.EXCHANGE_PLAN_REF_TYPE_NAME);
    }
}
