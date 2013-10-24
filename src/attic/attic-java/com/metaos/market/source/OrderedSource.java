/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.market.source.csv;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.metaos.market.source.*;

/**
 * Source of prices for ONE symbol ordered by date.
 */
public class OrderedSource extends BasicPricesSource {
    private boolean isClosed = false;
    private boolean nomore = false;
    private final BufferedReader fileReader;
    private final SourceLineProcessor processor;
    protected final String symbol;
    private String currentLine, firstLine, lastLine;


    /**
     * To be used by extending classes.
     */
    public OrderedSource(final String filePath, final String symbol,
            final SourceLineProcessor processor) throws IOException {
        this.fileReader = new BufferedReader(new FileReader(filePath));
        this.symbol = symbol;
        this.processor = processor;
    }


    public final void run() {
        while(this.next());
    }


    public final boolean next() {
        if(this.moveToNextLine()) {
            this.processor.process(this.firstLine);
            this.processor.concludeLineSet();
            return true;
        } else {
            return false;
        }
    }


    public boolean first() {
        if(this.isClosed) return false;
        if(this.firstLine == null) {
            while(!this.nomore && !this.isClosed) {
                if(this.moveToNextLine()) {
                    if(this.firstLine != null) return this.first();
                    else return false;
                }
            }
            return false;
        } else {
            this.processor.process(this.firstLine);
            this.processor.concludeLineSet();
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
            while(this.moveToNextLine());
            this.lastLine = this.currentLine;
            if(this.lastLine != null) return this.last();
            else return false;
        } else {
            this.processor.process(this.lastLine);
            this.processor.concludeLineSet();
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
    private final boolean moveToNextLine() {
        if(this.isClosed || this.nomore) return false;
        try {
            final String line = fileReader.readLine();
            if(line==null) {
                this.nomore = true;
                return false;
            }

            final Calendar moment = this.processor.getDate(line);
            if(symbol.equals(this.processor.getSymbol(line, 0)) 
                    && this.processor.isValid(line)) {
                this.currentLine = line;
                if(this.firstLine==null) this.firstLine = line;
                return moment != null;
            } else {
                if(this.moveToNextLine()) {
                    return true;
                } else {
                    this.lastLine = line;
                    this.nomore = true;
                    return false;
                }
            }
        } catch(Exception e) {
            this.nomore = true;
            return false;
        }
    }
}
