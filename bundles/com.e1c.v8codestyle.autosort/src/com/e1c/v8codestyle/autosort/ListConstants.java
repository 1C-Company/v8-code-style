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

import java.util.Set;

import org.eclipse.emf.ecore.EReference;

import  com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals;

/**
 * The constants with lists that can be sorted in TOP or subordinate objects.
 *
 * @author Dmitriy Marmyshev
 */
public final class ListConstants
{

    // @formatter:off
    public static final Set<EReference> TOP_OPBJECT_LISTS = Set.of(
        Literals.CONFIGURATION__LANGUAGES,
        Literals.CONFIGURATION__SUBSYSTEMS,
        Literals.SUBSYSTEM__SUBSYSTEMS,
        Literals.CONFIGURATION__STYLE_ITEMS,
        Literals.CONFIGURATION__STYLES,
        Literals.CONFIGURATION__COMMON_PICTURES,
        Literals.CONFIGURATION__INTERFACES,
        Literals.CONFIGURATION__SESSION_PARAMETERS,
        Literals.CONFIGURATION__ROLES,
        Literals.CONFIGURATION__COMMON_TEMPLATES,
        Literals.CONFIGURATION__FILTER_CRITERIA,
        Literals.CONFIGURATION__COMMON_MODULES,
        Literals.CONFIGURATION__EXCHANGE_PLANS,
        Literals.CONFIGURATION__XDTO_PACKAGES,
        Literals.CONFIGURATION__WEB_SERVICES,
        Literals.CONFIGURATION__HTTP_SERVICES,
        Literals.CONFIGURATION__WS_REFERENCES,
        Literals.CONFIGURATION__EVENT_SUBSCRIPTIONS,
        Literals.CONFIGURATION__SCHEDULED_JOBS,
        Literals.CONFIGURATION__SETTINGS_STORAGES,
        Literals.CONFIGURATION__FUNCTIONAL_OPTIONS,
        Literals.CONFIGURATION__FUNCTIONAL_OPTIONS_PARAMETERS,
        Literals.CONFIGURATION__DEFINED_TYPES,
        Literals.CONFIGURATION__COMMON_COMMANDS,
        Literals.CONFIGURATION__CONSTANTS,
        Literals.CONFIGURATION__COMMON_FORMS,
        Literals.CONFIGURATION__CATALOGS,
        Literals.CONFIGURATION__DOCUMENTS,
        Literals.CONFIGURATION__DOCUMENT_NUMERATORS,
        Literals.CONFIGURATION__SEQUENCES,
        Literals.CONFIGURATION__DOCUMENT_JOURNALS,
        Literals.CONFIGURATION__ENUMS,
        Literals.CONFIGURATION__REPORTS,
        Literals.CONFIGURATION__DATA_PROCESSORS,
        Literals.CONFIGURATION__INFORMATION_REGISTERS,
        Literals.CONFIGURATION__ACCUMULATION_REGISTERS,
        Literals.CONFIGURATION__CHARTS_OF_CHARACTERISTIC_TYPES,
        Literals.CONFIGURATION__CHARTS_OF_ACCOUNTS,
        Literals.CONFIGURATION__ACCOUNTING_REGISTERS,
        Literals.CONFIGURATION__CHARTS_OF_CALCULATION_TYPES,
        Literals.CONFIGURATION__CALCULATION_REGISTERS,
        Literals.CONFIGURATION__BUSINESS_PROCESSES,
        Literals.CONFIGURATION__TASKS,
        Literals.CONFIGURATION__EXTERNAL_DATA_SOURCES);
    // @formatter:on

