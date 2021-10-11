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
package com.e1c.v8codestyle.md;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_MANAGED_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__CLIENT_ORDINARY_APPLICATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__EXTERNAL_CONNECTION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__GLOBAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__PRIVILEGED;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__RETURN_VALUES_REUSE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;

/**
 * The Enumeration of common module types with title, suffix and feature settings.
 *
 * @author Dmitriy Marmyshev
 */
public enum CommonModuleTypes
{

    //@formatter:off
    SERVER(Messages.CommonModuleTypes_Server_module,
        new String[] { "", "" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT(Messages.CommonModuleTypes_Client_module,
        new String[] { "Client", "Клиент" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, false,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, false,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT_SERVER(Messages.CommonModuleTypes_Client_Server_module,
        new String[] { "ClientServer", "КлиентСервер" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    SERVER_CALL(Messages.CommonModuleTypes_Server_module_for_call_form_client,
        new String[] { "ServerCall", "ВызовСервера" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, false,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, true,
            COMMON_MODULE__EXTERNAL_CONNECTION, false,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT_GLOBAL(Messages.CommonModuleTypes_Client_global_module,
        new String[] { "Global", "Глобальный" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, true,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    SERVER_GLOBAL(Messages.CommonModuleTypes_Server_global_module,
        new String[] { "ServerGlobal", "СерверГлобальный" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, true,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT_CACHED(Messages.CommonModuleTypes_Client_Cached_module,
        new String[] { "ClientCached", "КлиентПовтИсп" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, false,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, false,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DURING_SESSION)),

    SERVER_CACHED(Messages.CommonModuleTypes_Server_Cached_module,
        new String[] { "Cached", "ПовтИсп" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DURING_SESSION)),

    SERVER_FULL_ACCESS(Messages.CommonModuleTypes_Server_Full_access_module,
        new String[] { "FullAccess", "ПолныеПрава" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, true,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    SERVER_OVERRIDABLE(Messages.CommonModuleTypes_Server_Overridable_module,
        new String[] { "Overridable", "Переопределяемый" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT_OVERRIDABLE(Messages.CommonModuleTypes_Client_Overridable_module,
        new String[] { "ClientOverridable", "КлиентПереопределяемый" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, false,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, false,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    SERVER_LOCALIZATION(Messages.CommonModuleTypes_Server_Localization_module,
        new String[] { "Localization", "Локализация" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, false,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, true,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, true,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE)),

    CLIENT_LOCALIZATION(Messages.CommonModuleTypes_Client_Localization_module,
        new String[] { "ClientLocalization", "КлиентЛокализация" }, //$NON-NLS-1$ //$NON-NLS-2$
        Map.of(
            COMMON_MODULE__CLIENT_MANAGED_APPLICATION, true,
            COMMON_MODULE__CLIENT_ORDINARY_APPLICATION, true,
            COMMON_MODULE__SERVER, false,
            COMMON_MODULE__SERVER_CALL, false,
            COMMON_MODULE__EXTERNAL_CONNECTION, false,
            COMMON_MODULE__GLOBAL, false,
            COMMON_MODULE__PRIVILEGED, false,
            COMMON_MODULE__RETURN_VALUES_REUSE, ReturnValuesReuse.DONT_USE));
    //@formatter:on

    private final String title;

    private final String[] nameSuffix;

    private final Map<EStructuralFeature, Object> featureValues;

    private Map<EStructuralFeature, Object> featureValuesMobileOnly;

    CommonModuleTypes(String title, String[] nameSuffix, Map<EStructuralFeature, Object> featureValues)
    {
        this.title = title;
        this.nameSuffix = nameSuffix;
        this.featureValues = featureValues;
    }

    /**
     * Gets the localizable title of the type.
     *
     * @return the title, cannot return {@code null}.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets the dually-named name suffixes where first is English script variant and second is Russian script variant.
     *
     * @return the name suffixes, cannot return {@code null}.
     */
    public String[] getNameSuffixes()
    {
        return nameSuffix;
    }

    /**
     * Gets the name suffix for the specific script variant of the project.
     *
     * @param scriptVariant the script variant, cannot be {@code null}.
     * @return the name suffix, cannot return {@code null}.
     */
    public String getNameSuffix(ScriptVariant scriptVariant)
    {
        return nameSuffix[scriptVariant.getValue()];
    }

    /**
     * Gets the feature values for the type of common module.
     *
     * @param mobileOnly the mobile use only, if true - returns without features not available in mobile application.
     * @return the feature values, cannot return {@code null}.
     */
    public Map<EStructuralFeature, Object> getFeatureValues(boolean mobileOnly)
    {
        if (mobileOnly)
        {
            if (featureValuesMobileOnly == null)
            {
                Map<EStructuralFeature, Object> forMobile = new HashMap<>();
                forMobile.putAll(featureValues);
                forMobile.remove(COMMON_MODULE__EXTERNAL_CONNECTION);
                forMobile.remove(COMMON_MODULE__PRIVILEGED);
                featureValuesMobileOnly = Map.copyOf(forMobile);
            }
            return featureValuesMobileOnly;
        }
        return featureValues;
    }

    /**
     * Find type by common module name which is most closes with suffix.
     * If non of suffixes matched then {@link CommonModuleTypes#SERVER} returns.
     *
     * @param name the name of common module, cannot be {@code null}.
     * @param script the script variant, cannot be {@code null}.
     * @return the common module type, cannot return {@code null}.
     */
    public static CommonModuleTypes findClosestTypeByName(String name, ScriptVariant script)
    {
        List<CommonModuleTypes> result = new ArrayList<>();
        CommonModuleTypes[] values = values();
        for (int i = 0; i < values.length; i++)
        {
            CommonModuleTypes type = values[i];
            String suffix = type.getNameSuffix(script);
            if (!suffix.isEmpty() && name.endsWith(suffix))
            {
                result.add(type);
            }
        }
        if (result.isEmpty())
        {
            result.add(SERVER);
        }
        if (result.size() > 1)
        {
            Collections.sort(result, (o1, o2) -> o2.getNameSuffix(script).length() - o1.getNameSuffix(script).length());
        }
        return result.get(0);
    }

}
