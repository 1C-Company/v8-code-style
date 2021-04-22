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
 *******************************************************************************/
package com.e1c.v8codestyle.autosort;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.*;

import java.util.List;

import org.eclipse.emf.ecore.EReference;

/**
 * The constants with lists that can be sorted in TOP or subordinate objects..
 *
 * @author Dmitriy Marmyshev
 */
public final class ListConstants
{

    // @formatter:off
    public static final List<EReference> TOP_OPBJECT_LISTS = List.of(
        CONFIGURATION__LANGUAGES,
        CONFIGURATION__SUBSYSTEMS,
        SUBSYSTEM__SUBSYSTEMS,
        CONFIGURATION__STYLE_ITEMS,
        CONFIGURATION__STYLES,
        CONFIGURATION__COMMON_PICTURES,
        CONFIGURATION__INTERFACES,
        CONFIGURATION__SESSION_PARAMETERS,
        CONFIGURATION__ROLES,
        CONFIGURATION__COMMON_TEMPLATES,
        CONFIGURATION__FILTER_CRITERIA,
        CONFIGURATION__COMMON_MODULES,
        CONFIGURATION__EXCHANGE_PLANS,
        CONFIGURATION__XDTO_PACKAGES,
        CONFIGURATION__WEB_SERVICES,
        CONFIGURATION__HTTP_SERVICES,
        CONFIGURATION__WS_REFERENCES,
        CONFIGURATION__EVENT_SUBSCRIPTIONS,
        CONFIGURATION__SCHEDULED_JOBS,
        CONFIGURATION__SETTINGS_STORAGES,
        CONFIGURATION__FUNCTIONAL_OPTIONS,
        CONFIGURATION__FUNCTIONAL_OPTIONS_PARAMETERS,
        CONFIGURATION__DEFINED_TYPES,
        CONFIGURATION__COMMON_COMMANDS,
        CONFIGURATION__CONSTANTS,
        CONFIGURATION__COMMON_FORMS,
        CONFIGURATION__CATALOGS,
        CONFIGURATION__DOCUMENTS,
        CONFIGURATION__DOCUMENT_NUMERATORS,
        CONFIGURATION__SEQUENCES,
        CONFIGURATION__DOCUMENT_JOURNALS,
        CONFIGURATION__ENUMS,
        CONFIGURATION__REPORTS,
        CONFIGURATION__DATA_PROCESSORS,
        CONFIGURATION__INFORMATION_REGISTERS,
        CONFIGURATION__ACCUMULATION_REGISTERS,
        CONFIGURATION__CHARTS_OF_CHARACTERISTIC_TYPES,
        CONFIGURATION__CHARTS_OF_ACCOUNTS,
        CONFIGURATION__ACCOUNTING_REGISTERS,
        CONFIGURATION__CHARTS_OF_CALCULATION_TYPES,
        CONFIGURATION__CALCULATION_REGISTERS,
        CONFIGURATION__BUSINESS_PROCESSES,
        CONFIGURATION__TASKS,
        CONFIGURATION__EXTERNAL_DATA_SOURCES);
    // @formatter:on

