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
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * 
 * <b>NOT thread safe</b>
 */
public class SecondOrderSource extends BasicPricesSource {
    private static final Logger log = Logger.getLogger(
            SecondOrderSource.class.getPackage().getClass().getName());
    private static final DateFormat dateFormat = new SimpleDateFormat(
                "dd-MMM-yyyy HH:mm:ss.SSS z");
    private final String symbols[];
    private final RandomAccessFile partReader[];
    private final long nextMilliseconds[];
    private final String filePath;
    private final SourceLineProcessor processor;
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
     public SecondOrderSource(final String filePath, final String[] symbols,
            final SourceLineProcessor lineProcessor) throws IOException {
        this.processor = lineProcessor;
        this.filePath = filePath;
        this.symbols = symbols;
        this.mainReader = new RandomAccessFile(filePath, "r");
        this.partReader = new RandomAccessFile[symbols.length];
        this.nextMilliseconds = new long[symbols.length];
        this.nextLine = new String[symbols.length];
     
        for(int i=0; i<symbols.length; i++) {
            long lastPosition = this.mainReader.getFilePointer();
            long foundPosition;
            for(;;) {
                foundPosition = this.mainReader.getFilePointer();
                final String line = readNextLineCycling(lastPosition);
                if(line==null) {
                    // All file's been read without finding desired symbol.
                    throw new IOException("Cannot find symbol '" 
                            + symbols[i] + "' in file '" + filePath + "'");
                }
                if(symbols[i].equals(this.processor.getSymbol(line, 0))) {
                    break;
                }
            }
            partReader[i] = new RandomAccessFile(filePath, "r");
            partReader[i].seek(foundPosition);
        }

        // Boot up
        for(int i=0; i<symbols.length; i++) {
            for(;;) {
                try {
                    final String line = partReader[i].readLine();
                    if( ! symbols[i].equals(this.processor.getSymbol(line,0))) {
                        this.nextLine[i] = null;
                    } else {
                        this.nextLine[i] = line;
                        this.nextMilliseconds[i] = this.processor.getDate(line)
                                .getTimeInMillis();
                        break;
                    }
                } catch(Exception e) {
                    log.log(Level.SEVERE, "Exception reading first line "
                            + "of file '" + this.filePath + "'", e);
                }
            }
        }
    }


    public void run() {
        while(this.next());
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
            for(int i=0; i<symbols.length; i++) {
                if(unpoll[i] || this.nextLine[i]==null) {
                    // Nothing to send to markets.
                } else {
                    somethingToReturn = true;
                    this.processor.process(this.nextLine[i]);

                    // Move to next line
                    final String line = partReader[i].readLine();
                    if(line != null) {
                        if(! symbols[i].equals(
                                this.processor.getSymbol(line, 0)) ) {
                            this.nextLine[i] = null;
                        } else {
                            this.nextLine[i] = line;
                            this.nextMilliseconds[i] = this.processor
                                    .getDate(line).getTimeInMillis();
                        }
                    } else {
                        this.nextLine[i] = null;
                    }
                }
            }
            if(somethingToReturn) {
                this.processor.concludeLineSet();
            } else {
                this.nomore = true;
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
