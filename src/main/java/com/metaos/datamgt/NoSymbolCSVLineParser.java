/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.datamgt;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.Logger;
import com.metaos.datamgt.*;
import com.metaos.datamgt.Field.*;


/**
 * Line processor for CSV files with information only for one symbol and
 * one date data per line.
 *
 * An <code>ErrorControl</code> object may be injected to detecte parsing
 * errors. Read documentation of <code>LineParser.ErrorControl</code>
 * interface to get more information on this topic.
 *
 * <span style="color:red">Not thread safe</span>
 */
public class NoSymbolCSVLineParser extends CSVLineParser {
    private final String symbol;

    /**
     * Creates a parser for CSV files with same symbol for all lines.
     * @param formatters list of formatters to translate string into numbers,
     *      strings or dates.
     * @param fieldNames name of fields to notify listeners, null for fields 
     * that will be ignored.
     * @param symbol string with the symbol whose data is contained in the 
     *      source.
     * @param dateIndex index of the previous list of formatters for the
     *      date of the line (should be null in the previous list of 
     *      fieldNames).
     */
    public NoSymbolCSVLineParser(final Format formatters[],
            final Field[] fieldNames, final String symbol, 
            final int dateIndexes[]) {
        super(appendSymbolFormatter(formatters),
              appendSymbolFieldName(fieldNames),
              formatters.length, dateIndexes);
        this.symbol = symbol;
    }



    public String toString() {
        return "NoSymbol" + super.toString();
    }


    //
    // Protected stuff -------------------------------
    //
    /**
     * Decoration method that adds symbol at the end of the line.
     */
    @Override protected void _parseLine(final String line) {
        super._parseLine(line + "," + symbol);
    }




    //
    // Private stuff -----------------------------------------
    //
    private static Format[] appendSymbolFormatter(final Format[] formatters) {
        final Format[] formatters2 = new Format[formatters.length+1];
        for(int i=0; i<formatters.length; i++) {
            formatters2[i] = formatters[i];
        }
        formatters2[formatters.length] = new MessageFormat("{0}");
        return formatters2;
    }

    private static Field[] appendSymbolFieldName(final Field[] fieldNames) {
        final Field[] fieldNames2 = new Field[fieldNames.length+1];
        for(int i=0; i<fieldNames.length; i++) {
            fieldNames2[i] = fieldNames[i];
        }
        fieldNames2[fieldNames.length] = new Field.EXTENDED(
                Qualifier.NONE, "RIC");
        return fieldNames2;
    }
}
