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
package com.e1c.v8codestyle.internal.autosort;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.CONFIGURATION;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.common.collections.IBmLongMap;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.core.event.BmChangeEvent;
import com._1c.g5.v8.bm.core.event.BmEvent;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.bm.integration.event.BmEventFilter;
import com._1c.g5.v8.bm.integration.event.IBmAsyncEventListener;
import com._1c.g5.v8.dt.core.lifecycle.ProjectContext;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IConfigurationProvider;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.core.platform.IDtProjectManager;
import com._1c.g5.v8.dt.core.platform.IWorkspaceOrchestrator;
import com._1c.g5.v8.dt.lifecycle.LifecycleParticipant;
import com._1c.g5.v8.dt.lifecycle.LifecyclePhase;
import com._1c.g5.v8.dt.lifecycle.LifecycleService;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.e1c.v8codestyle.autosort.AutoSortPreferences;
import com.e1c.v8codestyle.autosort.ISortService;
import com.e1c.v8codestyle.autosort.MdObjectByNameComparator;
import com.e1c.v8codestyle.autosort.SortItem;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Dmitriy Marmyshev
 *
 */
@Singleton
@LifecycleService(name = ISortService.SERVICE_NAME)
public class SortService
    implements ISortService
{

    private static final long JOB_DELAY = 500;

    protected static final String CONFIGURATION_FQN = CONFIGURATION.getName();

    private final IDtProjectManager dtProjectManager;

    private final IBmModelManager modelManager;

    private final IConfigurationProvider configurationProvider;

    private final IWorkspaceOrchestrator workspaceOrchestrator;

    private final BmEventFilter filter = BmEventFilter.eClassChangeFilter(MdClassPackage.Literals.MD_OBJECT);

    private final Map<IProject, IBmAsyncEventListener> projectListeners = new ConcurrentHashMap<>();
    private final Map<IProject, SortJob> jobs = new ConcurrentHashMap<>();

    @Inject
    public SortService(IDtProjectManager dtProjectManager, IBmModelManager modelManager,
        IConfigurationProvider configurationProvider, IWorkspaceOrchestrator workspaceOrchestrator)
    {
        this.dtProjectManager = dtProjectManager;
        this.modelManager = modelManager;
        this.configurationProvider = configurationProvider;
        this.workspaceOrchestrator = workspaceOrchestrator;
    }

    @LifecycleParticipant(phase = LifecyclePhase.RESOURCE_LOADING,
        dependsOn = { IBmModelManager.SERVICE_NAME, IDtProjectManager.SERVICE_NAME })
    public void init(ProjectContext projectContext)
    {

        IDtProject dtProject = projectContext.getProject();
        IProject project = dtProject.getWorkspaceProject();

        if (project != null)
        {
            // register BM listener to track changes
            IBmModel model = modelManager.getModel(project);
            if (model != null)
            {
                IBmAsyncEventListener listener = projectListeners.computeIfAbsent(project, MdObjectChangeListener::new);
                model.addAsyncEventListener(listener, filter);
            }
        }
    }

    @LifecycleParticipant(phase = LifecyclePhase.RESOURCE_UNLOADING)
    public void dispose(ProjectContext lifecycleContext)
    {
        IDtProject dtProject = lifecycleContext.getProject();
        IProject project = dtProject.getWorkspaceProject();

        if (project != null)
        {
            // remove BM listener
            IBmModel model = modelManager.getModel(dtProject);
            if (model != null)
            {
                IBmAsyncEventListener listener = projectListeners.get(project);
                if (listener != null)
                {
                    model.removeAsyncEventListener(listener);
                }
            }
        }
    }

    @Override
    public IStatus sortAllMetadata(IDtProject dtProject, IProgressMonitor monitor)
    {
        IBmModel model = modelManager.getModel(dtProject);
        if (model == null)
        {
            return Status.CANCEL_STATUS;
        }

        IProject project = dtProject.getWorkspaceProject();
        Collection<SortItem> items = readAllObjectsToSort(model, project, monitor);

        if (monitor.isCanceled())
        {
            return Status.CANCEL_STATUS;
        }

        if (project != null)
        {
            SortJob job = jobs.get(project);
            if (job != null)
            {
                try
                {
                    job.join(0, monitor);
                }
                catch (OperationCanceledException e)
                {
                    return Status.CANCEL_STATUS;
                }
                catch (InterruptedException e)
                {
                    AutoSortPlugin.logError(e);
                }
            }
        }

        return sortObject(dtProject, items, monitor);
    }

    @Override
    public IStatus sortObject(IDtProject dtProject, Collection<SortItem> items, IProgressMonitor monitor)
    {
        IBmModel model = modelManager.getModel(dtProject);
        if (model == null || model.isDisposed())
        {
            return Status.OK_STATUS;
        }

        if (monitor.isCanceled() || items == null)
        {
            return Status.CANCEL_STATUS;
        }
        if (items.isEmpty())
        {
            return Status.OK_STATUS;
        }

        model.getGlobalContext().execute(new SortBmTask(items));
        return Status.OK_STATUS;
    }

    @Override
    public void startSortAllMetadata(IProject project)
    {
        Job job = Job.create(Messages.SortService_Read_all_objects_to_sort, monitor -> {

            IBmModel model = modelManager.getModel(project);
            if (model == null)
            {
                return Status.CANCEL_STATUS;
            }

            Collection<SortItem> items = readAllObjectsToSort(model, project, monitor);

            if (monitor.isCanceled())
            {
                return Status.CANCEL_STATUS;
            }

            startSortObject(project, items);
            return Status.OK_STATUS;
        });
        job.schedule();
    }

    @Override
    public void startSortObject(IProject project, Collection<SortItem> items)
    {
        IDtProject dtProject = dtProjectManager.getDtProject(project);
        if (dtProject == null || items.isEmpty())
        {
            return;
        }

        SortJob job = jobs.computeIfAbsent(project, p -> new SortJob(dtProject, this, workspaceOrchestrator));
        job.getQueue().addAll(items);
        job.schedule(JOB_DELAY);
    }

    private Collection<SortItem> readAllObjectsToSort(IBmModel model, IProject project, IProgressMonitor monitor)
    {
        return model.executeReadonlyTask(new ReadSortObjects(project, monitor), true);
    }

    private Comparator<EObject> createProjectSorter(IProject project)
    {
        final Comparator<EObject> sorter;
        if (project == null)
        {
            sorter = new MdObjectByNameComparator(AutoSortPreferences.DEFAULT_SORT_ASCENDING);
        }
        else
        {
            sorter = new MdObjectByNameComparator(AutoSortPreferences.isSortAscending(project));
        }
        return sorter;
    }

    private final class MdObjectChangeListener
        implements IBmAsyncEventListener
    {

        private final IProject project;

        private MdObjectChangeListener(IProject project)
        {
            Assert.isNotNull(project);
            this.project = project;
        }

        @Override
        public void handleAsyncEvent(BmEvent event)
        {
            IBmLongMap<BmChangeEvent> change = event.getChangeEvents();
            if (change == null)
            {
                return;
            }

            Map<String, Set<EReference>> changedItems = new HashMap<>();

            for (BmChangeEvent changeEvent : change.values())
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
                            collectChangesOfList(changedItems, (EReference)entry.getKey(), entry.getValue());
                        }
                        else if (entry.getKey().equals(MdClassPackage.Literals.MD_OBJECT__NAME))
                        {
                            collectChangedOfNames(changedItems, entry.getValue());
                        }
                    }
                }

            }

            processChanges(changedItems);
        }

        private void collectChangesOfList(Map<String, Set<EReference>> changedItems, EReference listRef,
            List<Notification> notifications)
        {
            for (Notification notification : notifications)
            {
                if (notification.getEventType() == Notification.ADD)
                {
                    Object notifier = notification.getNotifier();
                    Object value = notification.getNewValue();
                    if (notifier instanceof IBmObject && value instanceof MdObject)
                    {
                        changedItems.computeIfAbsent(((IBmObject)notifier).bmGetFqn(), k -> new HashSet<>())
                            .add(listRef);
                    }
                }
            }
        }

        private void collectChangedOfNames(Map<String, Set<EReference>> changedItems, List<Notification> notifications)
        {
            for (Notification notification : notifications)
            {
                if (notification.getEventType() == Notification.SET
                    && !(notification.getNotifier() instanceof Configuration))
                {
                    Object notifier = notification.getNotifier();
                    Object value = notification.getNewValue();
                    if (notifier instanceof MdObject && value instanceof String)
                    {
                        MdObject mdObject = (MdObject)notifier;
                        EObject parent = mdObject.eContainer();
                        EReference listRef = (EReference)mdObject.eContainingFeature();
                        if (parent == null && ((IBmObject)mdObject).bmIsTop())
                        {
                            parent = configurationProvider.getConfiguration(mdObject);
                            listRef = getConfigurationFeature(mdObject);
                        }
                        if (!(parent instanceof IBmObject) || listRef == null
                            || !AutoSortPreferences.isAllowedToSort(project, listRef))
                        {
                            continue;
                        }

                        changedItems.computeIfAbsent(((IBmObject)parent).bmGetFqn(), k -> new HashSet<>()).add(listRef);
                    }
                }
            }
        }

        private void processChanges(Map<String, Set<EReference>> changedItems)
        {
            if (!changedItems.isEmpty())
            {
                List<SortItem> items = new ArrayList<>();

                Comparator<EObject> sorter = new MdObjectByNameComparator(AutoSortPreferences.isSortAscending(project));

                for (Entry<String, Set<EReference>> entry : changedItems.entrySet())
                {
                    for (EReference lisRef : entry.getValue())
                    {
                        SortItem item = new SortItem(entry.getKey(), lisRef, sorter);
                        items.add(item);
                    }
                }

                if (!items.isEmpty())
                {
                    startSortObject(project, items);
                }
            }
        }

        private EReference getConfigurationFeature(MdObject mdObject)
        {
            for (EReference feature : CONFIGURATION.getEAllReferences())
            {
                if (!feature.isMany() || feature.equals(MdClassPackage.Literals.CONFIGURATION__CONTENT)
                    || !feature.getEType().isInstance(mdObject))
                {
                    continue;
                }
                return feature;
            }
            return null;
        }

    }

    private class ReadSortObjects
        extends AbstractBmTask<Collection<SortItem>>
    {

        private final IProject project;

        private final IProgressMonitor monitor;

        private final Comparator<EObject> sorter;

        protected ReadSortObjects(IProject project, IProgressMonitor monitor)
        {
            super("Read collections to sort"); //$NON-NLS-1$
            this.project = project;
            this.monitor = monitor;
            sorter = createProjectSorter(project);
        }

        @Override
        public Collection<SortItem> execute(IBmTransaction transaction, IProgressMonitor m)
        {
            Collection<SortItem> result = new ArrayList<>();

            appendByConfiguration(result, transaction, m);

            if (project != null && AutoSortPreferences.isSortSubOrdinateObjects(project)
                || project == null && AutoSortPreferences.DEFAULT_SORT)
            {
                if (monitor.isCanceled() || m.isCanceled())
                {
                    return result;
                }

                Iterable<EClass> eClassIterator = transaction.getTopObjectEClasses();
                Map<EClass, List<EReference>> sortListRefs = getSubordinateListsToSort(eClassIterator, project);

                appendSubordinateObjects(result, sortListRefs, transaction, m);
            }
            return result;
        }

        private void appendSubordinateObjects(Collection<SortItem> result, Map<EClass, List<EReference>> sortListRefs,
            IBmTransaction transaction, IProgressMonitor m)
        {
            for (Entry<EClass, List<EReference>> entry : sortListRefs.entrySet())
            {
                for (Iterator<IBmObject> iterator = transaction.getTopObjectIterator(entry.getKey()); iterator
                    .hasNext();)
                {
                    if (monitor.isCanceled() || m.isCanceled())
                    {
                        return;
                    }

                    IBmObject object = iterator.next();
                    String fqn = object.bmGetFqn();
                    entry.getValue().forEach(ref -> result.add(new SortItem(fqn, ref, sorter)));
                }
            }
        }

        private Map<EClass, List<EReference>> getSubordinateListsToSort(Iterable<EClass> eClassIterator,
            IProject project)
        {
            Map<EClass, List<EReference>> sortListRefs = new HashMap<>();
            for (EClass topObjectEClass : eClassIterator)
            {
                if (topObjectEClass.equals(CONFIGURATION) || !MD_OBJECT.isSuperTypeOf(topObjectEClass))
                {
                    continue;
                }

                for (EReference feature : topObjectEClass.getEAllReferences())
                {
                    if (!feature.isMany() || project != null && !AutoSortPreferences.isAllowedToSort(project, feature)
                        || project == null && !AutoSortPreferences.DEFAULT_SORT)
                    {
                        continue;
                    }

                    sortListRefs.computeIfAbsent(topObjectEClass, e -> new ArrayList<>()).add(feature);
                }
            }
            return sortListRefs;
        }

        private void appendByConfiguration(Collection<SortItem> result, IBmTransaction transaction, IProgressMonitor m)
        {
            EObject top = transaction.getTopObjectByFqn(CONFIGURATION_FQN);
            if (top != null)
            {
                for (EReference feature : CONFIGURATION.getEAllReferences())
                {
                    if (monitor.isCanceled() || m.isCanceled())
                    {
                        return;
                    }

                    if (!feature.isMany() || feature.equals(MdClassPackage.Literals.CONFIGURATION__CONTENT)
                        || project != null && !AutoSortPreferences.isAllowedToSort(project, feature)
                        || project == null && !AutoSortPreferences.DEFAULT_SORT)
                    {
                        continue;
                    }
                    EList<?> collection = (EList<?>)top.eGet(feature, false);
                    if (collection.size() > 1)
                    {
                        result.add(new SortItem(CONFIGURATION_FQN, feature, sorter));
                    }
                }
            }
        }
    }

}
