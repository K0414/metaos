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
 * Creates a cache file with points to start reading symbol data.
 */
public final class CreateCacheForMultipleSymbolScanner {
    private static final Logger log = Logger.getLogger(
            CreateCacheForMultipleSymbolScanner.class.getPackage().getName());
    private final String symbols[];
    private final String filePath;
    private final LineParser lineParser;
    private RandomAccessFile mainReader;
    private String nextLine[];
    private long nextMilliseconds[];


    /**
     * Creates cache builder for source MultipleSymbolScanner source scanner.
     *
     * @param filePath complete filename and path containing prices.
     * @param symbols set of symbols present into the file.
     * @param lineProcessor strategy to part each line.
     * @throws IOException if one symbol (at least) cannot be found.
     */
     public CreateCacheForMultipleSymbolScanner(final String filePath, 
            final String[] symbols,
            final LineParser lineParser) throws IOException {
        this.lineParser = lineParser;
        this.filePath = filePath;
        this.symbols = symbols;
    }

    /**
     * Removes previous cache and creates the new one file.
     */
    public void run() throws IOException {
        final String cacheFilePath = filePath + ".cachejump";
        new File(cacheFilePath).delete();
        final MultipleSymbolScanner scanner = new MultipleSymbolScanner(
                this.filePath, this.symbols, this.lineParser,
                new SpreadTradesMgr[] { new TransparentSTMgr() });
        scanner.close();
    }



    public void close() {
    }
}
