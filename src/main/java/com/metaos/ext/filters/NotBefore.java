/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.ext.filters;

import com.metaos.datamgt.Field;
import com.metaos.datamgt.Filter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Filter lines not before a given date.
 */
public class NotBefore implements Filter {
    private final Calendar dateLimit;
    private static final SimpleDateFormat formatter 
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Creates the filter to avoid lines with date before given date.
     */
    public NotBefore(final Calendar dateLimit) {
        this.dateLimit = (Calendar) dateLimit.clone();
    }



    /**
     * Tests if given set of prices for the symbol is valid.
     * @return true if set of prices is valid, false if should be ignored.
     */
    public boolean filter(final Calendar when, final String symbol,
            final Map<Field, Double> values) {
        return ! when.before(this.dateLimit);
    }


    public String toString() {
        return "NotBefore " + formatter.format(this.dateLimit.getTime());
    }
}
