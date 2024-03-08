/*******************************************************************************
 * Copyright (C) 2024, 1C-Soft LLC and others.
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
package com.e1c.v8codestyle.md.configuration.check.itests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogTabularSection;
import com._1c.g5.v8.dt.metadata.mdclass.TabularSectionAttribute;
import com._1c.g5.v8.dt.validation.marker.Marker;
import com.e1c.g5.v8.dt.testing.check.SingleProjectReadOnlyCheckTestBase;
import com.e1c.v8codestyle.md.check.MdObjectSynonymCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationBriefInformationCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationCopyrightCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationDetailedInformationCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationInformationAddressCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationNameCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationSynonymCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationUpdateCatalogAddressCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationUseManagedFormsCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationVendorCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationVendorInformationAddressCheck;
import com.e1c.v8codestyle.md.configuration.check.ConfigurationVersionCheck;

/**
 * Multiple tests for Configuration root for correct behaviour.
 *
 * @author Dmitriy Marmyshev
 */
public class ConfigRootCorrectTest
    extends SingleProjectReadOnlyCheckTestBase
{
    private static final String CONFIGURATION = "Configuration";

    private static final String PROJECT_NAME = "ConfigRootCorrect";

    @Override
    protected String getTestConfigurationName()
    {
        return PROJECT_NAME;
    }

    /**
     * Test correct behaviour for {@link ConfigurationBriefInformationCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBriefInformation() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-brief-information", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationCopyrightCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCopyright() throws Exception
    {

        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-copyright", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationDetailedInformationCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testDetailedInformation() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-detailed-information", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationInformationAddressCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testInformationAddress() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-information-address", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationNameCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testName() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-name", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationSynonymCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testSynonym() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-synonym", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link MdObjectSynonymCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testMdoSynonym() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("mdo-synonym", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link MdObjectSynonymCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testCatalogMdoSynonym() throws Exception
    {
        String checkId = "mdo-synonym";

        assertNotNull(getProject());

        Catalog object = (Catalog)getTopObjectByFqn("Catalog.Products", getProject());
        Marker marker = getFirstMarker(checkId, object, getProject());
        assertNull(marker);

        CatalogAttribute attribute = object.getAttributes().get(0);
        marker = getFirstMarker(checkId, attribute, getProject());
        assertNull(marker);

        CatalogTabularSection ts = object.getTabularSections().get(0);
        marker = getFirstMarker(checkId, ts, getProject());
        assertNull(marker);

        TabularSectionAttribute tsAttribute = ts.getAttributes().get(0);
        marker = getFirstMarker(checkId, tsAttribute, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationUpdateCatalogAddressCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUpdateCatalogAddress() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-update-catalog-address", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationUseManagedFormsCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testUseManagedFormInIrdinaryApplication() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-use-managed-form-in-ordinary-application", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationVendorInformationAddressCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVendorInformationAddress() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-vendor-information-address", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationVendorCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVendor() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-vendor", id, getProject());
        assertNull(marker);
    }

    /**
     * Test correct behaviour for {@link ConfigurationVersionCheck}.
     *
     * @throws Exception the exception
     */
    @Test
    public void testVersion() throws Exception
    {
        assertNotNull(getProject());

        long id = getTopObjectIdByFqn(CONFIGURATION, getProject());
        Marker marker = getFirstMarker("configuration-version", id, getProject());
        assertNull(marker);
    }

}
