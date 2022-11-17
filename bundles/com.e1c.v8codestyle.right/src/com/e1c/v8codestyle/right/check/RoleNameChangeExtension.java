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
 *     Aleksandr Kapralov - initial API and implementation
 *******************************************************************************/
package com.e1c.v8codestyle.right.check;

import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.event.BmChangeEvent;
import com._1c.g5.v8.bm.core.event.BmSubEvent;
import com._1c.g5.v8.dt.rights.model.RightsPackage;
import com._1c.g5.v8.dt.rights.model.RoleDescription;
import com.e1c.g5.v8.dt.check.ICheckDefinition;
import com.e1c.g5.v8.dt.check.components.IBasicCheckExtension;
import com.e1c.g5.v8.dt.check.context.CheckContextCollectingSession;
import com.e1c.g5.v8.dt.check.context.OnModelFeatureChangeContextCollector;

/**
 * Check extension: run check for role if role right was changed
 *
 * @author Aleksandr Kapralov
 *
 */
public class RoleNameChangeExtension
    implements IBasicCheckExtension
{

    @Override
    public void configureContextCollector(final ICheckDefinition definition)
    {
        OnModelFeatureChangeContextCollector collector = (IBmObject bmObject, EStructuralFeature feature,
            BmSubEvent bmEvent, CheckContextCollectingSession contextSession) -> {

            if (bmObject instanceof RoleDescription && bmEvent instanceof BmChangeEvent
                && ((BmChangeEvent)bmEvent).isFqnChanged())
            {
                contextSession.addFullCheck(bmObject);
            }
        };
        definition.addGenericModelFeatureChangeContextCollector(collector, RightsPackage.Literals.ROLE_DESCRIPTION);
    }

}
