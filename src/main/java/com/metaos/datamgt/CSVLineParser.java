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

/**
 * Line processor for CSV files with only one date and symbol per line.
 *
 * An <code>ErrorControl</code> object may be injected to detecte parsing
 * errors. Read documentation of <code>LineParser.ErrorControl</code>
 * interface to get more information on this topic.
 *
 * <span style="color:red">Not thread safe</span>
 */
public class CSVLineParser implements LineParser {
    private final Format[] formatters;
    private final Field[] fieldNames;
    private final ParsePosition[] parsePositions;
    private final int symbolIndex, dateIndexes[];
    private final List<CacheWriteable> cacheListeners;
    private final List<Filter> pricesFilters;

    private String parsedLine;
    private ParseResult parsedData;
    private boolean parsingResult;
    private boolean isValid;

    private LineParser.ErrorControl errorControl = LineParser.nullErrorControl;

    
    /**
     * Creates a parser for CSV files.
     * @param formatters list of formatters to translate string into numbers,
     *      strings or dates.
     * @param fieldNames name of fields to notify listeners, null for fields 
     * that will be ignored.
     * @param symbolIndex index of the previous list of formatters for the
     *      symbol name (should be null in the previous list of fieldNames).
     * @param dateIndexes index of the previous list of formatters for the
     *      date of the line (should be null in the previous list of 
     *      fieldNames).
     */
    public CSVLineParser(final Format formatters[],
            final Field[] fieldNames, final int symbolIndex, 
            final int dateIndexes[]) {
        assert (fieldNames.length == formatters.length);
        assert (symbolIndex < fieldNames.length);

        this.cacheListeners = new ArrayList<CacheWriteable>();
        this.pricesFilters = new ArrayList<Filter>();
        this.dateIndexes = dateIndexes;
        this.symbolIndex = symbolIndex;
        this.parsedLine = "";
        this.fieldNames = new Field[fieldNames.length];
        this.formatters = new Format[formatters.length];
        this.parsePositions = new ParsePosition[formatters.length];
        for(int i=0; i<parsePositions.length; i++) {
            this.formatters[i] = formatters[i];
            this.fieldNames[i] = fieldNames[i];
            this.parsePositions[i] = new ParsePosition(0);
        }
        this.parsedData = new ParseResult();
    }


