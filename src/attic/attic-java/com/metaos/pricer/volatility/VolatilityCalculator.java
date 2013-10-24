/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.pricer.volatility;

import com.metaos.Instrument;

/**
 * Volatility calculi interface.
 */
public interface VolatilityCalculator {
    /**
     * Gets volatility for given values.
     */
    public double calculate(final Instrument instrument);

    /**
     * Gets the list of values where calculi have been made.
     */
    public double[] getValues(final Instrument instrument);
}
