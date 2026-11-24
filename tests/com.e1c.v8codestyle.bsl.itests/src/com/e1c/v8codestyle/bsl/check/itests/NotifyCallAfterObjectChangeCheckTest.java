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
package com.e1c.v8codestyle.bsl.check.itests;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.e1c.v8codestyle.bsl.check.NotifyCallAfterObjectChangeCheck;

/**
 * The test for {@link NotifyCallAfterObjectChangeCheck}
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallAfterObjectChangeCheckTest
    extends AbstractInlineSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ObjectChangeNotifyCallCheckTest";
    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/Forms/ItemForm/Module.bsl";

    public NotifyCallAfterObjectChangeCheckTest()
    {
        super(NotifyCallAfterObjectChangeCheck.class);
    }

    /**
     * Test error
     * Test no notify call and object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallAndObjectWrite() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure ClientProcedure() // error
            	ServerProcedure();
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
            	Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * Test error
     * Test no notify call and object delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallAndObjectDelete() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure ClientProcedure() // error
            	ServerProcedure();
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
            	Delete();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * Test error.
     * Test notify call that is not the last statement.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallIsNotAtTheEnd() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite() // error
                ServerProcedure();
                NotifyChanged("RecordChanged");
                Message("Done");
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
                Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * Test no error
     * Test notify call and object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallAndObjectWrite() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure ClientProcedure()
            	ServerProcedure();
            	NotifyChanged("SomeData");
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
            	Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * Test no error.
     * Test server method without object write or delete.
     *
     * @throws Exception the exception
     */
    @Test
    public void testServerMethodDoesNotChangeObject() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite()
                ServerProcedure();
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
                Message("Test");
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * Test no error.
     * Test notify call with set deletion mark
     *
     * @throws Exception the exception
     */
    @Test
    public void testNotifyCallWithSetDeletionMark() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure ClientProcedure()
            	ServerProcedure();
            	NotifyChanged("EventName", "SomeData", ThisObject);
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
            	SetDeletionMark(True);
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * Test no error.
     * Test method without server execution directive.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMethodWithoutServerDirective() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite()
                ServerProcedure();
            EndProcedure

            &AtClient
            Procedure ServerProcedure()
                Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * Test error.
     * Test client and server method with object write.
     *
     * @throws Exception the exception
     */
    @Test
    public void testClientAtServerMethodAndObjectWrite() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite() // error
                ServerProcedure();
            EndProcedure

            &AtClientAtServerNoContext
            Procedure ServerProcedure()
                Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * Test error.
     * Test several server method calls when one of them changes the object.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSeveralServerMethodsWithObjectChange() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite() // error
                ServerMethod1();
                ServerMethod2();
            EndProcedure

            &AtServer
            Procedure ServerMethod1()
                Message("Test");
            EndProcedure

            &AtServer
            Procedure ServerMethod2()
                Write();
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * Test error.
     * Test no notify call with set deletion mark
     *
     * @throws Exception the exception
     */
    @Test
    public void testNoNotifyCallWithSetDeletionMark() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure ClientProcedure() // error
            	ServerProcedure();
            EndProcedure

            &AtServer
            Procedure ServerProcedure()
            	SetDeletionMark(True);
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
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

    @Override
    protected String getModuleId()
    {
        return Path.ROOT.append(getTestConfigurationName()).append(getModuleFileName()).toString();
    }
}
