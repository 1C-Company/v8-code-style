/**
 *
 */
package com.e1c.v8codestyle.md.check;

import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * The abstract class to check UI feature of EMF object that is not empty or match pattern.
 *
 * @author Dmitriy Marmyshev
 */
public abstract class AbstractUiStringCheck
    extends BasicCheck
{
    protected static final String PARAM_UI_STRING_PATTERN = "uiStringPattern"; //$NON-NLS-1$

    protected final EReference uiStringRef;

    private final IV8ProjectManager v8ProjectManager;

    protected AbstractUiStringCheck(EReference uiStringRef, IV8ProjectManager v8ProjectManager)
    {
        this.uiStringRef = uiStringRef;
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.UI_STYLE)
            .extension(new MdUiStringExtension())
            .parameter(PARAM_UI_STRING_PATTERN, String.class, getUiStringPatternDefaultValue(),
                getUiStringPatternTitle());
    }

    protected String getUiStringPatternDefaultValue()
    {
        return StringUtils.EMPTY;
    }

    protected String getUiStringPatternTitle()
    {
        return "UI string pattern";
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        EObject eObject = (EObject)object;
        Object rawValue = eObject.eGet(uiStringRef);
        if (!(rawValue instanceof EMap))
        {
            return;
        }

        @SuppressWarnings("unchecked")
        EMap<String, String> uiString = (EMap<String, String>)rawValue;

        if (uiString.isEmpty())
        {
            resultAceptor.addIssue(getUiStringIsEmptyForAll(), uiStringRef);
        }
        else
        {
            String uiStringPatternText = parameters.getString(PARAM_UI_STRING_PATTERN);
            Pattern uiStringPattern = StringUtils.isBlank(uiStringPatternText) ? null
                : Pattern.compile(uiStringPatternText, Pattern.UNICODE_CHARACTER_CLASS);

            for (String languageCode : MdUiStringExtension.getLanguageCodes(parameters, eObject, v8ProjectManager))
            {
                String value = uiString.get(languageCode);
                if (StringUtils.isBlank(value))
                {
                    resultAceptor.addIssue(getUiStringIsEmpty(languageCode), uiStringRef);
                }
                else
                {
                    if (uiStringPattern != null && !uiStringPattern.matcher(value).matches())
                    {
                        resultAceptor.addIssue(getUiStringShouldMatchPattern(languageCode, uiStringPatternText),
                            uiStringRef);
                    }
                }
            }
        }
    }

    protected String getUiStringIsEmptyForAll()
    {
        return "UI string is empty for all languages";
    }

    protected String getUiStringIsEmpty(String languageCode)
    {
        return MessageFormat.format("UI string for language \"{0}\" is empty", languageCode);
    }

    protected String getUiStringShouldMatchPattern(String languageCode, String patternText)
    {
        return MessageFormat.format("UI string for language \"{0}\" should match pattern: \"{1}\"", languageCode,
            patternText);
    }

}
