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
package com.e1c.v8codestyle.form.fix;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.model.TableInitialTreeView;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.g5.v8.dt.check.qfix.components.SingleVariantModelBasicFix;

/**
 * QuickFix for form check - hierarchical-list-initial-tree-display
 * 
 * @author Artem Samohvalov
 */
@QuickFix(checkId = "hierarchical-list-initial-tree-display", supplierId = "com.e1c.v8codestyle.form")
public class HierarchicalListInitialTreeDisplayFix
    extends SingleVariantModelBasicFix<Table>
{

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description(Messages.HierarchicalListInitialTreeDisplayFix_description);
    }

    @Override
    protected void applyChanges(Table table, EStructuralFeature feature, BasicModelFixContext fixContext,
        IFixSession fixSession)
    {
        table.setInitialTreeView(TableInitialTreeView.EXPAND_TOP_LEVEL);
    }

}
