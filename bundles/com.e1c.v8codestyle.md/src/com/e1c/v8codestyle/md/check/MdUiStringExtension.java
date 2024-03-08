/**
 *
 */
package com.e1c.v8codestyle.md.check;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.Language;
import com.e1c.g5.v8.dt.check.CheckParameterDefinition;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;

/**
 * @author Dmitriy Marmyshev
 *
 */
public class MdUiStringExtension
    implements IBasicCheckExtension
{

    public static final String CHECK_LANGUAGES_PARAMETER_NAME = "checkLanguages"; //$NON-NLS-1$
    public static final String CHECK_ALL_LANGUAGES = "all"; //$NON-NLS-1$
    public static final String CHECK_DEFAUL_LANGUAGE = "default"; //$NON-NLS-1$

    private final List<String> defaultValue;
    private final String parameterTitle;


    public MdUiStringExtension()
    {
        this("List of language codes to check, or \"all\" for all or \"default\" for main language",
            List.of(CHECK_DEFAUL_LANGUAGE));
    }

    public MdUiStringExtension(String parameterTitle, List<String> defaultValue)
    {
        this.parameterTitle = parameterTitle;
        this.defaultValue = defaultValue;
    }

    @Override
    public void configureContextCollector(final ICheckDefinition definition)
    {
        final CheckParameterDefinition parameterDefinition = new CheckParameterDefinition(
            CHECK_LANGUAGES_PARAMETER_NAME,
            String.class, String.join(",", this.defaultValue), this.parameterTitle); //$NON-NLS-1$
        definition.addParameterDefinition(parameterDefinition);
    }

    public static Collection<String> getLanguageCodes(ICheckParameters parameters, EObject context,
        IV8ProjectManager v8ProjectManager)
    {
        String value = parameters.getString(CHECK_LANGUAGES_PARAMETER_NAME);
        Set<String> languageCodes = value == null ? Set.of() : Set.of(value.replace(" ", StringUtils.EMPTY).split(",")); //$NON-NLS-1$ //$NON-NLS-2$

        if (languageCodes.contains(CHECK_ALL_LANGUAGES))
        {
            IV8Project v8Project = v8ProjectManager.getProject(context);
            return v8Project.getLanguages().stream().map(Language::getLanguageCode).collect(Collectors.toSet());

        }
        else if (languageCodes.contains(CHECK_DEFAUL_LANGUAGE))
        {
            IV8Project v8Project = v8ProjectManager.getProject(context);
            Language language = v8Project.getDefaultLanguage();
            if (language == null && v8Project instanceof IExtensionProject)
            {
                Configuration configuration = ((IExtensionProject)v8Project).getConfiguration();
                if (configuration != null)
                {
                    language = configuration.getDefaultLanguage();
                }
            }
            return language == null ? Set.of() : Set.of(language.getLanguageCode());
        }
        else
        {
            return languageCodes;
        }
    }

}
