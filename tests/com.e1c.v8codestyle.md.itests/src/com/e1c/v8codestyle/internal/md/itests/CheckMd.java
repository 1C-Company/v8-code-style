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
package com.e1c.v8codestyle.internal.md.itests;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.bm.core.IBmTransaction;
import com._1c.g5.v8.bm.integration.AbstractBmTask;
import com._1c.g5.v8.bm.integration.IBmModel;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.metadata.mdclass.ReturnValuesReuse;
import com.e1c.g5.v8.dt.testing.check.CheckTestBase;

/**
 * @author Aleksandr Kapralov
 *
 */
public abstract class CheckMd
    extends CheckTestBase
{

    protected void updateCommonModule(IDtProject dtProject, String fqn, Map<EStructuralFeature, Boolean> types,
        ReturnValuesReuse returnValueReuse, String newFqn)
    {
        IBmModel model = bmModelManager.getModel(dtProject);
        model.execute(new AbstractBmTask<Void>("change type")
        {
            @Override
            public Void execute(IBmTransaction transaction, IProgressMonitor monitor)
            {
                IBmObject object = transaction.getTopObjectByFqn(fqn);

                for (Entry<EStructuralFeature, Boolean> entry : types.entrySet())
                {
                    object.eSet(entry.getKey(), entry.getValue());
                }

                if (!(object instanceof CommonModule))
                {
                    return null;
                }

                CommonModule module = (CommonModule)object;

                if (returnValueReuse != null)
                {
                    module.setReturnValuesReuse(returnValueReuse);
                }

                if (newFqn != null)
                {
                    String[] fqnArray = newFqn.split("[.]");
                    if (fqnArray.length == 2)
                    {
                        module.setName(fqnArray[1]);
                        transaction.updateTopObjectFqn(object, newFqn);
                    }
                }

                return null;
            }
        });
        waitForDD(dtProject);
    }

}
