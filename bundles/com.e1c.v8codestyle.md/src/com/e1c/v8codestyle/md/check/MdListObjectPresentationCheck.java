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
package com.e1c.v8codestyle.md.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__LIST_PRESENTATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT__OBJECT_PRESENTATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.INFORMATION_REGISTER;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.INFORMATION_REGISTER__LIST_PRESENTATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.INFORMATION_REGISTER__RECORD_PRESENTATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.INFORMATION_REGISTER__WRITE_MODE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.BasicDbObject;
import com._1c.g5.v8.dt.metadata.mdclass.InformationRegister;
import com._1c.g5.v8.dt.metadata.mdclass.Language;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com._1c.g5.v8.dt.metadata.mdclass.RegisterWriteMode;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.TopObjectFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * The check the {@link MdObject} has list presentation or object presentation filled
 * for default language of the project.
 *
 * @author Dmitriy Marmyshev
 */
public class MdListObjectPresentationCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "md-list-object-presentation"; //$NON-NLS-1$

    private final IV8ProjectManager v8ProjectManager;

    /**
     * Instantiates a new MD-object object or list presentation check.
     *
     * @param v8ProjectManager the v8 project manager service, cannot be {@code null}.
     */
    @Inject
    public MdListObjectPresentationCheck(IV8ProjectManager v8ProjectManager)
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
        builder.title(Messages.MdListObjectPresentationCheck_title)
            .description(Messages.MdListObjectPresentationCheck_decription)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .extension(new TopObjectFilterExtension())
            .issueType(IssueType.UI_STYLE);

        builder.topObject(BASIC_DB_OBJECT)
            .checkTop()
            .features(BASIC_DB_OBJECT__OBJECT_PRESENTATION, BASIC_DB_OBJECT__LIST_PRESENTATION);

        builder.topObject(INFORMATION_REGISTER)
            .checkTop()
            .features(INFORMATION_REGISTER__RECORD_PRESENTATION, INFORMATION_REGISTER__LIST_PRESENTATION,
                INFORMATION_REGISTER__WRITE_MODE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof MdObject))
        {
            return;
        }

        MdObject mdObject = (MdObject)object;
        if (mdObject.getObjectBelonging() != ObjectBelonging.NATIVE)
        {
            // skip extended object in Extension project
            return;
        }

        if (object instanceof InformationRegister
            && ((InformationRegister)object).getWriteMode() == RegisterWriteMode.RECORDER_SUBORDINATE)
        {
            return;
        }

        IV8Project project = v8ProjectManager.getProject(mdObject);
        Language language = project.getDefaultLanguage();
        if (monitor.isCanceled() || language == null)
        {
            return;
        }
        String languageCode = language.getLanguageCode();

        String listPresentation = null;
        String objectPresentation = null;

        EStructuralFeature feature = null;
        if (object instanceof BasicDbObject)
        {
            listPresentation = ((BasicDbObject)object).getListPresentation().get(languageCode);
            objectPresentation = ((BasicDbObject)object).getObjectPresentation().get(languageCode);
            feature = BASIC_DB_OBJECT__OBJECT_PRESENTATION;
        }
        else if (object instanceof InformationRegister)
        {
            listPresentation = ((InformationRegister)object).getListPresentation().get(languageCode);
            objectPresentation = ((InformationRegister)object).getRecordPresentation().get(languageCode);
            feature = INFORMATION_REGISTER__RECORD_PRESENTATION;
        }

        if (StringUtils.isBlank(objectPresentation) && StringUtils.isBlank(listPresentation))
        {
            String message =
                Messages.MdListObjectPresentationCheck_Neither_Object_presentation_nor_List_presentation_is_not_filled;
            resultAceptor.addIssue(message, feature);
        }

    }

}
