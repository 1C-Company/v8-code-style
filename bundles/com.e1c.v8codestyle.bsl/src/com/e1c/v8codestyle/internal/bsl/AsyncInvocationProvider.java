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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.IEObjectDescription;

import com._1c.g5.v8.dt.bm.xtext.BmAwareResourceSetProvider;
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
import com._1c.g5.v8.dt.mcore.util.Environment;
import com._1c.g5.v8.dt.mcore.util.McoreUtil;
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

    private static final String RET_TYPE_NAME = "Promise"; //$NON-NLS-1$
    private static final String EXEPTION_NAME = "RunCallback"; //$NON-NLS-1$
    private static final String TYPE_NAME = "NotifyDescription"; //$NON-NLS-1$
    private final Map<Version, Collection<String>> cashNames;
    private final Map<Version, Map<String, Collection<String>>> cashTypesMethodNames;
    private final IV8ProjectManager v8ProjectManager;
    private final BmAwareResourceSetProvider resourceSetProvider;
    private final Set<Environment> clientEnv;

    @Inject
    public AsyncInvocationProvider(IV8ProjectManager v8ProjectManager, BmAwareResourceSetProvider resourceSetProvider)
    {
        super();
        this.v8ProjectManager = v8ProjectManager;
        this.resourceSetProvider = resourceSetProvider;
        this.cashNames = new ConcurrentHashMap<>();
        this.cashTypesMethodNames = new ConcurrentHashMap<>();
        this.clientEnv = Set.of(Environment.CLIENT, Environment.MNG_CLIENT, Environment.MOBILE_CLIENT,
            Environment.MOBILE_THIN_CLIENT, Environment.THIN_CLIENT, Environment.WEB_CLIENT);
    }

    @Override
    public Collection<String> getAsyncInvocationNames(Version version)
    {
        return cashNames.computeIfAbsent(version, this::collectGlobalAsyncMethods);
    }

    @Override
    public Map<String, Collection<String>> getAsyncTypeMethodNames(Version version)
    {
        return cashTypesMethodNames.computeIfAbsent(version, this::collectAsyncMethods);
    }

    private Collection<String> collectGlobalAsyncMethods(Version version)
    {
        Collection<String> asyncMethodsNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Iterator<IV8Project> iterator = v8ProjectManager.getProjects().iterator();
        if (iterator.hasNext())
        {
            ResourceSet context = resourceSetProvider.get(iterator.next().getProject());
            IEObjectProvider provider = IEObjectProvider.Registry.INSTANCE.get(McorePackage.Literals.METHOD, version);
            Iterable<IEObjectDescription> items = provider.getEObjectDescriptions(null);
            for (IEObjectDescription item : items)
            {
                EObject object = EcoreUtil.resolve(item.getEObjectOrProxy(), context);
                if (object instanceof Method)
                {
                    collectMethod(asyncMethodsNames, (Method)object);
                }
            }
        }
        return asyncMethodsNames;
    }

    private Map<String, Collection<String>> collectAsyncMethods(Version version)
    {
        Map<String, Collection<String>> asyncMethodsNames = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Iterator<IV8Project> iterator = v8ProjectManager.getProjects().iterator();
        if (iterator.hasNext())
        {
            ResourceSet context = resourceSetProvider.get(iterator.next().getProject());
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
            if (isClient(method) && isRetTypePromise(method))
            {
                if (asyncMethodsNames.get(method.getName()) == null)
                {
                    asyncMethodsNames.putIfAbsent(method.getName(), new TreeSet<>());
                    asyncMethodsNames.putIfAbsent(method.getNameRu(), new TreeSet<>());
                }
                asyncMethodsNames.get(method.getName()).add(type.getName());
                asyncMethodsNames.get(method.getName()).add(type.getNameRu());
                asyncMethodsNames.get(method.getNameRu()).add(type.getName());
                asyncMethodsNames.get(method.getNameRu()).add(type.getNameRu());
            }
        }
    }

    private void collectMethod(Collection<String> asyncMethodsNames, Method method)
    {
        if (EXEPTION_NAME.equals(method.getName()))
        {
            return;
        }

        if (isMethodAsync(method))
        {
            asyncMethodsNames.add(method.getName());
            asyncMethodsNames.add(method.getNameRu());
        }
    }

    private boolean isMethodAsync(Method method)
    {
        if (isClient(method) && isRetTypePromise(method))
        {
            return true;
        }

        for (ParamSet paramSet : method.getParamSet())
        {
            for (Parameter param : paramSet.getParams())
            {
                if (isCallbackDescription(param))
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isClient(Method method)
    {
        for (Environment env : method.environments().toArray())
        {
            if (!clientEnv.contains(env))
            {
                return false;
            }
        }
        return true;
    }

    private boolean isRetTypePromise(Method method)
    {
        for (TypeItem type : method.getRetValType())
        {
            if (RET_TYPE_NAME.equals(McoreUtil.getTypeName(type)))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isCallbackDescription(Parameter param)
    {
        for (TypeItem type : param.getType())
        {
            if (TYPE_NAME.equals(McoreUtil.getTypeName(type)))
            {
                return true;
            }
        }
        return false;
    }

}
