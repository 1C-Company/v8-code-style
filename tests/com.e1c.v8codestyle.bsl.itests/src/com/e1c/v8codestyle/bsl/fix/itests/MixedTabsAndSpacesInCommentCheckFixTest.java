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
        updateModule(FOLDER_RESOURCE + "mixed-tab-and-spaces-in-doc-comment.bsl");

        List<Integer> errorLines = List.of(3, 4, 11, 17, 27, 37, 38, 45, 46);

        List<Marker> markers = getModuleMarkers();
        assertEquals(6, markers.size());

        errorLines.forEach(val -> assertHasTabs(val, true));

        for (int i = 0; i < markers.size(); ++i)
        {
            Marker marker = markers.get(i);
            performFix(marker, Messages.MixedTabsAndSpacesInCommentCheckFix_Description);
        }
        PlatformUI.getWorkbench().saveAllEditors(false);

        assertMarkerGone();

        errorLines.forEach(val -> assertHasTabs(val, false));
    }

    private void assertHasTabs(int lineNumber, boolean has)
    {
        String line = getStringByLineNumber(lineNumber);
        assertFalse(line.isEmpty());
        assertEquals(has, line.contains("\t"));
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
