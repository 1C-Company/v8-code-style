/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.qfix.external;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.validation.Issue.IssueImpl;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.common.Pair;
import com._1c.g5.v8.dt.common.StringUtils;
import com.e1c.g5.v8.dt.check.qfix.FixDescriptor;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFixChange;
import com.e1c.g5.v8.dt.check.qfix.IFixChangeProcessor;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.SingleVariantBasicFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Single variant xtext bsl module fix common class for multi-variant quick fixes
 *
 * @author Vadim Geraskin
 */
public abstract class SingleVariantXtextBslModuleFix
    extends SingleVariantBasicFix<SingleVariantXtextBslModuleFixContext>
{
    private BiFunction<SingleVariantXtextBslModuleFixContext, IFixSession, Pair<String, String>> descriptionSupplier;
    private boolean isInteractive;

    @Override
    public Collection<IFixChange> prepareChanges(SingleVariantXtextBslModuleFixContext context, IFixSession session)
    {
        Module module = session.getModule(context.getTargetModuleUri());
        if (module == null)
        {
            return Collections.emptyList();
        }

        return prepareChanges(context, session, this::applyChanges);
    }

    @Override
    public final FixVariantDescriptor describeChanges(SingleVariantXtextBslModuleFixContext context,
        IFixSession session)
    {
        Pair<String, String> description = descriptionSupplier.apply(context, session);
        return new FixVariantDescriptor(description.first, description.second);
    }

    @Override
    public final void onRegistration(FixDescriptor registrationContext)
    {
        registrationContext.setChangeProcessor(new SingleVariantModuleFixChangeProcessor());

        FixConfigurer configurer = new FixConfigurer();
        configureFix(configurer);
        applyConfiguration(configurer);
    }

    @Override
    public Class<SingleVariantXtextBslModuleFixContext> getRequiredContextType()
    {
        return SingleVariantXtextBslModuleFixContext.class;
    }

    /**
     * Configures the fix. The developer could provide descreptive/filtering information for the fix here
     *
     * @param configurer The configurer of the fix. May not be {@code null}
     */
    protected void configureFix(FixConfigurer configurer)
    {
        // Nothing by default
    }

    /**
     * Modification of code to be overriden by clients
     *
     * @param state the xtext resource, cannot be {@code null}
     * @param model xtext bsl module fix model facade, cannot be {@code null}
     * @return {@link TextEdit}, can be {@code null}
     * @throws BadLocationException
     */
    protected abstract TextEdit fixIssue(XtextResource state, IXtextBslModuleFixModel model)
        throws BadLocationException;

    protected void applyChanges(SingleVariantXtextBslModuleFixContext context, IFixSession session)
    {
        IssueImpl issue = context.getIssue();

        IXtextBslModuleFixModel fixModel = context.getModel(session, isInteractive);
        if (fixModel instanceof IXtextInteractiveBslModuleFixModel)
        {
            IModificationContext modificationContext =
                ((XtextInteractiveBslModuleFixModel)fixModel).getModificationContext();

            ExternalQuickfixModification<EObject> quickFixModification = new ExternalQuickfixModification<>(issue,
                EObject.class, element -> fixIssue((IXtextInteractiveBslModuleFixModel)fixModel, context));

            try
            {
                quickFixModification.apply(modificationContext);
            }
            catch (Exception e)
            {
                BslPlugin.log(BslPlugin.createErrorStatus("Error occured when applying quick fix", e)); //$NON-NLS-1$
            }
        }
        else
        {
            // Nothing at the moment
        }
    }

    private TextEdit fixIssue(IXtextInteractiveBslModuleFixModel fixModel,
        SingleVariantXtextBslModuleFixContext context)
    {
        Issue issue = fixModel.getIssue();
        if (issue.getOffset() == null)
        {
            return null;
        }

        List<TextEdit> textEdits = new ArrayList<>();
        IXtextDocument document = (IXtextDocument)fixModel.getDocument();
        document.readOnly(new IUnitOfWork.Void<XtextResource>()
        {
            @Override
            public void process(XtextResource state) throws Exception
            {
                TextEdit te = fixIssue(state, fixModel);
                if (te != null)
                {
                    textEdits.add(te);
                }
            }
        });
        return textEdits != null && !textEdits.isEmpty() ? textEdits.get(0) : null;
    }

    private Collection<IFixChange> prepareChanges(SingleVariantXtextBslModuleFixContext context, IFixSession session,
        VariantXtextModuleFixChangeDelegate changeDelegate)
    {
        return Collections.singleton(new SingleVariantModuleFixChange(changeDelegate));
    }

    /*
     * Applies configuration provided by the fix developer
     */
    private void applyConfiguration(FixConfigurer configurer)
    {
        this.descriptionSupplier = configurer.descriptionSupplier;
        if (this.descriptionSupplier == null)
        {
            // Description isn't specified - adding the safety loopback
            this.descriptionSupplier = (context, session) -> Pair.newPair(StringUtils.EMPTY, StringUtils.EMPTY);
        }
        // TODO: change the assignment to configurer.isInteractive once non-interactive (model) quick
        // fixes are developed
        this.isInteractive = configurer.isInteractive;
    }

    /*
     * Single variant fix change unique for the SingleVariantModelBasicFix
     */
    private final static class SingleVariantModuleFixChange
        implements IFixChange
    {
        private final VariantXtextModuleFixChangeDelegate delegate;

        public SingleVariantModuleFixChange(VariantXtextModuleFixChangeDelegate delegate)
        {
            this.delegate = delegate;
        }

        public void applyFix(SingleVariantXtextBslModuleFixContext context, IFixSession session)
        {
            delegate.applyFix(context, session);
        }
    }

    /*
     * Single variant fix change processor unique for the SingleVariantModelBasicFix
     */
    private final static class SingleVariantModuleFixChangeProcessor
        implements IFixChangeProcessor<SingleVariantModuleFixChange, SingleVariantXtextBslModuleFixContext>
    {
        @Override
        public void applyFix(SingleVariantModuleFixChange fixChange, SingleVariantXtextBslModuleFixContext context,
            IFixSession session)
        {
            fixChange.applyFix(context, session);
        }

        @Override
        public Class<SingleVariantModuleFixChange> getProcessedFixType()
        {
            return SingleVariantModuleFixChange.class;
        }
    }

    /**
     * Configuration container for the {@link SingleVariantXtextBslModuleFixContext} descendants allowing them to specify
     * fix parameters via the pure Java API
     *
     * @author Alexander Tretyakevich
     */
    protected static final class FixConfigurer
    {
        private BiFunction<SingleVariantXtextBslModuleFixContext, IFixSession, Pair<String, String>> descriptionSupplier;
        private boolean isInteractive;
        private String description = StringUtils.EMPTY;
        private String details = StringUtils.EMPTY;

        /**
         * Sets the dynamic description supplier for the fix. The description may be formed using the data from
         * the {@link SingleVariantXtextBslModuleFixContext}
         *
         * @param descriptionSupplier The supplier to set. May not be {@code null}
         * @return the instance of the fix configurer, never {@code null}
         */
        public FixConfigurer description(
            BiFunction<SingleVariantXtextBslModuleFixContext, IFixSession, Pair<String, String>> descriptionSupplier)
        {
            this.descriptionSupplier = descriptionSupplier;
            return this;
        }

        /**
         * Sets the static description for the fix
         *
         * @param description The description to set. May not be {@code null}
         * @return the instance of the fix configurer, never {@code null}
         */
        public FixConfigurer description(String description)
        {
            this.description = description;
            descriptionSupplier = (context, session) -> Pair.newPair(description, details);
            return this;
        }

        /**
         * Sets the static details for the fix
         *
         * @param description The description to set. May not be {@code null}
         * @return the instance of the fix configurer, never {@code null}
         */
        public FixConfigurer details(String details)
        {
            this.details = details;
            descriptionSupplier = (context, session) -> Pair.newPair(description, details);
            return this;
        }

        /**
         * Sets the modification model type - either interactive (UI) or not
         *
         * @param isInteractive {@code true} if quick fix supports inbteractive (UI) model, {@code false} otherwise
         * @return the instance of the fix configurer, never {@code null}
         */
        public FixConfigurer interactive(boolean isInteractive)
        {
            this.isInteractive = isInteractive;
            return this;
        }
    }
}
