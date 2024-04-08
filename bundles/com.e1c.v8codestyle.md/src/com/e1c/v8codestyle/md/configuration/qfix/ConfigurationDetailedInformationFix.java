package com.e1c.v8codestyle.md.configuration.qfix;

import java.util.Map.Entry;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.g5.v8.dt.check.qfix.components.SingleVariantModelBasicFix;
import com.e1c.v8codestyle.internal.md.CorePlugin;

@QuickFix(checkId = "configuration-detailed-information", supplierId = CorePlugin.PLUGIN_ID)
public class ConfigurationDetailedInformationFix
    extends SingleVariantModelBasicFix<Configuration>
{

    @Override
    protected void configureFix(SingleVariantModelBasicFix.FixConfigurer configurer)
    {
        super.configureFix(configurer);
        configurer.description("Replace detailed information with synonym");
    }

    @Override
    protected void applyChanges(Configuration object, EStructuralFeature feature, BasicModelFixContext context,
        IFixSession session)
    {
        object.getDetailedInformation().clear();
        for (Entry<String, String> entry : object.getSynonym())
        {
            object.getDetailedInformation().put(entry.getKey(), entry.getValue());
        }
    }
}
