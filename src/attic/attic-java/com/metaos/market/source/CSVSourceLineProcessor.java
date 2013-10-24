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
 * Line processor for CSV files with only one date and symbol per line.
 *
 * <b>Not thread safe</b>
 */
public class CSVSourceLineProcessor implements SourceLineProcessor {
    private final Format[] formatters;
    private final Field[] fieldNames;
    private final ParsePosition[] parsePositions;
    private final int symbolIndex, dateIndexes[];
    private final Map<Field, Double> parsedValues;
    private final List<MarketListener> marketListeners;
    private final List<PricesListener> pricesListeners;
    private final List<PricesFilter> pricesFilters;

    private String parsedLine;
    private String parsedSymbol;
    private Calendar parsedCalendar;
    private boolean parsingResult;
    
    /**
     * Creates a parser for CSV files.
     * @param formatters list of formatters to translate string into numbers,
     *      strings or dates.
     * @param fieldNames name of fields to notify listeners, null for fields 
     * that will be ignored.
     * @param symbolIndex index of the previous list of formatters for the
     *      symbol name (should be null in the previous list of fieldNames).
     * @param dateIndex index of the previous list of formatters for the
     *      date of the line (should be null in the previous list of 
     *      fieldNames).
     */
    public CSVSourceLineProcessor(final Format formatters[],
            final Field[] fieldNames, final int symbolIndex, 
            final int dateIndexes[]) {
        assert (fieldNames.length == formatters.length);
        assert (symbolIndex < fieldNames.length);

        this.marketListeners = new ArrayList<MarketListener>();
        this.pricesListeners = new ArrayList<PricesListener>();
        this.pricesFilters = new ArrayList<PricesFilter>();
        this.parsedValues = new HashMap<Field, Double>();
        this.dateIndexes = dateIndexes;
        this.symbolIndex = symbolIndex;
        this.fieldNames = new Field[fieldNames.length];
        this.formatters = new Format[formatters.length];
        this.parsePositions = new ParsePosition[formatters.length];
        for(int i=0; i<parsePositions.length; i++) {
            this.formatters[i] = formatters[i];
            this.fieldNames[i] = fieldNames[i];
            this.parsePositions[i] = new ParsePosition(0);
        }
    }


    public boolean isValid(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedSymbol!=null && this.parsedCalendar!=null
                && this.parsingResult;
    }


    public void process(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
    }


    public void addPricesFilter(final PricesFilter filter) {
        this.pricesFilters.add(filter);
    }


    public void addPricesListener(final PricesListener listener) {
        this.pricesListeners.add(listener);
    }


    public void addMarketListener(final MarketListener listener) {
        this.marketListeners.add(listener);
    }


    public void concludeLineSet() {
        if(this.parsedSymbol==null || this.parsedCalendar==null) return;

        boolean pricesAreValid = true;
        for(final PricesFilter filter : this.pricesFilters) {
            if( ! filter.filter(this.parsedCalendar, this.parsedSymbol, 
                    this.parsedValues)) {
                pricesAreValid = false;
                break;
            }
        }

        if(pricesAreValid) {
            for(final MarketListener listener : this.marketListeners) {
                for(final Map.Entry<Field, Double> entry
                        : this.parsedValues.entrySet()) {
                    entry.getKey().notify(listener, this.parsedCalendar,
                            this.parsedSymbol, entry.getValue());
                }
            }

            final List<String> symbolArray = Arrays.asList(this.parsedSymbol);
            for(final PricesListener listener : this.pricesListeners) {
                listener.update(symbolArray, this.parsedCalendar);
            }
        }

        this.parsedSymbol = null;
        this.parsedCalendar = null;
    }


    public String getSymbol(final String line, final int index) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedSymbol;
    }


    public Calendar getDate(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedCalendar;
    }


    //
    // Private stuff ----------------------------------------------
    //

    /**
     * Modifies internal values trying to parse given line.
     */
    private void _parseLine(final String line) {
        this.parsedValues.clear();
        this.parsedLine = line;
        this.parsedCalendar = null;
        this.parsedSymbol = null;
        this.parsingResult = false;

        final String parts[] = line.split(",");
        boolean allLineProcessed = true;
        for(int i=0; i<parts.length; i++) {
            if(this.formatters[i] != null) {
                try {
                    this.parsePositions[i].setIndex(0);
                    final Object obj = this.formatters[i]
                            .parseObject(parts[i], this.parsePositions[i]);
                    if(obj instanceof Object[]) {
                        if(i==this.symbolIndex) {
                            this.parsedSymbol = (String) ((Object[])obj)[0];
                        }
                    } else if(obj instanceof Double) {
                        this.parsedValues.put(this.fieldNames[i], (Double) obj);
                    } else if(obj instanceof Date) {
                        for(int j=0; j<dateIndexes.length; j++) {
                            if(i==this.dateIndexes[j]) {
                                if(this.parsedCalendar==null) {
                                    this.parsedCalendar=Calendar.getInstance();
                                    this.parsedCalendar.setTimeInMillis(
                                        ((Date) obj).getTime());
                                } else {
                                    this.parsedCalendar.setTimeInMillis(
                                        this.parsedCalendar.getTimeInMillis()
                                        + ((Date) obj).getTime());
                                }
                                break;
                            }
                        }
                    } else {
                        // Unknown type
                        allLineProcessed = false;
                    }
                } catch(Exception e) {
                    allLineProcessed = false;
                }
            }
        }

        this.parsingResult = allLineProcessed;
    }
}
