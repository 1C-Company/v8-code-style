/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NUMBER_QUALIFIERS;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NUMBER_QUALIFIERS__PRECISION;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.TYPE_DESCRIPTION;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.TYPE_DESCRIPTION__NUMBER_QUALIFIERS;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.TYPE_DESCRIPTION__TYPES;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.BASIC_DB_OBJECT;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.mcore.NumberQualifiers;
import com._1c.g5.v8.dt.mcore.TypeDescription;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.BasicFeature;
import com._1c.g5.v8.dt.metadata.mdclass.DefinedType;
import com._1c.g5.v8.dt.platform.IEObjectTypeNames;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.md.CorePlugin;

/**
 * The check that the DB attribute has number type and it is not more then 31.
 *
 *  @author Dmitriy Marmyshev
 *
 */
public final class DbObjectMaxNumberLengthCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "db-object-max-number-length"; //$NON-NLS-1$

    public static final String MAX_LENGTH = "maxNumberLength"; //$NON-NLS-1$

    public static final String MAX_LENGTH_DEFAULT = "31"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.DbObjectMaxNumberLengthCheck_title)
            .description(Messages.DbObjectMaxNumberLengthCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.PORTABILITY)
            .extension(new StandardCheckExtension(467, getCheckId(), CorePlugin.PLUGIN_ID))
            .extension(new SkipAdoptedInExtensionMdObjectExtension())
            .parameter(MAX_LENGTH, Integer.class, MAX_LENGTH_DEFAULT, Messages.DbObjectMaxNumberLengthCheck_parameter);

        builder.topObject(BASIC_DB_OBJECT)
            .containment(TYPE_DESCRIPTION)
            .features(TYPE_DESCRIPTION__TYPES, TYPE_DESCRIPTION__NUMBER_QUALIFIERS);

        builder.topObject(BASIC_DB_OBJECT).containment(NUMBER_QUALIFIERS).features(NUMBER_QUALIFIERS__PRECISION);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (object instanceof TypeDescription)
        {
            check((TypeDescription)object, resultAceptor, parameters, monitor);
        }
        else if (object instanceof NumberQualifiers)
        {
            check((NumberQualifiers)object, resultAceptor, parameters, monitor);
        }
    }

    private void check(TypeDescription object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (!(object.eContainer() instanceof BasicFeature) || object.getTypes().size() != 1
            || !IEObjectTypeNames.DEFINED_TYPE.equals(McoreUtil.getTypeCategory(object.getTypes().get(0))))
        {
            return;
        }

        TypeDescription typeDescription = object;
        EObject definedType = typeDescription.getTypes().get(0).eContainer();
        while (definedType != null && !(definedType instanceof DefinedType))
        {
            definedType = definedType.eContainer();
        }
        if (definedType instanceof DefinedType)
        {
            typeDescription = ((DefinedType)definedType).getType();
        }

        checkAndAddIssue(typeDescription, () -> {
            BasicFeature basicFeature = (BasicFeature)object.eContainer();
            return basicFeature.getName();
        }, resultAceptor, object, TYPE_DESCRIPTION__TYPES, parameters, monitor);

    }

    private void check(NumberQualifiers object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        if (!(object.eContainer() instanceof TypeDescription)
            || !(object.eContainer().eContainer() instanceof BasicFeature))
        {
            return;
        }
        TypeDescription typeDescription = (TypeDescription)object.eContainer();

        checkAndAddIssue(typeDescription, () -> {
            BasicFeature basicFeature = (BasicFeature)typeDescription.eContainer();
            return basicFeature.getName();
        }, resultAceptor, object, NUMBER_QUALIFIERS__PRECISION, parameters, monitor);
    }

    private void checkAndAddIssue(TypeDescription typeDescription, Supplier<String> basicFeatureName,
        ResultAcceptor resultAceptor, EObject object, EStructuralFeature feature, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (monitor.isCanceled() || typeDescription.getNumberQualifiers() == null
            || typeDescription.getTypes()
                .stream()
                .map(McoreUtil::getTypeName)
                .filter(Objects::nonNull)
                .noneMatch(IEObjectTypeNames.NUMBER::equals))
        {
            return;
        }

        int maxPrecision = parameters.getInt(MAX_LENGTH);
        int precision = typeDescription.getNumberQualifiers().getPrecision();

        if (precision > maxPrecision)
        {
            resultAceptor.addIssue(MessageFormat.format(Messages.DbObjectMaxNumberLengthCheck_message,
                basicFeatureName.get(), maxPrecision), object, feature);
        }
    }
}
