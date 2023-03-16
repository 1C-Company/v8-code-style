package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.MdObjectAttributeCommentCheck;

/**
 * Tests for {@link MdObjectAttributeCommentCheck} check
 *
 * @author Vadim Goncharov
 *
 */
public class MdObjectAttributeCommentCheckTest
    extends CheckTestBase
{
    private static final String CHECK_ID = "md-object-attribute-comment-incorrect-type"; //$NON-NLS-1$

    private static final String PROJECT_NAME = "MdObjectAttributeComment"; //$NON-NLS-1$

    /**
     * Test that md object have attribute Comment of correct type.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdObjectAttributeComment() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        long id = getTopObjectIdByFqn("Document.TestDocument1", dtProject);
        Marker marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
        
        id = getTopObjectIdByFqn("Document.TestDocument2", dtProject);
        marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);

        id = getTopObjectIdByFqn("Document.TestDocument3", dtProject);
        marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNull(marker);
        
        id = getTopObjectIdByFqn("Catalog.TestCatalog1", dtProject);
        marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNotNull(marker);
        
        id = getTopObjectIdByFqn("Catalog.TestCatalog2", dtProject);
        marker = getFirstNestedMarker(CHECK_ID, id, dtProject);
        assertNull(marker);

    }
}
