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

import com.metaos.datamgt.Field.*;
import com.metaos.datamgt.Field.Qualifier;
import com.metaos.util.*;

/**
 * Line processor for Reuters CSV files with only one date and symbol per line.
 *
 * <b>Not thread safe</b>
 */
public class ReutersCSVLineParser implements LineParser {
    private final CSVLineParser inner;
    /**
     * Creates a parser for a CSV file according to its header line.
     * @param filePath complete or relative path to file to parse.
     */
    public ReutersCSVLineParser(final String filePath) throws IOException {
        this.inner = new CSVLineParser(this.getFormatters(filePath),
                this.getFieldNames(filePath), this.getSymbolIndex(filePath), 
                this.getDateIndexes(filePath));
    }

    public boolean isValid(final String line) { 
        return this.inner.isValid(line);
    }

    public ParseResult parse(final String line) {
        return this.inner.parse(line);
    }

    public LineParser addFilter(final Filter filter) {
        this.inner.addFilter(filter);
        return this;
    }

    public LineParser addCacheWriteable(final CacheWriteable listener) {
        this.inner.addCacheWriteable(listener);
        return this;
    }

    public String getSymbol(final String line, final int index) {
        return this.inner.getSymbol(line, index);
    }

    public Calendar getLocalTimestamp(final String line) {
        return this.inner.getLocalTimestamp(line);
    }

    public Calendar getUTCTimestamp(final String line) {
        return this.inner.getUTCTimestamp(line);
    }

    public void reset() {
        this.inner.reset();
    }

    public void setErrorControl(final LineParser.ErrorControl errorControl) {
        this.inner.setErrorControl(errorControl);
    }

    public LineParser.ErrorControl getErrorControl() {
        return this.inner.getErrorControl();
    }

    public String toString() {
        return "ReutersCSVLineParser:" + this.inner.toString();
    }

    //
    // Private stuff ------------------ only useful for construction
    //
    private String filePath;
    private Format[] formatters;
    private Field[] fields;
    private int symbolIndex;
    private List<Integer> dateIndexes;

    private Format[] getFormatters(final String filePath) 
            throws IOException {
        if( ! filePath.equals(this.filePath)) {
            parseHeader(filePath);
        }
        return this.formatters;
    }

    private Field[] getFieldNames(final String filePath) 
            throws IOException {
        if( ! filePath.equals(this.filePath)) {
            parseHeader(filePath);
        }
        return this.fields;
    }

    private int getSymbolIndex(final String filePath) 
            throws IOException {
        if( ! filePath.equals(this.filePath)) {
            parseHeader(filePath);
        }
        return this.symbolIndex;
    }

    private int[] getDateIndexes(final String filePath) 
            throws IOException {
        if( ! filePath.equals(this.filePath)) {
            parseHeader(filePath);
        }
        final int[] tmp = new int[this.dateIndexes.size()];
        for(int i=0; i<tmp.length; i++) {
            tmp[i] = this.dateIndexes.get(i);
        }
        return tmp;
    }


    private void parseHeader(final String filePath) throws IOException {
        this.filePath = filePath;
        final BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        final String firstLine = reader.readLine();
        final String[] parts = firstLine.split(",");
        this.formatters = new Format[parts.length];
        this.fields = new Field[parts.length];
        this.dateIndexes = new ArrayList<Integer>();
        for(int i=0; i<parts.length; i++) {
            parts[i] = parts[i].replaceAll("#","");
            if(parts[i].equals("RIC")) {
                this.symbolIndex = i;
            } else if(parts[i].equals("GMT Offset")) {
                this.dateIndexes.add(i);
            } else if(parts[i].equals("Date[G]")) {
                this.dateIndexes.add(i);
            } else if(parts[i].equals("Time[G]")) {
                this.dateIndexes.add(i);
            }
            this.formatters[i] = formattersMap.get(parts[i]);
            if(this.formatters[i]==null) {
                this.formatters[i] = doubleFormat;
                this.fields[i] = new EXTENDED(Qualifier.NONE, parts[i]);
            } else {
                fields[i] = fieldsMap.get(parts[i]);
            }
        }
        reader.close();
    }


