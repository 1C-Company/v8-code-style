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
package com.e1c.v8codestyle;

/**
 * The project functional option.
 *
 * @author Dmitriy Marmyshev
 */
public final class ProjectOption
{

    private final String optionId;

    private final String presentation;

    private final String description;

    private final int order;

    private final boolean defaultValue;

    private final int hashCode;

    /**
     * Instantiates a new project option.
     *
     * @param optionId the option id, cannot be {@code null}.
     * @param order the order, cannot be {@code null}.
     * @param presentation the presentation, cannot be {@code null}.
     * @param description the description, may be {@code null}.
     * @param defaultValue the default value, cannot be {@code null}.
     */
    public ProjectOption(String optionId, int order, String presentation, String description, boolean defaultValue)
    {
        this.optionId = optionId;
        this.order = order;
        this.presentation = presentation;
        this.description = description;
        this.defaultValue = defaultValue;
        this.hashCode = getHashCode();
    }

    public String getOptionId()
    {
        return optionId;
    }

    public String getPresentation()
    {
        return presentation;
    }

    public String getDescription()
    {
        return description;
    }

    public int getOrder()
    {
        return order;
    }

    public boolean getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    private int getHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((optionId == null) ? 0 : optionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        ProjectOption other = (ProjectOption)obj;
        if (optionId == null)
        {
            if (other.optionId != null)
            {
                return false;
            }
        }
        else if (!optionId.equals(other.optionId))
        {
            return false;
        }
        return true;
    }

}
