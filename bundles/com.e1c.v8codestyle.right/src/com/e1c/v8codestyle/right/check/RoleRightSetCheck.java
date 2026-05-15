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
 *     Aleksandr Kapralov - issue #20
 *******************************************************************************/
package com.e1c.v8codestyle.right.check;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHTS;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHTS__OBJECT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT__RIGHT;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.OBJECT_RIGHT__VALUE;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.ROLE_DESCRIPTION;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.ROLE_DESCRIPTION__RIGHTS;
import static com._1c.g5.v8.dt.rights.model.RightsPackage.Literals.ROLE_DESCRIPTION__SET_FOR_NEW_OBJECTS;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;

import com._1c.g5.v8.bm.core.BmUriUtil;
import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexManager;
import com._1c.g5.v8.dt.bm.index.emf.IBmEmfIndexProvider;
import com._1c.g5.v8.dt.bm.index.rights.IBmRightsIndexManager;
import com._1c.g5.v8.dt.bm.index.rights.IBmRightsIndexProvider;
import com._1c.g5.v8.dt.bm.index.rights.IRightsDescription;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.core.platform.IBmModelManager;
import com._1c.g5.v8.dt.core.platform.IExtensionProject;
import com._1c.g5.v8.dt.core.platform.IResourceLookup;
import com._1c.g5.v8.dt.core.platform.IV8Project;
import com._1c.g5.v8.dt.core.platform.IV8ProjectManager;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.md.MdUtil;
import com._1c.g5.v8.dt.metadata.mdclass.Configuration;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.ObjectBelonging;
import com._1c.g5.v8.dt.metadata.mdclass.Role;
import com._1c.g5.v8.dt.metadata.mdclass.ScriptVariant;
import com._1c.g5.v8.dt.rights.IRightInfosService;
import com._1c.g5.v8.dt.rights.model.ObjectRight;
import com._1c.g5.v8.dt.rights.model.ObjectRights;
import com._1c.g5.v8.dt.rights.model.Right;
import com._1c.g5.v8.dt.rights.model.RightValue;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com._1c.g5.v8.dt.rights.model.util.RightName;
import com._1c.g5.v8.dt.rights.model.util.RightsModelUtil;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelObjectAssociationContextCollector;
import com.e1c.g5.v8.dt.check.context.OnModelObjectRemovalContextCollector;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;

/**
 * Abstract check that role has some right for any object.
 * This check register for two different types of object.
 * <ul><li>
 * Registers for {@link RoleDescription} and check it only it has flag {@link RoleDescription#isSetForNewObjects()}
 * which means data model contains only non-default rights for an object.
 * </li><li>
 * Also it registers for {@link ObjectRight} that contain specific right for certain object.
 * </li></ul>
 * <br>
 * The check reacts on changes in intermediate object {@link ObjectRights} to reschedule check for top-object.
 * <br>
 * Implementors should call {@code super.configureCheck(builder);} when overriding
 * method {@link #configureCheck(CheckConfigurer)}
 *
 * @author Dmitriy Marmyshev
 *
 */
