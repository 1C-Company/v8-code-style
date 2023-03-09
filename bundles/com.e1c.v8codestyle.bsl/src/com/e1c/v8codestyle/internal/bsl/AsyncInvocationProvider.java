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
package com.e1c.v8codestyle.internal.bsl;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.IEObjectDescription;

import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.ContextDef;
import com._1c.g5.v8.dt.mcore.McorePackage;
import com._1c.g5.v8.dt.mcore.Method;
import com._1c.g5.v8.dt.mcore.ParamSet;
import com._1c.g5.v8.dt.mcore.Parameter;
import com._1c.g5.v8.dt.mcore.Type;
import com._1c.g5.v8.dt.mcore.TypeItem;
import com._1c.g5.v8.dt.mcore.TypeSet;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.platform.IEObjectProvider;
import com._1c.g5.v8.dt.platform.version.Version;
import com.e1c.v8codestyle.bsl.IAsyncInvocationProvider;
import com.google.inject.Inject;

/**
 * Implementing service to provide asynchronous methods
 *
 * @author Artem Iliukhin
 */
public class AsyncInvocationProvider
    implements IAsyncInvocationProvider
{

    private final Map<Version, Collection<String>> cashNames;
    private final Map<Version, Map<String, Collection<String>>> cashTypesMethodNames;
    private final IConfigurationProvider configurationProvider;
    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public AsyncInvocationProvider(IConfigurationProvider configurationProvider, IV8ProjectManager v8ProjectManager)
    {
        super();
        this.configurationProvider = configurationProvider;
        this.v8ProjectManager = v8ProjectManager;
        this.cashNames = new ConcurrentHashMap<>();
        this.cashTypesMethodNames = new ConcurrentHashMap<>();
    }

    @Override
    public Collection<String> getAsyncInvocationNames(Version version)
    {
        if (cashNames.get(version) == null)
        {
            synchronized (this)
            {
                if (cashNames.get(version) != null)
                {
                    return cashNames.get(version);
                }
                collect(version);
            }
        }
        return cashNames.get(version);
    }

    @Override
    public Map<String, Collection<String>> getAsyncTypeMethodNames(Version version)
    {
        if (cashTypesMethodNames.get(version) == null)
        {
            synchronized (this)
            {
                if (cashTypesMethodNames.get(version) != null)
                {
                    return cashTypesMethodNames.get(version);
                }
                process(version);
            }
        }
        return cashTypesMethodNames.get(version);
    }

    private void collect(Version version)
    {
        Collection<String> asyncMethodsNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        Collection<IV8Project> projects = v8ProjectManager.getProjects();
        for (IV8Project project : projects)
        {
            Configuration context = configurationProvider.getConfiguration(project.getProject());
            IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.METHOD, version);

            Iterable<IEObjectDescription> items = provider.getEObjectDescriptions(null);
            for (IEObjectDescription item : items)
            {
                EObject object = EcoreUtil.resolve(item.getEObjectOrProxy(), context);
                if (object instanceof Method)
                {
                    collectMethods(asyncMethodsNames, object);
                }
            }
        }
        cashNames.put(version, asyncMethodsNames);
    }

    private Map<String, Collection<String>> process(Version version)
    {
        Map<String, Collection<String>> asyncMethodsNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Collection<IV8Project> projects = v8ProjectManager.getProjects();
        for (IV8Project project : projects)
        {
            Configuration context = configurationProvider.getConfiguration(project.getProject());

            IEObjectProvider provider =
                IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.TYPE_ITEM, version);
            Iterable<IEObjectDescription> items = provider.getEObjectDescriptions(null);

            for (IEObjectDescription item : items)
            {
                EObject object = EcoreUtil.resolve(item.getEObjectOrProxy(), context);
                if (object instanceof Type)
                {
                    Type type = (Type)object;
                    process(asyncMethodsNames, type);
                }
                else if (object instanceof TypeSet)
                {
                    TypeSet typeSet = (TypeSet)object;
                    for (Type type : typeSet.getTypes())
                    {
                        process(asyncMethodsNames, type);
                    }
                }
            }
        }

        cashTypesMethodNames.put(version, asyncMethodsNames);

        return asyncMethodsNames;
    }

    private void process(Map<String, Collection<String>> asyncMethodsNames, Type type)
    {
        if (type == null || type.eIsProxy())
        {
            return;
        }

        ContextDef contextDef = type.getContextDef();
        if (contextDef == null)
        {
            return;
        }

        for (Method method : contextDef.allMethods())
        {
            if (method.getName().endsWith("Async")) //$NON-NLS-1$
            {
                if (asyncMethodsNames.get(method.getName()) != null)
                {
                    asyncMethodsNames.get(method.getName()).add(type.getName());
                    asyncMethodsNames.get(method.getNameRu()).add(type.getNameRu());
                }
                asyncMethodsNames.putIfAbsent(method.getName(), new TreeSet<>());
                asyncMethodsNames.putIfAbsent(method.getNameRu(), new TreeSet<>());
            }
        }
    }

    private void collectMethods(Collection<String> asyncMethodsNames, EObject object)
    {
        Method method = (Method)object;
        if ("RunCallback".equals(method.getName())) //$NON-NLS-1$
        {
            return;
        }

        if (method.getName().endsWith("Async")) //$NON-NLS-1$
        {
            asyncMethodsNames.add(method.getName());
            asyncMethodsNames.add(method.getNameRu());
        }
        else
        {
            checkParamAndCollectMethods(asyncMethodsNames, method);
        }
    }

    private void checkParamAndCollectMethods(Collection<String> asyncMethodsNames, Method method)
    {
        for (ParamSet paramSet : method.getParamSet())
        {
            for (Parameter param : paramSet.getParams())
            {
                if (isCallbackDescription(param))
                {
                    asyncMethodsNames.add(method.getName());
                    asyncMethodsNames.add(method.getNameRu());
                    break;
                }
            }
        }
    }

    private boolean isCallbackDescription(Parameter param)
    {
        for (TypeItem type : param.getType())
        {
            if ("NotifyDescription".equals(McoreUtil.getTypeName(type))) //$NON-NLS-1$
            {
                return true;
            }
        }
        return false;
    }

}
