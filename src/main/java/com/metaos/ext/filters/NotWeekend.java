/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.ext.filters;

import com.metaos.datamgt.Field;
import com.metaos.datamgt.Filter;
import java.util.Calendar;
import java.util.Map;

/**
 * Filter lines avoiding weekends.
 */
public class NotWeekend implements Filter {
    /**
     * Creates the filter to avoid weekends.
     */
    public NotWeekend() {
    }


    /**
     * Tests if given set of prices for the symbol is not in weekend.
     * @return true if set of prices is valid, false if should be ignored.
     */
    public boolean filter(final Calendar when, final String symbol,
            final Map<Field, Double> values) {
        return ( when.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY
                 && when.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY );
    }


    public String toString() {
        return "NotWeekEnd";
    }
}
