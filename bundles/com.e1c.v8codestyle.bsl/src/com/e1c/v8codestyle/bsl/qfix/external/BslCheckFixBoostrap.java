/**
 * Copyright (C) 2022, 1C
 */
package com.e1c.v8codestyle.bsl.qfix.external;

import com._1c.g5.wiring.IManagedService;
import com.e1c.g5.v8.dt.check.qfix.IFixRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Java-based registrar of BSL quick fix components
 *
 * @author Alexander Tretyakevich
 */
@Singleton
public class BslCheckFixBoostrap
    implements IManagedService
{
    @Inject
    private IFixRepository fixRepository;

    @Inject
    private SingleVariantXtextBslModuleFixContextFactory singleVariantXtextBslModuleFixContextFactory;

    @Override
    public void activate()
    {
        fixRepository.registerContextFactory(singleVariantXtextBslModuleFixContextFactory);
    }

    @Override
    public void deactivate()
    {
        // Not necessary at the moment
    }
}
