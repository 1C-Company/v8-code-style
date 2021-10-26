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
package com.e1c.v8codestyle.bsl.strict.check.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.junit.Ignore;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.StringLiteral;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.bsl.strict.check.DocCommentFieldTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.DynamicFeatureAccessMethodNotFoundCheck;
import com.e1c.v8codestyle.bsl.strict.check.DynamicFeatureAccessTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.FunctionCtorReturnSectionCheck;
import com.e1c.v8codestyle.bsl.strict.check.FunctionReturnTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.InvocationParamIntersectionCheck;
import com.e1c.v8codestyle.bsl.strict.check.MethodParamTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.SimpleStatementTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.StructureCtorValueTypeCheck;
import com.e1c.v8codestyle.bsl.strict.check.VariableTypeCheck;

/**
 * Tests of strict types system in BSL module.
 *
 * @author Dmitriy Marmyshev
 */
public class CommonModuleStrictTypesTest
    extends SingleProjectReadOnlyCheckTestBase
{

    private static final String PROJECT_NAME = "CommonModule";

    private static final String FQN = "CommonModule.CommonModule";

    private static final String FOLDER = "/resources/strict/";

    private static final String COMMON_MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test of {@link StructureCtorValueTypeCheck} structure constructor value has returning type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testStructureCtorValueTypeCheck() throws Exception
    {

        String checkId = "structure-consructor-value-type";

        Module module = updateAndGetModule(checkId);

        List<StringLiteral> literals = EcoreUtil2.eAllOfType(module, StringLiteral.class);
        assertEquals(5, literals.size());

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(literals.get(0)).toString();

        Marker marker = markers.get(0);

        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(1);

        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link VariableTypeCheck} that each variable has value type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVariableTypeCheck() throws Exception
    {

        String checkId = "variable-value-type";

        Module module = updateAndGetModule(checkId);

        List<Variable> variables = EcoreUtil2.eAllOfType(module, Variable.class);
        assertEquals(2, variables.size());

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(1, markers.size());

        String uriToProblem = EcoreUtil.getURI(variables.get(0)).toString();

        Marker marker = markers.get(0);

        assertEquals("4", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link DocCommentFieldTypeCheck} that each field in documentation comment has defined type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDocCommentFieldTypeCheck() throws Exception
    {

        String checkId = "doc-comment-field-type";

        Module module = updateAndGetModule(checkId);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(1, markers.size());

        String uriToProblem = EcoreUtil.getURI(methods.get(0)).toString();

        Marker marker = markers.get(0);

        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link FunctionReturnTypeCheck} that function has returning type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testFunctionReturnTypeCheck() throws Exception
    {

        String checkId = "function-return-value-type";

        Module module = updateAndGetModule(checkId);

        List<Method> methods = module.allMethods();
        assertEquals(3, methods.size());

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(methods.get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(1);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link MethodParamTypeCheck} that each method's parameter has defined or computed type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodParamTypeCheckk() throws Exception
    {

        String checkId = "method-param-value-type";

        Module module = updateAndGetModule(checkId);

        List<Method> methods = module.allMethods();
        assertEquals(2, methods.size());

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(methods.get(0).getFormalParams().get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(1);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link DynamicFeatureAccessMethodNotFoundCheck} that dynamic method exist in the object.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicFeatureAccessMethodNotFoundCheck() throws Exception
    {

        String checkId = "dynamic-access-method-not-found";

        Module module = updateAndGetModule(checkId);

        List<DynamicFeatureAccess> dynamicMethods = EcoreUtil2.eAllOfType(module, DynamicFeatureAccess.class);
        assertEquals(2, dynamicMethods.size());

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(dynamicMethods.get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link DynamicFeatureAccessTypeCheck} that dynamic property exist and has return type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDynamicFeatureAccessTypeCheck() throws Exception
    {

        String checkId = "property-return-type";

        Module module = updateAndGetModule(checkId);

        List<DynamicFeatureAccess> dynamicProperties = EcoreUtil2.eAllOfType(module, DynamicFeatureAccess.class);
        assertEquals(2, dynamicProperties.size());

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(dynamicProperties.get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link SimpleStatementTypeCheck} that the statement change type of existing object type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSimpleStatementTypeCheck() throws Exception
    {

        String checkId = "statement-type-change";

        Module module = updateAndGetModule(checkId);

        List<SimpleStatement> statements = EcoreUtil2.eAllOfType(module, SimpleStatement.class);
        assertEquals(2, statements.size());

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        String uriToProblem = EcoreUtil.getURI(statements.get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

        marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link SimpleStatementTypeCheck} that the statement change type of existing object type.
     * Should respect in-line documentation comment with types
     *
     * @throws Exception the exception
     */
    @Test
    public void testSimpleStatementTypeCheckWithDocComment() throws Exception
    {

        String checkId = "statement-type-change";
        String resourceName = "statement-type-change-with-doc-comment";

        Module module = updateAndGetModule(resourceName);

        List<Marker> markers = getMarters(checkId, module);

        // FIXME check-system duplicates issues
        assertEquals(2, markers.size());

        Marker marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

    }

    /**
     * Test of {@link FunctionCtorReturnSectionCheck} that the statement change type of existing object type.
     *
     * @throws Exception the exception
     */
    @Test
    @Ignore // FIXME check-system fails on issue add
    public void testFunctionCtorReturnSectionCheck() throws Exception
    {

        String checkId = "constructor-function-return-section";

        Module module = updateAndGetModule(checkId);

        List<Function> finctions = EcoreUtil2.eAllOfType(module, Function.class);
        assertEquals(2, finctions.size());

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(1, markers.size());

        String uriToProblem = EcoreUtil.getURI(finctions.get(0)).toString();

        Marker marker = markers.get(0);
        assertEquals("6", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link InvocationParamIntersectionCheck} that invokable method parameter type intersects
     * with caller type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvocationParamIntersectionCheck() throws Exception
    {

        String checkId = "invocation-parameter-type-intersect";

        Module module = updateAndGetModule(checkId);

        List<StaticFeatureAccess> statements = EcoreUtil2.eAllOfType(module, StaticFeatureAccess.class);
        assertEquals(6, statements.size());

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(1, markers.size());

        String uriToProblem = EcoreUtil.getURI(statements.get(2)).toString();

        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));
        assertEquals(uriToProblem, marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_URI_TO_PROBLEM_KEY));

    }

    /**
     * Test of {@link InvocationParamIntersectionCheck} that invokable method parameter type intersects
     * with caller type, and skip checking if method has default value parameters.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvocationParamIntersectionCheckWithDefault() throws Exception
    {

        String checkId = "invocation-parameter-type-intersect";
        String resouceName = "invocation-parameter-type-intersect-with-default";

        Module module = updateAndGetModule(resouceName);

        List<Marker> markers = getMarters(checkId, module);

        assertEquals(1, markers.size());

        Marker marker = markers.get(0);
        assertEquals("5", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

    }

    private List<Marker> getMarters(String checkId, Module module)
    {
        String id = module.eResource().getURI().toPlatformString(true);
        List<Marker> markers =
            new ArrayList<>(Arrays.asList(markerManager.getMarkers(getProject().getWorkspaceProject(), id)));

        markers.removeIf(m -> !checkId.equals(getCheckIdFromMarker(m, getProject())));
        return markers;
    }

    private Module updateAndGetModule(String checkId) throws CoreException, IOException
    {
        try (InputStream in = getClass().getResourceAsStream(FOLDER + checkId + ".bsl"))
        {
            IFile file = getProject().getWorkspaceProject().getFile(COMMON_MODULE_FILE_NAME);
            file.setContents(in, true, true, new NullProgressMonitor());
        }
        testingWorkspace.waitForBuildCompletion();
        waitForDD(getProject());

        IBmObject mdObject = getTopObjectByFqn(FQN, getProject());
        assertTrue(mdObject instanceof CommonModule);
        Module module = ((CommonModule)mdObject).getModule();
        assertNotNull(module);
        return module;
    }

}
