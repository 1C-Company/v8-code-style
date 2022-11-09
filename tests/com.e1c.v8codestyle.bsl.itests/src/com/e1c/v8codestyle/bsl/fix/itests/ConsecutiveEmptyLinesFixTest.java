/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import com._1c.g5.v8.dt.core.naming.ISymbolicLinkLocalizer;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.naming.MdSymbolicLinkLocalizer;
import com._1c.g5.v8.dt.ui.util.OpenHelper;
import com._1c.g5.v8.dt.ui.validation.BmMarkerWrapper;
import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.IMarkerWrapper;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.wiring.ServiceAccess;
import com.e1c.g5.v8.dt.check.qfix.FixProcessHandle;
import com.e1c.g5.v8.dt.check.qfix.FixVariantDescriptor;
import com.e1c.g5.v8.dt.check.qfix.IFixManager;
import com.e1c.v8codestyle.bsl.check.ConsecutiveEmptyLinesCheck;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.qfix.ConsecutiveEmptyLinesFix;
import com.e1c.v8codestyle.internal.bsl.BslPlugin;

/**
 * Tests for {@link ConsecutiveEmptyLinesFix} fix.
 *
 * @author Artem Iliukhin
 */
public class ConsecutiveEmptyLinesFixTest
    extends AbstractSingleModuleTestBase
{

    private static final String DESCRIPTION = "Clear extra empty lines";
    private IFixManager fixManager = ServiceAccess.get(IFixManager.class);
    private IV8ProjectManager projectManager = ServiceAccess.get(IV8ProjectManager.class);
    private ISymbolicLinkLocalizer symbolicLinkLocalizer = new MdSymbolicLinkLocalizer();
    private final OpenHelper openHelper = new OpenHelper();

    public ConsecutiveEmptyLinesFixTest()
    {
        super(ConsecutiveEmptyLinesCheck.class);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "empty-lines.bsl");

        Marker marker = getModuleFirstMarker();
        assertNotNull(marker);
        assertEquals("3", marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY));

        applyFix(marker, getProject());

        IResourceLookup resourceLookup = BslPlugin.getDefault().getInjector().getInstance(IResourceLookup.class);
        IFile file = resourceLookup.getPlatformResource(getModule());

        file.setContents(file.getContents(), false, true, new NullProgressMonitor());

        waitForDD(getProject());

        marker = getModuleFirstMarker();
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
