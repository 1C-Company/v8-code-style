package com.e1c.v8codestyle.md.configuration.qfix;

import java.text.MessageFormat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com.e1c.g5.v8.dt.check.qfix.IFixSession;
import com.e1c.g5.v8.dt.check.qfix.components.BasicModelFixContext;
import com.e1c.g5.v8.dt.check.qfix.components.MultiVariantModelBasicFix;
import com.e1c.g5.v8.dt.check.qfix.components.QuickFix;
import com.e1c.v8codestyle.internal.md.CorePlugin;

@QuickFix(checkId = "configuration-version", supplierId = CorePlugin.PLUGIN_ID)
public class ConfigurationVersionFix
    extends MultiVariantModelBasicFix
{

    private static final String DEFAULT_VERSION = "1.0.1.1"; //$NON-NLS-1$
    private static final String SPLITTER = "\\."; //$NON-NLS-1$
    private static final String DELIMITER = "."; //$NON-NLS-1$

    @Override
    protected void buildVariants()
    {
        MultiVariantModelBasicFix.VariantBuilder builder = MultiVariantModelBasicFix.VariantBuilder.create(this);
        builder
            .description("Set default version",
                MessageFormat.format("Set or update version with default pattern {0} ", DEFAULT_VERSION))
            .change(this::setDefaultVersion)
            .build();
        builder.description("Remove incorrect version", "Set version to empty string")
            .change(this::removeVersion)
            .build();

    }

    private void setDefaultVersion(EObject model, EStructuralFeature feature, BasicModelFixContext context,
        IFixSession session)
    {
        if (model instanceof Configuration)
        {
            Configuration object = (Configuration)model;
            String version = object.getVersion();
            if (version == null)
            {
                version = StringUtils.EMPTY;
            }
            String[] versionParts = DEFAULT_VERSION.split(SPLITTER);
            String[] oldVersionParts = version.split(SPLITTER);

            for (int i = 0; i < oldVersionParts.length && i < versionParts.length; i++)
            {
                String part = oldVersionParts[i];
                if (StringUtils.isBlank(part))
                {
                    continue;
                }
                try
                {
                    int versionPart = Integer.parseInt(part);
                    versionParts[i] = String.valueOf(versionPart);
                }
                catch (NumberFormatException e)
                {
                    // continue
                }
            }

            object.setVersion(String.join(DELIMITER, versionParts));
        }
    }

    private void removeVersion(EObject model, EStructuralFeature feature, BasicModelFixContext context,
        IFixSession session)
    {

        if (model instanceof Configuration)
        {
            Configuration object = (Configuration)model;
            object.setVersion(StringUtils.EMPTY);
        }
    }
}
