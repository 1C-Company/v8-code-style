package com.e1c.v8codestyle.md.qfix;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.Language;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.g5.v8.dt.check.qfix.components.SingleVariantModelBasicFix;
import com.e1c.v8codestyle.internal.md.CorePlugin;
import com.google.inject.Inject;

@QuickFix(checkId = "mdo-synonym", supplierId = CorePlugin.PLUGIN_ID)
public class MdObjectSynonymDefaultLanguageFix
    extends SingleVariantModelBasicFix<MdObject>
{

    private final IV8ProjectManager v8ProjectManager;

    @Inject
    public MdObjectSynonymDefaultLanguageFix(IV8ProjectManager v8ProjectManager)
    {
        this.v8ProjectManager = v8ProjectManager;
    }

    @Override
    protected void configureFix(SingleVariantModelBasicFix.FixConfigurer configurer)
    {
        super.configureFix(configurer);
        configurer.description("Generate synonym by name for deafult language");
    }

    @Override
    protected void applyChanges(MdObject object, EStructuralFeature feature, BasicModelFixContext context,
        IFixSession session)
    {
        IV8Project v8Project = v8ProjectManager.getProject(object);
        Language language = v8Project.getDefaultLanguage();
        if (language == null && v8Project instanceof IExtensionProject)
        {
            Configuration configuration = ((IExtensionProject)v8Project).getConfiguration();
            if (configuration != null)
            {
                language = configuration.getDefaultLanguage();
            }
        }
        if (language != null)
        {
            String languageCode = language.getLanguageCode();
            EMap<String, String> synonym = object.getSynonym();
            if (StringUtils.isBlank(synonym.get(languageCode)))
            {
                String name = object.getName();
                synonym.put(languageCode, StringUtils.nameToText(name));
            }
        }
    }
}
