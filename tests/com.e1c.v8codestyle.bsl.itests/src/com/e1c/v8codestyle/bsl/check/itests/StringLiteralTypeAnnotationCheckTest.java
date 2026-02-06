/**
 * Copyright (C) 2025, 1C
 */
package com.e1c.v8codestyle.bsl.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.bsl.stringliteral.contenttypes.BslBuiltInLanguagePreferences;
import com._1c.g5.v8.dt.core.operations.ProjectPipelineJob;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.v8.dt.core.platform.IWorkspaceOrchestrator;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.StringLiteralTypeAnnotationCheck;
import com.google.inject.Inject;

/**
 * A class for testing {@link StringLiteralTypeAnnotationCheck}
 *
 * @author Babin Nikolay
 *
 */
public class StringLiteralTypeAnnotationCheckTest
    extends AbstractSingleModuleTestBase
{

    private static final String PROJECT_NAME = "CommonModule";

    private static final String MODULE_FILE_NAME = "/src/CommonModules/CommonModule/Module.bsl";

    @Inject
    private IBmModelManager modelManager;

    @Inject
    private IWorkspaceOrchestrator workspaceOrchestrator;

    @Inject
    private IDtProjectManager dtProjectManager;

    public StringLiteralTypeAnnotationCheckTest()
    {
        super(StringLiteralTypeAnnotationCheck.class);
    }

    /**
     * Checks invalid annotations locations.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInvalidAnnotationsLocationsMarkers() throws Exception
    {
        IEclipsePreferences preferences =
            BslBuiltInLanguagePreferences.getPreferences(getProject().getWorkspaceProject());
        preferences.putBoolean(BslBuiltInLanguagePreferences.APPLY_TAGS_TO_ENTIRE_EXPRESSION, true);
        preferences.flush();

        updateModule(FOLDER_RESOURCE + "string-literal-annotations-invalid-locations.bsl");
        setCheckEnable(true);

        ResourcesPlugin.getWorkspace().run((monitor) -> {
            getProject().getWorkspaceProject().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
        }, new NullProgressMonitor());

        IBmModel model = modelManager.getModel(getProject());

        Object waitHandle = workspaceOrchestrator.beginExclusiveOperation("Build-waiting", //$NON-NLS-1$
            List.of(getProject()), ProjectPipelineJob.BEFORE_BUILD_DD);

        try
        {
            model.execute(new AbstractBmTask<Void>()
            {
                @Override
                public Void execute(IBmTransaction transaction, IProgressMonitor progressMonitor)
                {
                    return null;
                }
            });
        }
        finally
        {
            workspaceOrchestrator.endOperation(waitHandle);
        }

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        assertEquals(Integer.valueOf(22), markers.get(0).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
        assertEquals(Integer.valueOf(25), markers.get(1).getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
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
