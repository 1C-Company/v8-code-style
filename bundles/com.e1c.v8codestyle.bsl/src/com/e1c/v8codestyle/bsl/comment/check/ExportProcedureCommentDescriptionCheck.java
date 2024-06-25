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
package com.e1c.v8codestyle.bsl.comment.check;

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.dt.core.api.platform.BmOperationContext;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Check that comment for the export procedure (function) contains Description section
 *
 * @author Olga Bozhko
 */
public class ExportProcedureCommentDescriptionCheck
    extends AbstractDocCommentTypeCheck
{
    private static final String CHECK_ID = "doc-comment-export-procedure-description-section"; //$NON-NLS-1$

    /**
     * Constructs an instance
     *
     * @param resourceLookup service for look up workspace resources, see {@link IResourceLookup}, cannot be <code>null</code>
     * @param namingService service for getting names of EDT object and resources, cannot be <code>null</code>
     * @param bmModelManager service for getting instance of Bm Model by {@link EObject}, cannot be <code>null</code>
     * @param v8ProjectManager {@link IV8ProjectManager} for getting {@link IV8Project} by {@link EObject}, cannot be <code>null</code>
     */
    @Inject
    public ExportProcedureCommentDescriptionCheck(IResourceLookup resourceLookup, INamingService namingService,
        IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager)
    {
        super(resourceLookup, namingService, bmModelManager, v8ProjectManager);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExportMethodCommentDescriptionCheck_title)
            .description(Messages.ExportMethodCommentDescriptionCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(453, getCheckId(), BslPlugin.PLUGIN_ID))
            .delegate(BslDocumentationComment.class);
    }

    @Override
    protected void checkDocumentationCommentObject(IDescriptionPart object, BslDocumentationComment root,
        DocumentationCommentResultAcceptor resultAceptor, ICheckParameters parameters,
        BmOperationContext typeComputationContext, IProgressMonitor monitor)
    {
        if (!root.getMethod().isExport())
        {
            return;
        }

        BslDocumentationComment docComment = (BslDocumentationComment)object;
        BslDocumentationComment.Description description = docComment.getDescription();
        if (description != null && description.getParts().isEmpty()
            && verifyTopRegion(getTopParentRegion(root.getMethod())))
        {
            resultAceptor.addIssue(MessageFormat.format(
                Messages.ExportMethodCommentDescriptionCheck_Missing_Description_in_export_procedure_comment,
                root.getMethod().getName()), root.getMethod(), NAMED_ELEMENT__NAME);
        }
    }

    private static Optional<RegionPreprocessor> getTopParentRegion(EObject object)
    {
        EObject parent = object.eContainer();
        PreprocessorItem lastItem = null;
        RegionPreprocessor region = null;
        do
        {
            if (parent instanceof RegionPreprocessor)
            {
                RegionPreprocessor parentRegion = (RegionPreprocessor)parent;
                if (lastItem != null && parentRegion.getItem().equals(lastItem))
                {
                    region = parentRegion;
                }
                else
                {
                    lastItem = null;
                }
            }
            else if (parent instanceof PreprocessorItem)
            {
                lastItem = (PreprocessorItem)parent;
            }
            parent = parent.eContainer();
        }
        while (parent != null);

        return Optional.ofNullable(region);
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

}
