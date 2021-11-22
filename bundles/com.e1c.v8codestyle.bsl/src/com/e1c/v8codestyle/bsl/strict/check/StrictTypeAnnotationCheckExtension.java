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
/**
 *
 */
package com.e1c.v8codestyle.bsl.strict.check;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com.e1c.g5.v8.dt.check.CheckParameterDefinition;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.v8codestyle.bsl.strict.StrictTypeUtil;

/**
 * The check extension adds parameter allows to skip search for annotation in module header.
 * The extension also pre-check that that annotation exist.
 *
 * @author Dmitriy Marmyshev
 */
public class StrictTypeAnnotationCheckExtension
    implements IBasicCheckExtension
{

    protected static final String PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION = "checkAnnotationInModuleDescription"; //$NON-NLS-1$

    protected static final String DEFAULT_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION = Boolean.TRUE.toString();

    @Override
    public void configureContextCollector(ICheckDefinition definition)
    {
        CheckParameterDefinition paramDef = new CheckParameterDefinition(PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION,
            Boolean.class, DEFAULT_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION,
            Messages.StrictTypeAnnotationCheckExtension_Check__strict_types_annotation_in_module_desctioption);
        definition.addParameterDefinition(paramDef);
    }

    @Override
    public boolean preCheck(Object object, ICheckParameters parameters, IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || !(object instanceof EObject))
        {
            return false;
        }

        boolean checkAnnotation = parameters.getBoolean(PARAM_CHECK_ANNOTATION_IN_MODULE_DESCRIPTION);
        if (!checkAnnotation)
        {
            return true;
        }

        Module module = EcoreUtil2.getContainerOfType((EObject)object, Module.class);

        if (monitor.isCanceled() || module == null)
        {
            return false;
        }

        return StrictTypeUtil.hasStrictTypeAnnotation(module);
    }

}
