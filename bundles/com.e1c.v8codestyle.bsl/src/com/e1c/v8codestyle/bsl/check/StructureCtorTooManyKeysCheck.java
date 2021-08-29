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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Checks Structure constructor string literal that contains too may keys.
 * Default value is 3 keys in literal. Otherwise it need to use {@code Structure.Insert()} functions to insert all keys.
 *
 * @author Dmitriy Marmyshev
 */
public class StructureCtorTooManyKeysCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "structure-consructor-too-many-keys"; //$NON-NLS-1$

    private static final String PARAM_MAX_STRUCTURE_KEYS = "maxKeys"; //$NON-NLS-1$

    private static final String DEFAULT_MAX_STRUCTURE_KEYS = "3"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.StructureCtorTooManyKeysCheck_title)
            .description(Messages.StructureCtorTooManyKeysCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR)
            .parameter(PARAM_MAX_STRUCTURE_KEYS, Integer.class, DEFAULT_MAX_STRUCTURE_KEYS,
                Messages.StructureCtorTooManyKeysCheck_Maximum_structure_constructor_keys);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof OperatorStyleCreator))
        {
            return;
        }

        OperatorStyleCreator osc = (OperatorStyleCreator)object;
        if (IEObjectTypeNames.STRUCTURE.equals(McoreUtil.getTypeName(osc.getType())) && !osc.getParams().isEmpty()
            && osc.getParams().get(0) instanceof StringLiteral)
        {
            StringLiteral literal = (StringLiteral)osc.getParams().get(0);

            String content = String.join("", literal.lines(true)); //$NON-NLS-1$
            String[] keys = content.replace(" ", "").split(","); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            int maxKeys = parameters.getInt(PARAM_MAX_STRUCTURE_KEYS);
            if (!monitor.isCanceled() && keys.length > maxKeys)
            {
                String message = MessageFormat.format(
                    Messages.StructureCtorTooManyKeysCheck_Structure_constructor_has_more_than__0__keys, maxKeys);
                resultAceptor.addIssue(message, literal, STRING_LITERAL__LINES);
            }
        }
    }

}
