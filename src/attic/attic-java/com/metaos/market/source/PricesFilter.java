/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.market.source;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;

import com.metaos.market.*;

/**
 * Filter for non acceptable prices.
 */
public interface PricesFilter {
    /**
     * Tests if given set of prices for the symbol is valid.
     * @return true if set of prices is valid, false if should be ignored.
     */
    public boolean filter(final Calendar when, final String symbol,
            final Map<Field, Double> values);
}
