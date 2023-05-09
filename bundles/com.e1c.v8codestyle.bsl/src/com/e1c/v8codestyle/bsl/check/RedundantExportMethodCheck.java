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
import java.util.List;
import java.util.Optional;
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
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.IResourceDescriptionsProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;

import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.google.inject.Inject;

/**
 * Checks the modules contain unused export procedures and functions.
 *
 * @author Artem Iliukhin
 */
@SuppressWarnings("restriction")
public final class RedundantExportMethodCheck
    extends AbstractModuleStructureCheck
{

    private static final String USER_DATA = "methodName"; //$NON-NLS-1$

    private static final String DEFAULT_EXCLUDE_REGION_NAME_LIST =
        String.join(",", ModuleStructureSection.PUBLIC.getNames()); //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_REGION_LIST = "excludeRegionName"; //$NON-NLS-1$

    private static final String CHECK_ID = "redundant-export-method"; //$NON-NLS-1$

    private static final String TYPE_NAME_OLD = "NotifyDescription"; //$NON-NLS-1$
    private static final String TYPE_NAME = "CallbackDescription"; //$NON-NLS-1$

    private final IReferenceFinder referenceFinder;

    private final IResourceAccess workSpaceResourceAccess;

    private final IResourceDescriptionsProvider resourceDescriptionsProvider;

    private final IQualifiedNameProvider bslQualifiedNameProvider;

    private final IScopeProvider scopeProvider;

    @Inject
    public RedundantExportMethodCheck(IResourceAccess workSpaceResourceAccess, IReferenceFinder referenceFinder,
        IResourceDescriptionsProvider resourceDescriptionsProvider, IQualifiedNameProvider bslQualifiedNameProvider,
        IScopeProvider scopeProvider)
    {
        this.workSpaceResourceAccess = workSpaceResourceAccess;
        this.referenceFinder = referenceFinder;
        this.resourceDescriptionsProvider = resourceDescriptionsProvider;
        this.bslQualifiedNameProvider = bslQualifiedNameProvider;
        this.scopeProvider = scopeProvider;
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
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.MANAGER_MODULE, ModuleType.COMMON_MODULE,
                ModuleType.OBJECT_MODULE))
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
        if (monitor.isCanceled())
        {
            return;
        }

        Method method = (Method)object;
        if (!method.isExport())
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(method, Module.class);

        String name = method.getName();
        if (name == null)
        {
            return;
        }
        if (isNotExclusion(parameters, method, monitor) && !isScheduledJobOrEventSubscription(module, name, monitor)
            && !existLocalNotifyDescription(module, name, monitor) && !haveCallerInOtherModule(method, monitor))
        {
            if (monitor.isCanceled())
            {
                return;
            }

            resultAceptor.addIssue(MessageFormat.format(Messages.RedundantExportCheck_Unused_export_method__0, name),
                method, BslPackage.Literals.METHOD__EXPORT);
        }
    }

    private boolean isScheduledJobOrEventSubscription(Module module, String methodName, IProgressMonitor monitor)
    {
        ModuleType moduleType = module.getModuleType();
        if (!ModuleType.COMMON_MODULE.equals(moduleType))
        {
            return false;
        }

        QualifiedName fullyQualifiedName = bslQualifiedNameProvider.getFullyQualifiedName(module);
        if (fullyQualifiedName != null)
        {
            String moduleName = fullyQualifiedName.skipLast(1).toString();
            String moduleMethodName = String.join(".", moduleName, methodName); //$NON-NLS-1$

            IScope jobs = scopeProvider.getScope(module, MdClassPackage.Literals.CONFIGURATION__SCHEDULED_JOBS);
            for (IEObjectDescription item : jobs.getAllElements())
            {
                if (monitor.isCanceled() || moduleMethodName.equalsIgnoreCase(item.getUserData(USER_DATA)))
                {
                    return true;
                }
            }

            IScope events = scopeProvider.getScope(module, MdClassPackage.Literals.CONFIGURATION__EVENT_SUBSCRIPTIONS);
            for (IEObjectDescription item : events.getAllElements())
            {
                if (monitor.isCanceled() || moduleMethodName.equalsIgnoreCase(item.getUserData(USER_DATA)))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean existLocalNotifyDescription(Module module, String name, IProgressMonitor monitor)
    {
        for (TreeIterator<EObject> iterator = module.eAllContents(); iterator.hasNext();)
        {
            if (monitor.isCanceled())
            {
                return true;
            }

            EObject containedObject = iterator.next();
            if (containedObject instanceof OperatorStyleCreator)
            {
                String typeName = McoreUtil.getTypeName(((OperatorStyleCreator)containedObject).getType());
                if (TYPE_NAME_OLD.equals(typeName) || TYPE_NAME.equals(typeName))
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
        }

        return false;
    }

    private boolean haveCallerInOtherModule(Method object, IProgressMonitor monitor)
    {
        IProgressMonitor subMonitor = new NullProgressMonitor()
        {
            @Override
            public boolean isCanceled()
            {
                return super.isCanceled() || monitor.isCanceled();
            }
        };

        Resource resource = object.eResource();

        IResourceDescriptions indexData =
            resourceDescriptionsProvider.getResourceDescriptions(resource.getResourceSet());

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
                    subMonitor.setCanceled(true);
                }
            }
        };

        TargetURIs targetUris = new TargetURISet()
        {
            //
        };

        targetUris.addURI(EcoreUtil.getURI(object));

        referenceFinder.findAllReferences(targetUris, workSpaceResourceAccess, indexData, acceptor, subMonitor);

        return subMonitor.isCanceled();
    }

    private boolean isNotExclusion(ICheckParameters parameters, Method method, IProgressMonitor monitor)
    {
        Optional<RegionPreprocessor> region = getTopParentRegion(method);
        if (region.isPresent())
        {
            String names = parameters.getString(PARAMETER_EXCLUDE_REGION_LIST);
            if (names != null)
            {
                Set<String> set = Set.of(names.split(",")); //$NON-NLS-1$
                for (String name : set)
                {
                    if (monitor.isCanceled())
                    {
                        return false;
                    }
                    if (StringUtils.equals(name, region.get().getName()))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
