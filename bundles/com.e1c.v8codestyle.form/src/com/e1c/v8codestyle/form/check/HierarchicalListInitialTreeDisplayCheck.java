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
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.TABLE;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.event.BmSubEvent;
import com._1c.g5.v8.dt.form.model.FormPackage;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.model.TableInitialTreeView;
import com._1c.g5.v8.dt.form.model.TableRepresentation;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.EIssue;
import com.e1c.g5.v8.dt.check.ICheck;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.ICheckResultAcceptor;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * check for hierarchical lists on form
 * if property InitialTreeView = ExpandAllLevels => error
 * 
 * @author Artem Samohvalov
 */
public class HierarchicalListInitialTreeDisplayCheck
    implements ICheck<Object>
{
    private final IBasicCheckExtension extension = new StandardCheckExtension(489, getCheckId(), CorePlugin.PLUGIN_ID);

    @Override
    public String getCheckId()
    {
        return "hierarchical-list-initial-tree-display"; //$NON-NLS-1$
    }

    @Override
    public void check(Object object, ICheckResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (object instanceof Table table
            && table.getRepresentation() == TableRepresentation.HIERARCHICAL_LIST
            && table.getInitialTreeView() == TableInitialTreeView.EXPAND_ALL_LEVELS)
        {
            EIssue issue = new EIssue(Messages.HierarchicalListInitialTreeDisplay_issue,
                FormPackage.Literals.TABLE__INITIAL_TREE_VIEW);
            resultAcceptor.addIssue(table, issue);
        }
    }

    @Override
    public void configureContextCollector(ICheckDefinition definition)
    {
        definition.addCheckedModelObjects(FORM, false, Set.of(TABLE));
        definition.addModelFeatureChangeContextCollector(new ObjectCollectionFeatureChangeContextCollector(), TABLE);

        definition.setTitle(Messages.HierarchicalListInitialTreeDisplay_title);
        definition.setDescription(Messages.HierarchicalListInitialTreeDisplay_description);
        definition.setComplexity(CheckComplexity.NORMAL);
        definition.setDefaultSeverity(IssueSeverity.MINOR);
        definition.setIssueType(IssueType.UI_STYLE);
        extension.configureContextCollector(definition);
    }

    private static final class ObjectCollectionFeatureChangeContextCollector
        implements OnModelFeatureChangeContextCollector
    {
        @Override
        public void collectContextOnFeatureChange(IBmObject object, EStructuralFeature feature, BmSubEvent bmEvent,
            CheckContextCollectingSession contextSession)
        {
            if (object instanceof Table table && table.getRepresentation() == TableRepresentation.HIERARCHICAL_LIST)
            {
                contextSession.addModelCheck(table);
            }
        }
    }
}
