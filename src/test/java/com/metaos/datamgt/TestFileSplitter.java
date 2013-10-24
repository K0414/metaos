/*
 * Copyright 2011 - 2012
 * All rights reserved. License and terms according to LICENSE.txt file.
 * The LICENSE.txt file and this header must be included or referenced 
 * in each piece of code derived from this project.
 */
package com.metaos.datamgt;

import java.io.*;
import java.text.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Testing case for Multiple Symbol scanner.
 */
public class TestFileSplitter {
    // Utility class for tests.
    private class LineMemory implements Listener {
        private List<ParseResult> results = new ArrayList<ParseResult>();

        public void notify(final ParseResult result) {
            results.add((ParseResult) (result.clone()));
        }
    }

    @Test
    public void testTwoSymbolsSplitter() throws Exception {
        final SpreadTradesMgr accumulator = new TransparentSTMgr();
        final FileSplitting repository = new FileSplitting("tmp", "1min");
        accumulator.addListener(repository.new CSVReutersSplitter());
        final MultipleSymbolScanner mss = new MultipleSymbolScanner(
                "../../src/testdata/ABE+SABE.MC-test.csv", 
                new String[] { "ABE.MC", "SABE.MC" },
                new ReutersCSVLineParser(
                        "../../src/testdata/ABE+SABE.MC-test.csv"), 
                new SpreadTradesMgr[] { accumulator });
        mss.run();                
        System.out.println("Ok");
    }



    

    public static void main(final String args[]) throws Exception {
        final TestFileSplitter test = new TestFileSplitter();
        test.testTwoSymbolsSplitter();
    }
}
