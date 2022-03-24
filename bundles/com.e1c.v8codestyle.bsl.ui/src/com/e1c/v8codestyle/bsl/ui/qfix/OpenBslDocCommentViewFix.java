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
package com.e1c.v8codestyle.bsl.ui.qfix;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.resource.XtextResource;

import com._1c.g5.v8.dt.bsl.ui.quickfix.BslQuickFixUtil;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.IXtextInteractiveBslModuleFixModel;
import com.e1c.v8codestyle.bsl.qfix.external.SingleVariantXtextBslModuleFix;
import com.e1c.v8codestyle.internal.bsl.ui.UiPlugin;
import com.e1c.v8codestyle.internal.bsl.ui.views.BslDocCommentView;

/**
 * The fix for all strict types checks that allows to remove annotation.
 *
 * @author Dmitriy Marmyshev
 */
public class OpenBslDocCommentViewFix
    extends SingleVariantXtextBslModuleFix
{

    @SuppressWarnings("nls")
    public static Collection<String> getCheckIds()
    {
        return Set.of("doc-comment-field-type-strict", "doc-comment-collection-item-type",
            "constructor-function-return-section", "doc-comment-use-minus",
            "doc-comment-export-function-return-section", "doc-comment-field-name", "doc-comment-field-type",
            "doc-comment-complex-type-with-link", "doc-comment-return-section-type",
            "doc-comment-description-ends-on-dot", "doc-comment-field-in-description-suggestion",
            "doc-comment-parameter-in-description-suggestion", "doc-comment-parameter-section",
            "doc-comment-procedure-return-section", "doc-comment-ref-link", "doc-comment-type");
    }

    private final CheckUid checkUid;

    /**
     * Instantiates a new open BSL doc comment view fix.
     *
     * @param checkId the check id, cannot be {@code null}.
     */
    public OpenBslDocCommentViewFix(CheckUid checkId)
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
        configurer.interactive(true)
            .description(Messages.OpenBslDocCommentViewFix_Description)
            .details(Messages.OpenBslDocCommentViewFix_Details);
    }

    @Override
    protected TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model) throws BadLocationException
    {
        IXtextInteractiveBslModuleFixModel interactiveModel = (IXtextInteractiveBslModuleFixModel)model;

        Integer offset = model.getIssue().getOffset() + 1;
        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display.isDisposed())
        {
            return null;
        }

        display.asyncExec(() -> {
            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(BslDocCommentView.ID);
            }
            catch (PartInitException e)
            {
                UiPlugin.logError(e);
            }
        });

        ITextViewer viewer = BslQuickFixUtil.getTextViewer(interactiveModel.getModificationContext());
        if (viewer != null && !display.isDisposed())
        {
            display.asyncExec(() -> {
                viewer.revealRange(offset, 1);
                viewer.getTextWidget().setFocus();
                viewer.setSelectedRange(offset, 1);
            });
        }
        return null;
    }
}
