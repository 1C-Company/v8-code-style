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

import com.e1c.v8codestyle.bsl.check.NotifyCallInAfterWriteEventCheck;

/**
 * Test for {@link NotifyCallInAfterWriteEventCheck}
 * 
 * @author Artem Samohvalov
 */
public class NotifyCallInAfterWriteEventCheckTest
    extends AbstractInlineSingleModuleTestBase
{
    private static final String PROJECT_NAME = "ObjectChangeNotifyCallCheckTest";
    private static final String MODULE_FILE_NAME = "/src/Catalogs/TestCatalog/Forms/ItemForm/Module.bsl";

    public NotifyCallInAfterWriteEventCheckTest()
    {
        super(NotifyCallInAfterWriteEventCheck.class);
    }

    /**
     * test error
     * 
     * @throws Exception
     */
    @Test
    public void testNoNotifyCall() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite(WriteParameters) // error
            	SomeMethod();
            EndProcedure

            Procedure SomeMethod()
            	SomeMethod2();
            EndProcedure

            Procedure SomeMethod2() // no Notify call
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkerEqual(2);
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNotifyCall() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite(WriteParameters)
            	Notify("");
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNotifyCallNested() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite(WriteParameters)
            	If 1 = 1 Then
            		#Region Abcde
            			Notify("");
                    #EndRegion
            	EndIf
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNotifyCallInSubMethod() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite(WriteParameters)
            	SomeMethod();
            EndProcedure

            Procedure SomeMethod()
            	SomeFunction();
            EndProcedure

            Function SomeFunction()
            	Notify("");
            	Return 0;
            EndFunction
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
    }

    /**
     * test no error
     * 
     * @throws Exception
     */
    @Test
    public void testNoNotifyCallInExternalSubMethod() throws Exception
    {
        String bsl = """
            &AtClient
            Procedure AfterWrite(WriteParameters)
            	CommonModule.MethodInAnotherModule(); // method that can call the notify, no error here
            EndProcedure
            """;

        loadModuleCode(bsl);
        assertMarkersEmpty();
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
