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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.e1c.g5.v8.dt.check.qfix.FixDescriptor;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFix;
import com.e1c.g5.v8.dt.check.qfix.IFixChange;
import com.e1c.g5.v8.dt.check.qfix.IFixChangeProcessor;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.IFixVariant;
import com.e1c.g5.v8.dt.check.qfix.components.BasicFix;

/**
 * Multi-variant xtext module fix definition
 *
 * @author Alexander Tretyakevich - Initial contribution
 * @author Vadim Geraskin - Additional features
 */
public abstract class MultiVariantXtextBslModuleFix
    extends BasicFix<SingleVariantXtextBslModuleFixContext>
    implements IFix<SingleVariantXtextBslModuleFixContext>
{
    private Collection<IFixVariant<SingleVariantXtextBslModuleFixContext>> fixVariants = new HashSet<>();

    @Override
    public Class<SingleVariantXtextBslModuleFixContext> getRequiredContextType()
    {
        return SingleVariantXtextBslModuleFixContext.class;
    }

    @Override
    public Collection<IFixVariant<SingleVariantXtextBslModuleFixContext>> getVariants(
        SingleVariantXtextBslModuleFixContext context, IFixSession session)
    {
        return fixVariants;
    }

    @Override
    public void onRegistration(FixDescriptor registrationContext)
    {
        VariantXtextBslModuleFixChangeProcessor changeProcessor = new VariantXtextBslModuleFixChangeProcessor();
        registrationContext.setChangeProcessor(changeProcessor);
        buildVariants();
    }

    /**
     * Creates and sets up fix variants
     */
    protected abstract void buildVariants();

    /**
     * Provides a builder of the quick fix variant
     *
     * @author Vadim Geraskin
     */
    protected static class VariantBuilder
    {
        private final MultiVariantXtextBslModuleFix fix;
        private IMultiVariantXtextModuleFixChangeDelegate delegate;
        private String description;
        private String details;
        private boolean isInteractive;

        /**
         * Creates variant builder instance
         *
         * @param fix the reference to the model fix, cannot be {@code null}
         */
        public VariantBuilder(MultiVariantXtextBslModuleFix fix)
        {
            this.fix = fix;
        }

        /**
         * Static variant builder creator
         *
         * @param fix the reference to the model fix, cannot be {@code null}
         * @return the instance of the variant builder, never {@code null}
         */
        public static VariantBuilder create(MultiVariantXtextBslModuleFix fix)
        {
            return new VariantBuilder(fix);
        }

        /**
         * Creates code change delegate
         *
         * @param delegate change delegate, cannot be {@code null}
         * @return the instance of the variant builder, never {@code null}
         */
        public VariantBuilder change(IMultiVariantXtextModuleFixChangeDelegate delegate)
        {
            this.delegate = delegate;
            return this;
        }

        /**
         * Sets the modification model type - either interactive (UI) or not
         *
         * @param isInteractive {@code true} if quick fix supports inbteractive (UI) model, {@code false} otherwise
         * @return the instance of the variant builder, never {@code null}
         */
        public VariantBuilder interactive(boolean isInteractive)
        {
            this.isInteractive = isInteractive;
            return this;
        }

        /**
         * Creates variant description and details messages
         *
         * @param description the short description, cannot be {@code null}
         * @param details the detailed message, can be {@code null}
         * @return the instance of the variant builder, never {@code null}
         */
        public VariantBuilder description(String description, String details)
        {
            this.description = description;
            this.details = details;
            return this;
        }

        /**
         * Finalizes the variant builder, registers the variant
         */
        public void build()
        {
            IFixVariant<SingleVariantXtextBslModuleFixContext> variant =
                new MultiVariantXtextBslModuleVariant<>(delegate, description, details, isInteractive);
            fix.fixVariants.add(variant);
        }
    }

    private static class MultiVariantXtextBslModuleVariant<C extends SingleVariantXtextBslModuleFixContext>
        implements IFixVariant<C>
    {
        private final IMultiVariantXtextModuleFixChangeDelegate delegate;
        private final String description;
        private final String details;
        private final boolean isInteractive;

        /**
         * Creates {@link MultiVariantXtextBslModuleVariant} instance
         *
         * @param delegate the delegate to be executed, cannot be {@code null}
         * @param description the fix variant short description, cannot be {@code null}
         * @param details the fix variant detailed description, can be {@code null}
         * @param isInteractive {@code true} if quick fix supports interactive (UI) model, {@code false} otherwise
         */
        MultiVariantXtextBslModuleVariant(IMultiVariantXtextModuleFixChangeDelegate delegate, String description,
            String details, boolean isInteractive)
        {
            this.delegate = delegate;
            this.description = description;
            this.details = details;
            this.isInteractive = true;
        }

        @Override
        public Collection<IFixChange> prepareChanges(C context, IFixSession session)
        {
            if (context.getTargetModuleUri() == null)
            {
                return Collections.emptyList();
            }

            return Collections.singleton(new VariantXtextBslModuleFixChange(delegate, isInteractive));
        }

        @Override
        public FixVariantDescriptor describeChanges(C context, IFixSession session)
        {
            return new FixVariantDescriptor(description, details);
        }
    }

    private static final class VariantXtextBslModuleFixChange
        implements IFixChange
    {
        private final IMultiVariantXtextModuleFixChangeDelegate delegate;
        private final boolean isInteractive;

        /**
         * Creates {@link VariantXtextBslModuleFixChange} instance
         *
         * @param delegate the delegate to be executed, cannot be {@code null}
         * @param isInteractive {@code true} if quick fix supports interactive (UI) model, {@code false} otherwise
         */
        VariantXtextBslModuleFixChange(IMultiVariantXtextModuleFixChangeDelegate delegate, boolean isInteractive)
        {
            this.delegate = delegate;
            this.isInteractive = isInteractive;
        }

        /**
         * Executes the quick fix resolution code
         *
         * @param context the quick fix context, cannot be {@code null}
         * @param session the quick fix session, cannot be {@code null}
         */
        public void applyFix(SingleVariantXtextBslModuleFixContext context, IFixSession session)
        {
            IXtextBslModuleFixModel fixModel = context.getModel(session, isInteractive);
            if (fixModel instanceof IXtextInteractiveBslModuleFixModel)
            {
                ((IXtextDocument)fixModel.getDocument()).readOnly(new IUnitOfWork.Void<XtextResource>()
                {
                    @Override
                    public void process(XtextResource state) throws Exception
                    {
                        delegate.applyFix(context, session, state, fixModel);
                    }
                });
            }
            else
            {
                // Nothing at the moment
            }
        }
    }

    /*
     * Variant fix change processor
     */
    private static final class VariantXtextBslModuleFixChangeProcessor
        implements IFixChangeProcessor<VariantXtextBslModuleFixChange, SingleVariantXtextBslModuleFixContext>
    {
        @Override
        public void applyFix(VariantXtextBslModuleFixChange fixChange, SingleVariantXtextBslModuleFixContext context,
            IFixSession session)
        {
            fixChange.applyFix(context, session);
        }

        @Override
        public Class<VariantXtextBslModuleFixChange> getProcessedFixType()
        {
            return VariantXtextBslModuleFixChange.class;
        }
    }
}
