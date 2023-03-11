package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.dt.bsl.model.BinaryExpression;
import com._1c.g5.v8.dt.bsl.model.BinaryOperation;
import com._1c.g5.v8.dt.bsl.model.Conditional;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.FunctionStyleCreator;
import com._1c.g5.v8.dt.bsl.model.GotoStatement;
import com._1c.g5.v8.dt.bsl.model.IfStatement;
import com._1c.g5.v8.dt.bsl.model.IndexAccess;
import com._1c.g5.v8.dt.bsl.model.Invocation;
import com._1c.g5.v8.dt.bsl.model.LoopStatement;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.OperatorStyleCreator;
import com._1c.g5.v8.dt.bsl.model.SimpleStatement;
import com._1c.g5.v8.dt.bsl.model.Statement;
import com._1c.g5.v8.dt.bsl.model.TryExceptStatement;
import com._1c.g5.v8.dt.bsl.model.UnaryExpression;
import com._1c.g5.v8.dt.bsl.model.WhileStatement;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.CommonSenseCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

public final class CognitiveComplexityCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "cognitive-complexity"; //$NON-NLS-1$
    private static Set<BinaryOperation> COMPLEXITY_OPERATORS = Set.of(BinaryOperation.OR, BinaryOperation.AND);

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title("Когнитивная сложность")
            .description("Когнитивная сложность")
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new CommonSenseCheckExtension(getCheckId(), BslPlugin.PLUGIN_ID))
            .module()
            .checkedObjectType(METHOD)
            .parameter("complexityThreshold", Integer.class, "15", "Допустимая сложность");
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        int complexityValue = 0;
        int nestedLevel = 0;
        Method method = (Method)object;
        for (Statement statement : method.getStatements())
        {
            if (monitor.isCanceled())
            {
                return;
            }
            complexityValue += computeStatementComplexity(statement, nestedLevel, monitor);
        }
        int complexityThreshold = parameters.getInt("complexityThreshold");
        if (complexityValue > complexityThreshold)
        {
            resultAceptor.addIssue("Когнитивная сложность " + Integer.toString(complexityValue), NAMED_ELEMENT__NAME);
        }
    }

    private int computeStatementComplexity(Statement statement, int nestedLevel, IProgressMonitor monitor)
    {
        int complexityValue = 0;
        if (monitor.isCanceled())
        {
            return complexityValue;
        }
        if (statement instanceof LoopStatement)
        {
            complexityValue += 1 + nestedLevel;
            if (statement instanceof WhileStatement)
            {
                complexityValue +=
                    computeExpressionComplexity(((WhileStatement)statement).getPredicate(), nestedLevel, monitor);
            }
            for (Statement substatement : ((LoopStatement)statement).getStatements())
            {
                complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, monitor);
            }
        }
        else if (statement instanceof IfStatement)
        {
            complexityValue += nestedLevel;
            IfStatement ifStatement = (IfStatement)statement;
            Conditional ifPart = ifStatement.getIfPart();
            complexityValue += computeConditionalComplexity(ifPart, nestedLevel, monitor);
            for (Conditional elseIfPart : ifStatement.getElsIfParts())
            {
                complexityValue += computeConditionalComplexity(elseIfPart, nestedLevel, monitor);
            }
            EList<Statement> substatements = ifStatement.getElseStatements();
            if (!substatements.isEmpty())
            {
                complexityValue++;
                for (Statement substatement : substatements)
                {
                    complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, monitor);
                }
            }
        }
        else if (statement instanceof TryExceptStatement)
        {
            TryExceptStatement tryExceptStatement = (TryExceptStatement)statement;
            for (Statement substatement : tryExceptStatement.getTryStatements())
            {
                complexityValue += computeStatementComplexity(substatement, nestedLevel, monitor);
            }
            for (Statement substatement : tryExceptStatement.getExceptStatements())
            {
                complexityValue += nestedLevel + 1;
                complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, monitor);
            }
        }
        else if (statement instanceof GotoStatement)
        {
            complexityValue++;
        }
        else if (statement instanceof SimpleStatement)
        {
            SimpleStatement simpleStatement = (SimpleStatement)statement;
            complexityValue += computeExpressionComplexity(simpleStatement.getLeft(), nestedLevel, monitor);
            Expression right = simpleStatement.getRight();
            if (right != null)
            {
                complexityValue += computeExpressionComplexity(right, nestedLevel, monitor);
            }
        }

        return complexityValue;
    }

    private int computeConditionalComplexity(Conditional conditional, int nestedLevel, IProgressMonitor monitor)
    {
        int complexityValue = 1;
        complexityValue += computeExpressionComplexity(conditional.getPredicate(), nestedLevel, monitor);
        for (Statement substatement : conditional.getStatements())
        {
            complexityValue += computeStatementComplexity(substatement, nestedLevel + 1, monitor);
        }
        return complexityValue;
    }

    private int computeExpressionComplexity(Expression expression, int nestedLevel, IProgressMonitor monitor)
    {
        // TODO: увеличивать вложенность для тренарного оператора и вложенного метода
        // TODO: увелисть сложность для тренарного оператора и рекурсивного вызова
        int complexityValue = 0;
        if (expression instanceof BinaryExpression)
        {
            BinaryExpression binaryExpression = (BinaryExpression)expression;
            if (COMPLEXITY_OPERATORS.contains(binaryExpression.getOperation()))
            {
                complexityValue++;
            }
            complexityValue += computeExpressionComplexity(binaryExpression.getLeft(), nestedLevel, monitor);
            complexityValue += computeExpressionComplexity(binaryExpression.getRight(), nestedLevel, monitor);
        }
        else if (expression instanceof UnaryExpression)
        {
            complexityValue +=
                computeExpressionComplexity(((UnaryExpression)expression).getOperand(), nestedLevel, monitor);
        }
        else if (expression instanceof DynamicFeatureAccess)
        {
            complexityValue +=
                computeExpressionComplexity(((DynamicFeatureAccess)expression).getSource(), nestedLevel, monitor);
        }
        else if (expression instanceof Invocation)
        {
            Invocation invocation = (Invocation)expression;
            for (Expression parameter : invocation.getParams())
            {
                complexityValue += computeExpressionComplexity(parameter, nestedLevel, monitor);
            }
        }
        else if (expression instanceof IndexAccess)
        {
            IndexAccess indexAccess = (IndexAccess)expression;
            complexityValue += computeExpressionComplexity(indexAccess.getSource(), nestedLevel, monitor);
            complexityValue += computeExpressionComplexity(indexAccess.getIndex(), nestedLevel, monitor);
        }
        else if (expression instanceof FunctionStyleCreator)
        {
            FunctionStyleCreator creator = (FunctionStyleCreator)expression;
            complexityValue += computeExpressionComplexity(creator.getTypeNameExpression(), nestedLevel, monitor);
            Expression params = creator.getParamsExpression();
            if (params != null)
            {
                complexityValue += computeExpressionComplexity(params, nestedLevel, monitor);
            }
        }
        else if (expression instanceof OperatorStyleCreator)
        {
            for (Expression parameter : ((OperatorStyleCreator)expression).getParams())
            {
                complexityValue += computeExpressionComplexity(parameter, nestedLevel, monitor);
            }
        }
        return complexityValue;
    }
}
