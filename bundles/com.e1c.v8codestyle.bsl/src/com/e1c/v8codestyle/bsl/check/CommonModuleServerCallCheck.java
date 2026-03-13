/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.COMMON_MODULE__SERVER_CALL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;
import static org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider.PERSISTED_DESCRIPTIONS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.findReferences.IReferenceFinder;
import org.eclipse.xtext.findReferences.IReferenceFinder.IResourceAccess;
import org.eclipse.xtext.findReferences.TargetURISet;
import org.eclipse.xtext.findReferences.TargetURIs;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceDescriptionsProvider;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.dt.bsl.common.IBslPreferences;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.typesystem.ExportMethodTypeProvider;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com.e1c.g5.dt.core.api.naming.INamingService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.strict.check.AbstractTypeCheck;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.e1c.v8codestyle.md.CommonModuleTypes;
import com.google.inject.Inject;

/**
 * Check correct set falg "Server call" on common module.
 *
 *  @author Ivan Sergeev
 */
@SuppressWarnings("restriction")
public class CommonModuleServerCallCheck
    extends AbstractTypeCheck
{
    private static final String CHECK_ID = "common-module-server-call"; //$NON-NLS-1$

    private final IReferenceFinder referenceFinder;

    private final IResourceAccess workSpaceResourceAccess;

    private final IResourceDescriptionsProvider resourceDescriptionsProvider;

    @Inject
    public CommonModuleServerCallCheck(IResourceLookup resourceLookup, IBslPreferences bslPreferences,
        IQualifiedNameConverter qualifiedNameConverter, ExportMethodTypeProvider exportMethodTypeProvider,
        INamingService namingService, IBmModelManager bmModelManager, IV8ProjectManager v8ProjectManager,
        IResourceAccess workSpaceResourceAccess, IReferenceFinder referenceFinder,
        IResourceDescriptionsProvider resourceDescriptionsProvider)
    {
        super(resourceLookup, bslPreferences, qualifiedNameConverter, namingService, bmModelManager, v8ProjectManager);
        this.workSpaceResourceAccess = workSpaceResourceAccess;
        this.referenceFinder = referenceFinder;
        this.resourceDescriptionsProvider = resourceDescriptionsProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.CommonModuleServerCallCheck_Title)
            .description(Messages.CommonModuleServerCallCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .extension(new StandardCheckExtension(469, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(new TopObjectFilterExtension())
            .topObject(COMMON_MODULE)
            .checkTop()
            .features(MD_OBJECT__NAME, COMMON_MODULE__SERVER_CALL);
    }


    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IBmTransaction bmTransaction, IProgressMonitor progressMonitor)
    {
        CommonModule commonModule = (CommonModule)object;
        if (commonModule.isServerCall())
        {
            IV8Project project = v8ProjectManager.getProject(commonModule);
            ScriptVariant scriptVariant = project == null ? ScriptVariant.ENGLISH : project.getScriptVariant();
            String suffixe = CommonModuleTypes.SERVER_CALL.getNameSuffix(scriptVariant);
            String name = commonModule.getName();
            if (!(name.endsWith(suffixe)))
            {
                List<Boolean> callInClient = new ArrayList<>();
                List<Method> methods = commonModule.getModule().allMethods();
                for (Method method : methods)
                {
                    if (!method.isExport())
                    {
                        continue;
                    }
                    callInClient.add(callInOtherModule(method, progressMonitor, bmTransaction));
                }
                if (!callInClient.isEmpty() && !callInClient.contains(true))
                {
                    resultAcceptor.addIssue(Messages.CommonModuleServerCallCheck_Issue, MD_OBJECT__NAME);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private boolean callInOtherModule(Method object, IProgressMonitor monitor, IBmTransaction bmTransaction)
    {
        boolean clientCall = false;
        List<URI> URIs = new ArrayList<>();
        IProgressMonitor subMonitor = new NullProgressMonitor()
        {
            @Override
            public boolean isCanceled()
            {
                return super.isCanceled() || monitor.isCanceled();
            }
        };

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getLoadOptions().put(PERSISTED_DESCRIPTIONS, Boolean.TRUE);

        IResourceDescriptions indexData = resourceDescriptionsProvider.getResourceDescriptions(resourceSet);

        IReferenceFinder.Acceptor acceptor = new IReferenceFinder.Acceptor()
        {
            @Override
            public void accept(EObject source, URI sourceUri, EReference eReference, int index, EObject targetOrProxy,
                URI targetUri)
            {
                if (subMonitor.isCanceled())
                {
                    return;
                }

                if (!sourceUri.path().equals(targetUri.path()))
                {
                    subMonitor.setCanceled(true);
                }
            }

            @Override
            public void accept(IReferenceDescription description)
            {

                if (subMonitor.isCanceled())
                {
                    return;
                }

                if (!description.getSourceEObjectUri().path().equals(description.getTargetEObjectUri().path()))
                {
                    URIs.add(description.getSourceEObjectUri());
                }
            }
        };

        TargetURIs targetUris = new TargetURISet()
        {
            //
        };

        targetUris.addURI(EcoreUtil.getURI(object));

        referenceFinder.findAllReferences(targetUris, workSpaceResourceAccess, indexData, acceptor, subMonitor);

        if (!URIs.isEmpty())
        {
            List<Boolean> checkClientCall = new ArrayList<>();
            for (URI uri : URIs)
            {
                EObject obj = bmTransaction.getExternalObjectByUri(uri);
                Method method = EcoreUtil2.getContainerOfType(obj, Method.class);
                Environmental environmental = EcoreUtil2.getContainerOfType(method, Environmental.class);
                Environments environments = environmental.environments();

                if (environments.contains(Environment.WEB_CLIENT) || environments.contains(Environment.MOBILE_CLIENT)
                    || environments.contains(Environment.THIN_CLIENT)
                    || environments.contains(Environment.MOBILE_THIN_CLIENT))
                {
                    checkClientCall.add(true);
                }
                else if (environments.contains(Environment.CLIENT) || environments.contains(Environment.MNG_CLIENT))
                {
                    checkClientCall.add(false);
                }
            }
            if (checkClientCall.contains(true))
            {
                clientCall = true;
            }
        }
        return clientCall;
    }
}
