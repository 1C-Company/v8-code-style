/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.BslContextDefMockMethod;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FormalParam;
import com._1c.g5.v8.dt.bsl.model.Function;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ReturnStatement;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Variable;
import com._1c.g5.v8.dt.mcore.DerivedProperty;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;
import com.google.common.base.Strings;

/**
 * 	check array modification in FillCheckProcessing()
 *  check find the modifications of the CheckedAttributes array
 *  and set issue for that
 *
 *  @author Artem Samohvalov
 */
public class FillCheckProcessingPropertiesArrayModificationCheck
    extends BasicCheck<Object>
{
    private static final String CHECKED_METHOD_NAME = "fillcheckprocessing"; //$NON-NLS-1$
    private static final String CHECKED_METHOD_NAME_RU = "обработкапроверкизаполнения"; //$NON-NLS-1$

    private static final String EXCEPT_METHOD_NAME = "deleteuncheckedattributesfromarray"; //$NON-NLS-1$
    private static final String EXCEPT_METHOD_NAME_RU = "удалитьнепроверяемыереквизитыизмассива"; //$NON-NLS-1$

    private static final Set<String> CHECK_ADD_METHOD_CALLS =
        Set.of("add", "добавить", "insert", "вставить", "set", "установить"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
    private static final Set<String> CHECK_DELETE_METHOD_CALLS = Set.of("delete", "удалить", "clear", "очистить"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final int MAX_DEPTH = 20;

    @Override
    public String getCheckId()
    {
        return "fill-check-processing-properties-array-modification"; //$NON-NLS-1$
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FillCheckProcessingPropertiesArrayModificationCheck_Title)
            .description(Messages.FillCheckProcessingPropertiesArrayModificationCheck_Description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new StandardCheckExtension(463, getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Method method = (Method)object;
        String methodNameLower = method.getName().toLowerCase();

        if (!CHECKED_METHOD_NAME.equals(methodNameLower) && !CHECKED_METHOD_NAME_RU.equals(methodNameLower))
        {
            return;
        }

        if (method.getFormalParams().size() != 2)
        {
            return; // incorrect signature of FillCheckProcessing
        }

        CheckContext context = new CheckContext();
        context.resultAcceptor = resultAcceptor;
        context.currentMethod = method;
        context.currentMethodName = methodNameLower;
        context.startMethod = method;

        String checkedAttributesName = Strings.nullToEmpty(method.getFormalParams().get(1).getName()).toLowerCase(); // get target variable, always on second position
        Set<String> variableSet = new HashSet<>();
        variableSet.add(checkedAttributesName);
        context.targetVariableDeque.push(variableSet);

        iterationByMethod(context, 0);
    }

    private void iterationByMethod(CheckContext context, int depth)
    {
        if (depth > MAX_DEPTH || context.currentMethod == null)
        {
            return;
        }

        TreeIterator<EObject> it = EcoreUtil.getAllContents(context.currentMethod, true);
        while (it.hasNext())
        {
            EObject element = it.next();

            if (element instanceof ReturnStatement returnStatement)
            {
                processReturnValue(context, returnStatement, depth);
                if (context.isLastReturnTarget)
                    return; // find target return
            }
            else if (element instanceof SimpleStatement simpleStatement)
            {
                processAssignment(context, simpleStatement, depth);
                processIndexAccess(context, simpleStatement);
            }
            else if (element instanceof Invocation invocation)
            {
                processInvocation(context, invocation, depth);
            }
        }
    }

    /**
     * Unified Invocation handling:
     * - method call on target variable => report issue if Add/Delete
     * - call that receives target as argument => dive into that method
     * - call whose result is later treated as target (handled from assignment side)
     */
    private void processInvocation(CheckContext context, Invocation invocation, int depth)
    {
        if (invocation.getMethodAccess() instanceof DynamicFeatureAccess dynamicAccess) // if access from '.' => this is method call
        {
            if (dynamicAccess.getSource() instanceof StaticFeatureAccess staticAccess) // if source is variable 
            {
                if (isModule(staticAccess))
                {
                    // CommonModule.Method(...) – may receive target as argument
                    goToMethod(context, invocation, false, depth);

                    if (context.hadModification && isStartMethod(context))
                    {
                        setIssueByInvocation(context, invocation);
                        context.hadModification = false;
                    }
                }
                else
                {
                    String varName = staticAccess.getName().toLowerCase();
                    if (containsInVariableDeque(context, varName) && !setModification(context, dynamicAccess.getName()))
                    {
                        setIssueByMethodName(context, dynamicAccess.getName(), invocation);
                    }
                }
            }
            // for - SomeMethod(Target).Delete(); don't work for - SomeMethod(Target).SomeMethod().Delete()
            else if (dynamicAccess.getSource() instanceof Invocation subInvocation)
            {
                goToMethod(context, subInvocation, true, depth);
                if (context.isLastReturnTarget && !setModification(context, dynamicAccess.getName()))
                {
                    setIssueByMethodName(context, dynamicAccess.getName(), invocation);
                }

                if (context.hadModification && isStartMethod(context))
                {
                    setIssueByInvocation(context, invocation);
                    context.hadModification = false;
                }
            }
        }
        else
        {
            // plain Method(...)
            StaticFeatureAccess assignmentLeft = null;
            boolean onlyFunction = false;

            if (invocation.eContainer() instanceof SimpleStatement ss && ss.getRight() == invocation
                && ss.getLeft() instanceof StaticFeatureAccess left && isVariable(left))
            {
                // left = Call(...)
                assignmentLeft = left;
                onlyFunction = true;
            }

            goToMethod(context, invocation, onlyFunction, depth);

            if (assignmentLeft != null && context.isLastReturnTarget)
            {
                addToVariableDeque(context, assignmentLeft.getName().toLowerCase());
            }
            if (context.hadModification && isStartMethod(context))
            {
                setIssueByInvocation(context, invocation);
                context.hadModification = false;
            }
        }
    }

    /**
     * add variable to targetVariableNames
     * if statement has two StaticFeatureAccess in left and right
     * and variable.name = TARGET_VARIABLE_NAME
     * 
     * need for tracking: MyVar = FillCheckProcessing;
     *                    MyVar.Delete(); // error
     * and:               MyVar = FillCheckProcessingRetFunc();
     *                    MyVar.Delete();
     * 
     * @param simpleStatement
     */
    private void processAssignment(CheckContext context, SimpleStatement simpleStatement, int depth)
    {
        if (simpleStatement.getRight() == null
            || !(simpleStatement.getLeft() instanceof StaticFeatureAccess leftStatement) || !isVariable(leftStatement))
        {
            return;
        }

        String leftName = leftStatement.getName().toLowerCase();

        if (simpleStatement.getRight() instanceof StaticFeatureAccess rightStatement && isVariable(rightStatement)) // this is the assignable value
        {
            String rightVarName = rightStatement.getName().toLowerCase();

            // target = something_else => stop tracking left
            if (containsInVariableDeque(context, leftName) && !containsInVariableDeque(context, rightVarName))
            {
                removeFromVariableDeque(context, leftName);
                return;
            }

            // something = target => start tracking right
            if (containsInVariableDeque(context, rightVarName))
            {
                addToVariableDeque(context, leftName);
            }
        }
        else if (containsInVariableDeque(context, leftName))
        {
            // target = something_else => stop tracking left
            removeFromVariableDeque(context, leftName);
        }
    }

    /**
     * the method searches for an assignment to the target by index
     * 
     * example: target_var[1] = "new" // error
     * 
     * @param simpleStatement
     * @param resultAcceptor
     */
    private void processIndexAccess(CheckContext context, SimpleStatement simpleStatement)
    {
        if (simpleStatement.getRight() != null // if some expr on the right: some = some_expr
            && simpleStatement.getLeft() instanceof IndexAccess indexAccess // if index access: some[0] = some_expr
            && indexAccess.getSource() instanceof StaticFeatureAccess staticAccess // if variable: variable[0] = some_expr
            && containsInVariableDeque(context, staticAccess.getName().toLowerCase())) // if target variable: target[0] = some_expr
        {
            if (!isStartMethod(context))
            {
                context.hadModification = true;
                return;
            }
            context.resultAcceptor.addIssue(Messages.FillCheckProcessingPropertiesArrayModificationCheck_IndexSetIssue,
                simpleStatement);
        }
    }

    /**
     * need for tracking: MyVar = ReturnTarget(); // this is target
     * 
     * @param returnStatement
     */
    private void processReturnValue(CheckContext context, ReturnStatement returnStatement, int depth)
    {
        if (returnStatement.getExpression() instanceof StaticFeatureAccess staticAccess)
        {
            context.isLastReturnTarget = containsInVariableDeque(context, staticAccess.getName().toLowerCase());
            return;
        }

        if (returnStatement.getExpression() instanceof Invocation invocationExpression)
        {
            goToMethod(context, invocationExpression, false, depth);
            return;
        }

        context.isLastReturnTarget = false;
    }

    /**
     * go to method 
     * if method has FillCheckProcessing (or it reference) in arguments
     *      or some global var has target reference
     * 
     * @param simpleStatement
     */
    private void goToMethod(CheckContext context, Invocation invocation, boolean onlyFunction, int depth)
    {
        String methodName = invocation.getMethodAccess().getName();
        if (methodName.equalsIgnoreCase(context.currentMethodName))
        {
            return; // direct recursion guard
        }

        Optional<Method> optionalMethod = getMethodByInvocation(context, invocation);
        if (optionalMethod.isEmpty()) // if method name not exists for some reason
        {
            return;
        }

        Method targetMethod = optionalMethod.get();
        if (onlyFunction && !(targetMethod instanceof Function)) // if is not function
            return;

        List<Integer> targetPositions = new ArrayList<>();
        EList<Expression> params = invocation.getParams();

        for (int i = 0; i < params.size(); ++i)
        {
            if (params.get(i) instanceof StaticFeatureAccess sfa
                && containsInVariableDeque(context, sfa.getName().toLowerCase()))
            {
                targetPositions.add(i);
            }
        }

        // if args hasn't a target variable
        if (targetPositions.isEmpty())
        {
            return;
        }

        context.isLastReturnTarget = false;
        Method lastMethod = context.currentMethod;

        changeCurrentMethod(context, targetMethod);
        updateTargetVariablesForMethod(context, targetPositions);

        iterationByMethod(context, depth + 1);
        // method ends

        context.targetVariableDeque.pop(); // method ends, targets don't needs
        changeCurrentMethod(context, lastMethod);
    }

    private void changeCurrentMethod(CheckContext context, Method method)
    {
        context.currentMethod = method;
        context.currentMethodName = method.getName().toLowerCase();
    }

    /**
     * situation:
     * method call      -> CallMethod(MyTargetValue, 123, MySecondTargetValue, "hello") => target is 0 and 2
     * method signature -> CallMethod(A, B, C, D) => A and C is target variables
     * 
     * @param targetPositions
     */
    private void updateTargetVariablesForMethod(CheckContext context, List<Integer> targetPositions)
    {
        EList<FormalParam> argumentList = context.currentMethod.getFormalParams();

        Set<String> targetSet = new HashSet<>();
        for (int i = 0; i < argumentList.size(); ++i)
        {
            if (targetPositions.contains(i))
            {
                targetSet.add(argumentList.get(i).getName().toLowerCase());
            }
        }

        context.targetVariableDeque.push(targetSet);
    }

    private void setIssueByMethodName(CheckContext context, String methodName, EObject location)
    {
        String lowerMethodName = methodName.toLowerCase();

        if (CHECK_ADD_METHOD_CALLS.contains(lowerMethodName))
        {
            String message =
                MessageFormat.format(Messages.FillCheckProcessingPropertiesArrayModificationCheck_AddIssue, methodName);
            context.resultAcceptor.addIssue(message, location);
        }
        else if (CHECK_DELETE_METHOD_CALLS.contains(lowerMethodName)
            && !EXCEPT_METHOD_NAME_RU.equals(context.currentMethodName)
            && !EXCEPT_METHOD_NAME.equals(context.currentMethodName))
        {
            String message = MessageFormat
                .format(Messages.FillCheckProcessingPropertiesArrayModificationCheck_DeleteIssue, methodName);
            context.resultAcceptor.addIssue(message, location);
        }
    }

    private void setIssueByInvocation(CheckContext context, Invocation invocation)
    {
        String calledMethodName = invocation.getMethodAccess() == null ? null : invocation.getMethodAccess().getName();
        if (calledMethodName != null)
        {
            String message = MessageFormat.format(
                Messages.FillCheckProcessingPropertiesArrayModificationCheck_CalledMethodChangeArrayIssue,
                calledMethodName);
            context.resultAcceptor.addIssue(message, invocation);
        }
    }

    private boolean containsInVariableDeque(CheckContext context, String lowerCaseVariableName)
    {
        return !context.targetVariableDeque.isEmpty()
            && context.targetVariableDeque.peek().contains(lowerCaseVariableName);
    }

    private void addToVariableDeque(CheckContext context, String lowerCaseVariableName)
    {
        if (!context.targetVariableDeque.isEmpty())
        {
            context.targetVariableDeque.peek().add(lowerCaseVariableName);
        }
    }

    private void removeFromVariableDeque(CheckContext context, String lowerCaseVariableName)
    {
        if (!context.targetVariableDeque.isEmpty())
        {
            context.targetVariableDeque.peek().remove(lowerCaseVariableName);
        }
    }

    private boolean isStartMethod(CheckContext context)
    {
        return context.currentMethod.equals(context.startMethod);
    }

    private Optional<Method> getMethodByInvocation(CheckContext context, Invocation invocation)
    {
        if (invocation.getMethodAccess() instanceof StaticFeatureAccess staticAccess
            && !staticAccess.getFeatureEntries().isEmpty())
        {
            EObject feature = staticAccess.getFeatureEntries().get(0).getFeature();
            if (feature instanceof Method method)
            {
                return Optional.of(method);
            }
        }
        else if (invocation.getMethodAccess() instanceof DynamicFeatureAccess dynamicAccess
            && !dynamicAccess.getFeatureEntries().isEmpty())
        {
            EObject feature = dynamicAccess.getFeatureEntries().get(0).getFeature();
            if (feature instanceof Method method)
            {
                return Optional.of(method);
            }
            if (feature instanceof BslContextDefMockMethod mockMethod)
            {
                Module module = EcoreUtil2.getContainerOfType(context.currentMethod, Module.class);
                if (module != null && module.eResource() != null)
                {
                    return getMethodByUriAndName(mockMethod.getSourceUri(), mockMethod.getName(),
                        module.eResource().getResourceSet());
                }
            }
        }

        return Optional.empty();
    }

    private boolean isVariable(StaticFeatureAccess staticAccess)
    {
        if (staticAccess.getFeatureEntries().isEmpty())
        {
            return false;
        }
        return staticAccess.getFeatureEntries().get(0).getFeature() instanceof Variable;
    }

    private boolean setModification(CheckContext context, String methodName)
    {
        if (!isStartMethod(context))
        {
            String lower = methodName.toLowerCase();
            if (CHECK_ADD_METHOD_CALLS.contains(lower) || (CHECK_DELETE_METHOD_CALLS.contains(lower)
                && !EXCEPT_METHOD_NAME_RU.equals(context.currentMethodName)
                && !EXCEPT_METHOD_NAME.equals(context.currentMethodName)))
            {
                context.hadModification = true;
                return true;
            }
        }

        return false;
    }

    private boolean isModule(StaticFeatureAccess staticAccess)
    {
        if (staticAccess.getFeatureEntries().isEmpty())
        {
            return false;
        }
        EObject feature = staticAccess.getFeatureEntries().get(0).getFeature();
        if (feature instanceof Module)
        {
            return true;
        }
        if (feature instanceof DerivedProperty derivedProperty)
        {
            return derivedProperty.getSource() instanceof CommonModule;
        }
        return false;
    }

    public Optional<Method> getMethodByUriAndName(URI targetModuleUri, String methodName, ResourceSet resourceSet)
    {
        Resource resource = resourceSet.getResource(targetModuleUri, true);

        if (resource == null || resource.getContents().isEmpty()
            || !(resource.getContents().get(0) instanceof Module targetModule))
        {
            return Optional.empty();
        }

        String lowerName = methodName.toLowerCase();
        for (Method method : targetModule.allMethods())
        {
            if (method.getName().toLowerCase().equals(lowerName))
            {
                return Optional.of(method);
            }
        }

        return Optional.empty();
    }

    private static class CheckContext
    {
        // on the top set of the 'target variables in lower case' for CURRENT method
        // go to method push set
        // return from the method pop set
        final Deque<Set<String>> targetVariableDeque = new ArrayDeque<>();

        Method startMethod;

        ResultAcceptor resultAcceptor;
        Method currentMethod;
        String currentMethodName; // in lower case (for optimization)

        boolean isLastReturnTarget = false;
        boolean hadModification = false;
    }
}
