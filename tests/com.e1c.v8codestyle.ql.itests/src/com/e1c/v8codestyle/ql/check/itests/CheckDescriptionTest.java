/**
 *
 */
package com.e1c.v8codestyle.ql.check.itests;

import static com.e1c.v8codestyle.internal.ql.CorePlugin.PLUGIN_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.e1c.g5.v8.dt.check.settings.CheckUid;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.internal.ql.CorePlugin;

/**
 * The test of existing all markdown description for every check of the bundle.
 * Note! That maven tycho run test only for HTML files which creates from markdown.
 *
 * @author Dmitriy Marmyshev
 */
public class CheckDescriptionTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String HTML_PATH_PREFIX = "/check.descriptions/";

    private static final String MD_PATH_PREFIX = "/markdown/";

    private static final String HTML_DESCRIPTION_EXT = ".html";

    private static final String MD_DESCRIPTION_EXT = ".md";

    private static final String MESSAGE = "Checks in bundle {0} has no description files:\n- {1}";

    private static final List<String> LANGUAGE_CODES = List.of("", "ru");

    private static final String PROJECT_NAME = "CastToMaxNumber";

    @Test
    public void testCheckDefaultDescriptionExist() throws Exception
    {
        Set<CheckUid> checks = checkRepository.getChecksWithDescriptions()
            .entrySet()
            .stream()
            .filter(e -> PLUGIN_ID.equals(e.getKey().getContributorId()))
            .map(Entry::getKey)
            .collect(Collectors.toSet());

        assertFalse(checks.isEmpty());

        Collection<String> notFound = new ArrayList<>();
        Class<?> bundleClass = CorePlugin.getDefault().getClass();

        for (CheckUid check : checks)
        {
            for (String languageCode : LANGUAGE_CODES)
            {
                String langFolder = languageCode.isEmpty() ? "" : languageCode + "/";
                String path = HTML_PATH_PREFIX + langFolder + check.getCheckId() + HTML_DESCRIPTION_EXT;
                String mdPath = MD_PATH_PREFIX + langFolder + check.getCheckId() + MD_DESCRIPTION_EXT;

                if (bundleClass.getResource(path) == null && bundleClass.getResource(mdPath) == null)
                {
                    notFound.add(PLUGIN_ID + mdPath);
                }
            }
        }

        assertTrue(MessageFormat.format(MESSAGE, PLUGIN_ID, String.join("\n- ", notFound)), notFound.isEmpty());
    }

    @Override
    protected String getTestConfigurationName()
    {
        // The project is need to initialize check repository
        return PROJECT_NAME;
    }

    @Override
    protected boolean enableCleanUp()
    {
        return true;
    }
}
