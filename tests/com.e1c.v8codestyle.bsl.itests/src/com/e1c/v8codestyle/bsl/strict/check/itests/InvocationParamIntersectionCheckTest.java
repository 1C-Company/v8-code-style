/**
 *
 */
package com.e1c.v8codestyle.bsl.strict.check.itests;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.strict.check.InvocationParamIntersectionCheck;

/**
 * Tests for {@link InvocationParamIntersectionCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class InvocationParamIntersectionCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "CatalogModules";

    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/ObjectModule.bsl";

    /**
     * Instantiates a new invocation parameter intersection check test.
     */
    public InvocationParamIntersectionCheckTest()
    {
        super(InvocationParamIntersectionCheck.class);
    }

    /**
     * Test invocation intersection of type {@code CatalogObject.TestCatalog} with parent type {@code CatalogObject}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvocationIntersectionWithParentType() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "strict/invocation-parameter-type-intersect-catalog-object.bsl");

        List<Marker> markers = getModuleMarkers();
        assertTrue(markers.isEmpty());
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return MODULE_FILE_NAME;
    }

}
