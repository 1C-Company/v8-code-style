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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check that comment is added to the export procedure (function)
 *
 * @author Olga Bozhko
 */
public class ExportProcedureMissingCommentCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "export-procedure-missing-comment"; //$NON-NLS-1$
    private static final int STANDARD_NUM = 453;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportProcedureMissingCommentCheck_title)
            .description(Messages.ExportProcedureMissingCommentCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(STANDARD_NUM, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        Method method = (Method)eObject;

        if (verifyTopRegion(getTopParentRegion(method)) && method.isExport()
            && isMethodHasNoComment(NodeModelUtils.findActualNodeFor(method)))
        {
            resultAceptor.addIssue(
                MessageFormat.format(Messages.ExportProcedureMissingCommentCheck_Export_procedure_missing_comment,
                    method.getName()),
                McorePackage.Literals.NAMED_ELEMENT__NAME);
        }
    }

    private static boolean verifyTopRegion(Optional<RegionPreprocessor> regionTop)
    {
        if (regionTop.isEmpty())
        {
            return false;
        }
        return regionTop.get().getName().equals(ModuleStructureSection.PUBLIC.getNames()[0])
            || regionTop.get().getName().equals(ModuleStructureSection.PUBLIC.getNames()[1]);
    }

    private static boolean isMethodHasNoComment(INode root)
    {
        if (root != null)
        {
            for (ILeafNode node : root.getLeafNodes())
            {
                if (BslCommentUtils.isCommentNode(node))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
