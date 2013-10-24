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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.metaos.datamgt.LineParser.ErrorControl;

/**
 * Prices source for  files with a partial order in date.
 * Specifically, data is ordered following this pattern:<br>
 * <center>
 *   dateA1&lt;dateA2&lt;dateA3...&lt;dateAN,dateB1&lt;...dateBM...
 * </center>
 * But <i>dateB1</i>&lt;<i>dateAN</i> is verified, so when the file
 * is read as a continuous source, several pointers to different chunks of
 * data (every chunk starts with a date locally minimum) should read each
 * line and comopose a sole response grouped by <i>date</i>.
 * <br/>
 * <b>NOT thread safe</b>
 */
public class MultipleSymbolScanner implements LineScanner {
    private static final Logger log = Logger.getLogger(
            MultipleSymbolScanner.class.getPackage().getClass().getName());
    private final String symbols[];
    private final RandomAccessFile partReader[];
    private final long nextMilliseconds[];
    private final String filePath;
    private final LineParser lineParser;
    private final SpreadTradesMgr spreadTradesMgr[];
    private RandomAccessFile mainReader;
    private String nextLine[];
    private boolean nomore = false;
    private boolean isClosed = false;


    /**
     * Creates source for sources with two orders: firstly in RIC, secondly in
     * time.
     *
     * @param filePath complete filename and path containing prices.
     * @param symbols set of symbols present into the file.
     * @param lineProcessor strategy to part each line.
     * @throws IOException if one symbol (at least) cannot be found.
     */
     public MultipleSymbolScanner(final String filePath, final String[] symbols,
            final LineParser lineParser, 
            final SpreadTradesMgr[] spreadTradesMgr) 
            throws IOException {
        this.lineParser = lineParser;
        this.spreadTradesMgr = spreadTradesMgr;
        this.filePath = filePath;
        this.symbols = symbols;
        this.mainReader = new RandomAccessFile(filePath, "r");
        this.partReader = new RandomAccessFile[symbols.length];
        this.nextMilliseconds = new long[symbols.length];
        this.nextLine = new String[symbols.length];

        boolean optimisticJumps = true;


        final ErrorControl errorControl = this.lineParser.getErrorControl();
        this.lineParser.setErrorControl(LineParser.nullErrorControl);

        final String cacheFilePath = filePath + ".cachejump";

        if(new File(cacheFilePath).exists()) {
            final ObjectInputStream cacheJumpsFileIn = new ObjectInputStream(
                    new FileInputStream(cacheFilePath));

            log.info("Using stored cached jumps in file " + cacheFilePath);
            try {
                try {
                    for(;;) {
                        final String s = cacheJumpsFileIn.readUTF();
                        final long pos = cacheJumpsFileIn.readLong();

                        for(int i=0; i<symbols.length; i++) {
                            if(symbols[i].equals(s)) {
                                partReader[i] = new RandomAccessFile(
                                        filePath, "r");
                                partReader[i].seek(pos);
                                log.info("Using cached information: position "
                                        + pos + " for " + s);
                                break;
                            }
                        }
                    }
                } catch(IOException ioe) {
                    cacheJumpsFileIn.close();
                }
                for(int i=0; i<partReader.length; i++) {
                    if(partReader[i] == null) {
                        throw new IOException("Symbol " + symbols[i] 
                                + " not placed");
                    }
                }
            } catch(IOException ioe) {
                log.log(Level.SEVERE, ioe.toString(), ioe);
                log.severe("Error reading file "+cacheFilePath+". Removing");
                new File(cacheFilePath).delete();
            }
        } else {
            final ObjectOutputStream cacheJumpsFileOut = new ObjectOutputStream(
                    new FileOutputStream(cacheFilePath));
            log.fine("Caching jumps for future uses in file " + cacheFilePath);

            log.info("Scannig file to place reading pointers");
     
            for(int i=0; i<symbols.length; i++) {
                long lastPosition = this.mainReader.getFilePointer();
                long foundPosition;
                log.fine("Starting to search symbol " + symbols[i] 
                        + " from position " + lastPosition);
                for(;;) {
                    foundPosition = this.mainReader.getFilePointer();
                    final String line = readNextLineCycling(lastPosition);
                    if(line==null) {
                        // All file's been read without finding desired symbol.
                        throw new IOException("Cannot find symbol '" 
                                + symbols[i] + "' in file '" + filePath + "'");
                    }
                    if(this.lineParser.isValid(line) && 
                            symbols[i].equals(this.lineParser
                                    .getSymbol(line, 0))) {
                        break;
                    }
                }
                log.info("Located place for reader on " + symbols[i] + ":"
                        + foundPosition);
                cacheJumpsFileOut.writeUTF(symbols[i]);
                cacheJumpsFileOut.writeLong(foundPosition);
                partReader[i] = new RandomAccessFile(filePath, "r");
                partReader[i].seek(foundPosition);
                final int optimisticJump = (int) (
                            2*(foundPosition - lastPosition)/3);
                this.mainReader.skipBytes(optimisticJump);
            }
            cacheJumpsFileOut.flush();
            cacheJumpsFileOut.close();
        }

        log.info("File scanned. Placing reading pointers");

        // Boot up
        for(int i=0; i<symbols.length; i++) {
            for(;;) {
                try {
                    String line = partReader[i].readLine();
                    while(line!=null && !this.lineParser.isValid(line)) {
                        line = partReader[i].readLine();
                    }

                    if( ! symbols[i].equals(this.lineParser.parse(line)
                            .getSymbol(0))) {
                        this.nextLine[i] = null;
                    } else {
                        this.nextLine[i] = line;
                        this.nextMilliseconds[i] = this.lineParser.parse(line)
                                .getUTCTimestampCopy().getTimeInMillis();
                        break;
                    }
                } catch(Exception e) {
                    log.log(Level.SEVERE, "Exception reading first line "
                            + "of file '" + this.filePath + "'", e);
                    log.severe("Maybe cache file is not correct?");
                }
            }
        }
        log.info("Readers placed correctly. Source started");
        this.lineParser.setErrorControl(errorControl);
    }


