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

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.bm.ui.refactoring.BslBmRefactoringResourceSetProvider;
import com._1c.g5.v8.dt.bsl.bm.ui.refactoring.BslDtMatchProvider;
import com._1c.g5.v8.dt.bsl.bm.ui.refactoring.BslFullTextSearchSupplier;
import com._1c.g5.v8.dt.bsl.model.BslPackage;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItem;
import com._1c.g5.v8.dt.bsl.model.RegionPreprocessor;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.bsl.services.BslGrammarAccess;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.filesystem.IProjectFileSystemSupportProvider;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.IExternalPropertyManagerRegistry;
import com._1c.g5.v8.dt.search.core.IDtMatchProvider;
import com._1c.g5.v8.dt.search.core.SearchFor;
import com._1c.g5.v8.dt.search.core.SearchIn;
import com._1c.g5.v8.dt.search.core.SearchScope;
import com._1c.g5.v8.dt.search.core.findref.FullTextSearchReferenceFinder;
import com._1c.g5.v8.dt.search.core.findref.FullTextSearchReferenceFinder.IFullTextSearchReferenceFinderSupplier;
import com._1c.g5.v8.dt.search.core.findref.FullTextSearchReferenceFinder.IFullTextSearchReferenceResult;
import com._1c.g5.v8.dt.search.core.findref.FullTextSearchReferenceResult;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.bsl.ModuleStructureSection;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * Checks the modules contain unused export procedures and functions.
 *
 * @author Artem Iliukhin
 */
public final class ExcessExportMethodCheck
    extends BasicCheck
{

    private static final String DEFAULT_EXCLUDE_REGION_NAME_LIST =
        String.join(",", ModuleStructureSection.PUBLIC.getNames()); //$NON-NLS-1$

    private static final String PARAMETER_EXCLUDE_REGION_LIST = "excludeRegionName"; //$NON-NLS-1$

    private static final String CHECK_ID = "excess-export-method"; //$NON-NLS-1$

    private final IProjectFileSystemSupportProvider fileSystemSupportProvider;
    private final BslGrammarAccess bslGrammar;
    private final DynamicFeatureAccessComputer dynamicFeatureAccessComputer;
    private final IV8ProjectManager projectManager;
    private final IGlobalScopeProvider scopeProvider;
    private final IExternalPropertyManagerRegistry propertyManagerRegistry;
    private final IBmModelManager manager;
    private final BslBmRefactoringResourceSetProvider resourceSetProvider;

    @Inject
    public ExcessExportMethodCheck(IV8ProjectManager projectManager,
        IProjectFileSystemSupportProvider fileSystemSupportProvider, IGlobalScopeProvider scopeProvider,
        DynamicFeatureAccessComputer dynamicFeatureAccessComputer, BslGrammarAccess bslGrammar,
        IExternalPropertyManagerRegistry propertyManagerRegistry, IBmModelManager manager,
        BslBmRefactoringResourceSetProvider resourceSetProvider)
    {
        this.projectManager = projectManager;
        this.fileSystemSupportProvider = fileSystemSupportProvider;
        this.bslGrammar = bslGrammar;
        this.dynamicFeatureAccessComputer = dynamicFeatureAccessComputer;
        this.scopeProvider = scopeProvider;
        this.manager = manager;
        this.propertyManagerRegistry = propertyManagerRegistry;
        this.resourceSetProvider = resourceSetProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.ExcessExportCheck_Escess_title)
            .description(Messages.ExcessExportCheck_Excess_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .disable()
            .extension(new StandardCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter(PARAMETER_EXCLUDE_REGION_LIST, String.class, DEFAULT_EXCLUDE_REGION_NAME_LIST,
                Messages.ExcessExportCheck_Exclude_title);

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
        if (ModuleType.FORM_MODULE == module.getModuleType())
        {
            return;
        }

        if (isNotExclusion(parameters, method))
        {
            IFullTextSearchReferenceFinderSupplier referenceFinderSupplier =
                new BslFullTextSearchReferenceFinderSupplier(method, fileSystemSupportProvider, bslGrammar,
                    resourceSetProvider, dynamicFeatureAccessComputer, manager, propertyManagerRegistry, projectManager,
                    scopeProvider);

            FullTextSearchReferenceFinder referenceFinder = new FullTextSearchReferenceFinder(method.getName(), method,
                referenceFinderSupplier, fileSystemSupportProvider);

            Collection<IFullTextSearchReferenceResult> refs = referenceFinder.findReferences(
                Set.of(SearchFor.LANGUAGE_ELEMENTS), Set.of(SearchIn.MODULES),
                Set.of(SearchScope.COMMON_MODULES, SearchScope.COMMON_FORMS, SearchScope.CONSTANTS,
                    SearchScope.CATALOGS, SearchScope.DOCUMENTS, SearchScope.DOCUMENT_JOURNALS, SearchScope.REPORTS,
                    SearchScope.INFORMATION_REGISTERS, SearchScope.ACCUMULATION_REGISTERS,
                    SearchScope.CALCULATION_REGISTERS, SearchScope.BUSINESS_PROCESSES, SearchScope.TASKS),
                Set.of(projectManager.getProject(method).getProject().getName()), monitor);

            if (refs.isEmpty())
            {
                resultAceptor.addIssue(Messages.ExcessExportCheck_Unused_export_method, method,
                    BslPackage.Literals.METHOD__EXPORT);
            }
        }
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

    private final class BslFullTextSearchReferenceFinderSupplier
        extends BslFullTextSearchSupplier
        implements IFullTextSearchReferenceFinderSupplier
    {
        private URI targetUri;

        @Inject
        private BslFullTextSearchReferenceFinderSupplier(EObject sourceObject,
            IProjectFileSystemSupportProvider fileSystemSupportProvider, BslGrammarAccess bslGrammar,
            BslBmRefactoringResourceSetProvider resourceSetProvider,
            DynamicFeatureAccessComputer dynamicFeatureAccessComputer, IBmModelManager manager,
            IExternalPropertyManagerRegistry propertyManagerRegistry, IV8ProjectManager projectManager,
            IGlobalScopeProvider scopeProvider)
        {
            super(fileSystemSupportProvider, bslGrammar, resourceSetProvider, dynamicFeatureAccessComputer,
                projectManager, scopeProvider);
            this.targetUri = EcoreUtil.getURI(sourceObject);
        }

        @Override
        public Collection<IFullTextSearchReferenceResult> createReferences(IFile file,
            Collection<IDtMatchProvider> matches, String name)
        {
            Collection<IFullTextSearchReferenceResult> references = Lists.newArrayListWithCapacity(matches.size());
            for (IDtMatchProvider result : matches)
            {
                if (result instanceof BslDtMatchProvider)
                {
                    BslDtMatchProvider bslMatch = (BslDtMatchProvider)result;
                    references.add(new FullTextSearchReferenceResult(bslMatch.getSemanticObjectUri(), targetUri,
                        bslMatch.getTopObjectUri(), null, -1,
                        new TextSelection(result.getDtMatch().getOffset(), result.getDtMatch().getLength())));
                    return references;
                }
            }
            return references;
        }

        @Override
        public Collection<IFullTextSearchReferenceResult> createReferences(IBmObject bmObject,
            Collection<IDtMatchProvider> matches, String name)
        {
            return Lists.newArrayListWithCapacity(matches.size());
        }

    }

}
