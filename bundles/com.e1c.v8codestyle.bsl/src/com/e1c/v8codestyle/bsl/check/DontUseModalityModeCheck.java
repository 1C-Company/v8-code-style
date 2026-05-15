/*******************************************************************************
 * Copyright (C) 2025, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.INVOCATION;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.FeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.ModalityUseMode;
import com.e1c.g5.v8.dt.check.BslDirectLocationIssue;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.DirectLocation;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.Issue;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.inject.Inject;

/**
 * Checks dont use modality call in dont use modality mode.
 *
 *  @author Ivan Sergeev
 */
public class DontUseModalityModeCheck
    extends AbstractModuleStructureCheck
{
    private static final String CHECK_ID = "dont-use-modality-mode"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_CALL = Set.of("открытьзначение", //$NON-NLS-1$
        "openvalue", //$NON-NLS-1$
        "открытьформумодально", //$NON-NLS-1$
        "openformmodal", //$NON-NLS-1$
        "вопрос", //$NON-NLS-1$
        "doquerybox", //$NON-NLS-1$
        "предупреждение", //$NON-NLS-1$
        "domessagebox", //$NON-NLS-1$
        "выбратьизсписка", //$NON-NLS-1$
        "choosefromlist", //$NON-NLS-1$
        "ввестистроку", //$NON-NLS-1$
        "inputstring", //$NON-NLS-1$
        "ввестичисло", //$NON-NLS-1$
        "inputnumber", //$NON-NLS-1$
        "ввестидату", //$NON-NLS-1$
        "inputdate", //$NON-NLS-1$
        "открытьмодально", //$NON-NLS-1$
        "domodal", //$NON-NLS-1$
        "поместитьфайл", //$NON-NLS-1$
        "putfile", //$NON-NLS-1$
        "отметитьэлементы", //$NON-NLS-1$
        "checkitems", //$NON-NLS-1$
        "выбратьэлемент", //$NON-NLS-1$
        "chooseitem", //$NON-NLS-1$
        "установитьрасширениеработысфайлами", //$NON-NLS-1$
        "installfilesystemextension", //$NON-NLS-1$
        "установитьвнешнююкомпоненту", //$NON-NLS-1$
        "installaddin", //$NON-NLS-1$
        "выбратьизменю", //$NON-NLS-1$
        "choosefrommenu"); //$NON-NLS-1$

    private final IConfigurationProvider configurationProvider;

    @Inject
    public DontUseModalityModeCheck(IConfigurationProvider configurationProvider)
    {
        super();
        this.configurationProvider = configurationProvider;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DontUseModalityModeCheck_Title)
            .description(Messages.DontUseModalityModeCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(INVOCATION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Invocation invocation = (Invocation)object;
        Configuration configuration = configurationProvider.getConfiguration(invocation);
        if (configuration.getModalityUseMode() == ModalityUseMode.DONT_USE)
        {
            FeatureAccess featureAccess = invocation.getMethodAccess();
            String name = featureAccess.getName();

            if (IMMUTABLE_MAP_CALL.contains(name.toLowerCase()))
            {
                ICompositeNode node = NodeModelUtils.findActualNodeFor(featureAccess);
                DirectLocation directLocation =
                    new DirectLocation(node.getOffset(), node.getLength(), node.getStartLine(), invocation);

                Issue issue = new BslDirectLocationIssue(Messages.DontUseModalityModeCheck_Issue, directLocation);
                resultAceptor.addIssue(issue);
            }
        }
    }
}