    public void run() {
        while(this.next());
    }

    public void reset() {
        throw new UnsupportedOperationException("Not implemented yet, sorry");
    }

    public boolean first() {
        throw new UnsupportedOperationException("Not implemented yet, sorry");
    }

    public boolean last() {
        throw new UnsupportedOperationException("Not implemented yet, sorry");
    }

    /**
     * Reads next line and notifies susbscribed observers.
     * @return true if more lines would be available, false if there was
     * no possible to read the line, since the EOF has been reached.
     */
    public boolean next() {
        if(this.isClosed) return false;
        if(this.nomore) return false;
        try {
            final boolean unpoll[] = new boolean[symbols.length];
            long minTime = nextMilliseconds[0];
            for(int i=1; i<symbols.length; i++) {
                if(nextMilliseconds[i]>minTime) unpoll[i] = true;
                else if(nextMilliseconds[i]<minTime) {
                    minTime = nextMilliseconds[i];
                    for(int j=0; j<i; j++) unpoll[j] = true;
                }
            }

            boolean somethingToReturn = false;
            final ParseResult compossedResult = new ParseResult();
            for(int i=0; i<symbols.length; i++) {
                if(unpoll[i] || this.nextLine[i]==null) {
                    // Nothing to send to listeners.
                } else {
                    somethingToReturn = true;
                    final ParseResult localResult = 
                            this.lineParser.parse(this.nextLine[i]);
                    // Move to next valid line.
                    String line = partReader[i].readLine();
                    while(line!=null && !this.lineParser.isValid(line)) {
                        line = partReader[i].readLine();
                    }
                    if(line != null) {
                        if(! symbols[i].equals(localResult.getSymbol(0))) {
                            this.nextLine[i] = null;
                        } else {
                            this.nextLine[i] = line;
                            this.nextMilliseconds[i] = this.lineParser
                                    .getUTCTimestamp(line)
                                            .getTimeInMillis();
                        }
                    } else {
                        this.nextLine[i] = null;
                    }
                    compossedResult.merge(localResult);
                }
            }
            if(somethingToReturn) {
                for(int i=0; i<this.spreadTradesMgr.length; i++) {
                    this.spreadTradesMgr[i].accumulate(compossedResult);
                }
            } else {
                this.nomore = true;
                for(int i=0; i<this.spreadTradesMgr.length; i++) {
                    this.spreadTradesMgr[i].endAccumulation();
                }
            }
            return somethingToReturn;
        } catch(Exception e) {
            log.log(Level.SEVERE, "Exception dealing with file '"
                    + filePath + "'", e);
            return false;
        }
    }
 

    public void close() {
        this.isClosed = true;
        try {
            this.mainReader.close();
        } catch(IOException ioe) {
            // Maybe nothing should happen in this case.... don't worry.
        }
    }


    //
    // Private stuff ----------------------------------
    //
    private String readNextLineCycling(final long positionLimit)
            throws IOException {
        final long previousPos = this.mainReader.getFilePointer();
        final String line = this.mainReader.readLine();
        final long newPos = this.mainReader.getFilePointer();
        if(previousPos<positionLimit && newPos>=positionLimit) {
            return null;
        }
        if(line==null) {
            this.mainReader.seek(0);
            if(positionLimit==0) return null;
            return this.mainReader.readLine();
        } else {
            return line;
        }
    }
}
