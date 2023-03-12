package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.DYNAMIC_FEATURE_ACCESS;

import java.text.MessageFormat;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormParameter;
import com._1c.g5.v8.dt.bsl.model.DynamicFeatureAccess;
import com._1c.g5.v8.dt.bsl.model.Expression;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.StaticFeatureAccess;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Check the use unknown form parameter access in form module
 * @author Vadim Goncharov
 */
public class UnknownFormParameterAccessCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "unknown-form-parameter-access"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD = "Parameters"; //$NON-NLS-1$

    private static final String PARAMETERS_KEYWORD_RU = "Параметры"; //$NON-NLS-1$

    private static final Set<String> STANDART_PARAMETERS_LIST = Set.of("ChoiceMode", //$NON-NLS-1$
        "AdditionalParameters", //$NON-NLS-1$
        "ДополнительныеПараметры", //$NON-NLS-1$
        "Basis", //$NON-NLS-1$
        "Основание", //$NON-NLS-1$
        "ChoiceParameters", //$NON-NLS-1$
        "ПараметрыВыбора", //$NON-NLS-1$
        "CloseOnChoice", //$NON-NLS-1$
        "ЗакрыватьПриВыборе", //$NON-NLS-1$
        "CloseOnOwnerClose", //$NON-NLS-1$
        "ЗакрыватьПриЗакрытииВладельца", //$NON-NLS-1$
        "CopyingValue", //$NON-NLS-1$
        "ЗначениеКопирования", //$NON-NLS-1$
        "FillingText", //$NON-NLS-1$
        "ТекстЗаполнения", //$NON-NLS-1$
        "FunctionalOptionParameters", //$NON-NLS-1$
        "ПараметрыФункциональныхОпций", //$NON-NLS-1$
        "Key", //$NON-NLS-1$
        "Ключ", //$NON-NLS-1$
        "PurposeUseKey", //$NON-NLS-1$
        "КлючНазначенияИспользования", //$NON-NLS-1$
        "ReadOnly", //$NON-NLS-1$
        "ТолькоПросмотр", //$NON-NLS-1$
        "SourceRecordKey", //$NON-NLS-1$
        "ИсходныйКлючЗаписи", //$NON-NLS-1$
        "VersionNumberSwitchToDataHistoryVersion", //$NON-NLS-1$
        "НомерВерсииПереходаНаВерсиюИсторииДанны"); //$NON-NLS-1$

    private static final Set<String> STANDART_METHODS_LIST = Set.of("Свойство", //$NON-NLS-1$
        "Property"); //$NON-NLS-1$

    public UnknownFormParameterAccessCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.UnknownFormParameterAccessCheck_title)
            .description(Messages.UnknownFormParameterAccessCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.CODE_STYLE)
            .extension(new StandardCheckExtension(741, getCheckId(), BslPlugin.PLUGIN_ID))
            .extension(ModuleTypeFilter.onlyTypes(ModuleType.FORM_MODULE))
            .module()
            .checkedObjectType(DYNAMIC_FEATURE_ACCESS);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        DynamicFeatureAccess dfa = (DynamicFeatureAccess)object;

        String dfaName = dfa.getName();
        Expression src = dfa.getSource();
        if (!(src instanceof StaticFeatureAccess) || !isFormParameterAccess((StaticFeatureAccess)src)
            || isStandartFormParameter(dfaName) || isStardarmFormParameterMethod(dfaName) || monitor.isCanceled())
        {
            return;
        }

        Module module = EcoreUtil2.getContainerOfType(dfa, Module.class);
        EObject moduleOwner = module.getOwner();
        if (moduleOwner == null || monitor.isCanceled())
        {
            return;
        }

        Form form = (Form)moduleOwner;

        EList<FormParameter> formParameters = form.getParameters();
        Set<String> paramNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (FormParameter param : formParameters)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            paramNames.add(param.getName().toLowerCase());
        }

        if (!paramNames.contains(dfaName))
        {
            resultAcceptor.addIssue(MessageFormat.format(Messages.UnknownFormParameterAccessCheck_Unknown_form_parameter_access, dfaName), dfa);
        }
    }

    private boolean isFormParameterAccess(StaticFeatureAccess sfa)
    {
        String name = sfa.getName();
        return name.equalsIgnoreCase(PARAMETERS_KEYWORD) || name.equalsIgnoreCase(PARAMETERS_KEYWORD_RU);
    }

    private boolean isStardarmFormParameterMethod(String dfaName)
    {
        return STANDART_METHODS_LIST.contains(dfaName);
    }

    private boolean isStandartFormParameter(String dfaName)
    {
        return STANDART_PARAMETERS_LIST.contains(dfaName);
    }

}
