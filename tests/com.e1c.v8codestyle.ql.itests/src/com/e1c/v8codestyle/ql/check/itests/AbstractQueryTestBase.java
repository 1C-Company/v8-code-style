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
package com.e1c.v8codestyle.ql.check.itests;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.Ignore;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.dcs.util.DcsUtil;
import com._1c.g5.v8.dt.ql.model.QuerySchema;
import com.e1c.g5.v8.dt.check.ICheck;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.IDelegateApplicabilityFilter;
import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.check.settings.ICheckParameterSettings;
import com.e1c.g5.v8.dt.check.settings.ICheckSettings;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck;
import com.e1c.g5.v8.dt.ql.check.QlBasicDelegateCheck.QueryOwner;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.internal.ql.CorePlugin;
import com.e1c.v8codestyle.ql.check.itests.TestingQlResultAcceptor.QueryMarker;

/**
 * The abstract query schema test base.
 * Allows to load and validate query schema model from bundle resource file.
 *
 * @author Dmitriy Marmyshev
 */
@Ignore
public class AbstractQueryTestBase
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "QlEmptyProject";

    protected static final String FOLDER_RESOURCE = "/resources/";

    private IDtProject dtProject;

    private Class<? extends ICheck> checkClass;

    private ICheck check;

    private ICheckDefinition definition;

    private TestingCheckResultAcceptor resultAcceptor;

    private TestingQlResultAcceptor qlResultAcceptor;

    /**
     * Instantiates a new abstract query test base.
     *
     * @param checkClass the check class
     */
    protected AbstractQueryTestBase(Class<? extends ICheck> checkClass)
    {
        super();
        this.checkClass = checkClass;
    }

    @Override
    protected boolean enableCleanUp()
    {
        return false;
    }

    /**
     * Gets the DT project instance.
     *
     * @return the project, cannot return {@code null}.
     */
    protected IDtProject getProject()
    {
        return dtProject;
    }

    /**
     * Sets the up of the abstract check. It is forbidden for clients to override this method.
     *
     * @throws Exception the exception
     */
    @Before
    public void setUp() throws Exception
    {

        IProject project = testingWorkspace.getProject(getTestConfigurationName());

        if (!project.exists() || !project.isAccessible())
        {
            testingWorkspace.cleanUpWorkspace();
            dtProject = openProjectAndWaitForValidationFinish(getTestConfigurationName());
        }
        dtProject = dtProjectManager.getDtProject(project);
        setCheckEnable(true);

        resultAcceptor = new TestingCheckResultAcceptor();
        qlResultAcceptor = new TestingQlResultAcceptor();
        QlBasicDelegateCheck.setResultAcceptor((o, f) -> qlResultAcceptor);
    }

    /**
     * Sets the check enable or disable.
     *
     * @param enable the new check enable
     */
    private void setCheckEnable(boolean enable) throws Exception
    {
        IProject project = getProject().getWorkspaceProject();
        CheckUid cuid = new CheckUid(getCheckId(), CorePlugin.PLUGIN_ID);
        ICheckSettings settings = checkRepository.getSettings(cuid, project);
        if (settings.isEnabled() != enable)
        {
            settings.setEnabled(enable);
            checkRepository.applyChanges(Collections.singleton(settings), project);
            waitForDD(getProject());
        }
    }

    /**
     * Gets the test configuration name, the project name.
     *
     * @return the test configuration name, cannot return {@code null}.
     */
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Gets the check instance created in BSL bundle.
     *
     * @return the check instance, cannot return {@code null}.
     */
    private ICheck getCheckInstance() throws Exception
    {
        if (check == null)
        {
            check = CorePlugin.getDefault().getInjector().getInstance(checkClass);
            definition = createCheckDefinition();
            assertNotNull(definition);
            check.configureContextCollector(definition);
        }
        return check;
    }

    /**
     * Gets the check id gets from the check instance.
     *
     * @return the check id, cannot return {@code null}.
     */
    private String getCheckId() throws Exception
    {
        return getCheckInstance().getCheckId();
    }

    /**
     * Load query from bundle resource and validate it.
     *
     * @param resourcePath the resource path, cannot be {@code null}.
     * @return the query schema, cannot return {@code null}.
     * @throws Exception the exception if cannot load query model
     */
    protected QuerySchema loadQueryAndValidate(String resourcePath) throws Exception
    {
        String queryText =
            new String(getClass().getResourceAsStream(resourcePath).readAllBytes(), StandardCharsets.UTF_8);

        QuerySchema querySchema = DcsUtil.getQuerySchema(queryText, getProject());
        assertNotNull(querySchema);

        validateQuery(querySchema);
        return querySchema;
    }

    /**
     * Validate query and collect the errors.
     *
     * @param querySchema the query schema, cannot be {@code null}.
     * @throws Exception the exception
     *
     * @see #getQueryMarkers() to get error markers
     */
    private void validateQuery(QuerySchema querySchema) throws Exception
    {
        QlBasicDelegateCheck.setOwner(new QueryOwner(querySchema, null));

        ICheckParameters checkParametes = getCheckParameters();

        ICheck validator = getCheckInstance();
        if (!(validator instanceof QlBasicDelegateCheck))
        {
            throw new IllegalStateException("Check should be QlBasicDelegateCheck class, but was: " + validator);
        }

        IDelegateApplicabilityFilter filter = getDelegateFilter();
        assertNotNull(filter);
        IProgressMonitor monitor = new NullProgressMonitor();
        for (TreeIterator<EObject> iterator = querySchema.eAllContents(); iterator.hasNext();)
        {
            EObject child = iterator.next();
            if (filter.isApplicable(child))
            {
                validator.check(child, resultAcceptor, checkParametes, monitor);
            }
        }
    }

    /**
     * Gets the query markers: error message, the QL object, it's feature and position in text.
     *
     * @return the query markers, cannot return {@code null}.
     */
    protected List<QueryMarker> getQueryMarkers()
    {
        return qlResultAcceptor.getMarkers();
    }

    private ICheckDefinition createCheckDefinition() throws Exception
    {
        String fullCalss = "com.e1c.g5.v8.dt.internal.check.derived.CheckDefinition";
        Bundle current = FrameworkUtil.getBundle(getClass());
        for (Bundle bundle : current.getBundleContext().getBundles())
        {
            if ("com.e1c.g5.v8.dt.check".equals(bundle.getSymbolicName()))
            {
                Class<?> clazz = bundle.loadClass(fullCalss);
                Constructor<?> c = clazz.getConstructor();
                return (ICheckDefinition)c.newInstance();
            }
        }
        return null;
    }

    private IDelegateApplicabilityFilter getDelegateFilter() throws Exception
    {
        if (definition == null)
        {
            getCheckInstance();
        }
        Method method = definition.getClass().getMethod("getDelegateApplicabilityFilter");
        IDelegateApplicabilityFilter filter = (IDelegateApplicabilityFilter)method.invoke(definition);

        method = definition.getClass().getMethod("getSupportedDelegateTypes");
        @SuppressWarnings("unchecked")
        Set<Class<?>> delegateClasses = (Set<Class<?>>)method.invoke(definition);

        return object -> {
            if (filter != null && !filter.isApplicable(filter))
            {
                return false;
            }
            for (Class<?> delegate : delegateClasses)
            {
                if (delegate.isInstance(object))
                {
                    return true;
                }
            }
            return false;
        };
    }

    private ICheckParameters getCheckParameters() throws Exception
    {
        IProject project = getProject().getWorkspaceProject();
        CheckUid cuid = new CheckUid(getCheckId(), CorePlugin.PLUGIN_ID);
        ICheckSettings settings = checkRepository.getSettings(cuid, project);

        Map<String, Object> values = settings.getParameters()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, e -> getParameterObject(e.getValue())));
        return new TestingCheckParameters(values);
    }

    private Object getParameterObject(ICheckParameterSettings parameter)
    {
        Class<?> type = parameter.getType();
        String value = parameter.getValue();

        if (type == Integer.class)
        {
            return Integer.valueOf(value);
        }
        else if (type == Boolean.class)
        {
            return Boolean.valueOf(value);
        }
        else if (type == Double.class)
        {
            return Double.valueOf(value);
        }
        else if (type == Long.class)
        {
            return Long.valueOf(value);
        }

        return value;
    }

}
