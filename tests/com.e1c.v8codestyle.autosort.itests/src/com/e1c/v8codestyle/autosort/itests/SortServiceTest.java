/*******************************************************************************
 * Copyright (C) 2021, 1C-Soft LLC and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     1C-Soft LLC - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.autosort.itests;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.core.event.BmChangeEvent;
import com._1c.g5.v8.bm.core.event.BmEvent;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.bm.integration.event.IBmSyncEventListener;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationAware;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.md.sort.MdSortPreferences;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassFactory;
import com._1c.g5.v8.dt.testing.GuiceModules;
import com._1c.g5.v8.dt.testing.JUnitGuiceRunner;
import com._1c.g5.v8.dt.testing.TestingWorkspace;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;
import com.e1c.v8codestyle.autosort.ISortService;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * Tests for {@link ISortService}
 */
@RunWith(JUnitGuiceRunner.class)
@GuiceModules(modules = { ExternalDependenciesModule.class })
public class SortServiceTest
{
    private static final String PROJECT_NAME = "Sort";

    @Rule
    public TestingWorkspace testingWorkspace = new TestingWorkspace(true, true);

    @Inject
    private ISortService sortService;

    @Inject
    public IDtProjectManager dtProjectManager;

    @Inject
    private IV8ProjectManager v8ProjectManager;

    @Inject
    public IBmModelManager bmModelManager;

