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
package com.e1c.v8codestyle.bsl.comment.check.itests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.IExtraInfoKeys;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.v8codestyle.bsl.check.itests.AbstractSingleModuleTestBase;
import com.e1c.v8codestyle.bsl.comment.check.RefLinkPartCheck;

/**
 * Tests for {@link RefLinkPartCheck} check.
 *
 * @author Dmitriy Marmyshev
 */
public class RefLinkPartCheckTest
    extends AbstractSingleModuleTestBase
{
    private static final String PROJECT_NAME = "EventHandlerBooleanParam";

    private static final String MODULE_FILE_NAME = "/src/Catalogs/Products/ManagerModule.bsl";

    public RefLinkPartCheckTest()
    {
        super(RefLinkPartCheck.class);
    }

    @Override
    protected boolean enableCleanUp()
    {
        return true;
    }

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected String getModuleFileName()
    {
        return MODULE_FILE_NAME;
    }

    /**
     * Test the documentation comment contains valid links
     *
     * @throws Exception the exception
     */
    @Test
    @Ignore("https://github.com/1C-Company/v8-code-style/issues/1376")
    public void testInvalidLinks() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "doc-comment-ref-link.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(2, markers.size());

        Set<String> lines = markers.stream()
            .map(marker -> marker.getExtraInfo().get(IExtraInfoKeys.TEXT_EXTRA_INFO_LINE_KEY))
            .collect(Collectors.toSet());

        assertEquals(Set.of("5", "7"), lines);
    }
}
