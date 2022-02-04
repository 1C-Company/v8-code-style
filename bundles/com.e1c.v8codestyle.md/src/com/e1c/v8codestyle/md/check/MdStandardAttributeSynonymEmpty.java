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
 *     Bombin Valentin - issue #119
 *******************************************************************************/

package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__STANDARD_ATTRIBUTES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__HIERARCHICAL;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CATALOG__OWNERS;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.STANDARD_ATTRIBUTE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.STANDARD_ATTRIBUTE__SYNONYM;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.event.BmSubEvent;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com._1c.g5.v8.dt.metadata.mdclass.StandardAttribute;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.inject.Inject;

/**
 * The check the {@link Catalog} has specified synonyms for owner and parent
 * for default language of the project.
 *
 * @author Bombin Valentin
 * @author Dmitriy Marmyshev
 */
public class MdStandardAttributeSynonymEmpty
    extends BasicCheck
{
    private static final String CHECK_ID = "md-standard-attribute-synonym-empty"; //$NON-NLS-1$

    private static final String OWNER_NAME = "Owner"; //$NON-NLS-1$

    private static final String PARENT_NAME = "Parent"; //$NON-NLS-1$

    private static final Set<EStructuralFeature> FEATURES =
        Set.of(BASIC_DB_OBJECT__STANDARD_ATTRIBUTES, CATALOG__HIERARCHICAL, CATALOG__OWNERS);

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public MdStandardAttributeSynonymEmpty(IV8ProjectManager v8ProjectManager)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.MdOwnerAttributeSynonymEmpty_Title)
            .description(Messages.MdOwnerAttributeSynonymEmpty_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new TopObjectFilterExtension())
            .extension(new StandardCheckExtension(getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new CatalogChangeExtension());

        builder.topObject(CATALOG).containment(STANDARD_ATTRIBUTE).features(STANDARD_ATTRIBUTE__SYNONYM);
        builder.topObject(CATALOG).checkTop().features(FEATURES.toArray(new EStructuralFeature[0]));

    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        EObject eObject = (EObject)object;
        IV8Project project = v8ProjectManager.getProject(eObject);
        String languageCode = project.getDefaultLanguage().getLanguageCode();
        if (monitor.isCanceled())
        {
            return;
        }

        if (object instanceof StandardAttribute)
        {
            StandardAttribute attribute = (StandardAttribute)object;
            EObject parent = attribute.eContainer();
            if (!isValidCatalog(parent))
            {
                return;
            }
            Catalog catalog = (Catalog)parent;
            if (PARENT_NAME.equalsIgnoreCase(attribute.getName()) && hasParent(catalog)
                && isSynonymEmpty(attribute, languageCode))
            {
                resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_parent_ErrorMessage,
                    STANDARD_ATTRIBUTE__SYNONYM);
            }
            else if (OWNER_NAME.equalsIgnoreCase(attribute.getName()) && hasAnyOwner(catalog)
                && isSynonymEmpty(attribute, languageCode))
            {
                resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_owner_ErrorMessage,
                    STANDARD_ATTRIBUTE__SYNONYM);
            }
        }
        else if (isValidCatalog(object))
        {
            Catalog catalog = (Catalog)object;

            checkParent(catalog, resultAceptor);
            checkOwner(catalog, resultAceptor);
        }
    }

    private boolean isValidCatalog(Object object)
    {
        if (object instanceof Catalog)
        {
            Catalog catalog = (Catalog)object;
            return catalog.getObjectBelonging() == ObjectBelonging.NATIVE;
        }
        return false;
    }

    private boolean hasAnyOwner(Catalog catalog)
    {
        return !catalog.getOwners().isEmpty();
    }

    private boolean hasParent(Catalog catalog)
    {
        return catalog.isHierarchical();
    }

    private StandardAttribute getStandardAttributeByName(Catalog catalog, String attributeName)
    {
        for (StandardAttribute attribute : catalog.getStandardAttributes())
        {
            if (attributeName.equalsIgnoreCase(attribute.getName()))
            {
                return attribute;
            }
        }
        return null;
    }

    private boolean isSynonymEmpty(StandardAttribute attribute, String languageCode)
    {
        return StringUtils.isBlank(attribute.getSynonym().get(languageCode));
    }

    private void checkParent(Catalog catalog, ResultAcceptor resultAceptor)
    {
        if (!hasParent(catalog))
        {
            return;
        }

        StandardAttribute attribute = getStandardAttributeByName(catalog, PARENT_NAME);

        if (attribute == null)
        {
            resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_parent_ErrorMessage,
                BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
        }
    }

    private void checkOwner(Catalog catalog, ResultAcceptor resultAceptor)
    {
        if (!hasAnyOwner(catalog))
        {
            return;
        }

        StandardAttribute attribute = getStandardAttributeByName(catalog, OWNER_NAME);

        if (attribute == null)
        {
            resultAceptor.addIssue(Messages.MdOwnerAttributeSynonymEmpty_owner_ErrorMessage,
                BASIC_DB_OBJECT__STANDARD_ATTRIBUTES);
        }
    }

    private class CatalogChangeExtension
        implements IBasicCheckExtension
    {

        @Override
        public void configureContextCollector(final ICheckDefinition definition)
        {
            OnModelFeatureChangeContextCollector collector = (IBmObject bmObject, EStructuralFeature feature,
                BmSubEvent bmEvent, CheckContextCollectingSession contextSession) -> {

                if (bmObject instanceof Catalog && FEATURES.contains(feature))
                {
                    contextSession.addFullCheck(bmObject);
                }
            };
            definition.addModelFeatureChangeContextCollector(collector, CATALOG);
        }

    }
}
