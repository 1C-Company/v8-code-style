/*******************************************************************************
 * Copyright (C) 2023, 1C-Soft LLC and others.
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

package com.e1c.v8codestyle.form.check;

import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_FORM_ATTRIBUTE__EDIT;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.ABSTRACT_FORM_ATTRIBUTE__VIEW;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.FORM_COMMAND__USE;
import static com._1c.g5.v8.dt.form.model.FormPackage.Literals.VISIBLE__USER_VISIBLE;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ADJUSTABLE_BOOLEAN;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.ADJUSTABLE_BOOLEAN__COMMON;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.dt.form.model.FormAttribute;
import com._1c.g5.v8.dt.form.model.FormCommand;
import com._1c.g5.v8.dt.form.model.Visible;
import com._1c.g5.v8.dt.mcore.NamedElement;
import com._1c.g5.v8.dt.metadata.mdclass.AdjustableBoolean;
import com._1c.g5.v8.dt.metadata.mdclass.ForRoleType;
import com.e1c.g5.v8.dt.check.CheckComplexity;
import com.e1c.g5.v8.dt.check.ICheckParameters;
import com.e1c.g5.v8.dt.check.components.BasicCheck;
import com.e1c.g5.v8.dt.check.settings.IssueSeverity;
import com.e1c.g5.v8.dt.check.settings.IssueType;
import com.e1c.v8codestyle.check.StandardCheckExtension;
import com.e1c.v8codestyle.internal.form.CorePlugin;

/**
 * Check if Form Item (attribute, command, visible item) use role-based settings for visible, use edit.
 * 
 * @author Vadim Goncharov
 */
public class FormItemVisibleSettingsByRolesCheck
    extends BasicCheck
{

    private static final String CHECK_ID = "form-item-visible-settings-by-roles"; //$NON-NLS-1$

    private static final Set<EStructuralFeature> FEATURE_LIST =
        Set.of(VISIBLE__USER_VISIBLE, FORM_COMMAND__USE, ABSTRACT_FORM_ATTRIBUTE__EDIT, ABSTRACT_FORM_ATTRIBUTE__VIEW);

    public FormItemVisibleSettingsByRolesCheck()
    {
        super();
    }

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    @Override
    protected void configureCheck(CheckConfigurer builder)
    {
        builder.title(Messages.FormItemVisibleSettingsByRoles_title)
            .description(Messages.FormItemVisibleSettingsByRoles_description)
            .complexity(CheckComplexity.NORMAL)
            .severity(IssueSeverity.MINOR)
            .issueType(IssueType.WARNING)
            .extension(new StandardCheckExtension(737, getCheckId(), CorePlugin.PLUGIN_ID))
            .topObject(FORM)
            .containment(ADJUSTABLE_BOOLEAN)
            .features(ADJUSTABLE_BOOLEAN__COMMON);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAceptor, ICheckParameters parameters,
        IProgressMonitor monitor)
    {

        AdjustableBoolean adjBoolean = (AdjustableBoolean)object;
        EObject eContainer = adjBoolean.eContainer();
        EStructuralFeature eContainingFeature = adjBoolean.eContainingFeature();

        if (monitor.isCanceled() || !(adjBoolean.isCommon() && isCorrectContainer(eContainer)
            && isCorrectContainmentFeature(eContainingFeature)))
        {
            return;
        }

        EList<ForRoleType> forRoleList = adjBoolean.getFor();
        if (monitor.isCanceled() || forRoleList == null)
        {
            return;
        }

        if (!forRoleList.isEmpty())
        {

            String itemName = getItemName(eContainer);
            String propertyName = getPropertyName(eContainer, eContainingFeature);

            resultAceptor.addIssue(
                MessageFormat.format(Messages.FormItemVisibleSettingsByRoles_Message_template, propertyName, itemName),
                eContainer, ADJUSTABLE_BOOLEAN__COMMON);
        }

    }

    private boolean isCorrectContainer(EObject eContainer)
    {
        return eContainer instanceof Visible || eContainer instanceof FormCommand
            || eContainer instanceof FormAttribute;
    }

    private boolean isCorrectContainmentFeature(EStructuralFeature feature)
    {
        return FEATURE_LIST.contains(feature);
    }

    private String getItemName(EObject eContainer)
    {
        String itemName = null;

        if (eContainer instanceof Visible && eContainer instanceof NamedElement)
        {
            itemName = ((NamedElement)eContainer).getName();
        }
        else if (eContainer instanceof FormCommand)
        {

            itemName = ((FormCommand)eContainer).getName();
        }
        else if (eContainer instanceof FormAttribute)
        {
            itemName = ((FormAttribute)eContainer).getName();
        }

        return itemName;
    }

    private String getPropertyName(EObject eContainer, EStructuralFeature eContainingFeature)
    {
        String propertyName = null;
        if (eContainer instanceof Visible
            || eContainer instanceof FormAttribute && eContainingFeature.equals(ABSTRACT_FORM_ATTRIBUTE__VIEW))
        {
            propertyName = Messages.FormItemVisibleSettingsByRoles_Property_name_visible;
        }
        else if (eContainer instanceof FormAttribute && eContainingFeature.equals(ABSTRACT_FORM_ATTRIBUTE__EDIT))
        {
            propertyName = Messages.FormItemVisibleSettingsByRoles_Property_name_edit;
        }
        else if (eContainer instanceof FormCommand)
        {
            propertyName = Messages.FormItemVisibleSettingsByRoles_Property_name_use;
        }
        return propertyName;
    }

}
