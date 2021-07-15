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
 *     Aleksandr Kapralov - issue #14
 *******************************************************************************/
package com.e1c.v8codestyle.md.check;

import java.util.List;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.e1c.g5.v8.dt.check.CheckParameterDefinition;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.ext.ITopObjectFilter;

/**
 * The extention allows to filter {@link MdObject} name that has no suffix listed in parameters.
 * Parameter value may contains comma separated list of suffixes.
 *
 * @author Dmitriy Marmyshev
 */
public class MdObjectNameWithoutSuffix
    implements IBasicCheckExtension
{

    /** Default paramenter name of the name suffix list */
    public static final String NAME_SUFFIX_PARAMETER_NAME = "nameSuffix"; //$NON-NLS-1$

    private final String parameterName;

    private final String defaultValue;

    private final String parameterTitle;

    /**
     * Instantiates a new instance of filter by MD object name without suffix.
     *
     * @param parameterName the parameter name, cannot be {@code null}.
     * @param parameterTitle the parameter title, cannot be {@code null}.
     * @param defaultValue the default value, cannot be {@code null}.
     */
    public MdObjectNameWithoutSuffix(String parameterName, String parameterTitle, String defaultValue)
    {
        this.parameterName = parameterName;
        this.parameterTitle = parameterTitle;
        this.defaultValue = defaultValue;
    }

    /**
     * Instantiates a new md object name without suffix.
     *
     * @param defaultValue the default value, cannot be {@code null}.
     */
    public MdObjectNameWithoutSuffix(String defaultValue)
    {
        this(NAME_SUFFIX_PARAMETER_NAME, Messages.MdObjectNameWithoutSuffix_Name_suffix_list_title, defaultValue);
    }

    /**
     * Instantiates a new md object name without suffix.
     */
    public MdObjectNameWithoutSuffix()
    {
        this(StringUtils.EMPTY);
    }

    @Override
    public void configureContextCollector(ICheckDefinition definition)
    {
        CheckParameterDefinition parameterDefinition =
            new CheckParameterDefinition(this.parameterName, String.class, this.defaultValue, this.parameterTitle);
        definition.addParameterDefinition(parameterDefinition);
    }

    @Override
    public ITopObjectFilter contributeTopObjectFilter()
    {
        return (IBmObject topObject, ICheckParameters parameters) -> {

            if (!(topObject instanceof MdObject))
                return false;

            MdObject mdObject = (MdObject)topObject;
            String name = mdObject.getName();
            if (name == null)
                return false;

            String nameSuffix = parameters.getString(parameterName);
            if (nameSuffix == null || nameSuffix.isBlank())
                return false;

            List<String> nameSuffixes = List.of(nameSuffix.replace(" ", "").split(",")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            return nameSuffixes.stream().noneMatch(s -> name.indexOf(s, 1) != -1);
        };
    }
}
