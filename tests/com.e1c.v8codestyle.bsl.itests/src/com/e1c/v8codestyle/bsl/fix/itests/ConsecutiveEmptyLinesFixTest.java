/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.naming.ISymbolicLinkLocalizer;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.naming.MdSymbolicLinkLocalizer;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com._1c.g5.v8.dt.ui.validation.BmMarkerWrapper;
import com._1c.g5.v8.dt.validation.marker.IMarkerWrapper;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.check.qfix.FixProcessHandle;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFixManager;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;
import com.e1c.v8codestyle.bsl.qfix.ConsecutiveEmptyLinesFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link ConsecutiveEmptyLinesFix} fix.
 *
 * @author Artem Iliukhin
 */
public class ConsecutiveEmptyLinesFixTest
    extends CheckTestBase
{

    private static final String PROJECT_NAME = "ConsecutiveEmptyLines";
    private static final String FQN_COMMON_MODULE = "CommonModule.CommonModule";
    private static final String CHECK_ID = "module-consecutive-blank-lines";
    private static final String DESCRIPTION = "Clear extra empty lines";
    private IFixManager fixManager = ServiceAccess.get(IFixManager.class);
    private IV8ProjectManager projectManager = ServiceAccess.get(IV8ProjectManager.class);
    private ISymbolicLinkLocalizer symbolicLinkLocalizer = new MdSymbolicLinkLocalizer();
    private final OpenHelper openHelper = new OpenHelper();

    @Test
    public void testApplyFix() throws Exception
    {
        IResourceLookup resourceLookup = BslPlugin.getDefault().getInjector().getInstance(IResourceLookup.class);

        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_COMMON_MODULE, dtProject);
        assertTrue(object instanceof CommonModule);
        CommonModule module = (CommonModule)object;

        Marker marker = getFirstMarker(CHECK_ID, module.getModule(), dtProject);
        assertNotNull(marker);

        applyFix(marker, dtProject);

        IFile file = resourceLookup.getPlatformResource(module.getModule());

        file.setContents(file.getContents(), false, true, new NullProgressMonitor());

        waitForDD(dtProject);

        object = getTopObjectByFqn(FQN_COMMON_MODULE, dtProject);
        assertTrue(object instanceof CommonModule);
        module = (CommonModule)object;

        waitForDD(dtProject);

        marker = getFirstMarker(CHECK_ID, module.getModule(), dtProject);
        assertNull(marker);
    }

    /**
     * Apply fix for the marker.
     *
     * @param marker the marker
     * @param dtProject the DT project of the marker
     */
    private void applyFix(Marker marker, IDtProject dtProject)
    {
        IMarkerWrapper markerWrapper = new BmMarkerWrapper(marker, dtProject.getWorkspaceProject(), bmModelManager,
            projectManager, symbolicLinkLocalizer, openHelper);

        FixProcessHandle handle = fixManager.prepareFix(markerWrapper, dtProject);

        FixVariantDescriptor variantDescr = null;

        Collection<FixVariantDescriptor> variants = fixManager.getApplicableFixVariants(handle);
        for (FixVariantDescriptor variant : variants)
        {
            if (variant.getDescription().matches(DESCRIPTION))
            {
                variantDescr = variant;
            }
        }

        fixManager.selectFixVariant(variantDescr, handle);
        fixManager.executeFix(handle, new NullProgressMonitor());
        fixManager.finishFix(handle);
    }

}

