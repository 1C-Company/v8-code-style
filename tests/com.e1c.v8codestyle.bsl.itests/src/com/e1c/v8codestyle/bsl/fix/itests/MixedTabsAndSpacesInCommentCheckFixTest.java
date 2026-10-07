/*******************************************************************************
 * Copyright (C) 2026, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.bsl.fix.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.comment.check.MixedTabsAndSpacesInCommentCheck;

/**
 *  Tests for MixedTabsAndSpacesInCommentCheckFix fix.
 *
 *  @author Artem Samohvalov
 */
public class MixedTabsAndSpacesInCommentCheckFixTest
    extends AbstractQuickFixTest
{

    public MixedTabsAndSpacesInCommentCheckFixTest()
    {
        super(MixedTabsAndSpacesInCommentCheck.class);
    }

    @Test
    public void testApplyFix() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "fix-mixed-tabs-and-spaces-in-comment.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(6, markers.size());

        assertHasTabs(1);
        assertHasTabs(8);
        assertHasTabs(16);
        assertHasTabs(24);
        assertHasTabs(31);
        assertHasTabs(38);

        for (int i = 0; i < markers.size(); ++i)
        {
            Marker marker = markers.get(i);
            performFix(marker, Messages.MixedTabsAndSpacesInCommentCheckFix_Description);
        }
        PlatformUI.getWorkbench().saveAllEditors(false);

        assertMarkerGone();

        assertNoTabs(1);
        assertNoTabs(8);
        assertNoTabs(16);
        assertNoTabs(24);
        assertNoTabs(31);
        assertNoTabs(38);
    }

    private void assertHasTabs(int lineNumber)
    {
        String line = getStringByLineNumber(lineNumber);
        assertFalse(line.isEmpty());
        assertTrue(line.contains("\t"));
    }

    private void assertNoTabs(int lineNumber)
    {
        String line = getStringByLineNumber(lineNumber);
        assertFalse(line.isEmpty());
        assertFalse(line.contains("\t"));
    }

    private String getStringByLineNumber(int lineNumber)
    {
        ICompositeNode node = NodeModelUtils.getNode(getModule());
        String fullText = node.getText();

        String[] lines = fullText.split("\\r?\\n");
        if (lineNumber <= lines.length)
        {
            return lines[lineNumber - 1];
        }
        return "";
    }
}
