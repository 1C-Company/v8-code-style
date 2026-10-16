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
package com.e1c.v8codestyle.form.fix.itests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.form.model.Form;
import com._1c.g5.v8.dt.form.model.FormItem;
import com._1c.g5.v8.dt.form.model.Table;
import com._1c.g5.v8.dt.form.model.TableInitialTreeView;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.form.fix.HierarchicalListInitialTreeDisplayFix;

/**
 * Tests for {@link HierarchicalListInitialTreeDisplayFix} fix.
 *
 * @author Artem Samohvalov
 */
public class HierarchicalListInitialTreeDisplayFixTest
    extends FormFixTestBase
{
    private static final String CHECK_ID = "hierarchical-list-initial-tree-display";
    private static final String PROJECT_NAME = "HierarchicalListInitialTreeDisplay";
    private static final String FQN_FORM = "Catalog.Products.Form.ListForm.Form";

    public HierarchicalListInitialTreeDisplayFixTest()
    {
        super(Messages.HierarchicalListInitialTreeDisplayFix_description);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish(PROJECT_NAME);
        assertNotNull(dtProject);

        IBmObject object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        Form form = (Form)object;

        var markers = getMarkers(form, dtProject);
        assertFalse(markers.isEmpty());

        // make fix
        applyFix(markers.get(0), dtProject);

        waitForDD(dtProject);

        object = getTopObjectByFqn(FQN_FORM, dtProject);
        assertTrue(object instanceof Form);
        form = (Form)object;

        markers = getMarkers(form, dtProject);
        assertTrue(markers.isEmpty());

        assertFixCompleted(form);
    }

    private List<Marker> getMarkers(Form form, IDtProject project)
    {
        List<Marker> markers = new ArrayList<>();
        for (FormItem item : form.getItems())
        {
            if (item instanceof Table table)
            {
                Marker marker = getFirstMarker(CHECK_ID, table, project);

                if (marker != null)
                {
                    markers.add(marker);
                }
            }
        }
        return markers;
    }

    private void assertFixCompleted(Form form)
    {
        TreeIterator<EObject> iterator = EcoreUtil.getAllContents(form, true);

        while (iterator.hasNext())
        {
            EObject element = iterator.next();

            if (element instanceof Table table)
            {
                assertTrue(table.getInitialTreeView() == TableInitialTreeView.EXPAND_TOP_LEVEL);
            }
        }
    }
}