    public static final Format textFormat=new MessageFormat("{0}");
    public static final Format dateFormat = 
            CalUtils.getDateFormat("dd-MMM-yyyy");
    public static final Format timeFormat = 
            CalUtils.getDateFormat("HH:mm:ss.SSS");
    public static final Format doubleFormat = new DecimalFormat("#.##");

    public static final Map<String, Format> formattersMap = 
            new HashMap<String, Format>();
    public static final Map<String, Field> fieldsMap = 
            new HashMap<String, Field>();
    public static final Map<Field, String> reverseFieldsMap = 
            new HashMap<Field, String>();

    static {
        fieldsMap.put("Ask Price", new CLOSE(Qualifier.ASK));
        fieldsMap.put("Ask Size", new VOLUME(Qualifier.ASK));
        fieldsMap.put("Ask Size", new VOLUME(Qualifier.BID));
        fieldsMap.put("Ave.Price", new EXTENDED(Qualifier.NONE,"Ave.Price"));
        fieldsMap.put("Bid Price", new CLOSE(Qualifier.BID));
        fieldsMap.put("Close Ask", new CLOSE(Qualifier.ASK));
        fieldsMap.put("Close Bid", new CLOSE(Qualifier.BID));
        fieldsMap.put("Close", new CLOSE());
        fieldsMap.put("High Ask", new HIGH(Qualifier.ASK));
        fieldsMap.put("High Bid", new HIGH(Qualifier.BID));
        fieldsMap.put("High", new HIGH());
        fieldsMap.put("Last", new CLOSE());
        fieldsMap.put("Low Ask", new LOW(Qualifier.ASK));
        fieldsMap.put("Low Bid", new LOW(Qualifier.BID));
        fieldsMap.put("Low", new LOW());
        fieldsMap.put("No. Asks", new EXTENDED(Qualifier.ASK, "No."));
        fieldsMap.put("No. Bids", new EXTENDED(Qualifier.BID, "No."));
        fieldsMap.put("No. Trades", new EXTENDED(Qualifier.NONE,"No."));
        fieldsMap.put("Open Ask", new OPEN(Qualifier.ASK));
        fieldsMap.put("Open Bid", new OPEN(Qualifier.BID));
        fieldsMap.put("Open", new OPEN());
        fieldsMap.put("Price", new CLOSE());
        fieldsMap.put("VWAP", new EXTENDED(Qualifier.NONE,"VWAP"));
        fieldsMap.put("Volume", new VOLUME());
        fieldsMap.put("GMT Offset", new EXTENDED(Qualifier.NONE,"GMT"));
        formattersMap.put("Ask Price", doubleFormat);
        formattersMap.put("Ask Size", doubleFormat);
        formattersMap.put("Ave.Price", doubleFormat);
        formattersMap.put("Bid Price", doubleFormat);
        formattersMap.put("Bid Size", doubleFormat);
        formattersMap.put("Close Ask", doubleFormat);
        formattersMap.put("Close Bid", doubleFormat);
        formattersMap.put("Close", doubleFormat);
        formattersMap.put("Date[G]", dateFormat);
        formattersMap.put("High Ask", doubleFormat);
        formattersMap.put("High Bid", doubleFormat);
        formattersMap.put("High", doubleFormat);
        formattersMap.put("Last", doubleFormat);
        formattersMap.put("Low Ask", doubleFormat);
        formattersMap.put("Low Bid", doubleFormat);
        formattersMap.put("Low", doubleFormat);
        formattersMap.put("No. Asks", doubleFormat);
        formattersMap.put("No. Bids", doubleFormat);
        formattersMap.put("No. Trades", doubleFormat);
        formattersMap.put("Open Ask", doubleFormat);
        formattersMap.put("Open Bid", doubleFormat);
        formattersMap.put("Open", doubleFormat);
        formattersMap.put("Price", doubleFormat);
        formattersMap.put("RIC", textFormat);
        formattersMap.put("Time[G]", timeFormat);
        formattersMap.put("Type", textFormat);
        formattersMap.put("VWAP", doubleFormat);
        formattersMap.put("Volume", doubleFormat);
        formattersMap.put("GMT Offset", new DecimalFormat("+#;-#"));

        for(final Map.Entry<String, Field> entry : fieldsMap.entrySet()) {
            reverseFieldsMap.put(entry.getValue(), entry.getKey());
        }
    }
}