    @Test
    public void testSortOff() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IStatus status = sortService.sortAllMetadata(dtProject, new NullProgressMonitor());
        assertTrue(status.isOK());
        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("ГМодуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("АМ_Модуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(5).getName());
    }

    @Test
    public void testSortSortTop() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IEclipsePreferences autoSortPrefs = AutoSortPreferences.getPreferences(project);
        autoSortPrefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, true);
        autoSortPrefs.flush();

        IEclipsePreferences mdSortPrefs = MdSortPreferences.getPreferences(project);
        mdSortPrefs.putBoolean(MdSortPreferences.NATURAL_SORT_ORDER, true);
        mdSortPrefs.flush();

        IStatus status = sortService.sortAllMetadata(dtProject, new NullProgressMonitor());
        assertTrue(status.isOK());
        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ_Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(5).getName());
    }

    @Test
    public void testSortOrderAsDesigner() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);

        IEclipsePreferences autoSortPrefs = AutoSortPreferences.getPreferences(project);
        autoSortPrefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, true);
        autoSortPrefs.flush();

        IEclipsePreferences mdSortPrefs = MdSortPreferences.getPreferences(project);
        mdSortPrefs.putBoolean(MdSortPreferences.NATURAL_SORT_ORDER, false);
        mdSortPrefs.flush();

        IStatus status = sortService.sortAllMetadata(dtProject, new NullProgressMonitor());
        assertTrue(status.isOK());
        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ_Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(5).getName());
    }

    @Test
    public void testSortAfterRemoveEvent() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);
        IV8Project v8Project = v8ProjectManager.getProject(dtProject);
        assertNotNull(dtProject);

        IEclipsePreferences autoSortPrefs = AutoSortPreferences.getPreferences(project);
        autoSortPrefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, true);
        autoSortPrefs.flush();

        IEclipsePreferences mdSortPrefs = MdSortPreferences.getPreferences(project);
        mdSortPrefs.putBoolean(MdSortPreferences.NATURAL_SORT_ORDER, false);
        mdSortPrefs.flush();

        CountDownLatch waitSortStartedlatch = new CountDownLatch(1);
        bmModelManager.addSyncEventListener(bmModelManager.getBmNamespace(project),
            new BmEventListener(project, waitSortStartedlatch)
            {
                boolean isBmObjectRemoved = false;
                boolean isStortStarted = false;

                @Override
                protected void registerEvent(Notification notification)
                {
                    if (notification.getEventType() == Notification.REMOVE)
                    {
                        isBmObjectRemoved = true;
                    }
                    else if (notification.getEventType() == Notification.MOVE)
                    {
                        isStortStarted = true;
                    }
                }

                @Override
                protected boolean isShouldCountDownLatch()
                {
                    return isBmObjectRemoved && isStortStarted;
                }
            });

        bmModelManager.getModel(project).execute(new AbstractBmTask<Void>("Detach a top object") //$NON-NLS-1$
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject commonModule = transaction.getTopObjectByFqn("CommonModule.ГМодуль");
                Configuration configuration =
                    transaction.toTransactionObject(((IConfigurationAware)v8Project).getConfiguration());
                configuration.getCommonModules().remove((CommonModule)commonModule);
                transaction.detachTopObject(commonModule);
                return null;
            }
        });

        waitSortStartedlatch.await();
        // wait until sorting is completed.
        TimeUnit.SECONDS.sleep(5);

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ_Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(4).getName());
    }

    @Test
    public void testSortAfterMoveEvent() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);
        IV8Project v8Project = v8ProjectManager.getProject(dtProject);
        assertNotNull(dtProject);

        IEclipsePreferences autoSortPrefs = AutoSortPreferences.getPreferences(project);
        autoSortPrefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, true);
        autoSortPrefs.flush();

        IEclipsePreferences mdSortPrefs = MdSortPreferences.getPreferences(project);
        mdSortPrefs.putBoolean(MdSortPreferences.NATURAL_SORT_ORDER, false);
        mdSortPrefs.flush();

        CountDownLatch waitSortStartedlatch = new CountDownLatch(1);
        bmModelManager.addSyncEventListener(bmModelManager.getBmNamespace(project),
            new BmEventListener(project, waitSortStartedlatch)
            {
                private int moveCount = 0;

                @Override
                protected void registerEvent(Notification notification)
                {
                    if (notification.getEventType() == Notification.MOVE)
                    {
                        moveCount++;
                    }
                }

                @Override
                protected boolean isShouldCountDownLatch()
                {
                    return moveCount == 2;
                }
            });

        bmModelManager.getModel(project).execute(new AbstractBmTask<Void>("Move a top object") //$NON-NLS-1$
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor progressMonitor)
            {
                IBmObject commonModule = transaction.getTopObjectByFqn("CommonModule.БМодуль");
                Configuration configuration =
                    transaction.toTransactionObject(((IConfigurationAware)v8Project).getConfiguration());
                configuration.getCommonModules().move(4, (CommonModule)commonModule);
                return null;
            }
        });
        waitSortStartedlatch.await();
        // wait until sorting is performed.
        TimeUnit.SECONDS.sleep(5);

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        assertTrue(object instanceof Configuration);

        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ_Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(5).getName());
    }

    @Test
    public void testSortAfterAddEvent() throws Exception
    {
        IProject project = testingWorkspace.setUpProject(PROJECT_NAME, getClass());
        assertNotNull(project);
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        assertNotNull(dtProject);
        IV8Project v8Project = v8ProjectManager.getProject(dtProject);
        assertNotNull(dtProject);

        IEclipsePreferences autoSortPrefs = AutoSortPreferences.getPreferences(project);
        autoSortPrefs.putBoolean(AutoSortPreferences.KEY_ALL_TOP, true);
        autoSortPrefs.flush();

        IEclipsePreferences mdSortPrefs = MdSortPreferences.getPreferences(project);
        mdSortPrefs.putBoolean(MdSortPreferences.NATURAL_SORT_ORDER, false);
        mdSortPrefs.flush();

        CountDownLatch waitSortStartedlatch = new CountDownLatch(1);
        bmModelManager.addSyncEventListener(bmModelManager.getBmNamespace(project),
            new BmEventListener(project, waitSortStartedlatch)
            {
                boolean isBmObjectAdded = false;
                boolean isStortStarted = false;

                @Override
                protected void registerEvent(Notification notification)
                {
                    if (notification.getEventType() == Notification.ADD)
                    {
                        isBmObjectAdded = true;
                    }
                    else if (notification.getEventType() == Notification.MOVE)
                    {
                        isStortStarted = true;
                    }
                }

                @Override
                protected boolean isShouldCountDownLatch()
                {
                    return isBmObjectAdded && isStortStarted;
                }
            });

        bmModelManager.getModel(project).execute(new AbstractBmTask<Void>("Add a top object") //$NON-NLS-1$
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor progressMonitor)
            {
                CommonModule commonModule = MdClassFactory.eINSTANCE.createCommonModule();
                commonModule.setName("КМодуль");
                commonModule.setUuid(UUID.randomUUID());
                transaction.attachTopObject((IBmObject)commonModule, "CommonModule.КМодуль");

                Configuration configuration =
                    transaction.toTransactionObject(((IConfigurationAware)v8Project).getConfiguration());

                configuration.getCommonModules().add(commonModule);
                return null;
            }
        });
        waitSortStartedlatch.await();
        // wait until sorting is performed.
        TimeUnit.SECONDS.sleep(5);

        IBmObject object = getTopObjectByFqn(CONFIGURATION.getName(), dtProject);
        Configuration configuration = (Configuration)object;
        assertFalse(configuration.getCommonModules().isEmpty());

        assertEquals("АМ_Модуль", configuration.getCommonModules().get(0).getName());
        assertEquals("АМ2_4Модуль", configuration.getCommonModules().get(1).getName());
        assertEquals("АМодуль", configuration.getCommonModules().get(2).getName());
        assertEquals("БМодуль", configuration.getCommonModules().get(3).getName());
        assertEquals("ГМодуль", configuration.getCommonModules().get(4).getName());
        assertEquals("КМодуль", configuration.getCommonModules().get(5).getName());
        assertEquals("ОбщийМодуль", configuration.getCommonModules().get(6).getName());
    }

    protected IBmObject getTopObjectByFqn(final String fqn, IDtProject dtProject)
    {
        IBmModel model = this.bmModelManager.getModel(dtProject);
        return model.executeReadonlyTask(new AbstractBmTask<IBmObject>("GetObject")
        {
            @Override
            public IBmObject execute(IBmTransaction transaction, IProgressMonitor progressMonitor)
            {
                return transaction.getTopObjectByFqn(fqn);
            }
        });
    }

    private abstract class BmEventListener
        implements IBmSyncEventListener
    {
        private final CountDownLatch latch;
        private final IProject project;

        /**
         * Creates instance of listener.
         *
         * @param project project to listen events to, cannot be {@code null}
         * @param latch latch waiting until notified about {@code eventType} and starting sort, cannot be {@code null}
         */
        public BmEventListener(IProject project, CountDownLatch latch)
        {
            this.project = Preconditions.checkNotNull(project);
            this.latch = Preconditions.checkNotNull(latch);
        }

        @Override
        public void handleSyncEvent(BmEvent event)
        {
            for (BmChangeEvent changeEvent : event.getChangeEvents().values())
            {
                if (changeEvent.getObject() instanceof IBmObject && ((IBmObject)changeEvent.getObject()).bmIsTop()
                    && ((IBmObject)changeEvent.getObject()).bmGetFqn() != null)
                {
                    Map<EStructuralFeature, List<Notification>> notifications = changeEvent.getNotifications();
                    for (Entry<EStructuralFeature, List<Notification>> entry : notifications.entrySet())
                    {
                        if (entry.getKey() instanceof EReference
                            && AutoSortPreferences.isAllowedToSort(project, (EReference)entry.getKey()))
                        {
                            for (Notification notification : entry.getValue())
                            {
                                registerEvent(notification);
                            }
                        }
                    }
                }
            }
            if (isShouldCountDownLatch())
            {
                latch.countDown();
            }
        }

        /**
         * Specifies how event must be registered.
         *
         * @param notification the notification containing event data, cannot be {@code null}
         */
        protected abstract void registerEvent(Notification notification);

        /**
         * Checks if listener should count down latch.
         *
         * @return {@code true} if should count down latch, {@code false} otherwise
         */
        protected abstract boolean isShouldCountDownLatch();
    }
}