public abstract class RoleRightSetCheck
    extends BasicCheck<Object>
{

    protected static final String EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME = "excludeObjectNamePattern"; //$NON-NLS-1$

    private final IResourceLookup resourceLookup;

    private final IV8ProjectManager v8ProjectManager;

    private final IBmModelManager bmModelManager;

    private final IBmRightsIndexManager bmRightsIndexManager;

    private final IBmEmfIndexManager bmEmfIndexManager;

    private final IRightInfosService rightInfosService;

    /**
     * Creates new instance which helps to check that role has specified right for an object.
     *
     * @param v8ProjectManager the V8 project manager, cannot be {@code null}.
     * @param bmModelManager  the BM model manager, cannot be {@code null}.
     * @param bmRightsIndexProvider the BM rights index provider, cannot be {@code null}.
     * @param bmEmfIndexProvider the BM EMF index provider, cannot be {@code null}.
     * @param rightInfosService the right info service, cannot be {@code null}.
     */
    protected RoleRightSetCheck(IResourceLookup resourceLookup, IV8ProjectManager v8ProjectManager,
        IBmModelManager bmModelManager, IBmRightsIndexManager bmRightsIndexManager,
        IBmEmfIndexManager bmEmfIndexManager, IRightInfosService rightInfosService)
    {
        this.resourceLookup = resourceLookup;
        this.v8ProjectManager = v8ProjectManager;
        this.bmModelManager = bmModelManager;
        this.bmRightsIndexManager = bmRightsIndexManager;
        this.bmEmfIndexManager = bmEmfIndexManager;
        this.rightInfosService = rightInfosService;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MAJOR)
            .issueType(IssueType.SECURITY)
            .extension(new CombinedChangeExtension())
            .extension(new ExcludeRoleByPatternExtension(bmModelManager))
            .extension(new RoleNameChangeExtension())
            .topObject(ROLE_DESCRIPTION)
            .checkTop()
            .features(ROLE_DESCRIPTION__SET_FOR_NEW_OBJECTS, ROLE_DESCRIPTION__RIGHTS)
            .containment(OBJECT_RIGHT)
            .features(OBJECT_RIGHT__RIGHT, OBJECT_RIGHT__VALUE);

        if (needCheckObjectRight())
        {
            builder.parameter(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME, String.class, StringUtils.EMPTY,
                Messages.RoleRightSetCheck_Exclude_object_name_pattern);
        }
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {
        if (!(object instanceof EObject eObject))
        {
            return;
        }
        IV8Project v8Project = v8ProjectManager.getProject(eObject);
        if (object instanceof RoleDescription)
        {
            check((RoleDescription)eObject, resultAceptor, parameters, v8Project, monitor);
        }
        else if (object instanceof ObjectRight)
        {
            check((ObjectRight)eObject, resultAceptor, parameters, v8Project, monitor);
        }
    }

    /**
     * Gets the BM model manager service.
     *
     * @return the BM model manager, cannot return {@code null}.
     */
    protected IBmModelManager getBmModelManager()
    {
        return bmModelManager;
    }

    /**
     * Gets the object right name that need to check that exist in role rights.
     *
     * @return the right name constant, cannot return {@code null}.
     */
    protected abstract RightName getRightName();

    /**
     * Creates formated issue message for the right and the MD object.
     *
     * @param mdObject the MD object that has forbidden right, cannot be {@code null}.
     * @param v8Project the v8-project, cannot be {@code null}
     * @return the formatted issue message that right set for the object, cannot return {@code null}.
     */
    protected String getIssueMessage(MdObject mdObject, IV8Project v8Project)
    {
        String rightName = getRightName(v8Project);
        String mdObjectName = getMdObjectName(mdObject, v8Project);
        return MessageFormat.format(Messages.RoleRightSetCheck_Role_right__0__set_for__1, rightName, mdObjectName);
    }

    /**
     * Enables or disabled parameter for excluding objects
     *
     * @return true, if check needs {@link EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME} parameter
     */
    protected boolean needCheckObjectRight()
    {
        return true;
    }

    private void check(RoleDescription object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IV8Project v8Project, IProgressMonitor monitor)
    {
        if (!object.isSetForNewObjects())
        {
            return;
        }

        Collection<MdObject> mdObjects = getDefaultObjectsWithRight(object, monitor);

        IBmModel model = bmModelManager.getModel(object);
        Role role = RightsModelUtil.getOwner(object, model);

        List<ObjectRights> rights = object.getRights();
        for (MdObject mdObject : mdObjects)
        {
            if (monitor.isCanceled())
            {
                return;
            }
            if (needCheckObjectRight())
            {
                String excludeObjectNamePattern = parameters.getString(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME);
                if (excludeObjectNamePattern != null && !excludeObjectNamePattern.isBlank()
                    && mdObject.getName().matches(excludeObjectNamePattern))
                {
                    continue;
                }
            }
            ObjectRights objectRights = RightsModelUtil.filterObjectRightsByEObject(mdObject, rights);
            if (skipCheck(mdObject, v8Project, role, objectRights))
            {
                continue;
            }
            String message = getIssueMessage(mdObject, v8Project);
            if (objectRights == null)
            {
                resultAceptor.addIssue(message, ROLE_DESCRIPTION__RIGHTS);
            }
            else
            {
                // add marker as closer as possible to the problem place in the model
                resultAceptor.addIssue(message, objectRights, OBJECT_RIGHTS__OBJECT);
            }
        }
    }

    private void check(ObjectRight object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IV8Project v8Project, IProgressMonitor monitor)
    {
        Right right = object.getRight();

        if (right == null || !getRightName().getName().equals(right.getName()))
        {
            return;
        }

        Role role = null;

        RightValue rightValue = object.getValue();
        if (rightValue == null)
        {
            role = getRole(object);
            rightValue = RightsModelUtil.getDefaultRightValue(object, role);
        }

        if (!RightsModelUtil.getBooleanRightValue(rightValue) || monitor.isCanceled())
        {
            return;
        }

        ObjectRights rights = EcoreUtil2.getContainerOfType(object, ObjectRights.class);
        MdObject mdObject = rights.getObject() instanceof MdObject ? (MdObject)rights.getObject() : null;
        if (mdObject == null || monitor.isCanceled())
        {
            return;
        }

        if (needCheckObjectRight())
        {
            String excludeObjectNamePattern = parameters.getString(EXCLUDE_OBJECT_NAME_PATTERN_PARAMETER_NAME);
            if (excludeObjectNamePattern != null && !excludeObjectNamePattern.isBlank()
                && mdObject.getName().matches(excludeObjectNamePattern))
            {
                return;
            }
        }

        String message = getIssueMessage(mdObject, v8Project);
        resultAceptor.addIssue(message, OBJECT_RIGHT__RIGHT);
    }

    private Role getRole(ObjectRight objectRight)
    {
        IBmModel model = bmModelManager.getModel(objectRight);
        RoleDescription description = EcoreUtil2.getContainerOfType(objectRight, RoleDescription.class);
        return RightsModelUtil.getOwner(description, model);
    }

    private String getRightName(IV8Project project)
    {
        RightName right = getRightName();
        if (project != null && project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            return right.getNameRu();
        }

        return right.getName();
    }

    private String getMdObjectName(MdObject mdObject, IV8Project project)
    {
        if (mdObject == null || project == null)
        {
            return "Unknown"; //$NON-NLS-1$
        }

        if (project.getScriptVariant() == ScriptVariant.RUSSIAN)
        {
            QualifiedName fqn = MdUtil.getFullyQualifiedNameRu(mdObject);
            if (fqn != null)
            {
                return fqn.toString();
            }
        }

        QualifiedName fqn = MdUtil.getFullyQualifiedName(mdObject);
        if (fqn != null)
        {
            return fqn.toString();
        }

        return "Unknown"; //$NON-NLS-1$
    }

    private Collection<MdObject> getDefaultObjectsWithRight(RoleDescription description, IProgressMonitor monitor)
    {
        IProject project = resourceLookup.getProject(description);

        Set<Long> objectIdForRole = getRoleTopObjects(project, description, monitor);
        if (monitor.isCanceled())
        {
            return Collections.emptyList();
        }

        // return only unique objects
        Map<Long, MdObject> result = new HashMap<>();

        IBmEmfIndexProvider bmEmfIndexProvider = bmEmfIndexManager.getEmfIndexProvider(project);
        RightsModelUtil.SUPPORTED_RIGHT_ECLASSES.stream().forEach(eClass -> {

            //  filter class that can't have needed right
            if (!hasRight(eClass, description))
            {
                return;
            }

            for (Iterator<IEObjectDescription> iterator =
                bmEmfIndexProvider.getEObjectIndexByType(eClass).iterator(); iterator.hasNext();)
            {
                if (monitor.isCanceled())
                {
                    return;
                }
                IEObjectDescription objectDescription = iterator.next();
                EObject object = objectDescription.getEObjectOrProxy();
                if (object instanceof IBmObject && object instanceof MdObject)
                {
                    final long bmObjectId = ((IBmObject)object).bmGetId();

                    if (!objectIdForRole.contains(bmObjectId))
                    {
                        result.put(bmObjectId, (MdObject)object);
                    }
                }
            }
        });
        return result.values();
    }

    private Set<Long> getRoleTopObjects(IProject project, RoleDescription description, IProgressMonitor monitor)
    {
        IBmModel model = bmModelManager.getModel(description);

        Role role = RightsModelUtil.getOwner(description, model);

        IBmRightsIndexProvider bmRightsIndexProvider = bmRightsIndexManager.getRightsIndexProvider(project);
        IRightsDescription roleIndex = bmRightsIndexProvider.getRoleIndex(role);

        if (roleIndex == null)
        {
            return Collections.emptySet();
        }

        Set<Long> result = new HashSet<>();

        Set<Long> topObjects = roleIndex.getTopObjectsWithNonDefaultRights();
        String rightName = getRightName().getName();

        for (Long objectId : topObjects)
        {
            if (monitor.isCanceled())
            {
                return Collections.emptySet();
            }
            Map<String, Boolean> rights = roleIndex.getRights(objectId);
            if (rights != null && rights.containsKey(rightName))
            {
                result.add(objectId);
            }
        }
        return result;
    }

    private boolean hasRight(EClass eClass, EObject context)
    {
        Set<Right> rights = rightInfosService.getEClassRights(context, eClass);
        Set<String> rightNames = rights.stream().map(NamedElement::getName).collect(Collectors.toSet());
        return rightNames.contains(getRightName().getName());
    }

    private boolean skipCheck(MdObject mdObject, IV8Project v8Project, Role role, ObjectRights objectRights)
    {
        // Role always 'Allow all except... ' (role.isSetForNewObjects() == true)
        if (v8Project instanceof IExtensionProject extensionProject)
        {
            // Extension role cannot contain configuration rights.
            if (mdObject instanceof Configuration)
            {
                return true;
            }

            Configuration extensionConfiguration = extensionProject.getConfiguration();
            List<Role> defaultRoles = extensionConfiguration.getDefaultRoles();
            boolean isAdoptedObject = RightsModelUtil.isAdoptedMdObject(mdObject);

            // Default native extension role cannot contain adopted object rights.
            if (role.getObjectBelonging() == ObjectBelonging.NATIVE && defaultRoles.contains(role) && isAdoptedObject)
            {
                return true;
            }

            RightValue rightValue = null;
            if (objectRights != null)
            {
                rightValue = objectRights.getRights()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(objectRight -> getRightName().getName().equals(objectRight.getRight().getName()))
                    .findFirst()
                    .map(ObjectRight::getValue)
                    .orElse(null);
            }

            if (rightValue == null)
            {
                rightValue = RightsModelUtil.getDefaultRightValue(mdObject, role);
            }

            if (!RightsModelUtil.getBooleanRightValue(rightValue))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * This extension is combining all changes in containments of RoleDescription and schedule TOP object if needed.
     * Also it reacts on adding or removing TOP MD-objects and schedule all role descriptions with SetForNewObjects.
     */
    private final class CombinedChangeExtension
        implements IBasicCheckExtension
    {
        @Override
        public void configureContextCollector(ICheckDefinition definition)
        {
            // add reaction change of features "SET_FOR_NEW_OBJECTS" of existing object
            definition.addGenericModelFeatureChangeContextCollector((bmObject, feature, bmEvent, contextSession) -> {
                if (feature == ROLE_DESCRIPTION__SET_FOR_NEW_OBJECTS)
                {
                    // global flag changed - so fully re-check ROLE_DESCRIPTION for all checks
                    contextSession.addFullCheck(bmObject);
                }
            }, ROLE_DESCRIPTION);

            // if MD object changed then schedule full check for description or even for all checks
            definition.addGenericModelFeatureChangeContextCollector((bmObject, feature, bmEvent, contextSession) -> {
                if (feature == OBJECT_RIGHTS__OBJECT)
                {
                    IBmObject top = bmObject.bmGetTopObject();
                    if (top instanceof RoleDescription && ((RoleDescription)top).isSetForNewObjects())
                    {
                        contextSession.addModelCheck(top);
                    }
                    else if (top instanceof RoleDescription)
                    {
                        // schedule full check for top for all checks
                        contextSession.addFullCheck(top);
                    }
                }
            }, OBJECT_RIGHTS, ROLE_DESCRIPTION);

            // add reaction on creating new objects - that schedule role description
            OnModelObjectAssociationContextCollector topCollector = (bmObject, bmEvent, contextSession) -> {
                IBmObject top = bmObject.bmGetTopObject();
                if (top instanceof RoleDescription && ((RoleDescription)top).isSetForNewObjects())
                {
                    contextSession.addModelCheck(top);
                }
            };
            definition.addGenericModelAssociationContextCollector(topCollector, OBJECT_RIGHTS, ROLE_DESCRIPTION);
            definition.addGenericModelAssociationContextCollector(topCollector, OBJECT_RIGHT, OBJECT_RIGHTS);

            // For every new TOP MD object schedule all role descriptions with SetForNewObjects
            definition.addGenericModelAssociationContextCollector((bmObject, bmEvent, contextSession) -> {
                if (bmObject.bmIsTop() && bmObject.bmGetTransaction() != null)
                {
                    scheduleFullCheckForAllRoles(bmObject.eClass(), bmObject.bmGetTransaction(), contextSession);
                }
            }, MD_OBJECT);

            // if delete some objects then re-schedule full check for role description
            OnModelObjectRemovalContextCollector containmentRemoval =
                (removedObjectUri, removedObjectEClass, bmEvent, contextSession, transaction) -> {
                    URI topUri = removedObjectUri.trimFragment().appendFragment(BmUriUtil.TOP_OBJECT_PATH);
                    IBmObject top = transaction.getObjectByUri(topUri);
                    if (top instanceof RoleDescription && ((RoleDescription)top).isSetForNewObjects())
                    {
                        contextSession.addModelCheck(top);
                    }
                };
            definition.addModelRemovalContextCollector(containmentRemoval, OBJECT_RIGHTS);
            definition.addModelRemovalContextCollector(containmentRemoval, OBJECT_RIGHT);

            // if TOP object removed then schedule all role descriptions with SetForNewObjects
            definition.addModelRemovalContextCollector(
                (removedObjectUri, removedObjectEClass, bmEvent, contextSession, transaction) -> {
                    if (BmUriUtil.TOP_OBJECT_PATH.equals(removedObjectUri.fragment()))
                    {
                        scheduleFullCheckForAllRoles(removedObjectEClass, transaction, contextSession);
                    }
                }, MD_OBJECT);
        }

        private void scheduleFullCheckForAllRoles(EClass eClass, IBmTransaction transaction,
            CheckContextCollectingSession contextSession)
        {
            if (transaction != null && RightsModelUtil.SUPPORTED_RIGHT_ECLASSES.contains(eClass))
            {
                for (Iterator<IBmObject> iterator = transaction.getTopObjectIterator(ROLE_DESCRIPTION); iterator
                    .hasNext();)
                {
                    IBmObject object = iterator.next();
                    if (object instanceof RoleDescription && ((RoleDescription)object).isSetForNewObjects())
                    {
                        contextSession.addFullCheck(object);
                    }
                }
            }
        }
    }

}
