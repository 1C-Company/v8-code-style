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
 *     Manaev Konstanitn - issue #855
 *******************************************************************************/

package com.e1c.v8codestyle.form.check;

import org.eclipse.emf.common.notify.Notification;
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
 * The extension that registers {@link Form} for model check if an event handler or an item contained in the form have been changed
 *
 * @author Manaev Konstantin
 */
public class EventHandlerChangeExtension
    implements IBasicCheckExtension
{
    boolean handlerWasChange(EStructuralFeature feature, BmSubEvent bmEvent)
    {
        return feature == FormPackage.Literals.EVENT_HANDLER__NAME
            || feature == FormPackage.Literals.EVENT_HANDLER_CONTAINER__HANDLERS;
    }

    boolean itemWasRemove(EStructuralFeature feature, BmSubEvent bmEvent)
    {
        if (feature == FormPackage.Literals.FORM_ITEM_CONTAINER__ITEMS)
        {
            for (Notification notification : ((BmChangeEvent)bmEvent).getNotifications(feature))
            {
                if (notification.getEventType() == Notification.REMOVE)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void configureContextCollector(final ICheckDefinition definition)
    {
        OnModelFeatureChangeContextCollector collector = (IBmObject bmObject, EStructuralFeature feature,
            BmSubEvent bmEvent, CheckContextCollectingSession contextSession) -> {
            if (handlerWasChange(feature, bmEvent) || itemWasRemove(feature, bmEvent))
            {
                IBmObject top = bmObject.bmIsTop() ? bmObject : bmObject.bmGetTopObject();
                if (top instanceof Form)
                {
                    contextSession.addModelCheck(top);
                }
            }
        };
        definition.addModelFeatureChangeContextCollector(collector, FormPackage.Literals.EVENT_HANDLER_CONTAINER);
        definition.addModelFeatureChangeContextCollector(collector, FormPackage.Literals.EVENT_HANDLER);
    }
}
