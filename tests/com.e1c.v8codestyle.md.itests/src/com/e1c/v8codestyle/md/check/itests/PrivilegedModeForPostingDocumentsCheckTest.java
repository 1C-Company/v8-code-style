/**
 *
 */
package com.e1c.v8codestyle.md.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.md.check.PrivilegedModeForPostingDocumentsCheck;

/**
 * The test class for {@link PrivilegedModeForPostingDocumentsCheck}
 *
 * @author Vitaly Prolomov
 *
 */
public class PrivilegedModeForPostingDocumentsCheckTest
    extends CheckTestBase
{
    private static final String PROJECT_NAME = "PrivilegedModeForPostingDocuments";

    private static final String CHECK_ID = "privileged-mode-for-posting-documents";

    /**
     * Test Document is not meant to be posted (posting is not allowed and does not have any registers).
     *
     * @throws CoreException
     */
    @Test
    public void testDocumentNotMeantToBePosted() throws CoreException
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        long id = getTopObjectIdByFqn("Document.DocumentNotMeantToBePosted", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);

        assertNull(marker);
    }

    /**
     * Test Document is meant to be posted (posting allowed and document has registers)
     * and privilege posting mode is on (So, everything is correct and no warnings are generated).
     *
     * @throws CoreException
     */
    @Test
    public void testDocumentWithPrivilegedPostedModeOn() throws CoreException
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        long id = getTopObjectIdByFqn("Document.DocumentWithPrivilegedPostingMode", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);

        assertNull(marker);
    }

    /**
     * Test Document is meant to be posted but privileged posting mode is not on,
     * so warning is generated.
     *
     * @throws CoreException
     */
    @Test
    public void testDocumentWithUnprivilegedPostedModeOn() throws CoreException
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        long id = getTopObjectIdByFqn("Document.DocumentWithUnprivilegedPostingMode", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);

        assertNotNull(marker);
    }

    /**
     * Test document is meant to be posted but privilege unposting mode is off,
     * so warning is generated.
     *
     * @throws CoreException
     */
    @Test
    public void testDocumentWithUnprivilegedUnpostingModeOn() throws CoreException
    {
        IDtProject project = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(project);

        long id = getTopObjectIdByFqn("Document.DocumentWithUnprivilegedUnpostingMode", project);
        Marker marker = getFirstMarker(CHECK_ID, id, project);

        assertNotNull(marker);
    }

}
