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
package com.e1c.v8codestyle.bsl.qfix;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD__EXPORT;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.model.Method;
import com.e1c.g5.v8.dt.bsl.check.qfix.IXtextBslModuleFixModel;
import com.e1c.g5.v8.dt.bsl.check.qfix.SingleVariantXtextBslModuleFix;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * The fix to remove export keyword.
 *
 * @author Dmitriy Marmyshev
 */
public class RemoveExportFix
    extends SingleVariantXtextBslModuleFix
{
    private final CheckUid checkUid;

    @SuppressWarnings("nls")
    public static Collection<String> getCheckIds()
    {
        return Set.of("export-method-in-command-form-module", "redundant-export-method");
    }

    /**
     * Instantiates a new removes the export keyword.
     *
     * @param checkId the check id, cannot be {@code null}.
     */
    public RemoveExportFix(CheckUid checkId)
    {
        this.checkUid = checkId;
    }

    @Override
    public CheckUid getCheckId()
    {
        return checkUid;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description(Messages.RemoveExportFix_Remove_export_keyword_des)
            .details(Messages.RemoveExportFix_Remove_export_keyword_det);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        if (model.getElement() instanceof Method && ((Method)model.getElement()).isExport())
        {
            List<INode> nodes = NodeModelUtils.findNodesForFeature(model.getElement(), METHOD__EXPORT);
            if (nodes.isEmpty())
            {
                IStatus status =
                    BslPlugin.createErrorStatus("RemoveExportFix: cannot get node for method export keyword: " //$NON-NLS-1$
                        + EcoreUtil.getURI(model.getElement()), null);
                BslPlugin.log(status);
                return null;
            }
            INode node = nodes.get(0);
            return new DeleteEdit(node.getTotalOffset(), node.getTotalLength());
        }
        return null;
    }

}