    // @formatter:off
    public static final Set<EReference> SUBORDINATE_OBJECT_LISTS = Set.of(
        Literals.CATALOG__FORMS,
        Literals.CATALOG__TEMPLATES,
        Literals.CATALOG__COMMANDS,
        Literals.DOCUMENT__FORMS,
        Literals.DOCUMENT__TEMPLATES,
        Literals.DOCUMENT__COMMANDS,
        Literals.DOCUMENT_JOURNAL__FORMS,
        Literals.DOCUMENT_JOURNAL__TEMPLATES,
        Literals.DOCUMENT_JOURNAL__COMMANDS,
        Literals.ENUM__FORMS,
        Literals.ENUM__TEMPLATES,
        Literals.ENUM__COMMANDS,
        Literals.REPORT__FORMS,
        Literals.REPORT__TEMPLATES,
        Literals.REPORT__COMMANDS,
        Literals.EXTERNAL_REPORT__FORMS,
        Literals.EXTERNAL_REPORT__TEMPLATES,
        Literals.DATA_PROCESSOR__FORMS,
        Literals.DATA_PROCESSOR__TEMPLATES,
        Literals.DATA_PROCESSOR__COMMANDS,
        Literals.EXTERNAL_DATA_PROCESSOR__FORMS,
        Literals.EXTERNAL_DATA_PROCESSOR__TEMPLATES,
        Literals.INFORMATION_REGISTER__FORMS,
        Literals.INFORMATION_REGISTER__TEMPLATES,
        Literals.INFORMATION_REGISTER__COMMANDS,
        Literals.ACCUMULATION_REGISTER__FORMS,
        Literals.ACCUMULATION_REGISTER__TEMPLATES,
        Literals.ACCUMULATION_REGISTER__COMMANDS,
        Literals.ACCOUNTING_REGISTER__FORMS,
        Literals.ACCOUNTING_REGISTER__TEMPLATES,
        Literals.ACCOUNTING_REGISTER__COMMANDS,
        Literals.CHART_OF_CHARACTERISTIC_TYPES__FORMS,
        Literals.CHART_OF_CHARACTERISTIC_TYPES__TEMPLATES,
        Literals.CHART_OF_CHARACTERISTIC_TYPES__COMMANDS,
        Literals.CHART_OF_ACCOUNTS__FORMS,
        Literals.CHART_OF_ACCOUNTS__TEMPLATES,
        Literals.CHART_OF_ACCOUNTS__COMMANDS,
        Literals.CHART_OF_CALCULATION_TYPES__FORMS,
        Literals.CHART_OF_CALCULATION_TYPES__TEMPLATES,
        Literals.CHART_OF_CALCULATION_TYPES__COMMANDS,
        Literals.EXCHANGE_PLAN__FORMS,
        Literals.EXCHANGE_PLAN__TEMPLATES,
        Literals.EXCHANGE_PLAN__COMMANDS,
        Literals.BUSINESS_PROCESS__FORMS,
        Literals.BUSINESS_PROCESS__TEMPLATES,
        Literals.BUSINESS_PROCESS__COMMANDS,
        Literals.TASK__FORMS,
        Literals.TASK__TEMPLATES,
        Literals.TASK__COMMANDS,
        Literals.SETTINGS_STORAGE__FORMS,
        Literals.SETTINGS_STORAGE__TEMPLATES,
        Literals.WEB_SERVICE__OPERATIONS,
        Literals.HTTP_SERVICE__URL_TEMPLATES,
        Literals.URL_TEMPLATE__METHODS,
        Literals.FILTER_CRITERION__FORMS,
        Literals.FILTER_CRITERION__COMMANDS,
        Literals.DB_OBJECT_TABULAR_SECTION__ATTRIBUTES,
        Literals.CATALOG__ATTRIBUTES,
        Literals.DOCUMENT__ATTRIBUTES,
        Literals.CHART_OF_CHARACTERISTIC_TYPES__ATTRIBUTES,
        Literals.INFORMATION_REGISTER__ATTRIBUTES,
        Literals.EXCHANGE_PLAN__ATTRIBUTES,
        Literals.REPORT__ATTRIBUTES,
        Literals.REPORT_TABULAR_SECTION__ATTRIBUTES,
        Literals.EXTERNAL_REPORT__ATTRIBUTES,
        Literals.DATA_PROCESSOR__ATTRIBUTES,
        Literals.DATA_PROCESSOR_TABULAR_SECTION__ATTRIBUTES,
        Literals.EXTERNAL_DATA_PROCESSOR__ATTRIBUTES,
        Literals.ACCUMULATION_REGISTER__ATTRIBUTES,
        Literals.BUSINESS_PROCESS__ATTRIBUTES,
        Literals.TASK__ATTRIBUTES,
        Literals.CHART_OF_ACCOUNTS__ATTRIBUTES,
        Literals.CHART_OF_CALCULATION_TYPES__ATTRIBUTES,
        Literals.ACCOUNTING_REGISTER__ATTRIBUTES,
        Literals.CALCULATION_REGISTER__ATTRIBUTES,
        Literals.CATALOG__TABULAR_SECTIONS,
        Literals.DOCUMENT__TABULAR_SECTIONS,
        Literals.CHART_OF_CHARACTERISTIC_TYPES__TABULAR_SECTIONS,
        Literals.EXCHANGE_PLAN__TABULAR_SECTIONS,
        Literals.REPORT__TABULAR_SECTIONS,
        Literals.EXTERNAL_REPORT__TABULAR_SECTIONS,
        Literals.DATA_PROCESSOR__TABULAR_SECTIONS,
        Literals.EXTERNAL_DATA_PROCESSOR__TABULAR_SECTIONS,
        Literals.BUSINESS_PROCESS__TABULAR_SECTIONS,
        Literals.TASK__TABULAR_SECTIONS,
        Literals.CHART_OF_ACCOUNTS__TABULAR_SECTIONS,
        Literals.CHART_OF_CALCULATION_TYPES__TABULAR_SECTIONS,
        Literals.INFORMATION_REGISTER__RESOURCES,
        Literals.ACCUMULATION_REGISTER__RESOURCES,
        Literals.ACCOUNTING_REGISTER__RESOURCES);
    // @formatter:on

    private ListConstants()
    {
        throw new IllegalAccessError();
    }

}
