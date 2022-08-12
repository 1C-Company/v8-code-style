/*******************************************************************************
 * Copyright (C) 2022, 1C-Soft LLC and others.
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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.event.BmChangeEvent;
import com._1c.g5.v8.bm.core.event.BmSubEvent;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormPackage;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;

/**
 * The extension that registers {@link Form} for model check if an command handler
 * or an item contained in the form have been changed
 *
 * @author Artem Iliukhin
 */
public class CommandHandlerChangeExtension
    implements IBasicCheckExtension
{
    @Override
    public void configureContextCollector(final ICheckDefinition definition)
    {
        OnModelFeatureChangeContextCollector collector = (IBmObject bmObject, EStructuralFeature feature,
            BmSubEvent bmEvent, CheckContextCollectingSession contextSession) -> {
            if (isActionChanged(feature) || isCommandChanged(feature, bmEvent))
            {
                IBmObject top = bmObject.bmIsTop() ? bmObject : bmObject.bmGetTopObject();
                if (top instanceof Form)
                {
                    contextSession.addModelCheck(top);
                }
            }
        };

        definition.addModelFeatureChangeContextCollector(collector, FormPackage.Literals.FORM_COMMAND);
        definition.addModelFeatureChangeContextCollector(collector, FormPackage.Literals.FORM_ITEM_CONTAINER);

        Set<EClass> containdedObjects = new HashSet<>();
        containdedObjects.add(FormPackage.Literals.FORM_COMMAND);
        containdedObjects.add(FormPackage.Literals.FORM_ITEM_CONTAINER);
        definition.addCheckedModelObjects(FormPackage.Literals.FORM, true, containdedObjects);
    }

    private boolean isActionChanged(EStructuralFeature feature)
    {
        return feature == FormPackage.Literals.FORM_COMMAND__ACTION;
    }

    private boolean isCommandChanged(EStructuralFeature feature, BmSubEvent bmEvent)
    {
        if (feature == FormPackage.Literals.FORM__FORM_COMMANDS)
        {
            for (Notification notification : ((BmChangeEvent)bmEvent).getNotifications(feature))
            {
                if (notification.getEventType() == Notification.ADD
                    || notification.getEventType() == Notification.REMOVE)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
