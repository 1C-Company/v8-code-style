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
package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_DATA_PATH;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_DATA_PATH__EXTRA_PATHS;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_DATA_PATH__OBJECTS;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_DATA_PATH__SEGMENTS;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com._1c.g5.v8.dt.common.Functions;
import com._1c.g5.v8.dt.form.model.AbstractDataPath;
import com._1c.g5.v8.dt.form.model.DataPathReferredObject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.MultiLanguageDataPath;
import com._1c.g5.v8.dt.form.model.PropertyInfo;
import com._1c.g5.v8.dt.form.service.datasourceinfo.IDataSourceInfoAssociationService;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.google.inject.Inject;

/**
 * Checks that each segment of {@link Form} item data-path has referred object.
 *
 * @author Dmitriy Marmyshev
 */
public class DataPathReferredObjectCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-data-path"; //$NON-NLS-1$

    private IDataSourceInfoAssociationService dataSourceInfoAssociationService;

    @Inject
    public DataPathReferredObjectCheck(IDataSourceInfoAssociationService dataSourceInfoAssociationService)
    {
        super();
        this.dataSourceInfoAssociationService = dataSourceInfoAssociationService;
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DataPathReferredObjectCheck_title)
            .description(Messages.DataPathReferredObjectCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .topObject(FORM)
            .containment(ABSTRACT_DATA_PATH)
            .features(ABSTRACT_DATA_PATH__SEGMENTS, ABSTRACT_DATA_PATH__EXTRA_PATHS, ABSTRACT_DATA_PATH__OBJECTS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof MultiLanguageDataPath)
        {
            return;
        }

        AbstractDataPath dataPath = (AbstractDataPath)object;

        if (dataPath.getSegments().isEmpty())
        {
            return;
        }

        Set<Integer> foundSegmentObjects = new HashSet<>();
        for (DataPathReferredObject refObject : dataPath.getObjects())
        {
            if (monitor.isCanceled())
            {
                return;
            }
            int segmentIndex = refObject.getSegmentIdx();
            if (segmentIndex > -1 && refObject.getObject() != null)
            {
                foundSegmentObjects.add(segmentIndex);
            }
        }

        if (monitor.isCanceled() || dataPath.getSegments().size() == foundSegmentObjects.size())
        {
            return;
        }

        Form form = (Form)dataPath.bmGetTopObject();
        PropertyInfo found = dataSourceInfoAssociationService.findPropertyInfo(form, dataPath);
        if (found != null)
        {
            return;
        }

        for (int i = 0; i < dataPath.getSegments().size(); i++)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            String segment = dataPath.getSegments().get(i);
            if (!foundSegmentObjects.contains(i))
            {
                String propertyName = getCotainingPropertyPresentation(dataPath);
                String message = MessageFormat.format(Messages.DataPathReferredObjectCheck_message, propertyName,
                    String.join(".", dataPath.getSegments()), i + 1, segment); //$NON-NLS-1$

                resultAceptor.addIssue(message, ABSTRACT_DATA_PATH__SEGMENTS);
                return;
            }
        }
    }

    private String getCotainingPropertyPresentation(AbstractDataPath dataPath)
    {
        EObject parent = dataPath;
        if (dataPath.eContainer() instanceof MultiLanguageDataPath)
        {
            parent = dataPath.eContainer();
        }
        EReference feature = parent.eContainmentFeature();

        if (feature != null)
        {
            return Functions.featureToLabel().apply(feature);
        }
        else
        {
            return Messages.DataPathReferredObjectCheck_Data_path;
        }
    }
}
