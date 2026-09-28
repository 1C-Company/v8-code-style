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
 *******************************************************************************/
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.text.MessageFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.builder.MonitorBasedCancelIndicator;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;

import com._1c.g5.v8.dt.bsl.common.IModuleExtensionService;
import com._1c.g5.v8.dt.bsl.common.IModuleExtensionServiceProvider;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.resource.BslResourceDescription;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Unused module method check. This class needs {@link IQualifiedNameProvider} to inject other service.
 *
 * @author Andrey Volkov
 * @author Dmitriy Marmyshev
 */
public final class ModuleUnusedMethodCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "module-unused-method"; //$NON-NLS-1$

    private static final String EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME = "excludeModuleMethodNamePattern"; //$NON-NLS-1$

    private static final String DEFAULT_EXCLUDE_METHOD_NAME_PATTERN = "^(?U)(Подключаемый|Attachable)_.*$"; //$NON-NLS-1$

    private final IResourceDescription.Manager resourceDescriptionManager;

    /**
     * Instantiates a new module unused method check.
     *
     * @param resourceDescriptionManager the resource description manager service, cannot be {@code null}.
     */
    @Inject
    public ModuleUnusedMethodCheck(IResourceDescription.Manager resourceDescriptionManager)
    {
        super();
        this.resourceDescriptionManager = resourceDescriptionManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ModuleUnusedMethodCheck_Title)
            .description(Messages.ModuleUnusedMethodCheck_Description)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .parameter(EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME, String.class, DEFAULT_EXCLUDE_METHOD_NAME_PATTERN,
                Messages.ModuleUnusedMethodCheck_Exclude_method_name_pattern_title)
            .issueType(IssueType.WARNING)
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        Module module = (Module)object;

        IModuleExtensionService service = IModuleExtensionServiceProvider.INSTANCE.getModuleExtensionService();
        String excludeNamePattern = parameters.getString(EXCLUDE_METHOD_NAME_PATTERN_PARAMETER_NAME);

        Predicate<Method> predicate = method -> method.getName() != null && !method.isUsed() && !method.isExport()
            && !method.isEvent() && service.getSourceMethodNames(method).isEmpty()
            && !isExcludeName(method.getName(), excludeNamePattern);

        // TODO - only full validation first, optimization later
        //@formatter:off
        //if (!((BslResource)module.eResource()).isOnlyMethodReparse())
        //{
        Resource resource = module.eResource();
        predicate = predicate.and(method -> isMethodWithoutExternalCallers(method, resource, progressMonitor));
        //}
        //@formatter:on

        module.allMethods()
            .stream()
            .filter(predicate)
            .forEach(unusedMethod -> resultAceptor.addIssue(
                MessageFormat.format(Messages.ModuleUnusedMethodCheck_Unused_method__0, unusedMethod.getName()),
                unusedMethod, McorePackage.Literals.NAMED_ELEMENT__NAME));
    }

    private List<URI> getUsedMethods(IProgressMonitor progressMonitor, Resource resource, Method method)
    {
        URI selfUri = EcoreUtil.getURI(method);
        IResourceDescription descr = resourceDescriptionManager.getResourceDescription(resource);
        Iterable<? extends IReferenceDescription> references = (descr instanceof BslResourceDescription)
            ? ((BslResourceDescription)descr).getReferenceDescriptions(new MonitorBasedCancelIndicator(progressMonitor))
            : descr.getReferenceDescriptions();
        return StreamSupport.stream(references.spliterator(), false)
            .filter(reference -> selfUri.equals(reference.getTargetEObjectUri()))
            .map(IReferenceDescription::getSourceEObjectUri)
            .toList();
    }

    private boolean isMethodWithoutExternalCallers(Method method, Resource resource, IProgressMonitor progressMonitor)
    {
        for (URI caller : getUsedMethods(progressMonitor, resource, method))
        {
            EObject callerObj = resource.getEObject(caller.fragment());
            if (callerObj != null && EcoreUtil2.getContainerOfType(callerObj, Method.class) != method)
            {
                return false;
            }
        }
        return true;
    }

    private boolean isExcludeName(String name, String excludeNamePattern)
    {
        return StringUtils.isNotEmpty(excludeNamePattern) && name.matches(excludeNamePattern);
    }
}
