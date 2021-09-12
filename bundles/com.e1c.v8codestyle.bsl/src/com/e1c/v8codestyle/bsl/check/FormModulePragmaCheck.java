/**
 *
 */
package com.e1c.v8codestyle.bsl.check;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA;
import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.PRAGMA__VALUE;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.EcoreUtil2;

import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.model.Pragma;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * @author Dmitriy Marmyshev
 * @author Victor Golubev
 *
 */
public class FormModulePragmaCheck
    extends BasicCheck
{
    private static final String CHECK_ID = "form-module-pragma"; //$NON-NLS-1$

    private static final Set<String> DEFAULT_COMPILATION_DIRECTIVES = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    static
    {
        DEFAULT_COMPILATION_DIRECTIVES.add("НаКлиенте"); //$NON-NLS-1$
        DEFAULT_COMPILATION_DIRECTIVES.add("AtClient"); //$NON-NLS-1$
        DEFAULT_COMPILATION_DIRECTIVES.add("НаСервере"); //$NON-NLS-1$
        DEFAULT_COMPILATION_DIRECTIVES.add("AtServer"); //$NON-NLS-1$
        DEFAULT_COMPILATION_DIRECTIVES.add("НаСервереБезКонтекста"); //$NON-NLS-1$
        DEFAULT_COMPILATION_DIRECTIVES.add("AtServerNoContext"); //$NON-NLS-1$
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormModulePragmaCheck_title)
            .description(Messages.FormModulePragmaCheck_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.ERROR)
            .module()
            .checkedObjectType(PRAGMA);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        Pragma pragma = (Pragma)object;
        if (DEFAULT_COMPILATION_DIRECTIVES.contains(pragma.getSymbol()))
        {
            Module module = EcoreUtil2.getContainerOfType(pragma, Module.class);
            ModuleType type = module.getModuleType();

            if (type != ModuleType.FORM_MODULE && type != ModuleType.COMMAND_MODULE)
            {
                resultAceptor.addIssue(Messages.FormModulePragmaCheck_Form_module_compilation_pragma_used, pragma,
                    PRAGMA__VALUE);
            }
        }
    }

}
