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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.inject.Inject;

/**
 * The name of a md object of the extension object does not have a prefix corresponding
 * to the prefix of the extension itself.
 *
 * @author Artem Iliukhin
 */
public class ExtensionMdObjectNamePrefixCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "extension-md-object-prefix"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new extension md object name prefix check.
     *
     * @param v8ProjectManager the v8 project manager
     */
    @Inject
    public ExtensionMdObjectNamePrefixCheck(IV8ProjectManager v8ProjectManager)
    {
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
        builder.title(Messages.ExtensionMdObjectNamePrefixCheck_Title)
            .description(Messages.ExtensionMdObjectNamePrefixCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(469, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new MdObjectFromExtensionProjectExtension(v8ProjectManager))
            .topObject(MD_OBJECT)
            .checkTop()
            .containment(MD_OBJECT)
            .features(MD_OBJECT__NAME);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        MdObject mdObject = (MdObject)object;
        IV8Project extension = v8ProjectManager.getProject(mdObject);
        if (extension instanceof IExtensionProject
            && (isNonAdoptedParent(mdObject) || isAdoptedParentNonAdoptedChild(mdObject)))
        {
            String name = mdObject.getName();
            String prefix = getNamePrefix((IExtensionProject)extension);
            if (!StringUtils.isEmpty(prefix) && !name.startsWith(prefix))
            {
                resultAceptor.addIssue(MessageFormat
                    .format(Messages.ExtensionMdObjectNamePrefixCheck_Object_0_should_have_1_prefix, name, prefix),
                    MD_OBJECT__NAME);
            }
        }
    }

    private boolean isAdoptedParentNonAdoptedChild(MdObject mdObject)
    {
        return !((IBmObject)mdObject).bmIsTop() && mdObject.eContainer() instanceof MdObject
            && ((MdObject)mdObject.eContainer()).getObjectBelonging() == ObjectBelonging.ADOPTED
            && mdObject.getObjectBelonging() != ObjectBelonging.ADOPTED;
    }

    private boolean isNonAdoptedParent(MdObject mdObject)
    {
        return ((IBmObject)mdObject).bmIsTop() && mdObject.getObjectBelonging() != ObjectBelonging.ADOPTED;
    }

    private String getNamePrefix(IExtensionProject extension)
    {
        return extension.getConfiguration().getNamePrefix();
    }

}
