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
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.OPERATOR_STYLE_CREATOR;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.STRING_LITERAL__LINES;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.dt.bsl.model.BslContextDef;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FeatureEntry;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.resource.DynamicFeatureAccessComputer;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.mcore.ContextDef;
import com._1c.g5.v8.dt.mcore.DerivedProperty;
import com._1c.g5.v8.dt.mcore.Environmental;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.Property;
import com._1c.g5.v8.dt.mcore.util.Environments;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Check that notify description procedure is exist and available at the client.
 *
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 */
public class NotifyDescriptionToServerProcedureCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "notify-description-to-server-procedure"; //$NON-NLS-1$

    private static final String COMMA = ","; //$NON-NLS-1$

    private static final String BSLCD = "bslcd"; //$NON-NLS-1$

    private static final String THIS_OBJECT = "ThisObject"; //$NON-NLS-1$

    private static final String THIS_OBJECT_RU = "ЭтотОбъект"; //$NON-NLS-1$

    private static final String NOTIFICATION = "NotifyDescription"; //$NON-NLS-1$

    private final DynamicFeatureAccessComputer dynamicFeatureAccessComputer;

    public NotifyDescriptionToServerProcedureCheck()
    {
        super();

        IResourceServiceProvider rsp =
            IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("*.bsl")); //$NON-NLS-1$

        this.dynamicFeatureAccessComputer = rsp.get(DynamicFeatureAccessComputer.class);
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.NotifyDescriptionToServerProcedureCheck_title)
            .description(Messages.NotifyDescriptionToServerProcedureCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .module()
            .checkedObjectType(OPERATOR_STYLE_CREATOR);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        OperatorStyleCreator osc = (OperatorStyleCreator)object;
        String operatorName = McoreUtil.getTypeName(osc.getType());

        if (!NOTIFICATION.equals(operatorName) || osc.getParams().isEmpty()
            || !(osc.getParams().get(0) instanceof StringLiteral))
            return;

        StringLiteral param = (StringLiteral)osc.getParams().get(0);
        final String methodName = getCalledProcedureName(param);
        if (monitor.isCanceled() || StringUtils.isBlank(methodName))
        {
            return;
        }

        BslContextDef bslContextDef = getBslContexDef(osc);

        if (monitor.isCanceled() || bslContextDef == null)
        {
            return;
        }

        Set<String> listMethodNames = getAllMethodNames(bslContextDef);

        if (listMethodNames.contains(methodName))
        {
            Method method = getExportMethod(bslContextDef, methodName);
            if (method == null)
            {
                resultAceptor.addIssue(
                    Messages.NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_should_be_export,
                    param, STRING_LITERAL__LINES);
            }
            else
            {
                Environments calleeEnv = method.environments();
                if (!calleeEnv.containsAny(Environments.MNG_CLIENTS))
                {
                    resultAceptor.addIssue(
                        Messages.NotifyDescriptionToServerProcedureCheck_Notify_description_to_Server_procedure, param,
                        STRING_LITERAL__LINES);
                }
            }
        }
        else
        {
            resultAceptor.addIssue(
                Messages.NotifyDescriptionToServerProcedureCheck_Notify_description_procedure_not_found, param,
                STRING_LITERAL__LINES);
        }
    }

    private Set<String> getAllMethodNames(BslContextDef bslContextDef)
    {
        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String[] allMethodNames = bslContextDef.getAllMethodNames().split(COMMA);
        result.addAll(List.of(allMethodNames));
        return result;
    }

    private String getCalledProcedureName(Expression param)
    {
        if (param instanceof StringLiteral)
        {
            StringLiteral literal = (StringLiteral)param;
            if (literal.getLines().size() == 1)
                return literal.lines(true).get(0);
        }
        return null;
    }

    private Method getExportMethod(BslContextDef bslContextDef, String methodName)
    {
        List<Method> methods = bslContextDef.allMethods();
        for (Method method : methods)
        {
            if (methodName.equalsIgnoreCase(method.getName()))
            {
                return method;
            }
        }
        return null;
    }

    private BslContextDef getBslContexDef(OperatorStyleCreator osc)
    {
        List<Expression> params = osc.getParams();

        if (params.size() > 1)
        {
            Expression moduleParam = params.get(1);
            if (moduleParam instanceof StaticFeatureAccess)
            {
                return getBslContexDef((StaticFeatureAccess)moduleParam);
            }
            else if (moduleParam instanceof DynamicFeatureAccess)
            {
                return getBslContexDef((DynamicFeatureAccess)moduleParam);
            }
        }

        return null;
    }

    private BslContextDef getBslContexDef(StaticFeatureAccess object)
    {
        if (object.getName().equals(THIS_OBJECT) || object.getName().equals(THIS_OBJECT_RU))
        {
            ContextDef contextDef = EcoreUtil2.getContainerOfType(object, Module.class).getContextDef();
            if (contextDef instanceof BslContextDef)
            {
                return (BslContextDef)contextDef;
            }
        }
        else
        {
            URI uri = getCommonModuleUri(object);
            uri = constructBslCdUri(uri);
            EObject contextDef = EcoreFactory.eINSTANCE.createEObject();
            ((InternalEObject)contextDef).eSetProxyURI(uri);
            contextDef = EcoreUtil.resolve(contextDef, object);
            if (!contextDef.eIsProxy() && contextDef instanceof BslContextDef)
            {
                return (BslContextDef)contextDef;
            }
        }
        return null;
    }

    private BslContextDef getBslContexDef(DynamicFeatureAccess object)
    {

        URI uri = getCommonModuleUri(object);
        uri = constructBslCdUri(uri);
        EObject contextDef = EcoreFactory.eINSTANCE.createEObject();
        ((InternalEObject)contextDef).eSetProxyURI(uri);
        contextDef = EcoreUtil.resolve(contextDef, object);
        if (!contextDef.eIsProxy() && contextDef instanceof BslContextDef)
        {
            return (BslContextDef)contextDef;
        }
        return null;
    }

    private URI constructBslCdUri(URI uri)
    {
        if (uri == null)
            return null;
        return uri.trimFileExtension().appendFileExtension(BSLCD);
    }

    private URI getCommonModuleUri(DynamicFeatureAccess object)
    {
        Environmental environmental = EcoreUtil2.getContainerOfType(object, Environmental.class);

        List<FeatureEntry> features = dynamicFeatureAccessComputer.getLastObject(object, environmental.environments());
        for (FeatureEntry featureEntry : features)
        {
            EObject feature = featureEntry.getFeature();
            if (feature instanceof Property && ((Property)feature).getName().equals(THIS_OBJECT))
            {
                Expression staticFeatureAccess = object.getSource();
                if (staticFeatureAccess instanceof StaticFeatureAccess)
                {
                    return getCommonModuleUri((StaticFeatureAccess)staticFeatureAccess);
                }
            }
            else if (feature instanceof Module)
            {
                return EcoreUtil.getURI(feature);
            }
            else if (feature instanceof CommonModule)
            {
                EObject module = (EObject)feature.eGet(MdClassPackage.Literals.COMMON_MODULE__MODULE, false);
                if (module != null)
                {
                    return EcoreUtil.getURI(module);
                }
            }
        }
        return null;
    }

    private URI getCommonModuleUri(StaticFeatureAccess object)
    {
        for (FeatureEntry entry : object.getFeatureEntries())
        {
            EObject f = entry.getFeature();
            if (f instanceof DerivedProperty)
            {
                EObject source = ((DerivedProperty)f).getSource();
                if (source instanceof CommonModule)
                {
                    return EcoreUtil.getURI((EObject)source.eGet(MdClassPackage.Literals.COMMON_MODULE__MODULE, false));
                }
            }
        }
        return null;
    }

}
