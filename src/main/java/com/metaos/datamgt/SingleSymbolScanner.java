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
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Source of prices for ONE symbol ordered by date.
 * This class is NOT thread safe.
 */
public class SingleSymbolScanner implements LineScanner {
    private static final Logger log = Logger.getLogger(SingleSymbolScanner
            .class.getPackage().getName());
    private boolean isClosed;
    private boolean endReached;
    private BufferedReader fileReader;
    private final String filePath;
    private final LineParser lineParser;
    private final SpreadTradesMgr spreadTradesMgr[];
    protected final String symbol;
    private String currentLine, firstLine, lastLine;


    /**
     * Creates a scanner to deal only with one symbol at time over a file.
     *
     * @param spreadTradesMgr list of SpreadTradesMgr objects to notify when
     * each line is read.
     */
    public SingleSymbolScanner(final String filePath, final String symbol,
            final LineParser lineParser,final SpreadTradesMgr[] spreadTradesMgr)
            throws IOException {
        this.isClosed = false;
        this.endReached = false;
        this.filePath = filePath;
        this.fileReader = new BufferedReader(new FileReader(this.filePath));
        this.symbol = symbol;
        this.lineParser = lineParser;
        this.spreadTradesMgr = spreadTradesMgr;
        this.lineParser.addFilter(new Filter() {
                public boolean filter(final Calendar w, final String s,
                        final Map<Field, Double> v) {
                    return symbol.equals(s);
                }
            });
        log.info("Created scanner for symbol " + symbol + " over " + filePath);
    }



    public final void run() {
        log.fine("Running source for symbol " + this.symbol + " over file "
                + this.filePath);
        while(this.next());
    }


    public final void reset() {
        this.lineParser.reset();
        for(int i=0; i<this.spreadTradesMgr.length; i++) {
            this.spreadTradesMgr[i].reset();
        }
        this.currentLine = null;
        this.firstLine = null;
        this.lastLine = null;
        this.isClosed = false;
        this.endReached = false;
        try {
            if(!this.isClosed) this.fileReader.close();
            this.fileReader = new BufferedReader(new FileReader(this.filePath));
            this.lineParser.addFilter(new Filter() {
                public boolean filter(final Calendar w, final String s,
                        final Map<Field, Double> v) {
                    return symbol.equals(s);
                }
            });
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public final boolean next() {
        if(this.readNextLine()) {
            final ParseResult result = this.lineParser.parse(this.currentLine);
            for(int i=0; i<this.spreadTradesMgr.length; i++) {
                this.spreadTradesMgr[i].accumulate(result);
            }
            return true;
        } else {
            for(int i=0; i<this.spreadTradesMgr.length; i++) {
                this.spreadTradesMgr[i].endAccumulation();
            }
            return false;
        }
    }


    public boolean first() {
        if(this.isClosed) return false;
        if(this.firstLine == null) {
            while(!this.endReached && !this.isClosed) {
                if(this.readNextLine()) {
                    if(this.firstLine != null) return this.first();
                    else return false;
                }
            }
            return false;
        } else {
            final ParseResult result = this.lineParser.parse(this.firstLine);
            for(int i=0; i<this.spreadTradesMgr.length; i++) {
                this.spreadTradesMgr[i].accumulate(result);
                this.spreadTradesMgr[i].endAccumulation();
            }
            return true;
        }
    }


    public boolean last() {
        // Implementation notes: we have no two choices:
        //   a) read the whole source remembering the last valid read line. 
        //   b) read backwards from last line return the first valid line.
        // Particullary, reading only the last line is not useful 
        // (it may be invalid).
        
        // Read the whole source looking for the last valid line.

        if(this.isClosed) return false;
        if(this.lastLine == null) {
            Calendar moment = null;
            while(this.readNextLine());
            this.lastLine = this.currentLine;
            if(this.lastLine != null) return this.last();
            else return false;
        } else {
            final ParseResult result = this.lineParser.parse(this.lastLine);
            for(int i=0; i<this.spreadTradesMgr.length; i++) {
                this.spreadTradesMgr[i].accumulate(result);
                this.spreadTradesMgr[i].endAccumulation();
            }
            return true;
        }
    }


    public final void close() {
        this.isClosed = true;
        try {
            this.fileReader.close();
        } catch(IOException ioe) {
            // Maybe nothing should happen in this case.... don't worry.
        }
    }

    //
    // Private stuff -------------------------
    //

    /**
     * Moves to next valid line.
     *
     * @return false if cannot move to next line (no more valid lines to read).
     */
    private boolean readNextLine() {
        int result;
        do {
            result = _readNextLine();
        } while(result==0);
        if(result==+1) return true;
        if(result==-1) {
            this.lastLine = this.currentLine;
            return false;
        }
        throw new IllegalArgumentException("_readNextLine is not working as"
                + "expected!");
    }


    /**
     * Internal usage by <code>readNextLine</code> to avoid recurssion.
     * @return -1 if <code>readNextLine</code> must return false,
     * +1 if <code>readNextLine</code> must return true or 0 if
     * this function should be reevaluated.
     */
    private int _readNextLine() {
        if(this.isClosed || this.endReached) return -1;
        try {
            final String line = fileReader.readLine();
            if(line==null) {
                this.endReached = true;
                return -1;
            }

            final Calendar moment = this.lineParser.getLocalTimestamp(line);
            if(this.lineParser.isValid(line)) {
                this.currentLine = line;
                if(this.firstLine==null) this.firstLine = line;
                return moment != null ? +1 : -1;
            } else {
                this.currentLine = line;
                return 0;
            }
        } catch(IOException e) {
            this.endReached = true;
            return -1;
        }
    }
}