    // @formatter:off
    public static final List<EReference> SUBORDINATE_OBJECT_LISTS = List.of(
        CATALOG__FORMS,
        CATALOG__TEMPLATES,
        CATALOG__COMMANDS,
        DOCUMENT__FORMS,
        DOCUMENT__TEMPLATES,
        DOCUMENT__COMMANDS,
        DOCUMENT_JOURNAL__FORMS,
        DOCUMENT_JOURNAL__TEMPLATES,
        DOCUMENT_JOURNAL__COMMANDS,
        ENUM__FORMS,
        ENUM__TEMPLATES,
        ENUM__COMMANDS,
        REPORT__FORMS,
        REPORT__TEMPLATES,
        REPORT__COMMANDS,
        EXTERNAL_REPORT__FORMS,
        EXTERNAL_REPORT__TEMPLATES,
        DATA_PROCESSOR__FORMS,
        DATA_PROCESSOR__TEMPLATES,
        DATA_PROCESSOR__COMMANDS,
        EXTERNAL_DATA_PROCESSOR__FORMS,
        EXTERNAL_DATA_PROCESSOR__TEMPLATES,
        INFORMATION_REGISTER__FORMS,
        INFORMATION_REGISTER__TEMPLATES,
        INFORMATION_REGISTER__COMMANDS,
        ACCUMULATION_REGISTER__FORMS,
        ACCUMULATION_REGISTER__TEMPLATES,
        ACCUMULATION_REGISTER__COMMANDS,
        ACCOUNTING_REGISTER__FORMS,
        ACCOUNTING_REGISTER__TEMPLATES,
        ACCOUNTING_REGISTER__COMMANDS,
        CHART_OF_CHARACTERISTIC_TYPES__FORMS,
        CHART_OF_CHARACTERISTIC_TYPES__TEMPLATES,
        CHART_OF_CHARACTERISTIC_TYPES__COMMANDS,
        CHART_OF_ACCOUNTS__FORMS,
        CHART_OF_ACCOUNTS__TEMPLATES,
        CHART_OF_ACCOUNTS__COMMANDS,
        CHART_OF_CALCULATION_TYPES__FORMS,
        CHART_OF_CALCULATION_TYPES__TEMPLATES,
        CHART_OF_CALCULATION_TYPES__COMMANDS,
        EXCHANGE_PLAN__FORMS,
        EXCHANGE_PLAN__TEMPLATES,
        EXCHANGE_PLAN__COMMANDS,
        BUSINESS_PROCESS__FORMS,
        BUSINESS_PROCESS__TEMPLATES,
        BUSINESS_PROCESS__COMMANDS,
        TASK__FORMS,
        TASK__TEMPLATES,
        TASK__COMMANDS,
        SETTINGS_STORAGE__FORMS,
        SETTINGS_STORAGE__TEMPLATES,
        WEB_SERVICE__OPERATIONS,
        HTTP_SERVICE__URL_TEMPLATES,
        URL_TEMPLATE__METHODS,
        FILTER_CRITERION__FORMS,
        FILTER_CRITERION__COMMANDS,
        DB_OBJECT_TABULAR_SECTION__ATTRIBUTES,
        CATALOG__ATTRIBUTES,
        DOCUMENT__ATTRIBUTES,
        CHART_OF_CHARACTERISTIC_TYPES__ATTRIBUTES,
        INFORMATION_REGISTER__ATTRIBUTES,
        EXCHANGE_PLAN__ATTRIBUTES,
        REPORT__ATTRIBUTES,
        REPORT_TABULAR_SECTION__ATTRIBUTES,
        EXTERNAL_REPORT__ATTRIBUTES,
        DATA_PROCESSOR__ATTRIBUTES,
        DATA_PROCESSOR_TABULAR_SECTION__ATTRIBUTES,
        EXTERNAL_DATA_PROCESSOR__ATTRIBUTES,
        ACCUMULATION_REGISTER__ATTRIBUTES,
        BUSINESS_PROCESS__ATTRIBUTES,
        TASK__ATTRIBUTES,
        CHART_OF_ACCOUNTS__ATTRIBUTES,
        CHART_OF_CALCULATION_TYPES__ATTRIBUTES,
        ACCOUNTING_REGISTER__ATTRIBUTES,
        CALCULATION_REGISTER__ATTRIBUTES,
        CATALOG__TABULAR_SECTIONS,
        DOCUMENT__TABULAR_SECTIONS,
        CHART_OF_CHARACTERISTIC_TYPES__TABULAR_SECTIONS,
        EXCHANGE_PLAN__TABULAR_SECTIONS,
        REPORT__TABULAR_SECTIONS,
        EXTERNAL_REPORT__TABULAR_SECTIONS,
        DATA_PROCESSOR__TABULAR_SECTIONS,
        EXTERNAL_DATA_PROCESSOR__TABULAR_SECTIONS,
        BUSINESS_PROCESS__TABULAR_SECTIONS,
        TASK__TABULAR_SECTIONS,
        CHART_OF_ACCOUNTS__TABULAR_SECTIONS,
        CHART_OF_CALCULATION_TYPES__TABULAR_SECTIONS,
        INFORMATION_REGISTER__RESOURCES,
        ACCUMULATION_REGISTER__RESOURCES,
        ACCOUNTING_REGISTER__RESOURCES);
    // @formatter:on

    private ListConstants()
    {
        throw new IllegalAccessError();
    }

}
