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

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import com._1c.g5.v8.dt.bsl.model.BooleanLiteral;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.IfPreprocessorStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.PreprocessorItemStatements;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormAttribute;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.ModuleTopObjectNameFilterExtension;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Checks use security password storage.
 *
 *  @author Ivan Sergeev
 */
public class PasswordStorageSecureCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "secure-password-storage"; //$NON-NLS-1$

    private static final String ATTRIBUTE_NAME = "Attribute name"; //$NON-NLS-1$

    private static final Set<String> IMMUTABLE_MAP_ATTRIBUTE =
        Set.of("пароль", "парольsmtp", "password", "passwordsmtp"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private static final String DELIMITER = ","; //$NON-NLS-1$

    private static final String DEFAULT_ATTRIBUTE = String.join(DELIMITER, IMMUTABLE_MAP_ATTRIBUTE);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.PasswordStorageSecureCheck_Title)
            .description(Messages.PasswordStorageSecureCheck_Description)
            .extension(new ModuleTopObjectNameFilterExtension())
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.SECURITY)
            .parameter(ATTRIBUTE_NAME, String.class, DEFAULT_ATTRIBUTE,
                Messages.PasswordStorageSecureCheck_Parametr_Title)
            .module()
            .checkedObjectType(MODULE);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Module formModule = (Module)object;
        if (ModuleType.FORM_MODULE != formModule.getModuleType())
        {
            return;
        }
        Form form = (Form)formModule.getOwner();
        List<FormAttribute> attributes = form.getAttributes();
        for (FormAttribute formAttribute : attributes)
        {
            String attributesNames = parameters.getString(ATTRIBUTE_NAME).toLowerCase();
            if (attributesNames.contains(formAttribute.getName().toLowerCase()))
            {
                List<Method> methods = formModule.allMethods();
                if (useFormData(methods, formAttribute.getName()))
                {
                    if (!useSafeStorage(methods))
                    {
                        resultAceptor.addIssue(Messages.PasswordStorageSecureCheck_Issue,
                            form/*, FormPackage.Literals.FORM*/);
                    }
                }
            }
        }
    }

    private boolean useFormData(List<Method> methods, String attributeName)
    {
        for (Method method : methods)
        {
            if ("ПриЗагрузкеДанныхИзНастроекНаСервере".equalsIgnoreCase(method.getName()) //$NON-NLS-1$
                || "OnLoadDataFromSettingsAtServer".equalsIgnoreCase(method.getName())) //$NON-NLS-1$
            {
                List<Statement> statements = method.allStatements();
                for (Statement statement : statements)
                {
                    if (statement instanceof SimpleStatement simp)
                    {
                        if (simp.getLeft() instanceof StaticFeatureAccess left)
                        {
                            if (left.getName().equalsIgnoreCase(attributeName))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean useSafeStorage(List<Method> methods)
    {
        boolean getPassword = false;
        boolean setPassword = false;
        for (Method method : methods)
        {
            if ("ПриСозданииНаСервере".equalsIgnoreCase(method.getName()) || //$NON-NLS-1$
                "OnCreateAtServer".equalsIgnoreCase(method.getName())) //$NON-NLS-1$
            {
                List<Statement> statements = method.allStatements();
                if (findCall(statements, method.getName()))
                {
                    if (checkStatements(statements))
                    {
                        getPassword = true;
                    }
                }
            }
            else if ("ПриЗаписиНаСервере".equalsIgnoreCase(method.getName()) //$NON-NLS-1$
                || "OnWriteAtServer".equalsIgnoreCase(method.getName())) //$NON-NLS-1$
            {
                List<Statement> statements = method.allStatements();
                if (findCall(statements, method.getName()))
                {
                    if (checkStatements(statements))
                    {
                        setPassword = true;
                    }
                }
            }
        }
        if (getPassword && setPassword)
        {
            return true;
        }
        return false;
    }

    private boolean findCall(List<Statement> statements, String name)
    {
        String checkCall = ""; //$NON-NLS-1$
        String checkCallEn = ""; //$NON-NLS-1$
        if ("ПриСозданииНаСервере".equalsIgnoreCase(name) || //$NON-NLS-1$
            "OnCreateAtServer".equalsIgnoreCase(name)) //$NON-NLS-1$
        {
            checkCall = "общегоназначения.прочитатьданныеизбезопасногохранилища"; //$NON-NLS-1$
            checkCallEn = "common.readdatafromsecurestorage"; //$NON-NLS-1$
        }
        else if ("ПриЗаписиНаСервере".equalsIgnoreCase(name) || "OnWriteAtServer".equalsIgnoreCase(name)) //$NON-NLS-1$ //$NON-NLS-2$
        {
            checkCall = "общегоназначения.записатьданныевбезопасноехранилище"; //$NON-NLS-1$
            checkCallEn = "common.writedatatosecurestorage"; //$NON-NLS-1$
        }
        for (Statement statement : statements)
        {
            String statementText = NodeModelUtils.findActualNodeFor(statement).getText().toLowerCase();
            if (statementText.contains(checkCall) || statementText.contains(checkCallEn))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkStatements(List<Statement> statements)
    {
        for (Statement statement : statements)
        {
            if (hasPrivileged(statement))
            {
                return true;
            }
        }
        return false;
    }

    private boolean hasPrivileged(Statement statement)
    {
        if (statement instanceof SimpleStatement simpleStatement)
        {
            Expression leftExpression = simpleStatement.getLeft();
            if (!(leftExpression instanceof Invocation))
            {
                return false;
            }
            Invocation invocation = (Invocation)leftExpression;
            String name = invocation.getMethodAccess().getName();

            if (("УстановитьПривилегированныйРежим".equalsIgnoreCase(name) //$NON-NLS-1$
                || "SetPrivilegedMode".equalsIgnoreCase(name)) //$NON-NLS-1$
                && invocation.getParams().size() == 1 && invocation.getParams().get(0) instanceof BooleanLiteral)
            {
                return ((BooleanLiteral)invocation.getParams().get(0)).isIsTrue();
            }
        }
        else if (statement instanceof IfStatement ifStatement)
        {
            List<Statement> inIfStatements = ifStatement.getIfPart().getStatements();
            List<Conditional> inElsIfStatements = ifStatement.getElsIfParts();
            boolean safeModeStatus = false;
            for (Statement statementFromIf : inIfStatements)
            {
                if (hasPrivileged(statementFromIf))
                {
                    safeModeStatus = true;
                }
            }
            for (Conditional conditional : inElsIfStatements)
            {
                List<Statement> statemenstCond = conditional.getStatements();
                for (Statement statementFromElsIf : statemenstCond)
                {
                    if (hasPrivileged(statementFromElsIf))
                    {
                        return true;
                    }
                }
            }
            return safeModeStatus;
        }
        else if (statement instanceof TryExceptStatement tryExceptStatement)
        {
            List<Statement> tryStatements = tryExceptStatement.getTryStatements();
            for (Statement tryStatement : tryStatements)
            {
                if (hasPrivileged(tryStatement))
                {
                    return true;
                }
            }
            return false;
        }
        else if (statement instanceof IfPreprocessorStatement ifPrepStat)
        {
            List<EObject> ifObjects = ifPrepStat.getIfPart().eContents();
            for (EObject eObject : ifObjects)
            {
                if (eObject instanceof PreprocessorItemStatements preprocessorItemStatements)
                {
                    List<Statement> ifStatements = preprocessorItemStatements.getStatements();
                    for (Statement ifStatement : ifStatements)
                    {
                        if (hasPrivileged(ifStatement))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
