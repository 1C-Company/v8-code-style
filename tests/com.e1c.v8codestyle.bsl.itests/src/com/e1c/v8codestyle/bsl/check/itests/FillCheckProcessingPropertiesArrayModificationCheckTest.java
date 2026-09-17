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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com._1c.g5.v8.dt.validation.marker.Marker;
import com._1c.g5.v8.dt.validation.marker.StandardExtraInfo;
import com.e1c.v8codestyle.bsl.check.FillCheckProcessingPropertiesArrayModificationCheck;

/**
 *  Tests for {@link FillCheckProcessingPropertiesArrayModificationCheck} check.
 *
 *  @author Artem Samohvalov
 */
public class FillCheckProcessingPropertiesArrayModificationCheckTest
    extends AbstractSingleModuleTestBase
{
    public FillCheckProcessingPropertiesArrayModificationCheckTest()
    {
        super(FillCheckProcessingPropertiesArrayModificationCheck.class);
    }

    /**
     * Test no error
     * Test delete element from reference in УдалитьНепроверяемыеРеквизитыИзМассива (DeleteUncheckedAttributesFromArray) method 
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElementInDeleteMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element-in-delete-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error
     * Test delete element from global reference in УдалитьНепроверяемыеРеквизитыИзМассива (DeleteUncheckedAttributesFromArray) method 
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElementFromGlobalInDeleteMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element-from-global-in-delete-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error
     * Test add element from no reference in AddMethod
     * MyVar = CheckedAttributes; MyVar = New Array; MyVar.Add(""); // no error
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToNoReferenceInAddMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-to-no-reference-in-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error
     * Test add element from no reference in AddMethod
     * MyVar = CheckedAttributes; MyVar2 = New Array; MyVar = MyVar2; MyVar.Add(""); // no error
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToNoReference() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-to-no-reference.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test no error
     * Test add element from global no reference
     * MyVar = CheckedAttributes; MyVar2 = New Array; MyVar = MyVar2; MyVar.Add(""); // no error
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToNoReferenceGlobal() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-to-no-reference-global.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(0, markers.size());
    }

    /**
     * Test delete element from reference in УдалитьНепроверяемыеРеквизитыИзМассива (DeleteUncheckedAttributesFromArray) sub method 
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElementInDeleteSubMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element-in-delete-sub-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(14), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to CheckAttributes
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test delete element from CheckAttributes
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to CheckAttributes reference
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToRef() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-ref.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to method return reference
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToMethodReturnRef() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-by-method-return.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test delete element from reference in another method
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElementInAnotherMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element-in-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(10), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element from reference in УдалитьНепроверяемыеРеквизитыИзМассива (DeleteUncheckedAttributesFromArray) method 
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementInDeleteMethod() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-in-delete-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(10), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to global reference variable
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToGlobalRef() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-global-ref-in-method.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(15), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test delete element from method return global reference
     *
     * @throws Exception the exception
     */
    @Test
    public void testDeleteElementByMethodGlobalRefReturn() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-delete-element-by-method-global-ref-return.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(8), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to local variable from method return reference
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToLocalVarFromMethodReturnRef() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-to-local-var-from-method-return.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(6), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }

    /**
     * Test add element to return reference in method with nested
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddElementToMethodReturnRefNested() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-add-element-to-method-return-ref-nested.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(7), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
    
    /**
     * Test insert element
     *
     * @throws Exception the exception
     */
    @Test
    public void testInsertElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-insert-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
    
    /**
     * Test set element
     *
     * @throws Exception the exception
     */
    @Test
    public void testSetElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-set-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
    
    /**
     * Test clear element
     *
     * @throws Exception the exception
     */
    @Test
    public void testClearElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-clear-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
    
    /**
     * Test [] element
     *
     * @throws Exception the exception
     */
    @Test
    public void testAddWithIndexOperatiorElement() throws Exception
    {
        updateModule(FOLDER_RESOURCE + "check-attributes-insert-with-index-operator-to-element.bsl");

        List<Marker> markers = getModuleMarkers();
        assertEquals(1, markers.size());
        Marker marker = markers.get(0);

        assertEquals(Integer.valueOf(5), marker.getExtraInfo().get(StandardExtraInfo.TEXT_LINE));
    }
}
