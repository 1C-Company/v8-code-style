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
 *     Aleksandr Kapralov - issue #17
 *******************************************************************************/
package com.e1c.v8codestyle.internal.bsl;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.findReferences.IReferenceFinder;
import org.eclipse.xtext.findReferences.IReferenceFinder.IResourceAccess;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptionsProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bm.xtext.BmAwareResourceSetProvider;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.common.IStaticExpressionValueComputer;
import com._1c.g5.v8.dt.bsl.contextdef.IBslModuleContextDefService;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.model.resource.owner.IBslOwnerComputerService;
import com._1c.g5.v8.dt.bsl.resource.BslEventsService;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.resource.ExportMethodProvider;
import com._1c.g5.v8.dt.bsl.resource.TypesComputer;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.bsl.stringliteral.contenttypes.IStringLiteralTypeComputer;
import com._1c.g5.v8.dt.bsl.typesystem.ExportMethodTypeProvider;
import com._1c.g5.v8.dt.core.naming.ITopObjectFqnGenerator;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.platform.version.IRuntimeVersionSupport;
import com._1c.g5.wiring.AbstractServiceAwareModule;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.e1c.g5.v8.dt.check.settings.ICheckRepository;

/**
 * The external dependencies for plugin
 *
 * @author Dmitriy Marmyshev
 */
@SuppressWarnings("restriction")
class ExternalDependenciesModule
    extends AbstractServiceAwareModule
{

    ExternalDependenciesModule(Plugin plugin)
    {
        super(plugin);
    }

    @Override
    protected void doConfigure()
    {
        bind(IResourceLookup.class).toService();
        bind(IRuntimeVersionSupport.class).toService();
        bind(IV8ProjectManager.class).toService();
        bind(IBslPreferences.class).toService();
        bind(IQualifiedNameConverter.class).toService();
        bind(IBslModuleContextDefService.class).toService();
        bind(IBmModelManager.class).toService();
        bind(INamingService.class).toService();
        bind(ITopObjectFqnGenerator.class).toService();

        bind(ICheckRepository.class).toService();
        bind(IFixRepository.class).toService();

        URI uri = URI.createURI("*.bsl"); //$NON-NLS-1$
        final IResourceServiceProvider rsp = IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(uri);

        bind(IStringLiteralTypeComputer.class).toProvider(() -> rsp.get(IStringLiteralTypeComputer.class));
        bind(IResourceDescription.Manager.class).toProvider(() -> rsp.get(IResourceDescription.Manager.class));
        bind(BslEventsService.class).toProvider(() -> rsp.get(BslEventsService.class));
        bind(TypesComputer.class).toProvider(() -> rsp.get(TypesComputer.class));
        bind(DynamicFeatureAccessComputer.class).toProvider(() -> rsp.get(DynamicFeatureAccessComputer.class));
        bind(ExportMethodProvider.class).toProvider(() -> rsp.get(ExportMethodProvider.class));
        bind(ExportMethodTypeProvider.class).toProvider(() -> rsp.get(ExportMethodTypeProvider.class));
        bind(BslMultiLineCommentDocumentationProvider.class)
            .toProvider(() -> rsp.get(BslMultiLineCommentDocumentationProvider.class));
        bind(IBslOwnerComputerService.class).toProvider(() -> rsp.get(IBslOwnerComputerService.class));
        bind(IScopeProvider.class).toProvider(() -> rsp.get(IScopeProvider.class));
        bind(IQualifiedNameProvider.class).toProvider(() -> rsp.get(IQualifiedNameProvider.class));

        bind(IReferenceFinder.class).toProvider(() -> rsp.get(IReferenceFinder.class));
        bind(IResourceDescriptionsProvider.class).toProvider(() -> rsp.get(IResourceDescriptionsProvider.class));
        bind(IResourceAccess.class).toService();
        bind(ResourceDescriptionsProvider.class).toService();
        bind(IConfigurationProvider.class).toService();
        bind(BslGrammarAccess.class).toProvider(() -> rsp.get(BslGrammarAccess.class));
        bind(BmAwareResourceSetProvider.class).toProvider(() -> rsp.get(BmAwareResourceSetProvider.class));
        bind(IStaticExpressionValueComputer.class).toProvider(() -> rsp.get(IStaticExpressionValueComputer.class));
    }
}
