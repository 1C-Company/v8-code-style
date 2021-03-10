/**
 *
 */
package com.e1c.v8codestyle.md.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.testing.check.CheckTestBase;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.md.check.MdObjectNameLength;

/**
 * Tests for {@link MdObjectNameLength} check
 *
 * @author Dmitriy Marmyshev
 *
 */
@SuppressWarnings("nls")
public class MdObjectNameLengthTest
    extends CheckTestBase
{


    private static final String PROJECT_NAME = "MdObjectNameLength";

    /**
     * Test that md object name length longer ther maximal length.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectNameLength() throws Exception
    {

        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Catalog.ProductsTooLong", dtProject);
        Marker marker = getFirstMarker(MdObjectNameLength.CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

    /**
     * Test configuration name length ignores.
     *
     * @throws Exception the exception
     */
    @Test
    public void testConfigurationNameLengthIgnore() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Configuration", dtProject);
        Marker marker = getFirstMarker(MdObjectNameLength.CHECK_ID, id, dtProject);
        assertNull(marker);
    }
}
