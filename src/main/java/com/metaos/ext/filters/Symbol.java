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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Filter lines containing information on a given symbol or list of symbols.
 */
public class Symbol implements Filter {
    private final Set<String> symbols;
    
    /**
     * Creates the filtor for given symbol.
     */
    public Symbol(final String symbol) {
        this(new String[] { symbol });
    }


    /**
     * Creates the filtor for given list of symbol.
     */
    public Symbol(final String[] symbols) {
        this.symbols = new HashSet<String>();
        for(int i=0; i<symbols.length; i++) this.symbols.add(symbols[i]);
    }


    /**
     * Tests if given set of prices for the symbol is valid.
     * @return true if set of prices is valid, false if should be ignored.
     */
    public boolean filter(final Calendar when, final String symbol,
            final Map<Field, Double> values) {
        return this.symbols.contains(symbol);
    }


    public String toString() {
        return "Symbol " + this.symbols;
    }
}
