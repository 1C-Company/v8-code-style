package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdObjectAttributeCommentNotExistCheck;

/**
 * Tests for {@link MdObjectAttributeCommentNotExistCheck} check.
 *
 * @author Vadim Goncharov
 *
 */
public class MdObjectAttributeCommentNotExistCheckTest
    extends CheckTestBase
{

    private static final String CHECK_ID = "md-object-attribute-comment-not-exist";

    private static final String PROJECT_NAME = "MdObjectAttributeCommentNotExist";

    /**
     * Test that md object's attribute named "Comment" does not exist
     * @throws Exception the exception
     */
    @Test
    public void testMdObject() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Document.TestDocument1", dtProject);
        Marker marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Document.TestDocument2", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Document.TestDocument3", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("Catalog.TestCatalog1", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

        id = getTopObjectIdByFqn("Catalog.TestCatalog2", dtProject);
        marker = getFirstMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
    }

}
