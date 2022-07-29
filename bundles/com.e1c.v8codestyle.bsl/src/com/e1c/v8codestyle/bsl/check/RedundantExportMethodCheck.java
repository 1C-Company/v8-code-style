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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.findReferences.IReferenceFinder;
import org.eclipse.xtext.findReferences.IReferenceFinder.IResourceAccess;
import org.eclipse.xtext.findReferences.TargetURISet;
import org.eclipse.xtext.findReferences.TargetURIs;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceDescriptionsProvider;
import org.eclipse.xtext.resource.impl.DefaultReferenceDescription;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.resource.BslResource;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.EventSubscription;
import com._1c.g5.v8.dt.metadata.mdclass.ScheduledJob;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.google.inject.Inject;

/**
 * Checks the modules contain unused export procedures and functions.
 *
 * @author Artem Iliukhin
 */
public final class RedundantExportMethodCheck
    extends BasicCheck
{

    private static final Set<ModuleType> CHECKED_MODULES =
        Set.of(ModuleType.MANAGER_MODULE, ModuleType.COMMON_MODULE, ModuleType.OBJECT_MODULE);

    private static final String DEFAULT_EXCLUDE_REGION_NAME_LIST =
        String.join(",", ModuleStructureSection.PUBLIC.getNames()); //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_REGION_LIST = "excludeRegionName"; //$NON-NLS-1$

    private static final String CHECK_ID = "redundant-export-method"; //$NON-NLS-1$

    private static final Object TYPE_NAME = "NotifyDescription"; //$NON-NLS-1$

    private final IReferenceFinder referenceFinder;

    private final IResourceAccess workSpaceResourceAccess;

    private final IResourceDescriptionsProvider resourceDescriptionsProvider;

    private final IConfigurationProvider configurationProvider;

    private final IQualifiedNameProvider bslQualifiedNameProvider;

    @Inject
    public RedundantExportMethodCheck(IResourceAccess workSpaceResourceAccess, IReferenceFinder referenceFinder,
        IResourceDescriptionsProvider resourceDescriptionsProvider, IConfigurationProvider configurationProvider,
        IQualifiedNameProvider bslQualifiedNameProvider)
    {
        this.workSpaceResourceAccess = workSpaceResourceAccess;
        this.referenceFinder = referenceFinder;
        this.resourceDescriptionsProvider = resourceDescriptionsProvider;
        this.configurationProvider = configurationProvider;
        this.bslQualifiedNameProvider = bslQualifiedNameProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.RedundantExportCheck_Escess_title)
            .description(Messages.RedundantExportCheck_Excess_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .disable()
            //.extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(PARAMETER_EXCLUDE_REGION_LIST, String.class, DEFAULT_EXCLUDE_REGION_NAME_LIST,
                Messages.RedundantExportCheck_Exclude_title);

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;
        if (!method.isExport())
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);
        ModuleType moduleType = module.getModuleType();
        if (!CHECKED_MODULES.contains(moduleType))
        {
            return;
        }

        String name = method.getName();
        if (isNotExclusion(parameters, method) && !isScheduledJobOrEventSubscription(module, name)
            && !isNotifyDescription(module, name)
            && !haveCallerInOtherModule(method))
        {
            resultAceptor.addIssue(Messages.RedundantExportCheck_Unused_export_method, method,
                BslPackage.Literals.METHOD__EXPORT);
        }

    }

    private boolean isScheduledJobOrEventSubscription(Module module, String methodName)
    {
        ModuleType moduleType = module.getModuleType();
        if (!ModuleType.COMMON_MODULE.equals(moduleType))
        {
            return false;
        }

        Map<String, Set<String>> moduleMethod = new HashMap<>();
        Configuration configuration = configurationProvider.getConfiguration(module);
        List<ScheduledJob> jobs = configuration.getScheduledJobs();
        for (ScheduledJob job : jobs)
        {
            String[] items = job.getMethodName().split("[.]"); //$NON-NLS-1$
            if (items.length > 2)
            {
                moduleMethod.putIfAbsent(items[1], new HashSet<>());
                moduleMethod.get(items[1]).add(items[2]);
            }
        }

        List<EventSubscription> events = configuration.getEventSubscriptions();
        for (EventSubscription event : events)
        {
            String[] items = event.getHandler().split("[.]"); //$NON-NLS-1$
            if (items.length > 2)
            {
                moduleMethod.putIfAbsent(items[1], new HashSet<>());
                moduleMethod.get(items[1]).add(items[2]);
            }
        }

        QualifiedName fullyQualifiedName = bslQualifiedNameProvider.getFullyQualifiedName(module);
        if (fullyQualifiedName != null)
        {
            String moduleName = fullyQualifiedName.getSegment(1);
            Set<String> methodNames = moduleMethod.get(moduleName);
            if (methodNames != null && methodNames.contains(methodName))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isNotifyDescription(Module module, String name)
    {
        for (TreeIterator<EObject> iterator = module.eAllContents(); iterator.hasNext();)
        {
            EObject containedObject = iterator.next();
            if (containedObject instanceof OperatorStyleCreator
                && TYPE_NAME.equals(McoreUtil.getTypeName(((OperatorStyleCreator)containedObject).getType())))
            {
                List<Expression> params = ((OperatorStyleCreator)containedObject).getParams();
                if (!params.isEmpty() && params.get(0) instanceof StringLiteral)
                {
                    StringLiteral literal = (StringLiteral)params.get(0);
                    List<String> lines = literal.lines(true);
                    if (!lines.isEmpty() && lines.get(0).equals(name))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean haveCallerInOtherModule(Method object)
    {
        IProgressMonitor monitor = new NullProgressMonitor();
        Resource resource = object.eResource();
        if (resource instanceof BslResource)
        {
            ((BslResource)resource).setDeepAnalysis(true);
        }

        IResourceDescriptions indexData =
            resourceDescriptionsProvider.getResourceDescriptions(resource.getResourceSet());

        IReferenceFinder.Acceptor acceptor = new IReferenceFinder.Acceptor()
        {

            @Override
            public void accept(EObject source, URI sourceUri, EReference eReference, int index, EObject targetOrProxy,
                URI targetUri)
            {
                accept(new DefaultReferenceDescription(source, targetOrProxy, eReference, index, sourceUri));
            }

            @Override
            public void accept(IReferenceDescription description)
            {
                if (!description.getSourceEObjectUri().path().equals(description.getTargetEObjectUri().path()))
                {
                    monitor.setCanceled(true);
                }
            }
        };

        TargetURIs targetUris = new TargetURISet()
        {
            //
        };

        targetUris.addURI(EcoreUtil.getURI(object));

        referenceFinder.findAllReferences(targetUris, workSpaceResourceAccess, indexData, acceptor, monitor);

        return monitor.isCanceled();
    }

    private boolean isNotExclusion(ICheckParameters parameters, Method method)
    {
        RegionPreprocessor region = EcoreUtil2.getContainerOfType(method, RegionPreprocessor.class);
        while (region != null)
        {

            PreprocessorItem preprocessorItem = region.getItemAfter();
            if (preprocessorItem == null)
            {
                return true;
            }

            ICompositeNode node = NodeModelUtils.findActualNodeFor(preprocessorItem);
            if (node == null)
            {
                return true;
            }

            ICompositeNode nodeMethod = NodeModelUtils.findActualNodeFor(method);
            if (nodeMethod == null)
            {
                return true;
            }

            if (nodeMethod.getTotalOffset() < node.getTotalOffset())
            {
                String names = parameters.getString(PARAMETER_EXCLUDE_REGION_LIST);
                if (names != null)
                {
                    Set<String> set = Set.of(names.split(",")); //$NON-NLS-1$
                    for (String name : set)
                    {
                        if (StringUtils.equals(name, region.getName()))
                        {
                            return false;
                        }
                    }
                }
            }

            region = EcoreUtil2.getContainerOfType(region.eContainer(), RegionPreprocessor.class);
        }
        return true;
    }
}