    public boolean isValid(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
       }
       return this.isValid;
    }


    public ParseResult parse(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }

        final String symbol = this.parsedData.getSymbol(0);
        if(symbol!=null) {
            for(final CacheWriteable listener : this.cacheListeners) {
                for(final Map.Entry<Field, Double> entry
                        : this.parsedData.values(symbol).entrySet()) {
                    entry.getKey().notify(listener, 
                            this.parsedData.getLocalTimestamp(symbol),
                            symbol, entry.getValue());
                }
            }
        }
        return (ParseResult) this.parsedData.clone();
    }


    public LineParser addFilter(final Filter filter) {
        this.pricesFilters.add(filter);
        return this;
    }


    public LineParser addCacheWriteable(final CacheWriteable listener) {
        this.cacheListeners.add(listener);
        return this;
    }


    public String getSymbol(final String line, final int index) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedData.getSymbol(index);
    }


    public Calendar getLocalTimestamp(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedData.getLocalTimestamp(0);
    }

    public Calendar getUTCTimestamp(final String line) {
        if( ! line.equals(this.parsedLine) ) {
            _parseLine(line);
        }
        return this.parsedData.getUTCTimestampCopy();
    }

    public void reset() {
        this.pricesFilters.clear();
        this.cacheListeners.clear();
        this.parsedData = new ParseResult();
        this.parsedLine = "";
        this.isValid = false;
        this.parsingResult = false;
    }


    /**
     * Sets the object to control errors when parsing lines.
     */
    public void setErrorControl(final ErrorControl errorControl) {
        if(errorControl!=null) this.errorControl = errorControl;
    }

    /**
     * Gets the object to control errors when parsing lines.
     */
    public ErrorControl getErrorControl() {
        return this.errorControl;
    }


    public String toString() {
        final StringBuffer tmp = new StringBuffer();
        tmp.append("CSVLineParser[filters=");
        for(final Filter f : this.pricesFilters) {
            tmp.append(f.toString()).append(" ");
        }
        tmp.append("]");
        return tmp.toString();
    }

    //
    // Private stuff ----------------------------------------------
    //

    /**
     * Modifies internal values trying to parse given line.
     */
    protected void _parseLine(final String line) {
        this.parsedLine = line;
        this.parsedData.reset();
        this.parsingResult = false;

        final String parts[] = line.split(",");
        boolean anyValuePresent = false;

        // First, look for the symbol (following ParseResult protocol)
        this.parsePositions[this.symbolIndex].setIndex(0);
        final Object objTmp = this.formatters[this.symbolIndex].parseObject(
                parts[this.symbolIndex], this.parsePositions[this.symbolIndex]);
        this.parsedData.addSymbol((String) ((Object[])objTmp)[0]);


        // Then, look for symbol trade data
        boolean timeZoneHasBeenSet = false;
        for(int i=0; i<parts.length; i++) {
            if(this.formatters[i] != null) {
                try {
                    this.parsePositions[i].setIndex(0);
                    final Object obj = this.formatters[i]
                            .parseObject(parts[i], this.parsePositions[i]);
                    if(obj instanceof Object[]) {
                        if(i==this.symbolIndex) {
                            continue;
                        }
                    } else if(obj instanceof Number) {
                        boolean isFieldIndex = false;
                        double val = ((Number)obj).doubleValue();
                        for(int j=0; j<dateIndexes.length; j++) {
                            if(i==this.dateIndexes[j]) {
                                isFieldIndex = true;
                                if(this.parsedData.getLocalTimestamp(0)==null) {
                                    this.parsedData.newTimestamp(0);
                                }
                                
                                final long millis = this.parsedData
                                        .getLocalTimestamp(0).getTimeInMillis();
                                if(val>0) {
                                    this.parsedData.getLocalTimestamp(0)
                                            .setTimeZone(TimeZone.getTimeZone(
                                                    "GMT+"+(int)(val)));
                                } else if(val==0) {
                                    this.parsedData.getLocalTimestamp(0)
                                            .setTimeZone(TimeZone.getTimeZone(
                                                    "GMT"));
                                } else {
                                    this.parsedData.getLocalTimestamp(0)
                                            .setTimeZone(TimeZone.getTimeZone(
                                                    "GMT-"+(int)((-val))));
                                }
                                this.parsedData.getLocalTimestamp(0)
                                            .set(Calendar.ZONE_OFFSET, 
                                                (int)val * (60 * 60 * 1000));

                                // Due to a bug in Calendar, when setting 
                                // timezone, milliseconds are altered too...
                                this.parsedData.getLocalTimestamp(0)
                                        .setTimeInMillis(millis);
                                anyValuePresent = true;
                                timeZoneHasBeenSet = true;
                            }
                        }
                        if(!isFieldIndex) {
                            this.parsedData.putValue(this.fieldNames[i],val);
                            anyValuePresent = true;
                        }
                    } else if(obj instanceof Date) {
                        for(int j=0; j<dateIndexes.length; j++) {
                            if(i==this.dateIndexes[j]) {
                                // Remember:
                                //  Calendar.setTimeInMillis sets time
                                //  in GMT+0
                                //  Calendar.get(field) gets the value of time
                                //  in selected GMT time zone.
                                //  Calendar.getTimeInMillis, in the other side,
                                //  gets milliseconds in GMT+0
                                if(this.parsedData.getLocalTimestamp(0)==null) {
                                    this.parsedData.newTimestamp(0);
                                }
                                this.parsedData.getLocalTimestamp(0)
                                    .setTimeInMillis(
                                        this.parsedData.getLocalTimestamp(0)
                                                .getTimeInMillis() 
                                        + ((Date) obj).getTime());
                                break;
                            }
                        }
                    } else {
                        // Unknown type
                        this.errorControl.unknownType(line, i, 
                                this.formatters[i], parts[i]);
                    }
                } catch(Exception e) {
                    // Ok, don't worry, nothing matters
                    this.errorControl.exception(line, i, 
                                this.formatters[i], parts[i], e);
                }
            }
        }
        this.parsingResult = anyValuePresent;
        if(!timeZoneHasBeenSet && this.parsedData.getLocalTimestamp(0)!=null) {
            final long millis = this.parsedData.getLocalTimestamp(0)
                    .getTimeInMillis();
            this.parsedData.getLocalTimestamp(0).setTimeZone(
                    TimeZone.getTimeZone("GMT"));
            this.parsedData.getLocalTimestamp(0).setTimeInMillis(millis);
        }


        this.isValid = this.parsedData.getSymbol(0) != null 
                    && this.parsedData.getLocalTimestamp(0) != null
                    && this.parsingResult;

        if(this.isValid) {
            for(final Filter f : this.pricesFilters) {
                if( ! f.filter(this.parsedData.getLocalTimestamp(0),
                            this.parsedData.getSymbol(0), 
                            this.parsedData.values(0))) {
                    this.isValid = false;
                    break;
                }
            }
        }
    }
}
