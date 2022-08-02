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
package com.e1c.v8codestyle.form.fix;

import java.text.MessageFormat;
import java.util.Optional;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.form.model.FormField;
import com._1c.g5.v8.dt.metadata.mdclass.Language;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.g5.v8.dt.check.qfix.components.SingleVariantModelBasicFix;
import com.e1c.v8codestyle.form.check.DynamicListItemTitleCheck;
import com.e1c.v8codestyle.internal.form.CorePlugin;
import com.google.inject.Inject;

/**
 * This fix generate title text for the form table column of dynamic list its item name CamelCase.
 *
 * @author Dmitriy Marmyshev
 */
@QuickFix(checkId = DynamicListItemTitleCheck.CHECK_ID, supplierId = CorePlugin.PLUGIN_ID)
public class DynamicListItemTitleGenerateFix
    extends SingleVariantModelBasicFix<FormField>
{
    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new dynamic list item title generate fix.
     *
     * @param v8ProjectManager the v8 project manager, cannot be {@code null}.
     */
    @Inject
    public DynamicListItemTitleGenerateFix(IV8ProjectManager v8ProjectManager)
    {
        super(FormField.class);
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(FixConfigurer configurer)
    {
        configurer.description((context, session) -> {
            long id = context.getTargetObjectId();
            IBmObject object = session.getModelObject(id);
            if (object instanceof FormField)
            {
                FormField field = (FormField)object;
                String name = field.getName();
                String title = StringUtils.nameToText(name);
                return MessageFormat.format(Messages.DynamicListItemTitleGenerateFix_title, title, name);
            }
            return Messages.DynamicListItemTitleGenerateFix_Default_title;
        });
    }

    @Override
    protected void applyChanges(FormField object, EStructuralFeature feature, BasicModelFixContext contex,
        IFixSession session)
    {
        String name = object.getName();
        String title = StringUtils.nameToText(name);

        IV8Project v8Project = v8ProjectManager.getProject(session.getDtProject());

        Optional<String> languageCode = getDefaultLanguageCode(v8Project);
        if (languageCode.isPresent())
        {
            object.getTitle().put(languageCode.get(), title);
        }
    }

    protected Optional<String> getDefaultLanguageCode(IV8Project project)
    {
        Language language = project.getDefaultLanguage();

        if (language == null)
        {
            if (!project.getLanguages().isEmpty())
            {
                language = project.getLanguages().iterator().next();
            }
            else if (project instanceof IExtensionProject && ((IExtensionProject)project).getParent() != null)
            {
                language = ((IExtensionProject)project).getParent().getDefaultLanguage();
            }
        }

        if (language == null)
        {
            return Optional.empty();
        }
        return Optional.ofNullable(language.getLanguageCode());
    }

}
